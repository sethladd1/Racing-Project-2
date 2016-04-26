import java.io.File;



import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.*;
public class Shell
{
	private ArrayList<Run> runs;
	private Run curRun;
	private boolean power;
	private int IND=0, PARIND=1, GRP=2;
	private String errorMessage;
	private static GUI ui;
	public Shell(String filePath, boolean noGUI)
	{
		curRun = new Run(0,1);
		
		
		
		power = true;
		runs = new ArrayList<Run>();
		runs.add(curRun);
		if(!noGUI){
			ui = new GUI(this);
			ui.setVisible(true);
		}
		readCommandsFromFile(filePath);
	}
	public Shell(boolean noGUI){
		
		curRun = new Run(0,1);
		
		power = true;
		if(!noGUI){
			ui = new GUI(this);
			ui.setVisible(true);
		}
		runs = new ArrayList<Run>();
		runs.add(curRun);
	}
	public void commandPromptLoop(){
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
	public void readCommandsFromFile(String filePath){
		Scanner in;
		String cmdLine;
		try{
			File file = new File(filePath);
			in =  new Scanner(file);
			while(in.hasNextLine()){
				cmdLine = in.nextLine();
				if(!readCommand(cmdLine)){
					System.out.println("line: " + cmdLine + " not read. Error message: " + errorMessage);
				}
			}
			in.close();
		}catch(FileNotFoundException e){
			System.out.println("File not found.");
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
		int chanNum, runNum, compNum;
		//all commands from file are preceded with timestamp.
		//I use regex to find out if string is timestamp,set time, and grab next token
		Pattern p = Pattern.compile("[0-9]+\\:[0-9]+\\:[0-9]+\\.{0,1}[0-9]*");;
		Matcher matcher = p.matcher(commandToken);
		if(matcher.matches()){
			if(curRun.running())
				curRun.setTime(matcher.group());
			commandToken = in.next();
		}
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
		else if(commandToken.equalsIgnoreCase("on")){
			if(!power){
				runs = new ArrayList<Run>();
				curRun = new Run(IND, 1);
				runs.add(curRun);
			}
			power = true;
			in.close();
			return true;
		}
		else if(commandToken.equalsIgnoreCase("off")){
			power = false;
			in.close();
			return true;
		}
		// Exit	the	simulator
		else if(commandToken.equalsIgnoreCase("exit"))
		{
			System.exit(0);
			in.close();
			return true;
		}
		else if(power){



			// Resets	the	System	to	initial	state
			if(commandToken.equalsIgnoreCase("reset"))
			{

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
					if(ui != null)
						ui.syncChanIcons();

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
				String device = in.next();
				chanNum = in.nextInt();
				curRun.connect(device, chanNum);
			}

			// Disconnect	a	sensor from channel	<NUM>
			else if(commandToken.equalsIgnoreCase("disc"))
			{
				try{
					chanNum = in.nextInt();
					curRun.connect("", chanNum);
				}catch(Exception e){
					errorMessage = "Missing or invalid argument";
					in.close();
					return false;
				}
			}

			// IND	|	PARIND	|	GRP	|	PARGRP
			else if(commandToken.equalsIgnoreCase("event"))
			{
				if(!curRun.running()){
					in.close();
					errorMessage = "The current run has ended";
					return false;
				}
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
						else
							if(type.equalsIgnoreCase("grp"))
							{
								curRun.setType(GRP);
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
					errorMessage = "must end current run before starting a new run";
					return false;
				}
				else{
					Run newRun = new Run(0, curRun.getRunNum()+1);
					for(int i=1;i<8;++i){
						newRun.connect(curRun.getSensor(i), i);
						newRun.setChannelState(i, curRun.getChannel(i));
					}
					curRun = newRun;
					runs.add(curRun);
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
							if(ui!=null)
								ui.print(r.print());
							else
								r.print();
							in.close();
							return true;
						}
					}
				}catch(Exception e){
					curRun.print();
					System.out.print("\n\n\n");
					in.close();
					return true;
				}
			}

			// Export	run	in JSON to	file	�RUN<RUN>�
			else if(commandToken.equalsIgnoreCase("export"))
			{
				try
				{
					runNum = in.nextInt();
					in.close();
					for(Run r : runs){
						if(r.getRunNum() == runNum){
							File file = new File("RUN"+runNum);
							errorMessage = r.export(file);
							if(!errorMessage.isEmpty()){
								return false;
							}
							System.out.println("exported run "+runNum+" to file ./RUN"+runNum);
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
					if(!curRun.running())
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
					if(!(curRun.getSensor(chanNum)==null)){
						curRun.trigger(chanNum);
					}
					else{
						if(chanNum>0 && chanNum<8)
							errorMessage = "no sensor attached to channel " + chanNum;
						else
							errorMessage = "invalid argument: no channel "+chanNum;
						in.close();
						return false;
					}

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
	public boolean getPower(){
		return power;
	}
	public String getErrorMessage(){
		return errorMessage;
	}
	public Run getCurrentRun(){
		return curRun;
	}
	public ArrayList<Run> getRuns(){
		return runs;
	}

}