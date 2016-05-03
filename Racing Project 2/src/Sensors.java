
public class Sensors {
	static Sensor array[];
	public Sensors(int arraySize, boolean startThreads){
		array = new Sensor[arraySize];
		for(int i=0;i<arraySize;++i){
			array[i]= new Sensor(i+1);
			if(startThreads){
				array[i].start();
			}
		}
	}
	public boolean trigger(int sensor){
		if(sensor>array.length||sensor<0){
			return false;
		}
		else{
			array[sensor-1].triggered=true;
			return true;
		}
	}

	public class Sensor extends Thread{
		int num;
		public  boolean triggered = false;
		String type;
		private boolean enabled = false;
		public Sensor(int channel){
			num = channel;
			type="";
		}
		public void enable(){
			enabled=true;
		}
		public boolean isEnabled(){
			return enabled;
		}
		public void disable(){
			enabled = false;
		}
		public boolean isEnabledandConnected(){
			return enabled&&!type.isEmpty();
		}
		public void disconnect(){
			this.type = "";
		}
		public void connect(String type){
			this.type = type;
		}
		public void toggle(){
			enabled = !enabled;
		}
		public void run(){
			while(true){
				if(triggered){
					if(enabled){
						Shell.trigger(1);
						triggered = false;
					}
					else{
						triggered = false;
					}
				}
				try{
					sleep(50);
				}catch(InterruptedException e){

				}
			}
		}
	}
}
