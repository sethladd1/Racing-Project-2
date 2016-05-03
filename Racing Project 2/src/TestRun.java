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


		r.addRacer(111);

		assertEquals(1, racers.size());
		assertEquals(111, racers.get(0).getNumber());
		assertTrue(r.start());
		assertFalse(r.start());
		assertTrue(racers.get(0).started());
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis()-start<5000){}
		assertTrue(r.finish());
		assertFalse(r.finish());
		assertTrue(racers.get(0).finished());
		assertTrue(racers.get(0).getRunTime()>=5000);
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

		
		
		assertTrue(r.addRacer(111));
		assertTrue(r.addRacer(112));
		assertTrue(r.addRacer(113));
		assertTrue(r.addRacer(114));
		assertFalse(r.addRacer(111));
		
		for(Racer rc : racers){
			assertFalse(rc.started());
			assertFalse(rc.finished());
		}
		assertTrue(r.start());
		assertTrue(racers.get(0).started());
		assertFalse(racers.get(1).started());

		assertTrue(r.start());
		assertTrue(racers.get(1).started());
		assertTrue(r.start());
		assertTrue(r.start());
		assertFalse(r.start());
		for(Racer rc : racers){
			assertTrue(rc.started());
			r.finish();
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

		r.start(1);
		assertFalse(racers.get(1).started());
		assertTrue(racers.get(0).started());
		r.start(2);
		assertTrue(racers.get(1).started());
		r.finish(2);
		assertFalse(racers.get(0).finished());
		assertTrue(racers.get(1).finished());
		r.finish(1);
		assertTrue(racers.get(0).finished());
		
	}
	public void testGRP(){
		r = new Run(2,1);
		racers = r.getRacers();


		for(int i=0;i<10;++i){
			r.addRacer(i);
		}
		r.start();
		for(int i=0;i<10;++i){
			assertTrue(racers.get(i).started());
			assertFalse(racers.get(i).finished());
		}
		r.finish();
		assertTrue(racers.get(0).finished());
		assertFalse(racers.get(1).finished());
		assertFalse(racers.get(2).finished());
		
		for(int i=1;i<10;++i){
			r.finish();
			assertTrue(racers.get(i).finished());
			
		}
		r = new Run(2,1);
		racers = r.getRacers();

		r.start();
		assertEquals(0,r.getGroupRanks().size());
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis()-start<100){}
		r.finish();
		assertEquals(1,r.getGroupRanks().size());
		start = System.currentTimeMillis();
		while(System.currentTimeMillis()-start<10){}
		r.finish();
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
