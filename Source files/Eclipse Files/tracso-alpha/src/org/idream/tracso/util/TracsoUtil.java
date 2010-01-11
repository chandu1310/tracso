package org.idream.tracso.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.Random;

import org.idream.tracso.adapter.TracsoDB;
import org.idream.tracso.adapter.impl.TracsoTestDBImpl;

/*
 * Contans method to verify whether a user is logged in or not.
 * 
 * RESPONSE XML STRUCTURE:
 */
// <tracso>
// 	<uid></uid>
//	<uname></uname>
// 	<sid></sid>
// 	<location>
//	 	<puid></puid>
//	 	<latitude></latitude>
//	 	<longitude></longitude>
//	 </location>	
// </tracso>


//<tracso>
//	<uid></uid>
//	<uname></uname>
//	<sid></sid>
//	<userlist>
//		<user>
//		<uid></uid>
//		<name></name>
//		<user>
//	</userlist>
//</tracso>

public class TracsoUtil extends Base {
	
	private static TracsoUtil classInstance = null;
	ResponseCache cache;
	TracsoDB db;
	Properties applConfig;
	
	private TracsoUtil(File configFile)
	{
		super();
		applConfig = new Properties();
	    try {	    	
	    	applConfig.load(new FileInputStream(configFile));
	    	db = getDBObject();
		} catch (Exception e) {
			e.printStackTrace();
			db = new TracsoTestDBImpl(); 
		}
		cache = new ResponseCache(applConfig);
	}
	
	private TracsoDB getDBObject() throws Exception
	{
		String dbImpl = applConfig.getProperty("dbimpl");
		TracsoDB db = (TracsoDB)(Class.forName(dbImpl)).newInstance();
		db.intialize(applConfig);
		return db;
	}
	
	public final synchronized static TracsoUtil getInstance(File configFile)
	{
		if(classInstance == null)
			classInstance = new TracsoUtil(configFile);
		return classInstance;
	}
	
	public final synchronized int createNewUser(String username, String password,
												String mobileNumber, String emailAddress){
		return db.createNewUser(username, password, mobileNumber, emailAddress);		 
	}	
	
	public final synchronized String login(String username, String password){		
		
		int rslt = db.validateLogin(username, password); 
		if(rslt !=INVALID_CREDENTIALS)
		{
			return username.toUpperCase()+"@"+generateSessionId()+"%"+rslt;
		}
		return "INVALID@IN0VA0L0ID%-1";
	}
	
	public final synchronized void logout(String userid){
	}
	
	public final synchronized boolean isUserLoggedIn(String sessionId){
		
		return isSessionValid(sessionId);		
	}
	
