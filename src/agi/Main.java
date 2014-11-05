package agi;

import java.io.IOException;
import java.util.ResourceBundle;

import agi.memcached.MemcachedHandling;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ResourceBundle myResources = ResourceBundle.getBundle("agi");
		
		MemcachedHandling mch;
		
		String host1 = myResources.getString("memCacheHost1");
		String host2 = myResources.getString("memCacheHost2");
		int port1 = Integer.parseInt(myResources.getString("memCachePort1"));
		int port2 = Integer.parseInt(myResources.getString("memCachePort2"));

		int weight1 = Integer.parseInt(myResources.getString("memCacheWeight1"));
		int weight2 = Integer.parseInt(myResources.getString("memCacheWeight2"));
					
		
		if ("".equals(host2)) {
			mch = new MemcachedHandling(host1, port1);
		} else {
			mch = new MemcachedHandling(host1, port1, weight1, host2, port2, weight2);
		}
		
		if(mch != null) {
			mch.clearCache();
		}
		
		new org.asteriskjava.fastagi.DefaultAgiServer();
	}

}