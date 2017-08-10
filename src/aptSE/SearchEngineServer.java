package aptSE;
import aptSE.DBmanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
/**
 * Servlet implementation class SearchEngineServer
 */
@WebServlet(
		description = "Server of the our search engine project", 
		urlPatterns = { 
				"/SearchEngineServerPath", 
				"/APTSE"
		})
public class SearchEngineServer extends HttpServlet {
	DBmanager db;
	private static final long serialVersionUID = 1L;
	DBmanager DB;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchEngineServer() {
    	
        super();
        db=new DBmanager();
        // TODO Auto-generated constructor stub
        DB=new DBmanager();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter Out=response.getWriter();
		String Diff=request.getParameter("someFieldId");
		String SearchWord=request.getParameter("SearchTextBox");
		if(Diff!=null)
		{
			if(Integer.parseInt(Diff)==0)
			{
			if(SearchWord!=null)
				{
					Out.write(GetEquivalentWords(SearchWord));
				}
				return;
			}
		
		}
		else 
		{
			String[] Links=null;
			Searcher s= new Searcher(SearchWord,db);
			try {
				Links=s.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SaveSearchedWord(SearchWord);
			String CSSData=  "<link rel="+"\"stylesheet\""+ "href="+"\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css \">";
			CSSData+="<script src="+" \"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>";
			CSSData+="<script src="+" \"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>";			
			String Header="<head>"+CSSData+"<form method="+"\""+"GET"+"\""+ "action="+"\""+"SearchEngineServerPath"+"\""+" >"+ "<input  type="+"text"+" size="+"100"+"  value="+"\""+SearchWord+" \" " +" name="+"SearchTextBox"+">"+"<input value="+"\""+"Search "+"\""+ "type="+"\""+"submit"+"\""+" /> </form>";
			String DIV="<div style="+"\" "+"float: left;"+"\""+"><img src="+"../SmallDowarley.gif " +" width="+"200 "+ "height="+"100 " +"alt="+"12 "+"></div>";
			String URL="";
			for(int i=0;i<Links.length;i++)
			{		
				if(Links[i]!=null)
				{
					URL+=URLPrint(Links[i],Links[i],db.GetPageTitle(Links[i])); //Unclosed quotation mark after the character string ' '.s.GetPageTitle(Links[i])
					
				}

			}
			//WriteToFile("C:\\Users\\Dell\\Documents\\GitHub\\AP_project\\LastQuery.txt",Links);
			String PagesAvailable="<div class="+"\"container\">" 
					+     "<ul class="+"\"pagination\">" 
					+    "<li class="+"\"active\">"+"<a href="+"\"#\">1</a></li>"
					+    "<li><a href="+"\"#\">2</a></li>"
					+    "<li><a href="+"\"#\">3</a></li>"
					+    "<li><a href="+"\"#\">4</a></li>"
					+    "<li><a href="+"\"#\">5</a></li></ul></div>";
			Out.println(Header+DIV+URL+PagesAvailable+"</html");
		}
			
		



		
	
	}
	public void SaveSearchedWord(String SearchWord)
	{
		String Query="If Not Exists( Select SearchedPhrases from SearchWords where SearchedPhrases=\'"+SearchWord+"\') Begin Insert SearchWords([SearchedPhrases]) Values(\'"+SearchWord+"\') End;";
		System.out.println("3ash");
		DB.executeUpdate(Query);
		return;
	}
	public String GetEquivalentWords(String Word)
	{
		StringBuffer returnData=new StringBuffer();
		String query = "select * from SearchWords where SearchedPhrases like"+"'"+Word+"%'" ; 
		ResultSet Rs=DB.executeQuery(query);
		try {
			
			while(Rs.next())
			{
				returnData.append(Rs.getString("SearchedPhrases")+",");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnData.toString();
	}
protected String URLPrint(String URLBlue,String URLGreen,String Caption)
{
	String ToPrint;
	ToPrint="<div style="+"\"" +" width: 100%; overflow: hidden;"+"\" "+"> ";
	ToPrint+="<font size="+" \" 5 \" >"+"<a href="+"\""+URLBlue+"\" >"+Caption+"</a></font>";
	ToPrint+="  <p><font face="+"\""+"verdana "+"\""+ "color="+"\""+"green"+"\""+" >"+URLGreen+"</font></p> </div>";
	return ToPrint;
}

public static void WriteToFile (String fileName , String[] text ){
	
	try {
		
	//	FileWriter fw = new FileWriter (fileName); 
		File file=new File(fileName);
		file.delete();
		PrintWriter out = new PrintWriter(new FileOutputStream (new File (fileName), true));
		out.write(text[0]);
		out.println("");
		for ( int i = 1 ; i < text.length ;  i ++)
		{
			out.append(text[i]) ; 
			out.println(); 	
		}
		out.close();
		
	} catch (  IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}



}



