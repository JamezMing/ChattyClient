package logic;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

import common.ClientLogger;
import common.GlobalVariables;

public class ClientSendingThread extends Thread{
	private DatagramSocket sendSoc;
	private int hostSoc;
	private InetAddress hostAddr;
	private ClientManager myManager;
	private BufferedReader reader;
	private boolean running = true;
	
	
	public ClientSendingThread(DatagramSocket send, int dest, InetAddress addr, ClientManager manager){
		ClientLogger.log("Thread start");
		hostSoc = dest;
		sendSoc = send;
		hostAddr = addr;
		myManager = manager;
		ClientLogger.log("Host Addr rec by thread: " + hostAddr.getHostAddress());
	}
	
	public void writeBufferReader(String message){
		synchronized(this){
			InputStreamReader in = new InputStreamReader(new ByteArrayInputStream(message.getBytes()));
			reader = new BufferedReader(in);
			notify();
		}
	}
	
	public void endThread(){
		running = false;
	}
	
	
	public void sendChatMessage(String message, InetAddress chatAddr, Integer recSoc){
		new Thread(new Runnable(){
			String message;
			InetAddress chatAddr;
			Integer recSoc;
			public void run(){
				ClientLogger.log("Chat message sent thread created");
				DatagramPacket pac = new DatagramPacket(message.getBytes(), message.length(), chatAddr, recSoc);
				try {
					sendSoc.send(pac);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			public Runnable setParams(String message, InetAddress chatAddr, Integer recSoc){
				this.message = message;
				this.chatAddr = chatAddr;
				this.recSoc = recSoc;
				return this;
			}
		}.setParams(message, chatAddr, recSoc)).start();
	}


	public void run(){
		try{			
			while(hostAddr == null){
				ClientLogger.log("Host Address Undefined, please enter a valid host address to continue");
				break;
			}
			while(running){
				synchronized(this){
					
				wait();
				String msg;
				while ((msg = reader.readLine()) == null) {
					ClientLogger.log("Preparing Reader...");
					Thread.sleep(100);
		        }
				ClientLogger.log(msg);

		        //Temp Solution 
		        if(msg.substring(0, 5).equals(GlobalVariables.REGISTER_ACTION)){
		        	if(myManager.getRegStat() == true){
		        		continue;
		        	}
		        	String keyStr = new String(GlobalVariables.delimiter + DatatypeConverter.printHexBinary(myManager.getPublicKey()));
		        	msg = msg.concat(keyStr);
		        	ClientLogger.log(msg);
		        }
		        else if(!msg.substring(0,5).equals(GlobalVariables.USER_INFO_REQUEST_ACTION)){
		        	String keyStr = new String(GlobalVariables.delimiter + DatatypeConverter.printHexBinary(myManager.getSecretKey()));
		        	msg = msg.concat(keyStr);
		        	ClientLogger.log(msg);
		        }
		        else{
		        	ClientLogger.log(msg);
		        }
		        /*if(msg.substring(0, 5).equals(GlobalVariables.REGISTER_ACTION)){
		        	String[] argtalk = msg.split(GlobalVariables.delimiter);
		        	myManager.chatWithUser(argtalk[1], InetAddress.getByName(argtalk[3]), new Integer(argtalk[4]));
		        }*/
		        /*if(myManager.getRegStat() == true){
		        	msg = msg.concat(GlobalVariables.delimiter + DatatypeConverter.printHexBinary(myManager.getSecretKey()));
		        }*/
		        //Temp Solution
		        DatagramPacket sendPac = new DatagramPacket(msg.getBytes(), msg.length(), hostAddr, hostSoc);
		        sendSoc.send(sendPac);
		        myManager.pushNewTimer(10000);
		        
			}}

		}catch(IOException e){
			e.printStackTrace();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		
	}
	
}
