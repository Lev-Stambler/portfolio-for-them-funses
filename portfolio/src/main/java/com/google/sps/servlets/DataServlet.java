// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    ArrayList<String> comments = new ArrayList<String>();
    int maxComments = Integer.parseInt(getParameter(request, "maxComments", "10"));

    // Get the comments from the datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment");
    PreparedQuery results = datastore.prepare(query);
    
    // extract only the max number of comments into an ArrayList
    for (Entity ent : results.asIterable()) {
      String comment = (String) ent.getProperty("body");
      comments.add(comment);
      if (comments.size() >= maxComments)
        break;
    }
    String jsonStr = gson.toJson(comments);
    response.setContentType("application/json");
    response.getWriter().println(jsonStr);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String comment = getParameter(request, "comment", "");
    boolean shouldAddComment = Boolean.parseBoolean(getParameter(request, "shouldAddComment", "false"));
    
    // Only add the comment if the add option is indicated
    if (shouldAddComment) {
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("body", comment);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
    }
    response.sendRedirect("/");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
