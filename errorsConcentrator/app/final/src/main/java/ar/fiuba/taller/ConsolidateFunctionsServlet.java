package ar.fiuba.taller;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;

import java.io.IOException;
import java.util.Date;
import java.util.Arrays;
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
import com.googlecode.objectify.cmd.Query;

public class ConsolidateFunctionsServlet extends HttpServlet {
   private static final Logger log = Logger.getLogger(ConsolidateFunctionsServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String task;
    String request;
    String functions = "";
    FunctionCache functionCacheObj;
    Queue consolidateQueue = QueueFactory.getQueue("consolidate-queue");

    log.info("POST recibido");

    task = req.getParameter("task");

    if(task.equals("init")) {
      log.info("Tarea recibida: init");
      Query<FunctionCache> query = ObjectifyService.ofy().load()
      .type(FunctionCache.class).order("-count").limit(Constants.QUERY_LIMIT);
      QueryResultIterator<FunctionCache> iterator = query.iterator();
      log.info("Armando la lista de funciones obtenidas");
      while (iterator.hasNext()) {
        functions += iterator.next() + ",";
      }
      log.info("Funciones obtenidas: " + functions);
      functions = functions.substring(0, functions.length() - 1);
      request = "?task=update&functions=" + functions;
      log.info("Enviando lista de funciones al update para iniciar le batch de actualizacion de la tabla maestra de funciones");
      consolidateQueue.add(TaskOptions.Builder.withUrl("/consolidatefunctions")
        .param("task", "update").param("functions", functions));
      log.info("Enviando tarea para iniciar el batch de limpieza de la cache");
      consolidateQueue.add(TaskOptions.Builder.withUrl("/consolidatefunctions")
        .param("task", "clean"));
    } else if (task.equals("clean")) {
      log.info("Tarea recibida: clean");
      functionCacheObj = ObjectifyService.ofy().load().type(FunctionCache.class).
      filter("count >", 0).first().now();
      if(functionCacheObj == null) {
        log.info("Termine de resetear la cache. Termino aca");
      } else {
        log.info("Reseteando la funcion: " + functionCacheObj.getName());
        functionCacheObj.reset();
        ObjectifyService.ofy().save().entity(functionCacheObj).now();
        log.info("Pasando la posta a otro clean");
        consolidateQueue.add(TaskOptions.Builder.withUrl("/consolidatefunctions")
          .param("task", "clean"));
      }
    } else if (task.equals("update")) {
      log.info("Obtengo la lista de funciones");
      functions = req.getParameter("functions");
      if(functions.length() == 0) {
        log.info("Termine la actualizacion");
        log.info("Comienzo con el batch de incremento de las horas");
        consolidateQueue.add(TaskOptions.Builder.withUrl("/consolidatefunctions")
          .param("task", "inchours"));
      } else {
        log.info("Obtengo la siguiente funcion a almacenar");
        String[] functionsArray = explode(functions);
        String name = functionsArray[functionsArray.length - 1];
        log.info("Funcion a almacenar: " + name);
        Function function = new Function(name);
        ObjectifyService.ofy().save().entity(function).now();
        consolidateQueue.add(TaskOptions.Builder.withUrl("/consolidatefunctions")
        .param("task", "update").param("functions",
          implode(Arrays.copyOf(functionsArray, functionsArray.length-1))));
      }
    } else if (task.equals("inchours")) {
      Query<Function> query= ObjectifyService.ofy().load().type(Function.class);
      String cursorStr = req.getParameter("cursor");
      if (cursorStr != null)
        query = query.startAt(Cursor.fromWebSafeString(cursorStr));
      QueryResultIterator<Function> iterator = query.iterator();
      if(iterator.hasNext()) {
          Function function = iterator.next();
          function.incHour();
          ObjectifyService.ofy().save().entity(function).now();
          consolidateQueue.add(TaskOptions.Builder.withUrl("/consolidatefunctions")
          .param("task", "inchours").param("cursor", iterator.getCursor().toWebSafeString()));
      } else {
        log.info("Termino el incremento de horas y empiezo a borrar los registros que estan de mas");
        consolidateQueue.add(TaskOptions.Builder.withUrl("/consolidatefunctions")
          .param("task", "delete"));
      }
    } else if (task.equals("delete")) {
        Function function = ObjectifyService.ofy().load().type(Function.class).filter("hour >=", 7).first().now();
        if(function != null) {
          log.info("Eliminando: " + function.getName());
          ObjectifyService.ofy().delete().entity(function).now();
          consolidateQueue.add(TaskOptions.Builder.withUrl("/consolidatefunctions")
          .param("task", "delete"));
        } else {
          log.info("Termine de borrar todo lo viejo");
        }
    } else {
      log.severe("Parametros invalidos");
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
    log.info("Servlet finalizado.");
  }

  private String[] explode(String string) {
    return string.split(",");
  }

  private String implode(String[] stringArray) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < stringArray.length; i++) {
        sb.append(stringArray[i]);
        if (i != stringArray.length - 1) {
            sb.append(",");
        }
    }
    return sb.toString();
  }

}
