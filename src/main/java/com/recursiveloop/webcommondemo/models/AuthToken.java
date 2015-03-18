// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommondemo.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;


@XmlAccessorType(XmlAccessType.FIELD)
public class AuthToken {
  @XmlElement(name="token")
  private String m_token = "";

  public AuthToken() {}

  public AuthToken(String token) {
    m_token = token;
  }

  public String getToken() {
    return m_token;
  }
}
