//package ap.project;
//import java.sql.*;
//import com.microsoft.sqlserver.jdbc.*;
//public class TestMainsql {
//
//	public static void main(String[] args) {
//	// Declare the JDBC objects.
//			Connection con = null;
//			CallableStatement cstmt = null;
//			ResultSet rs = null;
//			
//			try {
//				// Establish the connection. 
//				String url="jdbc:microsoft:sqlserver://tcp:YOUSSEF:1433;";
//				SQLServerDataSource ds = new SQLServerDataSource();
//				ds.setIntegratedSecurity(true);
//				//ds.setServerName("tcp:YOUSSEF");
//				//ds.setPortNumber(1433); 
//				ds.setDatabaseName("MenZone");
//				con = ds.getConnection(url,"");
//				 
//		        	// Execute a stored procedure that returns some data.
//	            		
//	            		Statement stmt=con.createStatement();
//	            		stmt.executeUpdate("Insert into Pants values(17,'cotton')");
//	            		rs = stmt.executeQuery("select * from Pants where Make='Cotton'");
//	            		if(rs.getFetchSize()==0){System.out.println("hahahah");}
//		        	// Iterate through the data in the result set and display it.
//		        	while (rs.next()) {
//		            		System.out.println(rs.getString("Pants_ID")+ rs.getString("Make"));
//		        	}
//		        }
//		        
//			// Handle any errors that may have occurred.
//		    	catch (Exception e) {
//		    		e.printStackTrace();
//		    	}
//
//		   	finally {
//		    		if (rs != null) try { rs.close(); } catch(Exception e) {}
//		    		if (cstmt != null) try { cstmt.close(); } catch(Exception e) {}
//		    		if (con != null) try { con.close(); } catch(Exception e) {}
//		    	}
//		}
//	}
