package com.lucene.proj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

public class SearchFiles {

//private SearchFiles() {}


	public static void main(String[] args) throws Exception 
	{
		String usage = "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
		if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
			System.out.println(usage);
			System.exit(0);
	}

  String index = "Z:/SEM 2/Info Retrieval/lucene/ProductIndex/";
  String field = "Content";

  String queryFile = "Z:/SEM 2/Info Retrieval/lucene/CranFiles/cran.qry";
  String simScore = "bm25";  //for score
 
  /*
  String queries = null;
  int repeat = 0;
  boolean raw = false;
  String queryString = null;
  int hitsPerPage = 10;
  
  for(int i = 0;i < args.length;i++) {
    if ("-index".equals(args[i])) {
      index = args[i+1];
      i++;
   } else if ("-field".equals(args[i])) {
      field = args[i+1];
      i++;
    } else if ("-queries".equals(args[i])) {
     queries = args[i+1];
     i++;
    } else if ("-query".equals(args[i])) {
      queryString = args[i+1];
      i++;
    } else if ("-repeat".equals(args[i])) {
      repeat = Integer.parseInt(args[i+1]);
      i++;
    } else if ("-raw".equals(args[i])) {
      raw = true;
    } else if ("-paging".equals(args[i])) {
      hitsPerPage = Integer.parseInt(args[i+1]);
     if (hitsPerPage <= 0) {
        System.err.println("There must be at least 1 hit per page.");
        System.exit(1);
      }
      i++;
    }
  }
  */
  
  IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
  IndexSearcher searcher = new IndexSearcher(reader);
 // Analyzer analyzer = new StandardAnalyzer();
  
  searcher.setSimilarity(new BM25Similarity(0.2F, 0.5F));
  Similarity simi= new ClassicSimilarity();
  searcher.setSimilarity(simi);
  simScore = "TF-IDF";
  System.out.println(reader.getDocCount(field));

  
  BufferedReader queryBuffer = new BufferedReader(new FileReader(new File(queryFile)));

/*  BufferedReader in = null;
  if (queries != null) {
    in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
  } else {
    in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
  }
  
  QueryParser parser = new QueryParser(field, analyzer);
  while (true) {
*/   
  	int counter=0;
//	Query query;
	String line;
	String queryText="";
//	String[] pair= {};
	boolean firstQuery=true;

while ((line = queryBuffer.readLine()) != null) {

	//System.out.println(line);
	if (line.startsWith(".I")) {
		if (firstQuery==true) {
			//pair = line.split(" ", 2);
			counter++;
			firstQuery=false;
			continue;
		}
		//query= parser.parse(queryText);
		//System.out.println(counter);
		//System.out.println(queryText);
		//pair = line.split(" ", 2);
		
		doPagingSearch(queryBuffer, searcher, String.valueOf(counter), queryText, simScore);
		counter++;
		continue;
	}
	else if (line.startsWith(".W")) {
		queryText="";
		continue;
	}				
	queryText = queryText + line;
}

//query= parser.parse(queryText);
doPagingSearch(queryBuffer, searcher, String.valueOf(counter), queryText, simScore);

queryBuffer.close();
}

 
public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, String queryID, String queryText, String simScore) throws IOException, ParseException 
{
 
	Analyzer analyzer = new StandardAnalyzer();
	QueryParser p1 = new QueryParser("Title", analyzer);
	QueryParser p2 = new QueryParser("Author", analyzer);
	QueryParser p3 = new QueryParser("Content", analyzer);
	    
	Query q1 = p1.parse(queryText);
	Query q2 = p2.parse(queryText);
	Query q3 = p3.parse(queryText);
	
    Query boostQ1 = new BoostQuery(q1, (float) 2);
    Query boostQ2 = new BoostQuery(q2, (float) 2.5);
    Query boostQ3 = new BoostQuery(q3, (float) 0.5);

    BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
    booleanQuery.add(boostQ1, Occur.SHOULD);
    booleanQuery.add(boostQ2, Occur.SHOULD);
    booleanQuery.add(boostQ3, Occur.SHOULD);
	
    //PrintStream out = new PrintStream(new FileOutputStream("", true));
	
  TopDocs results = searcher.search(booleanQuery.build(), 20);
  ScoreDoc[] hits = results.scoreDocs;
  
  int TotalHits = Math.toIntExact(results.totalHits);
  int count;
  int start=0;
  int end = Math.min(TotalHits,20 );
  
  System.out.println(TotalHits + " total matching documents");
  
  for (int i = start; i < end; i++) {
		Document doc = searcher.doc(hits[i].doc);
		
		System.out.println("Doc: "+doc);
		String docNo = doc.get("path");
		//docno.substring(15, 20);
		count=0;
		
//		Z:\SEM 2\Info Retrieval\lucene-7.2.1\ProductData\1343.txt 19 5.453277 TF-IDF
		
		while(docNo.charAt(count)!='.') {
				count++;
		}
		
		docNo= docNo.substring(43, count);
	//	System.out.println("DOC NO "+docNo);
		System.out.println(queryID+" Q0"+docNo+" "+i+" "+"Score: "+hits[i].score+" "+simScore);
		
		
		}
	}	

}
        
 