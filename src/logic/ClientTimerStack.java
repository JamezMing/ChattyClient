package logic;

import java.util.Stack;

public class ClientTimerStack {
	private Stack<ClientTimer> timerStack = new Stack<ClientTimer>();
	
	public ClientTimerStack(){}
	public ClientTimerStack(ClientTimer[] timers){
		for(ClientTimer ct: timers){
			timerStack.push(ct);
		}
	}
	
	
	public void pushTimer(ClientTimer timer){
		timerStack.push(timer);
	}
	
	public ClientTimer popTimer(){
		return timerStack.pop();
	}
	
	public int getTimerCount(){
		return timerStack.size();
	}
	
	public void clearStack(){
		timerStack.clear();
	}

}
