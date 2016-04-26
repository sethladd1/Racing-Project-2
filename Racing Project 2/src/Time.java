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
			return "0:0:0.0";
		}
		double e = (double)(System.currentTimeMillis()-start);
		return convertToTimestamp(e);
	}
	public static String convertToTimestamp(double elapsedInMilliseconds){
		// get minutes elapsed
		double elapsedsec = elapsedInMilliseconds/1000;
		int h = (int)(elapsedsec/(60*60));
		int min = (int)((elapsedsec%(60*60))/60);
		// get remaining sec
		double r = (elapsedsec%(60*60))%60;
		//format seconds to hundredth precision
		String sec = String.format("%1$.2f", r);
		return Integer.toString(h)+ ":" + Integer.toString(min)+":"+sec ;

	}
	public long elapsed(){
		return System.currentTimeMillis()-start;
	}
	/**
	 * 
	 * @param str - a time stamp in the form <hour>:<min>:<sec> where <hour> and <min> are integers,and <sec> is floating point
	 * @return true if the string is of the right form
	 */
	public boolean stringToTime(String str){
		Pattern p = Pattern.compile("[0-9]+\\:[0-9]+\\:[0-9]+\\.{0,1}[0-9]*");
		Matcher matcher = p.matcher(str);
		if(matcher.matches()){
			String s[] = matcher.group().split("\\:");
			try{
				String dec;
				String sec[] = s[2].split("\\.");
				String seconds;
				if(sec.length>1){
					seconds = sec[0];
					dec = sec[1];
					if(dec.length()>3){
						dec=dec.substring(0, 3);
					}
					else{
						while(dec.length()<3){
							dec=dec.concat("0");
						}
					}
				}
				else{
					seconds = sec[0];
					dec = "0";
				}
				setCurrent(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(seconds), Integer.parseInt(dec));
				return true;
			} catch(NumberFormatException e){
				return false;

			}
		}
		return false;
	}


}
