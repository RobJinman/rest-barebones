package com.recursiveloop.webcommondemo.exceptions;

import java.io.Serializable;


public class InternalServerException extends Exception implements Serializable {
  private static final long serialVersionUID = 1L;

  public InternalServerException() {
    super();
  }

  public InternalServerException(Exception e)  {
    super(e);
  }

  public InternalServerException(String msg)   {
    super(msg);
  }

  public InternalServerException(String msg, Exception e)  {
    super(msg, e);
  }
}
