import junit.framework.TestCase;


public class TestRacer extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	public void test(){
		Racer r = new Racer(115);
		assertEquals(115, r.getNumber());
		assertFalse(r.started());
		assertFalse(r.finished());
		r.setStart(15654894);
		assertTrue(r.started());
		assertEquals(15654894, r.getStart());
		r.setFinish(17654894);
		assertTrue(r.finished());
		assertEquals(17654894, r.getFinish());
		assertEquals(2000000, r.getRunTime());
		
		
		r = new Racer(211);
		r.setStart(456468465);
		r.setDNF(true);
		assertTrue(r.DNF());
		assertEquals(-1, r.getRunTime());
		assertEquals(-1, r.getFinish());
	}

}
