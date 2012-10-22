package org.laeng.app.sql_elephant;

import java.io.File;

public class Main {

	
	public static void main(String[] args) {
		String USAGE = String.format("%s <config_file>\n", System.getProperty("sun.java.command"));
		
		if (args.length != 1) {
			System.out.println("Exactly 1 argument required\n");
			System.out.println(USAGE);
			System.exit(1);
		}
			
		try {
			File configFile = new File(args[0]);
			if (configFile.exists()) {
				new Dumper().dump(configFile);				
			}
			else {
				System.out.printf("Can't open config file: %s\n", configFile);
				System.out.println(USAGE);
				System.exit(2);
			}
			
			System.out.println(args[0]);
		} catch (Exception e) {
			System.out.println("Fatal Exception during dump\n");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
