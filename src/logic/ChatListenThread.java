package logic;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

import org.tmatesoft.sqljet.core.SqlJetException;

import common.GlobalVariables;


public class ChatListenThread extends Thread{
	private DatagramSocket listSoc;
	private boolean isRunning = true;
	ClientManager myManager;
	
	public ChatListenThread(DatagramSocket soc, ClientManager manager){
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
				if(new String(recPac.getData()).startsWith(GlobalVariables.CHAT_ACTION)){
					//TODO
					String str_receive = new String(recPac.getData(),0,recPac.getLength()) +   
		                    " from " + recPac.getAddress().getHostAddress() + ":" + recPac.getPort();  
					System.out.println(str_receive);
					recPac.setLength(1024);
				}
			}
			System.out.println("The chat thread is terminated");
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}