package com.powerdes.health;

import org.w3c.dom.*;

import javax.xml.parsers.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

//Notes: only value and date are relevant
public class ParseExport {

	String filePath;

	//Maps creationDate -> Value
	Map<Date, Integer> StepCounts;
	Map<Date, Double> DistanceWalkingRunning;
	Map<Date, Integer> FlightsClimbed;

	public ParseExport(String filePath) {
		this.filePath = filePath;

		StepCounts = new TreeMap<Date, Integer>(new DateComparator());
		DistanceWalkingRunning = new TreeMap<Date, Double>(new DateComparator());
		FlightsClimbed = new TreeMap<Date, Integer>(new DateComparator());
	}

	/**
	 * Parses crappy XML file into hashmaps
	 */
	public void parseExport() {

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//lazy try catch
		try {

			//init
			File inputFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			//parse
			//Looking to grab all "Record" fields in HealthData node.
			NodeList nList = doc.getElementsByTagName("Record");

			String temp;
			String creationDate;

			for(int i = 0; i < nList.getLength(); i++) {
				Node currNode = nList.item(i);

				//all nodes are element nodes, skip check
				Element element = (Element) currNode;

				if(element.getAttribute("type").equals("HKQuantityTypeIdentifierStepCount")) {
				
					
					temp = element.getAttribute("creationDate");
					creationDate = temp.substring(0, temp.indexOf(' '));
					Date d = sdf.parse(creationDate);
					
					if(StepCounts.containsKey(d)) {
						StepCounts.put(d, StepCounts.get(d) + Integer.parseInt(element.getAttribute("value")));
					} else {
						StepCounts.put(d, Integer.parseInt(element.getAttribute("value")));
					}
				} else if(element.getAttribute("type").equals("HKQuantityTypeIdentifierDistanceWalkingRunning")) {
					temp = element.getAttribute("creationDate");
					creationDate = temp.substring(0, temp.indexOf(' '));
					Date d = sdf.parse(creationDate);

					if(DistanceWalkingRunning.containsKey(d)) {
						DistanceWalkingRunning.put(d, round(DistanceWalkingRunning.get(d) + Double.parseDouble(element.getAttribute("value")), 3));
					} else {
						DistanceWalkingRunning.put(d, round(Double.parseDouble(element.getAttribute("value")), 3));
					}
				} else if(element.getAttribute("type").equals("HKQuantityTypeIdentifierFlightsClimbed")) {
					temp = element.getAttribute("creationDate");
					creationDate = temp.substring(0, temp.indexOf(' '));
					Date d = sdf.parse(creationDate);

					if(FlightsClimbed.containsKey(d)) {
						FlightsClimbed.put(d, FlightsClimbed.get(d) + Integer.parseInt(element.getAttribute("value")));
					} else {
						FlightsClimbed.put(d, Integer.parseInt(element.getAttribute("value")));
					}
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints averages to console
	 * StepCounts
	 * DistanceWalkingRunning
	 * FlightsClimbed
	 * this method is probably sub-optimally implemented -_-;
	 * Writes to file also
	 */
	public void parse() {
		
		String filePath = System.getProperty("user.dir");
		Object[] parseHelper;
		String startDate = null;
		String endDate = null;
		double value = 0.0;

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		for(Date key: StepCounts.keySet()) {
			parseHelper = StepCounts.keySet().toArray();
			startDate = sdf.format(parseHelper[0]);
			endDate = sdf.format(parseHelper[parseHelper.length-1]);
			value = value + StepCounts.get(key);
		}

		System.out.println("Between "+startDate+" and "+endDate+", you averaged "+round(value/StepCounts.size(), 3)+" of steps per day");
		
		//
		
		StringBuilder s = new StringBuilder();
		s.append("Date,Steps" + "\r\n");
		for(Date key: StepCounts.keySet()) {
			s.append(key.toString()+","+StepCounts.get(key) + "\r\n");
		}
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filePath+"\\output\\"+startDate+"-"+endDate+"-StepCounts.csv", "UTF-8");
			writer.println(s.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		writer.close();
		
		//

		value = 0.0;
		
		for(Date key: DistanceWalkingRunning.keySet()) {
			parseHelper = StepCounts.keySet().toArray();
			startDate = sdf.format(parseHelper[0]);
			endDate = sdf.format(parseHelper[parseHelper.length-1]);
			value = value + DistanceWalkingRunning.get(key);
		}

		System.out.println("Between "+startDate+" and "+endDate+", you averaged "+round(value/DistanceWalkingRunning.size(), 3)+" miles per day");
		
		//
		
		s = new StringBuilder();
		s.append("Date,Miles" + "\r\n" );
		for(Date key: DistanceWalkingRunning.keySet()) {
			s.append(key.toString()+","+DistanceWalkingRunning.get(key) + "\r\n");
		}

		try {
			writer = new PrintWriter(filePath+"\\output\\"+startDate+"-"+endDate+"-DistanceWalkingRunning.csv", "UTF-8");
			writer.println(s.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		writer.close();
		
		//
		
		value = 0.0;
		
		for(Date key: FlightsClimbed.keySet()) {
			parseHelper = StepCounts.keySet().toArray();
			startDate = sdf.format(parseHelper[0]);
			endDate = sdf.format(parseHelper[parseHelper.length-1]);
			value = value + FlightsClimbed.get(key);
		}

		System.out.println("Between "+startDate+" and "+endDate+", you averaged "+round(value/FlightsClimbed.size(), 3)+" flights of stairs per day");
		
		s = new StringBuilder();
		s.append("Date,Flights" + "\r\n" );
		for(Date key: FlightsClimbed.keySet()) {
			s.append(key.toString()+","+FlightsClimbed.get(key));
		}

		try {
			writer = new PrintWriter(filePath+"\\output\\"+startDate+"-"+endDate+"-FlightsClimbed.csv", "UTF-8");
			writer.println(s.toString() + "\r\n" );
		} catch (Exception e) {
			e.printStackTrace();
		}
		writer.close();
	}
	
	//Helper Functions

	public boolean isAfter(String d1, String d2) {

		Date first = null;
		Date second = null;

		try {
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			first = sdf.parse(d1);
			second = sdf.parse(d2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return first.after(second);
	}

	/**
	 * Taken from http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
	 */
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	
	public class DateComparator implements Comparator<Date> {
	    public int compare(Date date1, Date date2) {
	        return date1.compareTo(date2);
	    }
	}

}
