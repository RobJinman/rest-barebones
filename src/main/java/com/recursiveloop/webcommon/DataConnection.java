// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommon;

import com.recursiveloop.webcommon.annotations.Config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.logging.Logger;
import java.util.logging.Level;


/**
* Wraps the JDBC connection. It expects to find the 'datasource' name in global settings.
*/
@ApplicationScoped
public class DataConnection {
  private static final Logger m_logger = Logger.getLogger(DataConnection.class.getName());

  private boolean m_isGood = false;
  private Connection m_connection;
  private DataSource m_datasource;

  @Inject @Config("datasource")
  String m_dataSrcName;

  public Connection getConnection() {
    try {
      if (m_connection == null || m_connection.isClosed()) {
        m_connection = newConnection();
      }
    }
    catch (SQLException ex) {
      m_connection = null;
      m_logger.log(Level.SEVERE, "Error connecting to data source", ex);
    }

    return m_connection;
  }

  public boolean isGood() {
    return m_isGood;
  }

  @PostConstruct
  private void open() {
    try {
      Context init = new InitialContext();
      Context context = (Context)init.lookup("java:comp/env");

      m_datasource = (DataSource)context.lookup(m_dataSrcName);

      m_connection = newConnection();

      m_isGood = true;
    }
    catch (Exception e) {
      close();
      m_logger.log(Level.SEVERE, "Error connecting to data source", e);
    }
  }

  private Connection newConnection() throws SQLException {
    Connection c = m_datasource.getConnection();
    c.setAutoCommit(false);

    return c;
  }

  @PreDestroy
  private void close() {
    try {
      if (m_connection != null) {
        m_connection.close();
      }
    }
    catch (Exception e) {
      m_logger.log(Level.SEVERE, "Error closing data source connection", e);
    }

    m_isGood = false;
  }
}
