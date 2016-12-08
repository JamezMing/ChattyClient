package logic;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import org.tmatesoft.sqljet.core.SqlJetException;

import common.GlobalVariables;
import javafx.application.Platform;


public class ClientListenThread extends Thread{
	private DatagramSocket listSoc;
	private boolean isRunning = true;
	ClientManager myManager;
	
	public ClientListenThread(DatagramSocket soc, ClientManager manager){
		listSoc = soc;
		myManager = manager;
	}
	
	public void endThread(){
		isRunning = false;
	}
	
	public void run(){
		try {
			byte[] buffer = new byte[1024];
			DatagramPacket recPac = new DatagramPacket(buffer, 1024);
			while(isRunning){
				listSoc.receive(recPac);
				System.out.println(new String(recPac.getData()));
				if(new String(recPac.getData()).startsWith(GlobalVariables.REGISTER_SUCCESS)){
					String strRec = new String(recPac.getData(),0,recPac.getLength());
					myManager.decodeSecret(new BigInteger(strRec.split(GlobalVariables.delimiter)[2], 16).toByteArray());
					myManager.disPlayMessageFromServer("Register Successful");
					myManager.popTimer();
				}
				else if(new String(recPac.getData()).startsWith(GlobalVariables.REGISTER_DENIED)){
					myManager.disPlayMessageFromServer("Registration Denied by the server");
					myManager.popTimer();
				}
				else if(new String(recPac.getData()).startsWith(GlobalVariables.PUBLISH_SUCCESS)||new String(recPac.getData()).startsWith(GlobalVariables.PUBLISH_DENIED)){
					myManager.popTimer();
				}
				else if(new String(recPac.getData()).startsWith(GlobalVariables.CHAT_ACTION)){
					String str_receive = new String(recPac.getData(),0,recPac.getLength());
					String[] msg_fields = str_receive.split(GlobalVariables.token);
					System.out.println("Chat Message Received");
					InetAddress addr = InetAddress.getByName(msg_fields[2]);
					Platform.runLater(new Runnable(){
						public void run(){
							try {
								myManager.receiveChatMessage(msg_fields[4], addr, msg_fields[1], new Integer(msg_fields[3]));
								System.out.println("Addr: " + msg_fields[2] + " " +  "Name: " + msg_fields[1] + " " + "Port Number: " + msg_fields[3]);
							} catch (NumberFormatException | IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					
				}
				//reg_f%_1%_192.168.2.222%_80
				else if(new String(recPac.getData()).startsWith(GlobalVariables.REGISTER_FULL)){
					String str_receive = new String(recPac.getData(),0,recPac.getLength());
					String[] msg_fields = str_receive.split(GlobalVariables.token);
					InetAddress nextAddress = InetAddress.getByName(msg_fields[2]);
					Integer nextPort = new Integer(msg_fields[3]);
					myManager.disPlayMessageFromServer("The server is full. ");
					myManager.disPlayMessageFromServer("Please try register on: ");
					myManager.disPlayMessageFromServer(nextAddress.getHostAddress());
					myManager.disPlayMessageFromServer(nextPort.toString());
					myManager.popTimer();
				}
				recPac.setLength(1024);
			}
			listSoc.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}