package agi;

import java.io.IOException;

import net.rubyeye.xmemcached.XMemcachedClient;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new XMemcachedClient("127.0.0.1", 88);
	}

}
