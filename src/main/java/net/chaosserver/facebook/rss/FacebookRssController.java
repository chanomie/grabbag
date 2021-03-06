package net.chaosserver.facebook.rss;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.chaosserver.facebook.FacebookHelper;
import net.chaosserver.facebook.FacebookUnauthorizedException;
import net.chaosserver.rss.RssItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.http.client.utils.URIBuilder;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/facebook/rss")
public class FacebookRssController {
	final Logger logger = LoggerFactory.getLogger(FacebookRssController.class);
	
    @Autowired
    private FacebookHelper facebookHelper;
	
	@RequestMapping(method = RequestMethod.GET)
    public String get(
    		@RequestParam(value = "facebookId", required = true) String facebookId,
    		@RequestParam(value = "code", required = false) String facebookCode,
    		Model model
    		) throws FacebookUnauthorizedException, URISyntaxException, JsonParseException, IOException {
		
		String facebookAccessToken = null;
		logger.debug("Input parameters - facebookId [{}], code [{}]", facebookId, facebookCode);

		try {
			facebookAccessToken = facebookHelper.getAccessToken(facebookId, facebookCode);
		} catch (FacebookUnauthorizedException e) {
			URIBuilder uriBuilder = new URIBuilder("https://www.facebook.com/dialog/oauth");
			uriBuilder.addParameter("client_id", "1658060647750088");
			uriBuilder.addParameter("redirect_uri", "http://localhost:8080/facebook/rss?facebookId=jordan.reed");
			uriBuilder.addParameter("scope", "read_stream");
			return "redirect:" + uriBuilder.build();
		}
		
		// 1. Get Friend List & Store
		// 2. For Each Friend
		//   2a. /{user-id}/links
		//   2b. /{user-id}/links
		//   2c. /{user-id}/statuses
		
	    URIBuilder uriBuilder = new URIBuilder("https://graph.facebook.com/me/home");
	    uriBuilder.addParameter("access_token", facebookAccessToken);
	    URL debugTokenUrl = uriBuilder.build().toURL();
	    
	    logger.debug("Request for accessTokenCheck [{}]", debugTokenUrl);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonFactory factory = mapper.getJsonFactory();
	    JsonParser jp = factory.createJsonParser(new BufferedInputStream(
	    		debugTokenUrl.openStream()));

	    List rssItemList = new ArrayList<RssItem>();
	    JsonNode actualObj = mapper.readTree(jp);
	    if(actualObj.get("data").isArray()) {
	        for (JsonNode objNode : actualObj.path("data")) {
	            logger.debug("id [{}], from.name [{}], message [{}], "
	            		+ "picture [{}], link [{}], createdTime [{}], "
	            		+ "type [{}], statusType [{}]", 
	            		objNode.path("id").getTextValue(),
	            		objNode.path("from").path("name").getTextValue(),
	            		objNode.path("message").getTextValue(),
	            		objNode.path("picture").getTextValue(),
	            		objNode.path("link").getTextValue(),
	            		objNode.path("created_time").getTextValue(),
	            		objNode.path("type").getTextValue(),
	            		objNode.path("status_type").getTextValue());
	            
	            RssItem rssItem = new RssItem();
	            
	            rssItem.setTitle(objNode.path("story").getTextValue());
	            rssItem.setLink(objNode.path("link").getTextValue());
	            rssItem.setDescription(objNode.path("message").getTextValue());
	            rssItem.setAuthor(objNode.path("from").path("name").getTextValue());
	            
	            // actions[0].link
	            rssItem.setComments(
	            		"https://graph.facebook.com/" 
	            		+ objNode.path("id").getTextValue() 
	            		+ "/comments"
	            );
	            rssItem.setGuid(objNode.path("id").getTextValue());
	            rssItem.setPubDate(objNode.path("created_time").getTextValue());
	            rssItemList.add(rssItem);
	            
	            /*
	            protected String category;
	            protected String enclosure;
	            protected String source;
	            */
	        }
	        model.addAttribute("rssItemList", rssItemList);
	    } else {
	    	logger.debug(".data is not an array");
	    }		
		
        return "facebook/rss/view";
    }
	
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String update(    		
    		@RequestParam(value = "facebookId", required = true) String facebookId,
    		@RequestParam(value = "code", required = false) String facebookCode
    		) throws FacebookUnauthorizedException, URISyntaxException {
    	
		String facebookAccessToken = null;
		logger.debug("Input parameters - facebookId [{}], code [{}]", facebookId, facebookCode);    	
		try {
			facebookAccessToken = facebookHelper.getAccessToken(facebookId, facebookCode);
		} catch (FacebookUnauthorizedException e) {
			URIBuilder uriBuilder = new URIBuilder("https://www.facebook.com/dialog/oauth");
			uriBuilder.addParameter("client_id", "1658060647750088");
			uriBuilder.addParameter("redirect_uri", "http://localhost:8080/facebook/rss?facebookId=jordan.reed");
			uriBuilder.addParameter("scope", "read_stream");
			return "redirect:" + uriBuilder.build();
		}
		
		
    	
    	
    	return "facebook/rss/view";
    }
	

}
