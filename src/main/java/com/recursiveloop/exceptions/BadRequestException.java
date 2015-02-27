// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.exceptions;

import java.io.Serializable;


public class BadRequestException extends Exception implements Serializable {
  private static final long serialVersionUID = 1L;

  public BadRequestException() {
    super();
  }

  public BadRequestException(String msg)   {
    super(msg);
  }

  public BadRequestException(String msg, Exception e)  {
    super(msg, e);
  }
}
