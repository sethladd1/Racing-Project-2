import java.io.File;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.*;
public class Shell
{
	//	private ArrayList<String> debugMessages = new ArrayList<String>();

	private ArrayList<Run> runs;
	private Run curRun;
	private boolean power = true;
	private int IND=0, PARIND=1;
	private String errorMessage;
	public Shell(String filePath)
	{
		Scanner in;
		String cmdLine = "";
		boolean exit = false;
		try{
			File file = new File(filePath);
			in =  new Scanner(file);
			while(in.hasNextLine()){
				cmdLine = in.nextLine();
				if(readCommand(cmdLine)){
					System.out.println("line" + cmdLine + "not read");
				}
			}
		}catch(FileNotFoundException e){
			System.out.println("File not found.");
		}

		while(exit==false)
		{
			System.out.print("CMD> ");
			in = new Scanner(System.in);
			cmdLine = in.nextLine();

			if(!readCommand(cmdLine))
				System.out.println("Command not read");
			if(cmdLine.equalsIgnoreCase("exit"))
				exit = true;
		}
	}
	public Shell(){
		Scanner in;
		String cmdLine = "";
		boolean exit = false;
		while(exit==false)
		{
			System.out.print("CMD> ");
			in = new Scanner(System.in);
			cmdLine = in.nextLine();

			if(!readCommand(cmdLine))
				System.out.println("Command not understood");
			if(cmdLine.equalsIgnoreCase("exit"))
				exit = true;
		}
	}
	/**
	 * @param line - line to be processed
	 * @return - whether or not the command was read or not
	 */
	public boolean readCommand(String line)
	{
		Scanner in = new Scanner(line);
		errorMessage = "";
		String commandToken = in.next();
		int chanNum, compNum;
		//all commands from file are preceded with timestamp.
		//I use regex to find out if string is timestamp,set time, and grab next token
		Pattern p = Pattern.compile("[0-9]+\\:[0-9]+\\.[0-9]+");
		Matcher matcher = p.matcher(commandToken);
		if(matcher.matches()){
			String s[] = matcher.group().split("\\:");
			curRun.setTime(0, Integer.parseInt(s[0]), Integer.parseInt(s[1].split("\\.")[0]), Integer.parseInt(s[1].split("\\.")[1]));;
			commandToken = in.next();
		}

		//		if(commandToken.equalsIgnoreCase("debug"))
		//		{
		//			System.out.println("---- Printing Debug Messages ----");
		//			for(String s : debugMessages)
		//				System.out.println(s);
		//			System.out.println("---------------------------------");
		//		}

		// Turn	system	on,	enter	quiescent	state
		else if(commandToken.equalsIgnoreCase("power"))
		{
			if(!power){
				runs = new ArrayList<Run>();
				curRun = new Run(IND, 1);
				runs.add(curRun);
			}
			power = !power;
			//			debugMessages.add(tur);

		}

		// Exit	the	simulator
		else if(commandToken.equalsIgnoreCase("exit"))
		{
			//			debugMessages.add("Simulator Exiting...");
			System.exit(0);
		}

		// Resets	the	System	to	initial	state
		else if(commandToken.equalsIgnoreCase("reset"))
		{
			//			debugMessages.add(sim.reset());
			runs = new ArrayList<Run>();
			curRun = new Run(IND, 1);
			runs.add(curRun);

		}

		// Set	the	current	time
		else if(commandToken.equalsIgnoreCase("time"))
		{
			String time = in.next();
			if(curRun.setTime(time))
				return true;
			else
				return false;

		}

		// Toggle	the	state	of	the	channel	<CHANNEL>
		else if(commandToken.equalsIgnoreCase("tog") || commandToken.equalsIgnoreCase("toggle"))
		{
			try{
				chanNum = in.nextInt();
				curRun.toggle(chanNum);
				//				debugMessages.add(time.getCurrentTimeStamp() + "<competitor " + competitors.get(nextStart) + "> TROG" + chanNum); 
			}
			catch(InputMismatchException e){
				return false;
			}
		}

		// CONN <sensor> <NUM>
		// Connect	a	type	of	sensor to	channel	<NUM>
		// <sensor>	=	{EYE,	GATE,	PAD}
		else if(commandToken.equalsIgnoreCase("conn"))
		{
			String device = in.next();
			int deviceNum = in.nextInt();
		}

		// Disconnect	a	sensor from channel	<NUM>
		else if(commandToken.equalsIgnoreCase("disc"))
		{
			try{
				chanNum = in.nextInt();
				if(curRun.getChannel(chanNum)){
					curRun.toggle(chanNum);
				}
			}catch(Exception e){
				errorMessage = "Missing or invalid argument";
				in.close();
				return false;
			}
		}

		// IND	|	PARIND	|	GRP	|	PARGRP
		else if(commandToken.equalsIgnoreCase("event"))
		{
			try{
			String type = in.next();
			if(type.equalsIgnoreCase("ind")){
				curRun.setType(IND);
			}
			else
				if(type.equalsIgnoreCase("parind"))
				{
					curRun.setType(PARIND);
				}
				else{ 
					in.close();
					errorMessage = "Invalid argument";
					return false;
				}
			}catch(NoSuchElementException e){
				in.close();
				errorMessage = "Missing argument";
				return false;
			}

		}

		// Create	a	new	Run (must	end	a	run	first)
		else if(commandToken.equalsIgnoreCase("newrun"))
		{
			if(curRun.running()){
				in.close();
				return false;
			}


		}

		// Done	with	a	Run
		else if(commandToken.equalsIgnoreCase("endrun"))
		{
			curRun.end();
		}

		// Print	the	run	on	stdout
		else if(commandToken.equalsIgnoreCase("print"))
		{
			try{
				print(Integer.parseInt(in.next()));
			}catch(Exception e){
				errorMessage = "Missing or invalid argument";
				in.close();
				return false;
			}
		}

		// Export	run	in	XML to	file	�RUN<RUN>�
		else if(commandToken.equalsIgnoreCase("export"))
		{
			try
			{
				//TODO 
				int runNum = in.nextInt();
				for(Run r : runs){
					if(r.getRunNum() == runNum){
						export(r);
						in.close();
						return true;
					}
				}
				errorMessage = "run "+ runNum+ " not found";
				return false;

			}
			catch(Exception e){
				errorMessage = "Missing or invalid argument";
				in.close();
				return false;
			}
		}

		// Set	<NUMBER>	as	the	next	competitor	to	start.
		else if(commandToken.equalsIgnoreCase("num"))
		{
			try{ 
				compNum = in.nextInt();
				curRun.addRacer(compNum);
			}
			catch(Exception e){
				errorMessage = "Missing or invalid argument";
				in.close();
				return false;
			}
		}

		// Clear	<NUMBER>	as	the	next	competitor
		else if(commandToken.equalsIgnoreCase("clr"))
		{
			try{ 
				compNum = in.nextInt();
				if(!curRun.removeRacer(compNum)){
					errorMessage = "racer "+ compNum +" not in this run";
					in.close();
					return false;
				}
			}
			catch(Exception e){
				errorMessage = "Missing or invalid argument";
				in.close();
				return false;
			}
		}

		// Exchange	next	two	competitors	to	finish in	IND
		else if(commandToken.equalsIgnoreCase("swap"))
		{
			if(!curRun.swap()){
				errorMessage = "less than 2 racers in the finish queue";
				in.close();
				return false;
			}
		}

		// The	next	competitor	to	finish	will	not	finish
		else if(commandToken.equalsIgnoreCase("dnf"))
		{
			if(!curRun.setDNF()){
				errorMessage = "no racers in the finish queue";
				in.close();
				return false;
			}
		}

		// Trigger	channel	<NUM>
		else if(commandToken.equalsIgnoreCase("trig"))
		{
			try{
				chanNum = in.nextInt();
				curRun.trigger(chanNum);
			}
			catch(InputMismatchException e){
				errorMessage = "invalid argument";
				in.close();
				return false;
			}
		}

		// Start	trigger	channel	1 (shorthand	for	TRIG	1)
		else if(commandToken.equalsIgnoreCase("start"))
		{
			if(!curRun.trigger(1)){
				errorMessage = "no racers in the start queue";
				in.close();
				return false;
			}

		}

		// Finish	trigger	channel	2 (shorthand	for	TRIG	2)
		else if(commandToken.equalsIgnoreCase("finish"))
		{
			if(!curRun.trigger(2)){
				errorMessage = "no racers waiting to finish";
				in.close();
				return false;
			}

		}
		else
		{
			in.close();
			errorMessage = "Command not recognized";
			return false;
		}

		in.close();
		return true;
	}

