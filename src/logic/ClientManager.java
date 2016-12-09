package logic;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import org.tmatesoft.sqljet.core.SqlJetException;

import common.ClientDataBaseManager;
import common.GlobalVariables;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ui.ChatUIController;
import ui.ClientUIController;


public class ClientManager {
	
	private ClientUIController myWinController;
	private DatagramSocket recevingPort;
	private DatagramSocket sendingPort;
	private InetAddress hostAddr;
	private String myName;
	private int hostRecPort;
	private Integer messageIndex = 0;
	private byte[] publicKey;
	private byte[] secretKey;
	private ClientSignatureGen sigGen; 
	private boolean isRegistered = false;
	private boolean isInit = false;
	ClientTimerStack timerStack = new ClientTimerStack();
	ArrayList<ChatUIController> chatList = new ArrayList<ChatUIController>();
	ClientSendingThread senTh;
	ClientListenThread lisTh;
	

	public ClientManager(int recPortNum, int sendPortNum, String addr, int hostPortNum, ClientUIController myController) throws SQLException{
		try{
			System.out.println("Constructed");
			recevingPort = new DatagramSocket(recPortNum);
			sendingPort = new DatagramSocket(sendPortNum);
			System.out.println("Content send to Constructer: " + addr);
			hostAddr = InetAddress.getByName(addr);
			System.out.println("The host address in constructor: " + hostAddr.getHostAddress());
			hostRecPort = hostPortNum;
			myName = new String("Default");
			sigGen = new ClientSignatureGen();
			myWinController = myController;
			sigGen.init();
			publicKey = sigGen.getPublicKey();

		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidParameterSpecException
				| InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (SocketException e){
			System.out.println("Socket Failure");
			e.printStackTrace();
			//TODO
		} catch(UnknownHostException e){
			System.out.println("Address Failure");
			System.out.println("Content send to Constructer: " + addr);
			e.printStackTrace();
		}
		
	}
	
	public ClientManager(String name, int recPortNum, int sendPortNum, String addr, int hostPortNum, ClientUIController myController) throws SQLException{
		try{
			System.out.println("Constructed");
			recevingPort = new DatagramSocket(recPortNum);
			recevingPort.setReuseAddress(true);
			sendingPort = new DatagramSocket(sendPortNum);
			sendingPort.setReuseAddress(true);
			System.out.println("Content send to Constructer: " + addr);

			hostAddr = InetAddress.getByName(addr);
			hostRecPort = hostPortNum;
			sigGen = new ClientSignatureGen();
			System.out.println("The host address in constructor: " + hostAddr.getHostAddress());

			myWinController = myController;
			myName = name;	
			sigGen.init();
			publicKey = sigGen.getPublicKey();

		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidParameterSpecException
				| InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e){
			System.out.println("Socket Failure");
			e.printStackTrace();
			//TODO
		} catch(UnknownHostException e){
			System.out.println("Address Failure");
			System.out.println("Content send to Constructer: " + addr);
			e.printStackTrace();
		}
		
		
	}
	
	public ClientManager(String name, int recPortNum, int sendPortNum, String addr, int hostPortNum, byte[] pubKey, byte[] secKey, ClientUIController myController) throws SQLException{
		try{
			System.out.println("Constructed");
			recevingPort = new DatagramSocket(recPortNum);
			sendingPort = new DatagramSocket(sendPortNum);
			System.out.println("Content send to Constructer: " + addr);
		
			hostAddr = InetAddress.getByName(addr);
			System.out.println("The host address in constructor: " + hostAddr.getHostAddress());
			hostRecPort = hostPortNum;
			sigGen = new ClientSignatureGen();
			myWinController = myController;
			myName = name;	
			publicKey = pubKey;
			secretKey = secKey;
			isRegistered = true;
		}catch (SocketException e){
			System.out.println("Socket Failure");
			e.printStackTrace();
			//TODO
		} catch(UnknownHostException e){
			System.out.println("Address Failure");
			System.out.println("Content send to Constructer: " + addr);
			e.printStackTrace();
		}
	}


	protected DatagramSocket getSendingSoc(){
		return sendingPort;
	}
	
	protected void decodeSecret(byte[] key) throws SqlJetException, UnsupportedEncodingException{
		try {
			sigGen.decodeKey(key);
			secretKey = sigGen.getSecret();
			isRegistered = true;
			String pubKey = javax.xml.bind.DatatypeConverter.printHexBinary(publicKey);
			String secKey = javax.xml.bind.DatatypeConverter.printHexBinary(secretKey);
			ClientDataBaseManager.insertItem(myName, new Integer(sendingPort.getLocalPort()).toString(), new Integer(recevingPort.getLocalPort()).toString(), secKey, pubKey, hostAddr.getHostAddress(), new Integer(hostRecPort).toString());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void init(){
		senTh = new ClientSendingThread(sendingPort, hostRecPort, hostAddr, this);
		senTh.start();
		lisTh = new ClientListenThread(recevingPort, this);
		lisTh.start();
		isInit = true;
	}
	
	public ClientSendingThread getSendThread(){
		if(isInit){
			return senTh;
		}
		return null;
	}
	
	
	public ClientListenThread getRecThread(){
		if(isInit){
			return lisTh;
		}
		return null;
	}
	
	public void sendChatMessage(String message, InetAddress tarAddr, Integer port) throws UnknownHostException{
		String msg = new String(GlobalVariables.CHAT_ACTION + GlobalVariables.delimiter + myName + 
				GlobalVariables.delimiter + InetAddress.getLocalHost().getHostAddress() + GlobalVariables.delimiter + 
				new Integer(this.recevingPort.getLocalPort()).toString() + GlobalVariables.delimiter + message);
		senTh.sendChatMessage(msg, tarAddr, port);
		for(ChatUIController chatCon: chatList){
			if(chatCon.isTargetChat(tarAddr, port)){
				chatCon.addDisplayLine("I said: " + message);
				return;
			}
		}
	}
	
	public void startNewChat(InetAddress tarAddr, Integer port){
		ChatUIController chatCon = new ChatUIController(tarAddr, port, this);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../ui/ChatUI.fxml"));
		fxmlLoader.setController(chatCon);
		AnchorPane rootLayout;
		chatList.add(chatCon);
		try {
			rootLayout = (AnchorPane) fxmlLoader.load();
			Stage newSta = new Stage();
			newSta.setTitle("Chat Session");
			newSta.setScene(new Scene(rootLayout));
		    newSta.setOnHiding(new EventHandler<WindowEvent>() {
	             @Override
	             public void handle(WindowEvent event) {
	                 Platform.runLater(new Runnable() {
	                     @Override
	                     public void run() {
	                         System.out.println("Application Closed by click to Close Button(X)");
	                         chatList.remove(chatCon);
	                     }
	                 });
	             }
	        });
		    newSta.show();
		} catch (IOException e) {
			System.out.println("The fxml file is not found");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void receiveChatMessage(String message, InetAddress tarAddr, String name, Integer port) throws IOException{
		for(ChatUIController chatCon: chatList){
			if(chatCon.isTargetChat(tarAddr, port)){
				System.out.println("Corresponding chat window found");
				chatCon.addDisplayLine(tarAddr.getHostAddress() + " " + name + " said: " + message);
				return;
			}
		}
		for(ChatUIController chatCon: chatList){
			System.out.println(chatCon.getTargetAddress().getHostAddress());
		}
		ChatUIController chatCon = new ChatUIController(tarAddr, port, this);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../ui/ChatUI.fxml"));
		fxmlLoader.setController(chatCon);
		AnchorPane rootLayout = (AnchorPane) fxmlLoader.load();
		chatList.add(chatCon);
		Stage newSta = new Stage();
		newSta.setTitle("Chat Session With - " + name);
		newSta.setScene(new Scene(rootLayout));
	    newSta.setOnHiding(new EventHandler<WindowEvent>() {
             @Override
             public void handle(WindowEvent event) {
                 Platform.runLater(new Runnable() {
                     @Override
                     public void run() {
                         System.out.println("Application Closed by click to Close Button(X)");
                         chatList.remove(chatCon);
                     }
                 });
             }
        });
	    newSta.show();
	    chatCon.addDisplayLine(tarAddr.getHostAddress() + " " + name + " said: " + message);
	}
	
	public String getName(){
		return myName;
	}
	
	public void sendMessageServer(String msg){
		senTh.writeBufferReader(msg);
	}
	
	public void registerClient(InetAddress serAddr, Integer recPort) throws UnknownHostException{
		String msg = new String(common.GlobalVariables.REGISTER_ACTION + GlobalVariables.delimiter + messageIndex.toString()
				+ GlobalVariables.delimiter + myName + GlobalVariables.delimiter + InetAddress.getLocalHost().getHostAddress()
				+ GlobalVariables.delimiter + this.recevingPort.getLocalPort());
		this.myWinController.displayMessage("Registering to the server, index = " + messageIndex);
		messageIndex++;
		sendMessageServer(msg);
	}
	
	public void publishClientInfo(InetAddress serAddr, Integer recPort, boolean status, String[] listOfName) throws UnknownHostException{
		String nameString = Arrays.toString(listOfName);
		String msg;
		if(status == true){
			msg = new String(common.GlobalVariables.PUBLISH_ACTION + GlobalVariables.delimiter + messageIndex.toString()
			+ GlobalVariables.delimiter + myName + GlobalVariables.delimiter + this.recevingPort.getLocalPort() + GlobalVariables.delimiter + 
			GlobalVariables.STATUS_ON + GlobalVariables.delimiter + nameString);
		}else{
			msg = new String(common.GlobalVariables.PUBLISH_ACTION + GlobalVariables.delimiter + messageIndex.toString()
			+ GlobalVariables.delimiter + myName + GlobalVariables.delimiter + this.recevingPort.getLocalPort() + GlobalVariables.delimiter + 
			GlobalVariables.STATUS_OFF + GlobalVariables.delimiter + nameString);
		}
		this.myWinController.displayMessage("Trying to publish to the server, index = " + messageIndex);
		messageIndex++;
		sendMessageServer(msg);
	}
	
	public void disPlayMessageFromServer(String message){
		message = String.format("Server Says: %s \n", message);
		myWinController.displayMessage(message);
	}

	
	//User input format: chat + message + tarAddr + tarPort
	public void chatWithUser(String msg, InetAddress addr, Integer recPort){
		String msgSend = new String(GlobalVariables.CHAT_ACTION + GlobalVariables.delimiter + myName + 
				GlobalVariables.delimiter + hostAddr + GlobalVariables.delimiter  + hostRecPort);
		new ClientTalkThread(msgSend, sendingPort, addr, recPort).start();
	}
	
	public void requestUserInfo(String userName, InetAddress serverAddr, Integer serverPort) throws UnknownHostException{
		String msgSend = new String(GlobalVariables.USER_INFO_REQUEST_ACTION + GlobalVariables.delimiter + userName + GlobalVariables.delimiter + 
				this.getName() +  GlobalVariables.delimiter + recevingPort.getLocalPort());
		new ClientTalkThread(msgSend, sendingPort, serverAddr, serverPort).start();
		pushNewTimer(10000);
	}
	
	public void requestInformation(){
		String msgSend = new String(GlobalVariables.REQUEST_ACTION + GlobalVariables.delimiter + messageIndex.toString() + GlobalVariables.delimiter + this.getName());
		messageIndex++;
		sendMessageServer(msgSend);
	}
	
	public void requestHistoryMessage(Integer index){
		String msgSend = new String(GlobalVariables.IMFORMATION_REQUEST_ACTION + GlobalVariables.delimiter + index.toString() + GlobalVariables.delimiter + this.getName());
		sendMessageServer(msgSend);
	}
	
	public boolean getRegStat(){
		return isRegistered;
	}
	
	public void terminateManager(){
		if(isRegistered == true){
			new ClientTalkThread(new String(common.GlobalVariables.BYE_ACTION + 
			GlobalVariables.delimiter + myName + GlobalVariables.delimiter + 
			DatatypeConverter.printHexBinary(this.getPublicKey())), sendingPort, hostAddr, hostRecPort);
		}
		senTh.endThread();
		lisTh.endThread();
		recevingPort.close();
		sendingPort.close();
	}
	
	
	public void pushNewTimer(int delay){
		ClientTimer timer = new ClientTimer(this);
		timerStack.pushTimer(timer);
		timer.init(10000);
	}
	
	
	public void popTimer(){
		timerStack.popTimer().cancel();;
	}
	

	public byte[] getPublicKey(){
		return publicKey;
	}
	
	protected byte[] getSecretKey(){
		return secretKey;
	}

	
}
