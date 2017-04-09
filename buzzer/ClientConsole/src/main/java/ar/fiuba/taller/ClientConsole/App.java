package ar.fiuba.taller.ClientConsole;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Constants;

public class App 
{
	final static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args )
    {
    	PropertyConfigurator.configure(Constants.LOGGER_CONF);
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
    	List<Thread> usersList = new ArrayList<Thread>();
    	String username, mode, reg;
    	
    	logger.info("Se inicia una nueva instancia de ClientConsole");
    	
		try {
			// Obtengo los usuarios y los pongo a ejecutar el script
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(Constants.USERS_FILE));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray arr = (JSONArray) jsonObject.get(Constants.USERS_KEY);
			Thread userConsoleThread;
			logger.info("Leyendo el archivo de usuarios a simular");
			Iterator<String> iterator = arr.iterator();
			StringTokenizer st;
			while (iterator.hasNext()) {
				username = iterator.next();
//				st = new StringTokenizer(reg, "#");
//				username = st.nextToken();
//				mode = st.nextToken();
				logger.info("Siguiente usuario a crear: " + username);
			 	userConsoleThread = new Thread(new UserConsole(username));
			 	userConsoleThread.start();
			 	usersList.add(userConsoleThread);
			 	logger.info("Usuario " + userConsoleThread.getId() + " creado!");
			}

			// Espero a que los usuarios hayan terminado de ejecutar
			logger.info("Esperando a que los usuarios terminen: " + usersList.size());
			for(Thread userThread : usersList ) {
				userThread.join();
				logger.info("Usuario " + userThread.getId() + " finalizado!");
			}
		} catch (InterruptedException e) {
			logger.error("Error al joinear los threads de usuarios");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.error("No se encontro el archivo de usuarios");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error al leer el archivo de usuarios");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (ParseException e) {
			logger.error("Error al parsear el archivo de usuarios");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}