
public class Driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Shell shell;
		if(args.length>1){
			shell = new Shell(args[1]);
		}
		else{
			shell = new Shell();
		}
		shell.commandPromptLoop();
	}

}
