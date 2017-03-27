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
import java.util.concurrent.ConcurrentHashMap;

public class RobotDetector {

	String wikiBot;
	ConcurrentHashMap<String,TimeCapsule> blockedMap;
	RobotDetector(ConcurrentHashMap<String,TimeCapsule> map) throws IOException
	{
		File wiki=new File("wikiRobot.txt");
		Scanner s=new Scanner(wiki);
		StringBuilder sb=new StringBuilder();
		while(s.hasNext())
		{
			sb.append(s.nextLine()+"\n");
		}
		wikiBot=sb.toString();
		blockedMap=map;
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
		fileText=fileText.replaceAll(" ","");
		int startPos=fileText.indexOf("user-agent:*");
		
		if(startPos==-1)
			return true;
		
		fileText=fileText.substring(startPos);
		String[] split = fileText.split("\n");
		
		if(isBlocked(link.getHost(),split))//currently blocked with delay->not allowed.
			return false;
		
		
		for(int i=1;i<split.length;i++) //start from line after user agent(1).
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
	boolean isBlocked(String hostName,String[] robotCommands)
	{
		if(blockedMap.contains(hostName))
		{
			TimeCapsule current=blockedMap.get(hostName);
			
			if(current.isBlocked()) 
				return true;
			current.updateTime(); //in list but not currently blocked ->renew blocking.
		}
		else //not in map -> search for delay in robots.txt
		{
			float delay=getDelay(robotCommands);
			
			if(delay==0) //no delay.
				return false;
			
			//delay exists -> add host to the map
			blockedMap.put(hostName, new TimeCapsule(delay));	
		}
		return false;
	}
	float getDelay(String[] robotCommands)//recieves a an array of commands placed after user-agent:* 
	{
		for(int i=1;i<robotCommands.length;i++)
		{
			if(robotCommands[i].startsWith("user-agent"))//reached another user agent.
				break;
			if(robotCommands[i].startsWith("crawl-delay:"))
			{
				int starti=robotCommands[i].indexOf(":")+1;
				robotCommands[i]=robotCommands[i].substring(starti);
				return Float.parseFloat(robotCommands[i]);
			}
		}
		return 0;
	}
}