	private boolean export(Run run){
		Scanner stdin = new Scanner(System.in);
		System.out.print("Enter a file to export to: ");

		File file = new File(stdin.nextLine());
		char replace = 'n';
		while(file.exists() && Character.toLowerCase(replace) != 'y'){
			System.out.print("File already exists. \nOverwrite -> o\nRename -> r\n Cancel -> c\n");
			replace = stdin.next().charAt(0);
			if(Character.toLowerCase(replace) == 'c'){
				stdin.close();
				return true;
			}

		}
		stdin.close();
		Gson g = new Gson();
		JsonObject jso = new JsonObject();
		ArrayList<JsonObject> jsObjects =  new ArrayList<JsonObject>();
		ArrayList<Racer> racers =  run.getRacers();
		Time t = new Time();
		for(Racer r : racers){
			jso.addProperty("Number", r.getNumber());
			if(r.started())
				jso.addProperty("Start", t.convertToTimestamp(r.getStart()));
			else
				jso.addProperty("Start", "");
			if(r.finished()){
				jso.addProperty("Finish", t.convertToTimestamp(r.getFinish()));
				jso.addProperty("Run Time", t.convertToTimestamp(r.getRunTime()));
			}
			else{
				jso.addProperty("Finish", "");
				jso.addProperty("Run Time", "DNF");
			}
			jsObjects.add(jso);
		}
		String output = g.toJson(jsObjects);
		try{
		FileWriter writer = new FileWriter(file);
		writer.write(output);
		writer.close();
		return true;
		} catch(IOException e){
			errorMessage = "There was an error writing to file:\n"+e.getMessage();
			return false;
		}
		
	}
	/**
	 * Prints out results
	 * @return File of messages
	 */
	public static void print(int run)
	{
		Scanner in;

		ArrayList<Integer> startOcc = new ArrayList<Integer>();
		ArrayList<Integer> endOcc = new ArrayList<Integer>();

		for(int i = 0; i < eventCodes.size(); i++)
		{
			in = new Scanner(eventCodes.get(i));
			String keyWord = in.next();
			if(keyWord.equalsIgnoreCase("new"))
			{
				startOcc.add(i);
			}

			if(keyWord.equalsIgnoreCase("end"))
			{
				endOcc.add(i);
			}

			in.close();
		}

		if(startOcc.size() != endOcc.size())
		{
			throw new IllegalStateException("new != end occurances");
		}

		for(int k = startOcc.get(run) + 1; k < endOcc.get(run); k++)
		{
			System.out.println(eventCodes.get(k));
		}
	}

}
