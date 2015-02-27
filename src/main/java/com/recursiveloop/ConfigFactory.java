// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop;

import com.recursiveloop.annotations.Config;

import javax.inject.Named;
import javax.inject.Inject;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;


public class ConfigFactory {
  @Inject @Named("global")
  ApplicationSettings m_globals;

  @Produces
  @Config
  public String getString(InjectionPoint ip) {
    String full = ip.getMember().getDeclaringClass().getName();
    String member = ip.getMember().getName();
    String name = ip.getAnnotated().getAnnotation(Config.class).value();

    if (name.equals("[unassigned]")) {
      name = member;
    }

    String conf = m_globals.getString(full + "." + name);
    if (conf != null) {
      return conf;
    }

    String simple = ip.getMember().getDeclaringClass().getSimpleName();
    conf = m_globals.getString(simple + "." + name);
    if (conf != null) {
      return conf;
    }

    conf = m_globals.getString(name);
    if (conf != null) {
      return conf;
    }

    return null;
  }

  @Produces
  @Config
  public Number getNumber(InjectionPoint ip) {
    String full = ip.getMember().getDeclaringClass().getName();
    String member = ip.getMember().getName();
    String name = ip.getAnnotated().getAnnotation(Config.class).value();

    if (name.equals("[unassigned]")) {
      name = member;
    }

    Number conf = m_globals.getNumber(full + "." + name);
    if (conf != null) {
      return conf;
    }

    String simple = ip.getMember().getDeclaringClass().getSimpleName();
    conf = m_globals.getNumber(simple + "." + name);
    if (conf != null) {
      return conf;
    }

    conf = m_globals.getNumber(name);
    if (conf != null) {
      return conf;
    }

    return null;
  }
}
