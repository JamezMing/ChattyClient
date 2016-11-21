package ui;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.tmatesoft.sqljet.core.SqlJetException;

import common.ClientDataBaseManager;
import logic.ClientManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ClientLoginUIController extends AnchorPane{
	
	@FXML Button logInButton;
	@FXML TextField nameInputField;
	@FXML TextField portInputField;
	@FXML TextField chatSendPortField;
	@FXML TextField chatRecPortField;
	@FXML TextField sendingPortInputField;
	@FXML MenuItem aboutMenuBtn;
	@FXML Label infoLabel;
	
	@FXML public void onClickLogInBtn(){
		boolean resume = false;
		Integer portNum = new Integer(-1);
		Integer portSendNum = new Integer(-1);

		while(true){
			try{
				portNum = Integer.parseInt(portInputField.getText());
				portSendNum =  Integer.parseInt(sendingPortInputField.getText());
				if((portNum > 65535) || (portSendNum > 65535)){
					throw new NumberFormatException("The port number is too big");
				}
				break;
			}catch(NumberFormatException e){
				infoLabel.setText("Please enter a valid port number. ");
				throw e;
			}
		}
		
			
		
		
		String userName = nameInputField.getText();
		ClientDataBaseManager.init();
		String[] record = ClientDataBaseManager.lookupRecordByNameAndPort(userName, portNum.toString());
		if (record!= null){
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText("Do you wish to resume your existing dialog?");
			alert.setContentText("Press OK to resume");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
			    resume = true;
			} else {
			    resume = false;
			    ClientDataBaseManager.deleteRecord(userName, portNum.toString());			
			}
		}
		sendingPortInputField.clear();
		nameInputField.clear();
		portInputField.clear();
		Stage preStage = (Stage) logInButton.getScene().getWindow();
		preStage.close();
		try{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientUI.fxml"));
			ClientUIController cliCrl = new ClientUIController();
			fxmlLoader.setController(cliCrl);
			System.out.println(userName);
			AnchorPane rootLayout = (AnchorPane) fxmlLoader.load();
			Stage newSta = new Stage();
			newSta.setTitle("Chatty Client - " + userName);
			newSta.setScene(new Scene(rootLayout));
			if(resume == false){
				cliCrl.setMyName(userName);
				cliCrl.setMyLisPort(portNum.toString());
				cliCrl.setMyAddress();
				cliCrl.setMySendingPort(portSendNum.toString());
				
			}
			else{
				cliCrl.setMyName(record[0]);
				cliCrl.setMyLisPort(record[2]);
				cliCrl.setMyAddress();
				cliCrl.setMySendingPort(record[1]);
				cliCrl.setServerAddr(record[5]);
				cliCrl.setServerPortNum(new Integer(record[6]));
				cliCrl.registerManager(record[0], new Integer(record[2]), new Integer(record[1]), record[5], new Integer(record[6]), javax.xml.bind.DatatypeConverter.parseHexBinary(record[4]), javax.xml.bind.DatatypeConverter.parseHexBinary(record[3]));

			}

			newSta.show();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
 

}
