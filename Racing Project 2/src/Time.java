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
		double e = (double)(System.currentTimeMillis()-start);
		return convertToTimestamp(e);
	}
	public String convertToTimestamp(double elapsedInMilliseconds){
		// get minutes elapsed
		int min = (int)elapsedInMilliseconds/60;
		// get remaining sec
		double r = elapsedInMilliseconds%60;
		//format seconds to hundredth precision
		String sec = Double.toString(r);
		String.format("%1$.2f", sec);
		return Integer.toString(min)+":"+sec ;
		
	}
	public boolean stringToTime(String s){
		Pattern p = Pattern.compile("[0-9]+\\:[0-9]+\\:[0-9]+");
		Matcher matcher = p.matcher(s);
		if(matcher.matches()){
			String arr[] = matcher.group().split("\\:");
			if(arr.length>2){
				try{
					setCurrent(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), 0);
					return true;
				} catch(NumberFormatException e){
					return false;
				}
			}
		}
		return false;
	}

	
}

