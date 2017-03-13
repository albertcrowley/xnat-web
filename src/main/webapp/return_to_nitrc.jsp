<%@ page import="java.io.*" %>
<%@ page import="javax.xml.parsers.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="java.util.Iterator" %>





<HTML>
 <HEAD>
  <TITLE>NITRC</TITLE>

<%!


 public void buildMapping(Node current, int depth, java.util.HashMap map, StringBuffer debug) {
debug.append("build map called <br>");
    String data = "";
    if (current instanceof Element) {
        Element temp = (Element) current;
        if ( temp.getTagName() == "project" ) {
		  map.put(temp.getAttribute("ir_name"), temp.getAttribute("nitrc_name"));
		}
	  
    }
    for (int i = 0; i < current.getChildNodes().getLength(); i++) {
      buildMapping(current.getChildNodes().item(i), depth+1, map, debug);
    }
}

%>
<%
//    Builder builder = new Builder();


    try {
        File fXmlFile = new File("/opt/nitrc_import/nitrc_import_config.xml");
        if(!fXmlFile.exists() || fXmlFile.isDirectory()) {
            fXmlFile = new File("/data/xnat/nitrc_import/nitrc_import_config.xml");
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

      Element root = doc.getDocumentElement();
	  java.util.HashMap map = new java.util.HashMap<String,String>();

StringBuffer debug = new StringBuffer();

      buildMapping(root, 0, map, debug);



      	String projectName = request.getParameter("name");
	if (projectName == null || projectName.equals("")) {
	   projectName = request.getParameter("project");
	}
	String nitrcProjectName = map.get(projectName).toString();


      if ("join".equals(request.getParameter("action"))){
        out.println ("<meta http-equiv=\"refresh\" content=\"0; url=/project/request.php?group_name=" + nitrcProjectName + "\" >");
      } else { // Default action is to redirect to project home page
	out.println ("<meta http-equiv=\"refresh\" content=\"0; url=/projects/" + nitrcProjectName + "/\" >");
      }

    }
    catch (IOException ex) {
      out.println(ex);
    }

%>

 </HEAD>
 <BODY>
 </BODY>
</HTML>
