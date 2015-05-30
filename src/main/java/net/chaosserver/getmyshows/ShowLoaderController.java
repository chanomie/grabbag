package net.chaosserver.getmyshows;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Show Loader controller is used to find magnet files on various services.
 *  
 * @author jreed
 */
@RequestMapping("/getmyshows")
public class ShowLoaderController {
	final Logger logger = LoggerFactory.getLogger(ShowLoaderController.class);
	
	protected static final int RESULT_DEFAULT = 5;
	protected static final int RESULTS_MAX = 5;
	protected static final Map<String, String> loaderMap;
	static {
		loaderMap = new HashMap<String,String>();
		loaderMap.put("tpb", PirateBayShowLoader.class.getName());
		loaderMap.put("iso", IsoHuntShowLoader.class.getName());
		loaderMap.put("opb", OldPirateBayShowLoader.class.getName());
		loaderMap.put("default", PirateBayShowLoader.class.getName());
	};
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(@RequestParam(value = "loader") String loader,
		@RequestParam(value = "show") String show,
		@RequestParam(value = "format") String format,
		@RequestParam(value = "category") String category,
		@RequestParam(value = "results") String resultsString,
		HttpServletResponse response) {
		
	    ShowLoader showLoader;
    	try {
    		if(loaderMap.containsKey(loader)) {
				Class showLoaderClass = Class.forName(loaderMap.get(loader));
				showLoader = (ShowLoader) showLoaderClass.getConstructor().newInstance();
    		} else {
    			throw new IllegalStateException("No object in map");
    		}
		} catch (Throwable e) {
			logger.warn("Unable to get a loader for [{}], so using default.", loader, e);
			showLoader = new PirateBayShowLoader();
		}
	    
    	// Read the number of results.
	    int results = RESULT_DEFAULT;
	    try {
	      results = Integer.parseInt(resultsString);
	    } catch (Exception e) {}
	    if ((results < 0) || (results > RESULTS_MAX)) {
	      results = RESULT_DEFAULT;
	    }
	    
	    if ("TEXT".equals(format)) {
	    	response.setContentType("text/plain");
	    } else {
	        response.setContentType("application/json");
	    }
	    response.addHeader("Access-Control-Allow-Origin", "*");
		
		return null;
	}

}
