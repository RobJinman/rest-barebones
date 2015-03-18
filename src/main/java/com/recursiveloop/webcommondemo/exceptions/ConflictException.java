// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommondemo.exceptions;

import java.io.Serializable;


public class ConflictException extends Exception implements Serializable {
  private static final long serialVersionUID = 1L;

  public ConflictException() {
    super();
  }

  public ConflictException(String msg)   {
    super(msg);
  }

  public ConflictException(String msg, Exception e)  {
    super(msg, e);
  }
}
