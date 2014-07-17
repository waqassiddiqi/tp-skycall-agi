package agi;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;

import agi.Util.PromptRepository;
import agi.db.MappingDao;
import agi.memcached.MemcachedHandling;

public class Robot extends BaseAgiScript {
	private static Logger log = Logger.getLogger(Robot.class.getName());
	private static int channel = 1;
	private static int max_channels;
	public static MemcachedHandling mch;	
	
	static {
		ResourceBundle myResources = ResourceBundle.getBundle("agi");
		max_channels = Integer.parseInt(myResources.getString("max_channels"));
		initializeMemcache(myResources);
	}

	public static void initializeMemcache(ResourceBundle myResources) {
		try {
			String host1 = myResources.getString("memCacheHost1");
			String host2 = myResources.getString("memCacheHost2");
			int port1 = Integer.parseInt(myResources.getString("memCachePort1"));
			int port2 = Integer.parseInt(myResources.getString("memCachePort2"));

			int weight1 = Integer.parseInt(myResources.getString("memCacheWeight1"));
			int weight2 = Integer.parseInt(myResources.getString("memCacheWeight2"));
						
			int expiryTime = Integer.parseInt(myResources.getString("expiryTimeInSecs"));
			
			if ("".equals(host2)) {
				mch = new MemcachedHandling(host1, port1);
			} else {
				mch = new MemcachedHandling(host1, port1, weight1, host2, port2, weight2);
			}
			
			mch.setExpiry(expiryTime);
			
		} catch (Exception e) {
			log.error("Exception in ", e);
		}
	}
	
	public void service(AgiRequest agiReq, AgiChannel channel) throws AgiException {
		
		MDC.put("requestid", agiReq.getUniqueId());
		
		log.info("CallerID number: " + agiReq.getCallerIdNumber());
		log.info("CallerAni2 number: " + agiReq.getCallingAni2());
		log.info("Channel Id:" + agiReq.getUniqueId());
		log.info("Channel Dnid:" + agiReq.getDnid());
		log.info("Channel Rdnis:" + agiReq.getRdnis());
		log.info("Channel UniqueId:" + agiReq.getUniqueId());

		String id = "";
		
		id = new MappingDao().getMapping(agiReq.getDnid(), agiReq.getCallerIdNumber());
		log.info("SKype ID is: " + id);
		
		if(id != null && id.trim().length() > 0) {
			
			int idleChannel = getIdleChannel();
			String cmd = "SIP/stsTrunk_" + idleChannel + "/" + id;

			log.info("Executing: " + cmd);
			try {
				exec("Dial", cmd);
			} catch (Exception exp) {
				log.error("Error in executing ", exp);
			}
			
		} else {
			streamFile(PromptRepository.getMessage("not_in_skype_list"), "0123456789#*");
		}
		
		MDC.remove("requestid");
	}

	public static int getIdleChannel() {
		int chann = -1;
		for (;;) {
			if (channel < max_channels) {
				chann = channel % max_channels;
				channel += 1;
			} else {
				log.error("No channel idle, All channels busy");
				break;
			}
			Object value = mch.getFromMemcache(chann + "");
			if (value == null) {
				break;
			}
		}
		
		channel = 1;
		return chann;
	}
}
