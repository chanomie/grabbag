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

public class PirateBayShowLoader implements ShowLoader {
	protected static final int RESULTS = 5;
	protected String urlRoot = "http://thepiratebay.se";
	protected Pattern showNamePattern = Pattern
			.compile(".*Details for.*>(.*)</a>.*");
	protected Pattern magnetPattern = Pattern
			.compile(".*<a href=\"(magnet:[^\"]*)\".*");
	protected Pattern vipPattern = Pattern.compile("vip.gif");
	protected Pattern showDatePattern = Pattern
			.compile(".*<font class=\"detDesc\">Uploaded (.+?)&nbsp;.*");
	private static final Logger log = Logger
			.getLogger(PirateBayShowLoader.class.getName());

	public List<TorrentLink> getResultList(String searchTerm, int results, String category)
			throws UnsupportedEncodingException, IOException {

		List<TorrentLink> torrentLinkList = new ArrayList<TorrentLink>();
		int showCounter = 0;

		String siteUrl = urlRoot + "/search/"
				+ URLEncoder.encode(searchTerm, "UTF-8") + "/0/7/0";
		if ("Comics".equals(category)) {
			siteUrl = urlRoot + "/search/"
					+ URLEncoder.encode(searchTerm, "UTF-8") + "/0/7/602";
		}
		TorrentLink torrentLink = new TorrentLink();
		boolean ignoreLine = false;

		log.info("Making request to [" + siteUrl + "]");
		BufferedReader bufferedReader = getInputString(siteUrl);
		String line = bufferedReader.readLine();
		// log.info("Read first line");
		while ((line != null) && (showCounter < results)) {
			Matcher matcher = this.showNamePattern.matcher(line);
			if (matcher.matches()) {
				torrentLink.setTorrentName(matcher.group(1));
			}

			matcher = this.magnetPattern.matcher(line);
			if (matcher.matches()) {
				torrentLink.setMagnetUrl(matcher.group(1));
				if ((!line.contains("vip.gif"))
						&& (!line.contains("trusted.png"))) {
					ignoreLine = true;
				}
			}

			matcher = this.showDatePattern.matcher(line);
			if (matcher.matches()) {
				if (!ignoreLine) {
					torrentLink.setShowDate(matcher.group(1));
					torrentLinkList.add(torrentLink);
					torrentLink = new TorrentLink();
					showCounter++;
				} else {
					ignoreLine = false;
				}
			}

			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		return torrentLinkList;
	}

	@Deprecated
	public String getResultObject(String searchTerm, String resultFormat,
			int results, String category) throws UnsupportedEncodingException,
			IOException {
		StringBuffer resultString = new StringBuffer();
		int showCounter = 0;

		if (!"TEXT".equals(resultFormat)) {
			resultString.append("[");
		}

		String siteUrl = urlRoot + "/search/"
				+ URLEncoder.encode(searchTerm, "UTF-8") + "/0/7/0";
		if ("Comics".equals(category)) {
			siteUrl = urlRoot + "/search/"
					+ URLEncoder.encode(searchTerm, "UTF-8") + "/0/7/602";
		}

		String torrentName = "";
		String magnetUrl = "";
		String showDate = "";
		boolean ignoreLine = false;

		log.info("Making request to [" + siteUrl + "]");
		BufferedReader bufferedReader = getInputString(siteUrl);
		String line = bufferedReader.readLine();
		// log.info("Read first line");
		while ((line != null) && (showCounter < results)) {
			Matcher matcher = this.showNamePattern.matcher(line);
			if (matcher.matches()) {
				torrentName = matcher.group(1);
			}

			matcher = this.magnetPattern.matcher(line);
			if (matcher.matches()) {
				magnetUrl = matcher.group(1);

				if ((!line.contains("vip.gif"))
						&& (!line.contains("trusted.png"))) {
					ignoreLine = true;
				}
			}

			matcher = this.showDatePattern.matcher(line);
			if (matcher.matches()) {
				if (!ignoreLine) {
					showDate = matcher.group(1);

					if ((showCounter > 0) && (!"TEXT".equals(resultFormat))) {
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
				} else {
					ignoreLine = false;
				}

			}

			line = bufferedReader.readLine();
		}

		if (!"TEXT".equals(resultFormat)) {
			resultString.append("]");
		}

		bufferedReader.close();
		return resultString.toString();
	}

	protected BufferedReader getInputString(String siteUrl) throws IOException {
		URL url = new URL(siteUrl);
		URLConnection urlc = url.openConnection();

		InputStream inputStream = urlc.getInputStream();

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));

		return bufferedReader;
	}
}
