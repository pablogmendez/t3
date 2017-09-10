package ar.fiuba.taller.ClientConsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;
import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;

public class MainClientConsole {
	final static Logger logger = Logger.getLogger(MainClientConsole.class);

	public static void main(String[] args) {
		PropertyConfigurator.configure(Constants.LOGGER_CONF);
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Set<Callable<String>> usersSet = new HashSet<Callable<String>>();
		int usersAmount = 0;
		ConfigLoader configLoader = null;
		
		if(args.length == 0) {
			displayHelp();
		}
		
		final String mode = args[0];

		try {
			configLoader = new ConfigLoader(Constants.CONF_FILE);
		} catch (IOException e) {
			logger.error("Error al cargar la configuracion");
			System.exit(Constants.EXIT_FAILURE);
		}

		try {
			usersAmount = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			// Do nothing
		}
		
		final Thread userThread = createUser(mode, configLoader.getProperties(), args[1], args[2]);
		final ExecutorService executor = createUsers(mode, usersAmount);
		
		if (mode.equals(Constants.INTERACTIVE_MODE)) {
			System.out.printf(
					"Iniciando el Client console en modo interactivo para el usuario %s",
					args[1]);
			userThread.start();
		} else if (mode.equals(Constants.BATCH_MODE)) {
			System.out.printf("Iniciando el Client console en modo batch");
			try {
				for(int i = 0; i < Integer.parseInt(args[1]); i++) {
					usersSet.add(new BatchUser(configLoader.getProperties(), "user" + i, "localhost:9092"));
				}
				executor.invokeAll(usersSet);
			} catch (InterruptedException e) {
				// Do nothing
			}
		} else {
			displayHelp();
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (mode.equals(Constants.INTERACTIVE_MODE)) {
					userThread.interrupt();
					try {
						userThread.join(Constants.USER_THREAD_WAIT_TIME);
					} catch (InterruptedException e) {
						// Do nothing
					}
				} else {
					executor.shutdownNow();
					try {
						executor.awaitTermination(Constants.USER_THREAD_WAIT_TIME, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						// Do nothing
					}
				}
			}
		});
	}

	private static Thread createUser(String mode, Map<String, String> config, String userName, String hostName) {
		if (mode.equals(Constants.INTERACTIVE_MODE)) {
			if ((userName == null || ("").equals(userName))
					&& (hostName == null || ("").equals(hostName))) {
				displayHelp();
			};
			System.out.printf(
					"Iniciando el Client console en modo interactivo para el usuario %s",
					userName);
			return new Thread(
					new InteractiveUser(config, userName, hostName));
		}
		else {
			return null;
		}
	}
	
	private static ExecutorService createUsers(String mode, int userAmount) {
		if (mode.equals(Constants.BATCH_MODE)) {		
			ExecutorService executor = Executors.newFixedThreadPool(userAmount);
			return executor;
		} else {
			return null;
		}
	}
	
	private static void displayHelp() {
		System.out.printf(
				"Client console%n**************%nSintaxis:%n./ClientConsole <params>%nParametros:%ni [username] [host]: Inicia el cliente en modo interactivo%nusername: Nombre del usuario%nhost: Nombre y puerto del servidor a conectar (ej. localhost:9092)%n%nb [usersamount] [host]: Inicia el cliente en modo batch%nusersamount: Cantidad de usuarios a simular%nhost: Nombre y puerto del servidor a conectar (ej. localhost:9092)%n%n");
		System.exit(Constants.EXIT_FAILURE);
	}

}