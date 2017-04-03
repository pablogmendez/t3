package ar.fiuba.taller.ClientConsole;

import java.io.FileNotFoundException;
import java.io.FileReader;
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

public class ScriptReader implements Runnable {

	final static Logger logger = Logger.getLogger(App.class);
	
	BlockingQueue<Command> commandQueue;
	String commandScript;
	String username;
	
	public ScriptReader(BlockingQueue<Command> commandQueue, String commandScript, String username) {
		this.commandQueue = commandQueue;
		this.commandScript = commandScript;
		this.username = username;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		logger.info("Iniciando el script reader");
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(commandScript));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray commandArray = (JSONArray) jsonObject.get(Constants.COMMAND_ARRAY);
            Iterator<JSONObject> iterator = commandArray.iterator();
            JSONObject commandObject;
			Command command;
			
			logger.info("Leyendo el command script: " + commandScript);
			 while (iterator.hasNext()) {
				commandObject = iterator.next();
				command = new Command((String)commandObject.get(
						Constants.COMMAND_KEY), 
						username, 
						(String)commandObject.get(Constants.MESSAGE_KEY),
						null, null);
				logger.info("Se inserto comando con los siguientes parametros: " 
						+ "\nUsuario: " + command.getUser()
						+ "\nComando: " + command.getCommand()
						+ "\nMensaje: " + command.getMessage());
				commandQueue.put(command);
			}
		} catch (InterruptedException e) {
			logger.error("Error al pushear comandos en la cola");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.error("No se encontro el archivo de comandos");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error al leer el archivo de comandos");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (ParseException e) {
			logger.error("Error al parsear el archivo de comandos");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}
