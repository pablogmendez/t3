package ar.fiuba.taller.ClientConsole;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class App 
{
	final static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args )
    {
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
    	Thread userConsoleThread = new Thread(new UserConsole());
    	userConsoleThread.start();
    	try {
			userConsoleThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
