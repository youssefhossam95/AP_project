package aptSE;
import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import javafx.animation.KeyValue.Type;

public class Searcher {
String ReqSearch;
DBmanager db;
String[] SplitWords;
double [] IDF;
int[] IDs;
HashMap<String,Integer> URLs;
int NumFetchedPages=0;
HashMap<String,Double> pagesMap;
public double avg = 0 ; 

//double[] Relevance;
//double [] TF;
//ArrayList<ArrayList<String>> AllWords; 
//ArrayList<ArrayList<Integer>> AllWordsIDs;
//Set<String> SetAllWords; 

//String [] OrderedLinks;
//boolean PhraseSearch;

Searcher(String Q , DBmanager db)
{
	ReqSearch=Q;
	this.db=db;
	SplitWords=ReqSearch.split(" ");
	IDF= new double[SplitWords.length];
	IDs= new int[SplitWords.length];
	pagesMap=new HashMap<String,Double>();
	 URLs= new HashMap<String,Integer>();
	 
	//TF= new double[SplitWords.length];
	//Relevance=new double[SplitWords.length];
	//SetAllWords= new HashSet<String>();
	//AllWords = new ArrayList<ArrayList<String>>();
	//AllWordsIDs = new ArrayList<ArrayList<Integer>>();
   
    
    
}




// to be called
public void calculateIDF() throws SQLException
{
	int idTemp;
	String currentStem;
	ResultSet stemIDs;
	int currentID;
	int pagesOfStem;
	int countOfPages;
	NumFetchedPages=db.GetNumOfFetchedPages();
	
	System.out.println("entered idf");

	   /// to calculate IDF of words
		for(int i=0;i<SplitWords.length;i++)
		{
			idTemp=db.GetID1(SplitWords[i]);
			IDs[i]=idTemp;
			Stemmer s = new Stemmer () ;  
			//currentStem=db.GetStemmedWord(IDs[i]);
			currentStem = s.GetStemedString(SplitWords[i]); 
			//stemIDs=db.GetStemmedIDs(currentStem);
			//while(stemIDs.next()) //hanzbtha
			//{
			//	currentID=stemIDs.getInt(1);
				
			    pagesOfStem=db.NumPagesOfThisWord(currentStem);
			 //   countOfPages+=pagesOfStem;  
				
			double a ; 
			    if (pagesOfStem != 0 ) 
			    		 {
			    			a= Double.valueOf(NumFetchedPages)/Double.valueOf( pagesOfStem ); // is that 1 ok ?? 
			    			IDF[i]=Math.log10(a);	

			    		 }
			    else 
			    	
			    	IDF[i] = 0 ; 
			
		}
	
	
}


public void calculateTF() throws SQLException
{
	System.out.println("In CacluateTF");
	String currentURL;
	double wordWeight;
	int URLWords=0;
	double tf; 
	double idf_tf;
	String stem;
	ResultSet rs=null;
	ResultSet rsOfWords;
	int currentID,currentPriority,currentDiff ;
	
	
	for(int i=0; i<SplitWords.length; i++) // loop on words
	{
		Stemmer s= new Stemmer();
		stem=s.GetStemedString(SplitWords[i]);//use the function
			
		rsOfWords=db.GetPrioritiesAndDiff(stem);
		System.out.println("enered words");
		int count=0;
		while(rsOfWords.next()) 
		{
			currentID=rsOfWords.getInt("ID");
			currentPriority=rsOfWords.getInt("Priorityy");
			currentDiff=rsOfWords.getInt("Difference");
			currentURL=rsOfWords.getString("URL");
			URLWords = rsOfWords.getInt("NumberOfWords"); 
			int indexed = rsOfWords.getInt("isIndexed"); 
			if( indexed == 0   || URLWords==0)
				continue ; 
			
			if ( currentPriority  == 2 )
				currentPriority = 20 ; 
			else if ( currentPriority == 3 )
				currentPriority = URLWords/2  ; 
//			if(!URLs.containsKey(currentURL))
//			{
//				URLWords=db.GetNumOfWords(currentURL);
//				URLs.put(currentURL, URLWords);
//			}
//			else 
//				URLWords = URLs.get(currentURL); 
			
			if(currentID==IDs[i])
			{
				wordWeight=1*currentPriority;
				
			}
			else
			{
				wordWeight=(Double.valueOf(1*currentPriority)/Double.valueOf(currentDiff+2));
			}

			
			tf =wordWeight/Double.valueOf(URLWords);
			idf_tf=IDF[i]*tf;
			
			if (idf_tf < 0 )
			{
				System.out.println("problem with idf_tf" );
				WriteToFile("output.txt", String.valueOf(db.GetID1(SplitWords[0]))) ; 
			}
			if (tf < 0 )
				System.out.println("problem with tf" );
			addPage(currentURL, idf_tf);//donc hasabna el relevanve
			//System.out.println(count++);
		}
		


		}
	}
	
	
	
	
public static void WriteToFile (String fileName , String text ){
	
	try {
		
	//	FileWriter fw = new FileWriter (fileName); 
		PrintWriter out = new PrintWriter(new FileOutputStream (new File (fileName), true));
		out.append(text); 
		out.println("");
		out.close();
		
	} catch (  IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}






public String [] execute() throws SQLException
{
	String[] OrderedLinks = null;
	if(ReqSearch.startsWith("\"") && ReqSearch.endsWith("\"")) //Phrase search
	{
		if (SplitWords.length != 0 ) 
		{
			
			SplitWords[0] = SplitWords[0].substring(1, SplitWords[0].length())  ; // remove " from first word 
			SplitWords[SplitWords.length-1] = SplitWords[SplitWords.length-1].substring(0, SplitWords[SplitWords.length-1].length()-1); 
			PhraseSearch(); 
			OrderedLinks = getLinksInOrder() ; 
		//PhraseSearch=true;
		//call db.GetPriority to get priority
		//call db.GetPopularity to get popularity
		}
	}
	else
	{
		//PhraseSearch=false;
		this.calculateIDF();
		this.calculateTF();

		OrderedLinks=this.getLinksInOrder();
	}
	
	
	return OrderedLinks;
	
	
}








public String[] getLinksInOrder() //returns ranked urls
{
	
	ArrayList<Page> temp= new ArrayList<Page>();
	int count = 0 ; 
	for(Map.Entry<String, Double> m : pagesMap.entrySet())
	{
		double totalScore=m.getValue()*10000 +db.GetPopularity(m.getKey()); //scales the idf-tf and add the popularity.
		temp.add(new Page(m.getKey(),totalScore));
		double x = 10000 ; 
		avg+= m.getValue() * x  / Double.valueOf(db.GetPopularity(m.getKey())) ; 
		count ++ ; 
	}
	if (count != 0 )
		avg /= Double.valueOf(count); 
	else 
		avg = 0 ; 
	
	//System.out.println("the average of IDF-tf/ popularity = " + String.valueOf(avg));
	Collections.sort(temp); //sort array list according to rank score.
	Collections.reverse(temp);
	String [] links=new String[temp.size()];
	int i=0;
	for(Page p: temp)
	{
		links[i]=p.url;
		i++;
	}
	pagesMap.clear();
	return links;
}


public void PhraseSearch (){ 
	List <String> URLSList  = new ArrayList<String>() ; 
	
	ResultSet rs ; 
	rs =  db.GetWordIndex(SplitWords[0]); 
	
	try {
		while (rs.next())
		{
			String url = rs.getString("URL"); 
			int indo = rs.getInt("Index"); 
			boolean OK = true ; 
			for (int i = 1 ; i < SplitWords.length ; i ++ )
			{
				if (! db.CheckWordInIndex(SplitWords[i], url, indo + i)) 
				{
					OK = false ; 
					break ; 
				}
				
			}
			
			if ( OK )
			{
				double pop = db.GetPopularity(url); 
				//  URLSList.add(url);
				pop = 0 ; 
		
				addPage(url, pop);
			}
		}
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//URLSList.add( rs.getString("URL")); 
	
	
				
//	String [] URLS = new String [URLSList.size()] ; 
//	for ( int i = 0 ; i < URLSList.size() ; i ++ )
//		URLS[i] = URLSList.get(i); 
//	
//
//	return URLS;
}


public void addPage(String url,Double rankScore) //adds a page to the map or update score if exists
{
	
	Double currentScore;
    if((currentScore=pagesMap.putIfAbsent(url, rankScore))!= null) // page already exists->update score 
	{
		pagesMap.replace(url, currentScore+=rankScore);
	}
}



}

class Page implements Comparable<Page>{
    String url;
    Double rankScore;

    Page(String url,Double rankScore)
    {
    	this.url=url;
    	this.rankScore=rankScore;
    }
    public int compareTo(Page other){
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than 
        // other and 0 if they are supposed to be equal
        return this.rankScore.compareTo(other.rankScore);
    }
    
    @Override 
	public int hashCode()
	{
		return this.url.hashCode();
	}
    
    
    @Override
    public boolean equals(Object obj)
    {
    	if (!(obj instanceof Page))
            return false;
        if (obj == this)
            return true;

        Page p = (Page) obj;
    	return this.url.equals(p.url);
    }
}






//public void brouillon()
//{
//ehseby el number of zeft number of pages
	
//	String tempURL;
//	int NumOfOccOfThisWord;
//	int AllWords;
	
	
	
	
	
//	
//	
//	
//	for(int i=0; i<AllWordsIDs.size() ;i++)
//	{
//		for(int j=0; j<AllWordsIDs.get(i).size();j++)
//		{
//			ResultSet rs=db.GetURL(AllWordsIDs.get(i).get(j));
//			while(rs.next())
//			{
//				tempURL=rs.getString(1);
//				URLs.add(tempURL);
//			    //NumOfOccOfThisWord=db.GetCountOfThisWord(tempURL, AllWordsIDs.get(i).get(j));
//				URLWords=db.GetNumOfWords(tempURL);
//				
//			
//				if(AllWordsIDs.get(i).get(j)==IDs[i]) //ahseb el tf el 3ady
//				{
//					NumOfOccOfThisWord=db.GetCountOfThisWord(tempURL, AllWordsIDs.get(i).get(j),1);
//				    count=count+NumOfOccOfThisWord*1;
//				    NumOfOccOfThisWord=db.GetCountOfThisWord(tempURL, AllWordsIDs.get(i).get(j),2);
//				    count=count+NumOfOccOfThisWord*2;
//				    NumOfOccOfThisWord=db.GetCountOfThisWord(tempURL, AllWordsIDs.get(i).get(j),3);
//				    count=count+NumOfOccOfThisWord*3;
//				
//					
//				}
//				else
//				{
//				
//					
//				}
//			}
//			
//		}
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	// original word
//	for(int i=0;i<SplitWords.length;i++)
//	{
//		ResultSet rs=db.GetURL(IDs[i]);
//		try {
//			while(rs.next())
//			{
//				tempURL=rs.getString(1);
//				URLs.add(tempURL);
//			    NumOfOccOfThisWord=db.GetCountOfThisWord(tempURL, IDs[i]);
//				AllWords=db.GetNumOfWords(tempURL);
//				
//				
//				
//				
//				
//				
//				
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		
//	}
	
	
	
//}

	


//public void AllWordsOfThisSearch() throws SQLException
//{
//	String currentStem, tempStem;
//	
//	ResultSet AllOrigWords;
//	ResultSet IDsForStemmed;
//	int idTemp,stemID;
//	for(int i=0; i<SplitWords.length;i++)
//	{
//		ArrayList<String> tempWords=new ArrayList<String>();
//		ArrayList<Integer> tempIDs=new ArrayList<Integer>();
//		
//		
//		idTemp=db.GetID1(SplitWords[i]);
//		IDs[i]=idTemp;
//		currentStem=db.GetStemmedWord(IDs[i]);
//		AllOrigWords=db.GetOriginalWords(currentStem);
//		IDsForStemmed=db.GetStemmedIDs(currentStem);
//		
//		while(AllOrigWords.next())
//		{
//			tempStem=AllOrigWords.getString(1);
//			
//			//SetAllWords.add(tempStem);
//			tempWords.add(tempStem);
//		}
//		
//		AllWords.add(tempWords);
//		
//		while(IDsForStemmed.next())
//		{
//			stemID=IDsForStemmed.getInt(1);
//			tempIDs.add(stemID);
//		}
//		
//		AllWordsIDs.add(tempIDs);
//		
//	}
//}

