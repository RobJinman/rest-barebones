// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop;


public interface ApplicationSettings {
  public String getString(String name);
  public String getString(String name, String defaultValue);
  public Number getNumber(String name);
  public Number getNumber(String name, Number defaultValue);
}
