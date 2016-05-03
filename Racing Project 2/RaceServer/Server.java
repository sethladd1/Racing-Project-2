
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;



import java.util.Collection;
import java.util.Comparator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class Server {

    // a shared area where we get the POST data and then use it in the other handler
    static String sharedResponse = "";
    static ArrayList<RacerStats> stats= new ArrayList<RacerStats>();
    static Gson gson = new Gson();
    static int runNum = 1;
    public static void main(String[] args) throws Exception {

        // set up a simple HTTP server on our local host
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        
      
        // create a context to get the request to display the results
        server.createContext("/results", new DisplayHandler());

        // create a context to get the request for the POST
        server.createContext("/sendresults",new PostHandler());
        server.createContext("/competitors", new CompetitorsHandler());
        server.setExecutor(null); // creates a default executor

        // get it going
        server.start();
    }
    static class CompetitorsHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange t) throws IOException {
			// TODO Auto-generated method stub
			
			Gson g = new Gson();
			ArrayList<JsonObject> objects = new ArrayList<JsonObject>();
			JsonObject jso = new JsonObject();
			jso.addProperty("NAME", "Joe");
			jso.addProperty("NUMBER", 111);
			objects.add(jso);
			jso = new JsonObject();
			jso.addProperty("NAME", "Bob");
			jso.addProperty("NUMBER", 112);
			objects.add(jso);
			jso = new JsonObject();
			jso.addProperty("NAME", "Jim");
			jso.addProperty("NUMBER", 113);
			objects.add(jso);
			jso = new JsonObject();
			jso.addProperty("NAME", "George PFunk Clinton");
			jso.addProperty("NUMBER", 114);
			objects.add(jso);
			String response = g.toJson(objects);
			t.sendResponseHeaders(200, response.length());
//            t.getResponseHeaders().set("Content-type", "text/HTML");

            // write out the response
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
		}
    	
    }
    static class DisplayHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {

            String response;

            response=getOutput();
            // set up the header
            System.out.println("Part of the test");
            t.sendResponseHeaders(200, response.length());
            t.getResponseHeaders().set("Content-type", "text/HTML");

            // write out the response
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        public String getOutput(){
        	String out = "<html><head><style >th{background-color:lightgreen;}tr[name='1']{background-color:lightgrey;}</style></head><body><table><th >Last Race</th><tr><th> Place </th><th> Number </th><th> Name </th><th> Time </th></tr>";
        	int i=0;
        	for(RacerStats r : stats){
        		i++;
        		out += "<tr id='"+i+"' name='"+i%2+"'><td> "+r.rank +" </td><td> "+r.number+" </td><td id='name"+i+"'> "+ r.name+" </td><td> "+ r.time+" </td></tr>";
        	}
        	return out+="</table></body></html>";
        }
    }

    static class PostHandler implements HttpHandler {
        public void handle(HttpExchange transmission) throws IOException {

            //  shared data that is used with other handlers
            sharedResponse = "";

            // set up a stream to read the body of the request
            InputStream inputStr = transmission.getRequestBody();

            // set up a stream to write out the body of the response
            OutputStream outputStream = transmission.getResponseBody();

            // string to hold the result of reading in the request
            StringBuilder sb = new StringBuilder();

            // read the characters from the request byte by byte and build up the sharedResponse
            int nextChar = inputStr.read();
            while (nextChar > -1) {
                sb=sb.append((char)nextChar);
                nextChar=inputStr.read();
            }
            
            // create our response String to use in other handler
            sharedResponse = sb.toString();
            
            // respond to the POST with ROGER
            String postResponse = getStats();
            System.out.println("response: " + sharedResponse);



            // assume that stuff works all the time
            transmission.sendResponseHeaders(300, postResponse.length());

            // write it and return it
            outputStream.write(postResponse.getBytes());

            outputStream.close();
        }
    }
    private static String getStats(){
    	try{
    	stats.addAll(gson.fromJson(sharedResponse, new TypeToken<Collection<RacerStats>>(){}.getType())) ;
    	stats.sort(new Comparator<RacerStats>() {

			@Override
			public int compare(RacerStats arg0, RacerStats arg1) {
				// TODO Auto-generated method stub
				return arg0.rank - arg1.rank;
			}
    		
		});
    	return "ROGER JSON RECEIVED";
    	}catch(JsonSyntaxException e){
    		return "Json Syntax Exception";
    	}
    }
    

}

