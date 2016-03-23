import java.util.ArrayList;


public class Run {
	private ArrayList<Racer> racers;
	private Time time;
	private int type; //0=IND, 1=PARIND
	private boolean running;
	private boolean channels[];
	private int runNum;
	
	public Run(int type, int runNum){}
	public boolean setType(int type){}
	public int getType(){}
	public int getRunNum(){}
	public boolean setTime (String time){
		return this.time.stringToTime(time);
	}
	public boolean setTime (int H, int M, int S, int mS){}
	void toggle (int channel){}
	boolean trigger (int channel){}
	ArrayList<Racer> getRacers (){}
	boolean addRacer (int number){}
	
	boolean running (){}
	void end (){}
}
