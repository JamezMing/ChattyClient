package ui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import logic.ClientManager;
import javafx.scene.control.Alert.AlertType;

public class ChatConnectUIController extends AnchorPane{
	@FXML TextArea clientAddrField;
	@FXML TextArea clientPortField;
	@FXML Button connectButton;
	private ClientManager myManager;
	
	public ChatConnectUIController(ClientManager myManager){
		this.myManager = myManager;
	}
	
	@FXML public void onClickConnectButton(){
		while(true){
			String addrString = clientAddrField.getText();
			try {
				InetAddress addr = InetAddress.getByName(addrString);
				Integer port = new Integer(clientPortField.getText());
				if(port > 65565 || port <= 0){
					throw new NumberFormatException();
				}
				myManager.startNewChat(addr, port);
				clientAddrField.clear();
				clientPortField.clear();
				break;
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("ERROR");
				alert.setHeaderText("Error Message");
				String s ="The address you entered is invalid!";
				alert.setContentText(s);
				alert.show();
				e.printStackTrace();
			} catch (NumberFormatException e){
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("ERROR");
				alert.setHeaderText("Error Message");
				String s ="The port number you entered is invalid!";
				alert.setContentText(s);
				alert.show();
				e.printStackTrace();
			}
		}
		
		Stage preStage = (Stage) connectButton.getScene().getWindow();
		preStage.close();
	}
}