	public final synchronized String generateSessionId()
	{
		char [] alphabet =	{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		Random r = new Random();
		StringBuffer sessionStr = new StringBuffer(""+Calendar.getInstance().getTimeInMillis());
		//System.out.println(sessionStr);
		for(int i=0; i<5; i++){
		sessionStr.insert(r.nextInt(sessionStr.length()-1), alphabet[r.nextInt(25)]);
		}
		return sessionStr.toString();
	}
	
	private final synchronized boolean isSessionValid(String sessionId)
	{
		StringBuffer decStr = new StringBuffer();
		for(int i=0; i<sessionId.length();i++)
		{
			if(Character.isDigit(sessionId.charAt(i)))
					decStr.append(sessionId.charAt(i));
		}
		Calendar cal = Calendar.getInstance();
		long l = Long.parseLong(decStr.toString());
		long diff = cal.getTimeInMillis() - l;		
		if( diff >= MAX_SESSION_LIMIT*60*1000 )
			return false;
		return true;
	}
	
//	<userlist>
//	<user><uid></uid><name></name><user>
//	</userlist>	
	public final synchronized String getUsersList(String alphabet)
	{
		ArrayList rst = db.getUsersList(alphabet);
		StringBuffer xml = new StringBuffer();
		xml.append("<userlist>");
		for (int i = 0; i < rst.size(); i++) {
			xml.append("<user><uid>"+((String[])rst.get(i))[1].trim()+"</uid><name>"+((String[])rst.get(i))[0].trim()+"</name></user>"); 
		}	
		xml.append("</userlist>");
		return xml.toString();
	}
	
	/*
	 * Current location fetch requests are not cached since they keep changing
	 * Hence, commented the code for caching.
	 */
	public final synchronized String getCurrentLocationOf(String userid, String puserid)
	{		
//		if(userid!=null && !"".equals(userid))
//		{
//			int uid = Integer.parseInt(userid);
			String respStr;
//			respStr = cache.getFromCache(uid);
//			if (respStr == null)
//			{
				respStr = fetchLocationFromDatabase(Integer.parseInt(puserid));
//				cache.pushToCache(uid, respStr);
//			}
			return respStr;
//		}
//		return "<location>" +
//				"<puid>"+puserid+"</puid>" +
//				"<username>INVALID</username>" +
//				"<mobilenumber>INVALID</mobilenumber>" +
//				"<emailaddress>INVALID</emailaddress>" +				
//				"<latitude>-1</latitude>" +
//				"<longitude>-1</longitude>" +
//				"</location>";
	}
	
	
	/*
	 * Caching only trip responses since these are static.
	 */
//	<trips>
//	<tripid></tripid>
//	</trips>
	public final synchronized String getTrips(String userid, String puserid)
	{
		if(userid!=null && !"".equals(userid))
		{
			int uid = Integer.parseInt(userid);
			String respStr = cache.getFromCache(uid);
			if (respStr == null)
			{
				ArrayList trips = db.getTrips(Integer.parseInt(puserid));
				respStr = "<trips>";
				for(int i=0; i<trips.size();i++)
				{
					respStr = respStr+"<tripid>"+trips.get(i)+"</tripid>";
				}
				respStr=respStr+"</trips>";
				cache.pushToCache(uid, respStr);
			}
			return respStr;
		}
		return "<trips></trips>";
	}
	
//	<trippoints>
//		<point>
//			<user_id></user_id>
//			<trip_id></trip_id>
//			<lattitude></lattitude>
//			<longitude></longitude>
//			<last_updated_on></last_updated_on>
//		</point>
//	</trippoints>	
	public final synchronized String getTripPoints(String puserid, String tripid)
	{
		StringBuffer respStr = new StringBuffer();
		respStr.append("<trippoints>");
		ArrayList pts = db.getTripPoints(puserid, tripid);
		
		if(pts.size()>0)
		{
			String[] cols = (String[])pts.get(0);
			for(int j=1; j<pts.size(); j++)
			{
				String val[] = (String[])pts.get(j);
				respStr.append("<point>");
				for(int i=0; i<cols.length; i++)
				{
					respStr.append("<"+cols[i]+">"+val[i]+"</"+cols[i]+">");
				}
				respStr.append("</point>");
			}
		}
		respStr.append("</trippoints>");
		return respStr.toString();
	}
	
	public final synchronized String fetchLocationFromDatabase(int puid)
	{
		String respStr;

		String[] loc = db.fetchLocationOfUser(puid);
		respStr = "<location>" +
		"<puid>"+puid+"</puid>" +
		"<username>"+loc[0].trim()+"</username>" +
		"<mobilenumber>"+loc[1].trim()+"</mobilenumber>" +
		"<emailaddress>"+loc[2].trim()+"</emailaddress>" +
		"<latitude>"+loc[3].trim()+"</latitude>" +
		"<longitude>"+loc[4].trim()+"</longitude>" +
		"</location>";
		return respStr;
	}
	
	public final synchronized void updateLocation(String uid, String latitude, String longitude)
	{
		//TO DO: Connect to database and update latitude and longitude locations
		System.out.println(uid+" - "+latitude+" - "+longitude);
		db.updateUserLocation(uid, latitude, longitude);
		return;
	}
	
	public final synchronized void updateTripLocation(String uid, String tripid, String latitude, String longitude)
	{
		db.updateTripLocation(uid, tripid, latitude, longitude);
		return;
	}
	
	
	public static void main(String[] args) {
		TracsoUtil lm = TracsoUtil.getInstance(new File("tracso.config"));
		//lm.createNewUser("charan", "magadheera", "1234567891", "charan@tracso.com");
		lm.updateLocation("1132", "-91.1", "-97.4");
		
		String sid = lm.login("admin", "welcome");		
		System.out.println(sid);		
		System.out.println(lm.isSessionValid( sid.substring(sid.indexOf('@')+1, sid.indexOf('%')) ));
		System.out.println(lm.getUsersList("c"));		
		System.out.println(lm.getCurrentLocationOf("1126", "1132"));
	}
}
