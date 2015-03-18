// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommondemo.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;


public class MyServlet extends HttpServlet {
  private static final Logger m_logger = Logger.getLogger(MyServlet.class.getName());

  public void init() {
    m_logger.log(Level.INFO, "Servlet initialised");
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    m_logger.log(Level.INFO, "Recieved GET request");
    response.getWriter().println("Hello from MyServlet!");
  }
}
