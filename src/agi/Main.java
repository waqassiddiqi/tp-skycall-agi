package agi;

import java.io.IOException;

import org.asteriskjava.fastagi.AgiException;

import agi.Util.PromptRepository;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws AgiException 
	 */
	public static void main(String[] args) throws IOException, AgiException {
		System.out.print(PromptRepository.getMessage("prompt.not_a_subscriber"));
	}

}
