import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Time
{
	private long start;
	
	public Time()
	{
		
	}
	public Time(int H, int M, int S, int MS)
	{
		setCurrent(H, M, S, MS);
	}
	public void start()
	{
		start = System.currentTimeMillis();
	}
	public void setCurrent(int H, int M, int S, int MS)
	{
		long newElapsed = H*60*60*1000+M*60*1000+S*1000+MS;
		start = System.currentTimeMillis()-newElapsed;
	}
	public String getCurrentTimeStamp(){
		if(start == 0){
			return "00:00.00";
		}
		double e = (double)(System.currentTimeMillis()-start)/1000;
		return convertToTimestamp(e);
	}
	private String convertToTimestamp(double elapsedInSecond){
		// get minutes elapsed
		int min = (int)elapsedInSecond/60;
		// get remaining sec
		double r = elapsedInSecond%60;
		//format seconds to hundredth precision
		String sec = Double.toString(r);
		String.format("%1$.2f", sec);
		return Integer.toString(min)+":"+sec ;
		
	}
	public Time stringToTime(String s)
	{
		Pattern p = Pattern.compile("[0-9]+\\:[0-9]+\\.[0-9]+");
		Matcher matcher = p.matcher(s);
		if(matcher.matches()){
			String arr[] = matcher.group().split("\\:");
			return new Time(0, Integer.parseInt(arr[0]), Integer.parseInt(arr[1].split("\\.")[0]), Integer.parseInt(arr[1].split("\\.")[1]));
		}
		else{
			return new Time();
		}
	}
	
}

