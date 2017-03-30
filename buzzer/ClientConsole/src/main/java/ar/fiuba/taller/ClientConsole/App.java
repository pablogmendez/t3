package ar.fiuba.taller.ClientConsole;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class App 
{
    public static void main( String[] args )
    {
    	Thread userConsoleThread = new Thread(new UserConsole());
    	userConsoleThread.start();
    	try {
			userConsoleThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
    	
    }
}
