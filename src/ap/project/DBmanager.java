package ap.project;
import java.io.ByteArrayInputStream;
import java.sql.*;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
public class DBmanager {

	private Connection con;
	DBmanager()
	{
		String url="jdbc:microsoft:sqlserver://tcp:YOUSSEF:1433;";
		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setIntegratedSecurity(true); 
		ds.setDatabaseName("SearchEngine");
		try {
			con = ds.getConnection(url,"");
		} catch (SQLServerException e) {
			
			e.printStackTrace();
		}
	}
	
	synchronized public boolean executeUpdate(String stat) //returns false if update failed.
	{
		Statement stmt = null;
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
		try {
			stmt.executeUpdate(stat);
		} catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
		return true;
	}
	synchronized public ResultSet executeQuery(String stat)
	{
		Statement stmt = null;
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		try {
			ResultSet rs=stmt.executeQuery(stat);
			return rs;
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	synchronized public void updatePage(String text,String title,String headers,String URL)
	{
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall("{call updatePage(?,?,?,?)}");
			stmt.setString(1, text);
			stmt.setString(2, title);
			stmt.setString(3, headers);
			stmt.setString(4, URL);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		try {
			stmt.execute();
		
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	synchronized public void insertPointsTo(String source,String dest)
	{
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall("{call insertPointsTo(?,?)}");
			stmt.setString(1, source);
			stmt.setString(2, dest);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		try {
			stmt.execute();
		
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	synchronized public boolean tryInsertPage(String url)
	{
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall("{call tryInsertPage(?)}");
			stmt.setString(1, url);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		try {
			stmt.execute();
		
		} catch (SQLException e) {
			
			if(e.getErrorCode()!=2627) //duplicate key error.
				e.printStackTrace();
			return false;
		}
		return true;
	}
	synchronized public void InsertPage(String url,String text,String title,String header) throws SQLException
	{
		CallableStatement stmt = null;
			stmt = con.prepareCall("{call insertPage(?,?,?,?)}");
			stmt.setString(1, url);
			stmt.setString(2, text);
			stmt.setString(3, title);
			stmt.setString(4, header);
			stmt.execute();
	}
	synchronized public boolean isPageExists(String url)
	{
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall("{call findPage(?,?)}");
			stmt.setString(1, url);
			stmt.registerOutParameter("title", Types.VARCHAR);
			stmt.execute();
			if(stmt.getObject(2) ==null)
				return false;
			else
				return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}
	synchronized public boolean isOldPage(String url)
	{
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall("{call isPageOld(?,?)}");
			stmt.setString(1, url);
			stmt.registerOutParameter("result", Types.INTEGER);
			stmt.execute();
			if(stmt.getInt(2)==0)
				return false;
			else
				return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isNewsPage(String currentURL) {
		return false;
	}
}

