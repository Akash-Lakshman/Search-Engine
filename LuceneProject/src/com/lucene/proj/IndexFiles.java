package com.lucene.proj;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


/** Index all text files under a directory.
* <p>
* This is a command-line application demonstrating simple Lucene indexing.
* Run it with no command-line arguments for usage information.
*/

public class IndexFiles {
 
private IndexFiles() {}

/** Index all text files under a directory. */
public static void main(String[] args) {
  String usage = "java org.apache.lucene.demo.IndexFiles"
               + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
               + "This indexes the documents in DOCS_PATH, creating a Lucene index"
               + "in INDEX_PATH that can be searched with SearchFiles";
  
  String indexPath = "Z:/SEM 2/Info Retrieval/lucene/ProductIndex/";
  String docsPath = "Z:/SEM 2/Info Retrieval/lucene/ProductData/";
  boolean create = true;	
  
  for(int i=0;i<args.length;i++) {
    if ("-index".equals(args[i])) {
      indexPath = args[i+1];
      i++;
    } else if ("-docs".equals(args[i])) {
      docsPath = args[i+1];
      i++;
    } else if ("-update".equals(args[i])) {
      create = false;
    }
  }

  if (docsPath == null) {
    System.err.println("Usage: " + usage);
    System.exit(1);
  }

  final Path docDir = Paths.get(docsPath);
  if (!Files.isReadable(docDir)) {
    System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
    System.exit(1);
  }
  
  Date start = new Date();
  try {
    System.out.println("Indexing to directory '" + indexPath + "'...");

    Directory dir = FSDirectory.open(Paths.get(indexPath));
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

    if (create) {
      // Create a new index in the directory, removing any
      // previously indexed documents:
      iwc.setOpenMode(OpenMode.CREATE);
    } else {
      // Add new documents to an existing index:
      iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
    }


    IndexWriter writer = new IndexWriter(dir, iwc);
    indexDocs(writer, docDir);

    writer.close();

    Date end = new Date();
    System.out.println(end.getTime() - start.getTime() + " Total Time Taken (in milli seconds).");

  } catch (IOException e) {
    System.out.println(" caught a " + e.getClass() +
     "\n with message: " + e.getMessage());
  }
}

/**
 * Indexes the given file using the given writer, or if a directory is given,
 * recurses over files and directories found under the given directory.
 * 
 * NOTE: This method indexes one document per input file.  This is slow.  For good/
 * throughput, put multiple documents into your input file(s).  An example of this is
 * in the benchmark module, which can create "line doc" files, one document per line,
 * using the
 * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
 * >WriteLineDocTask</a>.
 *  
 * @param writer Writer to the index where the given file/dir info will be stored
 * @param path The file to index, or the directory to recurse into to find files to index
 * @throws IOException If there is a low-level I/O error
 */
static void indexDocs(final IndexWriter writer, Path path) throws IOException {
  if (Files.isDirectory(path)) {
    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        try {
          indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
        } catch (IOException ignore) {
          // don't index files that can't be read.
        }
        return FileVisitResult.CONTINUE;
      }
    });
  } else {
    indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
  }
}

/** Indexes a single document */
static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
//  try (InputStream stream = Files.newInputStream(file)) {
	  
    
	InputStream stream1 = Files.newInputStream(file);
	InputStream stream2 = Files.newInputStream(file);
	InputStream stream3 = Files.newInputStream(file);
	
//	InputStream stream = Files.newInputStream(file);
	  
        
    BufferedReader br1 = null;
	BufferedReader br2 = null;
	BufferedReader br3 = null;
	
	boolean x = false;
	
	String line;
	String outputAuthor = "";
	String outputTitle = "";
	String outputContent = "";

	br1 = new BufferedReader(new InputStreamReader(stream1));
	br2 = new BufferedReader(new InputStreamReader(stream2));
	br3 = new BufferedReader(new InputStreamReader(stream3));
	
	while ((line = br1.readLine()) != null) {
		if (x==true) {
			if(line.startsWith(".B")){
				x=false;
				break;
			}
			outputAuthor = outputAuthor + line + System.getProperty("line.separator");
		}
		if (line.startsWith(".A")) {
			x=true;
		}
	}
	
	x=false;
	while ((line = br2.readLine()) != null) {
		if (x==true) {
			if(line.startsWith(".A")){
				x=false;
				break;
			}
			outputTitle = outputTitle + line + System.getProperty("line.separator");
		}
		if (line.startsWith(".T")) {
			x=true;
		}
	}
	x=false;
	while ((line = br3.readLine()) != null) {
		if (x==true) {
			if(line.startsWith(".I")){
				x=false;
				break;
			}
			outputContent = outputContent + line + System.getProperty("line.separator");
		}
		if (line.startsWith(".W")) {
			x=true;
		}
	}

	System.out.println(outputAuthor+"\n");
	System.out.println(outputTitle+"\n");
	System.out.println(outputContent+"\n");

	InputStream streamAuthor = new ByteArrayInputStream(outputAuthor.getBytes(StandardCharsets.UTF_8));
	InputStream streamTitle = new ByteArrayInputStream(outputTitle.getBytes(StandardCharsets.UTF_8));
	InputStream streamContent = new ByteArrayInputStream(outputContent.getBytes(StandardCharsets.UTF_8));

	
	Document doc = new Document();
		
    Field pathField = new StringField("path", file.toString(), Field.Store.YES);
    doc.add(pathField);
    
    // Add the last modified date of the file a field named "modified".
    // Use a LongPoint that is indexed (i.e. efficiently filterable with
    // PointRangeQuery).  This indexes to milli-second resolution, which
    // is often too fine.  You could instead create a number based on
    // year/month/day/hour/minutes/seconds, down the resolution you require.
    // For example the long value 2011021714 would mean
    // February 17, 2011, 2-3 PM.
    doc.add(new LongPoint("modified", lastModified));
    
    // Add the contents of the file to a field named "contents".  Specify a Reader,
    // so that the text of the file is tokenized and indexed, but not stored.
    // Note that FileReader expects the file to be in UTF-8 encoding.
    // If that's not the case searching for special characters will fail.
    
    
    doc.add(new TextField("Author", new BufferedReader(new InputStreamReader(streamAuthor, StandardCharsets.UTF_8))));
    doc.add(new TextField("Title", new BufferedReader(new InputStreamReader(streamTitle, StandardCharsets.UTF_8))));
    doc.add(new TextField("Content", new BufferedReader(new InputStreamReader(streamContent, StandardCharsets.UTF_8))));
    
    if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
      // New index, so we just add the document (no old document can be there):
      System.out.println("adding " + file);
      writer.addDocument(doc);
    } else {
      // Existing index (an old copy of this document may have been indexed) so 
      // we use updateDocument instead to replace the old one matching the exact 
      // path, if present:
      System.out.println("updating " + file);
      writer.updateDocument(new Term("path", file.toString()), doc);
    	}
}
}


 

