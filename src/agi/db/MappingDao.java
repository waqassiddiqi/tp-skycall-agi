package agi.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class MappingDao {
	protected DatabaseConnection db;
	private Logger log = Logger.getLogger(getClass().getName());
	
	public MappingDao() {
		db = DatabaseConnection.getInstance();
	}
	
	public String getMapping(String virtualId) {
		
		ResultSet rs = null;
		String strSql = "SELECT skype_id FROM mapping_tab WHERE virtual_id = ?";
		PreparedStatement  stmt = null;
		
		try {
			stmt = this.db.getConnection().prepareStatement(strSql);
			stmt.setString(1, virtualId.trim());
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString(1);
			}
						
		} catch (Exception e) {
			log.error("getSubscriberByMsisdn failed: " + e.getMessage(), e);
		} finally {
			try {
				if(rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return null;
	}

}
