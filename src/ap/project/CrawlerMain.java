package ap.project;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
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
			DBman.executeUpdate("Delete from Page");
		}
		else
		{
			String URL=null;
			ResultSet rs=DBman.executeQuery("SELECT TOP 1 * FROM page ORDER BY LastFetched DESC");
			try {
				 if(rs.next())
					 URL=rs.getString("URL");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			Document doc = null;
			try {
				doc = Jsoup.connect(URL).get();
			} catch (Exception e) {
				e.printStackTrace();
				
			}
			Elements docLinks = doc.select("a[href]");
	        String [] neighbourLinks=new String[docLinks.size()];
	        for(int i=0;i<docLinks.size();i++)
	        {
	        	neighbourLinks[i]=docLinks.get(i).attr("abs:href");
	        }
	        
	        initialLinks=neighbourLinks; //start crawling from neighbour links.
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
		for(Thread t : threads)
		{
			counter++;
			t=new Thread(new Crawler(links,DBman,pagesCount,pagesThreshold),counter.toString());
			t.start();
		}
		int last=0;
		while(pagesCount.get()<=pagesThreshold) //wait for stopping condition.
		{
			int count=pagesCount.get();
			if(count%5==0 && last!=count )
			{
				System.out.println(pagesCount.get()+" pages scrapped");
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
