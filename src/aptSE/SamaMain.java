package aptSE;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class SamaMain {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		DBmanager db=new DBmanager();
		
		String Word = "\"machine learning\"" ;
		System.out.println(Word);
		
//		Word = Word.substring(1, Word.length()-1 ); 
		
//		System.out.println(Word);
		Searcher s  = new Searcher (Word , db); 
		
		String [] ur = s.execute() ; 
		for (int i = 0 ; i < ur.length ; i++)
		{
			
			System.out.println(ur[i])  ; 
			
			
		}
		
//		String query ="SELECT TOP (10) [ID],[Text],[StemmedText] ,[Difference]"
//				+ "  FROM [SearchEngine].[dbo].[Word] order by ID" ;
//		
//		ResultSet rs = db.executeQuery(query); 
//		
//		double average = 0 ; 
//		double count  = 0 ; 
//		
//		while (rs.next())
//		{
//			String Word = rs.getString("Text"); 
//			Searcher s  = new Searcher (Word , db); 
//			s.execute() ; 
//			average += s.avg ;  
//			count ++  ;
//		}
//		
//		average /= count ;
//		System.out.println(average);
		
		
		
		
		//ResultSet rs=db.GetWordID("and");
		//int id=db.GetID1("and");
		//rs.next();
		//System.out.print(id);
		//int number=db.GetNumOfFetchedPages();
		//System.out.print(number);
		//int id=db.GetID1("Accessibility");
		//System.out.print(id);
		//String s=db.GetStemmedWord(id);
		
		//System.out.println(s);
		
//		ResultSet rs=null;
//		rs=db.GetURL(id);
//		while(rs.next())
//		{
//			String s=rs.getString(1);
//			System.out.println(s);
//		}
		///////////////////////////////////////int num=db.GetNumPagesOfThisWord(id);
		//int num=db.NumPages(id);
		//System.out.print(num);
		//https://en.wikipedia.org/wiki/Big_data
		
//		String url="https://en.wikipedia.org/wiki/Big_data";
////		int num= db.GetNumOfWord(url);
////		System.out.print(num);
//		//int num=db.GetCountOfThisWord(url, id);
//		ResultSet rs=db.GetPriority(13);
//		while(rs.next())
//		{
//			int num=rs.getInt(1);
//			System.out.print(num);
//		}
//		ArrayList<ArrayList<Integer>> oout=new ArrayList<ArrayList<Integer>>();
//		ArrayList<Integer> inn=new ArrayList<Integer>();
//		
//		inn.add(1);
//		inn.add(2);
//		oout.add(inn);
//		inn.clear();
//		inn.add(3);
//		inn.add(4);
//		oout.add(inn);
//		inn.clear();
//		inn.add(5);
//		inn.add(6);
//		oout.add(inn);
//		System.out.print(oout.size());
//		System.out.print(oout.get(0).size());
//		
		
//		String x = "williams \"\"lolo\" [what]  "; 
//		String [] w = x.split("[\\[\\]\"]");
//		for ( int i = 0 ; i <w.length ; i ++)
//			System.out.println(w[i]);
//		System.out.println(x);
		
		//System.out.print(num);
	}

}
