
package  ap.project;

import java.sql.*;
import java.io.* ; 
import com.microsoft.sqlserver.jdbc.*;

import ap.project.Stemmer;
public class Indexer {
	
public static void WriteToFile (String fileName , String text ){
		
		try {
			
		//	FileWriter fw = new FileWriter (fileName); 
			PrintWriter out = new PrintWriter(new FileOutputStream (new File (fileName), true));
			out.append(text); 
			out.println("");
			out.close();
			
		} catch (  IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static boolean CheckInFile (String fileName, String URL){
		FileReader fr = null;
		try {
			
			fr = new FileReader (fileName);
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} 
		
		
		BufferedReader Br = new BufferedReader (fr);
		
		String VisitedURL = null ; 
		try {
			VisitedURL = Br.readLine() ;
		 
			while (VisitedURL != null)
			{
				if (VisitedURL.equals(URL))
					return true; 
				VisitedURL = Br.readLine(); 
			}
			
			Br.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return false ;

	}
	
	public static int GetMaxID (Statement st )
	{
		int maxID = 0 ; 
		String query = "SELECT max(ID) AS ID FROM Word" ; 
		try {
		
			ResultSet rs = st.executeQuery(query);
			rs.next(); 
			 maxID = rs.getInt("ID");  ;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxID ; 
		
		
		
	}
	
	
	public static void InsertWords (Statement st ,String Word, int MaxID )
	{
		String query = "SELECT * FROM Word WHERE Text ='" + Word +"'" ; 

		try {
			
			ResultSet rs = st.executeQuery(query);
			if (! rs.next() )
				{
					if (MaxID == 0 )
						MaxID = GetMaxID(st); 
					
					MaxID = MaxID + 1 ; 
					query = "INSERT INTO Word VALUES ( " +String.valueOf(MaxID) +
							",'" +Word+ "')" ; 

		        	st.executeUpdate(query);
					
				}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void InsertContains (Statement st ,String Word ,String URL, String Priority ,String Index)
	{
		
		String query = "SELECT * FROM Word WHERE Text ='" + Word +"'" ; 

		try {
				ResultSet rs = st.executeQuery(query);
				rs.next() ;
				String ID = rs.getString("ID") ;
					
					
					
				query = "INSERT INTO UContains VALUES ( '" +
					URL+ "'," + ID+ ","+ Priority+ ","+ Index + ")" ; 
				System.out.println(query);
		     st.executeUpdate(query);
					
				

		} catch (SQLException e) {
			System.out.println("Problem in INsert contains for the query : ");

			System.out.println(query);

			e.printStackTrace();
		}
		
	}
	public static void HandleText (Statement st , String Text, String URL, int MaxID,  String Priority)
	{
		if (Text == null)
			return ; 	
		String [] words = Text.split(" "); 
	    Stemmer s = new Stemmer();
	    int Index =  1 ; 
		for (int i = 0 ; i < words.length ; i ++)
			{
				String str = s.GetStemedString(words[i]); 
				
				InsertWords(st, str, MaxID);
				InsertContains(st,str, URL , Priority,String.valueOf(Index));
				Index += 1 ; // index of the word in the paragraph 
			}
		
	}
	public static void ProcessPageTable (Connection con  ) 
	{
		
		try {
			String query = "SELECT * FROM Page";
			int MaxID = 0 ; 
			Statement st = con.createStatement();
			
			Statement GetPage = con.createStatement() ;
			ResultSet rs = GetPage.executeQuery(query); 
			
			while (rs.next()) {
	    		String URL = rs.getString("URL"); 
	    		
	    		String Text = rs.getString("Body");    	
	    		HandleText (st, Text, URL , MaxID, "1"); 
	    		
	    		Text = rs.getString("Title");     		
	    		HandleText (st, Text, URL , MaxID, "2"); 
	    		
	    		Text = rs.getString("Headers"); 
	    		HandleText (st, Text, URL , MaxID, "3"); 
	    		
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
	// Declare the JDBC objects.
			Connection con = null;
			CallableStatement cstmt = null;
			ResultSet rs = null;
			Statement st = null ; 
			int MaxID = 0 ; 
		
			
			try {
				// Establish the connection. 
				SQLServerDataSource ds = new SQLServerDataSource();
				ds.setIntegratedSecurity(true);
				ds.setServerName("localhost");
				ds.setPortNumber(1433); 
				ds.setDatabaseName("SearchEngine");
				con = ds.getConnection();
				
				st = con.createStatement(); 
				
				
			   ProcessPageTable(con);
				 
		        	// Execute a stored procedure that returns some data.
//	            		cstmt = con.prepareCall("{call dbo.get_colours()}");
//	            		rs = cstmt.executeQuery();
//
	        	

		        }
		        
			// Handle any errors that may have occurred.
		    	catch (Exception e) {
		    		e.printStackTrace();
		    	}
			
				
		   	finally {
		    		if (rs != null) try { rs.close(); } catch(Exception e) {}
		    		if (cstmt != null) try { cstmt.close(); } catch(Exception e) {}
		    		if (con != null) try { con.close(); } catch(Exception e) {}
		    	}
		}
	}


