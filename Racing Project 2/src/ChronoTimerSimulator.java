
public class ChronoTimerSimulator {
	private final static String SERVER = "http://localhost:8000";
	public static void main(String[] args) {
		Shell shell;
		boolean noGUI = false;
		new HTTPHandler(SERVER);
		for(int i=0;i<args.length;++i){
			if(args[i].equalsIgnoreCase("-ng")){
				noGUI=true;
			}
		};
		if(args.length>0){
			shell = new Shell(args[0],noGUI);
		}
		else{
			shell = new Shell(noGUI);
		}
		
		
//		shell.commandPromptLoop();
	}
}