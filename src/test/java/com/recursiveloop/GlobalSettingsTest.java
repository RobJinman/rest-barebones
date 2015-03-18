package com.recursiveloop.webcommon;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class GlobalSettingsTest {
  private ApplicationSettings m_settings;

  @Before
  public void before() {
    m_settings = new GlobalSettings();
  }

  @Test
  public void retrieveString() {
    String myString = m_settings.getString("myString");
    assertEquals(myString, "This is a test");
  }

  @Test
  public void retrieveInt() {
    int i = m_settings.getNumber("numberA").intValue();
    assertEquals(i, 123);
  }

  @Test
  public void retrieveDouble() {
    double d = m_settings.getNumber("numberB").doubleValue();
    assertEquals(d, 4.567, 0.005);
  }

  @Test
  public void retrieveNegativeFloat() {
    float f = m_settings.getNumber("numberC").floatValue();
    assertEquals(f, -35.6, 0.005);
  }

  @Test
  public void retrieveStringAsNumber() {
    int i = m_settings.getNumber("myString", 5).intValue();
    assertEquals(i, 5);
  }

  @Test
  public void retrieveNonExistent() {
    Number n = m_settings.getNumber("nonexistent");
    assertEquals(n, null);
  }

  @After
  public void after() {}
}
