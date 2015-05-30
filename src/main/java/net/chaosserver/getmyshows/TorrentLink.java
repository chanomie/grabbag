package net.chaosserver.getmyshows;

public class TorrentLink {
    protected String torrentName;
    protected String magnetUrl;
    protected String showDate;
    
    public void setTorrentName(String torrentName) {
    	this.torrentName = torrentName;
    }
    
    public String getTorrentName() {
    	return this.torrentName;
    }
    
    public void setMagnetUrl(String magnetUrl) {
    	this.magnetUrl = magnetUrl;
    }
    
    public String getMagnetUrl() {
    	return this.magnetUrl;
    }
    
    public void setShowDate(String showDate) {
    	this.showDate = showDate;
    }
    
    public String getShowDate() {
    	return this.showDate;
    }
}
