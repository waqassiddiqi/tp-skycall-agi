package agi;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;

import agi.Util.CDRGenerator;
import agi.Util.PromptRepository;
import agi.db.MappingDao;
import agi.db.SubscriberDAO;
import agi.memcached.MemcachedHandling;
import agi.model.Subscriber;

public class Robot extends BaseAgiScript {
	private static Logger log = Logger.getLogger(Robot.class.getName());
	private static int channel = 1;
	private static int max_channels;
	private static int ringTimeout = 45;
	public static MemcachedHandling mch;	
	private static int searchTries = 1;
	
	static {
		ResourceBundle myResources = ResourceBundle.getBundle("agi");
		searchTries = Integer.parseInt(myResources.getString("channel_search_tries"));
		max_channels = Integer.parseInt(myResources.getString("max_channels"));
		ringTimeout = Integer.parseInt(myResources.getString("ring_time_out"));
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

		Subscriber sub = new SubscriberDAO().getSubscriberByMsisdn("92" + agiReq.getCallerIdNumber());
		String cmd = "";
		String var = "";
		
		if(sub != null && sub.getStatus() == 1) {
			String id = "";
			
			id = new MappingDao().getMapping(agiReq.getDnid(), agiReq.getCallerIdNumber());
			log.info("Skype ID is: " + id);
			
			if(id != null && id.trim().length() > 0) {
				
				int idleChannel = getIdleChannel();
				
				if(idleChannel > 0) {
					cmd = "SIP/stsTrunk_" + String.format("%02d", idleChannel) + "/" + id;

					CDRGenerator.getInstance().generate(
							new String[] { agiReq.getUniqueId(), "92" + agiReq.getCallerIdNumber(), agiReq.getDnid(), 
									Integer.toString(idleChannel) });
					
					log.info("Executing: " + cmd);
					
					try {
						exec("Dial", new String[] { cmd, Integer.toString(ringTimeout) });
						
						var = getVariable("DIALSTATUS");
						
						log.debug("DIALSTATUS: " + var);
						
						if(var != null && !var.toLowerCase().equalsIgnoreCase("answer")) {
							answer();
							streamFile(PromptRepository.getMessage("prompt.b_party_not_available"), "0123456789#*");
						}
						
					} catch (Exception exp) {
						log.error("Error in executing ", exp);
					}
				} else {
					answer();
					log.debug("Playing no_free_channel prompt");
					streamFile(PromptRepository.getMessage("prompt.no_free_channel"), "0123456789#*");
				}
			} else {
				answer();
				log.debug("Playing not_in_skype_list prompt");
				streamFile(PromptRepository.getMessage("prompt.not_in_skype_list"), "0123456789#*");
			}
			
		} else {
			answer();
			log.debug("Playing not_a_subscriber prompt");
			streamFile(PromptRepository.getMessage("prompt.not_a_subscriber"), "0123456789#*");
		}
		
		CDRGenerator.getInstance().generate(
				new String[] { agiReq.getUniqueId(), var });
		
		MDC.remove("requestid");
	}

	public static int getIdleChannel() {
		int chann = -1;
		
		for(int j=0; j<searchTries; j++) {
			for(int i=1; i<=max_channels; i++) {
				if(mch.getFromMemcache(chann + "") == null) {
					chann = i;
					mch.putInMemcached(chann + "", chann);
					return i;
				}
			}
		}
		
		return -1;
		/*
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
				mch.putInMemcached(chann + "", chann);
				break;
			}
		}
		
		channel = 0;
		
		return chann;
		*/
	}
}