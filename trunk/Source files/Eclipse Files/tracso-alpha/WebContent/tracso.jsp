<%@ page language="java" contentType="text/xml; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="org.idream.tracso.util.TracsoUtil"%>
<%@page import="org.idream.tracso.util.Base"%>
<%@page import="java.io.File"%>
<%
	String reqTypeStr=request.getParameter("reqType");
	if(reqTypeStr!=null && !"".equals(reqTypeStr))
	{
		//<tracso>
		//	<uid></uid>
		//	<uname></uname>
		//	<sid></sid>
		//	<location>
		// 	<puid></puid>
		// 	<latitude></latitude>
		// 	<longitude></longitude>
		// </location>	
		//</tracso>
		String responseXML = "<tracso>";
		String dataXML = "";
		int reqType= -1;
		String uid = "";
		String sid = "";
		String uname = "";
		
		reqType = Integer.parseInt(reqTypeStr);
		
		File configFile = new File(request.getRealPath("/tracso.config"));
		
		TracsoUtil tu = TracsoUtil.getInstance(configFile);		
		
		switch(reqType)
		{
			case Base.NEWUSER:
				{
					String p_username = request.getParameter("username");				
					String p_password = request.getParameter("password");
					String p_mobilenumber = request.getParameter("mno");
					String p_emailaddress = request.getParameter("email");
					int uid_int = tu.createNewUser(p_username, p_password, p_mobilenumber, p_emailaddress);
					uname = p_username;
					if(uid_int == -1)
						sid = "-1";
					else
						sid = tu.generateSessionId();
					uid = uid_int+"";
				}
				break;
			case Base.LOGIN:
				{
				String userid=request.getParameter("userid");
				String pwd=request.getParameter("password");
				String str1 = tu.login(userid, pwd);
				uname = str1.substring(0,  str1.indexOf('@'));
				sid =  str1.substring(str1.indexOf('@')+1, str1.indexOf('%'));	
				uid =  str1.substring(str1.indexOf('%')+1);
				}
				break;
			case Base.LOGOUT:
				//NOTHING TO DO. AVOID GETTING SUCH REQ. INITIALISE FLEX APP ON LOGOUT
				break;		
			case Base.PPLLST:
				{
					sid= request.getParameter("sid");
					if(tu.isUserLoggedIn(sid))
					{
						uid=request.getParameter("uid");
						String alphabet = request.getParameter("alpha");
						dataXML = tu.getUsersList(alphabet);
						sid = tu.generateSessionId();
					}				
				}
				break;
			case Base.LOCREQ:
				{
					uid=request.getParameter("uid");
					String puid= request.getParameter("puid");
					sid= request.getParameter("sid");
					if(tu.isUserLoggedIn(sid))
					{
						dataXML = tu.getCurrentLocationOf(uid, puid);
						sid = tu.generateSessionId();
					}
				}
				break;
			case Base.UPDLOC:
				{			
					uid=request.getParameter("uid");					
					String lat = request.getParameter("lat");
					String lon = request.getParameter("lon");
					tu.updateLocation(uid, lat, lon);
				}
				break;	
			case Base.UPDTRIPLOC:
				{
					uid=request.getParameter("uid");		
					String tid= request.getParameter("tid");
					String lat = request.getParameter("lat");
					String lon = request.getParameter("lon");
					tu.updateTripLocation(uid, tid, lat, lon);										
				}
				break;
			case Base.TRIPS:
				{
					uid=request.getParameter("uid");
					String puid= request.getParameter("puid");
					sid= request.getParameter("sid");
					if(tu.isUserLoggedIn(sid))
					{
						dataXML = tu.getTrips(uid, puid);
						sid = tu.generateSessionId();
					}
				}
				break;
			case Base.TRIPPOINTS:
				{
					uid=request.getParameter("uid");
					String puid= request.getParameter("puid");
					String tripid= request.getParameter("tid");
					sid= request.getParameter("sid");
					if(tu.isUserLoggedIn(sid))
					{
						dataXML = tu.getTripPoints(puid, tripid);
						sid = tu.generateSessionId();
					}					
				}
				break;
		}
		responseXML = responseXML 
		+"<uid>"+uid+"</uid>"
		+"<uname>"+uname+"</uname>"
	 	+"<sid>"+sid+"</sid>"
	 	+dataXML	
	 	+"</tracso>";
	 	%>
	 	<%=responseXML %>
	 	<%
	}
	else {
		response.sendRedirect("index.html");
	}
%>
