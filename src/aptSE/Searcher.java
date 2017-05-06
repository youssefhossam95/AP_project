package aptSE;
import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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


//double[] Relevance;
//double [] TF;
//ArrayList<ArrayList<String>> AllWords; 
//ArrayList<ArrayList<Integer>> AllWordsIDs;
//Set<String> SetAllWords; 

//String [] OrderedLinks;
//boolean PhraseSearch;

Searcher(String Q, DBmanager db)
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
	
	
	   /// to calculate IDF of words
		for(int i=0;i<SplitWords.length;i++)
		{
			countOfPages=0;
			idTemp=db.GetID1(SplitWords[i]);
			IDs[i]=idTemp;
			currentStem=db.GetStemmedWord(IDs[i]);
			stemIDs=db.GetStemmedIDs(currentStem);
			while(stemIDs.next())
			{
				currentID=stemIDs.getInt(1);
				
			    pagesOfStem=db.NumPagesOfThisWord(currentID);
			    countOfPages+=pagesOfStem;  
				
			}
			
			double a= NumFetchedPages/countOfPages;
			IDF[i]=Math.log10(a);	
			
			
		}
	
	
}


public void calculateTF() throws SQLException
{
	String currentURL;
	int NumOfOccOfThisWord;
	int URLWords=0;
	String stem;
	ResultSet rs=null;
	ResultSet rsOfWords;
	int currentID,currentPriority,currentDiff ;
	
	
	for(int i=0; i<SplitWords.length; i++) // loop on words
	{
		NumOfOccOfThisWord=0;
		stem=db.GetStemmedWord(IDs[i]);
		rs=db.GetURLsOfWord(stem);
		while(rs.next()) //loop on pages
		{
			currentURL=rs.getString(1);
			if(!URLs.containsKey(currentURL))
			{
				URLWords=db.GetNumOfWords(currentURL);
				URLs.put(currentURL, URLWords);
			}
			
			rsOfWords=db.GetPrioritiesAndDiff(currentURL, stem);
			while(rsOfWords.next()) //loop for counting words in a page
			{
				currentID=rsOfWords.getInt("ID");
				currentPriority=rsOfWords.getInt("Priorityy");
				currentDiff=rsOfWords.getInt("Difference");
				if(currentID==IDs[i])
				{
					NumOfOccOfThisWord+=1*currentPriority;
					
				}
				else
				{
					NumOfOccOfThisWord+=((1*currentPriority)/(currentDiff+2));
				}
			}
			if(db.GetNumOfWords(currentURL)!=0)
			{
			double tf=NumOfOccOfThisWord/db.GetNumOfWords(currentURL);
			double idf_tf=IDF[i]*tf;
			addPage(currentURL, idf_tf);//donc hasabna el relevanve
			}
		}
	}
	
	
	
	
}






public String [] execute() throws SQLException
{
	String[] OrderedLinks = null;
	if(ReqSearch.startsWith("\"") && ReqSearch.endsWith("\"")) //Phrase search
	{
		//PhraseSearch=true;
		//call db.GetPriority to get priority
		//call db.GetPopularity to get popularity
	
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
	
	for(Map.Entry<String, Double> m : pagesMap.entrySet())
	{
		double totalScore=m.getValue()*50 +db.GetPopularity(m.getKey()); //scales the idf-tf and add the popularity.
		temp.add(new Page(m.getKey(),totalScore));
	}
	
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

