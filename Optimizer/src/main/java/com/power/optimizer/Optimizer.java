package com.power.optimizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * 
 * @author Jasper
 *
 */
public class Optimizer {

	static String workingDir = System.getProperty("user.dir");

	public static void main(String[] args) {

		ArrayList<String> processes = new ArrayList<String>();

		try {
			String line;
			Process p = Runtime.getRuntime().exec
					(System.getenv("windir") +"\\system32\\"+"tasklist.exe" + " /fo CSV");
			BufferedReader input =
					new BufferedReader(new InputStreamReader(p.getInputStream()));
			input.readLine();
			while ((line = input.readLine()) != null) {
				processes.add(line.split(",")[0].replace("\"", ""));
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}

		buildPriorityChanger(processes);
		try {
			String line;
			Process p = Runtime.getRuntime().exec("cmd.exe /c call script\\prioritychanger.bat");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JOptionPane.showMessageDialog(null, "Finished.", "Toaster Optimizer", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void buildPriorityChanger(ArrayList<String> processes) {
		StringBuilder out = new StringBuilder();
		for(int i = 0; i < processes.size(); i++) {
			out.append("wmic process where name=\""+processes.get(i)+"\" CALL setpriority \"idle\"\r\n");
		}

		PrintWriter writer = null;

		try {
			writer = new PrintWriter(workingDir + "\\script\\prioritychanger.bat", "UTF-8");
			writer.print(out);
			writer.println("exit");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		writer.close();


	}
}
