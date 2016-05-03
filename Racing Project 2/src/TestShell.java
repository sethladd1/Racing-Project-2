import java.util.ArrayList;

import junit.framework.TestCase;


public class TestShell extends TestCase {

	Shell shell;
	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testInitialization(){
//		TODO power on, IND, run number is 1, Channels are disarmed.	
		shell = new Shell(true);
		assertTrue(shell.getPower());
		Run r = shell.getCurrentRun();
		assertEquals(1, r.getRunNum());
		assertEquals(0, r.getType());
		for(int i=1; i<5; ++i){
			assertFalse("No channels should be armed",Sensors.array[i].isEnabled());
		}
		
	}
	public void testCommands(){
		shell = new Shell(true);
		assertTrue(shell.readCommand("power"));
		assertFalse(shell.readCommand("tog 1"));
		assertTrue(shell.getErrorMessage().equals("Simulator off"));
		shell.readCommand("power");
 

		Run r = shell.getCurrentRun();
		
		ArrayList<Racer> rc= r.getRacers();
		assertTrue(shell.readCommand("tog 1"));
		assertTrue(Sensors.array[1].isEnabled());
		assertTrue(shell.readCommand("tog 1"));
		assertFalse(Sensors.array[1].isEnabled());
		assertTrue(shell.readCommand("tog 2"));
		assertTrue(Sensors.array[2].isEnabled());
		assertTrue(shell.readCommand("tog 2"));
		assertFalse(Sensors.array[2].isEnabled());
		assertTrue(shell.readCommand("tog 3"));
		assertTrue(Sensors.array[3].isEnabled());
		assertTrue(shell.readCommand("tog 3"));
		assertFalse(Sensors.array[3].isEnabled());
		assertTrue(shell.readCommand("tog 4"));
		assertTrue(Sensors.array[4].isEnabled());
		assertTrue(shell.readCommand("tog 4"));
		assertFalse(Sensors.array[4].isEnabled());
		
		assertTrue(shell.readCommand("tog 1"));
		assertTrue(shell.readCommand("tog 2"));
		assertTrue(shell.readCommand("num 16"));
		assertTrue(shell.readCommand("num 22"));
		assertEquals(2, rc.size());
		assertEquals(16, rc.get(0).getNumber());
		assertEquals(22, rc.get(1).getNumber());
		assertTrue(shell.readCommand("trig 1"));
		assertTrue(rc.get(0).started());
		assertTrue(shell.readCommand("trig 1"));
		assertTrue(rc.get(1).started());
		assertTrue(shell.readCommand("trig 2"));
		assertTrue(rc.get(0).finished());
		assertTrue(shell.readCommand("trig 2"));
		assertTrue(rc.get(1).finished());
		assertFalse(shell.readCommand("start"));
		assertFalse(shell.readCommand("finish"));
		assertFalse(shell.readCommand("newrun"));
		
		assertTrue(shell.readCommand("endrun"));
		r.print();
		assertTrue(shell.readCommand("newrun"));
		
		
		
	}
//	public void testCommandsFromFile(){
//		
//	}

}
