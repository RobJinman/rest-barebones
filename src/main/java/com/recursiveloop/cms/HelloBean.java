package com.recursiveloop.cms;


import javax.inject.Named;
import javax.enterprise.context.RequestScoped;


@Named(value = "helloBean")
@RequestScoped
public class HelloBean {
  private String m_name = "dear reader";

  public void setName(String name) {
    m_name = name;
  }

  public String getName() {
    return m_name;
  }
}
