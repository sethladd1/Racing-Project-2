import junit.framework.TestCase;


public class TestTime extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	public void test(){
		Time t = new Time();
		long start1 = System.currentTimeMillis();
		t.start();
		long start2 = System.currentTimeMillis();
		while(System.currentTimeMillis() - start1<10000){}
//		t.start() was called between start1 and start2 being set, so t.elapsed() should be >= (time since start2 set) and <= (time since start1 set)   
		assertTrue((t.elapsed()<=System.currentTimeMillis()-start1) && (t.elapsed()>=System.currentTimeMillis()-start2));
		start1 = System.currentTimeMillis();
		t.setCurrent(1, 5, 6, 0);
		assertTrue((t.elapsed()>=3906000 && t.elapsed()<=3906000 + System.currentTimeMillis()-start1));
		assertTrue("was " + t.convertToTimestamp(3906000),t.convertToTimestamp(3906000).equals("1:5:6.00"));
		
		start1 = System.currentTimeMillis();
		t.stringToTime("01:5:6");
		assertTrue((t.elapsed()>=3906000 && t.elapsed()<=3906000 + System.currentTimeMillis()-start1));
		
	}
}
