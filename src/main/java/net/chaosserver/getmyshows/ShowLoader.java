package net.chaosserver.getmyshows;

import java.io.IOException;
import java.util.List;

public interface ShowLoader {
	List<TorrentLink> getResultList(String showName, int results,
			String category) throws IOException;

}