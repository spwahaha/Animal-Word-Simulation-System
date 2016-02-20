package view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertInfo {
	public static void noWolrdAlert(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("NO WORLD HAS BEEN CREATED");
		alert.setContentText("Please create world first");
		alert.showAndWait();
	}
	
	public static void invalidPositionAlert(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("Invalid position");
		alert.setContentText("Please select a valid hex");
		alert.showAndWait();
	}
	
	public static void invalidNumber(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("Invalid number");
		alert.setContentText("Please input a valid number");
		alert.showAndWait();
	}
	
	public static void positionToken(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("This position is token");
		alert.setContentText("Please select another position");
		alert.showAndWait();
	}
	
}
