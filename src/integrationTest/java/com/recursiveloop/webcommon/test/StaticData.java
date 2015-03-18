// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommon.test;

import java.util.Properties;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;


public class StaticData {
  private static Properties m_properties = new Properties();
  private static final String FILE = "build/tmp/integrationTest/tmp.properties";

  public static void put(String key, String value) {
    m_properties.setProperty(key, value);
  }

  public static String get(String key) {
    return m_properties.getProperty(key);
  }

  public static void clear() throws IOException {
    byte[] b = {};

    FileOutputStream out = new FileOutputStream(FILE);
    out.write(b, 0, 0);

    m_properties.clear();
  }

  public static void load() throws IOException {
    FileInputStream fs = new FileInputStream(FILE);
    m_properties.load(fs);
  }

  public static void persist() throws IOException {
    FileWriter fw = new FileWriter(FILE);
    m_properties.store(fw, "");
  }
}
