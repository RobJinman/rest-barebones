// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.resources;

import java.util.logging.Logger;
import java.util.logging.Level;


public class RcUnrestrictedResourceImpl implements RcUnrestrictedResource {
  private static final Logger m_logger = Logger.getLogger(RcUnrestrictedResourceImpl.class.getName());

  @Override
  public String doGet() {
    return "Hello";
  }
}
