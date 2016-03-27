import java.util.ArrayList;

import junit.framework.TestCase;


public class TestRun extends TestCase {
	ArrayList<Racer> racers;
	Run r;
	protected void setUp() throws Exception {
		super.setUp();
		r = new Run(0,1);
		racers = r.getRacers();
		assertTrue(r.running());

		assertFalse(r.getChannel(1));
		r.toggle(1);
		assertTrue(r.getChannel(1));
		assertFalse(r.getChannel(2));
		r.toggle(2);
		assertTrue(r.getChannel(2));
		assertFalse(r.getChannel(3));
		r.toggle(3);
		assertTrue(r.getChannel(3));
		assertFalse(r.getChannel(4));
		r.toggle(4);
		assertTrue(r.getChannel(4));
	}
	public void testOneRacer(){
		
		try {
			setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		r.addRacer(111);

		assertEquals(1, racers.size());
		assertEquals(111, racers.get(0).getNumber());
		assertTrue(r.trigger(1));
		assertFalse(r.trigger(1));
		assertTrue(racers.get(0).started());
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis()-start<5000){}
		assertTrue(r.trigger(2));
		assertFalse(r.trigger(2));
		assertTrue(racers.get(0).finished());
		assertTrue("Was "+ racers.get(0).getRunTime(),racers.get(0).getRunTime()>=5000);
		r.end();
		assertFalse(r.running());
	}
	public void testMultipleRacer(){
		try {
			setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(r.addRacer(111));
		assertTrue(r.addRacer(112));
		assertTrue(r.addRacer(113));
		assertTrue(r.addRacer(114));
		assertFalse(r.addRacer(111));
		
		for(Racer rc : racers){
			assertFalse(rc.started());
			assertFalse(rc.finished());
		}
		assertTrue(r.trigger(1));
		assertTrue(racers.get(0).started());
		assertFalse(racers.get(1).started());

		assertTrue(r.trigger(3));
		assertTrue(racers.get(1).started());
		assertTrue(r.trigger(1));
		assertTrue(r.trigger(3));
		assertFalse(r.trigger(1));
		for(Racer rc : racers){
			assertTrue(rc.started());
			r.trigger(2);
			assertTrue(rc.finished());
		}
	}
}
