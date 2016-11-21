package logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientTalkThread extends Thread {
	
	private DatagramSocket sendSoc;
	private InetAddress talkAddr;
	private Integer talkPort;
	private String myMessage;
	//Chat Message Raw format: Chat
	
	public ClientTalkThread(String msg, DatagramSocket sendSoc,  InetAddress talkAddr, Integer talkPort){
		this.sendSoc = sendSoc;
		this.talkAddr = talkAddr;
		this.talkPort = talkPort;
		this.myMessage = msg;
	}
	
	public void run(){
		DatagramPacket dp = new DatagramPacket(myMessage.getBytes(), myMessage.length(), talkAddr, talkPort);
		try {
			sendSoc.send(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
