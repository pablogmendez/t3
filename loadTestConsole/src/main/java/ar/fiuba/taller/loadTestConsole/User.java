package ar.fiuba.taller.loadTestConsole;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

public class User implements Runnable {

	private ArrayBlockingQueue<Task> tasksQueueList;
	
	final static Logger logger = Logger.getLogger(App.class);
	
	public User(ArrayBlockingQueue<Task> tasksQueueList) {
		this.tasksQueueList = tasksQueueList;
	}
	
	public void run() {
		logger.info("Iniciando el usuario: " + Thread.currentThread().getId());

		String html;
		List<String> uriQueue;
		
		// Leo el script
		try {
			FileInputStream fstream = new FileInputStream(Constants.SCRIPT_FILE);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			StringTokenizer defaultTokenizer;
			// Leo la siguiente url
			while ((strLine = br.readLine()) != null)   {
				// Parseo la linea
				defaultTokenizer = new StringTokenizer(strLine);
				
				// Obtengo la url
				html = getPage(defaultTokenizer.nextToken(), defaultTokenizer.nextToken()).toLowerCase();
				
				// Parseo la pagina
				// Rescato los LINK e inserto las tasks en la cola
				uriQueue = getLinks(html, Constants.LINK_TAG);
				for(String uri : uriQueue) {
					tasksQueueList.put(new Task(Constants.NORMAL_TASK, Constants.GET_METHOD, uri));					
				}				
				
				// Rescato los IMG e inserto las tasks en la cola
				uriQueue = getLinks(html, Constants.IMG_TAG);
				for(String uri : uriQueue) {
					tasksQueueList.put(new Task(Constants.NORMAL_TASK, Constants.GET_METHOD, uri));					
				}
				
				// Rescato los SCRIPT e inserto las tasks en la cola
				uriQueue = getLinks(html, Constants.SCRIPT_TAG);
				for(String uri : uriQueue) {
					tasksQueueList.put(new Task(Constants.NORMAL_TASK, Constants.GET_METHOD, uri));					
				}
				


				// Espero a que todos los threads terminen
			}
			br.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}


	   private String getPage(String urlToRead, String method) throws Exception {
	      StringBuilder result = new StringBuilder();
	      URL url = new URL(urlToRead);
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod(method);
	      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = rd.readLine()) != null) {
	         result.append(line);
	      }
	      rd.close();
	      return result.toString();
	   }
	   
	   private List<String> getLinks(String page, String tag) throws URISyntaxException, IOException, BadLocationException {
			List<String> uriQueue = new ArrayList<String>(); 
			HTML.Tag currentTag = null;
			HTML.Attribute currentAttribute = null;
			Reader rd = new StringReader(tag);
			
			EditorKit kit = new HTMLEditorKit();
			HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
			kit.read(rd, doc, 0);
			
			if (tag.equals(Constants.LINK_TAG)) {
				currentTag = HTML.Tag.LINK;
				currentAttribute = HTML.Attribute.HREF;				
			}
			else if (tag.equals(Constants.SCRIPT_TAG)) {
				currentTag = HTML.Tag.LINK;
				currentAttribute = HTML.Attribute.HREF;				
			}
			else if (tag.equals(Constants.IMG_TAG)) {
				currentTag = HTML.Tag.LINK;
				currentAttribute = HTML.Attribute.HREF;
			}

			HTMLDocument.Iterator it = doc.getIterator(currentTag);
			while (it.isValid()) {
			  AttributeSet s = it.getAttributes();
			
			  String link = (String) s.getAttribute(currentAttribute);
			  if (link != null) {
			    uriQueue.add(link);
			  }
			  it.next();
			}
			return uriQueue;
	   }
}
