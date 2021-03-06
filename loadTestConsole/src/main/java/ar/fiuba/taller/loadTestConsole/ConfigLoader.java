package ar.fiuba.taller.loadTestConsole;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigLoader {

	private Integer simulationTime;
	private Integer userLoginTimeInterval;
	private String function;
	private Integer usersQueueSize;
	private Integer maxsizeUserPoolThread;
	private Integer maxSizeDownloadersPoolThread;
	private Integer tasksQueueSize;
	// Array para guardar los parametros de la funcion
	private ArrayList<Integer> functionPatternParam;

	private static ConfigLoader instance = null;

	final static Logger logger = Logger.getLogger(App.class);

	protected ConfigLoader() {
		// TODO Auto-generated constructor stub
	}

	public static ConfigLoader getInstance() {
		if (instance == null) {
			instance = new ConfigLoader();
		}
		return instance;
	}

	public void init(String configFile) throws IOException {
		logger.info("Cargando configuracion del sistema");
		functionPatternParam = new ArrayList<Integer>();
		Properties properties = new Properties();
		FileInputStream input = new FileInputStream(
				Constants.PROPERTIES_FILE);

		// cargamos el archivo de propiedades
		properties.load(input);

		// obtenemos las propiedades
		function = properties.getProperty(Constants.FUNCTION_PROPERTY);
		usersQueueSize = Integer.parseInt(
				properties.getProperty(Constants.USERES_QUEUE_SIZE));
		maxsizeUserPoolThread = Integer.parseInt(properties
				.getProperty(Constants.MAX_SIZE_USER_POOL_THREAD));
		maxSizeDownloadersPoolThread = Integer.parseInt(properties
				.getProperty(Constants.MAX_SIZE_DOWNLOADERS_POOL_THREAD));
		tasksQueueSize = Integer.parseInt(
				properties.getProperty(Constants.TASKS_QUEUE_SIZE));
		functionPatternParam.add(Integer.parseInt(properties
				.getProperty(Constants.NUMBER_OF_USERS_PROPERTY)));

		if (function.equals("StairsUserPattern")) {
			functionPatternParam.add(Integer.parseInt(properties
					.getProperty(Constants.STEP_LENGTH_PROPERTY)));

			logger.info("Parametros cargados exitosamente:");
			logger.info("simulationTime: " + simulationTime);
			logger.info("userLoginTimeInterval: " + userLoginTimeInterval);
			logger.info("function: " + function);
			logger.info("Parametros de la funcion " + function);
			for (Integer param : functionPatternParam) {
				logger.info(param);
			}
		}
	}

	public Integer getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(Integer simulationTime) {
		this.simulationTime = simulationTime;
	}

	public Integer getUserLoginTimeInterval() {
		return userLoginTimeInterval;
	}

	public void setUserLoginTimeInterval(Integer userLoginTimeInterval) {
		this.userLoginTimeInterval = userLoginTimeInterval;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public ArrayList<Integer> getFunctionPatternParam() {
		return functionPatternParam;
	}

	public Integer getUsersQueueSize() {
		return usersQueueSize;
	}

	public void setUsersQueueSize(Integer usersQueueSize) {
		this.usersQueueSize = usersQueueSize;
	}

	public Integer getMaxsizeUserPoolThread() {
		return maxsizeUserPoolThread;
	}

	public void setMaxsizeUserPoolThread(Integer maxsizeUserPoolThread) {
		this.maxsizeUserPoolThread = maxsizeUserPoolThread;
	}

	public Integer getMaxSizeDownloadersPoolThread() {
		return maxSizeDownloadersPoolThread;
	}

	public void setMaxSizeDownloadersPoolThread(
			Integer maxSizeDownloadersPoolThread) {
		this.maxSizeDownloadersPoolThread = maxSizeDownloadersPoolThread;
	}

	public Integer getTasksQueueSize() {
		return tasksQueueSize;
	}

	public void setTasksQueueSize(Integer tasksQueuesListSize) {
		this.tasksQueueSize = tasksQueuesListSize;
	}

}
