package agi.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class DatabaseConnection {
	private static Connection connection = null;
	private static DatabaseConnection instance = null;
	private Logger log;
	private String dbUsername;
	private String dbPassword;
	private String dbUrl;
	private String dbPort;
	private String dbName;
	
	public DatabaseConnection() {
		log = Logger.getLogger(getClass().getName());
	}
	
	private void readConfig() {
		ResourceBundle myResources = ResourceBundle.getBundle("agi");
		dbUsername = myResources.getString("db.user");
		dbPassword = myResources.getString("db.password");
		dbUrl = myResources.getString("db.url");
		dbPort = myResources.getString("db.port");
		dbName = myResources.getString("db.name");
	}
	
	public synchronized static DatabaseConnection getInstance() {
		if(instance == null) {
			instance = new DatabaseConnection();
		}
		return instance;
	}
	
	public Connection getConnection() {
		if(!isConnected()) {
			connect();
		} 
		return connection;
	}
	
	public boolean isConnected() {
		try {
			return (connection != null) && !connection.isClosed();
		} catch (SQLException e) {
			log.warn("DatabaseConnection: Connection check failed: " + e.getMessage(), e);
			return false;
		}
	}
	
	public synchronized boolean connect() {
		if(!isConnected()) {
			try {
				readConfig();
				
				Class.forName("com.mysql.jdbc.Driver");
				
				String url = "jdbc:mysql://" + this.dbUrl + ":" + this.dbPort + "/" + this.dbName + "?user=" + this.dbUsername 
						+ "&password=" + this.dbPassword;
				
				log.info("DatabaseConnection: Connecting to " + url);
				
				connection = DriverManager.getConnection(url);
				
			} catch (SQLException e) {
				
				log.error("DatabaseConnection: Connection failed: " + e.getMessage(), e);
				return false;
				
			} catch (ClassNotFoundException e) {
				log.error("DatabaseConnection: Driver nod found: " + e.getMessage(), e);
				return false;
			}
		} else {
			log.info("DatabaseConnection: already connected");
		}
		return true;
	}
	
	public synchronized void close() {
		if(isConnected()) {
			try {
				connection.close();
			} catch (SQLException e) {
				log.error("DatabaseConnection: Connection closure failed: " + e.getMessage(), e);
				e.printStackTrace();
			}
		}
	}
}