
public class ChronoTimerSimulator {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Shell shell;
		if(args.length>0){
			shell = new Shell(args[0]);
		}
		else{
			shell = new Shell();
		}
		shell.commandPromptLoop();
	}
}
