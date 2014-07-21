package agi.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import agi.model.Subscriber;

public class SubscriberDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	
	protected DatabaseConnection db;
	
	public SubscriberDAO() {
		db = DatabaseConnection.getInstance();
	}
	
	public Subscriber getSubscriberByMsisdn(String msisdn) {
		Subscriber sub = null;
		ResultSet rs = null;
		String strSql = "SELECT * FROM subscriber_tab WHERE msisdn = ?";
		PreparedStatement  stmt = null;
		
		try {
			stmt = this.db.getConnection().prepareStatement(strSql);
			stmt.setString(1, msisdn.trim());
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				sub = new Subscriber(rs.getString("msisdn"), rs.getInt("subtype"), rs.getInt("status"));
				sub.setId(rs.getInt("id"));
			}
		} catch (SQLException e) {
			log.error("getSubscriberByMsisdn failed: " + e.getMessage(), e);
		} finally {
			try {
				if(rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return sub;
	}
}