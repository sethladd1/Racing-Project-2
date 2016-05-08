import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


public class HTTPHandler {
	private static Run curRun;
	private static ArrayList<JsonObject> names = new ArrayList<JsonObject>();
	private static ArrayList<RacerStats> stats = new ArrayList<HTTPHandler.RacerStats>();
	private static String server = "http://localhost:8000";
	public HTTPHandler(String server){
		HTTPHandler.server = server;
	}
	public static void sendData(Run run){
		try {

			curRun = run;
			names.clear();
			stats.clear();
			getCompetitors();
			//Client will connect to this location
			URL site = new URL(server+"/sendresults");
			HttpURLConnection conn = (HttpURLConnection) site.openConnection();

			// now create a POST request
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());

			// build a string that contains JSON from run
			String content = getJSON();

			System.out.println("\n" + content);

			// write out string to output buffer for message
			out.writeBytes(content);
			out.flush();
			out.close();

			System.out.println("Done sent to server");

			InputStreamReader inputStr = new InputStreamReader(conn.getInputStream());

			// string to hold the result of reading in the response
			StringBuilder sb = new StringBuilder();

			// read the characters from the request byte by byte and build up the sharedResponse
			int nextChar = inputStr.read();
			while (nextChar > -1) {
				sb=sb.append((char)nextChar);
				nextChar=inputStr.read();
			}
			System.out.println("Return String: "+ sb);


		} catch (Exception e) {
			System.out.println("Unable to send data to the server");
//			e.printStackTrace();
		}
	}
	private static void getCompetitors(){
		try{
			//Client will connect to this location
			URL site = new URL(server+"/competitors");
			HttpURLConnection conn = (HttpURLConnection) site.openConnection();

			// now create a POST request
			conn.setRequestMethod("GET");

			conn.setDoInput(true);
			DataInputStream in = new DataInputStream(conn.getInputStream());
			StringBuilder sb = new StringBuilder();
			int nextChar = in.read();
	        while (nextChar > -1) {
	            sb=sb.append((char)nextChar);
	            nextChar=in.read();
	        }
	        String str = sb.toString();
//	        System.out.print("top"+sb.toString()+"bottom");
	        Gson g = new Gson();
	        names.addAll(g.fromJson(str, new TypeToken<Collection<JsonObject>>(){}.getType()));
			}catch(Exception e){	
				System.out.println("Unable to fetch competitor names from server");
//				e.printStackTrace();
			}
	}
	private static String getJSON(){
//		GRP race
		Gson g = new Gson();
		String name;
		ArrayList<Racer> racers = curRun.getRacers();
		ArrayList<Long> ranks = curRun.getGroupRanks();
		boolean found = false;
		if(curRun.getType()==2){
			int i=0;
			while(i<ranks.size()){
				if(i<racers.size()){
					found = false;
					for(JsonObject jso : names){
						if(jso.get("NUMBER").getAsInt() == racers.get(i).getNumber()){
							stats.add(new RacerStats(jso.get("NAME").getAsString(), String.valueOf(racers.get(i).getNumber()), i+1, Time.convertToTimestamp(racers.get(i).getRunTime())));
							found=true;
							break;
						}
					}
					if(!found)
						stats.add(new RacerStats("", String.valueOf(racers.get(i).getNumber()), i+1, Time.convertToTimestamp(racers.get(i).getRunTime())));
				}
				else{
					stats.add(new RacerStats("", "", i+1, Time.convertToTimestamp(ranks.get(i))));
				}
				i++;
			}
		}
		else{
			racers = sortedByTime(racers);
			for(int i=0;i<racers.size();++i){
				found = false;
				for(JsonObject jso : names){
					if(jso.get("NUMBER").getAsInt() == racers.get(i).getNumber()){
						stats.add(new RacerStats(jso.get("NAME").getAsString(), String.valueOf(racers.get(i).getNumber()), i+1, Time.convertToTimestamp(racers.get(i).getRunTime())));
						found=true;
						break;
					}
				}
				if(!found){
					if(racers.get(i).DNF()){
						stats.add(new RacerStats("", String.valueOf(racers.get(i).getNumber()), i+1, "DNF"));	
					}
					else{
						stats.add(new RacerStats("", String.valueOf(racers.get(i).getNumber()), i+1, Time.convertToTimestamp(racers.get(i).getRunTime())));
					}
				}
			}
		}
		return g.toJson(stats);
	}
	private static ArrayList<Racer> sortedByTime(ArrayList<Racer> racers){
		ArrayList<Racer> copy = new ArrayList<Racer>();
		for(Racer r : racers){
			int i=0;
			if(r.DNF()){
				copy.add(r);
			}
			else{
				while(i<copy.size() && copy.get(i).getRunTime()<r.getRunTime()){
					if(copy.get(i).DNF()){
						break;
					}
				}
				copy.add(i, r);
				i++;
			}
		}
		return copy;
	}
	private static class RacerStats{
		public int rank;
		public String name, time,  number;
		public RacerStats(String name, String num, int rank, String time){
			this.rank=rank;
			this.time = time;
			this.name = name;
			this.number=num;
		}
	}
	
}
