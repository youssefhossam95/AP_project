package aptSE;
import java.util.Scanner;

import java.sql.*;
import java.io.* ; 
import com.microsoft.sqlserver.jdbc.*;

import aptSE.Stemmer;
public class Indexer implements Runnable  {
	
	public ResultSet rs; 
	DBmanager DB; 
	 int MaxID =0 ; 
	int UContainsCount = 0 ;
	int IndexedPages =0; 
	public void run () {
		
		ProcessPageTable();
		
	}

	Indexer (ResultSet Rs , DBmanager DataBase  )
	{
		rs = Rs ; 
		DB = DataBase ; 
		GetMaxID(); 

		
		
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
			 MaxID = rs.getInt("ID")+1  ; // maxId contain the ID of the next word to be inserted 

		} catch (SQLException e) {
			System.out.println("ana fe GetMax ID ");

			e.printStackTrace();
		}
		 
		
		
		
	}
	
	public void GetUContainsCount () 
	{
		String query = "SELECT COUNT(*) AS Ucount FROM UContains" ; 
		try {
		
			ResultSet rs = DB.executeQuery(query);
			rs.next(); 
			 UContainsCount = rs.getInt("Ucount");  ;
			

		} catch (SQLException e) {
			System.out.println("ana Get UContain count ");

			e.printStackTrace();
		}
		
	}
	
	
	synchronized public void InsertWords (String Word, String StemmedWord )
	{
		//String query = "SELECT * FROM Word WHERE Text ='" + Word +"'" ; 

		try {
				
			    	DB.InsertWord(MaxID, Word, StemmedWord , Word.length()- StemmedWord.length());

					MaxID = MaxID + 1 ; 
					if (MaxID % 1000 == 0 )
						System.out.println("Words in The Data Base = "+ MaxID);
						
					
			    	//System.out.println("inserted the Word: " + Word );
					
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		//	System.out.println("ana fe InsertWords ");
		   //   Scanner scanner = new Scanner (System.in);
		   //   scanner.nextLine(); 
		//	e.printStackTrace();
		}
		
	}
	
	
	public void InsertContains (String Word ,String URL, int Priority ,int Index)
	{
		

		try {
				ResultSet rs = DB.GetWordID(Word);
				rs.next() ;
				String ID = rs.getString("ID") ;
					
		
				//System.out.print(Thread.currentThread().getName());
				//System.out.println(query);
				UContainsCount ++ ; 
				if (UContainsCount % 2000 == 0)
					System.out.println("Ucontains Records= " + UContainsCount);
		     DB.InsertUContains(URL, Integer.parseInt(ID), Priority, Index);
					
				

		} catch (SQLException e) {
			System.out.println("Problem in INsert contains for the query : ");

		//	System.out.println(query);

			e.printStackTrace();
		}
		
	}
	public  void HandleText ( String Text, String URL, int Priority)
	{
		if (Text == null)
			return ; 	
	    String[]words = Text.split("[ \\[\\]\"?!@{}()/<>;:,._=*&#^$@+-]"); 
	    Stemmer s = new Stemmer();
	    int Index =  1 ; 
		for (int i = 0 ; i < words.length ; i ++)
			{
				String str = s.GetStemedString(words[i]); 
				if(str == null)
					continue ; 
				InsertWords(  words[i] , str);
				InsertContains(words[i], URL , Priority,Index);
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
		    DB.DeleteURL(Text[0]); // delete any any records in the ucontains table with this url 
		    GetUContainsCount(); 
		    System.out.println(Thread.currentThread().getName() +" is Now Indexing" + Text[0]);
		    HandleText ( Text[1], Text[0] , 1); // handle body 
			
			HandleText (  Text[2],  Text[0] , 2); // handle headers 
		  		
			HandleText (  Text[3],  Text[0] , 3);  // handle Title 
			
			DB.MarkURLIndexed(Text[0]);
			System.out.println("Finished indexing:"+Text[0]);
			System.out.println("Indexed Pages= " + ++IndexedPages);
		}
	}
	
	public static void main(String[] args) throws SQLException {
	// Declare the JDBC objects.
//			Connection con = null;
//			CallableStatement cstmt = null;
//			ResultSet rs = null;
//			Statement st = null ; 
//			int MaxID = 0 ; 
//			
			String query = "SELECT * FROM Page WHERE isIndexed = 0 "; 
			DBmanager DataBase = new DBmanager ();
	
			ResultSet rs = DataBase.executeQuery(query); 
			
			 query = "SELECT COUNT(*) AS p FROM Page" ;
			 
			 ResultSet Temp = DataBase.executeQuery(query); 
			 Temp.next(); 
			 int NumberOfPages = Temp.getInt("p"); 
			 Temp = DataBase.executeQuery("SELECT count(*) as p from Page where [isIndexed] = 0"); 
			 Temp.next(); 
			 int UnIndexedPages = Temp.getInt("p"); 
			 
			Indexer I1 = new  Indexer(rs , DataBase );	
			I1.IndexedPages = NumberOfPages - UnIndexedPages   ; 
			
			System.out.println("number of indexed pages =" +I1.IndexedPages);

////			
			Scanner sc = new Scanner (System.in); 
			System.out.println("Enter Number of threads you want to use:");
			
			int threadNum = sc.nextInt(); 
////			
			System.out.println("Starting The Indexing Process...");

			Thread [] threads = new Thread [threadNum]; 
			for (int i = 0 ; i <threadNum ; i ++ ){
				
				threads[i]= new Thread (I1);
			
				threads[i].setName("Thread " +(i+1)+ " ");
				threads[i].start();
			}
//			
			for (int i = 0 ; i <threadNum ; i ++ ){
				
				try {
					threads[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			} 
		System.out.println("Indexing is OVER!");
			
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


