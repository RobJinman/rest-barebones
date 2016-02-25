package com.recursiveloop.webcommon.config;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;


@ApplicationScoped
public class DatabaseConfig implements ApplicationConfig, Serializable {
  public static final int DATABASE_CONFIG_PRECEDENCE = 200;

  public DatabaseConfig() {}

  @PostConstruct
  public void init() {
    if (m_datasourceName != null && m_configTable != null) {
      try {
        DataSource ds = (DataSource)(new InitialContext()).lookup(m_datasourceName);

        try (
          Connection con = ds.getConnection();
          Statement smnt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
          ResultSet rs = smnt.executeQuery(String.format("SELECT %s, %s FROM %s", m_keyColumn, m_valueColumn, m_configTable));
        ) {
          m_props = new Properties();

          while (rs.next()) {
            m_props.put(rs.getString(m_keyColumn), rs.getString(m_valueColumn));
          }
        }
      }
      catch (SQLException | NamingException ex) {
        m_logger.log(Level.SEVERE, "Error retrieving configuration from database", ex);
      }
    }
  }

  @Override
  public String get(String key) {
    if (m_props == null) {
      return null;
    }

    return m_props.getProperty(key);
  }

  @Override
  public int precedence() {
    return DATABASE_CONFIG_PRECEDENCE;
  }

  private static final Logger m_logger = Logger.getLogger(DatabaseConfig.class.getName());

  @Resource(lookup="java:global/configDatabase")
  String m_datasourceName;

  @Resource(lookup="java:global/configTable")
  String m_configTable;

  @Resource(lookup="java:global/configKeyColumn")
  String m_keyColumn;

  @Resource(lookup="java:global/configValueColumn")
  String m_valueColumn;

  private Properties m_props;
}
