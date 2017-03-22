package ap.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class RobotDetector {
	
	String wikiBot;
	RobotDetector() throws IOException
	{
		File wiki=new File("wikiRobot.txt");
		Scanner s=new Scanner(wiki);
		StringBuilder sb=new StringBuilder();
		while(s.hasNext())
		{
			sb.append(s.nextLine()+"\n");
		}
		wikiBot=sb.toString();
	}

	public String getText(String link) {
		StringBuilder response = null;
		try {
	        URL url = new URL(link);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        try {
	            conn.setDoInput(true);
	            conn.setDoOutput(true);
	            InputStreamReader input = new InputStreamReader(conn.getInputStream());
	            BufferedReader in =new BufferedReader(input);
	            response = new StringBuilder();
	            String inputLine;

	            while ((inputLine = in.readLine()) != null) 
	                response.append(inputLine);
	            in.close();
	        }
	        finally {
	            conn.disconnect();
	        }
	    } catch (Exception e) {
	        return "";
	    }
        return response.toString();
    }
	
	public boolean isAllowed(String url)
	{
		URL link = null;
		try {
			link = new URL(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		String fileText = null;
		if(link.getHost()=="en.wikipedia.org")
			fileText=wikiBot;
		else
		{
			try {
				fileText = getText("http://" +link.getHost()+"/robots.txt");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(fileText==null)
			return true;
		if(fileText.length()==0) //empty-> allowed
			return true;
		fileText=fileText.toLowerCase();
		int startPos=fileText.indexOf("user-agent: *");
		if(startPos==-1)
			return true;
		fileText=fileText.substring(startPos+13); // 13 is length of user-agent word.
		String[] split = fileText.split("\n");		
		for(int i=1;i<split.length;i++)
		{
			if(split[i].startsWith("allow"))
			{
				int starti=split[i].indexOf(":")+1;
				String path = link.getFile();
				split[i]=split[i].substring(starti);
				if(split[i].equals(path))
					return true;
				continue;
			}
			if(!split[i].startsWith("disallow")) //not a disallow statement.
				continue;
			if(split[i].startsWith("user-agent"))//reached another user agent.
				break;
			int starti=split[i].indexOf(":")+1;
			String path = link.getFile();
			split[i]=split[i].substring(starti);
			if(split[i]=="") //nothing disallowed.
				return true;
            if (split[i] == "/") return false;       // allows nothing if /
            if (split[i].length() <= path.length())
            { 
                String pathCompare = path.substring(0, split[i].length());
                if (pathCompare.equals(split[i])) return false;
            }
            
        }
		return true;
			
		
	}
	
public boolean test(String fileText,URL link)
{
	if(fileText==null)
		return true;
	if(fileText.length()==0) //empty-> allowed
		return true;
	fileText=fileText.toLowerCase();
	fileText=fileText.replaceAll(" ","");
	int startPos=fileText.indexOf("user-agent:*");
	if(startPos==-1)
		return true;
	fileText=fileText.substring(startPos); // 14 is length of user-agent word.
	String[] split = fileText.split("\n");	
	for(int i=1;i<split.length;i++)
	{
		if(split[i].startsWith("allow"))
		{
			int starti=split[i].indexOf(":")+1;
			String path = link.getFile();
			split[i]=split[i].substring(starti);
			if(split[i].equals(path))
				return true;
			continue;
		}
		if(split[i].startsWith("user-agent"))//reached another user agent.
			break;
		if(!split[i].startsWith("disallow")) //not a disallow statement.
			continue;
		int starti=split[i].indexOf(":")+1;
		String path =link.getFile();
		split[i]=split[i].substring(starti);
		if(split[i]=="") //nothing disallowed.
			return true;
        if (split[i].equals("/")) 
        	return false;       //allows nothing
        if (split[i].length() <= path.length())
        { 
            String pathCompare = path.substring(0, split[i].length());
            if (pathCompare.equals(split[i])) return false;
        }
        
    }
	return true;
}
}
