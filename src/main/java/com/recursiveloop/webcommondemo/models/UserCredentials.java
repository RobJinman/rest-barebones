package com.recursiveloop.webcommondemo.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import java.util.regex.Pattern;


@XmlAccessorType(XmlAccessType.FIELD)
public class UserCredentials {
  private static final int MIN_PASSWORD_LEN = 8;
  private static final int MAX_PASSWORD_LEN = 32;
  private static final Pattern USERNAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{4,16}");

  @XmlElement(name = "username")
  private String m_username;

  @XmlElement(name = "password")
  private String m_password;

  // If it's a new account
  @XmlElement(name = "activationCode")
  private String m_activationCode;

  public UserCredentials() {
    m_username = "";
    m_password = "";
    m_activationCode = "";
  }

  public void setUsername(String username) {
    m_username = username;
  }

  public String getUsername() {
    return m_username;
  }

  public void setPassword(String password) {
    m_password = password;
  }

  public String getPassword() {
    return m_password;
  }

  public void setActivationCode(String code) {
    m_activationCode = code;
  }

  public String getActivationCode() {
    return m_activationCode;
  }

  public boolean areValid() {
    return m_username != null
      && USERNAME_PATTERN.matcher(m_username).matches()
      && m_password != null
      && m_password.length() >= MIN_PASSWORD_LEN
      && m_password.length() <= MAX_PASSWORD_LEN;
  }
}
