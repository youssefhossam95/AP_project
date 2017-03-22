package ap.project;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class Crawler implements Runnable {
	//hello
	LinkedBlockingQueue<String> linksQ;
	RobotDetector robot;
	DBmanager Dbman;
	AtomicInteger pagesCount;
	int pagesThreshold;
	Crawler(LinkedBlockingQueue<String> Q,DBmanager man,AtomicInteger count,int l)
	{
		this.linksQ=Q;
		Dbman=man;
		pagesCount=count;
		pagesThreshold=l;
		try {
			robot=new RobotDetector();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void run() {
		String currentURL = null;
		while(!Thread.currentThread().isInterrupted() && pagesCount.get()<pagesThreshold)
		{
			try {
				currentURL= linksQ.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			try {
				currentURL=java.net.URLDecoder.decode(currentURL,"UTF-8"); //hawlo mn el URL encoding el utf-8
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			int index=currentURL.indexOf('#');//sheel el b3d el shbak 3shn byb2a mogrd targeting.
			if(index!=-1)
				currentURL=currentURL.substring(0, index);
			Document doc = null;
			try
			{	
				if((Dbman.tryInsertPage(currentURL) || (Dbman.isOldPage(currentURL) && Dbman.isNewsPage(currentURL))) && robot.isAllowed(currentURL)) //to fetch page it must be either an old news page or a non existing page.robot allow is a must.
				{	
					try {
						doc = Jsoup.connect(currentURL).get();
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					if(doc==null) //problem in fetching page.
						continue;
					pagesCount.incrementAndGet();
					System.out.println("Thread "+Thread.currentThread().getName()+" is crawling...");
				    Elements links = doc.select("a[href]");
				    String text=doc.body().text();
				    String [] neighbourLinks=new String[links.size()];
				    for(int i=0;i<links.size();i++)
				    {
				    	neighbourLinks[i]=links.get(i).attr("abs:href");
				    }
				    String headers=doc.select("h0, h1, h2, h3, h4, h5, h6").text();
				    String title= doc.title();
					Dbman.updatePage(text,title,headers,currentURL);
					for(String link :neighbourLinks) //add all neighbour links to pointsto table and push them in the queue.
					{
						
						Dbman.insertPointsTo(currentURL, link);
						try {
							linksQ.put(link);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println(currentURL+"\n"+link);
						}
					}
				}
			}catch(Exception e){e.printStackTrace();}
			
		}
	}
}
	