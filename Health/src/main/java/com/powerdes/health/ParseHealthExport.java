package com.powerdes.health;

public class ParseHealthExport {
	public static void main(String[] args) {
		
		//args[0] = some export.xml file
		ParseExport PE = new ParseExport(args[0]);
		PE.parseExport();
		PE.parse();
	}
}
