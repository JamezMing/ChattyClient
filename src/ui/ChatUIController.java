package ui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import common.ClientLogger;
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
		String frag;
		byte[] frag_b = new byte[140];
		int numFrac = ((int)msg.getBytes().length/140) + 1; 
		ClientLogger.log("The total length of the message is: " + msg.getBytes().length);
		for (int i = 0; i < numFrac; i++){
			frag_b = new byte[140];
			if((i+1)*140 < msg.getBytes().length){
				System.arraycopy(msg.getBytes(), i*140, frag_b, 0, 140);
				frag = new String(frag_b);
				frag = new String("(" + (i+1) + "/" + numFrac + ")" + frag);
			}else{
				System.arraycopy(msg.getBytes(), i*140, frag_b, 0, msg.getBytes().length - i*140);
				frag = new String(frag_b);
				frag = new String("(" + (i+1) + "/" + numFrac + ")" + frag);
			}
			try {
				myManager.sendChatMessage(frag, targetAddr, tarPort);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		messageInputArea.clear();
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
		
		if(targetAddr.equals(tarAddr) || tarPort == port){
			ClientLogger.log("Target Found");
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
