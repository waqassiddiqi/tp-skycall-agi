package agi.db;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class MappingDao {
	protected DatabaseConnection db;
	private Logger log = Logger.getLogger(getClass().getName());
	
	public MappingDao() {
		db = DatabaseConnection.getInstance();
	}
	
	public String getMapping(String virtualId, String msisdn) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = this.db.getConnection().prepareCall("{ call sp_getSkypeId(?, ?) }");
			stmt.setString(1, virtualId);
			stmt.setString(2, msisdn);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString(1);
			}
			
		} catch (SQLException e) {
			log.error("getMapping failed: " + e.getMessage(), e);
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
