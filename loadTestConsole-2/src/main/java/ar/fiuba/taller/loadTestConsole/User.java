package ar.fiuba.taller.loadTestConsole;

import java.io.FileReader;
import java.io.IOException;
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
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ar.fiuba.taller.loadTestConsole.Constants.REPORT_EVENT;
import ar.fiuba.taller.utils.HttpRequester;
import ar.fiuba.taller.utils.PageAnalyzer;

public class User implements Runnable {	
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

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		long time_end, time_start, avgTime = 0, successResponse = 0, failedResponse = 0;
		String response = null;
		Map<String, String> resourceMap = null;
		Set<Callable<Downloader>> downloadersSet  = new HashSet<Callable<Downloader>>();
		ExecutorService executorService = Executors.newFixedThreadPool(
				Integer.parseInt(propertiesMap.get(
						Constants.MAX_DOWNLOADERS)));
		JSONParser parser = new JSONParser();
		HttpRequester httpRequester = new HttpRequester();		
		Object objScript = null;
        JSONObject objStep = null; 
		JSONArray stepsArray = null;
		List<Future<Downloader>> futures = null;
		PageAnalyzer pageAnalyzer = new PageAnalyzer();
		Iterator<JSONObject> it = null;
		
		logger.info("Iniciando usuario");
		try {
			objScript = parser.parse(new FileReader(propertiesMap.get(
					Constants.SCRIPT_FILE)));
			stepsArray = (JSONArray)((JSONObject) objScript).get("steps");
			reportQueue.put(REPORT_EVENT.SCRIPT_EXECUTING);
			while(!Thread.interrupted()) {
				if(it == null || !it.hasNext()) {
					avgTime = 0;
					successResponse = 0;
					failedResponse = 0;
					downloadersSet.clear();
					it = stepsArray.iterator();
				}
				objStep = it.next();
				logger.info("Siguiente url a analizar: " + (String)objStep.get("url"));
				logger.info("Metodo: " + (String)objStep.get("method"));
				logger.info("headers: " + (String)objStep.get("headers"));
				logger.info("Body: " + (String)objStep.get("body"));
				time_start = System.currentTimeMillis();
				try {
					logger.debug("Hago el request");
					response = httpRequester.doHttpRequest((String)objStep.get("method"), 
							(String)objStep.get("url"),
							(String)objStep.get("headers"),
							(String)objStep.get("body"), 
							Integer.parseInt(propertiesMap.get(Constants.HTTP_TIMEOUT))*Constants.SLEEP_UNIT);
					logger.debug("Request listo");
					time_end = System.currentTimeMillis();
					avgTime = time_end - time_start;
					if(response == null) {
						logger.debug("Request igual a null");
						failedResponse++;
					} else {
						logger.debug("Request distinto de null");
						successResponse++;
						resourceMap = pageAnalyzer.getResources(response, (String)objStep.get("url"));
//						reportQueue.put(REPORT_EVENT.URL_ANALYZED);
						for (Map.Entry<String, String> entry : resourceMap.entrySet()) {
							logger.debug("tipo: " + entry.getKey());
							logger.debug("recurso: " + entry.getValue());
							downloadersSet.add(new Downloader(reportQueue, 
									entry.getValue(), entry.getKey(), propertiesMap));
						}
						logger.debug("CANTIDAD DE DOWNLOADERS A DISPARAR: " + downloadersSet.size());
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
					}
				} catch (Exception e) {
					logger.error("No se ha podido descargar el recurso.");
					failedResponse++;
				}
				logger.debug("ESCRIBO LAS ESTADISTICAS ANTES DE MANDARLAS");
				logger.debug("success " + successResponse);
				logger.debug("failed " + failedResponse);
				summaryQueue.put(new RequestStat(successResponse,
						failedResponse,
						avgTime));
			}
		} catch (InterruptedException e) {
			logger.info("Senial de interrupcion recibida. Eliminado los downloaders.");
			executorService.shutdownNow();
		} catch(IOException | ParseException e) {
			logger.error("Nose ha podido leer el script.");
		}
		try {
			logger.debug("YYYYYYYY");
			reportQueue.put(REPORT_EVENT.SCRIPT_EXECUTED);
		} catch (InterruptedException e1) {
			// Do nothing
			logger.debug("BBBBB");
		}
//		logger.debug("ASDASD");
	}
}
