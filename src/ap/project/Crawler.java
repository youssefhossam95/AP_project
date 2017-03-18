package ap.project;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class Crawler implements Runnable {
	
	LinkedBlockingQueue<String> links;
	DBmanager Dbman;
	Crawler(LinkedBlockingQueue<String> Q,DBmanager man)
	{
		this.links=Q;
		Dbman=man;
	}
	public void run() {
		String currentURL = null;
		while(!Thread.currentThread().isInterrupted())
		{
			try {
				currentURL= links.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			   
			if(Dbman.executeUpdate("Insert into Page columns(URL) values('"+currentURL+"')")) //if current page not already fetched.
			{
				Document doc = null;
				try {
					doc = Jsoup.connect(currentURL).get();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
		        Elements links = doc.select("a[href]");
		        String text=doc.body().text();
		        String [] neighbourLinks=new String[links.size()];
		        for(int i=0;i<links.size();i++)
		        {
		        	neighbourLinks[i]=links.get(i).attr("abs:href");
		        }
		        String headers=doc.select("h0, h1, h2, h3, h4, h5, h6").text();
		        String title= doc.title();
		        
				Dbman.executeUpdate("update Page set Body='"+text+"', Title='"+title+"',Headers='"+headers+"',LastFetched=GETDATE() where ");
			}
			
		}
		
		
		
	}
	
}
