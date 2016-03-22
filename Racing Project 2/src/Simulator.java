/**
 * Simulator Class
 * CB Evan Becker
 */
public class Simulator 
{
	private boolean power = true;
	
	/** Set power
	 * @param status
	 */
	public String power(boolean status)
	{
		power = status;
		if(power == true)
			return "Turning System off";
		else
			return "Turning System on (Entering Quiescent State)";
	}
	
	/** Set power
	 * @param status
	 */
	public String powerTog()
	{
		if(power == true)
		{
			power = false;
			return "Turning system off";
		}
		else
		{
			power = true;
			return "Turning System on (Entering Quiescent State)";
		}
	}
	
	public String reset()
	{
		
		
		return "Resetting System";
	}
	
	public boolean getStatus()
	{
		return power;
	}

}
