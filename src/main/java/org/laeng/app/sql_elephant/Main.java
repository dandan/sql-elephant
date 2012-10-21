package org.laeng.app.sql_elephant;

public class Main {

	public static void main(String[] args) {
		try {
			new SqlElephant().dump_to_csv();
		} catch (Exception e) {
			System.out.println("Fatal Exception during dump");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
