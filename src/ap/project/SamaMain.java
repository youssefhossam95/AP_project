package ap.project;
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
		//ResultSet rs=db.GetWordID("and");
		//int id=db.GetID1("and");
		//rs.next();
		//System.out.print(id);
		//int number=db.GetNumOfFetchedPages();
		//System.out.print(number);
		int id=db.GetID1("Accessibility");
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
		
		String url="https://en.wikipedia.org/wiki/Big_data";
//		int num= db.GetNumOfWord(url);
//		System.out.print(num);
		//int num=db.GetCountOfThisWord(url, id);
		ResultSet rs=db.GetPriority(13);
		while(rs.next())
		{
			int num=rs.getInt(1);
			System.out.print(num);
		}
		
		//System.out.print(num);
	}

}
