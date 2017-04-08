package ap.project;
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
public class CrawlerMain {

public static void main(String[] args) {
		
		int pagesThreshold; //stopping condition.
		System.out.println("Enter the number of threads you'd like to use: ");
		Scanner s=new Scanner(System.in);
		int threadsCount=s.nextInt();
		System.out.println("Would you like to?\n1:Start crawling from scratch\t2:Contiune from last time");
		int choice=s.nextInt();
		System.out.println("How many pages do you like to scrap?");
		pagesThreshold=s.nextInt();
		System.out.println("Starting the crawler with "+threadsCount+" threads...");
		try
		{
			Thread.sleep(1000);
		}
		catch(InterruptedException e)
		{
			System.out.println(e.getMessage());
		}
		
		//ronaldo,machine learning ,rihanna,bbc
		String[] initialLinks={
				"https://en.wikipedia.org/wiki/Machine_learning"
				,"https://en.wikipedia.org/wiki/Rihanna",
				"https://en.wikipedia.org/wiki/Cristiano_Ronaldo",
				"http://www.bbc.com/news","http://www.howstuffworks.com/"};
		LinkedBlockingQueue<String> links=new LinkedBlockingQueue<String>();
		DBmanager DBman=new DBmanager();
		
		
		if(choice==1)//start from scratch
		{
			System.out.println("Clearing existing database...");
			DBman.executeUpdate("Delete from PointsTo"); //clear database.
			DBman.executeUpdate("Delete from UContains"); 
			DBman.executeUpdate("Delete from Page");
		}
		else
		{
			//getting the last fetched 10 pages.
			String URL=null;
			ResultSet rs=DBman.executeQuery("SELECT TOP 10 * FROM page ORDER BY LastFetched DESC;");
			ArrayList<String> URLs=new ArrayList<String>();
			ArrayList<String>neighbourLinks=new ArrayList<String>();
			try {
				 while(rs.next())
					 URLs.add(rs.getString("URL"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Elements docLinks=null;
			Document doc = null;
			for(int j=0;j<URLs.size();j++) //for every page get its neighbour links and add them to the list.
			{
				try {
					
					doc = Jsoup.connect(URLs.get(j)).get();
					docLinks = doc.select("a[href]");
				} catch (Exception e) {
					e.printStackTrace();
					continue; //problem with the current link->go to next
				}
				
		        for(int i=0;i<docLinks.size();i++)
		        {
		        	neighbourLinks.add(docLinks.get(i).attr("abs:href"));
		        }
			}
	        initialLinks=new String[neighbourLinks.size()];
	        int i=0;
			for(String link: neighbourLinks)
			{
				initialLinks[i]=link;
				i++;
			}
		}
		
		for(String link : initialLinks) 
		{
			try {
				links.put(link);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
		System.out.println("Crawling Started!");
		Thread[] threads=new Thread[threadsCount];
		AtomicInteger pagesCount=new AtomicInteger();
		Integer counter=0;
		ConcurrentHashMap<String,TimeCapsule> blockedMap=new ConcurrentHashMap<String,TimeCapsule>();
		for(Thread t : threads)
		{
			counter++;
			t=new Thread(new Crawler(links,DBman,pagesCount,pagesThreshold,blockedMap),counter.toString());
			t.start();
		}
		int last=0;
		int count;
		while((count=pagesCount.get())<pagesThreshold) //wait for stopping condition.
		{
			if(count%5==0 && last!=count )
			{
				System.out.println(count+" pages scrapped");
				System.out.println(links.size());
				last=count;
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		System.out.println("Done with crawling!");
		

	}
	
}
