package ar.fiuba.taller.loadTestConsole;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
	final static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args )
    {
    	
    	logger.info("[*] Se inicia una nueva instancia de LoadTestConsole");
    	
    	ConfigLoader.getInstance().init(Constants.PROPERTIES_FILE);
    	
    	Thread userControlThread = new Thread(new UsersControl(ConfigLoader.getInstance().getSimulationTime()));
    	
    	userControlThread.start();
    	try {
			userControlThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
    	    
        logger.info("[*] Finaliza la instancia de LoadTestConsole");
    }
}
