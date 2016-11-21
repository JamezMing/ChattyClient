package requestsParser;
import java.net.InetAddress;

enum MessageType {
	REGISTER, DEREGISTER,REGISTER_DENIED, REGISTER_SUCCESS, 
	PUBLISH, PUBLISH_DENIED, PUBLISH_SUCCESS, REQUEST_IMFORMATION, 
	REQUEST_INFORMARTION_DENIED, REFER, CHAT, BYE
};
public class Request {
	private String message; 
	private InetAddress source;
	private int senderPortNum;
	
	public Request(String message, InetAddress source, int senderPortNum){
		this.message = message;
		this.source = source;
		this.senderPortNum = senderPortNum;
	}
	
	public String getMessage(){
		return message;
	}
	
	public InetAddress getSenderAddr(){
		return source;
	}
	
	public int getSenderPort(){
		return senderPortNum;
	}

}
