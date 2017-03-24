
package  ap.project;

import java.sql.*;
import java.io.* ; 
import com.microsoft.sqlserver.jdbc.*;

import ap.project.Stemmer;
public class Indexer implements Runnable  {
	
	public ResultSet rs; 
	DBmanager DB; 
	 int MaxID =0 ; 
	int UContainsCount = 0 ;
	public void run () {
		
		ProcessPageTable();
		
	}

	Indexer (ResultSet Rs , DBmanager DataBase  )
	{
		rs = Rs ; 
		DB = DataBase ; 
	
		
		
	}
	public  boolean NextRs ()
	{
		boolean x = false ; 
		try {
			x = rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ana fe NEXT RS ");
			e.printStackTrace();
		} 
		
		return x ; 
	}
	
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
	
	public  void GetMaxID ( )
	{
		
		String query = "SELECT max(ID) AS ID FROM Word" ; 
		try {
		
			ResultSet rs = DB.executeQuery(query);
			rs.next(); 
			 MaxID = rs.getInt("ID");  ;

		} catch (SQLException e) {
			System.out.println("ana fe GetMax ID ");

			e.printStackTrace();
		}
		 
		
		
		
	}
	
	
	synchronized public void InsertWords (String Word )
	{
		String query = "SELECT * FROM Word WHERE Text ='" + Word +"'" ; 

		ResultSet Rs = DB.executeQuery(query);
		try {
			if (! Rs.next())
				{
					if (MaxID == 0 )
						 GetMaxID(); 
					
					MaxID = MaxID + 1 ; 
					query = "INSERT INTO Word VALUES ( " +String.valueOf(MaxID) +
							",'" +Word+ "')" ; 

			    	DB.executeUpdate(query);
			    	System.out.println("inserted the Word: " + Word );
					
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ana fe InsertWords ");

			e.printStackTrace();
		}
		
	}
	
	
	public void InsertContains (String Word ,String URL, String Priority ,String Index)
	{
		
		String query = "SELECT * FROM Word WHERE Text ='" + Word +"'" ; 

		try {
				ResultSet rs = DB.executeQuery(query);
				rs.next() ;
				String ID = rs.getString("ID") ;
					
					
					
				query = "INSERT INTO UContains VALUES ( '" +
					URL+ "'," + ID+ ","+ Priority+ ","+ Index + ")" ; 
				System.out.print(Thread.currentThread().getName());
				//System.out.println(query);
				System.out.println(UContainsCount++);
		     DB.executeUpdate(query);
					
				

		} catch (SQLException e) {
			System.out.println("Problem in INsert contains for the query : ");

			System.out.println(query);

			e.printStackTrace();
		}
		
	}
	public  void HandleText ( String Text, String URL, String Priority)
	{
		if (Text == null)
			return ; 	
		String [] words = Text.split(" "); 
	    Stemmer s = new Stemmer();
	    int Index =  1 ; 
		for (int i = 0 ; i < words.length ; i ++)
			{
				String str = s.GetStemedString(words[i]); 
				if(str == null)
					continue ; 
				InsertWords( str);
				InsertContains(str, URL , Priority,String.valueOf(Index));
				Index += 1 ; // index of the word in the paragraph 
			}
		
	}
	
	synchronized public  boolean GetTextFromRs ( String[] output ){
		 
		boolean out = false ; 
		try {
			
		out = NextRs(); 
		
		if (out == false)
			return out ; 
		output[0] = rs.getString("URL"); 
		
		output[1]  = rs.getString("body");
		
		output[2] = rs.getString("Headers"); 
		
		output[3] = rs.getString("Title");
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ana fe GetTExt formm rs ");
			e.printStackTrace();
		} 
		
		return out ; 
		
		
	}
	public  void ProcessPageTable ( ) 

	{
	
		String[] Text = new String [4]; // 0 :URL 1:body 2:Headers 3:Title 
		while (GetTextFromRs(Text)) {
			
			HandleText ( Text[1], Text[0] , "1"); // handle body 
			
			HandleText (  Text[2],  Text[0] , "2"); // handle headers 
		  		
			HandleText (  Text[3],  Text[0] , "3");  // handle Title 
			
			
		}
	}
	
	public static void main(String[] args) {
	// Declare the JDBC objects.
//			Connection con = null;
//			CallableStatement cstmt = null;
//			ResultSet rs = null;
//			Statement st = null ; 
//			int MaxID = 0 ; 
//			
			String query = "SELECT * FROM Page"; 
			DBmanager DataBase = new DBmanager ();
			ResultSet rs = DataBase.executeQuery(query); 
			ResultSet rs2 = DataBase.executeQuery(query); 
			Indexer I1 = new  Indexer(rs , DataBase );
			
			
			int threadNum = 8 ; 
			
			Thread [] threads = new Thread [threadNum]; 
			for (int i = 0 ; i <threadNum ; i ++ ){
				threads[i]= new Thread (I1);
				threads[i].setName("thread " +(i+1)+ " ");
				threads[i].start();
			}
			
			
		//	t1.start();
			
		//	t2.start();
//			Indexer I1 = new  Indexer(rs , DataBase );
//			Indexer I2 = new  Indexer(rs , DataBase );	
//			try {
//				I1.ProcessPageTable(); 
//				System.out.println(I1.rs.getString("URL"));
//				I2.ProcessPageTable(); ; 
//				System.out.println(I1.rs.getString("URL")) ;
//				} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} 
//			Thread[] threads=new Thread[2]; 
//			
//			int counter=0;
//			for(Thread t : threads)
//			{
//				counter++;
//				t=new Thread(new Indexer(rs , DataBase , 0));
//				t.start();
//			}
					
			
			try {
				// Establish the connection. 
//				SQLServerDataSource ds = new SQLServerDataSource();
//				ds.setIntegratedSecurity(true);
//				ds.setServerName("localhost");
//				ds.setPortNumber(1433); 
//				ds.setDatabaseName("SearchEngine");
//				con = ds.getConnection();
				
//				st = con.createStatement(); 
//				DBmanager DB = new DBmanager (); 
				
				//InsertWords (DB, "hello", 1); 
				 
		        	// Execute a stored procedure that returns some data.
//	            		cstmt = con.prepareCall("{call dbo.get_colours()}");
//	            		rs = cstmt.executeQuery();
//
	        	

		        }
		        
			// Handle any errors that may have occurred.
		    	catch (Exception e) {
		    		e.printStackTrace();
		    	}
			
				
//		   	finally {
//		    		if (rs != null) try { rs.close(); } catch(Exception e) {}
//		    		if (cstmt != null) try { cstmt.close(); } catch(Exception e) {}
//		    		if (con != null) try { con.close(); } catch(Exception e) {}
//		    	}
		}
	}


