//package ap.project;
//import java.util.Scanner;
//import java.util.concurrent.LinkedBlockingQueue;
//public class CrawlerMain {
//
//	public static void main(String[] args) {
//		System.out.println("Enter the number of threads you'd like to use: ");
//		Scanner s=new Scanner(System.in);
//		int threadsCount=s.nextInt();
//		System.out.println("Starting the crawler with "+threadsCount+" threads...");
//		try
//		{
//			Thread.sleep(1000);
//		}
//		catch(InterruptedException e)
//		{
//			System.out.println(e.getMessage());
//		}
//		System.out.println("Crawling Started!");
//		
//		//ronaldo,machine learning ,rihanna
//		String[] initialLinks={
//				"https://en.wikipedia.org/wiki/Cristiano_Ronaldo",
//				"https://en.wikipedia.org/wiki/Machine_learning",
//				"https://en.wikipedia.org/wiki/Rihanna"};
//		LinkedBlockingQueue<String> links=new LinkedBlockingQueue<String>();
//		
//		for(String link : initialLinks)
//		{
//			try {
//				links.put(link);
//			} catch (InterruptedException e) {
//				
//				e.printStackTrace();
//			}
//		}
//		
//		Thread[] threads=new Thread[threadsCount];
//		
//		for(Thread t : threads)
//		{
//			t=new Thread(new Crawler(links));
//			t.start();
//		}
//		s.close();
//		
//
//	}
//
//}
