package org.idream.tracso.util;

import java.util.Properties;

public class Base {
	
	//REQTYPE CONSTANTS
	public static final int LOGIN = 1; //Login req with uid and pwd - response is uid, sid, name.
	public static final int LOGOUT = 2; //logout req - kill session
	public static final int PPLLST = 3; // people list - alphabet and sid - resp is a sid, list of ppl with their uid and names 
	public static final int LOCREQ = 4; // location req of a uid with sid. resp is a latitue and long with new sid.  
	public static final int UPDLOC = 5; // update location of a uid with given lat and long. sid not required for this.
	public static final int NEWUSER = 6; // update location of a uid with given lat and long. sid not required for this.
	public static final int TRIPS = 7; // get trip ids for a given uid
	public static final int TRIPPOINTS = 8; // get trip points for a given uid and trip id.
	public static final int UPDTRIPLOC = 9; // upload trip point.
	
	public static final int VALID_CREDENTIALS = 7;
	public static final int INVALID_CREDENTIALS = -800;

	public static int MAX_SESSION_LIMIT = 1;
	public static int CACHE_SIZE;
	
	public Base()
	{
		Base.MAX_SESSION_LIMIT = 2; // 15 Mins in productive. 2 mins in dev
		Base.CACHE_SIZE = 100;
	}
	
	public Base(Properties applConfig) {
		String maxSessionLimitStr = applConfig.getProperty("MAX_SESSION_LIMIT");
		String cacheSizeStr = applConfig.getProperty("CACHE_SIZE");
		if(maxSessionLimitStr!=null && !"".equals(maxSessionLimitStr))
			Base.MAX_SESSION_LIMIT = Integer.parseInt(maxSessionLimitStr);
		
		if(cacheSizeStr!=null && !"".equals(cacheSizeStr))
			Base.CACHE_SIZE = Integer.parseInt(cacheSizeStr);
	}
}
