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

public class FunctionsServlet extends HttpServlet {
   private static final Logger log = Logger.getLogger(FunctionsServlet.class.getName());

  // Process the http POST of the form
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("POST recibido");

    String date = req.getParameter("date");
    String description = req.getParameter("description");
    log.info("Parametros recibidos");
    log.info("date: " + date);
    log.info("description: " + description);

    resp.setStatus(HttpServletResponse.SC_OK);
  }
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      log.info("Hice un get");
      response(resp, "login ok");
  }


  private void response(HttpServletResponse resp, String msg)
      throws IOException {
    PrintWriter out = resp.getWriter();
    out.println("<html>");
    out.println("<body>");
    out.println("<t1>" + msg + "</t1>");
    out.println("</body>");
    out.println("</html>");
  }
}
//[END all]
