package demoServlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import bundle.CritterBundle;
import bundle.CritterRequestBundle;
import bundle.CritterResponseBundle;
import bundle.EntityBundle;
import bundle.LoginFeedbackBundle;
import bundle.PositionBundle;
import bundle.RunBundle;
import bundle.StepBundle;
import bundle.WorldInfoBundle;
import bundle.WorldStateBundle1;
import demoServlet.Authorization.Level;
import javafx.stage.FileChooser;
import simulation.Critter;
import simulation.Food;
import simulation.HexCoord;
import simulation.Rock;
import simulation.World;
import view.Position;

/**
 * Servlet implementation class
 */
@WebServlet("/") /* relative URL path to servlet (under package name 'demoServlet'). */
public class CritterWorld extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Authorization auth;
	private String contentType;
	private static final String content_Type = "Content_Type";
	private static final String content_json = "application/json";
	private static final String content_plain = "text/plain";
	private URL requestUrl;
	private static double HEX_LENGTH = 30;
	private static int ROW = 20;
	private static int COL = 10;
	private static int DEFAULT_RATE = 50;
	private HashMap<Position,HexCoord> PosiToHex = new HashMap<Position,HexCoord>();
	private HashMap<HexCoord,Position> HexToPosi = new HashMap<HexCoord,Position>();
	private ArrayList<Position> positions = new ArrayList<Position>();
	private HexCoord selectedHex = null;
	private FileChooser fileChooser;
	private boolean executing = false;
	private World world;
	private float rate = DEFAULT_RATE;
	private ArrayList<WorldDiff> versionList = new ArrayList<WorldDiff>();
	private int current_version;
	private Hashtable<Integer, Integer> critterToUser = new Hashtable<Integer,Integer>();
	private boolean running;
	private int step_times;
	public void init(){
		auth = new Authorization(getServletContext());
	}
	
	
	/**
	 * Handle GET request
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		contentType = getContentType(request);
		
//		System.out.println(contentType);
		String reqUrl = request.getRequestURI();
//		requestUrl = new URL(reqUrl);
		if(reqUrl.contains("critters")){
			//list all critters
			listAllCritters(request, response);
		}else if(reqUrl.contains("critter")){
			// retrieve a critter
			retriveACritter(request, response);
		}else if(reqUrl.contains("world")){
			// request may be get state of the world 
			// or get subsection of the world
			boolean isGetState = isGetState(request);
			if(isGetState){
				getWorldState(request, response);
			}else{
				getWorldSubsection(request, response);
			}
		}
		
	}



	/**
	 * Judge whether the request is for get world state or for subsection of the world
	 * @param request
	 * @return is get world state request
	 */
	private boolean isGetState(HttpServletRequest request) {
		// TODO Auto-generated method stub
		Enumeration paras = request.getParameterNames();
//		System.out.println(headerNames);
		while(paras.hasMoreElements()){
			String key = (String) paras.nextElement();
			if(key.equalsIgnoreCase("from_row")){
				return false;
			}
		}
		return true;

	}


	private void listAllCritters(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		int sessionId = getSessionId(request);
		ArrayList<Critter> cris = this.world.getCritters();
		
	}


	private void retriveACritter(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		int userid = getSessionId(request);
		int criId = getCritterId(request);
		System.out.println("criId:  " + criId );
		// get the critter according to critter id
		ArrayList<Critter> cris = this.world.getCritters();
		Critter critter = null;
		for(Critter cri : cris){
			if(cri.getId() == criId){
				critter = cri;
				break;
			}
		}
		//get criter bundle and write to response
		CritterBundle cb = new CritterBundle();
		cb.setBundle(critter, userid, critterToUser, this.auth);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		response.addHeader("Content-Type", "application/json");
		PrintWriter w = response.getWriter();
		
		String json = gson.toJson(cb);
		w.println(json);
		w.flush();
		w.close();
		response.setStatus(200);
		
	}


	private int getCritterId(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String url = request.getRequestURI();
		int index1 = url.lastIndexOf('/');
		String id = url.substring(index1+1, url.length());
		
		return Integer.parseInt(id);
	}


	/**
	 * handle the get state of world request
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void getWorldState(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		int id = getSessionId(request);
		int updateSince = getUpdateSince(request);
		WorldStateBundle1 worldState = new WorldStateBundle1();
		if(this.world != null){
			worldState.setBundleState(updateSince, this.current_version, versionList, 
			this.world.getMap(), id, this.critterToUser);
			worldState.setCurrent_timeStep(this.world.getSteps());
			worldState.setCurrent_version(this.current_version);
			System.out.println("current version:   "+ this.current_version);
			System.out.println("set version:  " + worldState.current_version_number);
			worldState.setUpdate(updateSince);
			worldState.setRate(this.rate);
			worldState.setName(this.world.getName());
			worldState.setPopulation(this.world.getCritterNumber());
			worldState.setRow(this.world.getRow());
			worldState.setCol(this.world.getCol());
		}
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		response.addHeader("Content-Type", "application/json");
		PrintWriter w = response.getWriter();
		
		String json = gson.toJson(worldState);
		w.println(json);
		w.flush();
		w.close();
		response.setStatus(200);
	}


	/**
	 * get the update_since parameter of the request
	 * @param request 
	 * @return the update_since parameter of the request
	 */
	private int getUpdateSince(HttpServletRequest request) {
		// TODO Auto-generated method stub
		Enumeration paras = request.getParameterNames();
//		System.out.println(headerNames);
		while(paras.hasMoreElements()){
			String key = (String) paras.nextElement();
			if(key.equalsIgnoreCase("update_since")){
				return Integer.parseInt(request.getParameter(key));
			}
		}		
		return 0;
	}


	private void getWorldSubsection(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * Handle POST request.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		contentType = getContentType(request);
		
		String reqUrl = request.getRequestURI();
//		request.getParameterNames();
//		requestUrl = new URL(reqUrl);
		if(reqUrl.endsWith("login")){
			handleLogin(request, response);
		}else if(reqUrl.contains("critters")){
			createACritter(request, response);
		}else if(reqUrl.contains("world")){
			makeNewWorld(request, response);
		}else if(reqUrl.contains("create_entity")){
			createEntity(request, response);
		}else if(reqUrl.contains("step")){
			step(request, response);
		}else if(reqUrl.contains("run")){
			run(request, response);
		}
		
	}
	
	/**
	 * get the content type of the request
	 * @param request
	 * @return the content type of the request
	 */
	private String getContentType(HttpServletRequest request) {
		// TODO Auto-generated method stub
		Enumeration headerNames =  request.getHeaderNames();
		System.out.println(headerNames);
		while(headerNames.hasMoreElements()){
			String key = (String) headerNames.nextElement();
			if(key.equalsIgnoreCase("Content-Type")){
				contentType = request.getHeader(key);
				break;
			}
		}
		System.out.println(contentType);
		return contentType;
	}


	private void createACritter(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		String url = request.getRequestURI();
		int sessionId = getSessionId(request);
		if(!this.auth.authorize(sessionId, Level.ADMIN) && !this.auth.authorize(sessionId, Level.WRITE)){
			response.setStatus(401);
			return;
		}
		
		response.addHeader(content_Type, content_json);
		PrintWriter w = response.getWriter();
		BufferedReader r = request.getReader();
		Gson gson = new Gson();
		CritterRequestBundle crb = gson.fromJson(r, CritterRequestBundle.class);
		String speciesId = crb.species_id;
		if(speciesId == null){
			speciesId = "random";
		}
		String program = crb.program;
		int[] mem = crb.mem;
		ArrayList<PositionBundle> posis = crb.positions;
		
//		String path = System.getProperty("user.dir");
		String path = "D:/workspace/cs2112/12A7/examples";
		path = path + "/CritterInfo.txt";
		File file = new File(path);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write("species: " + speciesId + '\n');
		writer.write("memsize: " + mem[0] + '\n');
		writer.write("defense: " + mem[1] + '\n');
		writer.write("offense: " + mem[2] + '\n');
		writer.write("size: " + mem[3] + '\n');
		writer.write("energy: " + mem[4] + '\n');
		writer.write("posture: " + mem[7] + '\n');
		writer.write(program);
		writer.flush();
		writer.close();
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for(int i = 0; i < posis.size(); i++){
			Critter cri = new Critter(path);
			cri.setName(speciesId);
			PositionBundle pb = posis.get(i);
			HexCoord hex = new HexCoord(pb.col,pb.row);
			this.world.addObj(cri, hex);
			ids.add(cri.getId());
			this.critterToUser.put(cri.getId(), sessionId);
		}
		CritterResponseBundle cresponseb = new CritterResponseBundle(speciesId, ids);
		String json = gson.toJson(cresponseb);
		w.println(json);
		w.flush();
		w.close();
		response.setStatus(200);
		
		
		
	}

	/**
	 * handle the new world request
	 * @param request the request for creating new world
	 * @param response
	 * @throws IOException
	 */
	private void makeNewWorld(HttpServletRequest request, HttpServletResponse response)
			throws IOException{
		// TODO Auto-generated method stub
		int id = getSessionId(request);
		if(!this.auth.authorize(id, Level.ADMIN)){
			response.setStatus(401);
		}else{
			response.addHeader(content_Type, content_plain);
			PrintWriter w = response.getWriter();
			w.append("Ok");
			BufferedReader r = request.getReader();
			Gson gson = new Gson();
			WorldInfoBundle worldBundle = gson.fromJson(r, WorldInfoBundle.class);
			String description = worldBundle.description;
//			String path = System.getProperty("user.dir");
			String path = "D:/workspace/cs2112/12A7/examples";
			path = path + "/worldInfo.txt";
			File file = new File(path);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(description);
			writer.flush();
			writer.close();
			System.out.println("this is the world path: "+ path);
			this.world = new World(path);
			System.out.println("make new world:  " + path);
			ArrayList<Critter> critters = this.world.getCritters();
			for(int i = 0; i < this.world.getCritters().size(); i++){
				this.critterToUser.put(critters.get(i).getId(), id);
			}
			System.out.println("world created");
			WorldDiff diff = new WorldDiff(this.world.getChange(), this.world.getDieCritter());
			versionList.add(diff);
			this.current_version = 1;
			w.flush();
			w.close();
			response.setStatus(200);
		}
	}

	/**
	 * get the session_id parameter of the request
	 * @param request
	 * @return session_id
	 */
	private int getSessionId(HttpServletRequest request) {
		// TODO Auto-generated method stub
		Enumeration paras = request.getParameterNames();
//		System.out.println(headerNames);
		while(paras.hasMoreElements()){
			String key = (String) paras.nextElement();
			if(key.equalsIgnoreCase("session_id")){
				return Integer.parseInt(request.getParameter(key));
			}
		}
		return 0;
	}


	private void createEntity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		int id = getSessionId(request);
		if(!this.auth.authorize(id, Level.ADMIN)&&!this.auth.authorize(id, Level.WRITE)){
			response.setStatus(401);
		}else{
			Gson gson = new Gson();
			BufferedReader br = request.getReader();
			EntityBundle eb = gson.fromJson(br, EntityBundle.class);
			HexCoord hex = new HexCoord(eb.col, eb.row);
			if(this.world.getMap().get(hex)!=null){
				response.setStatus(406);
				return;
			}
			if(eb.type.equalsIgnoreCase("food")){
				Food food = new Food(eb.amount);
				this.world.addObj(food, hex);
			}else{
				Rock rock = new Rock();
				this.world.addObj(rock, hex);
			}
			response.addHeader(this.contentType, this.content_plain);
			PrintWriter w = response.getWriter();
			w.println("Ok");
			w.flush();
			w.close();
			response.setStatus(200);
		}
	}


	private void step(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		int sessionId=  getSessionId(request);
		
		if(this.running){
			response.setStatus(406);
			return;
		}else if(!this.auth.authorize(sessionId, Level.ADMIN) 
				&& !this.auth.authorize(sessionId, Level.WRITE)){
			response.setStatus(401);
			return;
		}else{
			Gson gson = new Gson();
			BufferedReader br = request.getReader();
			StepBundle sb = gson.fromJson(br, StepBundle.class);
	
			if(sb.count != null) step_times = sb.count;
			if(step_times == 0) step_times = 1;
//			while(step_times>0){
//				step_times--;
//				new Thread(){
//					public void run(){
//						world.execute(1);
//						WorldDiff diff = new WorldDiff(world.getChange(), world.getDieCritter());
//						versionList.add(diff);
//					}
//				}.start();
//			}
			world.execute(1);
			WorldDiff diff = new WorldDiff(world.getChange(), world.getDieCritter());
			versionList.add(diff);
			current_version++;
			
			response.addHeader(content_Type, content_plain);
			PrintWriter w = response.getWriter();
			w.append("Ok");
			w.close();
		}
	}


	private void run(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		int sessionId = getSessionId(request);
		if(!this.auth.authorize(sessionId, Level.ADMIN) 
				&& !this.auth.authorize(sessionId, Level.WRITE)){
			response.setStatus(401);
			return;
		}else{
			Gson gson = new Gson();
			BufferedReader br = request.getReader();
			RunBundle rb = gson.fromJson(br, RunBundle.class);
			if(rb.rate < 0){
				PrintWriter w = response.getWriter();
				w.append("Ok");
				w.flush();
				w.close();
				return;
			}
			this.rate = rb.rate;
			if(Math.abs(this.rate - 0) < 0.001 ){
				running = false;
				return;
			}
			if(running) return;
			
			running = true;
//			while(running){
//				if(!running){
//					break;
//				}
				new Thread(){
					public void run(){
						while(running){
							if(!running){
								break;
							}
							
							world.execute(1);
							WorldDiff diff = new WorldDiff(world.getChange(), world.getDieCritter());
							versionList.add(diff);
							world.clearChange();
							current_version++;
							System.out.println(rate);
							try {
								sleep((long) (1000/rate));
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						System.out.println("running");
					}
				}.start();
			}
			
			
			response.addHeader(content_Type, content_plain);
			PrintWriter w = response.getWriter();
			w.append("Ok");
			w.flush();
			w.close();
			response.setStatus(200);
			
		}
		

//		
//	}
	/**
	 * do delete
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String reqUrl = request.getRequestURI();
		int sessionId = getSessionId(request);
		int id = getCritterId(request);
		if(!this.auth.authorize(sessionId, Level.ADMIN) 
				&& !critterToUser.get(id).equals(sessionId)){
			response.setStatus(401);
		}

		this.world.removeCritter(id);
//		this.world.removeObj(placeObj, posi)
		System.out.println("sessionId: " + sessionId + "   id:  "+id);
		response.setStatus(200);
	}
	
	
	private void deleteACritter(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * handle the login in request
	 * @param request 
	 * @param response
	 * @throws IOException
	 */
	private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader r = request.getReader();
		Gson gson = new Gson();
		r.mark(1);
		System.out.println(r.readLine());
		r.reset();
		LoginInfo loginInfo = gson.fromJson(r, LoginInfo.class);
		int sessionId = auth.login(loginInfo.level, loginInfo.password);

		LoginFeedbackBundle bundle = new LoginFeedbackBundle(sessionId);
		String json = gson.toJson(bundle);
		PrintWriter w = response.getWriter();
		w.println(json);
		System.out.println(json);
	    w.flush();
	    w.close();
	    response.setStatus(200);
	}

}
