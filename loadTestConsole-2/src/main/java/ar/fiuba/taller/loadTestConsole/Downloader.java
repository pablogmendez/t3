package ar.fiuba.taller.loadTestConsole;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.loadTestConsole.Constants.REPORT_EVENT;
import ar.fiuba.taller.utils.HttpRequester;

public class Downloader implements Callable {

	final static Logger logger = Logger.getLogger(Downloader.class);
	private ArrayBlockingQueue<REPORT_EVENT> reportQueue;
	private String url;
	private String type;
	private Map<String, String> propertiesMap;

	public Downloader(ArrayBlockingQueue<REPORT_EVENT> reportQueue,
			String url, String type, Map<String, String> propertiesMap) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.reportQueue = reportQueue;
		this.url = url;
		this.type = type;
		this.propertiesMap = propertiesMap;
	}

	@Override
	public Object call() {
		long time_start, time_end, time_elapsed;
		String strTime = null;
		HttpRequester httpRequester = new HttpRequester();
		
		logger.info("Iniciando Downloader.");
		try {
			reportQueue.put(REPORT_EVENT.RESOURCE_DOWNLOAD);
			time_start = System.currentTimeMillis();
			try {
				httpRequester.doHttpRequest("get", 
						url, null, null, 
						Integer.parseInt(propertiesMap.get(Constants.HTTP_TIMEOUT)));
				time_end = System.currentTimeMillis();
				time_elapsed = time_end - time_start;
				strTime = Long.toString(time_elapsed);
				reportQueue.put(Constants.TYPE_RESOURCE_MAP.get(type));
				reportQueue.put(REPORT_EVENT.RESOURCE_DOWNLOADED);	
			} catch (Exception e) {
				// Do nothing
			}
		} catch (InterruptedException e1) {
			try {
				reportQueue.put(REPORT_EVENT.RESOURCE_DOWNLOADED);
			} catch (InterruptedException e) {
				// Do nothing
			}				
			logger.info("Downloader Interrumpido.");
		}
		return strTime;
	}
}
