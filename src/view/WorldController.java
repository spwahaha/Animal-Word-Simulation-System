package view;

import java.awt.Button;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
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
	private HashMap<Position,HexCoord> PosiToHex = new HashMap<Position,HexCoord>();
	private HashMap<HexCoord,Position> HexToPosi = new HashMap<HexCoord,Position>();
	private ArrayList<Position> positions = new ArrayList<Position>();
	private HashSet<HexCoord> changes = new HashSet<HexCoord>();
	private HexCoord selectedHex = null;
	private FileChooser fileChooser;
	private boolean executing = false;
	private World world;
	private int rate = DEFAULT_RATE;
	
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
	void initialize(){
		fileChooser = new FileChooser();
		System.out.println("initialization");
		map_scrollpane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
		map_scrollpane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		map_scrollpane.setStyle("-fx-background: #FFFFFF;");
		System.out.print(pane);
		map_scrollpane.setContent(pane);
		
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

	}
	
	
	/**
	 * Execute load world action,user can choose a world file
	 * 
	 * @param e mouse Event
	 */
	@FXML
	void loadWorld(MouseEvent e){

		fileChooser.setTitle("Open World file");
		/*
		 * change to
		 * fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        	)
		 */
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		System.out.println(root_vbox);
		File worldFile = fileChooser.showOpenDialog(root_vbox.getScene().getWindow());
		if(worldFile == null) return;
		clearCanvas();
		String absolutePath = worldFile.getAbsolutePath();
//		String absolutePath = "D:\workspace\eclipse\12A41\examples\attackworld.txt";
		try {
			this.world = new World(absolutePath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ROW = this.world.getRow();
		COL = this.world.getCol();
		drawHex();
		Hashtable<HexCoord, Placeable> map = this.world.getMap();
		Set<HexCoord> keys = map.keySet();
		for(HexCoord hex : keys){
			drawOneObj(hex,map.get(hex));
		}
		updateUI();
//		updateCritterNumber();
//		updateExecutedStep();
		updateWorldName();
		System.out.println("load world successfully ");
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
		boolean validWorld = checkWorld();
		if(!validWorld) return;
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
		Critter critter = new Critter(absolutePath);
		HashSet<HexCoord> addPosi = this.world.loadCritter(absolutePath, num);
		System.out.println(this.world.getCritterNumber());
		for(HexCoord hex : addPosi){
			drawOneObj(hex, this.world.getMap().get(hex));
		}
		updateCritterNumber();
		System.out.println("load critter");
	}
	
	
	/**
	 * handle event of clicking map
	 * 
	 * @param e
	 * @throws IOException 
	 */
	@FXML
	void mapClicked(MouseEvent e) throws IOException{
		if(this.world==null) return;
//		boolean validWorld = checkWorld();
//		if(!validWorld) return;
		if(e.getButton() == MouseButton.SECONDARY){
			System.out.println("right click");
			loadAcritter(e.getX(),e.getY());
		}else{
			System.out.println(e.getX() + ":    " + e.getY());
			this.selectedHex = getSelectedHex(e.getX(), e.getY());
			System.out.println(selectedHex);
			if(this.selectedHex==null) return;
			showHexInfo(this.selectedHex);
			System.out.println(this.selectedHex);
			System.out.println("mapclicked");
		}

	}
	
	
	
	/**
	 * Execute step action
	 * 
	 * @param e
	 */
	@FXML
	void stepExecute(MouseEvent e){
		boolean validWorld = checkWorld();
		if(!validWorld) return;
		System.out.println("step");
		world.execute(1);
		Hashtable<HexCoord, Placeable> map = this.world.getMap();
		HashSet<HexCoord> worldchange = this.world.getChange();
		System.out.println(changes);
		for(HexCoord hex : worldchange){
			this.changes.add(hex);
		}
		updateUI();
	}
	
	/**
	 * Start world execution 
	 * 
	 * @param e
	 */
	@FXML
	void startExecute(MouseEvent e){
		boolean validWorld = checkWorld();
		if(!validWorld) return;
		System.out.println("start now" + validWorld);
		rate = getStepRate();
		if(rate < -1) return;
		if(rate == -1){
			rate = DEFAULT_RATE;
		}
		if(!validWorld) return;
		executing = true;
		if(rate==0) return;
		new Thread() { // Create a new background process	
			public void run() {
				long start = System.currentTimeMillis();
				while(executing){
					if(!executing){
						
						break;
					} 
					if(rate==0){
						 continue;
						 
					}
					try {
						Thread.sleep(1000/rate);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					world.execute(1);
					HashSet<HexCoord> worldChanges = world.getChange();
					for(HexCoord hex:worldChanges){
						changes.add(hex);
					}
				   	System.out.println("execute");    
				   	long current = System.currentTimeMillis();

				   	if(current - start > 35){
				   		start = current;
				   		Platform.runLater(new Runnable() { // Go back to UI/application thread
				            public void run() {
				                // Update UI to reflect changes to the model
						   		updateUI();
						   		System.out.println("updateUI");
				            }
				        });
				   	} 

			    }
				System.out.println("jump out");
			}
		}.start(); // Starts the background thread!

	}
	
	
	/**
	 * Stop world execution
	 * 
	 * @param e
	 */
	@FXML
	void stopExecute(MouseEvent e){
		boolean validWorld = checkWorld();
		if(!validWorld) return;
		if(executing)
			executing = false;
		System.out.println("stop");
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
					int hexC = i;
					int hexR = (i+1)/2 +drawRol-j;
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
					int hexC = i;
					int hexR = (i+1)/2 +drawRol-j-1;
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
					int hexC = i;
					int hexR = (i+1)/2 +drawRol-j-1;
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
				double y = dy;
				double x = HEX_LENGTH;
				for(int j = 0; j < drawRol; j++){
					x = (i+1) * 1.5 * HEX_LENGTH - 0.5 * HEX_LENGTH;
					drawOneHex(x,y);
//					System.out.println("i: " + (i) + "j: " + ((i+1)/2 +drawRol-j));
					int hexC = i;
					int hexR = (i+1)/2 +drawRol-j -1 ;
					Position posi = new Position(x,y);
					HexCoord hex = new HexCoord(hexC, hexR);
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
	 */
	private void showHexInfo(HexCoord Hex) {
		// TODO Auto-generated method stub
		Hashtable<HexCoord,Placeable> hs = this.world.getMap();
		Placeable obj = hs.get(Hex);
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
			showCritterInfo((Critter)obj);
		}
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
	private int getStepRate() {
		// TODO Auto-generated method stub
		int number = 0;
		try{
			if(stepRate_text.getText().trim().equals(""))
				return -1;
			number = Integer.parseInt(stepRate_text.getText());
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
		Hashtable<HexCoord, Placeable> map = world.getMap();
//		foodValue_label.setText(""+j);
		for(HexCoord hex : changes){
			drawOneObj(hex,map.get(hex));
		}
		changes.clear();
		updateExecutedStep();
		updateCritterNumber();
		
	}
	
	/**
	 * update the executedstep
	 */
	private void updateExecutedStep() {
		// TODO Auto-generated method stub
		executedNum_label.setText(""+this.world.getSteps());
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
		critterNum_label.setText(""+this.world.getCritterNumber());
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
