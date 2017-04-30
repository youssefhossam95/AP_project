package ap.project;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import javafx.animation.KeyValue.Type;
public class DBmanager {

	public Connection con;
	DBmanager()
	{
		String url="jdbc:microsoft:sqlserver://tcp:AHMED-PC\\SQLEXPRESS:1433;databaseName=SearchEngine;";
		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setIntegratedSecurity(true); 
		ds.setDatabaseName("SearchEngine");
		try {
			con = ds.getConnection(url,"");
		} catch (SQLServerException e) {
			
			e.printStackTrace();
		}
	}
	
	 public ResultSet GetWordID (String Word){
		ResultSet out =null; 
		try {
		CallableStatement stmt = null;
		stmt = con.prepareCall("{call GetWord (?)}");
		
		stmt.setString(1, Word);
		//stmt.registerOutParameter("ID",java.sql.Types.INTEGER);
		out = stmt.executeQuery(); 
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out ; 
	}
	
	 public void InsertWord (int ID , String Word , String StemmedWord , int Difference) throws SQLException{ 
		
		CallableStatement stmt = null;
		stmt = con.prepareCall("{call InsertWord (? , ?, ?, ?)}");
		
		stmt.setInt(1, ID);
		stmt.setString(2, Word);
		stmt.setString(3, StemmedWord);
		stmt.setInt(4, Difference) ; 
		//stmt.registerOutParameter("ID",java.sql.Types.INTEGER);
		stmt.execute(); 
		
		
		
	}
	
	 public void InsertUContains (String URL , int ID , int Priority , int Index ){ 
		try {
		CallableStatement stmt = null;
		stmt = con.prepareCall("{call InsertUContains (? , ? , ? , ?)}");
		
		stmt.setString(1,URL );
		stmt.setInt(2,ID);
		stmt.setInt(3, Priority);
		stmt.setInt(4, Index);

		//stmt.registerOutParameter("ID",java.sql.Types.INTEGER);
		stmt.execute(); 
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	 public void DeleteURL (String URL ){ 
		try {
		CallableStatement stmt = null;
		stmt = con.prepareCall("{call DeleteURL (?)}");
		
		stmt.setString(1,URL );


		//stmt.registerOutParameter("ID",java.sql.Types.INTEGER);
		stmt.execute(); 
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	 public void MarkURLIndexed  (String URL ){ 
		try {
		CallableStatement stmt = null;
		stmt = con.prepareCall("{call MarkURLIndexed (?)}");
		
		stmt.setString(1,URL );


		//stmt.registerOutParameter("ID",java.sql.Types.INTEGER);
		stmt.execute(); 
		
		} catch (SQLException e) {
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
			
			//increment rank by one.
			try {
				stmt=con.prepareCall("{call IncrementRank(?)}");
				stmt.setString(1, url);
				stmt.execute();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
			stmt = con.prepareCall("{call getPageAge(?,?)}");
			stmt.setString(1, url);
			stmt.registerOutParameter("result", Types.INTEGER);
			stmt.execute();
			int x=stmt.getInt(2);
			if(stmt.getInt(2)==-1)//different day 
				return true;
			if(stmt.getInt(2)>12) //more than 12 hours->old page
				return true;
			if(stmt.getInt(2)<2) //less than 2 hours->new
				return false;
			if(isNewsPage(url))//more than 2 hours and news->OLD	
				return true;
			return false; //between 2 and 12 hours but not news.
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isNewsPage(String url) {
		Document doc;
		URL link = null;
		try {
			link = new URL(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return false;
		}
		String homeURL="http://"+link.getHost();
		try {
			doc = Jsoup.connect(homeURL).get();
		} catch (IOException e) {
			
			e.printStackTrace();
			return false;
		}
		try{
			
		String description = doc.select("meta[name=description]").get(0) .attr("content");  
        description=description.toLowerCase();
        
        if(description.contains("news"))
        	return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return false;
	}
////sama
	///--Get the wordID for a specific word without stemming
	public int GetID1(String word) throws SQLException
	{
    	int x;
    	ResultSet rs=this.GetWordID(word);
    	rs.next();
    	if(rs==null) 
    		x=0;
    	else	
    		x=rs.getInt(1);
    	return x;

		
   
		
		
	}
	
	// NUMBER OF FETCHED PAGES
	public int GetNumOfFetchedPages() 
	{int x=0;
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall("{call GetFetchedPages(?)}");
			stmt.registerOutParameter(1, Types.INTEGER);
			ResultSet rs=stmt.executeQuery();
			rs.next();
			x=rs.getInt(1);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			}
	
	
     return x;
}
	
	//--Get the stemmed word for a specific word id
	
	public String GetStemmedWord(int ID)
	{
		String x=" ";
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall("{call GetStemWord(?,?)}");
			stmt.setInt(1, ID);
			stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
			ResultSet rs=stmt.executeQuery();
			rs.next();
			x=rs.getString(1);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			}
	
	
     return x;
	}

	//--Get the urls for a specific wordID 
	public ResultSet GetURL(int ID)
	{
		ResultSet rs =null; 
		try {
		CallableStatement stmt = null;
		stmt = con.prepareCall("{call GetURL (?)}");
		
		stmt.setInt(1, ID);
		
		rs = stmt.executeQuery(); 
		
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return rs ; 
	}

	
	// number of pages for this word
	
	public int NumPagesOfThisWord(int id) throws SQLException
	{
		ResultSet rs=this.GetURL(id);
		int count=0;
		while(rs.next())
		{
			count++;
		}
		return count;
	}

	//--the number of words in this url
	
	public int GetNumOfWords(String url)
	{
		int x=0;
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall("{call GetCountOfWords(?,?)}");
			stmt.setString(1, url);
			stmt.registerOutParameter(2, Types.INTEGER);
			ResultSet rs=stmt.executeQuery();
			rs.next();
			x=rs.getInt(1);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			}
	
	
     return x;
	}

	//--number of occ of this word in this url
	public int GetCountOfThisWord(String URL, int ID)
	{
		int x=0;
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall("{call GetCountOfThisWord(?,?,?)}");
			stmt.setString(1, URL);
			stmt.setInt(2, ID);
			stmt.registerOutParameter(3, Types.INTEGER);
			ResultSet rs=stmt.executeQuery();
			rs.next();
			x=rs.getInt(1);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			}
	
	
     return x;
		
	}
	
	
    // get priority
	public ResultSet GetPriority(int id)
	{
		//int x=0;
		ResultSet rs=null;
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall("{call GetPriority(?,?)}");
			stmt.setInt(1, id);
			stmt.registerOutParameter(2, Types.INTEGER);
			 rs=stmt.executeQuery();
			//rs.next();
			//x=rs.getInt(1);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			}
		//return x;
		return rs;
	}
//  //--get the number of pages that this word occ in
//	public int GetNumPagesOfThisWord(int ID)
//	{
//		int x=0;
//		CallableStatement stmt = null;
//		try {
//			stmt = con.prepareCall("{call GetNumPages(?,?)}");
//			stmt.setInt(1, ID);
//			stmt.registerOutParameter(2, Types.INTEGER);
//			ResultSet rs=stmt.executeQuery();
//			rs.next();
//			x=rs.getInt(1);
//		} 
//		catch (SQLException e)
//		{
//			e.printStackTrace();
//			}
//	
//	
//   return x;
//	}
//
}

