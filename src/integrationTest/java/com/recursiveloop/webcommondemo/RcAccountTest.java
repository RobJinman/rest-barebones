// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommondemo;

import com.recursiveloop.webcommondemo.resources.RcAccount;
import com.recursiveloop.webcommondemo.models.UserCredentials;
import com.recursiveloop.webcommondemo.exceptions.InternalServerException;
import com.recursiveloop.webcommondemo.exceptions.UnauthorisedException;
import com.recursiveloop.webcommon.test.TestSuite;
import com.recursiveloop.webcommon.test.StaticData;
import com.recursiveloop.webcommon.DataConnection;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import java.net.URL;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.NotAuthorizedException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.CallableStatement;
import java.util.UUID;
import java.io.IOException;


@RunWith(Arquillian.class)
public class RcAccountTest {

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "RcAccountTest.war")
      .addPackage("com/recursiveloop/webcommon/test")
      .addPackage("com/recursiveloop/webcommon")
      .addPackage("com/recursiveloop/webcommon/annotations")
      .addPackage("com/recursiveloop/webcommondemo")
      .addPackage("com/recursiveloop/webcommondemo/models")
      .addPackage("com/recursiveloop/webcommondemo/resources")
      .addPackage("com/recursiveloop/webcommondemo/exceptions")
      .addAsResource("config.properties")
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
      .addAsWebInfResource("jboss-web.xml", "jboss-web.xml")
      .setWebXML("web.xml");
  }

  @Inject
  TestSuite m_testSuite;

  @Inject
  DataConnection m_data;

  @Test
  @InSequence(1)
  public void connection_ok() {
    System.out.println("**((CONNECTION_OK))**");

    Assert.assertTrue(m_data.isGood());
  }

  private static String m_email = "martha123@website.com";
  private static String m_username = "martha";
  private static String m_password = "pineapple";

  @Test
  @InSequence(2)
  public void setup() throws SQLException, IOException {
    System.out.println("**((SETUP))**");
    StaticData.clear();
    m_testSuite.prepDB();

    String q = "{ ? = call rl.registeruser(?) }";
    CallableStatement cs = m_data.getConnection().prepareCall(q);

    cs.registerOutParameter (1, Types.VARCHAR);
    cs.setString(2, m_email);
    cs.execute();

    String code = cs.getString(1);
    StaticData.put("code", code);
    m_data.getConnection().commit();

    Statement st = m_data.getConnection().createStatement();
    ResultSet rs = st.executeQuery("SELECT code, email FROM rl.pending_account WHERE email = '" + m_email + "'");

    Assert.assertTrue(rs.next());
    Assert.assertEquals(m_email, rs.getString("email"));
    Assert.assertEquals(code, rs.getString("code"));
    Assert.assertFalse(rs.next());
    rs.close();

    rs = st.executeQuery("SELECT email, username FROM rl.account WHERE email = '" + m_email + "'");
    Assert.assertFalse(rs.next());
    rs.close();

    StaticData.persist();
  }

  @Test
  @InSequence(3)
  @RunAsClient
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_incorrect_code_1(@ArquillianResteasyResource("rest") RcAccount rcAccount)
    throws InternalServerException, UnauthorisedException {

    System.out.println("**((WITH_INCORRECT_CODE))**");

    UserCredentials credentials = new UserCredentials();
    credentials.setUsername(m_username);
    credentials.setPassword(m_password);
    credentials.setActivationCode("notthecorrectcode");

    rcAccount.doPost(credentials);
  }

  @Test
  @InSequence(4)
  public void with_incorrect_code_2() throws SQLException, IOException {
    StaticData.load();
    String code = StaticData.get("code");

    Statement st = m_data.getConnection().createStatement();
    ResultSet rs = st.executeQuery("SELECT code, email FROM rl.pending_account WHERE email = '" + m_email + "'");

    Assert.assertTrue(rs.next());
    Assert.assertEquals(m_email, rs.getString("email"));
    Assert.assertEquals(code, rs.getString("code"));
    Assert.assertFalse(rs.next());
    rs.close();

    rs = st.executeQuery("SELECT email, username FROM rl.account WHERE email = '" + m_email + "'");

    Assert.assertFalse(rs.next());
    rs.close();
  }

  @Test
  @InSequence(5)
  @RunAsClient
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_correct_code_1(@ArquillianResteasyResource("rest") RcAccount rcAccount)
    throws InternalServerException, UnauthorisedException, IOException {

    System.out.println("**((WITH_CORRECT_CODE))**");

    StaticData.load();
    String code = StaticData.get("code");

    UserCredentials credentials = new UserCredentials();
    credentials.setUsername("martha");
    credentials.setPassword("pineapple");
    credentials.setActivationCode(code);

    Response r = rcAccount.doPost(credentials);
    Assert.assertEquals(200, r.getStatus());
  }

  @Test
  @InSequence(6)
  public void with_correct_code_2() throws SQLException {
    Statement st = m_data.getConnection().createStatement();
    ResultSet rs = st.executeQuery("SELECT code, email FROM rl.pending_account WHERE email = '" + m_email + "'");

    Assert.assertFalse(rs.next());
    rs.close();

    rs = st.executeQuery("SELECT email, username FROM rl.account WHERE email = '" + m_email + "'");

    Assert.assertTrue(rs.next());
    Assert.assertEquals(m_email, rs.getString("email"));
    Assert.assertEquals(m_username, rs.getString("username"));
    Assert.assertFalse(rs.next());

    rs.close();
  }

  @Test
  @InSequence(7)
  public void tear_down() throws SQLException {
    System.out.println("**((TEAR_DOWN))**");
    m_testSuite.prepDB();
  }
}
