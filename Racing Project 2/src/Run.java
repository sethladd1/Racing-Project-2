import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class Run {
	private ArrayList<Racer> racers, startQueue, finishQueue;
	private Time time;
	private int type; //0=IND, 1=PARIND
	private boolean running;
	private boolean channels[];
	private int runNum;
	private boolean started;

	public Run(int type, int runNum){
		this.type=type;
		this.runNum=runNum;
		racers = new ArrayList<Racer>();
		startQueue = new ArrayList<Racer>();
		finishQueue = new ArrayList<Racer>();
		running = true;
		started = false;
		time = new Time();
		channels = new boolean[4];
	}
	public boolean setType(int type){
		if(running && (type == 0 || type == 1)){
			this.type=type;
			return true;
		}
		return false;
	}
	public int getType(){
		return type;
	}
	public int getRunNum(){
		return runNum;
	}
	public boolean setTime (String time){
		return this.time.stringToTime(time);
	}

	public boolean setTime (int H, int M, int S, int mS)
	{
		if(running){
			time.setCurrent(H, M, S, mS);
			return true;
		}
		return false;
	}
	public void toggle (int channel){
		if(channel<=channels.length && channel>0)
			channels[channel-1]=!channels[channel-1];
	}
	public boolean trigger (int channel){
		if((channel == 1&&channels[1]) || (channel == 3&&channels[3])){
			if(startQueue.isEmpty()){
				return false;
			}
			if(!started)
				time.start();
			started = true;
			
			startQueue.get(0).setStart(time.elapsed());
			finishQueue.add(startQueue.remove(0));
			return true;
		}
		else if((channel == 2&&channels[2]) || (channel == 4&&channels[4])){
			if(finishQueue.isEmpty()){
				return false;
			}
			if(!started)
				time.start();
			started =true;
			finishQueue.get(0).setFinish(time.elapsed());
			finishQueue.remove(0);
			return true;
		}
		return false;
	}
	public ArrayList<Racer> getRacers (){
		return racers;
	}
	public boolean addRacer (int number){
		if(running){
			for(Racer r : racers){
				if(r.getNumber() == number){
					return false;
				}
			}
			Racer r = new Racer(number);
			racers.add(r);
			startQueue.add(r);
			return true;
		}
		return false;
	}

	public boolean running (){
		return running;
	}
	public boolean setDNF(){
		if(finishQueue.size()>0){
			finishQueue.get(0).setDNF(true);
			finishQueue.remove(0);
			return true;
		}
		return false;
	}

	// chan-1 is the index in channel because the channels are numbered 1-4
	public boolean getChannel(int chan){
		if(chan<= channels.length && chan>0){
			return channels[chan-1];
		}
		return false;
	}
	public boolean removeRacer(int racerNum){

		for(Racer r : racers){
			if(r.getNumber() == racerNum){
				racers.remove(r);
				startQueue.remove(r);
				finishQueue.remove(r);
				return true;
			}
		}
		return false;
	}

	public boolean swap(){
		if(finishQueue.size()<2)
			return false;
		Racer hold = finishQueue.get(0);
		finishQueue.set(0, finishQueue.get(1));
		finishQueue.set(1, hold);
		return true;
	}

	public void end (){
		running = false;
	}
	public String export(File file){

		Gson g = new Gson();
		JsonObject jso = new JsonObject();
		ArrayList<JsonObject> jsObjects =  new ArrayList<JsonObject>();
		String errorMessage = "";
		for(Racer r : racers){
			jso.addProperty("Number", r.getNumber());
			if(r.started())
				jso.addProperty("Start", time.convertToTimestamp(r.getStart()));
			else
				jso.addProperty("Start", "Did not start");
			if(r.finished()){
				jso.addProperty("Finish", time.convertToTimestamp(r.getFinish()));
				jso.addProperty("Run Time", time.convertToTimestamp(r.getRunTime()));
			}
			else{
				jso.addProperty("Finish", "Did not Finish");
				jso.addProperty("Run Time", "Did not Finish");
			}
			jsObjects.add(jso);
		}
		String output = g.toJson(jsObjects);
		try{
			FileWriter writer = new FileWriter(file);
			writer.write(output);
			writer.close();
		} catch(IOException e){
			errorMessage = "There was an error writing to file:\n"+e.getMessage();	
		}
		return errorMessage;

	}

	public void print(Run run)
	{
		System.out.println("\tRUN " + runNum);;
		for(Racer r : racers){
			System.out.println("**************************");
			System.out.println("\tRacer: " +r.getNumber());
			System.out.println("**************************");
			if(r.started())
				System.out.println("Start: " +time.convertToTimestamp(r.getStart()));
			else
				System.out.println("Start: Did not start");
			if(r.finished()){
				System.out.println("Finish: " +time.convertToTimestamp(r.getFinish()));
				System.out.println("Run Time: " +time.convertToTimestamp(r.getRunTime()));
			}
			else{
				System.out.println("Finish: Did not finish");
				System.out.println("Run Time: Did not finish");
			}
		}
	}
}
