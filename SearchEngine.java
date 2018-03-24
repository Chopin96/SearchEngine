import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class SearchEngine {

    public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings)	
    public DirectedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    SearchEngine() {
	// Below is the directory that contains all the internet files
	HtmlParsing.internetFilesLocation = "internetFiles";
	wordIndex = new HashMap<String, LinkedList<String> > ();		
	internet = new DirectedGraph();				
    } // end of constructor//
    
    
    // Returns a String description of a searchEngine
    public String toString () {
	return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {
	internet.addVertex(url);
	internet.setVisited(url,true);
	LinkedList<String> neighbors= HtmlParsing.getLinks(url);
	LinkedList<String> url_content=HtmlParsing.getContent(url);
	Iterator<String> itr = url_content.iterator();

		while(itr.hasNext()){
			String word = itr.next();
			if(wordIndex.containsKey(word)){ //If the word s already included in the index
				if(!(wordIndex.get(word)).contains(url)) wordIndex.get(word).addLast(url); //Add the URL to the linked list under key s

			}
			else { //The word is not in index so we create a new pair <s,new_list>

				LinkedList<String> new_list = new LinkedList<String>();
					new_list.addLast(url);
				wordIndex.put(word,new_list);		
			}
		} 
	itr=neighbors.iterator();
		while(itr.hasNext()){
			String s = itr.next(); 
			internet.addEdge(url,s);
			if(!internet.getVisited(s)) traverseInternet(s);
		}

	
    } // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       Use the iterative procedure described in the text of the assignment to
       compute the pageRanks for every vertices in the graph. 
       
    */
    void computePageRanks() {
	LinkedList<String> v_list = internet.getVertices();
	Iterator<String> itr = v_list.iterator();
		while(itr.hasNext()){
			String v = itr.next();
			internet.setPageRank(v,1);
		}
	for(int i = 0;i<100;i++){
		itr = v_list.iterator();
		 while(itr.hasNext()){
		 	String vertex = itr.next();
		 	LinkedList<String> v_in_list = internet.getEdgesInto(vertex);
		 	Iterator<String> itr2 = v_in_list.iterator();
		 	double rank = 0.5;
		 	while(itr2.hasNext()){
		 		String v_in = itr2.next();
		 		rank = rank + 0.5*(internet.getPageRank(v_in)/internet.getOutDegree(v_in));
		 	}
		 	internet.setPageRank(vertex,rank);

		 }
	}	

	
    } // end of computePageRanks
    
	
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
    */
    String getBestURL(String query) {
	LinkedList<String> websites;
	query=query.toLowerCase();
	if(wordIndex.containsKey(query)) websites = wordIndex.get(query);
	 else return new String("");

	Iterator<String> itr = websites.iterator();
	String bestsite="";
	double bestscore = -1; 
	while(itr.hasNext()){
		String site = itr.next();
		double sitescore = internet.getPageRank(site);
		System.out.println(site+"="+sitescore);
		if(sitescore>bestscore){
			bestsite=site;
			bestscore=sitescore;
		} 

    }
     return bestsite;
    }
    
	
    public static void main(String args[]) throws Exception{		
	SearchEngine mySearchEngine = new SearchEngine();
		mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
	
	mySearchEngine.computePageRanks();
	
	BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
	String query;
	do {
	    System.out.print("Enter query: ");
	    query = stndin.readLine();
	    if ( query != null && query.length() > 0 ) {
		System.out.println("Best site = " + mySearchEngine.getBestURL(query));
	    }
	} while (query!=null && query.length()>0);				
    } // end of main
}