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
  private Connection m_autoConn;
  private Connection m_manualConn;
  private DataSource m_datasource;

  @Inject @Config("datasource")
  String m_dataSrcName;

  public Connection getConnection() {
    return getConnection(false);
  }

  public Connection getConnection(boolean autoCommit) {
    return autoCommit ? getAutoConn() : getManualConn();
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

      m_autoConn = null;
      m_manualConn = null;

      m_isGood = true;
    }
    catch (Exception e) {
      close();
      m_logger.log(Level.SEVERE, "Error connecting to data source", e);
    }
  }

  private Connection getAutoConn() {
    try {
      if (m_autoConn == null || m_autoConn.isClosed()) {
        m_autoConn = m_datasource.getConnection();
      }
    }
    catch (SQLException ex) {
      m_autoConn = null;
      m_logger.log(Level.SEVERE, "Error connecting to data source", ex);
    }

    return m_autoConn;
  }

  private Connection getManualConn() {
    try {
      if (m_manualConn == null || m_manualConn.isClosed()) {
        m_manualConn = m_datasource.getConnection();
        m_manualConn.setAutoCommit(false);
      }
    }
    catch (SQLException ex) {
      m_manualConn = null;
      m_logger.log(Level.SEVERE, "Error connecting to data source", ex);
    }

    return m_manualConn;
  }

  @PreDestroy
  private void close() {
    try {
      if (m_autoConn != null) {
        m_autoConn.close();
      }
    }
    catch (Exception e) {
      m_logger.log(Level.SEVERE, "Error closing data source connection", e);
    }

    try {
      if (m_manualConn != null) {
        m_manualConn.close();
      }
    }
    catch (Exception e) {
      m_logger.log(Level.SEVERE, "Error closing data source connection", e);
    }

    m_isGood = false;
  }
}
