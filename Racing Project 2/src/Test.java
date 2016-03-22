import java.io.File;

import junit.framework.TestCase;
//Currently only tests for exceptions
public class Test extends TestCase{
	/***
	 * Tests if readCommandLine throws an exception
	 */
	public void testLine() {
		try{
			Shell.readCommandLine("on\noff\n");
		}
		catch(Exception e){
			assertTrue("Should not throw exception.",false);
		}
	}
	/***
	 * Tests if readCommandTextFile throws an exception
	 */
	public void testFile() {
		try{
			Shell.readCommandTextFile(new File("././Input.txt"));
		}
		catch(Exception e){
			assertTrue("Should not throw exception.",false);
		}
	}
}
