package common;

import java.io.File;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public final class ClientDataBaseManager {
	private static SqlJetDb db;
	private static final String DB_NAME = "ClientData.db";
	private static final String TABLE_NAME = "Client";
	private static final String USER_NAME_FIELD = "user_name";
	private static final String USER_SENDPORT_FIELD = "send_port";
	private static final String USER_RECEIVEPORT_FIELD = "receive_port";
	private static final String USER_KEY_FIELD = "user_key";
	private static final String PUBLIC_KEY_FIELD = "public_key";
	private static final String USER_KEY_ADDRESS_FIELD = "key_addr";
	private static final String USER_KEY_PORT_FIELD = "key_port";
	private static final String NAMEPORT_INDEX = "nameport_index";
	private static final String NAME_INDEX = "name_index";
	private static final String PORT_INDEX = "port_index";


	private ClientDataBaseManager(){}
	
	public static void init(){
		File dbFile = new File(DB_NAME);
		
		ClientLogger.log("Database file is created: " + dbFile.exists());
		if(dbFile.exists()){
			try {
				db = SqlJetDb.open(dbFile, true);
				db.beginTransaction(SqlJetTransactionMode.WRITE);
				db.getOptions().setUserVersion(1);
			    db.commit();
			} catch (SqlJetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				db = SqlJetDb.open(dbFile, true);
				db.getOptions().setAutovacuum(true);
				db.beginTransaction(SqlJetTransactionMode.WRITE);
				db.getOptions().setUserVersion(1);
	            String createTableQuery = "CREATE TABLE " + TABLE_NAME + 
	            		" (" + USER_NAME_FIELD + " TEXT NOT NULL PRIMARY KEY , " + USER_SENDPORT_FIELD +
	            		" TEXT NOT NULL, " + USER_RECEIVEPORT_FIELD + " TEXT NOT NULL PRIMARY KEY, " + USER_KEY_FIELD + " TEXT NOT NULL, " + PUBLIC_KEY_FIELD + " TEXT NOT NULL, "
	            		+ USER_KEY_ADDRESS_FIELD + " TEXT NOT NULL, " + USER_KEY_PORT_FIELD + " TEXT NOT NULL " + ")";
	            String createNameQuery = "CREATE UNIQUE INDEX " + NAMEPORT_INDEX + " ON " + TABLE_NAME + "(" +  USER_NAME_FIELD + ", " + USER_RECEIVEPORT_FIELD  + ")"; 
	            String createPortIndex = "CREATE INDEX " + PORT_INDEX + " ON "+ TABLE_NAME + "(" +  USER_RECEIVEPORT_FIELD +  ")"; 
	            String createNameIndex = "CREATE INDEX " + NAME_INDEX + " ON "+ TABLE_NAME + "(" +  USER_NAME_FIELD +  ")"; 
				db.createTable(createTableQuery);
				db.createIndex(createNameQuery);
				db.createIndex(createNameIndex);
				db.createIndex(createPortIndex);
				db.commit();
			} catch (SqlJetException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		
	}

	
	public static void insertItem(String name, String send_port, String rec_port, String userKey, String publicKey, String ser_addr, String ser_port) throws SqlJetException{
		db.beginTransaction(SqlJetTransactionMode.WRITE);
	    ISqlJetTable table = db.getTable(TABLE_NAME);
	    table.insert(name, send_port, rec_port, userKey, publicKey, ser_addr, ser_port);
	    db.commit();
	}
	
    private static void printRecords(ISqlJetCursor cursor) throws SqlJetException {
	    try {
	      if (!cursor.eof()) {
	        do {
	        	ClientLogger.log(cursor.getRowId() + " : " + 
	                             cursor.getString(USER_NAME_FIELD) + " has sending port " + 
	                             cursor.getString(USER_SENDPORT_FIELD) + " and receving port " + 
	                             cursor.getString(USER_RECEIVEPORT_FIELD) + " and secret key " + cursor.getString(USER_KEY_FIELD) + cursor.getString(USER_KEY_ADDRESS_FIELD));
	         } while(cursor.next());
	      }
	    } finally {
	      cursor.close();
	    }
	}
    
    public static void getRecords() throws SqlJetException{
    	db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
  	    ISqlJetTable table = db.getTable(TABLE_NAME);
		
		  try {
		    printRecords(table.order(table.getPrimaryKeyIndexName()));
		  } finally {
		    db.commit();
		  }
    }
   
    public static boolean deleteRecord(String name, String recPort){
    	try{
    		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
    		ISqlJetCursor cursor = db.getTable(TABLE_NAME).lookup(NAMEPORT_INDEX, name, recPort);
    		cursor.delete();
    		return true;
    	}catch(SqlJetException e){
    		return false;
    	}
    }
    public static String[] lookupRecordByName(String name){
    	try{
    		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
    		ISqlJetCursor cursor = db.getTable(TABLE_NAME).lookup(NAME_INDEX, name);
    		ClientLogger.log(new String(cursor.getValue(USER_NAME_FIELD) + Long.toString(cursor.getInteger(USER_SENDPORT_FIELD))));
        	String[] res = {cursor.getString(USER_NAME_FIELD),  cursor.getString(USER_SENDPORT_FIELD), cursor.getString(USER_RECEIVEPORT_FIELD), cursor.getString(USER_KEY_FIELD), cursor.getString(PUBLIC_KEY_FIELD), (String)cursor.getValue(USER_KEY_ADDRESS_FIELD), cursor.getString(USER_KEY_PORT_FIELD)};
        	db.commit();
        	return res;
    	}catch(SqlJetException e){
    		ClientLogger.log("An error in database has ocurred.");
    		return null;
    	}
    	
    }
    
    public static String[] lookupRecordByRecPort(String recPort){
    	try{
    		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
    		ISqlJetCursor cursor = db.getTable(TABLE_NAME).lookup(PORT_INDEX, recPort);
    		ClientLogger.log(new String(cursor.getValue(USER_NAME_FIELD) + Long.toString(cursor.getInteger(USER_SENDPORT_FIELD))));
        	String[] res = {cursor.getString(USER_NAME_FIELD),  cursor.getString(USER_SENDPORT_FIELD), cursor.getString(USER_RECEIVEPORT_FIELD), cursor.getString(USER_KEY_FIELD), cursor.getString(PUBLIC_KEY_FIELD), (String)cursor.getValue(USER_KEY_ADDRESS_FIELD), cursor.getString(USER_KEY_PORT_FIELD)};
        	db.commit();
        	return res;
    	}catch(SqlJetException e){
    		return null;
    	}
    	
    }
    
    public static String[] lookupRecordByNameAndPort(String name, String recPort){
    	try{
    		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
    		ISqlJetCursor cursor = db.getTable(TABLE_NAME).lookup(NAMEPORT_INDEX, name, recPort);
    		ClientLogger.log(new String(cursor.getValue(USER_NAME_FIELD) + Long.toString(cursor.getInteger(USER_SENDPORT_FIELD))));
        	String[] res = {cursor.getString(USER_NAME_FIELD),  cursor.getString(USER_SENDPORT_FIELD), cursor.getString(USER_RECEIVEPORT_FIELD), cursor.getString(USER_KEY_FIELD), cursor.getString(PUBLIC_KEY_FIELD), (String)cursor.getValue(USER_KEY_ADDRESS_FIELD), cursor.getString(USER_KEY_PORT_FIELD)};
        	db.commit();
        	return res;
    	}catch(SqlJetException e){
    		return null;
    	}
    	
    }
	
	public static void clearDataBase(){
		File dbFile = new File(DB_NAME);
		dbFile.delete();
	}
	
	public static void closeConnect(){
		try {
			db.close();
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
