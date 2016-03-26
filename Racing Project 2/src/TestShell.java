import junit.framework.TestCase;


public class TestShell extends TestCase {

	Shell shell;
	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testInitialization(){
//		TODO power on, IND, run number is 1, Channels are disarmed.	
		 
	}
	public void testCommands(){
		shell.readCommand("power");
//		TODO check power 

		shell.readCommand("tog 1");
		assertEquals(true, shell.getCurrentRun().getChannel(1));
		shell.readCommand("tog 1");
		assertFalse(shell.getCurrentRun().getChannel(1));
		
//		TODO check every command possible
		
	}
	public void testCommandsFromFile(){
		
	}

}
