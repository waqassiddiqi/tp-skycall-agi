package agi.Util;

import org.apache.log4j.Logger;

public class CDRGenerator {
	public static CDRGenerator instance = null;
	private Logger log = Logger.getLogger(getClass().getName());
	
	public static CDRGenerator getInstance() {
		if(instance == null)
			instance = new CDRGenerator();
		
		return instance;
	}
	
	private CDRGenerator() { }
	
	public void generate(String[] args) {
		
		if(args == null)
			return;
		
		if(args.length == 0)
			return;
		
		StringBuilder sb = new StringBuilder();
		for(String arg : args) {
			sb.append(arg);
			sb.append(";");
		}
		
		log.info(sb.substring(0, sb.length() - 1));
	}
}
