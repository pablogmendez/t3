package ar.fiuba.taller.loadTestConsole;

import ar.fiuba.taller.loadTestConsole.Constants.REPORT_EVENT;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class LoadTestConsole {	
	private Map<String, String> propertiesMap;
	private Map<Integer, Integer> usersPatternMap;
	
	final static Logger logger = Logger.getLogger(LoadTestConsole.class);
	
	public LoadTestConsole() throws IOException {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		// Cargo la configuracion del archivo de properties
		loadProperties();
	}

	public void start() throws FileNotFoundException {
		Semaphore fileChangeSem = new Semaphore(0);
		ArrayBlockingQueue<SummaryStat> summaryQueue = 
				new ArrayBlockingQueue<SummaryStat>(Integer.parseInt(
						propertiesMap.get(Constants.SUMMARY_QUEUE_SIZE)));
		ArrayBlockingQueue<REPORT_EVENT> reportQueue = 
				new ArrayBlockingQueue<REPORT_EVENT>(Integer.parseInt(
						propertiesMap.get(Constants.REPORT_QUEUE_SIZE)));
		Summary summary = new Summary();
		Report report = new Report();
		AtomicInteger patternTime = new AtomicInteger(0);
		
		// Creo los threads
		Thread usersControllerThread = null;
		Thread patternFileWatcherThread = new Thread(new PatternFileWatcher(
				propertiesMap.get(Constants.USERS_PATTERN_FILE), 
				fileChangeSem,
				Integer.parseInt(propertiesMap.get(Constants.FILE_WATCHER_TIMEOUT))));
		Thread summaryControllerThread = new Thread(new SummaryController(summaryQueue, summary));
		Thread summaryPrinterThread = new Thread(new SummaryPrinter(summary, propertiesMap));
		Thread reportControllerThread = new Thread(new ReportController(reportQueue, report));
		Thread reportPrinterThread = new Thread(new ReportPrinter(report, propertiesMap));
		
		logger.info("Iniciando LoadTestConsole");
		logger.info("Iniciando los threads");
		patternFileWatcherThread.start();
		summaryControllerThread.start();
		summaryPrinterThread.start();
		reportControllerThread.start();
		reportPrinterThread.start();
		
		while(!Thread.interrupted()) {
			logger.info("Cargando patron de usuarios");
			loadUserPattern(patternTime);
			logger.info("Disparando el usersControllerThread");
			usersControllerThread = new Thread(new UsersController(
					propertiesMap, usersPatternMap, patternTime,
					summaryQueue, reportQueue));
			usersControllerThread.start();
			try {
				// Me quedo esperando hasta que cambie el archivo
				logger.info("Esperando hasta que cambie el archivo");
				fileChangeSem.acquire(); 
			} catch (InterruptedException e) {
				// Do nothing
				logger.error("No se ha podido tomar el semaforo");
			}
			logger.info("Cambio el archivo. Interrumpiendo el usersControllerThread");
			usersControllerThread.interrupt();
			try {
				usersControllerThread.join();
			} catch (InterruptedException e) {
				// Do nothing
			}
		}
	}	
	
	private void loadProperties() throws IOException {
		logger.info("Cargando configuracion");
		propertiesMap = new HashMap<String, String>();
		Properties properties = new Properties();
		try {
			properties.load(Thread.currentThread()
				    .getContextClassLoader().getResourceAsStream(Constants.PROPERTIES_FILE));
		} catch (IOException e) {
			System.err.println("No ha sido posible cargar el archivo de propiedades");
			throw new IOException();
		}
		for (String key : properties.stringPropertyNames()) {
		    String value = properties.getProperty(key);
		    propertiesMap.put(key, value);
		    logger.debug("Parametro cargado: " + key + "->" + value);
		}
	}
	
	private void loadUserPattern(AtomicInteger patternTime) throws FileNotFoundException {
		File file = new File(propertiesMap.get(Constants.USERS_PATTERN_FILE));
		int nextInt;
		usersPatternMap = new HashMap<Integer, Integer>();
	    if (file == null || !file.canRead()) {
	        throw new IllegalArgumentException("file not readable: " + file);
	    }

	    @SuppressWarnings("resource")
		final Scanner s = new Scanner(file).useDelimiter(Pattern.compile("(\\n)|:"));
	    try {
		    while (s.hasNext()) {
		    	nextInt = s.nextInt();
		    	if(nextInt >= patternTime.get()) {
		    		usersPatternMap.put(nextInt, s.nextInt());
		    	}
		    }	
	    } catch (InputMismatchException e) {
	    	// Do nothing
	    }
	}
}
