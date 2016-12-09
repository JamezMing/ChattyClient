package common;

public class GlobalVariables {
	public static final int HANDSHAKING_PORT = 6666;
	public static final String REGISTER_ACTION = "reg_a";
	public static final String DEREGISTER_ACTION = "der_a";
	public static final String REGISTER_DENIED = "reg_d";
	public static final String REGISTER_SUCCESS = "reg_s";
	public static final String REGISTER_FULL = "reg_f";
	public static final String PUBLISH_ACTION = "pub_a";
	public static final String PUBLISH_SUCCESS = "pub_s";
	public static final String PUBLISH_DENIED = "pub_d";
	public static final String IMFORMATION_REQUEST_ACTION = "inq_a";
	public static final String IMFORMATION_REQUEST_SUCCESS = "inq_s";
	public static final String REQUEST_ACTION = "req_a";
	public static final String REQUEST_SUCCESSFUL = "req_s";
	public static final String USER_INFO_REQUEST_ACTION = "fiq_a";
	public static final String USER_INFO_REQUEST_SUCCESS = "fiq_s";
	public static final String USER_INFO_REQUEST_DENIED = "fiq_d";
	public static final String REFER_ACTION = "ref_a";
	public static final String CHAT_ACTION = "cha_a";
	public static final String BYE_ACTION = "bye_a";
	public static final String INFORMATION_REQUEST_DENIED = "inq_d";
	public static final String PUBLIC_KEY = "pub_k";
	private static final String DB_NAME = "ClientData.db";	
	public static final String token = new String("(?<!%a)%_");
	public static final String delimiter = new String("%_");
	public static final String STATUS_ON = "on";
	public static final String STATUS_OFF = "off";
	public static final Object lock1 = new Object();
	public static final Object lock2 = new Object();

}
