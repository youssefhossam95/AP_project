package ap.project;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class TestMain {
	public static void main(String[] args) throws java.io.IOException {
		// TODO Auto-generated method stub
		        String url = "http://www.asic-world.com/verilog/verilog_one_day2.html";
		        print("Fetching %s...", url);
		        Document doc = Jsoup.connect(url).get();
//		        Elements links = doc.select("a[href]");
//		        Elements media = doc.select("[src]");
//		        Elements imports = doc.select("link[href]");
		        RobotDetector rb=new RobotDetector(new ConcurrentHashMap<String,TimeCapsule>());
//		        System.out.println(rb.wikiBot);
		        //boolean result=rb.test(rb.wikiBot, new URL("https://en.wikipedia.org/ai/rest_v1/?dc"));
		        //System.out.println(result);
		        
		        //System.out.println(dbman.isNewsPage("http://edition.cnn.com/2017/03/22/politics/house-health-care-bill/index.html"));		        
		        

//		        print("\nMedia: (%d)", media.size());
//		        for (Element src : media) {
//		            if (src.tagName().equals("img"))
//		                print(" * %s: <%s> %sx%s (%s)",
//		                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
//		                        trim(src.attr("alt"), 20));
//		            else
//		                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
//		        }
//
//		        print("\nImports: (%d)", imports.size());
//		        for (Element link : imports) {
//		            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
//		        }

//		        print("\nLinks: (%d)", links.size());
//		        for (Element link : links) {
//		            print(" %s", link.attr("abs:href"));
//		        }
//		        
//		        System.out.println(doc.select("h0, h1, h2, h3, h4, h5, h6").text());
		        
//		        System.out.println(doc.title());doc.title();
//		        System.out.println(doc.body().text());
		    }

		    private static void print(String msg, Object... args) {
		        System.out.println(String.format(msg, args));
		    }

		    private static String trim(String s, int width) {
		        if (s.length() > width)
		            return s.substring(0, width-1) + ".";
		        else
		            return s;
		    }

}