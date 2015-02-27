// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.models;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class RestrictedResource {
  private String m_hello = "";

  public RestrictedResource() {}

  public RestrictedResource(String thing) {
    m_hello = thing;
  }

  @XmlElement
  public String getHello() {
    return m_hello;
  }
}
