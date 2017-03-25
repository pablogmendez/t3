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
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

import ar.fiuba.taller.loadTestConsole.Constants.TASK_STATUS;

public class User implements Runnable {
	private ArrayBlockingQueue<UserTask> userTaskPendingQueue;
	private ArrayBlockingQueue<UserTask> userTaskFinishedQueue;
	private ArrayBlockingQueue<StatTask> statsQueue;
	final static Logger logger = Logger.getLogger(App.class);
	
	public User(ArrayBlockingQueue<UserTask> userTaskPendingQueue, ArrayBlockingQueue<UserTask> userTaskFinishedQueue, 
			ArrayBlockingQueue<StatTask> statsQueue) {
		this.userTaskPendingQueue = userTaskPendingQueue;
		this.userTaskFinishedQueue = userTaskFinishedQueue;
		this.statsQueue = statsQueue;
	}
	
	public void run() {
		logger.info("Iniciando el usuario: " + Thread.currentThread().getId());
		String method, uri, html;
		List<String> requestList;
		Integer downloaders = ConfigLoader.getInstance().getMaxSizeDownloadersPoolThread();
		Integer taskId;
		DownloaderTask finishedTask;
		Boolean gracefullQuit = false;
		UserTask userTask;
		DownloaderTask downloaderTask;
		long time_elapsed, time_end, time_start;
		Integer bytesDownloaded;
		
		logger.info("Creo la cola de tareas");
		ArrayBlockingQueue<DownloaderTask> downloaderTaskPendingQueue = new ArrayBlockingQueue<DownloaderTask>(ConfigLoader.getInstance().getTasksQueueSize());
		
		logger.info("Creo la cola para recibir los mensajes de terminado de los downloaders");
		ArrayBlockingQueue<DownloaderTask> downloaderTaskFinishedQueue = new ArrayBlockingQueue<DownloaderTask>(ConfigLoader.getInstance().getTasksQueueSize());
		
		logger.info("Creo el pool de threads de downloaders");
		ExecutorService downloadersThreadPool = Executors.newFixedThreadPool(downloaders);
		
		logger.info("Lanzo los downloaders y les paso las dos colas");
		for(int i = 0; i < downloaders; i++) {
			logger.info("Lanzando el downloader: " + i);
			downloadersThreadPool.submit(new Downloader(downloaderTaskPendingQueue, downloaderTaskFinishedQueue, statsQueue));
		}
		
		while(!gracefullQuit) {			
			try {
				userTask = userTaskPendingQueue.take();
				if(userTask.getId() == Constants.DISCONNECT_ID) {
					logger.info("Se ha recibido un mensaje de desconexion. Se envía mensaje de desconexion a los downloaders");
					for(int i = 0; i < downloaders; ++i) {
						downloaderTaskPendingQueue.put(new DownloaderTask(Constants.DISCONNECT_ID, null, null, null));
					}
					logger.info("Esperando a que los downloaders finalicen");
					while(downloaders > 0) {
						downloaderTask = downloaderTaskFinishedQueue.take();
						if(downloaderTask.getId() == Constants.DISCONNECT_ID) {
							downloaders--;
						}
					}
					logger.info("Downloaders finalizados");
					logger.info("Finalizando usuario");
					logger.info("Avisando al Control principal que el usuario termino");
					userTaskFinishedQueue.put(userTask);
					gracefullQuit = true;
					logger.info("Usuario finalizado");
				}
				else {
					logger.info("Se inicia un nuevo pulso de usuario");
					logger.info("Leo el script");
					try {
						FileInputStream fstream = new FileInputStream(Constants.SCRIPT_FILE);
						BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
						String strLine;
						StringTokenizer defaultTokenizer;
						taskId = 1;
						logger.info("Leo el script");
						while ((strLine = br.readLine()) != null)   {
							// Parseo la linea
							defaultTokenizer = new StringTokenizer(strLine);
									
							// Obtengo la url
							method = defaultTokenizer.nextToken();
							uri = defaultTokenizer.nextToken();
							
							logger.info("Siguiente paso a realizar: " + method + " " + uri);
							logger.info("Obteniendo recurso ...");
							html = getPage(method, uri).toLowerCase();
							time_start = System.currentTimeMillis();
							bytesDownloaded = html.length();
							time_end = System.currentTimeMillis();
							time_elapsed = time_end - time_start;
							logger.info("Recurso obtenido:\n" + html);
							

							logger.info("Bytes descargados: " + bytesDownloaded);
							logger.info("Tiempo transcurrido: " + time_elapsed + " milisegundos");
							
							// Enviando estadistica de descarga a la cola de estadisticas
							statsQueue.put(new StatTask(Constants.DEFAULT_ID, Constants.TASK_STATUS.SUBMITTED, 
									0, true, time_elapsed));
							
							logger.info("Rescato los tags LINK, IMG y SCRIPT e inserto las tasks en la cola");
							for(String tag: Arrays.asList(Constants.IMG_TAG, Constants.SCRIPT_TAG, Constants.LINK_TAG)) {
								requestList = getLinks(html, tag);
								for(String request : requestList) {
									logger.info("Enviando una nueva task con los siguiente parametros:\nTaskId: " + taskId
											+ "\nMetodo: " + Constants.GET_METHOD + "\nRequest: " + request + "\nEstado: " + Constants.TASK_STATUS.SUBMITTED);
									downloaderTaskPendingQueue.put(new DownloaderTask(taskId, Constants.GET_METHOD, request, Constants.TASK_STATUS.SUBMITTED));
									++taskId;
								}									
							}
							logger.info("Esperando a que terminen los downloaders");
							// 
							while(taskId > 0) {
								finishedTask = downloaderTaskFinishedQueue.take();
								taskId--;
								logger.info("Task finalizada:\nTaskId: " + finishedTask.getId() + "\nStatus: " + finishedTask.getStatus());
							}
						}
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						statsQueue.put(new StatTask(Constants.DEFAULT_ID, Constants.TASK_STATUS.SUBMITTED, 
								0, false, 0));
						e.printStackTrace();
					}

				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
			List<String> requestsList = new ArrayList<String>(); 
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
				currentTag = HTML.Tag.SCRIPT;
				currentAttribute = HTML.Attribute.SRC;				
			}
			else if (tag.equals(Constants.IMG_TAG)) {
				currentTag = HTML.Tag.IMG;
				currentAttribute = HTML.Attribute.SRC;
			}

			HTMLDocument.Iterator it = doc.getIterator(currentTag);
			while (it.isValid()) {
			  AttributeSet s = it.getAttributes();
			
			  String link = (String) s.getAttribute(currentAttribute);
			  if (link != null) {
				  requestsList.add(link);
			  }
			  it.next();
			}
			return requestsList;
	   }
}
