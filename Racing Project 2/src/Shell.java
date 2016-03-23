// TEST github update
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.*;
public class Shell
{
//	private ArrayList<String> debugMessages = new ArrayList<String>();

	private ArrayList<Run> runs;
	private Run curRun;
	private boolean power = true;
	private int IND=0, PARIND=1;
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
					if(readCommandLine(cmdLine)){
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

			if(!readCommandLine(cmdLine))
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

			if(!readCommandLine(cmdLine))
				System.out.println("Command not read");
			if(cmdLine.equalsIgnoreCase("exit"))
				exit = true;
		}
	}
	/**
	 * @param line - line to be processed
	 * @return - whether or not the command was read or not
	 */
	public boolean readCommandLine(String line)
	{
		Scanner in = new Scanner(line);

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
			curRun = new Run(IND);
			
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
			
		}

		// IND	|	PARIND	|	GRP	|	PARGRP
		else if(commandToken.equalsIgnoreCase("event"))
		{

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
			}catch(NumberFormatException e){
				return false;
			}
		}

		// Export	run	in	XML to	file	�RUN<RUN>�
		else if(commandToken.equalsIgnoreCase("export"))
		{
			try
            {
                  File file = new File("Run"data.xml");
                 
                  
                 
                  FileWriter writer = new FileWriter(file);
                  writer.write(formatRunAsXML(Integer.parseInt(in.next())));
                  writer.flush();
                  writer.close();
                 
                }
            catch (IOException e)
            {
                  e.printStackTrace();
            }
		}

		// Set	<NUMBER>	as	the	next	competitor	to	start.
		else if(commandToken.equalsIgnoreCase("num"))
		{
			try{ 
				compNum = in.nextInt();
				curRun.addRacer(compNum);
			}
			catch(InputMismatchException e){
				return false;

			}
		}

		// Clear	<NUMBER>	as	the	next	competitor
		else if(commandToken.equalsIgnoreCase("clr"))
		{
			try{ 
				compNum = in.nextInt();
				if(competitors.contains(compNum))
					competitors.remove(compNum);
			}
			catch(InputMismatchException e){
				return false;
			}
		}

		// Exchange	next	two	competitors	to	finish in	IND
		else if(commandToken.equalsIgnoreCase("swap"))
		{
			if(curRun.swap()){
				return false;
			}
		}

		// The	next	competitor	to	finish	will	not	finish
		else if(commandToken.equalsIgnoreCase("dnf"))
		{
			chan.trigger(2);
		}

		// Trigger	channel	<NUM>
		else if(commandToken.equalsIgnoreCase("trig"))
		{
			try{
				chanNum = in.nextInt();

				if(chanNum == 1){
					eventCodes.add(time.getCurrentTimeStamp() + "<competitor " + competitors.get(nextStart++) + "> TRIG" + chanNum); 
				}
				else if(chanNum ==2){
					eventCodes.add(time.getCurrentTimeStamp() + "<competitor " + competitors.get(nextStart++) + "> TRIG" + chanNum);
				}
				else 
					return false;
				chan.trigger(chanNum);
			}
			catch(InputMismatchException e){
				return false;
			}
		}

		// Start	trigger	channel	1 (shorthand	for	TRIG	1)
		else if(commandToken.equalsIgnoreCase("start"))
		{
			if(nextStart>=competitors.size() || nextStart<0){
				// TODO: if a valid command is entered that is not yet acceptable, just return false?  
				return false;
			}
			chan.trigger(1);
			eventCodes.add(time.getCurrentTimeStamp() + " competitor" +competitors.get(nextStart++) +" START");

		}

		// Finish	trigger	channel	2 (shorthand	for	TRIG	2)
		else if(commandToken.equalsIgnoreCase("finish"))
		{
			if(nextFinish>=competitors.size() || nextFinish<0){
				//not yet acceptable
				return false;
			}
			chan.trigger(2);
			eventCodes.add(time.getCurrentTimeStamp() + " competitor" +competitors.get(nextFinish++) +" FINISH");
		}
		else
		{
			return false;
		}

		in.close();
		return true;
	}

	/*
	 * Input -> File file = new File("test.txt");
	 */

	/**
	 * readCommandTextFile reads a file f (text document) and processes the command
	 * @param f
	 */
	public void readCommandTextFile(File f)
	{
		try
		{
			Scanner in = new Scanner(f);

			while(in.hasNextLine())
			{
				readCommandLine(in.nextLine());
			}

			in.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}

	private boolean export(Run run){}
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
