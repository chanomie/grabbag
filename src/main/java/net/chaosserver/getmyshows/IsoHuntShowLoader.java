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
 * IsoHunt Loader
 */
public class IsoHuntShowLoader implements ShowLoader
{
  protected static final int RESULTS = 5;
  protected String urlRoot = "http://isohunt.to";
  protected Pattern showNamePattern = Pattern.compile(".*<td class=\"title-row\"><a href=\"([^\"]*)\"><span>(.*)</span>.*<i title=\"Verified Torrent\">.*<td class=\"date-row\">(.*?)</td>.*");
  private static final Logger log = Logger.getLogger(IsoHuntShowLoader.class.getName());

  public List<TorrentLink> getResultList(String searchTerm, String format, int results,
			String category) throws IOException {

	List<TorrentLink> torrentLinkList = new ArrayList<TorrentLink>();
    int showCounter = 0;

    // http://oldpiratebay.org/search.php?q=Grimm&iht=8&Torrent_sort=seeders.desc
    String siteUrl = urlRoot + "/torrents/?ihq=" + URLEncoder.encode(searchTerm, "UTF-8") + "&Torrent_sort=seeders.desc";
    if("Comics".equals(category)) {
    	siteUrl = urlRoot + "/torrents/?ihq=" + URLEncoder.encode(searchTerm, "UTF-8") + "&Torrent_sort=seeders.desc";
    }
    
	TorrentLink torrentLink = new TorrentLink();
    String detailsPageUrl = "";

    log.info("Making request to [" + siteUrl + "]");
    BufferedReader bufferedReader = getInputString(siteUrl);
    String line = bufferedReader.readLine();
    // log.info("Read first line");
    while ((line != null) && (showCounter < results))
    {
      log.info("line: [" + line +"]");
      Matcher matcher = this.showNamePattern.matcher(line);
      if (matcher.matches()) {
        detailsPageUrl = matcher.group(1);
        torrentLink.setTorrentName(matcher.group(2));
        torrentLink.setShowDate(matcher.group(3));
        torrentLink.setMagnetUrl(getDetailsMagnetLink(urlRoot + detailsPageUrl));
        
        torrentLinkList.add(torrentLink);
        torrentLink = new TorrentLink();
        showCounter++;
      }
      line = bufferedReader.readLine();
    }

    bufferedReader.close();
	return torrentLinkList;
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
    	
    String siteUrl = urlRoot + "/torrents/?ihq=" + URLEncoder.encode(searchTerm, "UTF-8") + "&Torrent_sort=seeders.desc";
    if("Comics".equals(category)) {
    	siteUrl = urlRoot + "/torrents/?ihq=" + URLEncoder.encode(searchTerm, "UTF-8") + "&Torrent_sort=seeders.desc";
    }
    
    String torrentName = "";
    String detailsPageUrl = "";
    String showDate = "";
    String magnetUrl = "";

    log.info("Making request to [" + siteUrl + "]");
    BufferedReader bufferedReader = getInputString(siteUrl);
    String line = bufferedReader.readLine();
    // log.info("Read first line");
    while ((line != null) && (showCounter < results))
    {
      log.info("line: [" + line +"]");
      Matcher matcher = this.showNamePattern.matcher(line);
      if (matcher.matches()) {
        detailsPageUrl = matcher.group(1);
        torrentName = matcher.group(2);
        showDate = matcher.group(3);
        
        magnetUrl = getDetailsMagnetLink(urlRoot + detailsPageUrl);

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

  protected String getDetailsMagnetLink(String detailsUrl) throws IOException {
    String magnetUrl = null;
    Pattern magnetPattern = Pattern.compile(".*href=\"(magnet:[^\"]*)\".*");

    BufferedReader bufferedReader = getInputString(detailsUrl);
    String line = bufferedReader.readLine();
    // log.info("Read first line");
    while ((line != null) && (magnetUrl == null)) {
      log.info("line: [" + line +"]");
      Matcher matcher = magnetPattern.matcher(line);
      if (matcher.matches()) {
    	  magnetUrl = matcher.group(1);
      }
      line = bufferedReader.readLine();
    }

	  return magnetUrl;
  }

  protected BufferedReader getInputString(String siteUrl) throws IOException {
    URL url = new URL(siteUrl);
    URLConnection urlc = url.openConnection();
    urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/600.3.14 (KHTML, like Gecko) Version/8.0.3 Safari/600.3.14");
    
    InputStream inputStream = urlc.getInputStream();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

    return bufferedReader;
  }
}
