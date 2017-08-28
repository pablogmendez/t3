package ar.fiuba.taller.loadTestConsole;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.text.BadLocationException;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ar.fiuba.taller.loadTestConsole.Constants.REPORT_EVENT;
import ar.fiuba.taller.utils.HttpRequester;

public class User implements Callable {	
	final static Logger logger = Logger.getLogger(User.class);
	private Map<String, String> propertiesMap;
	private ArrayBlockingQueue<SummaryStat> summaryQueue;
	private ArrayBlockingQueue<REPORT_EVENT> reportQueue;

	public User(Map<String, String> propertiesMap,
			ArrayBlockingQueue<SummaryStat> summaryQueue,
			ArrayBlockingQueue<REPORT_EVENT> reportQueue) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.summaryQueue = summaryQueue;
		this.reportQueue = reportQueue;
		this.propertiesMap = propertiesMap;
	}

	private Map<String, String> getResources(String response, String url)
			throws URISyntaxException, IOException, BadLocationException {
		Map<String, String> tmpMap = new HashMap<String, String>();
		Iterator<Element> it = null;
		Document doc = null;
		Elements resource = null;
		String tmpUrl = null;
		
		doc = Jsoup.parse(response);
		for (Map.Entry<String, String> entry : Constants.RESOURCE_MAP.entrySet())
		{
			resource = doc.select(entry.getKey());
			it = resource.iterator();
			while (it.hasNext()) {
				tmpUrl = it.next().attr(entry.getValue());
				if(tmpUrl.indexOf("http") == -1) {
					tmpUrl = normalizeUrl(url, "last") + normalizeUrl(tmpUrl, "first");
				}
				tmpMap.put(entry.getKey(), tmpUrl);
			}
		}
		return tmpMap;
	}

	private String normalizeUrl(String url, String place) {
		if(place.equals("first")) {
			if(url.substring(0).equals("/")) {
				return url.substring(1, url.length() - 1);
			}			
		} else { // last			
			if(url.substring(url.length() - 1).equals("/")) {
				return url.substring(0, url.length() - 2);
			}
		}
		return url;
	}
	
	@Override
	public Object call() throws FileNotFoundException, IOException, ParseException {
		long time_end, time_start, avgTime, successResponse, failedResponse;
		String response = null;
		Map<String, String> resourceMap = null;
		Set<Callable<Downloader>> downloadersSet = new HashSet<Callable<Downloader>>();
		ExecutorService executorService = Executors.newFixedThreadPool(
				Integer.parseInt(propertiesMap.get(
						Constants.MAX_DOWNLOADERS)));
		JSONParser parser = new JSONParser();
		HttpRequester httpRequester = new HttpRequester();		
		Object objScript = parser.parse(new FileReader(propertiesMap.get(
        		Constants.SCRIPT_FILE)));
        JSONObject objStep = null; 
		JSONArray stepsArray = (JSONArray)((JSONObject) objScript).get("steps");
		List<Future<Downloader>> futures = null;
		
		logger.info("Iniciando usuario");
		try {
			while(!Thread.interrupted()) {
				Iterator<JSONObject> it = stepsArray.iterator();
				reportQueue.put(REPORT_EVENT.SCRIPT_EXECUTING);
				while(it.hasNext()) {
					avgTime = 0;
					successResponse = 0;
					failedResponse = 0;
					objStep = it.next();
					logger.info("Siguiente url a analizar: " + (String)objStep.get("url"));
					logger.info("Metodo: " + (String)objStep.get("method"));
					logger.info("headers: " + (String)objStep.get("headers"));
					logger.info("Body: " + (String)objStep.get("body"));
					time_start = System.currentTimeMillis();
					try {
						response = httpRequester.doHttpRequest((String)objStep.get("method"), 
								(String)objStep.get("url"),
								(String)objStep.get("headers"),
								(String)objStep.get("body"), 
								Integer.parseInt(propertiesMap.get(Constants.HTTP_TIMEOUT)));
						time_end = System.currentTimeMillis();
						avgTime = time_end - time_start;
						successResponse++;
						resourceMap = getResources(response, (String)objStep.get("url"));
//						for (Map.Entry<String, String> entry : resourceMap.entrySet()) {
//							downloadersSet.add(new Downloader(reportQueue, 
//									entry.getKey(), entry.getValue(), propertiesMap));
//						}
//						reportQueue.put(REPORT_EVENT.URL_ANALYZED);
//						try {
//							futures = executorService.invokeAll(downloadersSet);
//							for(Future<Downloader> future : futures){
//								if(future.get() != null) {
//									avgTime = (avgTime + Long.parseLong(
//											future.get().toString())/2);
//									successResponse++;
//								} else {
//									failedResponse++;
//								}
//							}
//						} catch (ExecutionException e) {
//							// Do nothing
//						}
					} catch (Exception e) {
						logger.error("No se ha podido descargar el recurso.");
						failedResponse++;
					}
					summaryQueue.put(new RequestStat(successResponse,
							failedResponse,
							avgTime));
				}
			}
		reportQueue.put(REPORT_EVENT.SCRIPT_EXECUTED);
		} catch (InterruptedException e) {
			logger.info("Senial de interrupcion recibida. Eliminado los downloaders.");
			executorService.shutdownNow();
		}
		return null;
	}
}
