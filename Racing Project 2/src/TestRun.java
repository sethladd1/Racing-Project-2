import java.util.ArrayList;

import junit.framework.TestCase;


public class TestRun extends TestCase {
	ArrayList<Racer> racers;
	Run r;
	protected void setUp() throws Exception {
	}
	public void testOneRacer(){
		r = new Run(0,1);
		racers = r.getRacers();
		try {
			setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void testParind(){
		r = new Run(1,1);
		racers = r.getRacers();
		r.addRacer(1);
		r.addRacer(2);
		assertEquals(r.getStartQueue1().get(0).getNumber(), 1);
		assertEquals(r.getStartQueue2().get(0).getNumber(), 2);
		r.toggle(1);
		r.toggle(2);
		r.toggle(3);
		r.toggle(4);
		r.connect("GATE", 1);
		r.connect("EYE", 2);
		r.connect("GATE", 3);
		r.connect("EYE", 4);
		r.trigger(1);
		assertFalse(racers.get(1).started());
		assertTrue(racers.get(0).started());
		r.trigger(3);
		assertTrue(racers.get(1).started());
		r.trigger(4);
		assertFalse(racers.get(0).finished());
		assertTrue(racers.get(1).finished());
		r.trigger(2);
		assertTrue(racers.get(0).finished());
		
	}
	public void testGRP(){
		r = new Run(2,1);
		racers = r.getRacers();
		r.toggle(1);
		r.toggle(2);

		r.connect("GATE", 1);
		r.connect("EYE", 2);

		for(int i=0;i<10;++i){
			r.addRacer(i);
		}
		r.trigger(1);
		for(int i=0;i<10;++i){
			assertTrue(racers.get(i).started());
			assertFalse(racers.get(i).finished());
		}
		r.trigger(2);
		assertTrue(racers.get(0).finished());
		assertFalse(racers.get(1).finished());
		assertFalse(racers.get(2).finished());
		
		for(int i=1;i<10;++i){
			r.trigger(2);
			assertTrue(racers.get(i).finished());
			
		}
		r = new Run(2,1);
		racers = r.getRacers();
		r.toggle(1);
		r.toggle(2);

		r.connect("GATE", 1);
		r.connect("EYE", 2);
		r.trigger(1);
		assertEquals(0,r.getGroupRanks().size());
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis()-start<100){}
		r.trigger(2);
		assertEquals(1,r.getGroupRanks().size());
		start = System.currentTimeMillis();
		while(System.currentTimeMillis()-start<10){}
		r.trigger(2);
		assertEquals(2,r.getGroupRanks().size());
		
		r.addRacer(1);
		assertTrue(racers.get(0).started());
		assertTrue(racers.get(0).finished());
		long l1=r.getGroupRanks().get(0);
		assertEquals(l1, racers.get(0).getRunTime());
		
		r.addRacer(2);
		assertTrue(racers.get(1).started());
		assertTrue(racers.get(1).finished());
		l1=r.getGroupRanks().get(1);
		assertEquals(l1, racers.get(1).getRunTime());
	}
}
