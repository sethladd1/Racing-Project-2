import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class Run {
	private ArrayList<Racer> racers, startQueue, finishQueue, startQueue2, finishQueue2;
	private ArrayList<Long> grpRanks;
	private Time time;
	private int type; //0=IND, 1=PARIND
	private final int IND=0,PARIND=1, GRP = 2, PARGRP=3;
	private int chan;
	private boolean running;
	private int runNum;
	private boolean started;
	private Racer lastFinish1, lastFinish2;
	private String runTime;
	/**
	 * 
	 * @param type - IND=0,PARIND=1,GRP=2,PARGRP=3
	 * @param runNum 
	 */
	public Run(int type, int runNum){
		if(type<IND || type>PARGRP)
			type=0;
		else
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
	}
	public boolean setType(int type){
		if(running && (type == IND || type == PARIND || type == GRP||type==PARGRP)){
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
		if(running)
			return time.getCurrentTimeStamp();
		else
			return runTime;
	}
	public String getRunningTime(Racer r){
		if(r.finished()){
			return Time.convertToTimestamp(r.getRunTime());
		}
		else if(r.started()){
			return Time.convertToTimestamp(time.elapsed()-r.getStart());
		}
		else{
			return Time.convertToTimestamp(0);
		}
	}

	public boolean start(int lane){
		if(type==PARIND){
			if(lane==1){
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
			else if(lane==2){
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
			return false;
		}
		else
			return start();
	}
	public boolean start(){
		if(type==IND || type==PARIND){
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
		if(type==GRP || type ==PARGRP){
			if(started) 
				return false;
			else{
				time.start();
				for(Racer r : racers){
					r.setStart(0);
				}
				started = true;
				return true;
			}
		}
		return false;
	}
	public boolean finish(){
		if(type==IND){
			if(finishQueue.isEmpty()){
				return false;
			}
			if(!started)
				time.start();
			started =true;
			finishQueue.get(0).setFinish(time.elapsed());
			lastFinish1=finishQueue.remove(0);
			return true;
		}
		if(type==GRP){
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

	}
	public boolean hasStarted(){
		return started;
	}
	public boolean finish(int lane){ 
		if(!running) 
			return false;
		if(type==PARIND){
			if(lane==1){
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
			if(lane==2){
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
		}
		else if(type==PARGRP){
			if(lane>0&&lane<=racers.size()){
				if(!racers.get(lane-1).finished()){
					racers.get(lane-1).setFinish(time.elapsed());
					lastFinish1=racers.get(lane-1);
					return true;
				}
			}
			else{
				return false;
			}
		}
		return finish();
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
		if(!running){
			return false;
		}
		for(Racer r : racers){
			if(r.getNumber() == number){
				return false;
			}
		}
		if(type != GRP){
			
			Racer r = new Racer(number);
			racers.add(r);
			if(type==PARIND){
				if(chan == 0){
					startQueue.add(r);
					chan = 1;
				}
				else if(chan == 1){
					startQueue2.add(r);
					chan = 0;
				}
			}
			else{
				startQueue.add(r);
			}
			return true;
		}
		else if(type==GRP){
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
		else if(type==PARGRP){
			Racer r = new Racer(number);
			r.setStart(0);
			racers.add(r);
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
		runTime = getElapsedTime();
		for(Racer r: racers){
			if(!r.finished()){
				r.setDNF(true);
			}
		}
		running = false;
		HTTPHandler.sendData(this);

	}
	public String export(File file){
		//		TODO update this so it exports times in grpRanks if type is GRP, associated with rank or racer nums if they exist
		Gson g = new Gson();
		JsonObject jso;
		ArrayList<JsonObject> jsObjects =  new ArrayList<JsonObject>();
		String errorMessage = "";
		if(type==GRP){
			for(int i=0;i<grpRanks.size();++i){
				jso = new JsonObject();
				
				if(i<racers.size()){
					jso.addProperty(String.valueOf(racers.get(i).getNumber()), Time.convertToTimestamp(grpRanks.get(i)));
				}
				else{
					String rank = String.valueOf(i+1);
					while(rank.length()<5){
						rank="0"+rank;
					}
					jso.addProperty(rank, Time.convertToTimestamp(grpRanks.get(i)));
				}
				jsObjects.add(jso);
			}
		}
		else{
			for(Racer r : racers){
				String val="";

				if(r.started()&&r.finished())
					val=getRunningTime(r);
				else{ 
					if(r.started() && !r.DNF()){
						val=getRunningTime(r) +" R";
					}
					if(r.DNF()){
						val="DNF";
					}
				}
				jso = new JsonObject();
				jso.addProperty(String.valueOf(r.getNumber()), val);
				jsObjects.add(jso);
			}
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
		String str = "RUN " + runNum+"\n";
		if(type==GRP){
			for(int i = 0;i<grpRanks.size();++i){
				if(i<racers.size()){
					Racer r = racers.get(i);
					str+=r.getNumber()+": " + getRunningTime(r)+"\n";
				}
				else{
					String rank = String.valueOf(i);
					while(rank.length()<5){
						rank="0"+rank;
					}
					str+=rank+": " +Time.convertToTimestamp(grpRanks.get(i))+"\n";

				}
			}
		}
		else{
			for(Racer r : racers){
				str+=r.getNumber()+": ";
				if(r.started()&&r.finished())
					str+=getRunningTime(r);
				else{ 
					if(r.started() && !r.DNF()){
						str+=getRunningTime(r) +" R";
					}
					if(r.DNF()){
						str+="DNF";
					}
				}
				str+="\n";
			}
		}
		System.out.print(str);
		return str;
	}
}