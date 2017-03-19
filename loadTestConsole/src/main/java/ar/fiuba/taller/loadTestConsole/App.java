package ar.fiuba.taller.loadTestConsole;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
	final static Logger logger = Logger.getLogger("load");
    public static void main( String[] args )
    {
    	 Properties propiedades = new Properties();
    	    InputStream entrada = null;

    	    try {

    	        entrada = new FileInputStream("src/main/resources/configuration.properties");

    	        // cargamos el archivo de propiedades
    	        propiedades.load(entrada);

    	        // obtenemos las propiedades y las imprimimos
    	        System.out.println(propiedades.getProperty("basedatos"));
    	        System.out.println(propiedades.getProperty("usuario"));
    	        System.out.println(propiedades.getProperty("clave"));
    	        logger.error("This is error : " );

    	    } catch (IOException ex) {
    	        ex.printStackTrace();
    	    } finally {
    	        if (entrada != null) {
    	            try {
    	                entrada.close();
    	            } catch (IOException e) {
    	                e.printStackTrace();
    	            }
    	        }
    	    }
    }
}
