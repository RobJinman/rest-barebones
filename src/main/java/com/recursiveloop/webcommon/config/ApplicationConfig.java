package com.recursiveloop.webcommon.config;


/**
 * A store of global configuration parameters
 */
public interface ApplicationConfig {
  /**
   * Retrieve the value of parameter 'key'
   */
  public String get(String key);

  /**
   * A number denoting the precedence of this configuration. For example,
   * a database configuration might have a precedence of 1, whereas a
   * properties configuration might have a precedence of 0. In this case,
   * a parameter from the database will override the same parameter in
   * the properties file.
   */
  public int precedence();
}
