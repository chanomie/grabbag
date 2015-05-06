package net.chaosserver.rss;

public class RssItem {
  /** The title of the item. */
  protected String title;
  
  /** The URL of the item. */
  protected String link;
  
  /** The item synopsis. */
  protected String description;
  
  /** Email address of the author of the item. */
  protected String author;
  
  /** Includes the item in one or more categories. */
  protected String category;
  
  /** URL of a page for comments relating to the item. */
  protected String comments;
  
  /** Describes a media object that is attached to the item. */
  protected String enclosure;
  
  /** A string that uniquely identifies the item. */
  protected String guid;
  
  /** Indicates when the item was published. */
  protected String pubDate;
  
  /** The RSS channel that the item came from. */
  protected String source;
  
  public void setTitle(String title) {
	  this.title = title;
  }
  
  public String getTitle() {
	  return this.title;
  }
  
  public void setLink(String link) {
	  this.link = link;
  }
  
  public String getLink() {
	  return this.link;
  }
  
  public void setDescription(String description) {
	  this.description = description;
  }
  
  public String getDescription() {
	  return this.description;
  }
  
  public void setAuthor(String author) {
	  this.author = author;
  }
  
  public String getAuthor() {
	  return this.author;
  }
  
  public void setCategory(String category) {
	  this.category = category;
  }
  
  public String getCategory() {
	  return this.category;
  }
  
  public void setComments(String comments) {
	  this.comments = comments;
  }
  
  public String getComments() {
	  return this.comments;
  }
  
  public void setEnclosure(String enclosure) {
	  this.enclosure = enclosure;
  }
  
  public void setGuid(String guid) {
	  this.guid = guid;
  }
  
  public String getGuid() {
	  return this.guid;
  }
  
  public void setPubDate(String pubDate) {
	  this.pubDate = pubDate;
  }
  
  public String getPubDate() {
	  return this.pubDate;
  }
  
  public void setSource(String source) {
	  this.source = source;
  }
  
  public String getSource() {
	  return this.source;
  }
 }
