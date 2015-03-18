// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommon;

import com.recursiveloop.webcommon.test.TestSuite;

import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;


@RunWith(Arquillian.class)
public class DataConnectionTest {

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "DataConnectionTest.war")
      .addPackage("com/recursiveloop/webcommon")
      .addPackage("com/recursiveloop/webcommon/test")
      .addPackage("com/recursiveloop/webcommon/annotations")
      .addAsResource("config.properties")
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
      .addAsWebInfResource("jboss-web.xml", "jboss-web.xml")
      .setWebXML("web.xml");
  }

  @Inject
  TestSuite m_testSuite;

  @Inject
  DataConnection m_db;

  @Test
  public void connection_ok() throws SQLException {
    System.out.println("**((CONNECTION_OK))**");

    Assert.assertTrue(m_db.isGood());
    m_testSuite.prepDB();
  }

  @Test
  public void create_table_dummy_values() throws SQLException {
    System.out.println("**((CREATE_TABLE_DUMMY_VALUES))**");

    Connection conn = m_db.getConnection();
    Statement st = conn.createStatement();

    st.executeUpdate("CREATE TABLE rl.dataconnectiontest (col1 TEXT, col2 INT)");
    st.executeUpdate("INSERT INTO rl.dataconnectiontest (col1, col2) VALUES ('this is string A', 234)");
    st.executeUpdate("INSERT INTO rl.dataconnectiontest (col1, col2) VALUES ('this is string B', 567)");
    st.executeUpdate("INSERT INTO rl.dataconnectiontest (col1, col2) VALUES ('this is string C', 567)");
    st.executeUpdate("INSERT INTO rl.dataconnectiontest (col1, col2) VALUES ('this is string D', 890)");

    conn.commit();

    ResultSet rs = st.executeQuery("SELECT col1, col2 FROM rl.dataconnectiontest WHERE col2 = 567");

    Assert.assertTrue(rs.next());
    Assert.assertEquals("this is string B", rs.getString("col1"));
    Assert.assertEquals(567, rs.getInt("col2"));
    Assert.assertTrue(rs.next());
    Assert.assertEquals("this is string C", rs.getString("col1"));
    Assert.assertEquals(567, rs.getInt("col2"));
    Assert.assertFalse(rs.next());

    rs.close();

    st.executeUpdate("DROP TABLE rl.dataconnectiontest");

    conn.commit();

    m_testSuite.prepDB();
  }
}
