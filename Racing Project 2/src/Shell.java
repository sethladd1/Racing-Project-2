import java.io.File;



import java.io.FileNotFoundException;

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

		while(!exit)
		{
			System.out.print("CMD> ");
			in = new Scanner(System.in);
			cmdLine = in.nextLine();

			if(!readCommand(cmdLine))
				System.out.println(errorMessage);
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
				System.out.println(errorMessage);
			if(cmdLine.equalsIgnoreCase("exit"))
				exit = true;
		}
	}
	public Run getCurrentRun(){
		return curRun;
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
		int chanNum, runNum, compNum;
		//all commands from file are preceded with timestamp.
		//I use regex to find out if string is timestamp,set time, and grab next token
		Pattern p = Pattern.compile("[0-9]+\\:[0-9]+\\.[0-9]+");
		Matcher matcher = p.matcher(commandToken);
		if(matcher.matches()){
			String s[] = matcher.group().split("\\:");
			curRun.setTime(0, Integer.parseInt(s[0]), Integer.parseInt(s[1].split("\\.")[0]), 10*Integer.parseInt(s[1].split("\\.")[1]));;
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
		if(commandToken.equalsIgnoreCase("power"))
		{
			if(!power){
				runs = new ArrayList<Run>();
				curRun = new Run(IND, 1);
				runs.add(curRun);
			}
			power = !power;
			in.close();
			return true;

		}
		else if(power){
			// Exit	the	simulator
			if(commandToken.equalsIgnoreCase("exit"))
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
				try{
				String time = in.next();
				in.close();
				if(curRun.setTime(time))
					return true;
				else{
						errorMessage = "Invalid argument. Time format: <hour>:<min>:<sec>";
					return false;
				}
				}catch(NoSuchElementException e){
					errorMessage = "Missing argument";
					return false;
				}

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
					errorMessage = "Invalid or missing argument";
					in.close();
					return false;
				}
			}

			// CONN <sensor> <NUM>
			// Connect	a	type	of	sensor to	channel	<NUM>
			// <sensor>	=	{EYE,	GATE,	PAD}
			else if(commandToken.equalsIgnoreCase("conn"))
			{
//				TODO
				String device = in.next();
				int deviceNum = in.nextInt();
				if(!curRun.getChannel(deviceNum)){
					curRun.toggle(deviceNum);
				}
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
					runNum = in.nextInt();
					for(Run r : runs){
						if(r.getRunNum() == runNum){

						}
					}
				}catch(Exception e){
					errorMessage = "Missing or invalid argument";
					in.close();
					return false;
				}
			}

			// Export	run	in JSON to	file	�RUN<RUN>�
			else if(commandToken.equalsIgnoreCase("export"))
			{
				try
				{
					runNum = in.nextInt();
					for(Run r : runs){
						if(r.getRunNum() == runNum){
							in.close();
							File file = new File("Run"+runNum);
							errorMessage = r.export(file);
							if(!errorMessage.isEmpty()){
								return false;
							}

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
					if(curRun.running())
					{
						errorMessage = "Run has ended";
						in.close();
						return false;
					}
					if(!curRun.addRacer(compNum)){
						errorMessage = "Racer was already added";
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
					errorMessage = "no racers in the finish queue";
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
		} else{
			errorMessage = "Simulator off";
			in.close();
			return false;
		}
	}
}
