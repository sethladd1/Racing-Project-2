import java.util.ArrayList;


public class Run {
	private ArrayList<Racer> racers;
	private Time time;
	private int type; //0=IND, 1=PARIND
	private boolean running;
	private boolean channels[];
	private int runNum;
	
	public Run(int type, int runNum){this.type=type;this.runNum=runNum;}//TODO?
	public boolean setType(int type){this.type=type;return true;}//TODO?
	public int getType(){return type;}//TODO?
	public int getRunNum(){return runNum;}//TODO?
	public boolean setTime (String time){
		return this.time.stringToTime(time);
	}
	public boolean setTime (int H, int M, int S, int mS){if(time != null){return false;}time=new Time(H,M,S,mS);return true;}//TODO?
	void toggle (int channel){channels[channel-1]=!channels[channel-1];}//TODO?
	boolean trigger (int channel){channels[channel-1]=true;channels[channel-1]=false;return true;}//TODO
	ArrayList<Racer> getRacers (){return racers;}//TODO?
	boolean addRacer (int number){racers.add(new Racer(number));return true;}//TODO?
	
	boolean running (){return running;}//TODO?
	void end (){}//TODO
}
