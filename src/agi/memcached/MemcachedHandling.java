package agi.memcached;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.log4j.Logger;

public class MemcachedHandling {
	private Logger log = Logger.getLogger(getClass().getName());
	private MemcachedClientBuilder builder;
	private MemcachedClient client;
	final int default_expiry = 3600;
	private static int expiry;

	public int getExpiry() {
		if (expiry <= 0) {
			return 3600;
		}
		return expiry;
	}

	public void setExpiry(int expiry) {
		MemcachedHandling.expiry = expiry;
	}

	public MemcachedHandling(String hostname, int port) throws IOException {
		this.client = new XMemcachedClient(hostname, port);
	}

	public MemcachedHandling(String hostname1, int port1, int weight1,
			String hostname2, int port2, int weight2)
			throws IOException {
		this.builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses(hostname1 + ":" + port1 + " " + hostname2
						+ ":" + port2), new int[] { weight1, weight2 });
		this.client = this.builder.build();
	}

	public void putSubType(String msisdn, String subtype) {
		try {
			
			log.debug("[Memcached] Putting key:" + msisdn + "_subtype value:" + subtype);
			this.client.add(msisdn + "_subtype", 86400, subtype);
			
		} catch (TimeoutException | InterruptedException | MemcachedException ex) {
			log.error(ex.getMessage(), ex);
			log.error(ex.getCause(), ex);
		}
	}

	public String getSubType(String msisdn) {
		log.debug("Getting subtype from cache msisdn:" + msisdn);
		
		try {
			String subType = (String) this.client.get(msisdn + "_subtype");
			if (subType == null) {
				return "";
			}
			
			return subType;
			
		} catch (TimeoutException | InterruptedException | MemcachedException ex) {
			log.error(ex.getMessage(), ex);
			log.error(ex.getCause(), ex);
		}
		
		return "";
	}

	public boolean putInMemcached(String key, Object data) {
		try {
			
			this.client.add(key, expiry, data);
			
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	public boolean removeFromMemcached(String key) {
		try {
			
			log.debug("[Memcached] deleting : [" + key + "]");
			
			this.client.delete(key);
			
			log.debug("[Memcached] deleted : [" + key + "]");
			
			return true;
			
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return false;
	}

	public Object getFromMemcache(String key) {
		try {
			
			log.debug("[Memcached] Getting Key: " + key);
			Object arr = (String) this.client.get(key);
			log.debug("[Memcached] getNotification return: " + arr);
			
			return arr;
			
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return null;
	}	
	
	public void clearCache() {
		try {
			
			log.debug("[Memcached] Clearing cache");
			
			client.flushAll();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}
}
