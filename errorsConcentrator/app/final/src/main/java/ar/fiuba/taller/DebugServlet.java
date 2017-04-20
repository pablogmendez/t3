package ar.fiuba.taller;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;

import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;

import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.*;


public class DebugServlet extends HttpServlet {
   private static final Logger log = Logger.getLogger(DebugServlet.class.getName());


  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("POST recibido");

    Queue debugQueue = QueueFactory.getQueue("debug-queue");

    String task = req.getParameter("task");
    log.info("task: " + task);
    if(task.equals("functions")) {
      Query<Function> query= ObjectifyService.ofy().load().type(Function.class);
      String cursorStr = req.getParameter("cursor");
      if (cursorStr != null) {
        log.info("Listando funciones");
        query = query.startAt(Cursor.fromWebSafeString(cursorStr));
      }
      QueryResultIterator<Function> iterator = query.iterator();
      if(iterator.hasNext()) {
          Function function = iterator.next();
          log.info("Function: " + function.getId() + ", " + function.getName() + ", " + function.getHour());
          debugQueue.add(TaskOptions.Builder.withUrl("/debug")
          .param("task", "functions").param("cursor", iterator.getCursor().toWebSafeString()));
      } else {
        debugQueue.add(TaskOptions.Builder.withUrl("/debug")
          .param("task", "functionsCache"));
        log.info("--------------------------------------------------------------------------");
      }
    } else if (task.equals("functionsCache")) {
      Query<FunctionCache> query= ObjectifyService.ofy().load().type(FunctionCache.class);
      String cursorStr = req.getParameter("cursor");
      if (cursorStr != null) {
        log.info("Listando funciones de la cache");
        query = query.startAt(Cursor.fromWebSafeString(cursorStr));
      }
      QueryResultIterator<FunctionCache> iterator = query.iterator();
      if(iterator.hasNext()) {
          FunctionCache function = iterator.next();
          log.info("Function: " + function.getId() + ", " + function.getName() + ", " + function.getCount());
          debugQueue.add(TaskOptions.Builder.withUrl("/debug")
          .param("task", "functionsCache").param("cursor", iterator.getCursor().toWebSafeString()));
      } else {          
        debugQueue.add(TaskOptions.Builder.withUrl("/debug")
          .param("task", "msg"));
        log.info("--------------------------------------------------------------------------");
      }
    } else if (task.equals("msg")) {
      Query<AppMsgCount> query= ObjectifyService.ofy().load().type(AppMsgCount.class);
      String cursorStr = req.getParameter("cursor");
      if (cursorStr != null) {
        log.info("Listando mensajes");
        query = query.startAt(Cursor.fromWebSafeString(cursorStr));
      }
      QueryResultIterator<AppMsgCount> iterator = query.iterator();
      if(iterator.hasNext()) {
          AppMsgCount function = iterator.next();
          log.info("msg: " + function.getId() + ", " + function.getApplication() + ", " + function.getCount());
          debugQueue.add(TaskOptions.Builder.withUrl("/debug")
          .param("task", "msg").param("cursor", iterator.getCursor().toWebSafeString()));
      } else {          
        log.info("--------------------------------------------------------------------------");
      }
    } else if (task.equals("deleteFunctions")) {
      Function function = ObjectifyService.ofy().load().type(Function.class).first().now();
      if(function != null) {
        log.info("Eliminando: " + function.getName());
        ObjectifyService.ofy().delete().entity(function).now();
        debugQueue.add(TaskOptions.Builder.withUrl("/debug")
        .param("task", "deleteFunctions"));
      } else {
        debugQueue.add(TaskOptions.Builder.withUrl("/debug")
        .param("task", "deleteCache"));
        log.info("Termine de borrar funciones");
      }
    } else if (task.equals("deleteCache")) {
      FunctionCache function = ObjectifyService.ofy().load().type(FunctionCache.class).first().now();
      if(function != null) {
        log.info("Eliminando: " + function.getName());
        ObjectifyService.ofy().delete().entity(function).now();
        debugQueue.add(TaskOptions.Builder.withUrl("/debug")
        .param("task", "deleteCache"));
      } else {
        debugQueue.add(TaskOptions.Builder.withUrl("/debug")
        .param("task", "deleteMsg"));
        log.info("Termine de borrar cache");
      }
    } else if (task.equals("deleteMsg")) {
      AppMsgCount function = ObjectifyService.ofy().load().type(AppMsgCount.class).first().now();
      if(function != null) {
        log.info("Eliminando: " + function.getApplication());
        ObjectifyService.ofy().delete().entity(function).now();
        debugQueue.add(TaskOptions.Builder.withUrl("/debug")
        .param("task", "deleteMsg"));
      } else {
        log.info("Termine de borrar los mensajes");
      }
    } else { // AppMsgCount
      log.severe("Parametros invalidos");
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }

}
