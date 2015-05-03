package net.chaosserver.facebook;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.utils.URIBuilder;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


public class FacebookHelper {
	final Logger logger = LoggerFactory.getLogger(FacebookHelper.class);
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
    protected String chanomieFeederAppSecret;
    
    public FacebookHelper() {
    	chanomieFeederAppSecret = System.getProperty("chanomieFeederAppKey");
    	logger.debug("Loaded from property chanomieFeederAppKey, is null? [{}]", chanomieFeederAppSecret == null);
        if (chanomieFeederAppSecret == null) {
        	chanomieFeederAppSecret = System.getenv("chanomieFeederAppKey");
        	logger.debug("Loaded from environment chanomieFeederAppKey, is null? [{}]", chanomieFeederAppSecret == null);
        }
    }

    public String getAccessToken(String facebookId, String facebookCode) throws FacebookUnauthorizedException {
    	String facebookAccessToken;
    	try {
			facebookAccessToken = getPersistedAccessToken(facebookId);
		} catch (FacebookUnauthorizedException e) {
			// No persisted token.
			if(facebookCode == null) {
				throw new FacebookUnauthorizedException("No access token");
			} else {
				facebookAccessToken = getAccessTokenWithCode(facebookCode);
				setAccessToken(facebookId, facebookAccessToken);
			}
			
		}
    	
    	return facebookAccessToken;
    }
    
    /**
     * Returns the access token for the given Facebook Id
     * @param facebookId
     */
	public String getPersistedAccessToken(String facebookId) throws FacebookUnauthorizedException {
		String facebookAccessTokenString;
		try {
		    Key facebookAccessTokenKey = KeyFactory.createKey("FacebookAccessToken", facebookId);
		    Entity facebookAccessToken = datastore.get(facebookAccessTokenKey);
		    facebookAccessTokenString = (String) facebookAccessToken.getProperty("accessToken");
		  
		    // Test the validity of the access token.
		    URIBuilder uriBuilder = new URIBuilder("https://graph.facebook.com/debug_token");
		    uriBuilder.addParameter("input_token", facebookAccessTokenString);
		    uriBuilder.addParameter("access_token", facebookAccessTokenString);
		    URL debugTokenUrl = uriBuilder.build().toURL();
		    
		    logger.debug("Request for accessTokenCheck [{}]", debugTokenUrl);
		    ObjectMapper mapper = new ObjectMapper();
		    JsonFactory factory = mapper.getJsonFactory();
		    JsonParser jp = factory.createJsonParser(new BufferedInputStream(
		    		debugTokenUrl.openStream()));
		    
		    JsonNode actualObj = mapper.readTree(jp);
		    boolean accessTokenValid = actualObj.path("data").path("is_valid").getBooleanValue();
		    logger.debug("Response from accessTokenCheck [{}], accessTokenValid [{}]", actualObj, accessTokenValid);		    
		    
		    if(!accessTokenValid) {
			    logger.debug("Stored access token is invalid. Delete from storage.");		    
		    	datastore.delete(facebookAccessTokenKey);
		    	throw new FacebookUnauthorizedException("Access token was invalid for [" + facebookId + "]");
		    }

		} catch (EntityNotFoundException | URISyntaxException | IOException e) {
			throw new FacebookUnauthorizedException("No access token available for id [" + facebookId + "]", e);
		}
		
		return facebookAccessTokenString;
	}
	
	public String getAccessTokenWithCode(String facebookCode) throws FacebookUnauthorizedException {
		String facebookAccessToken;
		
		try {
		    URIBuilder uriBuilder = new URIBuilder("https://graph.facebook.com/v2.3/oauth/access_token");
		    uriBuilder.addParameter("client_id", "1658060647750088");
		    uriBuilder.addParameter("client_secret", getChanomieFeederAppSecret());
		    uriBuilder.addParameter("redirect_uri", "http://localhost:8080/facebook/rss?facebookId=jordan.reed");
		    uriBuilder.addParameter("code", facebookCode);
		    logger.debug("Request URL [{}]", uriBuilder);

		    URL accessTokenUrl = uriBuilder.build().toURL(); 

		    ObjectMapper mapper = new ObjectMapper();
		    JsonFactory factory = mapper.getJsonFactory();
		    JsonParser jp = factory.createJsonParser(new BufferedInputStream(
          		accessTokenUrl.openStream()));
		    JsonNode actualObj = mapper.readTree(jp);
          
		    facebookAccessToken = actualObj.path("access_token").getTextValue();
		    String expiresIn = actualObj.path("expires_in").getTextValue();
		    logger.debug("Reponse was actualObj [{}], facebookAccessToken [{}], expiresIn [{}]", actualObj, facebookAccessToken, expiresIn);
		    
		    if(facebookAccessToken != null) {
		    	return facebookAccessToken;
		    } else {
		    	throw new FacebookUnauthorizedException("Unable to exchange code for access token, result was null");
		    }
		} catch (IOException | URISyntaxException e) {
	    	throw new FacebookUnauthorizedException("Unable to exchange code for access token due to exception", e);			
		}
		
	}
	
	public void setAccessToken(String facebookId, String accessToken) {
		Entity facebookAccessToken = new Entity("FacebookAccessToken", facebookId);
		facebookAccessToken.setProperty("accessToken", accessToken);
		
		datastore.put(facebookAccessToken);
	}
	
	public String getChanomieFeederAppSecret() {
		return this.chanomieFeederAppSecret;
	}
	
}
