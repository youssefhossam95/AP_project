package ap.project;
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
		ds.setDatabaseName("MenZone");
		try {
			con = ds.getConnection(url,"");
		} catch (SQLServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	synchronized public boolean executeUpdate(String stat) //returns false if update failed.
	{
		Statement stmt = null;
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try {
			stmt.executeUpdate(stat);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ResultSet rs=stmt.executeQuery(stat);
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
}
