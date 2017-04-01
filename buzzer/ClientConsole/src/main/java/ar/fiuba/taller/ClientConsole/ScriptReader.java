package ar.fiuba.taller.ClientConsole;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.*;

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
			JSONObject obj = new JSONObject(commandScript);
			JSONArray arr = obj.getJSONArray(Constants.COMMAND_ARRAY);
			Command command;
			
			logger.info("Leyendo el command script: " + commandScript);
			for (int i = 0; i < arr.length(); i++) {
				command = new Command(arr.getJSONObject(i).getString(
						Constants.COMMAND_KEY), 
						username, 
						arr.getJSONObject(i).getString(Constants.MESSAGE_KEY),
						null);
				commandQueue.put(command);
				logger.info("Se inserto comando con los siguientes parametros: " 
						+ "\nUsuario: " + command.getUser()
						+ "\nComando: " + command.getCommand()
						+ "\nMensaje: " + command.getMessage());
			}
		
		} catch (JSONException e1) {
			logger.error("Error al parsear el JSON con la lista de usuarios");
			logger.info(e1.toString());
			e1.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Error al pushear comandos en la cola");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}
