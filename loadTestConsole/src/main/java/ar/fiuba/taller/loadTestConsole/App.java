package ar.fiuba.taller.loadTestConsole;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
	final static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
    	
//    	logger.info("[*] Se inicia una nueva instancia de LoadTestConsole");
//    	
//    	Thread userControlThread = new Thread(new LoadTestConsole());
//    	
//    	userControlThread.start();
//    	try {
//			userControlThread.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	 
//    	    
//        logger.info("[*] Finaliza la instancia de LoadTestConsole");
    	Date date = new Date();
        Package pack = date.getClass().getPackage();
        String packageName = pack.getName();
        System.out.println("Package Name = " + packageName);

    }
}
