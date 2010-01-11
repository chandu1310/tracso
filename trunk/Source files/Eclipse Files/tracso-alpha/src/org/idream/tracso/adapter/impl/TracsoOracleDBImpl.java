package org.idream.tracso.adapter.impl;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Properties;

import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import oracle.jdbc.pool.OraclePooledConnection;

import org.idream.tracso.adapter.TracsoDB;
import org.idream.tracso.util.Base;

public class TracsoOracleDBImpl implements TracsoDB {
	private String ora_connString;
	private String ora_username;
	private String ora_password;


//	private OracleDataSource dataSource = null;
	private OracleConnectionPoolDataSource pool = null;
	
	public void intialize(Properties appconfig) throws Exception {
		// TODO Auto-generated method stub
			ora_connString = "jdbc:oracle:thin:@"+appconfig.getProperty("oradbserver")+":"+appconfig.getProperty("oradbname");
			ora_username = appconfig.getProperty("orausername");
			ora_password = appconfig.getProperty("orapassword");			
			pool = new OracleConnectionPoolDataSource();
			pool.setURL(ora_connString);
			pool.setUser(ora_username);
			pool.setPassword(ora_password);
	}
	
	public synchronized OracleConnection getConnection() throws Exception
	{
		OraclePooledConnection pc = (OraclePooledConnection)pool.getPooledConnection();
		OracleConnection conn =  (OracleConnection)pc.getConnection();
		return conn;
	}
	
	public int createNewUser(String username, String password, String mobileNumber, String emailAddress) {
		// TODO Auto-generated method stub
		int userid = -1;
		try {
			OracleConnection con = getConnection();
			CallableStatement stmt = con.prepareCall("{call tracso_new_user (?,?,?,?,?)}");
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.setString(3, mobileNumber);
			stmt.setString(4, emailAddress);
			stmt.registerOutParameter(5, Types.NUMERIC);
			stmt.execute();
			userid = stmt.getInt(5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			userid=-1;
		}
		return userid;
	}

	public String[] fetchLocationOfUser(int userid) {
		// TODO Auto-generated method stub
		String[] geolocationDetails = new String[]{"INVALID", "0000000000", "XYZ@tracso.com", "-1", "-1"};
		try {
			String query = "SELECT USER_NAME, MOBILE_NUMBER, EMAIL_ADDRESS, LATTITUDE, LONGITUDE FROM personal_information, locations WHERE personal_information.user_id = '"+userid+"' AND personal_information.user_id = locations.user_id"; 
			OracleConnection con = getConnection();
			Statement stmt = con.createStatement();
			ResultSet rslt = stmt.executeQuery(query);
			if(rslt.next())
			{
				geolocationDetails[0] = rslt.getString("USER_NAME");
				geolocationDetails[1] = rslt.getString("MOBILE_NUMBER");
				geolocationDetails[2] = rslt.getString("EMAIL_ADDRESS");
				geolocationDetails[3] = rslt.getString("LATTITUDE");
				geolocationDetails[4] = rslt.getString("LONGITUDE");
				rslt.close();
				return geolocationDetails; 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return geolocationDetails;
	}

	public ArrayList getUsersList(String alphabet) {
		ArrayList userslist = new ArrayList();
		try {
			String query = "SELECT USER_NAME, USER_ID FROM personal_information WHERE Lower(user_name) LIKE '"+alphabet.toLowerCase()+"%'"; 
				//"SELECT USER_NAME, USER_ID FROM tracso_users WHERE Lower(user_name) LIKE '"+alphabet.toLowerCase()+"%'";
			OracleConnection con = getConnection();
			Statement stmt = con.createStatement();
			ResultSet rslt = stmt.executeQuery(query);
				while(rslt.next())
				{
					String entry[] = new String[2];
					entry[0] = rslt.getString("USER_NAME");
					entry[1] = rslt.getString("USER_ID");
					userslist.add(entry);
				}
				rslt.close();
				return userslist; 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userslist;
	}

	public int validateLogin(String username, String password) {
		// TODO Auto-generated method stub
		int result = Base.INVALID_CREDENTIALS;
		try {
			String query ="SELECT Count(*) recordcount FROM users, personal_information WHERE users.user_id = personal_information.user_id AND Lower(user_name) = '"+username.toLowerCase()+"' AND Lower(password) = '"+password.toLowerCase()+"'"; 
			OracleConnection con = getConnection();
			Statement stmt = con.createStatement();
			ResultSet rslt = stmt.executeQuery(query);
				if(rslt.next())
				{
					if( rslt.getInt("recordcount") ==1)
					{
						rslt.close();
						query = "SELECT users.user_id FROM users, personal_information WHERE users.user_id = personal_information.user_id AND Lower(user_name) = '"+username.toLowerCase()+"' AND Lower(password) = '"+password.toLowerCase()+"'"; 
						rslt = stmt.executeQuery(query);
						rslt.next();
						return rslt.getInt("USER_ID");
					}	
					rslt.close();
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;		
	}

	public void updateUserLocation(String userid, String lat, String lon) {
		// TODO Auto-generated method stub
		try {
			String query = "UPDATE locations SET LATTITUDE = '"+lat+"', LONGITUDE = '"+lon+"' WHERE USER_ID = "+userid;
			OracleConnection con = getConnection();
			Statement stmt = con.createStatement();
			ResultSet rslt = stmt.executeQuery(query);
				rslt.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateTripLocation(String userid, String tripid, String lat, String lon) {
		// TODO Auto-generated method stub
		try {
			OracleConnection con = getConnection();    //tracso_addtotrip(1162, 'admin-1',17.376852, 78.486328)
			CallableStatement stmt = con.prepareCall("{call tracso_addtotrip(?,?,?,?)}");
			stmt.setString(1, userid);
			stmt.setString(2, tripid);
			stmt.setString(3, lat);
			stmt.setString(4, lon);
			stmt.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	public ArrayList getTrips(int userid) {
		ArrayList tripList = new ArrayList();
		try {
			String query = "SELECT trip_id FROM trip_information WHERE user_id = '"+userid+"'  AND last_updated_on > SYSDATE-1 GROUP BY trip_id"; 
			OracleConnection con = getConnection();
			Statement stmt = con.createStatement();
			ResultSet rslt = stmt.executeQuery(query);
				while(rslt.next())
				{
					String tripIdStr = rslt.getString("TRIP_ID");
					tripList.add(tripIdStr);
				}
				rslt.close();
				return tripList; 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tripList;
	}
	
	/*
	 * - returns an array list with String arrays
	 * - First Entry in the returned array list will have a string array with the 
	 * column names
	 */
	public ArrayList getTripPoints(String userid, String tripid)
	{
		ArrayList tripPoints = new ArrayList();
		try {
			String query = "SELECT * FROM trip_information WHERE user_id = '1162'  AND trip_id='admin-2' AND last_updated_on > SYSDATE-1 ORDER BY last_updated_on asc"; 
			OracleConnection con = getConnection();
			Statement stmt = con.createStatement();
			ResultSet rslt = stmt.executeQuery(query);
			ResultSetMetaData meta = rslt.getMetaData();
			int colCount = meta.getColumnCount();
			
			String colNames[] = new String[colCount];
			for(int i=1; i<=colCount; i++)
				colNames[i-1] = meta.getColumnName(i).trim();
			
			tripPoints.add(colNames);
			
			while(rslt.next())
			{
				String values[] = new String[colCount];
				for(int i=0; i<colCount; i++)
				{
					values[i] = rslt.getString(colNames[i]);
				}
				tripPoints.add(values);
			}
			rslt.close();
			return tripPoints; 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return tripPoints;
	}
}
