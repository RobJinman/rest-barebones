package com.recursiveloop.webcommon.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class PropertiesConfigTest {
  private PropertiesConfig m_config;

  @Before
  public void before() {
    m_config = new PropertiesConfig();
    m_config.init();
  }

  @Test
  public void retrieveString() {
    String myString = m_config.get("myString");
    assertEquals(myString, "This is a test");
  }

  @Test
  public void retrieveInt() {
    int i = Double.valueOf(m_config.get("numberA")).intValue();
    assertEquals(i, 123);
  }

  @Test
  public void retrieveDouble() {
    double d = Double.valueOf(m_config.get("numberB")).doubleValue();
    assertEquals(d, 4.567, 0.005);
  }

  @Test
  public void retrieveNegativeFloat() {
    float f = Double.valueOf(m_config.get("numberC")).floatValue();
    assertEquals(f, -35.6, 0.005);
  }

  @Test
  public void retrieveNonExistent() {
    assertEquals(m_config.get("nonexistent"), null);
  }

  @After
  public void after() {}
}
