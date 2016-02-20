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
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class FoodAmountController {
	@FXML
	TextField amount_field;
	@FXML
	PasswordField password_field;
	@FXML
	Button ok_btn;
	@FXML
	Button cancel_btn;
	
	public static int amount = 0;
	
	@FXML
	void Summit(MouseEvent e) throws IOException{
		amount = getFoodAmount();
		if(amount>0){
			((Node)(e.getSource())).getScene().getWindow().hide();
			WorldController.food_amount = amount;
//			FXMLLoader loader = new FXMLLoader();
//			loader.setLocation(getClass().getResource("MainUI.fxml"));
//			Parent root = loader.load();
//			WorldController wc = loader.getController();
//			wc.setFoodAmount(amount);
		}
	}
	
	private int getFoodAmount() {
		// TODO Auto-generated method stub
		int number = 0;
		try{
			if(amount_field.getText().trim().equals(""))
				return 300;
			number = Integer.parseInt(amount_field.getText());
			if(number<0) AlertInfo.invalidNumber();
		}catch (NumberFormatException e){
			AlertInfo.invalidNumber();
			number = -2;
		}
		return number < 0?-2:number;
	}
	
	@FXML
	void Cancel(MouseEvent e) throws IOException{
		((Node)(e.getSource())).getScene().getWindow().hide();
		WorldController.food_amount = -1;
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(getClass().getResource("MainUI.fxml"));
//		Parent root = loader.load();
//		WorldController wc = loader.getController();
//		wc.setFoodAmount(-1);
	}
	
}
