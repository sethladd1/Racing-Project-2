import java.util.ArrayList;

import junit.framework.TestCase;


public class TestShell extends TestCase {

	Shell shell;
	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testInitialization(){
//		TODO power on, IND, run number is 1, Channels are disarmed.	
		shell = new Shell();
		assertTrue(shell.getPower());
		Run r = shell.getCurrentRun();
		assertEquals(1, r.getRunNum());
		assertEquals(0, r.getType());
		for(int i=1; i<5; ++i){
			assertFalse("No channels should be armed: channel "+i +" armed",r.getChannel(i));
		}
		
	}
	public void testCommands(){
		shell = new Shell();
		assertTrue(shell.readCommand("power"));
		assertFalse(shell.readCommand("tog 1"));
		assertTrue(shell.getErrorMessage().equals("Simulator off"));
//		TODO check power 

		Run r = shell.getCurrentRun();
		
		ArrayList<Racer> rc= r.getRacers();
		assertTrue(shell.readCommand("tog 1"));
		assertTrue(r.getChannel(1));
		shell.readCommand("tog 1");
		assertFalse(r.getChannel(1));
		shell.readCommand("tog 2");
		assertTrue(r.getChannel(2));
		shell.readCommand("tog 2");
		assertFalse(r.getChannel(2));
		shell.readCommand("tog 3");
		assertTrue(r.getChannel(3));
		shell.readCommand("tog 3");
		assertFalse(r.getChannel(3));
		shell.readCommand("tog 4");
		assertTrue(r.getChannel(4));
		shell.readCommand("tog 4");
		assertFalse(r.getChannel(4));
		
		assertTrue(shell.readCommand("tog 1"));
		assertTrue(shell.readCommand("tog 2"));
		assertTrue(shell.readCommand("num 16"));
		assertTrue(shell.readCommand("num 22"));
		assertEquals(2, rc.size());
		assertEquals(16, rc.get(0).getNumber());
		assertEquals(22, rc.get(1).getNumber());
		
//		TODO check every command possible
		
	}
//	public void testCommandsFromFile(){
//		
//	}

}
