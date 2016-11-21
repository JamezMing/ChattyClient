package logic;

import java.util.Timer;
import java.util.TimerTask;

public class ClientTimer {
	private ClientManager myManager;
	private Timer timer;
	
	public ClientTimer(ClientManager myManager){
		this.myManager = myManager;
	}
	
	public void init(int timeout){
		timer = new Timer();
		timer.schedule(new TimerTask(){
			public void run(){
				myManager.disPlayMessageFromServer("Your message has time out");
			}
		}, 10000);
	}
	
	public void cancel(){
		timer.cancel();
	}
}
