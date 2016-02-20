package demoClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.swing.internal.plaf.basic.resources.basic_pt_BR;

import bundle.CritterBundle;
import bundle.CritterRequestBundle;
import bundle.CritterResponseBundle;
import bundle.EntityBundle;
import bundle.ObjBundle;
import bundle.RunBundle;
import bundle.StepBundle;
import bundle.WorldInfoBundle;
import bundle.WorldStateBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import simulation.World;
import simulation.Critter;
import simulation.Food;
import simulation.HexCoord;
import simulation.Placeable;
import simulation.Rock;

public class WorldController {
	private static double HEX_LENGTH = 30;
	private static int ROW = 20;
	private static int COL = 10;
	private static int DEFAULT_RATE = 50;
	private Hashtable<Position,HexCoord> PosiToHex = new Hashtable<Position,HexCoord>();
	private Hashtable<HexCoord,Position> HexToPosi = new Hashtable<HexCoord,Position>();
	private Hashtable<HexCoord,Placeable> HexToObj = new Hashtable<HexCoord,Placeable>();
	private Hashtable<Integer, Critter> idToCritter = new Hashtable<Integer, Critter>();
	private ArrayList<Position> positions = new ArrayList<Position>();
	private ArrayList<HexCoord> selectedHexs = new ArrayList<HexCoord>();
	private HashSet<HexCoord> changes = new HashSet<HexCoord>();
	private HexCoord selectedHex = null;
	private FileChooser fileChooser;
	private boolean executing = false;
	private World world;
	private float rate = DEFAULT_RATE;
	private int session_id;
	private int update_version;
	private int steps;
	private int population;
	private double selectedX;
	private double selectedY;
	private boolean drawed;
	public static int food_amount;
	
//	private ArrayList<HashSet<HexCoord>> versionList = new ArrayList<HashSet<HexCoord>>();
	MenuItem item1;
	MenuItem item2;
	MenuItem item3;
	MenuItem item4;
	ArrayList<MenuItem> emptyHexItem;
	//context menu on empty hex 
	final ContextMenu cm1 = new ContextMenu(); 
	//context menu on critter hex
	final ContextMenu cm2 = new ContextMenu();
	
	
	@FXML
	private VBox root_vbox;
	@FXML
	private Canvas map_canvas;
	@FXML
	private ScrollPane map_scrollpane;
	@FXML
	private Pane pane;
	@FXML
	private TextArea rules_area;
	@FXML
	private TextArea lastExecutedRule_area;
	@FXML
	private Label critterName_Label;
	@FXML
	private TextArea critterMem_area;
	@FXML
	private Label foodValue_label;
	@FXML
	private TextField critterNum_text;
	@FXML
	private TextField stepRate_text;
	@FXML
	private Label critterNum_label;
	@FXML
	private Label worldName_label;
	@FXML
	private Label executedNum_label;
    @FXML
    private Slider zoom_slider;
    Group zoomGroup;
	
    
	@FXML
	void initialize() throws IOException{
		emptyHexItem = new ArrayList<MenuItem>();
		item1 = new MenuItem("Add critter");
		item2 = new MenuItem("Add food");
		item3 = new MenuItem("Add rock");
		item4 = new MenuItem("Delete critter");
		item1.setOnAction(e ->{
			try {
				addAcritter();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		item2.setOnAction(e ->{
			try {
				addFood();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		item3.setOnAction(e->{
			try {
				addRock();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		item4.setOnAction(e->{
			try {
				deleteCritter();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		fileChooser = new FileChooser();
		System.out.println("initialization");
		map_scrollpane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
		map_scrollpane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		map_scrollpane.setStyle("-fx-background: #FFFFFF;");
		System.out.print(pane);
		map_scrollpane.setContent(pane);
//		System.out.println(LoginController.sessionId);
		
		
        zoom_slider.setMin(0.5);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(1.0);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        
        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);       
        emptyHexItem.add(item1);
        emptyHexItem.add(item2);
        emptyHexItem.add(item3);
        cm1.getItems().addAll(emptyHexItem);
        cm2.getItems().add(item4);
//        
//    	WorldStateBundle wsb = worldStateRequest();
//    	System.out.print(wsb);
//    	ROW = wsb.rows;
//		COL = wsb.cols;
//		setWorldPopulation(wsb.population);
//		setWorldsteps(wsb.current_timestep);
//		setWorldversion(wsb.current_version_number);
//		ArrayList<ObjBundle> state = wsb.state;
//		ArrayList<Integer> dead = wsb.dead_critters;
//		if(!drawed){
//    		drawHex();
//    	}
//    	drawObjs(state);
//    	removeCritters(dead);
//    	updateUI();
//   		System.out.println("updateUI");
		
//   		
//   		wsb = worldStateRequest();
//    	System.out.print(wsb);
//    	ROW = wsb.rows;
//		COL = wsb.cols;
//		setWorldPopulation(wsb.population);
//		setWorldsteps(wsb.current_timestep);
//		setWorldversion(wsb.current_version_number);
//		state = wsb.state;
//		dead = wsb.dead_critters;
//		if(!drawed){
//    		drawHex();
//    	}
//    	drawObjs(state);
//    	removeCritters(dead);
//    	updateUI();
//   		System.out.println("updateUI");
   		
        Thread thread = new Thread(){
            long timer1 =  System.currentTimeMillis();
        	public void run(){
        		while(true){
        			long current = System.currentTimeMillis();
        			if(current - timer1 > 35){
        				timer1 = current;
        				try {
    						WorldStateBundle wsb = worldStateRequest();
    						if( wsb == null || wsb.name==null){}
    						else{
    							ROW = wsb.rows;
    							COL = wsb.cols;
    							setWorldPopulation(wsb.population);
    							setWorldsteps(wsb.current_timestep);
    							setWorldversion(wsb.current_version_number);
    							ArrayList<ObjBundle> state = wsb.state;
    							ArrayList<Integer> dead = wsb.dead_critters;
    							Platform.runLater(new Runnable() { // Go back to UI/application thread
    					            public void run() {
    					                // Update UI to reflect changes to the model
    					            	if(!drawed){
    					            		drawHex();
    					            	}
    					            	drawObjs(state);
    					            	removeCritters(dead);
    					            	updateUI();
    							   		System.out.println("updateUI");
    					            }
    					        });
    						}
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
        			}
        		}
        	
        }};
        thread.setDaemon(true);
        thread.start();
        
	}
	
	
	protected void setWorldversion(int current_version_number) {
		// TODO Auto-generated method stub
		this.update_version = current_version_number;
	}


	protected void setWorldsteps(int current_timestep) {
		// TODO Auto-generated method stub
		this.steps = current_timestep;
	}


	protected void setWorldPopulation(int population2) {
		// TODO Auto-generated method stub
		this.population = population2;
	}


	private void deleteCritter() throws IOException {
		// TODO Auto-generated method stub
		double x = this.selectedX;
		double y = this.selectedY;
		HexCoord hex = getSelectedHex(x, y);
		Critter cri = (Critter)this.HexToObj.get(hex);
		int id = cri.getId();
		Gson gson = new Gson();
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/critter/"+cri.getId()+"?session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//		connection.setDoOutput(true);
		connection.setRequestMethod("DELETE");
		connection.connect();
		int responseCode = connection.getResponseCode();
		if(responseCode==401){
			AlertInfo.Unauthorized();
			return;
		}
		this.HexToObj.remove(hex);
		drawOneObj(hex,null);
		this.population--;
		updateCritterNumber();
	}


	private void addRock() throws IOException {
		// TODO Auto-generated method stub
		double x = this.selectedX;
		double y = this.selectedY;
		HexCoord hex = getSelectedHex(x,y);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/create_entity?session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		System.out.println("Doing POST "+ url);
		connection.setDoOutput(true); // send a POST message
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		EntityBundle eb = new EntityBundle(hex.getCol(), hex.getRow(), "rock");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		System.out.println(gson.toJson(eb,EntityBundle.class));
		w.println(gson.toJson(eb,EntityBundle.class));
		w.flush();
		
		int responseCode = connection.getResponseCode();
		if(responseCode==401){
			AlertInfo.Unauthorized();
			return;
		}else if(responseCode == 406){
			AlertInfo.positionToken();
			return;
		}else if(responseCode == 500){
			return;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		System.out.println(r.readLine());
		Position posi = this.HexToPosi.get(hex);
		drawRock(posi.x, posi.y);
		this.HexToObj.put(hex, new Rock());
	}


	private void addFood() throws IOException {
		// TODO Auto-generated method stub
		double x = this.selectedX;
		double y = this.selectedY;
		HexCoord hex = getSelectedHex(x,y);
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("FoodAmountDialog.fxml"));
		Parent root = loader.load();
		Stage stage = new Stage();
		stage.setTitle("Input food amount");
		stage.setScene(new Scene(root));
		stage.showAndWait();
		System.out.println("show + amount :   " +this.food_amount);
		if(this.food_amount == -1) return;
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/create_entity?session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		System.out.println("Doing POST "+ url);
		connection.setDoOutput(true); // send a POST message
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		EntityBundle eb = new EntityBundle(hex.getCol(), hex.getRow(), "food");
		eb.amount = this.food_amount;
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		System.out.println(gson.toJson(eb,EntityBundle.class));
		w.println(gson.toJson(eb,EntityBundle.class));
		w.flush();
		
		int responseCode = connection.getResponseCode();
		if(responseCode==401){
			AlertInfo.Unauthorized();
			return;
		}else if(responseCode == 406){
			AlertInfo.positionToken();;
			return;
		}else if(responseCode == 500){
			return;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		System.out.println(r.readLine());
		Position posi = this.HexToPosi.get(hex);
		drawFood(posi.x, posi.y);
		this.HexToObj.put(hex, new Food(food_amount));
		
		WorldStateBundle wsb = worldStateRequest();
		if( wsb == null || wsb.name==null){}
		else{
			ROW = wsb.rows;
			COL = wsb.cols;
			setWorldPopulation(wsb.population);
			setWorldsteps(wsb.current_timestep);
			setWorldversion(wsb.current_version_number);
			ArrayList<ObjBundle> state = wsb.state;
			ArrayList<Integer> dead = wsb.dead_critters;
            // Update UI to reflect changes to the model
          	if(!drawed){
          		drawHex();
	        }
           	drawObjs(state);
           	removeCritters(dead);
           	updateUI();
	   		System.out.println("updateUI");
		}
		
		
	}
	


	private void addAcritter() throws IOException {
		// TODO Auto-generated method stub
		double x = this.selectedX;
		double y = this.selectedY;
		HexCoord hex = getSelectedHex(x, y);
		System.out.println(x + "::" + y);
		System.out.println(hex);
		fileChooser.setTitle("Open Critter file");
		/*
		 * change to
		 * fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        	)
		 * 
		 */
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		System.out.println(root_vbox);
		File critterFile = fileChooser.showOpenDialog(root_vbox.getScene().getWindow());
		if(critterFile == null) return;
		String absolutePath = critterFile.getAbsolutePath();
		// send create critter request
		Critter critter = new Critter(absolutePath);
		
		ArrayList<HexCoord> hexList = new ArrayList<HexCoord>();
		hexList.add(hex);
		CritterRequestBundle crb = new CritterRequestBundle(critter, hexList);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/critters?session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		System.out.println("Doing POST "+ url);
		connection.setDoOutput(true); // send a POST message
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		System.out.println(gson.toJson(crb,CritterRequestBundle.class));
		w.println(gson.toJson(crb,CritterRequestBundle.class));
		w.flush();
		int responseCode = connection.getResponseCode();
		if(responseCode==401){
			AlertInfo.Unauthorized();
			return;
		}else if(responseCode == 406){
			AlertInfo.positionToken();
			return;
		}else if(responseCode == 500){
			return;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		//handle the response gson
		CritterResponseBundle creponseb = gson.fromJson(r, CritterResponseBundle.class);
		ArrayList<Integer> ids = creponseb.ids;
		String species = creponseb.species_id;

		// draw critter one these hexs
		Critter cri = new Critter(absolutePath);
		cri.setId(ids.get(0));
		cri.setPosition(hex);
		this.HexToObj.put(hex, cri);
		drawOneObj(hex,cri);
		
		this.population++;
		updateCritterNumber();
//		this.selectedHexs.clear();
	}


	/**
	 * Execute load world action,user can choose a world file
	 * 
	 * @param e mouse Event
	 * @throws IOException 
	 */
	@FXML
	void loadWorld(MouseEvent e) throws IOException{
		System.out.println(session_id);
		fileChooser.setTitle("Open World file");
		/*
		 * change to
		 * fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        	)
		 */
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
//		System.out.println(root_vbox);
		File worldFile = fileChooser.showOpenDialog(root_vbox.getScene().getWindow());
		if(worldFile == null) return;
		clearCanvas();
		String absolutePath = worldFile.getAbsolutePath();
//		String absolutePath = "D:\workspace\eclipse\12A41\examples\attackworld.txt";
		creatWorldRequest(absolutePath);
//		worldStateRequest();
		
		
//		
//		try {
//			this.world = new World(absolutePath);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		ROW = this.world.getRow();
//		COL = this.world.getCol();
//		drawHex();
//		Hashtable<HexCoord, Placeable> map = this.world.getMap();
//		Set<HexCoord> keys = map.keySet();
//		for(HexCoord hex : keys){
//			drawOneObj(hex,map.get(hex));
//		}
//		updateUI();
//		updateWorldName();
//		System.out.println("load world successfully ");
	}
	
	/**
	 * send request for creating a new world
	 * @param absolutePath the file path of the world file
	 * @throws IOException
	 */
	private void creatWorldRequest(String absolutePath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(absolutePath));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while(line!=null){
			sb.append(line);
			sb.append('\n');
			line = br.readLine();
		}
		
		Gson gson = new Gson();
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/world?session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		System.out.println("Doing POST "+ url);
		connection.setDoOutput(true); // send a POST message
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		WorldInfoBundle worldInfo = new WorldInfoBundle(sb.toString());
		System.out.println(gson.toJson(worldInfo,WorldInfoBundle.class));
		w.println(gson.toJson(worldInfo,WorldInfoBundle.class));
		w.flush();
		int responseCode = connection.getResponseCode();
		if(responseCode==401){
			AlertInfo.Unauthorized();
			return;
		}else if(responseCode == 500){
			return;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		System.out.println(r.readLine());
		System.out.println("connected");
		WorldStateBundle stateBundle = worldStateRequest();
		ROW = stateBundle.rows;
		COL = stateBundle.cols;
		this.population = stateBundle.population;
		this.steps = stateBundle.current_timestep;
		this.update_version = stateBundle.current_version_number;
		ArrayList<Integer> deadCris = stateBundle.dead_critters;
		drawHex();
		System.out.println(this.HexToPosi.get(new HexCoord(6,4)));
		ArrayList<ObjBundle> state = stateBundle.state;
		drawObjs(state);
		removeCritters(deadCris);
		updateUI();
		
	}

	private void removeCritters(ArrayList<Integer> deadCris) {
		// TODO Auto-generated method stub
		for(Integer i:deadCris){
			int id = i;
			Critter cri = this.idToCritter.get(id);
			if(cri == null) continue;
			HexCoord hex = cri.getPosition();
			this.HexToObj.remove(hex);
		}
	}


	private void drawObjs(ArrayList<ObjBundle> state) {
		// TODO Auto-generated method stub
		for(ObjBundle bundle : state){
			HexCoord hex = new HexCoord(bundle.col,bundle.row);
			Position position = this.HexToPosi.get(hex);
			System.out.println(position);
			double x = position.x;
			double y = position.y;
			if(bundle.type.equalsIgnoreCase("nothing")){
				drawOneHex(x,y);
				HexToObj.remove(hex);
			}else if(bundle.type.equalsIgnoreCase("critter")){
				Critter cri = getCritterFromBundle1(bundle);
				drawOneObj(hex,null);
				drawCritter(x,y,cri);
				// put critter in 
				HexToObj.remove(hex);
				HexToObj.put(hex, cri);
				System.out.println(hex + "critter");
			}else if(bundle.type.equalsIgnoreCase("food")){
				Food food = getFoodFromBundle(bundle);
				drawOneObj(hex,null);
				drawFood(x,y);
				HexToObj.remove(hex);
				HexToObj.put(hex, food);
			}else if(bundle.type.equalsIgnoreCase("rock")){
				drawOneObj(hex,null);
				drawRock(x,y);
				HexToObj.remove(hex);
				HexToObj.put(hex, new Rock());
			}
		}
	}


	private Food getFoodFromBundle(ObjBundle bundle) {
		// TODO Auto-generated method stub
//		System.out.println(bundle);
		Food food = new Food(bundle.value);
		return food;
	}


	private Critter getCritterFromBundle1(ObjBundle bundle) {
		// TODO Auto-generated method stub		
		Critter cri = new Critter();
		cri.setId(bundle.id);
		cri.setName(bundle.species_id);
		cri.setDirection(bundle.direction);
		for(int i = 0;i < bundle.mem.length;i++){
			cri.setMem(i, bundle.mem[i]);
		}
		System.out.println("cri : " + cri);
		System.out.println("bundle "+ bundle);
		cri.setLastRule(bundle.recently_executed_rule);
		
		cri.setStrProgram(bundle.program);
		return cri;
	}


	private WorldStateBundle worldStateRequest() throws IOException{

		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/world?update_since="
				+this.update_version+"&session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		System.out.println("Doing GET " + url);
		if(connection.getResponseCode() == 500){
			return null;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(
		connection.getInputStream()));
		Gson gson = new GsonBuilder().create();
		WorldStateBundle worldState = gson.fromJson(r, WorldStateBundle.class);
		return worldState;
		
	}
	

	/**
	 * Load a random world
	 * @param e
	 * @throws IOException
	 */
	@FXML
	void randomWorld(MouseEvent e) throws IOException{
		clearCanvas();
		Hashtable<HexCoord, Placeable> map;
		Set<HexCoord> keys;
		this.world = new World();
		ROW = this.world.getRow();
		COL = this.world.getCol();
		drawHex();
		map = this.world.getMap();
		keys = map.keySet();
		for(HexCoord hex : keys){
			drawOneObj(hex,map.get(hex));
		}
		
		updateUI();
//		updateCritterNumber();
//		updateExecutedStep();
		updateWorldName();
		System.out.println("random world");
	}
	
	/**
	 * add critter from UI
	 * @param e
	 * @throws IOException
	 */
	@FXML
	void loadCritter(MouseEvent e) throws IOException{
//		boolean validWorld = checkWorld();
//		if(!validWorld) return;
		int num = getCritterNum();
		if(num < 0) return;
		fileChooser.setTitle("Open Critter file");
		/*
		 * change to
		 * fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        	)
		 * 
		 */
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		System.out.println(root_vbox);
		File critterFile = fileChooser.showOpenDialog(root_vbox.getScene().getWindow());
		if(critterFile == null) return;
		String absolutePath = critterFile.getAbsolutePath();
		// send create critter request
		Critter critter = new Critter(absolutePath);
		if(this.selectedHexs.isEmpty()){
			// load random position critter
			int i = num * 4;
			Random rdn = new Random();
			while(i>0){
				i--;
				int c = rdn.nextInt(this.COL);
				int r = rdn.nextInt(this.ROW);
				HexCoord hex = new HexCoord(c,r);
				if(validHex(hex)){
					this.selectedHexs.add(hex);
				}
				if(this.selectedHexs.size()==num)
					break;
			}
		}
		
		
//		HashSet<HexCoord> addPosi = this.world.loadCritter(absolutePath, num);
		CritterRequestBundle crb = new CritterRequestBundle(critter, this.selectedHexs);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/critters?session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		System.out.println("Doing POST "+ url);
		connection.setDoOutput(true); // send a POST message
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		System.out.println(gson.toJson(crb,CritterRequestBundle.class));
		w.println(gson.toJson(crb,CritterRequestBundle.class));
		w.flush();
		int responseCode = connection.getResponseCode();
		if(responseCode==401){
			AlertInfo.Unauthorized();
			return;
		}else if(responseCode == 500){
			return;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		//handle the response gson
		CritterResponseBundle creponseb = gson.fromJson(r, CritterResponseBundle.class);
		ArrayList<Integer> ids = creponseb.ids;
		String species = creponseb.species_id;
		// clean all the selectedhexs from ui
		for(HexCoord hex: this.selectedHexs){
			drawOneObj(hex, null);
		}
		// draw critter one these hexs
		for(int i = 0; i < ids.size();i++){
			Critter cri = new Critter(absolutePath);
			cri.setId(ids.get(i));
			HexCoord hex = this.selectedHexs.get(i);
			cri.setPosition(hex);
			this.HexToObj.put(hex, cri);
			this.idToCritter.put(cri.getId(), cri);
			drawOneObj(hex,cri);
		}
		this.population = this.population + this.selectedHexs.size();
		updateCritterNumber();
		this.selectedHexs.clear();
		
//		System.out.println(this.world.getCritterNumber());
//		for(HexCoord hex : addPosi){
//			drawOneObj(hex, this.world.getMap().get(hex));
//		}
//		updateCritterNumber();
//		System.out.println("load critter");
	}
	
	/**
	 * if this hex is valid 1,in scope, 2,not token
	 * @param hex
	 * @return hex is valid
	 */
	private boolean validHex(HexCoord hex) {
		// TODO Auto-generated method stub
		int c = hex.getCol();
		int r = hex.getRow();
		if( c < 0 || r < 0) return false;
		if(c >= this.COL || r >= this.ROW) return false;
		if(2 * r - c < 0 || 2 * r - c >= 2 * this.ROW - this.COL) return false;
		if(this.HexToObj.get(hex)!=null) return false;
		return true;

	}


	/**
	 * handle event of clicking map
	 * 
	 * @param e
	 * @throws IOException 
	 */
	@FXML
	void mapClicked(MouseEvent e) throws IOException{
//		if(this.world==null) return;
//		boolean validWorld = checkWorld();
//		if(!validWorld) return;
		if(e.getButton() == MouseButton.SECONDARY){
			//set context menu
			this.selectedX = e.getX();
			this.selectedY = e.getY();
			System.out.println(e.getX() + "::" +e.getY());
			HexCoord hex = getSelectedHex(e.getX(), e.getY());
			System.out.println("hex:  " + hex);
			if(this.HexToObj.get(hex) == null){
				cm1.show(map_canvas, e.getScreenX(),
						e.getScreenY());	
			}else if(this.HexToObj.get(hex) instanceof Critter){
				cm2.show(map_canvas, e.getScreenX(), e.getScreenY());
			}
			
			
			System.out.println("right click");
//			loadAcritter(e.getX(),e.getY());
		}else{
			System.out.println(e.getX() + ":    " + e.getY());
			HexCoord hex = getSelectedHex(e.getX(), e.getY());
			System.out.println(hex);
			if(hex==null) return;
			// whether this hex has no object
			if(this.HexToObj.get(hex)==null){
				this.selectedHexs.add(hex);
				selectHex(hex);
			}else if(this.selectedHexs.isEmpty()){
				showHexInfo(hex);
				System.out.println(hex);
				System.out.println("mapclicked");				
			}else{
				//toggle that no object shoule be add here
				AlertInfo.positionToken();
			}

		}

	}
	
	
	/**
	 * update ui for selected hex
	 * @param hex the hex to be updated
	 */
	private void selectHex(HexCoord hex) {
		// TODO Auto-generated method stub
		Position posi = this.HexToPosi.get(hex);
		double x = posi.x;
		double y = posi.y;
		
		double x1 = x - HEX_LENGTH;
		double x2 = x - HEX_LENGTH/2;
		double x3 = x + HEX_LENGTH/2;
		double x4 = x + HEX_LENGTH;
		double x5 = x + HEX_LENGTH/2;
		double x6 = x - HEX_LENGTH/2;
		double y1 = y;
		double y2 = y - HEX_LENGTH * Math.sqrt(3) / 2;
		double y3 = y2;
		double y4 = y;
		double y5 = y + HEX_LENGTH * Math.sqrt(3) / 2;
		double y6 = y5;
		if(map_canvas.getWidth() < x4){
			map_canvas.setWidth(2 * x4);
		}
		if(map_canvas.getHeight() < y6){
			map_canvas.setHeight(2 * y6);
		}
		
//		System.out.println(map_canvas.getWidth());
		GraphicsContext gc = map_canvas.getGraphicsContext2D();
//		gc.setFill(Color.WHITE);
		gc.setFill(Color.BLUEVIOLET);
		gc.setStroke(Color.BLUE);
		gc.fillPolygon(new double[]{x1,x2,x3,x4,x5,x6},
				new double[]{y1,y2,y3,y4,y5,y6}, 6);
		gc.strokePolygon(new double[]{x1,x2,x3,x4,x5,x6},
				new double[]{y1,y2,y3,y4,y5,y6}, 6);
		
		
	}


	/**
	 * Execute step action
	 * 
	 * @param e
	 * @throws IOException 
	 */
	@FXML
	void stepExecute(MouseEvent e) throws IOException{
//		boolean validWorld = checkWorld();
//		if(!validWorld) return;
//		System.out.println("step");
//		world.execute(1);
//		Hashtable<HexCoord, Placeable> map = this.world.getMap();
//		HashSet<HexCoord> worldchange = this.world.getChange();
////		this.versionList.add(worldchange);
//		System.out.println(changes);
//		for(HexCoord hex : worldchange){
//			this.changes.add(hex);
//		}
//		updateUI();
		//send step execute request
		Gson gson = new Gson();
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/step?session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		StepBundle stepBundle = new StepBundle();
		stepBundle.count = 0;
		String json = gson.toJson(stepBundle, StepBundle.class);
		w.println(json);
		w.flush();
		System.out.println("Doing Post step " + url);
		
		if(connection.getResponseCode() == 406){
			AlertInfo.NotAccepted();
		}else if(connection.getResponseCode() == 401){
			AlertInfo.Unauthorized();
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(
		connection.getInputStream()));
		System.out.println("step :  " + r.readLine());
		
		WorldStateBundle wsb = worldStateRequest();
		if( wsb == null || wsb.name==null){}
		else{
			ROW = wsb.rows;
			COL = wsb.cols;
			setWorldPopulation(wsb.population);
			setWorldsteps(wsb.current_timestep);
			setWorldversion(wsb.current_version_number);
			ArrayList<ObjBundle> state = wsb.state;
			ArrayList<Integer> dead = wsb.dead_critters;
            // Update UI to reflect changes to the model
          	if(!drawed){
          		drawHex();
	        }
           	drawObjs(state);
           	removeCritters(dead);
           	updateUI();
	   		System.out.println("updateUI");
		}
	}
	
	/**
	 * Start world execution 
	 * 
	 * @param e
	 * @throws IOException 
	 */
	@FXML
	void startExecute(MouseEvent e) throws IOException{
		
		rate = getStepRate();
		if(rate < -1) return;
		if(rate == -1){
			rate = DEFAULT_RATE;
		}
		
		Gson gson = new Gson();
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/run?session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		RunBundle rb = new RunBundle(rate);
		String json = gson.toJson(rb, RunBundle.class);
		w.println(json);
		w.flush();
		System.out.println("Doing Post start " + url);
		
		if(connection.getResponseCode() == 406){
			AlertInfo.NotAccepted();
			return;
		}else if(connection.getResponseCode() == 401){
			AlertInfo.Unauthorized();
			return;
		}else if(connection.getResponseCode() == 500){
			return;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(
		connection.getInputStream()));
		System.out.println("step :  " + r.readLine());

	}
	
	
	/**
	 * Stop world execution
	 * 
	 * @param e
	 * @throws IOException 
	 */
	@FXML
	void stopExecute(MouseEvent e) throws IOException{
		
		Gson gson = new Gson();
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/run?session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		RunBundle rb = new RunBundle(0);
		String json = gson.toJson(rb, RunBundle.class);
		w.println(json);
		w.flush();
		System.out.println("Doing Post start " + url);
		
		if(connection.getResponseCode() == 406){
			AlertInfo.NotAccepted();
			return;
		}else if(connection.getResponseCode() == 401){
			AlertInfo.Unauthorized();
			return;
		}else if(connection.getResponseCode() == 500){
			return;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(
		connection.getInputStream()));
		System.out.println("step :  " + r.readLine());
		
//		
//		
//		boolean validWorld = checkWorld();
//		if(!validWorld) return;
//		if(executing)
//			executing = false;
//		System.out.println("stop");
	}
	
	/**
	 * Zoom in the map	
	 * @param event
	 */
    @FXML
    void zoomIn(ActionEvent event) {
    	System.out.println("airportapp.Controller.zoomIn");
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal += 0.1);
    }

    /**
     * Zoom out the map
     * @param event
     */
    @FXML
    void zoomOut(ActionEvent event) {
    	System.out.println("airportapp.Controller.zoomOut");
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + -0.1);
    }

    /**
     * Execute zoom action
     * @param scaleValue the scaleValue for map_scrollpane to scale
     */
    private void zoom(double scaleValue) {
//    System.out.println("WorldController.zoom, scaleValue: " + scaleValue);
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    
    public void setId(int id){
    	this.session_id = id;
    }
    
	/**
	 * Draw one object in the world given hexcoord 
	 * 
	 * @param c the column of hexcoord
	 * @param j the row of hexcoord
	 * @param dir the direction of the object, specularly for critter
	 */
	private void drawOneObj(HexCoord hex, Placeable obj) {
		// TODO Auto-generated method stub
		//when col is even number
		System.out.println(hex);
		Position position = this.HexToPosi.get(hex);
		double x = position.x;
		double y = position.y;
		if(obj == null){
			drawOneHex(x,y);
		}else if(obj instanceof Critter){
			drawOneObj(hex,null);
			drawCritter(x,y,(Critter)obj);
		}
		else if(obj instanceof Food){
			drawOneObj(hex,null);
			drawFood(x,y);
		}
		else{
			drawOneObj(hex,null);
			drawRock(x,y);
		}
	}

	/**
	 * draw a rock to a specific location
	 * 
	 * @param x the x coordinate of the location
	 * @param y the y coordinate of the location
	 */
	private void drawRock(double x, double y) {
		// TODO Auto-generated method stub
		GraphicsContext gc = map_canvas.getGraphicsContext2D();
		gc.setFill(Color.RED);
		double side = HEX_LENGTH * 1.5;
		double x1 = x - side / 2;
		double x2 = x + side / 2;
		double x3 = x;
		double y1 = y + side / 2 / Math.sqrt(3);
		double y2 = y1;
		double y3 = y1 - side / 2 * Math.sqrt(3);
		gc.fillPolygon(new double[]{x1,x2,x3}, new double[]{y1,y2,y3}, 3);
	}

	/**
	 * Draw food to a specific location
	 * 
	 * @param x the x coordinate of the location
	 * @param y the y coordinate of the location
	 */
	private void drawFood(double x, double y) {
		// TODO Auto-generated method stub
		GraphicsContext gc = map_canvas.getGraphicsContext2D();
		gc.setFill(Color.GREEN);
		double x1 = x - HEX_LENGTH/2;
		double x2 = x + HEX_LENGTH/2;
		double y1 = y - HEX_LENGTH/2;
		double y2 = y + HEX_LENGTH/2;
		gc.fillPolygon(new double[]{x1,x2,x2,x1}, new double[]{y1,y1,y2,y2}, 4);
	}

	/**
	 * Draw a critter to a specific location
	 * 
	 * @param x the x coordinate of location
	 * @param y the y coordinate of location
	 * @param cri the critter to be drawn
	 */
	private void drawCritter(double x, double y, Critter cri) {
		// TODO Auto-generated method stub
		GraphicsContext gc = map_canvas.getGraphicsContext2D();
		int posture = cri.getMem(7);
		Color color = getPostureColor(posture);
		gc.setFill(color);
		int size = cri.getMem(3);
		double r = HEX_LENGTH / 3;
		r = r + size/0.5;
		r = Math.min(r, HEX_LENGTH / 3 * 2);
		double x1 = x - r;
		double y1 = y - r;
		gc.fillOval(x1, y1, 2*r, 2*r);
		int dir = cri.getDirection();
		double theta = 2 * Math.PI / 6 * (3 - dir);
		double delta = 2 * Math.PI / 10;
		double theta2 = theta + delta;
		double eyeR = r / 3;
		double eye1x = x + r * Math.sin(theta + delta/2);
		double eye1y = y + r * Math.cos(theta + delta/2);
		double eye1x1 = eye1x - eyeR;
		double eye1y1 = eye1y - eyeR;
		gc.strokeOval(eye1x1, eye1y1, 2 * eyeR, 2 * eyeR);
		
		double eye2x = x + r * Math.sin(theta - delta/2);
		double eye2y = y + r * Math.cos(theta - delta/2);
		double eye2x1 = eye2x - eyeR;
		double eye2y1 = eye2y - eyeR;
		gc.strokeOval(eye2x1, eye2y1, 2 * eyeR, 2 * eyeR);
		
	}
	
	
	/**
	 * Get the color according to critter's posture
	 * 
	 * @param posture critter's posture
	 * @return color that corresponding to critter
	 */
	private Color getPostureColor(int posture) {
		// TODO Auto-generated method stub
		double n1 = posture/10;
		double n2 = posture%10;
		double n3 = (n1 + n2) / 2;
		return Color.color(n1/10, n2/10, n3/10);
	}

	/**
	 * draw the hex map of map size ROW * COL
	 */
	private void drawHex(){
//		int col = this.world.getCol();
		int col = COL;
		if(col % 2 ==0){
			drawEvenColHex();
		}else{
			drawOddColHex();
		}
		this.drawed = true;
	}
	
	/**
	 * draw the hex map of odd column number
	 */
	private void drawOddColHex() {
		// TODO Auto-generated method stub
//		int col = this.world.getCol(); // col is odd
//		int row = this.world.getRow();
		int col = COL;
		int row = ROW;
		int drawRol = row - col/2;
		for(int i = 0; i < col; i++){
			if(i % 2 ==0){
				// 0, 2, 4,... has one more hex
				double dy = HEX_LENGTH * Math.sqrt(3) / 2;
				double offsetY = 2 * dy;
				double y = dy;
				double x = 0;
				for(int j = 0; j <= drawRol; j++){
					x = (i+1) * 1.5 * HEX_LENGTH - 0.5 * HEX_LENGTH;
					drawOneHex(x,y);
					int hexC = (i+1)/2 +drawRol-j;
					int hexR = i;
//					System.out.println("i: " + (i) + "j: " + ((i+1)/2 +drawRol-j));
					Position posi = new Position(x,y);
					HexCoord hex = new HexCoord(hexC, hexR);
					this.PosiToHex.put(posi, hex);
					this.HexToPosi.put(hex, posi);
					this.positions.add(posi);
					y += offsetY;
				}
			}else{
				// 1, 3, 5 has one less hex each column
				double dy = HEX_LENGTH * Math.sqrt(3) / 2;
				double offsetY = 2 * dy;
				double y = dy * 2;
				double x = HEX_LENGTH;
				for(int j = 0; j <= drawRol - 1; j++){
					x = (i+1) * 1.5 * HEX_LENGTH - 0.5 * HEX_LENGTH;
					drawOneHex(x,y);
					int hexC = (i+1)/2 +drawRol-j-1;
					int hexR = i;
					Position posi = new Position(x,y);
					HexCoord hex = new HexCoord(hexC, hexR);
					this.PosiToHex.put(posi, hex);
					this.HexToPosi.put(hex, posi);
					this.positions.add(posi);
					y += offsetY;
//					System.out.println("i: " + (i) + "j: " + ((i+1)/2 +drawRol-j-1));
				}
				
			}
		}
	}

	/**
	 * draw the hex map of even column number
	 */
	private void drawEvenColHex() {
		// TODO Auto-generated method stub
		int col = COL;
		int row = ROW;
		int drawRol = row - col / 2;
		for(int i = 0; i < col; i++){
			if(i % 2 ==0){
				// 0, 2, 4,... has one more hex
				double dy = HEX_LENGTH * Math.sqrt(3) / 2;
				double offsetY = 2 * dy;
				double y = 2 * dy;
				double x = 0;
				for(int j = 0; j < drawRol; j++){
//					System.out.println(j);
					x = (i+1) * 1.5 * HEX_LENGTH - 0.5 * HEX_LENGTH;
					drawOneHex(x,y);
					int hexC = (i+1)/2 +drawRol-j-1;
					int hexR = i;
//					System.out.println("i: " + (i) + "j: " + ((i+1)/2 +drawRol-j));
					Position posi = new Position(x,y);
					HexCoord hex = new HexCoord(hexR, hexC);
					this.PosiToHex.put(posi, hex);
					this.HexToPosi.put(hex, posi);
					System.out.println(hex);
					System.out.println(posi);
					this.positions.add(posi);
					y += offsetY;
				}
			}else{
				// 1, 3, 5 has one less hex each column
				double dy = HEX_LENGTH * Math.sqrt(3) / 2;
				double offsetY = 2 * dy;
				double y = dy;
				double x = HEX_LENGTH;
				for(int j = 0; j < drawRol; j++){
					x = (i+1) * 1.5 * HEX_LENGTH - 0.5 * HEX_LENGTH;
					drawOneHex(x,y);
//					System.out.println("i: " + (i) + "j: " + ((i+1)/2 +drawRol-j));
					int hexC = (i+1)/2 +drawRol-j -1;
					int hexR =  i;
					Position posi = new Position(x,y);
					HexCoord hex = new HexCoord(hexR, hexC);
					this.PosiToHex.put(posi, hex);
					this.HexToPosi.put(hex, posi);
					this.positions.add(posi);
					y += offsetY;
				}
				
			}
		}
	}

	/**
	 * draw one hex give x, y, and side length of hexagon
	 * 
	 * @param x the x position of center of the hexagon
	 * @param y the y position of the center of the hexagon
	 * @param length the side length of hexagon
	 */
	private void drawOneHex(double x, double y){
//		System.out.println("x: " + x + "y: " + y);
		// construct hexgon with counter-clock-wise
		double x1 = x - HEX_LENGTH;
		double x2 = x - HEX_LENGTH/2;
		double x3 = x + HEX_LENGTH/2;
		double x4 = x + HEX_LENGTH;
		double x5 = x + HEX_LENGTH/2;
		double x6 = x - HEX_LENGTH/2;
		double y1 = y;
		double y2 = y - HEX_LENGTH * Math.sqrt(3) / 2;
		double y3 = y2;
		double y4 = y;
		double y5 = y + HEX_LENGTH * Math.sqrt(3) / 2;
		double y6 = y5;
		if(map_canvas.getWidth() < x4){
			map_canvas.setWidth(2 * x4);
		}
		if(map_canvas.getHeight() < y6){
			map_canvas.setHeight(2 * y6);
		}
		
//		System.out.println(map_canvas.getWidth());
		GraphicsContext gc = map_canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.setStroke(Color.BLACK);
		gc.fillPolygon(new double[]{x1,x2,x3,x4,x5,x6},
				new double[]{y1,y2,y3,y4,y5,y6}, 6);
		gc.strokePolygon(new double[]{x1,x2,x3,x4,x5,x6},
				new double[]{y1,y2,y3,y4,y5,y6}, 6);
//		System.out.println(x + ":" + y);
	}
	

	/**
	 * Show the information of the selected hex
	 * @param Hex the selected hex
	 * @throws IOException 
	 */
	private void showHexInfo(HexCoord Hex) throws IOException {
		// TODO Auto-generated method stub
//		Hashtable<HexCoord,Placeable> hs = this.world.getMap();
		Placeable obj = HexToObj.get(Hex);
		if(obj == null || obj instanceof Rock){
			rules_area.setText("");
			lastExecutedRule_area.setText("");
			critterName_Label.setText("");
			critterMem_area.setText("");
			foodValue_label.setText("");
			return;
		} 
		if(obj instanceof Food){
			showFoodInfo((Food)obj);
		}else if(obj instanceof Critter){
			retriveACritter((Critter) obj);
//			showCritterInfo((Critter)obj);
		}
	}

	private void retriveACritter(Critter obj) throws IOException {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/critter/"+obj.getId()+"?session_id=" + this.session_id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		System.out.println("Doing Get "+ url);
		int responseCode = connection.getResponseCode();
		if(responseCode==401){
			AlertInfo.Unauthorized();
			return;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
//		String line = r.readLine();
//		while(line != null){
//			System.out.println(line);
//			line = r.readLine();
//		}
		
		showCritterInfo(r);
		
		
		
//		System.out.println(r.readLine());
//		System.out.println("connected");
//		WorldStateBundle stateBundle = worldStateRequest();
//		ROW = stateBundle.rows;
//		COL = stateBundle.cols;
//		drawHex();
//		System.out.println(this.HexToPosi.get(new HexCoord(6,4)));
//		ArrayList<ObjBundle> state = stateBundle.state;
//		drawObjs(state);
//		updateUI();
//		
		
		
	}


	private void showCritterInfo(BufferedReader r) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		CritterBundle cb = gson.fromJson(r, CritterBundle.class);
		
		foodValue_label.setText("");
		StringBuilder sb = new StringBuilder();
		rules_area.setText(cb.program);
		lastExecutedRule_area.setText(""+cb.recently_executed_rule);
		System.out.println(critterName_Label);
		critterName_Label.setText(cb.species_id);
		
		critterMem_area.setText(memToString(cb.mem));
		
		
	}


	private String memToString(int[] mem) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("memsize: "); sb.append(mem[0]); sb.append('\n');
		sb.append("defense: "); sb.append(mem[1]); sb.append('\n');
		sb.append("offense: "); sb.append(mem[2]); sb.append('\n');
		sb.append("size: "); sb.append(mem[3]); sb.append('\n');
		sb.append("energy: "); sb.append(mem[4]); sb.append('\n');
		sb.append("pass: "); sb.append(mem[5]); sb.append('\n');
		sb.append("tag: "); sb.append(mem[6]); sb.append('\n');
		sb.append("posture: "); sb.append(mem[7]); sb.append('\n');
		return sb.toString();
	}


	/**
	 * Show critter information
	 * 
	 * @param cri
	 */
	private void showCritterInfo(Critter cri) {
		// TODO Auto-generated method stub
		foodValue_label.setText("");
		StringBuilder sb = new StringBuilder();
		cri.getRules().prettyPrint(sb);
//		System.out.println(rules_area);
		rules_area.setText(sb.toString());
		sb.delete(0, sb.length());
		cri.getRules().getRule(cri.getLastRuleIndex()).prettyPrint(sb);
		lastExecutedRule_area.setText(sb.toString());
		System.out.println(critterName_Label);
		critterName_Label.setText(cri.getName());
		sb.delete(0, sb.length());
		sb = cri.printMem();
		critterMem_area.setText(sb.toString());
	}

	/**
	 * show food information
	 * 
	 * @param food 
	 */
	private void showFoodInfo(Food food) {
		// TODO Auto-generated method stub
		rules_area.setText("");
		lastExecutedRule_area.setText("");
		critterName_Label.setText("");
		critterMem_area.setText("");
		this.foodValue_label.setText(""+food.getFoodValue());
	}

	/**
	 * load critter to a given location
	 * 
	 * @param x the x position of location
	 * @param y the y position of the location
	 * @throws IOException
	 */
	private void loadAcritter(double x, double y) throws IOException {
		// TODO Auto-generated method stub

		this.selectedHex = getSelectedHex(x, y);
		if(this.selectedHex == null){
			AlertInfo.invalidPositionAlert();
			return;
		}
		if(this.world.getMap().get(selectedHex)!=null){
			AlertInfo.positionToken();
			return;
		}
		
		fileChooser.setTitle("Open Critter file");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		System.out.println(root_vbox);
		File critterFile = fileChooser.showOpenDialog(root_vbox.getScene().getWindow());
		if(critterFile == null) return;
		String absolutePath = critterFile.getAbsolutePath();
		Critter critter = new Critter(absolutePath);
		boolean succ = world.addObj(critter, selectedHex);
		System.out.println(succ);
		System.out.println(this.world.getCritterNumber());
		if(succ){
			drawOneObj(selectedHex, critter);
			System.out.println(this.world.getCritterNumber());
		}
	}
	

	
	/**
	 * get the rate input
	 * @return rate
	 */
	private float getStepRate() {
		// TODO Auto-generated method stub
		float number = 0;
		try{
			if(stepRate_text.getText().trim().equals(""))
				return -1;
			number = Float.parseFloat(stepRate_text.getText());
			if(number<0) AlertInfo.invalidNumber();
		}catch (NumberFormatException e){
			AlertInfo.invalidNumber();
			number = -2;
		}
		return number < 0?-2:number;
	}

	/**
	 * update the canvas
	 */
	private void updateUI(){
//		Hashtable<HexCoord, Placeable> map = world.getMap();
////		foodValue_label.setText(""+j);
//		for(HexCoord hex : changes){
//			drawOneObj(hex,map.get(hex));
//		}
		changes.clear();
		updateExecutedStep();
		updateCritterNumber();
		
	}
	
	/**
	 * update the executed step
	 */
	private void updateExecutedStep() {
		// TODO Auto-generated method stub
		executedNum_label.setText(""+this.steps);
	}


	
	/**
	 * Check the world
	 * @return true if world exists
	 */
	private boolean checkWorld(){
		if(this.world == null){
			AlertInfo.noWolrdAlert();
			return false;
		}
		return true;
	}

	

	
	/**
	 * update name of the world
	 */
	private void updateWorldName() {
		// TODO Auto-generated method stub
		worldName_label.setText(this.world.getName());
	}


	/**
	 * Clear canvas, usually be called when load a new worlds
	 */
	private void clearCanvas() {
		// TODO Auto-generated method stub
		
		GraphicsContext gc = this.map_canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, this.map_canvas.getWidth(), this.map_canvas.getHeight());
		this.PosiToHex.clear();
		this.HexToPosi.clear();
	}


	
	/**
	 * update the label of number of steps executed 
	 */
	private void updateCritterNumber() {
		// TODO Auto-generated method stub
		critterNum_label.setText(""+this.population);
	}

	/**
	 * get the load critter number input
	 * @return
	 */
	private int getCritterNum() {
		// TODO Auto-generated method stub
		int number = 0;
		try{
			if(critterNum_text.getText().trim().equals(""))
				return 1;
			number = Integer.parseInt(critterNum_text.getText());
			if(number<0) AlertInfo.invalidNumber();
		}catch (NumberFormatException e){
			AlertInfo.invalidNumber();
			number = -1;
		}
		return number;
	}

	/**
	 * Get the corresponding Hexcoord by the x,y position
	 * 
	 * @param x the x position of the point
	 * @param y the y position of the point
	 * @return the corresponding hexcoord
	 */
	private HexCoord getSelectedHex(double x, double y) {
		// TODO Auto-generated method stub
		double length = Double.MAX_VALUE;
		Position minPosi = null;
		for(Position posi:this.positions){
			double distance = posi.dist(x, y);
			if(distance <= HEX_LENGTH * Math.sqrt(3) / 2){
				return this.PosiToHex.get(posi);
			}
			if(distance <= HEX_LENGTH && distance <=length){
				minPosi = posi;
			}
		}
		if(minPosi == null) return null;
		return this.PosiToHex.get(minPosi);
	}
	

}
