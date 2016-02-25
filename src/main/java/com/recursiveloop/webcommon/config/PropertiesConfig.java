package com.recursiveloop.webcommon.config;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;
import java.io.Serializable;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.annotation.PostConstruct;


@ApplicationScoped
public class PropertiesConfig implements ApplicationConfig, Serializable {
  public static final int PROPERTIES_CONFIG_PRECEDENCE = 100;

  public PropertiesConfig() {}

  @PostConstruct
  public void init() {
    try {
      InputStream s  = PropertiesConfig.class.getClassLoader().getResourceAsStream("config.properties");

      if (s != null) {
        m_props = new Properties();
        m_props.load(s);
      }
    }
    catch (IOException ex) {
      m_logger.log(Level.SEVERE, "Error reading properties file", ex);
      m_props = null;
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
    return PROPERTIES_CONFIG_PRECEDENCE;
  }

  private static final Logger m_logger = Logger.getLogger(PropertiesConfig.class.getName());

  private Properties m_props;
}
