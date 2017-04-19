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

public class ErrorsConcentratorServlet extends HttpServlet {

   private static final Logger log = Logger.getLogger(ErrorsConcentratorServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Issue issue;
    Queue functionsQueue;
    Queue appMsgCountQueue;
    String regexPattern = "(\\.[^\\.]*\\Q(\\E)";
    String function;

    log.info("POST recibido");

    String username = req.getParameter("username");
    String application = req.getParameter("application");
    String summary = req.getParameter("summary");
    String os = req.getParameter("os");
    String description = req.getParameter("description");
    log.info("Parametros recibidos");
    log.info("username: " + username);
    log.info("application: " + application);
    log.info("summary: " + summary);
    log.info("os: " + os);
    log.info("description: " + description);

    log.info("Creando issue");
    issue = new Issue(username, application, summary, os, description);
    log.info("Hora de creacion del issue: " + issue.getDate());
    log.info("Persistiendo issue");
    ObjectifyService.ofy().save().entity(issue).now();

    log.info("Conectando con cola AppMsgCount");
    appMsgCountQueue = QueueFactory.getQueue("appmsgcount-queue");
    log.info("Agregando task a la cola de AppMsgCount");
    appMsgCountQueue.add(TaskOptions.Builder.withUrl("/appmsgcount").
      param("application", application));

    log.info("Conectando con cola functions");
    functionsQueue = QueueFactory.getQueue("functions-queue");

    log.info("Analizando el stacktrace");
    Pattern p = Pattern.compile(regexPattern);
    Matcher m = p.matcher(description);
    while(m.find()) {
      function = m.group(1).substring(1, m.group(1).length() - 1);
      log.info("Funcion encontrada: " + function);
      log.info("Agregando task a la cola de Funciones");
      functionsQueue.add(TaskOptions.Builder.withUrl("/functionscache").
        param("name", function));
      resp.getWriter().write(issue.getId().toString());
      resp.getWriter().flush();
      resp.getWriter().close();
      resp.setStatus(HttpServletResponse.SC_OK);
    }
    log.info("Servlet terminado");
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("GET recibido");

    String type = req.getParameter("type");
    String result = "{\"data\": [";

    if(type.equals("reports")) {
      log.info("Solicitud de reporte");
      getAppReport(req, resp);
    } else if (type.equals("functions")) {
      log.info("Solicitud de funciones");
      getFunctions(req, resp);
    } else {
      log.severe("Parametros invalidos");
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }

  private void getAppReport(HttpServletRequest req, HttpServletResponse resp) {
    String result = "{\"data\": [";

    Query<AppMsgCount> query = ObjectifyService.ofy().load()
    .type(AppMsgCount.class).limit(Constants.QUERY_LIMIT);
    String cursorStr = req.getParameter("cursor");
    if (cursorStr != null)
      query = query.startAt(Cursor.fromWebSafeString(cursorStr));
    QueryResultIterator<AppMsgCount> iterator = query.iterator();
    while (iterator.hasNext()) {
        AppMsgCount amc = iterator.next();
        log.info("amc: application = " + amc.getApplication() + ", count = "
          + amc.getCount().toString());
        result += "{\"application\" : \"" + amc.getApplication() + "\"," +
                  "\"count\" : " + amc.getCount().toString() + "},";
    }
    result = result.substring(0, result.length() - 1);
    result += "]";
    Cursor cursor = iterator.getCursor();
    log.info("Cursor actual " + cursor.toWebSafeString());
    result += ", \"cursor\" : \"" + cursor.toWebSafeString() + "\"}";
    try {
      resp.getWriter().write(result);
      resp.getWriter().flush();
      resp.getWriter().close();
    } catch (IOException e) {

    }
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private void getFunctions(HttpServletRequest req, HttpServletResponse resp) {
    String result = "{\"data\": [";
    String topFunctions = "";

    int hours = Integer.parseInt(req.getParameter("hours"));
    log.info("Consultando las funciones obtenidas hace " + hours + " horas");
    Query<Function> query = ObjectifyService.ofy().load()
     .type(Function.class).filter("hour ==", hours);
    
    QueryResultIterator<Function> iterator = query.iterator();
    while (iterator.hasNext()) {
      topFunctions += "\"" + iterator.next().getName() + "\",";
    }
    log.info("///" + topFunctions);
    if(topFunctions.length() > 0) {
      topFunctions = topFunctions.substring(0, topFunctions.length() - 1);
    } else {
      log.info("No se encontraron funciones");
    }
    result += topFunctions + "]}";
    log.info("Resultado: " + result);
    try {
      resp.getWriter().write(result);
      resp.getWriter().flush();
      resp.getWriter().close();
    } catch (IOException e) {

    }
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private List<String> sortHashMapByValues(Map<String, Long> map) {
    List<String> mapKeys = new ArrayList<String>(map.keySet());
    List<Long> mapValues = new ArrayList<Long>(map.values());
    Collections.sort(mapValues);
    Collections.sort(mapKeys);

    LinkedHashMap<String, Long> sortedMap =
        new LinkedHashMap<String, Long>();

    java.util.Iterator<Long> valueIt = mapValues.iterator();
    while (valueIt.hasNext()) {
      Long val = valueIt.next();
      java.util.Iterator<String> keyIt = mapKeys.iterator();

      while (keyIt.hasNext()) {
        String key = keyIt.next();
        Long comp1 = map.get(key);
        Long comp2 = val;

        if (comp1.equals(comp2)) {
          keyIt.remove();
          sortedMap.put(key, val);
          break;
        }
      }
    }
    Map<String, Long> map2 = sortedMap;
    List<String> tt = new ArrayList<String>();
    ArrayList<String> keys = new ArrayList<String>(sortedMap.keySet());
    int i = keys.size() - 1;
    int j = Constants.QUERY_LIMIT;
    while (i >= 0 && j > 0) {
      tt.add(keys.get(i));
      j--;
      i--;
    }
    return tt;
  }
}
