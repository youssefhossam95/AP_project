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

public class Searcher {
String ReqSearch;
DBmanager db;
String[] SplitWords;
double [] IDF;
double [] TF;
int[] IDs;

int NumFetchedPages=0;


Searcher(String Q, DBmanager db)
{
	ReqSearch=Q;
	this.db=db;
	SplitWords=ReqSearch.split(" ");
	IDF= new double[SplitWords.length];
	TF= new double[SplitWords.length];
	IDs= new int[SplitWords.length];
}



public void calculateIDF() throws SQLException
{
	int idTemp;
	int pagesTemp;
	for(int i=0; i<SplitWords.length;i++)
	{
		idTemp=db.GetID1(SplitWords[i]);
		IDs[i]=idTemp;
	
		pagesTemp=db.NumPagesOfThisWord(idTemp);
		double a= NumFetchedPages/pagesTemp;
		IDF[i]=Math.log10(a);
		
	}
}


public void calculateTF()
{
	
}

public void ElLeilaa()
{
	for(int i=0;i<SplitWords.length;i++)
	{
		ResultSet rs=db.GetURL(IDs[i]);
		try {
			while(rs.next())
			{
				String tempURL=rs.getString(1);
				int NumOfOccOfThisWord=db.GetCountOfThisWord(tempURL, IDs[i]);
				int AllWords=db.GetNumOfWords(tempURL);
				
				
				
				
				
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
}
public void run(){}}

	




