import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class Run {
	private ArrayList<Racer> racers, startQueue, finishQueue, startQueue2, finishQueue2;
	private ArrayList<Long> grpRanks;
	private Time time;
	private int type; //0=IND, 1=PARIND
	private final int IND=0,PARIND=1, GRP = 2;
	private int chan;
	private boolean running;
	private boolean channels[];
	private String sensors[];
	private int runNum;
	private boolean started;
	private Racer lastFinish1, lastFinish2;
	public Run(int type, int runNum){
		this.type=type;
		this.runNum=runNum;
		racers = new ArrayList<Racer>();
		startQueue = new ArrayList<Racer>();
		finishQueue = new ArrayList<Racer>();
		startQueue2 = new ArrayList<Racer>();
		finishQueue2 = new ArrayList<Racer>();
		grpRanks = new ArrayList<Long>();
		chan = 0;
		running = true;
		started = false;
		time = new Time();
		channels = new boolean[8];
		sensors = new String[8];
	}
	public boolean setType(int type){
		if(running && (type == IND || type == PARIND || type == GRP)){
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
		boolean timeSet = false;
		if(running){
			timeSet = this.time.stringToTime(time);
			if(timeSet)
				started = true;
		}
		return timeSet;
	}

	public boolean setTime (int H, int M, int S, int mS)
	{
		if(running){
			time.setCurrent(H, M, S, mS);
			started = true;
			return true;
		}
		return false;
	}
	public String getElapsedTime(){
		return time.getCurrentTimeStamp();
	}
	public void toggle (int channel){
		if(channel<=channels.length && channel>0)
			channels[channel-1]=!channels[channel-1];
	}
	public void setChannelState(int channel, boolean state){
		if(channel<=channels.length && channel>0)
			channels[channel-1]=state;
	}
	public boolean connect(String sensor, int channel){
		if(channel<=channels.length && channel>0){
			sensors[channel-1] = sensor;
			return true;
		}
		else{
			return false;
		}

	}
	public String getSensor(int channel){
		if(channel<=channels.length && channel>0){
			return sensors[channel-1];
		}
		else
			return "";
	}
	public boolean trigger (int channel){
		switch(type){
		case IND:
			if((channel == 1&&channels[0])){
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
			else if((channel == 2&&channels[1])){
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
		case PARIND:
			if((channel == 1&&channels[0])){
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
			else if((channel == 3&&channels[2])){
				if(startQueue2.isEmpty()){
					return false;
				}
				if(!started)
					time.start();
				started = true;

				startQueue2.get(0).setStart(time.elapsed());
				finishQueue2.add(startQueue2.remove(0));
				return true;
			}
			else if((channel == 2&&channels[1])){
				if(finishQueue.isEmpty()){
					return false;
				}
				if(!started)
					time.start();
				started =true;
				finishQueue.get(0).setFinish(time.elapsed());
				lastFinish1 = finishQueue.remove(0);
				return true;
			}
			else if (channel == 4&&channels[3]){
				if(finishQueue2.isEmpty()){
					return false;
				}
				if(!started)
					time.start();
				started =true;
				finishQueue2.get(0).setFinish(time.elapsed());
				lastFinish2 = finishQueue2.remove(0);
				return true;
			}
			return false;
		case GRP:
			if(channel == 1&& channels[0]){
				if(started) 
					return false;
				else{
					time.start();
					started = true;
					return true;
				}
			}
			if(channel == 2 && channels[1]){
				if(started){
					grpRanks.add(time.elapsed());
					if(grpRanks.size()<=racers.size()){
						racers.get(grpRanks.size()-1).setFinish(grpRanks.get(grpRanks.size()-1));
					}
					return true;
				}
				else
					return false;
			}
			return false;
		default:
			return false;
		}
	}
	public ArrayList<Long> getGroupRanks(){
		return grpRanks;
	}
	public ArrayList<Racer> getRacers (){
		return racers;
	}
	/**
	 * 
	 * @return in IND races:the finish queue. in PARIND races: the finish queue associated with channel 1&2
	 */
	public ArrayList<Racer> getFinishQueue1 (){
		return finishQueue;
	}
	/**
	 * this is only useful in PARIND races
	 * @return the finish queue associated with channel 3&4
	 */
	public ArrayList<Racer> getFinishQueue2 (){
		return finishQueue2;
	}
	/**
	 * 
	 * @return in IND races:the start queue. in PARIND races: the start queue associated with channel 1&2
	 */
	public ArrayList<Racer> getStartQueue1 (){
		return startQueue;
	}
	/**
	 * this is only useful in PARIND races
	 * @return the start queue associated with channels 3&4 
	 */
	public ArrayList<Racer> getStartQueue2 (){
		return startQueue2;
	}
	/**
	 * 
	 * @return the last racer to finish on chan1
	 */
	public Racer getLastFinish1(){
		return lastFinish1;
	}
	
	public Racer getLastFinish2(){
		return lastFinish2;
	}
	public boolean addRacer (int number){
		if(running && type != GRP){
			for(Racer r : racers){
				if(r.getNumber() == number){
					return false;
				}
			}
			Racer r = new Racer(number);
			racers.add(r);
			if(chan == 0){
				startQueue.add(r);
				chan = 1;
			}
			else if(chan == 1){
				startQueue2.add(r);
				chan = 0;
			}
			return true;
		}
		if(type==GRP){
			for(Racer r : racers){
				if(r.getNumber() == number){
					return false;
				}
			}
			Racer r = new Racer(number);
			if(grpRanks.size()>racers.size()){
				long t = grpRanks.get(racers.size());
				r.setStart(0);
				r.setFinish(t);
				racers.add(r);
			}
			else{
				r.setStart(0);
				racers.add(r);				
			}
			return true;
		}
		return false;
	}

	public boolean running (){
		return running;
	}
	public boolean setDNF(){

		if(type == IND && finishQueue.size()>0){
			finishQueue.get(0).setDNF(true);
			finishQueue.remove(0);
			return true;
		}
		return false;
	}

	// chan-1 is the index in channels because the channels are numbered 1-4
	public boolean getChannel(int chan){
		if(chan<= channels.length && chan>0){
			return channels[chan-1];
		}
		return false;
	}
	public boolean removeRacer(int racerNum){

		for(Racer r : racers){
			if(r.getNumber() == racerNum){
				if(type==GRP){
					int index = racers.indexOf(r);
					racers.remove(r);
					for(;index<racers.size()&&index<grpRanks.size();++index){
						racers.get(index).setFinish(grpRanks.get(index));
					}
				}
				racers.remove(r);
				if(!startQueue.remove(r)){
					if(!finishQueue.remove(r)){
						if(!startQueue2.remove(r)){
							finishQueue2.remove(r);
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	public boolean swap(){
		if(finishQueue.size()<2 || type != IND)
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
		//		TODO update this so it exports times in grpRanks if type is GRP, associated with rank or racer nums if they exist
		Gson g = new Gson();
		JsonObject jso = new JsonObject();
		ArrayList<JsonObject> jsObjects =  new ArrayList<JsonObject>();
		String errorMessage = "";
		for(Racer r : racers){
			jso = new JsonObject();
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

	public String print()
	{
		String str = "\tRUN " + runNum+"\n";
		if(type==GRP){
			for(int i = 0;i<grpRanks.size();++i){
				if(i<racers.size()){
					Racer r = racers.get(1);
					str+=r.getNumber()+": " + time.convertToTimestamp(racers.get(i).getFinish())+"\n";
				}
				else{
					String rank = String.valueOf(i);
					while(rank.length()<5){
						rank="0"+rank;
					}
					str+=rank+": " +time.convertToTimestamp(grpRanks.get(i))+"\n";
					
				}
			}
		}
		else{
			for(Racer r : racers){
				str+="**************************\n";
				str+="\tRacer: " +r.getNumber()+"\n";
				str+="**************************\n";

				if(r.started())
					str+="Start: " +time.convertToTimestamp(r.getStart())+"\n";
				else
					str+="Start: Did not start\n";
				if(r.finished()){
					str+="Finish: " +time.convertToTimestamp(r.getFinish())+"\n";
					str+="Run Time: " +time.convertToTimestamp(r.getRunTime())+"\n";
				}
				else{
					str+="Finish: Did not finish\nRun Time: Did not finish\n";
				}
			}
		}
		System.out.print(str);
		return str;
	}
}
