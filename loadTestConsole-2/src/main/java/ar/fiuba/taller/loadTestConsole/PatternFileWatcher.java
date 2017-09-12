package ar.fiuba.taller.loadTestConsole;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.utils.FileWatcher;


public class PatternFileWatcher  {
	private Semaphore fileChangeSem;
	private String patternFilePath;
	private int pullTime;
	final static Logger logger = Logger.getLogger(PatternFileWatcher.class);
	
	public PatternFileWatcher(String patternFilePath, Semaphore fileChangeSem,
			int pullTime) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.patternFilePath = patternFilePath;
		this.fileChangeSem = fileChangeSem;
		this.pullTime = pullTime;
	}

	public void start() {
		// monitor a single file
		logger.info("Iniciando PatternFileWatcher");
		logger.debug( "patternFilePath -> " + patternFilePath);
		logger.debug( "pullTime -> " + pullTime);
		TimerTask task = new FileWatcher( new File(patternFilePath) ) {
			protected void onChange( File file ) {
				// here we code the action on a change
				logger.info("Ha cambiado el archivo del patron de usuarios");
				fileChangeSem.release();
			}
		};

		Timer timer = new Timer();
		// repeat the check every second
		timer.schedule( task , new Date(), pullTime );
	}

}
