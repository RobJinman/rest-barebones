package com.recursiveloop.webcommondemo.resources;

import java.util.logging.Logger;
import java.util.logging.Level;


public class RcUnrestrictedResourceImpl implements RcUnrestrictedResource {
  private static final Logger m_logger = Logger.getLogger(RcUnrestrictedResourceImpl.class.getName());

  @Override
  public String doGet() {
    return "Hello";
  }
}
