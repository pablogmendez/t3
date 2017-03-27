package ar.fiuba.taller.loadTestConsole;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ar.fiuba.taller.utils.HttpRequester;

public class User implements Runnable {
	private ArrayBlockingQueue<UserTask> userTaskPendingQueue;
	private ArrayBlockingQueue<UserTask> userTaskFinishedQueue;
	private ArrayBlockingQueue<SummaryTask> summaryQueue;
	private ArrayBlockingQueue<ReportTask> reportQueue;
	final static Logger logger = Logger.getLogger(App.class);
	
	public User(ArrayBlockingQueue<UserTask> userTaskPendingQueue, ArrayBlockingQueue<UserTask> userTaskFinishedQueue, 
			ArrayBlockingQueue<SummaryTask> summaryQueue, ArrayBlockingQueue<ReportTask> reportQueue) {
		this.userTaskPendingQueue = userTaskPendingQueue;
		this.userTaskFinishedQueue = userTaskFinishedQueue;
		this.summaryQueue = summaryQueue;
		this.reportQueue = reportQueue;
	}
	
	public void run() {
		logger.info("Iniciando el usuario");
		String html;
		List<String> requestList;
		Integer downloaders = ConfigLoader.getInstance().getMaxSizeDownloadersPoolThread();
		Integer taskId;
		DownloaderTask finishedTask;
		Boolean gracefullQuit = false;
		UserTask userTask;
		DownloaderTask downloaderTask;
		long time_elapsed, time_end, time_start;
		Integer bytesDownloaded;
		
		// Script
		Map<String, String> map;
		HttpRequester httpRequester = new HttpRequester();
		String method = "", url = "", data = "";
		File inputFile = new File(Constants.SCRIPT_FILE);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = null;
	    logger.info("1111");
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
	    org.w3c.dom.Document doc = null;
	    logger.info("44444");
		try {
			doc = dBuilder.parse(inputFile);
		} catch (SAXException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		logger.info("2222");
		doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("request");
		
		logger.info("Creo la cola de tareas");
		ArrayBlockingQueue<DownloaderTask> downloaderTaskPendingQueue = new ArrayBlockingQueue<DownloaderTask>(ConfigLoader.getInstance().getTasksQueueSize());
		
		logger.info("Creo la cola para recibir los mensajes de terminado de los downloaders");
		ArrayBlockingQueue<DownloaderTask> downloaderTaskFinishedQueue = new ArrayBlockingQueue<DownloaderTask>(ConfigLoader.getInstance().getTasksQueueSize());
		
		logger.info("Creo el pool de threads de downloaders");
		ExecutorService downloadersThreadPool = Executors.newFixedThreadPool(downloaders);
		
		logger.info("Lanzo " + downloaders + " downloaders y les paso las dos colas");
		for(int i = 0; i < downloaders; i++) {
			logger.info("Lanzando el downloader: " + i);
			downloadersThreadPool.submit(new Downloader(downloaderTaskPendingQueue, downloaderTaskFinishedQueue, summaryQueue, reportQueue));
		}
		
		while(!gracefullQuit) {			
			try {
				userTask = userTaskPendingQueue.take();
				// Informo al monitor que arranco el usuario
				reportQueue.put(new ReportTask(Constants.DEFAULT_ID, Constants.TASK_STATUS.SUBMITTED, true, null));
				if(userTask.getId() == Constants.DISCONNECT_ID) {
					logger.info("Se ha recibido un mensaje de desconexion. Se envía mensaje de desconexion a los downloaders");
					for(int i = 0; i < downloaders; ++i) {
						downloaderTaskPendingQueue.put(new DownloaderTask(Constants.DISCONNECT_ID, null, null, null, null));
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
						
						logger.info("Leo el script");
				        for (int temp = 0; temp < nList.getLength(); temp++) {
				        	map = new HashMap<String, String>();
				        	method = "";
				        	url = "";
				        	data = "";
							taskId = 0;

									
						   // Obtengo la url
						   org.w3c.dom.Node nNode = nList.item(temp);
						   org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
				           method = eElement.getElementsByTagName("method").item(0).getTextContent();
				           url = eElement.getElementsByTagName("url").item(0).getTextContent();
				           data = eElement.getElementsByTagName("data").item(0).getTextContent();
				           org.w3c.dom.Element e2 = (org.w3c.dom.Element) eElement.getElementsByTagName("headers").item(0);
				           org.w3c.dom.NodeList n2 = e2.getChildNodes();
				           for(int i = 0; i < n2.getLength(); i++) {
				        	   Node n =  n2.item(i);
				        	   org.w3c.dom.NodeList n3 = n.getChildNodes();
				               if(n3.getLength() > 0) {
				            	   if(!(n3.item(1).getTextContent().trim().equals(""))) {
				            		   map.put(n3.item(1).getTextContent(), n3.item(3).getTextContent());            		   
				            	   }
				               }
				           }
							
							logger.info("Siguiente paso a realizar: " + method + " " + url);
							logger.info("Obteniendo recurso ...");
							html = httpRequester.doHttpRequest(method, url, map, data).toLowerCase();
							time_start = System.currentTimeMillis();
							bytesDownloaded = html.length();
							time_end = System.currentTimeMillis();
							time_elapsed = time_end - time_start;
							

							logger.info("Bytes descargados: " + bytesDownloaded);
							logger.info("Tiempo inicial: " + time_start + " nanosgundos");
							logger.info("Tiempo final: " + time_end + " nanosgundos");
							logger.info("Tiempo transcurrido: " + time_elapsed + " nanosgundos");
							
							// Enviando estadistica de descarga a la cola de estadisticas
							summaryQueue.put(new SummaryTask(Constants.DEFAULT_ID, Constants.TASK_STATUS.SUBMITTED, 
									0, true, time_elapsed));
							
							logger.info("Rescato los tags LINK, IMG y SCRIPT e inserto las tasks en la cola");
							for(String tag: Arrays.asList(Constants.IMG_TAG, Constants.SCRIPT_TAG, Constants.LINK_TAG)) {
								logger.debug("Analizando el tag " + tag + "sobre el documento: html");
								requestList = getLinks(html, tag);
								logger.debug("Recursos encontrados en la pagina analizada: " + requestList.size());
								for(String request : requestList) {
									logger.info("Enviando una nueva task con los siguiente parametros:\nTaskId: " + taskId
											+ "\nMetodo: " + Constants.GET_METHOD + "\nRequest: " + request + "\nEstado: " + Constants.TASK_STATUS.SUBMITTED);
									downloaderTaskPendingQueue.put(new DownloaderTask(taskId, Constants.GET_METHOD, request, Constants.TASK_STATUS.SUBMITTED, tag));
									++taskId;
								}									
							}
							// Informo al monitor que analice una url
							reportQueue.put(new ReportTask(Constants.DEFAULT_ID, Constants.TASK_STATUS.EXECUTING, true, null));
							
							logger.info("Esperando a que terminen los downloaders");
							// 
							while(taskId > 0) {
								finishedTask = downloaderTaskFinishedQueue.take();
								taskId--;
								logger.info("Task finalizada:\nTaskId: " + finishedTask.getId() + "\nStatus: " + finishedTask.getStatus());
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						// Informo al summary que fallo la descarga
						summaryQueue.put(new SummaryTask(Constants.DEFAULT_ID, Constants.TASK_STATUS.SUBMITTED, 
								0, false, 0));
						// Informo al monitor que fallo la descarga
						reportQueue.put(new ReportTask(Constants.DEFAULT_ID, Constants.TASK_STATUS.FAILED, true, null));
						e.printStackTrace();
					}

				}
				// Informo que el user termino
				logger.info("Informando al monitor que el usuario termino");
				reportQueue.put(new ReportTask(Constants.DEFAULT_ID, Constants.TASK_STATUS.FINISHED, true, null));
				logger.info("Informando al loadtestconsolo que el usuario termino de ejecutar el script");
				userTaskFinishedQueue.put(new UserTask(Constants.DEFAULT_ID, Constants.TASK_STATUS.FINISHED));
				
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
			String currentAttribute = null;

			if (tag.equals(Constants.LINK_TAG)) {
				currentAttribute = "href";				
			}
			else if (tag.equals(Constants.SCRIPT_TAG)) {
				currentAttribute = "src";				
			}
			else if (tag.equals(Constants.IMG_TAG)) {
				currentAttribute = "src";
			}
			org.jsoup.nodes.Document doc = Jsoup.parse(page);
			Elements resource = doc.select(tag);
			Iterator<Element> it = resource.iterator();
			
			while(it.hasNext()) {
				requestsList.add(it.next().attr(currentAttribute));
			}
			
			return requestsList;
	   }
}
