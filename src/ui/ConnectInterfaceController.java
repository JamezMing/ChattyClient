package ui;

import java.net.BindException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;

import common.ClientLogger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ConnectInterfaceController extends AnchorPane{
	ClientUIController fatherPane;
	
	
	public ConnectInterfaceController(ClientUIController father){
		fatherPane = father;
	}
	
	@FXML Button confirmBtn;
	@FXML Button cancelBtn;
	@FXML CheckBox stateBox;
	@FXML TextArea serverIPField;
	@FXML TextArea serverPortField;
	
	@FXML public void onConfirmClicked() throws SocketException, SQLException{
		try{
			String serverAddr = InetAddress.getByName(serverIPField.getText()).getHostAddress();
			Integer serverPort = Integer.parseInt(serverPortField.getText());
			if(serverPort > 65535){
				throw new IllegalArgumentException("The port num is too big");
			}
			ClientLogger.log("Server addr from father pane: " + serverAddr);
			fatherPane.registerManager(InetAddress.getByName(serverIPField.getText()), serverPort);
			fatherPane.setServerAddr(serverAddr);
			fatherPane.setServerPortNum(serverPort);
			Stage curr = (Stage)confirmBtn.getScene().getWindow();
			curr.close();
		}catch(IllegalArgumentException e){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("ERROR");
			alert.setHeaderText("Error Message");
			String s ="Please input a valid host address and port numbers";
			alert.setContentText(s);
			alert.show();
		}catch(BindException eb){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("ERROR");
			alert.setHeaderText("Error Message");
			String s ="The address you have inputed is not valid for the moment";
			alert.setContentText(s);
			alert.show();
			eb.printStackTrace();
		}catch(UnknownHostException e){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("ERROR");
			alert.setHeaderText("Error Message");
			String s ="Please input a valid host address and port numbers";
			alert.setContentText(s);
			alert.show();
		}
	}
	
	@FXML public void onCancelClicked(){
		Stage curr = (Stage)cancelBtn.getScene().getWindow();
		curr.close();
	}
	
	

}
