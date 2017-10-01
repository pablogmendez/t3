package ar.fiuba.taller.ClientConsole;

import java.io.IOException;
import java.util.HashSet;
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

		if (args.length == 0) {
			displayHelp();
		}

		String mode = args[0];

		try {
			configLoader = new ConfigLoader(Constants.CONF_FILE);
		} catch (IOException e) {
			logger.error("Error al cargar la configuracion");
			System.exit(Constants.EXIT_FAILURE);
		}

		if (mode.equals(Constants.INTERACTIVE_MODE)) {
			if ((args[1] == null || ("").equals(args[1]))
					&& (args[2] == null || ("").equals(args[2]))) {
				displayHelp();
			}
			System.out.printf(
					"Iniciando el Client console en modo interactivo para el usuario %s",
					args[1]);
			InteractiveUser interactiveUser = new InteractiveUser(configLoader.getProperties(), args[1], args[2]);
			interactiveUser.run();
		} else if (mode.equals(Constants.BATCH_MODE)) {
			try {
				usersAmount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.out.printf("Argumento invalido");
				System.exit(1);
			}
			ExecutorService executor = Executors.newFixedThreadPool(usersAmount);
			System.out.printf("Iniciando el Client console en modo batch");
			for (int i = 0; i < Integer.parseInt(args[1]); i++) {
				usersSet.add(new BatchUser(configLoader.getProperties(),
						"user" + i, configLoader.getProperties().get(Constants.USERS_RESPONSE_HOST)));
			}
			try {
				executor.invokeAll(usersSet);
			}catch (Exception e) {
				logger.error("Error al invocar a los usuarios: " + e);
			} finally {
				executor.shutdownNow();
				try {
					executor.awaitTermination(
							Constants.USER_THREAD_WAIT_TIME,
							TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					// Do nothing
				}	
			}
		}
	}

	private static void displayHelp() {
		System.out.printf(
				"Client console%n**************%nSintaxis:%n./ClientConsole <params>%nParametros:%ni [username] [host]: Inicia el cliente en modo interactivo%nusername: Nombre del usuario%nhost: Nombre y puerto del servidor a conectar (ej. localhost:9092)%n%nb [usersamount] [host]: Inicia el cliente en modo batch%nusersamount: Cantidad de usuarios a simular%nhost: Nombre y puerto del servidor a conectar (ej. localhost:9092)%n%n");
		System.exit(Constants.EXIT_FAILURE);
	}

}