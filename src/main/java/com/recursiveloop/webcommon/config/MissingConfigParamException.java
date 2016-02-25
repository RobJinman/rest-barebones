package com.recursiveloop.webcommon.config;

import java.io.Serializable;


public class MissingConfigParamException extends Exception implements Serializable {
  private static final long serialVersionUID = 1L;

  public MissingConfigParamException() {
    super();
  }

  public MissingConfigParamException(String msg)   {
    super(msg);
  }

  public MissingConfigParamException(String msg, Exception e)  {
    super(msg, e);
  }
}
