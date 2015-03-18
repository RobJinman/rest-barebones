// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommondemo;

import com.recursiveloop.webcommondemo.resources.RcPendingAccount;
import com.recursiveloop.webcommondemo.models.UserEmail;
import com.recursiveloop.webcommondemo.exceptions.InternalServerException;
import com.recursiveloop.webcommondemo.exceptions.ConflictException;
import com.recursiveloop.webcommondemo.exceptions.BadRequestException;
import com.recursiveloop.webcommon.test.TestSuite;
import com.recursiveloop.webcommon.test.StaticData;
import com.recursiveloop.webcommon.test.mocks.MailerProducer;
import com.recursiveloop.webcommon.DataConnection;

import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import java.net.URL;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.CallableStatement;
import java.util.UUID;
import java.io.IOException;


@RunWith(Arquillian.class)
public class RcPendingAccountTest {
  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "RcPendingAccountTest.war")
      .addPackage("com/recursiveloop/webcommon/test")
      .addPackage("com/recursiveloop/webcommon/test/mocks")
      .addPackage("com/recursiveloop/webcommon")
      .addPackage("com/recursiveloop/webcommon/annotations")
      .addPackage("com/recursiveloop/webcommondemo")
      .addPackage("com/recursiveloop/webcommondemo/models")
      .addPackage("com/recursiveloop/webcommondemo/resources")
      .addPackage("com/recursiveloop/webcommondemo/exceptions")
      .addClass(MailerProducer.class)
      .addAsResource("config.properties")
      .addAsWebInfResource(new StringAsset(
        "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"" +
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
        "  xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee" +
        "  http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\"" +
        "  bean-discovery-mode=\"all\">" +
        "    <alternatives>" +
        "      <class>com.recursiveloop.webcommon.test.mocks.MailerProducer</class>" +
        "    </alternatives>" +
        "</beans>"), "beans.xml")
      .addAsLibraries(Maven.resolver().resolve("org.mockito:mockito-core:1.10.19").withTransitivity().as(GenericArchive.class))
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

  @Test
  @InSequence(2)
  public void setup() throws SQLException, IOException {
    System.out.println("**((SETUP))**");
    m_testSuite.prepDB();

    Statement st = m_data.getConnection().createStatement();
    st.executeUpdate("INSERT INTO rl.account (account_id, email, username, hash) VALUES (UUID_GENERATE_V4(), 'alreadytaken@website.com', 'joebloggs', 'abc')");

    m_data.getConnection().commit();
  }

  @Test
  @InSequence(3)
  @RunAsClient
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_taken_email_1(@ArquillianResteasyResource("rest") RcPendingAccount rcPendingAccount)
    throws InternalServerException, ConflictException, BadRequestException {

    System.out.println("**((WITH_TAKEN_EMAIL))**");

    UserEmail email = new UserEmail();
    email.setEmail("alreadytaken@website.com");

    Response r = rcPendingAccount.doPost(email);
    Assert.assertEquals(409, r.getStatus());
  }

  @Test
  @InSequence(4)
  public void with_taken_email_2() throws SQLException, IOException {
    Statement st = m_data.getConnection().createStatement();
    ResultSet rs = st.executeQuery("SELECT code, email FROM rl.pending_account WHERE email = 'alreadytaken@website.com'");
    Assert.assertFalse(rs.next());
    rs.close();
  }

  @Test
  @InSequence(5)
  @RunAsClient
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_good_email_1(@ArquillianResteasyResource("rest") RcPendingAccount rcPendingAccount)
    throws InternalServerException, ConflictException, BadRequestException {

    System.out.println("**((WITH_GOOD_EMAIL))**");

    UserEmail email = new UserEmail();
    email.setEmail("some_email@website.com");

    Response r = rcPendingAccount.doPost(email);

    Assert.assertEquals(200, r.getStatus());
  }

  @Test
  @InSequence(6)
  public void with_good_email_2() throws SQLException, IOException {
    Statement st = m_data.getConnection().createStatement();
    ResultSet rs = st.executeQuery("SELECT code, email FROM rl.pending_account WHERE email = 'some_email@website.com'");

    Assert.assertTrue(rs.next());
    Assert.assertEquals("some_email@website.com", rs.getString("email"));
    Assert.assertNotNull(rs.getString("code"));
    Assert.assertFalse(rs.next());
    rs.close();

    rs = st.executeQuery("SELECT email, username FROM rl.account WHERE email = 'some_email@website.com'");
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
