
public class Channel {

	boolean chan1;
	boolean chan2;
	
	
	public Channel()
	{
		chan1=false;
		chan2=false;
	}
	//public Channel(boolean chan1, boolean chan2, boolean chan3, boolean chan4)

	public String trigger(int i) {
		if (i==1)
		{
			if (chan1==true)
			{
				return "START";
			}
		}
		if (i==2)
		{
			if (chan2==true)
			{
				return "FINISH";
			}
		}
		return "FAIL";
	}

	public void toggle(int i) {
		// TODO Auto-generated method stub
		if (i==1)
		{
			if (chan1==false)
			{
				chan1=true;
			}
			else
			{
				chan1=false;
			}
		}
		if (i==2)
		{
			if (chan2==false)
			{
				chan2=true;
			}
			else
			{
				chan2=false;
			}
		}
	}

}
