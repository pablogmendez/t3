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

import com.googlecode.objectify.ObjectifyService;

/**
 * Form Handling Servlet
 * Most of the action for this sample is in webapp/guestbook.jsp, which displays the
 * {@link Greeting}'s. This servlet has one method
 * {@link #doPost(<#HttpServletRequest req#>, <#HttpServletResponse resp#>)} which takes the form
 * data and saves it.
 */
public class AppMsgCountServlet extends HttpServlet {

   private static final Logger log = Logger.getLogger(AppMsgCountServlet.class.getName());

  // Process the http POST of the form
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    AppMsgCount appMsgCount;

    log.info("POST recibido");

    String application = req.getParameter("application");
    log.info("application: " + application);

    log.info("Intentando cargar entidad");    
    appMsgCount = ObjectifyService.ofy().load().type(AppMsgCount.class).
    filter("application", application).first().now();

    if(appMsgCount == null) {
      log.info("Entidad inexistente. Creando una nueva.");    
      appMsgCount = new AppMsgCount(application);
    } else {
      log.info("Entidad existente. Incrementando contador.");    
      appMsgCount.incCount();
    }
    log.info("Persistiendo entidad.");    
    ObjectifyService.ofy().save().entity(appMsgCount).now();
    log.info("Servlet finalizado.");    
  }
}
//[END all]
