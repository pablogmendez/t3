package ar.fiuba.taller.ClientConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;

public class App 
{
	final static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args )
    {
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
    	List<Thread> usersList = new ArrayList<Thread>();
    	
    	logger.info("Se inicia una nueva instancia de ClientConsole");
    	
		try {
			// Obtengo los usuarios y los pongo a ejecutar el script
			JSONObject obj = new JSONObject(Constants.USERS_FILE);
			JSONArray arr = obj.getJSONArray(Constants.USERS_KEY);
			Thread userConsoleThread;
			logger.info("Leyendo el archivo de usuarios a simular");
			for (int i = 0; i < arr.length(); i++) {
					logger.info("Siguiente usuario a crear: " + arr.getJSONObject(i).getString(Constants.NAME_KEY));
				 	userConsoleThread = new Thread(new UserConsole(arr.getJSONObject(i).getString(Constants.NAME_KEY)));
				 	userConsoleThread.start();
				 	usersList.add(userConsoleThread);
				 	logger.info("Usuario " + userConsoleThread.getId() + " creado!");
			}

			// Espero a que los usuarios hayan terminado de ejecutar
			logger.info("Esperando a que los usuarios terminen");
			for(Thread userThread : usersList ) {
				userThread.join();
				logger.info("Usuario " + userThread.getId() + " finalizado!");
			}
			
		} catch (JSONException e1) {
			logger.error("Error al parsear el JSON con la lista de usuarios");
			logger.info(e1.toString());
			e1.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Error al joinear los threads de usuarios");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}