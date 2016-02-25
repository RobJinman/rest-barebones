package com.recursiveloop.webcommondemo;

import com.recursiveloop.webcommondemo.resources.RcAuthToken;
import com.recursiveloop.webcommondemo.models.UserCredentials;
import com.recursiveloop.webcommondemo.models.AuthToken;
import com.recursiveloop.webcommondemo.exceptions.InternalServerException;
import com.recursiveloop.webcommondemo.exceptions.UnauthorisedException;
import com.recursiveloop.webcommon.test.TestSuite;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.CallableStatement;
import java.util.UUID;
import java.net.URL;


@RunWith(Arquillian.class)
public class RcAuthTokenTest {

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "RcAuthTokenTest.war")
      .addPackage("com/recursiveloop/webcommon/test")
      .addPackage("com/recursiveloop/webcommon")
      .addPackage("com/recursiveloop/webcommon/config")
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

  @Resource(lookup="java:comp/env/jdbc/maindb")
  DataSource m_data;

  @Test
  @InSequence(1)
  public void connection_ok() {
    System.out.println("**((CONNECTION_OK))**");

    Assert.assertNotNull(m_data);
  }

  private static String m_email = "martha123@website.com";
  private static String m_username = "martha";
  private static String m_password = "pineapple";

  @Test
  @InSequence(2)
  public void setup() throws SQLException {
    System.out.println("**((SETUP))**");
    m_testSuite.prepDB();

    String q = "{ ? = call rl.registeruser(?) }";

    try (
      Connection con = m_data.getConnection();
    ) {
      String code = null;

      try (
        CallableStatement cs = con.prepareCall(q);
      ) {
        cs.registerOutParameter (1, Types.VARCHAR);
        cs.setString(2, m_email);
        cs.execute();

        code = cs.getString(1);
      }

      q = "{ ? = call rl.confirmUser(?, ?, ?) }";

      try (
        CallableStatement cs = con.prepareCall(q);
      ) {
        cs.registerOutParameter (1, Types.OTHER);
        cs.setString(2, m_username);
        cs.setString(3, m_password);
        cs.setString(4, code);
        cs.execute();

        UUID accountId = (UUID)cs.getObject(1);
      }
    }
  }

  @Test
  @InSequence(3)
  @RunAsClient
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_good_credentials(@ArquillianResteasyResource("rest") RcAuthToken rcAuthToken)
    throws SQLException, UnauthorisedException {

    System.out.println("**((WITH_GOOD_CREDENTIALS))**");

    UserCredentials credentials = new UserCredentials();
    credentials.setUsername("martha");
    credentials.setPassword("pineapple");

    AuthToken token = rcAuthToken.doPost(credentials);

    // Should return a 32 character string
    Assert.assertNotNull(token);
    Assert.assertTrue(token.getToken().matches("[0-9A-F]{32}"));
  }

  @Test
  @InSequence(4)
  @RunAsClient
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_bad_credentials(@ArquillianResteasyResource("rest") RcAuthToken rcAuthToken) {
    System.out.println("**((WITH_BAD_CREDENTIALS))**");

    UserCredentials credentials = new UserCredentials();
    credentials.setUsername("martha");
    credentials.setPassword("pinapple");

    try {
      rcAuthToken.doPost(credentials);
    }
    catch (WebApplicationException ex) {
      Response r = ex.getResponse();
      Assert.assertEquals(401, r.getStatus());
      return;
    }
    catch (Exception ex) {}

    Assert.assertTrue(false);
  }

  @Test
  @InSequence(5)
  public void tear_down() throws SQLException {
    System.out.println("**((TEAR_DOWN))**");
    m_testSuite.prepDB();
  }
}
