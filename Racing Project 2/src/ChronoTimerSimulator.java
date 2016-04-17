
public class ChronoTimerSimulator {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Shell shell;
		boolean noGUI = false;
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
