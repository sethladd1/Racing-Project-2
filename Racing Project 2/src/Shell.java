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
	private static Time time = new Time();
	private static Simulator sim = new Simulator();
	private static ArrayList<String> debugMessages = new ArrayList<String>();
	private static ArrayList<String> eventCodes = new ArrayList<String>();
	private static Channel chan = new Channel();

	private static int run;
	private static boolean runEnded = true;
	private static boolean runStarted = true;

	//nextStart and nextFinish are the index of the next racer in competitors 
	private static ArrayList<Integer> competitors = new ArrayList<Integer>();
	private static int nextStart, nextFinish;

	public static void main(String[] args)
	{
		Scanner in;
		String cmdLine = "";
		boolean exit = false;
		if(args.length>1){
			try{
				File file = new File(args[1]);
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
	/**
	 * @param line - line to be processed
	 * @return - whether or not the command was read or not
	 */
	public static boolean readCommandLine(String line)
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
			time.setCurrent(0, Integer.parseInt(s[0]), Integer.parseInt(s[1].split("\\.")[0]), Integer.parseInt(s[1].split("\\.")[1]));;
			commandToken = in.next();
		}

		if(commandToken.equalsIgnoreCase("debug"))
		{
			System.out.println("---- Printing Debug Messages ----");
			for(String s : debugMessages)
				System.out.println(s);
			System.out.println("---------------------------------");
		}

		// Turn	system	on,	enter	quiescent	state
		else if(commandToken.equalsIgnoreCase("on"))
		{
			debugMessages.add(sim.power(true));
		}

		// Turn	system	off (but	stay	in	simulator
		else if(commandToken.equalsIgnoreCase("off"))
		{
			debugMessages.add(sim.power(false));
		}

		// (if	off)	Turn	system	on,	enter	quiescent	state
		// (if	on)	Turn	system	off (but	stay	in	simulator
		else if(commandToken.equalsIgnoreCase("power"))
		{
			debugMessages.add(sim.powerTog());
		}

		// Exit	the	simulator
		else if(commandToken.equalsIgnoreCase("exit"))
		{
			debugMessages.add("Simulator Exiting...");
			sim.power(false);
			System.exit(0);
		}

		// Resets	the	System	to	initial	state
		else if(commandToken.equalsIgnoreCase("reset"))
		{
			debugMessages.add(sim.reset());
		}

		// Set	the	current	time
		else if(commandToken.equalsIgnoreCase("time"))
		{
			String timeTok = in.next();

			int h = Integer.parseInt(timeTok.substring(0,2));
			int m = Integer.parseInt(timeTok.substring(3,5));
			int s = Integer.parseInt(timeTok.substring(6,8));
			int ms = Integer.parseInt(timeTok.substring(9));

			time = new Time(h,m,s,ms);
			debugMessages.add("Time changed to: <" + h + ":" + m + ":" + s + "." + ms + ">");
		}

		// Toggle	the	state	of	the	channel	<CHANNEL>
		else if(commandToken.equalsIgnoreCase("tog") || commandToken.equalsIgnoreCase("toggle"))
		{
			try{
				chanNum = in.nextInt();
				chan.toggle(chanNum);
				debugMessages.add(time.getCurrentTimeStamp() + "<competitor " + competitors.get(nextStart) + "> TROG" + chanNum); 
			}
			catch(InputMismatchException e){

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
			if(!runEnded){
				in.close();
				return false;
			}
			runEnded = false;
			eventCodes.add("new run " + run );

		}

		// Done	with	a	Run
		else if(commandToken.equalsIgnoreCase("endrun"))
		{
			runEnded = true;
			eventCodes.add("end run " + run );
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
                  File file = new File("exportdata.xml");
                 
                  if (file.createNewFile())
                  {
                      debugMessages.add("File 'exportdata.xml' is created!");
                  }
                  else
                  {
                      debugMessages.add("File 'exportdata.xml' already exists.");
                  }
                 
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
				if(!competitors.contains(compNum))
					competitors.add(compNum);
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
			compNum =  competitors.get(nextStart);
			competitors.get(nextStart);
			competitors.set(nextStart, compNum);
		}

		// The	next	competitor	to	finish	will	not	finish
		else if(commandToken.equalsIgnoreCase("dnf"))
		{
			chan.trigger(2);
			eventCodes.add(time.getCurrentTimeStamp() + "<competitor"+competitors.get(nextFinish++) +"> DNF" );
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
	public static void readCommandTextFile(File f)
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

	// We are unclear on how this should look. Our version should be in this format: 
	// <run><competitorNum><timestamp1>start</timestamp1><timestamp2>finish</timestamp2><EventTime>time</EventTime></CompNum>...</run>
	private static String formatRunAsXML(int runNum){
		ArrayList<String> compStrings = new ArrayList<String>();
		ArrayList<String> compXML = new ArrayList<String>();
		Pattern p = Pattern.compile("[0-9]+\\:[0-9]+\\.[0-9]+");
		Pattern p2;
		Matcher matcher;
		String xString;
		Scanner in;
		String word;
		String time = "";
		String xml="";
		int compNum = 0; 
		int i = 0;
		for(int c : competitors){
			compStrings.add("competitor " + c);
			compXML.add("<"+c+">");
		}
		String s = eventCodes.get(i);
		while(!s.equals("new run "+ runNum) && i<eventCodes.size()){
			s = eventCodes.get(i++);
			if(s.equals("new run "+ runNum)){
				xml = "<Run" + runNum + ">";
			}
		}
		if(s.equals("new run "+ runNum)){
			for( ; i<eventCodes.size() && eventCodes.get(i).equals("end run " + runNum); ++i){


				in = new Scanner(s);
				word = in.next();
				matcher = p.matcher(word);
				if(matcher.matches()){
					time = word;
					word = in.next();

				}
				for(int j = 0; j<compStrings.size(); ++j){
					if(eventCodes.get(i).indexOf(compStrings.get(j))>-1){
						if(compStrings.get(j).indexOf("START")>-1){
							xString = compXML.get(j);

							xString += "<" + time +">" + "start" + "</" + time +">";
							compXML.set(j, xString);

						}
						else if(compStrings.get(j).indexOf("DNF")>-1){
							xString = compXML.get(j);

							xString += "<" + time +">" + "dnf" + "</" + time +"></"+competitors.get(j) + ">";
							compXML.set(j, xString);


						}
						else if(compStrings.get(j).indexOf("FINISH")>-1){
							xString = compXML.get(j);

							xString += "<" + time +">" + "finish" + "</" + time +"></"+competitors.get(j) + ">";
							compXML.set(j, xString);
						}

					}
				}
				in.close();
			}
			for(String x : compXML){

				xml += x;
			}
			
			return xml;
		}
		return "";
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
