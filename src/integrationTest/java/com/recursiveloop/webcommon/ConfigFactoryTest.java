package com.recursiveloop.webcommon;

import com.recursiveloop.webcommon.config.ConfigParam;
import com.recursiveloop.webcommon.test.TestSuite;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import javax.inject.Inject;
import javax.annotation.Resource;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.SQLException;
import java.sql.Statement;


@RunWith(Arquillian.class)
public class ConfigFactoryTest {
  @Deployment
  public static WebArchive createDeployment() throws NamingException, SQLException {
    return ShrinkWrap.create(WebArchive.class, "ConfigFactoryTest.war")
      .addPackage("com/recursiveloop/webcommon/test")
      .addPackage("com/recursiveloop/webcommon")
      .addPackage("com/recursiveloop/webcommon/config")
      .addAsResource(new StringAsset(
        "param2 = This is param2 from config.properties\n" +
        "param3 = This is param3 from config.properties\n" +
        "com.recursiveloop.webcommon.ConfigFactoryTest.param4 = This is param4 from config.properties\n" +
        "ConfigFactoryTest.param5 = This is param5 from config.properties\n"), "config.properties")
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
      .addAsWebInfResource("jboss-web.xml", "jboss-web.xml")
      .setWebXML("web.xml");
  }

  @Resource(lookup="java:comp/env/jdbc/maindb")
  DataSource m_data;

  // param1 is in database only
  // param2 is in properties file only
  // param3 is in both the database and the properties file

  @Inject @ConfigParam(key="param2")
  String m_param2a;

  @Test
  public void props_inject_by_key() {
    System.out.println("**((INJECT BY KEY FROM PROPERTIES FILE))**");
    Assert.assertEquals("This is param2 from config.properties", m_param2a);
  }

  @Inject @ConfigParam(key="param2", defaultValue="Default value")
  String m_param2b;

  @Test
  public void props_inject_by_key_with_default() {
    System.out.println("**((INJECT BY KEY FROM PROPERTIES FILE WITH DEFAULT VALUE))**");
    Assert.assertEquals("This is param2 from config.properties", m_param2b);
  }

  @Inject @ConfigParam
  String param2;

  @Test
  public void props_inject_by_member_name() {
    System.out.println("**((INJECT MEMBER FROM PROPERTIES FILE))**");
    Assert.assertEquals("This is param2 from config.properties", param2);
  }

  @Inject @ConfigParam(key="param4")
  String m_param4;

  @Test
  public void props_inject_by_full_name() {
    System.out.println("**((INJECT FULL.PATH.TO.PARAM FROM PROPERTIES FILE))**");
    Assert.assertEquals("This is param4 from config.properties", m_param4);
  }

  @Inject @ConfigParam(key="param5")
  String m_param5;

  @Test
  public void props_inject_by_class_member() {
    System.out.println("**((INJECT CLASS.MEMBER FROM PROPERTIES FILE))**");
    Assert.assertEquals("This is param5 from config.properties", m_param5);
  }

  @Inject @ConfigParam(key="param1")
  String m_param1;

  @Test
  public void db_inject_by_key() {
    System.out.println("**((INJECT BY KEY FROM DATABASE))**");
    Assert.assertEquals("This is param1 from the database", m_param1);
  }

  @Inject @ConfigParam
  String param1;

  @Test
  public void db_inject_by_member_name() {
    System.out.println("**((INJECT MEMBER FROM DATABASE))**");
    Assert.assertEquals("This is param1 from the database", param1);
  }

  @Inject @ConfigParam(key="param6")
  String m_param6;

  @Test
  public void db_inject_by_full_name() {
    System.out.println("**((INJECT FULL.PATH.TO.PARAM FROM DATABASE))**");
    Assert.assertEquals("This is param6 from the database", m_param6);
  }

  @Inject @ConfigParam(key="param7")
  String m_param7;

  @Test
  public void db_inject_by_class_member() {
    System.out.println("**((INJECT CLASS.MEMBER FROM PROPERTIES FILE))**");
    Assert.assertEquals("This is param7 from the database", m_param7);
  }

  @Inject @ConfigParam(key="param3")
  String m_param3;

  @Test
  public void test_precedence() {
    System.out.println("**((TEST PRECEDENCE))**");
    Assert.assertEquals("This is param3 from the database", m_param3);
  }

  @Inject @ConfigParam(key="nonexistent1", defaultValue="This is the default value")
  String m_nonexistent1;

  @Test
  public void test_default_value() {
    System.out.println("***((TEST DEFAULT VALUE))***");
    Assert.assertEquals("This is the default value", m_nonexistent1);
  }
}
