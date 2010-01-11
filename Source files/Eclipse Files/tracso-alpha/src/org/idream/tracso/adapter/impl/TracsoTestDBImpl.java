package org.idream.tracso.adapter.impl;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import org.idream.tracso.adapter.TracsoDB;

public class TracsoTestDBImpl implements TracsoDB{

	public int createNewUser(String username, String password, String mobileNumber, String emailAddress) {
		// TODO Auto-generated method stub
		return 1;
	}

	public String[] fetchLocationOfUser(int userid) {
		// TODO Auto-generated method stub
		Random r = new Random();
		return new String[]{"INVALID", "007", "XYZ@TRACSO.COM", r.nextDouble()+"", r.nextDouble()+""};
	}

	public ArrayList getUsersList(String alphabet) {
		// TODO Auto-generated method stub
		ArrayList a = new ArrayList();
		a.add(new String[]{"InvaildService", "-1"});
		return a;
	}

	public void intialize(Properties appconfig) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Initialized");
	}

	public int validateLogin(String username, String password) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updateUserLocation(String userid, String lat, String lon) {
		// TODO Auto-generated method stub
		
	}

	public ArrayList getTrips(int userid) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList getTripPoints(String userid, String tripid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateTripLocation(String userid, String tripid, String lat,
			String lon) {
		// TODO Auto-generated method stub
		
	}

}
