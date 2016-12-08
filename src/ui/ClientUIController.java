package ui;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import logic.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class ClientUIController extends AnchorPane{
	private ClientManager myManager;
	private Integer messageIndex = 0;
	private ArrayList<String> displayArray = new ArrayList<String>();
	@FXML TextField messageInputBox;
	@FXML Button enterButton;
	@FXML TextArea serverAddrDispArea;
	@FXML TextArea serverPortDispArea;
	@FXML TextArea myAddrDispArea;
	@FXML TextArea mySendingPortDispArea;
	@FXML TextArea myNameDispArea;
	@FXML TextArea myLisPortDispArea;
	@FXML TextArea messageDisplayArea;
	@FXML MenuItem connectButton;
	@FXML MenuItem exitButton;
	@FXML MenuItem chatBtn;
	@FXML Label chatRecPortField;
	@FXML Label chatSendPortField;
	
	/*public ClientUIController(ClientManager myManager){
		this.myManager = myManager;
	}*/
	
	@FXML public void onClickEnter() throws Exception{
		if((myManager == null)){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("ERROR");
			alert.setHeaderText("Error Message");
			String s ="You have not registered to any server yet!";
			alert.setContentText(s);
			alert.show();
		}else{
			String command = messageInputBox.getText();
			if(command.equalsIgnoreCase("Register") && myManager.getRegStat()==false){
				System.out.println(serverAddrDispArea.getText());
				System.out.println(serverPortDispArea.getText());
				myManager.registerClient(InetAddress.getByName(serverAddrDispArea.getText()), Integer.parseInt(serverPortDispArea.getText()));
			}
			else if(command.equalsIgnoreCase("Register") && myManager.getRegStat()==true){
				displayMessage("You have already been registered to a server");
			}
			//Register on -James -Molly -Helius
			else if(command.length() >= 7 && command.split(" ")[0].equalsIgnoreCase("Publish")){
				String state = command.split(" ")[1];
				boolean isAva; 
				if(state.equalsIgnoreCase("on")){
					isAva = true;
				}
				else{
					isAva = false;
				}
				String[] listOfUsers = Arrays.copyOfRange(command.split(" -"), 1, command.split(" -").length);
				myManager.publishClientInfo(InetAddress.getByName(serverAddrDispArea.getText()), Integer.parseInt(serverPortDispArea.getText()), isAva, listOfUsers);
			}
			//Chat "How are you?" 192.168.2.222 80

			else{
				command = String.format("I said: \n    %s \n", command);
				displayArray.add(command);
				System.out.println(displayArray.size());
				this.refreshDisplay();
			}
		}
		messageInputBox.clear();
	}
	
	@FXML public void onClickDisconnectionButton(){
		terminateManager();
	}
	
	@FXML public void onClickChatButton(){
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatConnectUI.fxml"));
			ChatConnectUIController con = new ChatConnectUIController(myManager);
			fxmlLoader.setController(con);
			AnchorPane rootLayout = (AnchorPane) fxmlLoader.load();
			Stage newSta = new Stage();
			newSta.setTitle("Chatty Client - new Chat Session");
			newSta.setScene(new Scene(rootLayout));
			newSta.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML public void onClickedExitButton(){
		terminateManager();
		Stage curr = (Stage) myNameDispArea.getScene().getWindow();
		curr.close();
	}
	
	@FXML public void onClickConnectButton() throws IOException{
		if((myManager == null) || (myManager.getRegStat() == false)){
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ConnectInterfaceUI.fxml"));
			ConnectInterfaceController intCtrl = new ConnectInterfaceController(this);
			fxmlLoader.setController(intCtrl);
			AnchorPane rootLayout = (AnchorPane) fxmlLoader.load();
			Stage newSta = new Stage();
			newSta.setTitle("Chatty Client - new Connection");
			newSta.setScene(new Scene(rootLayout));
			newSta.show();
		}
		else{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("ERROR");
			alert.setHeaderText("Error Message");
			String s ="Please disconnect the current connection with server first!";
			alert.setContentText(s);
			alert.show();
		}
	}
	
	@FXML public void setMyAddress(){
		String addr;
		try {
			addr = InetAddress.getLocalHost().getHostAddress();
			System.out.println(addr);
			myAddrDispArea.setText(addr);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public void registerManager(String name, int recPortNum, int sendPortNum, String addr, int hostPortNum, byte[] pubKey, byte[] secKey){
		if((myManager == null)){
			try {
				System.out.println("Resume");
				myManager = new ClientManager(name, recPortNum, sendPortNum, addr, hostPortNum, pubKey, secKey, this);
				myManager.init();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("ERROR");
			alert.setHeaderText("Error Message");
			String s ="Please disconnect the current connection with server first!";
			alert.setContentText(s);
			alert.show();
		}
	}
	
	
	public void registerManager(InetAddress serverAddr, Integer serverListPort) throws SocketException, UnknownHostException, SQLException{
		if((myManager == null) ){
			Integer myLisPort = Integer.parseInt(myLisPortDispArea.getText());
			Integer mySendPort = Integer.parseInt(this.mySendingPortDispArea.getText());
			String myName = myNameDispArea.getText();
			System.out.println(myName);
			InetAddress myServer = serverAddr;
			Integer myServerLisPort = serverListPort;
			try {
				myManager = new ClientManager(myName, myLisPort, mySendPort, myServer.getHostAddress(), myServerLisPort, this);
				System.out.println("The host addr is: " + myServer.getHostAddress());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myManager.init();
		}else if(myManager.getRegStat() == false && myManager != null){
			myManager.terminateManager();
			Integer myLisPort = Integer.parseInt(myLisPortDispArea.getText());
			Integer mySendPort = Integer.parseInt(this.mySendingPortDispArea.getText());
			String myName = myNameDispArea.getText();
			System.out.println(myName);
			InetAddress myServer = serverAddr;
			Integer myServerLisPort = serverListPort;
			try {
				myManager = new ClientManager(myName, myLisPort, mySendPort, myServer.getHostAddress(), myServerLisPort, this);
				System.out.println("The host addr is: " + myServer.getHostAddress());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myManager.init();
		}
		else{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("ERROR");
			alert.setHeaderText("Error Message");
			String s ="Please disconnect the current connection with server first!";
			alert.setContentText(s);
			alert.show();
		}

	}
	
	public void displayMessage(String str){
		displayArray.add(str);
		refreshDisplay();
	}
	
	protected void refreshDisplay(){
		messageDisplayArea.clear();
		for(String s: displayArray){
			messageDisplayArea.appendText(s);
			String newLine = System.getProperty("line.separator");
			messageDisplayArea.appendText(newLine);
		}
	}
	
	protected void terminateManager(){
		if(myManager != null){
			myManager.terminateManager();
		}
	}
	
	@FXML public String getMyName(){
		return myNameDispArea.getText();
	}
	
	@FXML public String getMyLisPort(){
		return myLisPortDispArea.getText();
	}
	
	@FXML public String getMySendingPort(){
		return mySendingPortDispArea.getText();
	}
	
	@FXML public void setMyName(String name){
		myNameDispArea.setText(name);
	}
	
	
	@FXML public void setMyLisPort(String portNum){
		myLisPortDispArea.setText(portNum);
	}
	
	@FXML public void setMySendingPort(String portNum){
		mySendingPortDispArea.setText(portNum);
	}
	
	@FXML public void setChatSendingPort(String portNum){
		chatSendPortField.setText(portNum);
	}
	
	@FXML public void setChatRexPort(String portNum){
		chatRecPortField.setText(portNum);
	}
	
	@FXML protected void setMyManager(ClientManager manager){
		myManager = manager;
	}
	
	@FXML protected void setServerAddr(String addr){
		serverAddrDispArea.setText(addr);
	}
	
	@FXML protected void setServerPortNum(Integer num){
		serverPortDispArea.setText(num.toString());
	}
	
	
	
}
