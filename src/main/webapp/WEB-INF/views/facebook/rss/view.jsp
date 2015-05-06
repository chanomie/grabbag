<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
  <head>
    <title>Facebook RSS</title>
  </head>
  <body>
  	<h1>Facebook RSS</h1>
  	<c:forEach items="${rssItemList}" var="rssItem">
  	  <table border="1">
  	    <tr>
  	      <th>Attribute</th>
  	      <th>Value</th>
  	    </tr>
  	    <tr>
  	      <td>Title</td>
  	      <td><c:out value="${rssItem.title}"/></td>
  	    </tr>
  	    <tr>
  	      <td>Link</td>
  	      <td><c:out value="${rssItem.link}"/></td>
  	    </tr>
  	    <tr>
  	      <td>Description</td>
  	      <td><c:out value="${rssItem.description}"/></td>
  	    </tr>
  	    <tr>
  	      <td>Author</td>
  	      <td><c:out value="${rssItem.author}"/></td>
  	    </tr>
  	    <tr>
  	      <td>Comments</td>
  	      <td><c:out value="${rssItem.comments}"/></td>
  	    </tr>
  	    <tr>
  	      <td>Guid</td>
  	      <td><c:out value="${rssItem.guid}"/></td>
  	    </tr>
  	    <tr>
  	      <td>PubDate</td>
  	      <td><c:out value="${rssItem.pubDate}"/></td>
  	    </tr>  	    
  	  </table>
  	</c:forEach>
  </body>
</html>