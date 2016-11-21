package common;


import org.tmatesoft.sqljet.core.SqlJetException;

public class DataBaseTest {
	public static void main(String args[]) throws SqlJetException{
		ClientDataBaseManager.init();
		//ClientDataBaseManager.insertItem("James12334", "80", "803332", "keyiskey", "PUB1" , "192.168.2.222");
		//ClientDataBaseManager.insertItem("James222312", "303", "14331", "Keyiskey2", "PUB2", "192.168.2.222");
		ClientDataBaseManager.getRecords();
		System.out.println(ClientDataBaseManager.lookupRecordByNameAndPort("James12334","803332"));
		ClientDataBaseManager.clearDataBase();

	}
}
