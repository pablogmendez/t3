package ar.fiuba.taller.loadTestConsole;

import ar.fiuba.taller.loadTestConsole.Constants.REPORT_EVENT;
import ar.fiuba.taller.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class LoadTestConsole {	
	private Map<String, String> propertiesMap;
	private Map<Integer, Integer> usersPatternMap;
	
	final static Logger logger = Logger.getLogger(LoadTestConsole.class);
	
	public LoadTestConsole() throws Exception {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		// Cargo la configuracion del archivo de properties y el patron de usuarios
		loadProperties();
		loadUserPattern();
	}

	public void start() {
		Semaphore fileChangeSem = new Semaphore(0);
		ArrayBlockingQueue<SummaryStat> summaryQueue = 
				new ArrayBlockingQueue<SummaryStat>(Integer.parseInt(
						propertiesMap.get(Constants.SUMMARY_QUEUE_SIZE)));
		ArrayBlockingQueue<REPORT_EVENT> reportQueue = 
				new ArrayBlockingQueue<REPORT_EVENT>(Integer.parseInt(
						propertiesMap.get(Constants.REPORT_QUEUE_SIZE)));
		Summary summary = new Summary();
		Report report = new Report();
		
		// Creo los threads
		
		Thread usersControllerThread = new Thread(new UsersController(
				Integer.parseInt(propertiesMap.get(Constants.MAX_USERS)), 
				Integer.parseInt(propertiesMap.get(Constants.MAX_DOWNLOADERS)), 
				usersPatternMap));
		Thread patternFileWatcherThread = new Thread(new PatternFileWatcher(
				propertiesMap.get(Constants.USERS_PATTERN_FILE), 
				fileChangeSem,
				Integer.parseInt(propertiesMap.get(Constants.FILE_WATCHER_TIMEOUT))));
		Thread summaryControllerThread = new Thread(new SummaryController(summaryQueue, summary));
		Thread summaryPrinterThread = new Thread(new SummaryPrinter(summary));
		Thread reportControllerThread = new Thread(new ReportController(reportQueue, report));
		Thread reportPrinterThread = new Thread(new ReportPrinter(report));
		
		logger.info("Iniciando LoadTestConsole");
		
		while(!Thread.interrupted()) {
			
		}
	}	
	
	private void loadProperties() throws Exception {
		logger.info("Cargando configuracion");
		propertiesMap = new HashMap<String, String>();
		Properties properties = new Properties();
		try {
			properties.load(Thread.currentThread()
				    .getContextClassLoader().getResourceAsStream(Constants.PROPERTIES_FILE));
		} catch (IOException e) {
			System.err.println("No ha sido posible cargar el archivo de propiedades");
			throw new Exception();
		}
		for (String key : properties.stringPropertyNames()) {
		    String value = properties.getProperty(key);
		    propertiesMap.put(key, value);
		    logger.debug("Parametro cargado: " + key + "->" + value);
		}
	}
	
	private void loadUserPattern() {
		
	}
}
