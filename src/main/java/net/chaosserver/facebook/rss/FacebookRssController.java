package net.chaosserver.facebook.rss;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import net.chaosserver.facebook.FacebookHelper;
import net.chaosserver.facebook.FacebookUnauthorizedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    		@RequestParam(value = "code", required = false) String facebookCode
    		) throws FacebookUnauthorizedException, URISyntaxException {
		
		String facebookAccessToken = null;
		logger.debug("Input parameters - facebookId [{}], code [{}]", facebookId, facebookCode);

		try {
			facebookAccessToken = facebookHelper.getAccessToken(facebookId);
		} catch (FacebookUnauthorizedException e) {
			// Facebook is unauthorized, need to get the access token.
			if(facebookCode != null) {
				try {
					logger.debug("Have code in the URL, attempting to exchange for access token.");

					// We got a code, so need an app secret
					facebookAccessToken = facebookHelper.getAccessTokenWithCode(facebookCode);
					facebookHelper.setAccessToken(facebookId, facebookAccessToken);
				} catch (FacebookUnauthorizedException e1) {
					logger.debug("facebookCode was in URL, but unable to exchange for access token", e1);
					throw(e1);
				}
			} else {
				URIBuilder uriBuilder = new URIBuilder("https://www.facebook.com/dialog/oauth");
				uriBuilder.addParameter("client_id", "1658060647750088");
				uriBuilder.addParameter("redirect_uri", "http://localhost:8080/facebook/rss?facebookId=jordan.reed");
				uriBuilder.addParameter("scope", "read_stream");
				return "redirect:" + uriBuilder.build();
			}
		}
		
		// You must have a valid access token at this point.
		URIBuilder uriBuilder = new URIBuilder("https://graph.facebook.com/v2.3/me/home");
		uriBuilder.addParameter("access_token", facebookAccessToken);
		
		
		// localhost:8080/facebook/rss?facebookId=jordan.reed
		// https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow/v2.3		
		
        return "facebook/rss/view";
    }

}
