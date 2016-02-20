package demoClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;

import bundle.LoginFeedbackBundle;
import bundle.LoginInfoBundle;
import demoServlet.Authorization;
import demoServlet.LoginInfo;

/** HTTP Client demo program. Can both GET and POST to a URL and read input back from the
 * server. Designed to be used with the demoServlet servlet.
 * Run as demoClient.Main http://localhost:8080/demoServlet/ to read the current message.
 * Run as demoClient.Main http://localhost:8080/demoServlet/ <msg> to change the message.
 * Run as demoClient.Main http://localhost:8080/demoServlet/ <msg> <other> to change 
 * the message and the other parameter
 */
public class MainClient {
	public static void main(String[] args) {
		if (args.length < 1 || args.length > 3) {
			usage();
		}
		new MainClient(args[0], args.length >= 2 ? args[1] : null,  args.length == 3 ? args[2] : null);
	}
	private static void usage() {
		System.err.println("Usage: MyClient <URL> [<message>]");
		System.exit(1);
	}
	
	private static class GetJsonBundle {
		  private String uri;
		  private String message;
		  private int otherParameter;
	}

	class PostJsonBundle {
		  private String message;
	}
	
	private URL url;
	
	/** If message is null, query the URL (GET) for the current message.
	 * Otherwise, use a POST request to send the message.
	 */
	public MainClient(String url, String message, String newOther) {
		Gson gson = new Gson();
		try {
			if (newOther == null)
				this.url = new URL(url);
			else
				this.url = new URL(url + "?other_param=" + newOther);
			
			HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
			if (message == null) {
//				System.out.println("start connecting");
//				connection.connect();
//				System.out.println("Doing GET " + url);
//				BufferedReader r = new BufferedReader(new InputStreamReader(
//						connection.getInputStream()));
//				GetJsonBundle bundle = gson.fromJson(r, GetJsonBundle.class);
//				System.out.println("uri: " + bundle.uri);
//				System.out.println("message: " + bundle.message);
//				System.out.println("other: " + bundle.otherParameter);
				
				
				System.out.println("Doing POST "+ url);
				connection.setDoOutput(true); // send a POST message
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				System.out.println("settype");
				PrintWriter w = new PrintWriter(connection.getOutputStream());
				LoginInfoBundle bundle = new LoginInfoBundle("admin","admin");
				w.println(gson.toJson(bundle, LoginInfoBundle.class));
				w.flush();
				BufferedReader r = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				r.mark(1);
				System.out.println(r.readLine());
				r.reset();
				LoginFeedbackBundle feedback = gson.fromJson(r, LoginFeedbackBundle.class);
//				System.out.println(gson);
//				System.out.println(feedback.getId());	
				System.out.println("connected");
				
				
			} else {
				System.out.println("Doing POST "+ url);
				connection.setDoOutput(true); // send a POST message
				connection.setRequestMethod("POST");
				PrintWriter w = new PrintWriter(connection.getOutputStream());
				PostJsonBundle bundle = new PostJsonBundle();
				bundle.message = message;
				w.println(gson.toJson(bundle, PostJsonBundle.class));
				w.flush();
				connection.setRequestProperty("Content-Type", "application/json");
				BufferedReader r = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				dumpResponse(r);		
				System.out.println("connected");
			}
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		}
	}
	/** Read back output from the server. Could change to parse JSON... */
	void dumpResponse(BufferedReader r) throws IOException {
		for (;;) {
			String l = r.readLine();
			if (l == null) break;
			System.out.println("Read: " + l);
		}
	}
}
