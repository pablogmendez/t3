/**
 * Copyright 2014-2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//[START all]
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

/**
 * Form Handling Servlet
 * Most of the action for this sample is in webapp/guestbook.jsp, which displays the
 * {@link Greeting}'s. This servlet has one method
 * {@link #doPost(<#HttpServletRequest req#>, <#HttpServletResponse resp#>)} which takes the form
 * data and saves it.
 */
public class ErrorsConcentratorServlet extends HttpServlet {

   private static final Logger log = Logger.getLogger(ErrorsConcentratorServlet.class.getName());

  // Process the http POST of the form
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    //Greeting greeting;
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
    m.find();
    function = m.group(1).substring(1, m.group(1).length() - 1);
    log.info("Funcion encontrada: " + function);
    try {
      log.info("Agregando task a la cola de Funciones");
      functionsQueue.add(TaskOptions.Builder.withUrl("/functions").
        param("date", issue.getStringDate()).param("function", function));
      resp.getWriter().write(issue.getId().toString());
      resp.getWriter().flush();
      resp.getWriter().close();
      resp.setStatus(HttpServletResponse.SC_OK);
    } catch (ParseException e) {
      e.printStackTrace();
      log.severe("Error al agregar la funcion a la cola de funciones");
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
    log.info("Servlet terminado");
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("GET recibido");

    String type = req.getParameter("type");
    String result = "{\"data\": [";

    if(type.equals("reports")) {
      log.info("Solicitud de reporte");      
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
      resp.getWriter().write(result);
      resp.getWriter().flush();
      resp.getWriter().close();
      resp.setStatus(HttpServletResponse.SC_OK);
    } else if (type.equals("functions")) {
      log.info("Solicitud de funciones");      
      Map<String, Long> map = new HashMap<String, Long>();
      int hours = Integer.parseInt(req.getParameter("hours"));
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.HOUR_OF_DAY, -hours);
      Date date = calendar.getTime();
      log.info("Consultando las funciones desde " + date.toString());      
      Query<Function> query = ObjectifyService.ofy().load()
      .type(Function.class).filter("date >=", date)
      .limit(Constants.QUERY_LIMIT);
            QueryResultIterator<Function> iterator = query.iterator();
      log.info("Cargando el map");      
      while (iterator.hasNext()) {
        Function function = iterator.next();
        if(!map.containsKey(function.getFunction())) {
          map.put(function.getFunction(), function.getCount());
        } else {
          map.put(function.getFunction(), map.get(function.getFunction()) + 1);
        }
      }
      List<String> topFunctions = sortHashMapByValues(map);
      for(String reg : topFunctions) {
        result += "\"" + reg + "\",";
      }
      if(topFunctions.size() > 0) {
        result = result.substring(0, result.length() - 1);
      }
      result += "]}";
      resp.getWriter().write(result);
      resp.getWriter().flush();
      resp.getWriter().close();
      resp.setStatus(HttpServletResponse.SC_OK);
    } else {
      log.severe("Parametros invalidos");
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN); 
    }
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
//[END all]
