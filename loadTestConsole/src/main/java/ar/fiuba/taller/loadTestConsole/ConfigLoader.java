package ar.fiuba.taller.loadTestConsole;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigLoader {
	
	private Integer simulationTime;
	private Integer userLoginTimeInterval;
	private String function;
	// Array para guardar los parametros de la funcion 
	private ArrayList<Integer> functionPatternParam;

	private static ConfigLoader instance = null;
	
	final static Logger logger = Logger.getLogger(App.class);
	
	protected ConfigLoader() {
		// TODO Auto-generated constructor stub
	}
	
	public static ConfigLoader getInstance() {
		if(instance == null) {
			instance = new ConfigLoader();
		}
		return instance;
	}
	
	public void init(String configFile) {
		
		logger.info("Cargando configuracion del sistema");
		
		functionPatternParam = new ArrayList<Integer>();
		
		 try {
			 Properties properties = null;
			 FileInputStream input = new FileInputStream(Constants.PROPERTIES_FILE);
		
		    // cargamos el archivo de propiedades
			 properties.load(input);
			 
		    // obtenemos las propiedades
			simulationTime = Integer.parseInt(properties.getProperty(Constants.SIMULATION_TIME_PROPERTY));
			userLoginTimeInterval = Integer.parseInt(properties.getProperty(Constants.USER_LOGIN_TIME_INTERVAL_PROPERTY));
			function = properties.getProperty(Constants.FUNCTION_PROPERTY);
			
			functionPatternParam.add(Integer.parseInt(properties.getProperty(Constants.NUMBER_OF_USERS_PROPERTY)));
			
			if(function.equals("stairs")) {
				functionPatternParam.add(Integer.parseInt(properties.getProperty(Constants.STEP_LENGTH_PROPERTY)));
			}
		} catch (IOException ex) {
		   logger.error(ex.toString());
		   ex.printStackTrace();
		}
		 
		logger.info("Parametros cargados exitosamente:");
		logger.info("simulationTime: " + simulationTime);
		logger.info("userLoginTimeInterval: " + userLoginTimeInterval);
		logger.info("function: " + function);
		logger.info("Parametros de la funcion " + function);
		for(Integer param : functionPatternParam) {
			logger.info(param);	
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

}
