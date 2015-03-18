// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommondemo;

import com.recursiveloop.webcommondemo.resources.RcRestrictedResource;
import com.recursiveloop.webcommondemo.models.RestrictedResource;
import com.recursiveloop.webcommondemo.exceptions.InternalServerException;
import com.recursiveloop.webcommondemo.exceptions.UnauthorisedException;
import com.recursiveloop.webcommon.test.StaticData;
import com.recursiveloop.webcommon.test.TestSuite;
import com.recursiveloop.webcommon.DataConnection;
import com.recursiveloop.webcommon.annotations.Config;
import com.recursiveloop.webcommon.Common;

import javax.inject.Inject;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.CallableStatement;
import java.util.UUID;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import java.io.IOException;


@RunWith(Arquillian.class)
public class RcRestrictedResourceTest {

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "RcRestrictedResourceTest.war")
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

    q = "{ ? = call rl.confirmUser(?, ?, ?) }";
    cs = m_data.getConnection().prepareCall(q);

    cs.registerOutParameter (1, Types.OTHER);
    cs.setString(2, m_username);
    cs.setString(3, m_password);
    cs.setString(4, code);
    cs.execute();

    UUID accountId = (UUID)cs.getObject(1);

    q = "{ ? = call rl.pwAuthenticate(?, ?) }";
    cs = m_data.getConnection().prepareCall(q);

    cs.registerOutParameter (1, Types.BINARY);
    cs.setString(2, m_username);
    cs.setString(3, m_password);
    cs.execute();

    byte[] bytes = cs.getBytes(1);
    String token = Common.toHex(bytes);

    StaticData.put("authToken", token);

    m_data.getConnection().commit();

    StaticData.persist();
  }

  @Test
  @InSequence(3)
  @RunAsClient
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_valid_token(@ArquillianResource URL base)
    throws InternalServerException, UnauthorisedException, Exception {

    StaticData.load();
    String token = StaticData.get("authToken");

    System.out.println("**((WITH_VALID_TOKEN))**");

    ClientRequest request = new ClientRequest(new URL(base, "rest/restrictedresource").toExternalForm());
    request.header("Accept", MediaType.APPLICATION_JSON);

    request.header("X-Username", m_username);
    request.header("X-Auth-Token", token);

    ClientResponse<RestrictedResource> response = request.get(RestrictedResource.class);
    RestrictedResource entity = response.getEntity();

    Assert.assertEquals(200, response.getStatus());
    Assert.assertNotNull(entity);
  }

  @Test
  @InSequence(4)
  @RunAsClient
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_invalid_token(@ArquillianResource URL base)
    throws InternalServerException, UnauthorisedException, Exception {

    System.out.println("**((WITH_INVALID_TOKEN))**");

    ClientRequest request = new ClientRequest(new URL(base, "rest/restrictedresource").toExternalForm());
    request.header("Accept", MediaType.APPLICATION_JSON);

    request.header("X-Username", m_username);
    request.header("X-Auth-Token", "DEADBEEF");

    ClientResponse<RestrictedResource> response = request.get(RestrictedResource.class);
    Assert.assertEquals(401, response.getStatus());
  }

  @Test
  @InSequence(5)
  @RunAsClient
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_bad_token_username_combo(@ArquillianResource URL base)
    throws InternalServerException, UnauthorisedException, Exception {

    StaticData.load();
    String token = StaticData.get("authToken");

    System.out.println("**((WITH_BAD_TOKEN_USERNAME_COMBO))**");

    ClientRequest request = new ClientRequest(new URL(base, "rest/restrictedresource").toExternalForm());
    request.header("Accept", MediaType.APPLICATION_JSON);

    request.header("X-Username", "badusername");
    request.header("X-Auth-Token", token);

    ClientResponse<RestrictedResource> response = request.get(RestrictedResource.class);
    Assert.assertEquals(401, response.getStatus());
  }

  @Test
  @InSequence(6)
  public void tear_down() throws SQLException {
    System.out.println("**((TEAR_DOWN))**");
    m_testSuite.prepDB();
  }
}
