package ar.fiuba.taller;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.Date;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import java.text.ParseException;

import com.googlecode.objectify.ObjectifyService;

public class FunctionsCacheServlet extends HttpServlet {
   private static final Logger log = Logger.getLogger(FunctionsCacheServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    FunctionCache functionCacheObj;

    log.info("POST recibido");

    String name = req.getParameter("name");
    log.info("Parametros recibidos");
    log.info("name: " + name);

    log.info("Intentando cargar entidad");    
    functionCacheObj = ObjectifyService.ofy().load().type(FunctionCache.class).
    filter("name", name).first().now();

    try {
      if(functionCacheObj == null) {
        log.info("Entidad inexistente. Creando una nueva.");    
        functionCacheObj = new FunctionCache(name);
      } else {
        log.info("Entidad existente. Incrementando contador.");    
        functionCacheObj.incCount();
      }
      log.info("Persistiendo entidad.");    
      ObjectifyService.ofy().save().entity(functionCacheObj).now();
    } catch (ParseException e) {
      e.printStackTrace();
      log.severe("Error al instanciar una nueva funcion");
    }
    log.info("Servlet finalizado.");    
  }
}