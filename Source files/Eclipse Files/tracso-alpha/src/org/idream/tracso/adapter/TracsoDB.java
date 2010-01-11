package org.idream.tracso.adapter;

import java.util.ArrayList;
import java.util.Properties;

public interface TracsoDB {
	public static final int INIT_SUCC = 1;
	public static final int INIT_FAILED = -1;
	public static final int INIT_PENDING = 0;
	
	public void intialize(Properties appconfig)  throws Exception;
	public int createNewUser(String username, String password, String mobileNumber, String emailAddress);
	public int validateLogin(String username, String password);
	public ArrayList getUsersList(String alphabet);
	public String[] fetchLocationOfUser(int userid);
	public ArrayList getTrips(int userid);
	public ArrayList getTripPoints(String userid, String tripid);
	public void updateUserLocation(String userid, String lat, String lon);
	public void updateTripLocation(String userid, String tripid, String lat, String lon);	
}
