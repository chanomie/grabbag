package net.chaosserver.getmyshows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

/**
 * OldPirateBay doesn't actually mean it's an old class.  When Pirate Bay
 * went down on 12/7 it was ressurected by IsoHunt as "Old Pirate Bay" so
 * this class actually is the new version.
 * 
 */
public class OldPirateBayShowLoader implements ShowLoader
{
  protected static final int RESULTS = 5;
  protected String urlRoot = "http://oldpiratebay.org";
  protected Pattern showNamePattern = Pattern.compile(".*<a href='(magnet:[^']*)'.*<span>(.*)</span>.*<td class=\"date-row\">(.*?)</td>.*");
  private static final Logger log = Logger.getLogger(OldPirateBayShowLoader.class.getName());

  public List<TorrentLink> getResultList(String searchTerm, int results,
			String category) throws IOException {

		List<TorrentLink> torrentLinkList = new ArrayList<TorrentLink>();
		throw new UnsupportedOperationException();
		// return torrentLinkList;
  }

  @Deprecated
  public String getResultObject(String searchTerm, String resultFormat, int results, String category) throws UnsupportedEncodingException, IOException {
    StringBuffer resultString = new StringBuffer();
    int showCounter = 0;

    if (!"TEXT".equals(resultFormat))
    {
      resultString.append("[");
    }

    // http://oldpiratebay.org/search.php?q=Grimm&iht=8&Torrent_sort=seeders.desc
    String siteUrl = urlRoot + "/search.php?q=" + URLEncoder.encode(searchTerm, "UTF-8") + "&iht=8&Torrent_sort=seeders.desc";
    if("Comics".equals(category)) {
	    siteUrl = urlRoot +  "/search.php?q=" + URLEncoder.encode(searchTerm, "UTF-8") + "&iht=7&Torrent_sort=seeders.desc";
    }
    
    String torrentName = "";
    String magnetUrl = "";
    String showDate = "";

    log.info("Making request to [" + siteUrl + "]");
    BufferedReader bufferedReader = getInputString(siteUrl);
    String line = bufferedReader.readLine();
    // log.info("Read first line");
    while ((line != null) && (showCounter < results))
    {
      log.info("line: [" + line +"]");
      Matcher matcher = this.showNamePattern.matcher(line);
      if (matcher.matches()) {
        magnetUrl = matcher.group(1);
        torrentName = matcher.group(2);
        showDate = matcher.group(3);
        if ((showCounter > 0) && 
                (!"TEXT".equals(resultFormat)))
              {
                resultString.append(",");
              }

              if ("TEXT".equals(resultFormat)) {
                resultString.append(magnetUrl);
                resultString.append("\n");
              } else {
                resultString.append("{\"torrentName\":\"");
                resultString.append(torrentName);
                resultString.append("\",");
                resultString.append("\"magnetLink\":\"");
                resultString.append(magnetUrl);
                resultString.append("\",");
                resultString.append("\"showDate\":\"");
                resultString.append(showDate);
                resultString.append("\"}");
              }

              showCounter++;
      }
      line = bufferedReader.readLine();
    }

    if (!"TEXT".equals(resultFormat))
    {
      resultString.append("]");
    }

    bufferedReader.close();
    return resultString.toString();
  }

  protected BufferedReader getInputString(String siteUrl) throws IOException {
    URL url = new URL(siteUrl);
    URLConnection urlc = url.openConnection();
    
    InputStream inputStream = urlc.getInputStream();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

    return bufferedReader;
  }
}
