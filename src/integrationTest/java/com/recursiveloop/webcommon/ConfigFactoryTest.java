// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommon;

import com.recursiveloop.webcommon.annotations.Config;

import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ConfigFactoryTest {

  @Deployment
  public static JavaArchive createDeployment() {
    JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "ConfigFactoryTest.jar")
      .addPackage("com/recursiveloop/webcommon")
      .addPackage("com/recursiveloop/webcommon/annotations")
      .addAsResource("config.properties")
      .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

    return archive;
  }

  @Inject @Config("stringA")
  String m_stringA;

  @Inject @Config("stringB")
  String m_stringB;

  @Inject @Config("stringC")
  String m_stringC;

  @Test
  public void inject_by_name() {
    System.out.println("**((INJECT_BY_NAME))**");

    Assert.assertEquals("This is string A", m_stringA);
    Assert.assertEquals("This is string B", m_stringB);
    Assert.assertEquals("This is string C", m_stringC);
  }

  @Inject @Config
  String stringA;

  @Inject @Config
  String stringB;

  @Inject @Config
  String stringC;

  @Test
  public void inject_by_member_name() {
    System.out.println("**((INJECT_BY_MEMBER_NAME))**");

    Assert.assertEquals("This is string A", stringA);
    Assert.assertEquals("This is string B", stringB);
    Assert.assertEquals("This is string C", stringC);
  }

  @Inject @Config("numberA")
  Number m_numberA;

  @Inject @Config
  Number numberB;

  @Test
  public void inject_numbers() {
    System.out.println("**((INJECT_MEMBERS))**");

    Assert.assertEquals(123, m_numberA.floatValue(), 0.1);
    Assert.assertEquals(-34.567, numberB.floatValue(), 0.0001);
  }
}
