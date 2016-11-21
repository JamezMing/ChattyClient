package ui;

import java.net.InetAddress;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import logic.ClientManager;

public class ChatUIController extends AnchorPane{
	@FXML TextArea messageDisplayArea;
	@FXML TextArea messageInputArea;
	@FXML Button sendButton;
	InetAddress targetAddr;
	Integer tarPort;
	ClientManager myManager;
	private ArrayList<String> displayArray = new ArrayList<String>();
	private boolean isSet = false;
	
	public ChatUIController(InetAddress tarAddr, Integer tarPort, ClientManager manager){
		targetAddr = tarAddr;
		this.tarPort = tarPort;
		myManager = manager;
		isSet = true;
	}
	
	//Chat "String" Address
	@FXML public void onClickSendButton(){
		String msg = messageInputArea.getText();
		myManager.sendChatMessage(msg, targetAddr, tarPort);
	}
	
	@FXML public void refreshDisplay(){
		messageDisplayArea.clear();
		for(String s: displayArray){
			messageDisplayArea.appendText(s);
			messageDisplayArea.appendText("" + '\n');
		}
	}
	
	public void addDisplayLine(String line){
		displayArray.add(line);
		refreshDisplay();
	}
	
	public void setTarget(InetAddress tarAddr, Integer tarPort){
		targetAddr = tarAddr;
		this.tarPort = tarPort;
		isSet = true;
	}
	
	public boolean isTargetChat(InetAddress tarAddr, Integer port){
		if(targetAddr == tarAddr || tarPort == port){
			return true;
		}
		else{
			return false;
		}
	}
	
	public InetAddress getTargetAddress(){
		if(isSet == true){
			return targetAddr;
		}
		return null;
	}
	
	public Integer getTargetPort(){
		if(isSet == true){
			return tarPort;
		}
		return null;
	}
	


}
