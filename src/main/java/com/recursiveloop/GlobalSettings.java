// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import javax.inject.Named;
import java.util.logging.Logger;
import java.util.logging.Level;


@ApplicationScoped
@Named("global")
public class GlobalSettings implements ApplicationSettings {
  private static final Logger m_logger = Logger.getLogger(GlobalSettings.class.getName());

  private Properties m_properties = new Properties();

  // Lazily convert properties into their correct types upon request and store them here
  private HashMap<String, Object> m_conf = new HashMap<String, Object>();

  public GlobalSettings() {
    try {
      InputStream inputStream  = GlobalSettings.class.getClassLoader().getResourceAsStream("config.properties");
      m_properties.load(inputStream);
    }
    catch (IOException ex) {
      m_logger.log(Level.SEVERE, "Error loading config.properties", ex);
    }
  }

  @Override
  public String getString(String name, String defaultValue) {
    return m_properties.getProperty(name, defaultValue);
  }

  @Override
  public String getString(String name) {
    return getString(name, null);
  }

  /**
  * Parse all numbers into doubles to avoid precision loss
  */
  @Override
  public Number getNumber(String name, Number defaultValue) {
    Object val = m_conf.get(name);

    try {
      if (val != null) {
        return (Number)val;
      }
      else {
        String s = m_properties.getProperty(name);

        if (s != null) {
          Double d = new Double(s);
          m_conf.put(name, d);

          return d;
        }
        else {
          return defaultValue;
        }
      }
    }
    catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

  @Override
  public Number getNumber(String name) {
    return getNumber(name, null);
  }
}
