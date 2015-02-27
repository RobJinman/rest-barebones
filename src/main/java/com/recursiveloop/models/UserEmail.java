// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class UserEmail {
  private String m_email;

  public UserEmail() {
    m_email = "";
  }

  public UserEmail(String email) {
    m_email = email;
  }

  public void setEmail(String email) {
    m_email = email;
  }

  @XmlElement
  public String getEmail() {
    return m_email;
  }
}
