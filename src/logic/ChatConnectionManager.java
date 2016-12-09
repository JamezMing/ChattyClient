package logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import common.ClientLogger;
import common.GlobalVariables;

public class ChatConnectionManager {
	InetAddress targetAddr;
	InetAddress myAddr;
	Integer tarRecPort;
	DatagramSocket sendSoc;
	DatagramSocket recSoc;
	ClientManager myManager;
	ChatListenThread lisTh;
	boolean isInit = false;

	
	public ChatConnectionManager(InetAddress targetAddr, Integer tarRecPort, Integer myRecPort, Integer mySendPort, ClientManager myManager) throws UnknownHostException, SocketException{
		this.targetAddr = targetAddr;
		this.tarRecPort = tarRecPort;
		this.myManager = myManager; 
		myAddr = InetAddress.getLocalHost();
		sendSoc = new DatagramSocket(mySendPort);
		recSoc = new DatagramSocket(myRecPort);
		lisTh = new ChatListenThread(recSoc, myManager);
		lisTh.start();
	}
	
	public void sendMessage(String message) throws IOException{
		Integer myRecPort = recSoc.getLocalPort();
		String msgStr = new String(GlobalVariables.CHAT_ACTION + GlobalVariables.delimiter + myManager.getName() + GlobalVariables.delimiter + myAddr.getHostAddress() + 
				GlobalVariables.delimiter + myRecPort.toString() + GlobalVariables.delimiter + message);
		ClientLogger.log(msgStr);
		DatagramPacket msgPac = new DatagramPacket(msgStr.getBytes(), msgStr.length(), targetAddr, tarRecPort);
		sendSoc.send(msgPac);
	}
	
	
	public void terminateManager(){
		sendSoc.close();
		recSoc.close();
		lisTh.endThread();
	}
	
	public String getUserName(){
		return targetAddr.getHostAddress();
	}
	
	
	
	
	

}
