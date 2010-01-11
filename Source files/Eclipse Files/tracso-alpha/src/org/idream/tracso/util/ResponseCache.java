package org.idream.tracso.util;

import java.util.Properties;

public class ResponseCache extends Base {	
	
	int start=0, end=-1;
	private Object cache[][];
	
	public ResponseCache(Properties applConfig) {
		super(applConfig);
		cache = new Object[CACHE_SIZE][2];
	}
	
	//latest entry will be end and start is the tail where we remove entries.
	public String getFromCache(int uid)
	{
		String resp = null;
		for(int i=0, j=end; i<CACHE_SIZE; i++, j=((j-1)%CACHE_SIZE))
		{
			if(j<start)
				break;
			if( cache[j][0] != null && ((Integer)cache[j][0]).intValue()==uid )
			{
				resp = (String)cache[j][0];
				break;
			}
		}
		return resp;
	}
	
	public void pushToCache(int uid, String resp)
	{
		end = (end+1)%CACHE_SIZE;
		if(start==end)
		{
			start = (start+1)%CACHE_SIZE;
		}
		cache[end][0] = new Integer(uid);
		cache[end][1] = resp;
	}
	
	public static class CacheEntry {
		int uid;
		String responseXML;
		public int getUid() {
			return uid;
		}
		public void setUid(int uid) {
			this.uid = uid;
		}
		public String getResponseXML() {
			return responseXML;
		}
		public void setResponseXML(String responseXML) {
			this.responseXML = responseXML;
		}		
	}
}
