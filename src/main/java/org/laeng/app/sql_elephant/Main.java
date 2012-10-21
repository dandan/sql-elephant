package org.laeng.app.sql_elephant;

public class Main {

	public static void main(String[] args) {
		try {
			new Dumper().dump();
		} catch (Exception e) {
			System.out.println("Fatal Exception during dump");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
