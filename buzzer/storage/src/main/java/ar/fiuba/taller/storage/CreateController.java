package ar.fiuba.taller.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;

public class CreateController implements Runnable {
	private BlockingQueue<Command> createQueue;
	private int shardingFactor;
	private UserIndex userIndex;
	private HashtagIndex hashtagIndex;
	private Command command;
	final static Logger logger = Logger.getLogger(App.class);
	
	public CreateController(BlockingQueue<Command> createQueue, int shardingFactor, UserIndex userIndex,
	HashtagIndex hashtagIndex) {
		super();
		this.createQueue 	= createQueue;
		this.shardingFactor = shardingFactor;
		this.userIndex 		= userIndex;
		this.hashtagIndex 	= hashtagIndex;
	}

	public void run() {
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
    	logger.info("Iniciando el create controller");
    	
    	try {
    		while(true) {    			
    			command = createQueue.take();
    			saveMessage(command);
    			updateUserIndex(command);
    			updateHashTagIndex(command);    			
    		}
		} catch (InterruptedException e) {
			logger.error("Error al leer un comando de la cola createQueue");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error al guardar el comando en la base de datos");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (ParseException e) {
			logger.error("Error al actualizar alguno de los indices");
			logger.info(e.toString());
			e.printStackTrace();
		}
    	
	}
}
