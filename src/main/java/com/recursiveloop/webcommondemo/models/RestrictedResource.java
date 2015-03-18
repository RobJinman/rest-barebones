// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommondemo.models;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;


@XmlAccessorType(XmlAccessType.FIELD)
public class RestrictedResource {
  @XmlElement(name = "hello")
  private String m_hello = "";

  public RestrictedResource() {}

  public RestrictedResource(String thing) {
    m_hello = thing;
  }

  public String getHello() {
    return m_hello;
  }
}
