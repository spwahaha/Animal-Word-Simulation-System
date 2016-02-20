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

public class LoginController {
	@FXML
	TextField level_field;
	@FXML
	PasswordField password_field;
	@FXML
	Button login_btn;
	@FXML
	Button cancel_btn;
	
	public static int sessionId = 0;
	
	@FXML
	void Login(MouseEvent e) throws IOException{
		String username = level_field.getText();
		String password = password_field.getText();
		System.out.println("clicked");
		URL url = new URL("http://localhost:8080/12A7/demoServelet/CritterWorld/login");
		Gson gson = new Gson();
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		System.out.println("Doing POST "+ url);
		connection.setDoOutput(true); // send a POST message
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content_Type", "application/json");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		LoginInfoBundle bundle = new LoginInfoBundle(username,password);
		w.println(gson.toJson(bundle, LoginInfoBundle.class));
		w.flush();
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		r.mark(1);
		System.out.println(r.readLine());
		r.reset();
		LoginFeedbackBundle feedback = gson.fromJson(r, LoginFeedbackBundle.class);
		if(feedback.getId()>0){
			((Node)(e.getSource())).getScene().getWindow().hide();
			this.sessionId = feedback.getId();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("MainUI.fxml"));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.setTitle("Critter revolution");
			stage.setScene(new Scene(root));
			WorldController wc = loader.getController();
			wc.setId(sessionId);
			stage.show();
		}else{
			AlertInfo.WrongPassword();
		}
		System.out.println("connected");
	}
	
	
	@FXML
	void Cancel(MouseEvent e) throws IOException{
		Platform.exit();
	}
	
}
