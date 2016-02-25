package com.recursiveloop.webcommon.test;

import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Statement;
import java.sql.SQLException;


@ApplicationScoped
public class TestSuite {
  @Resource(lookup="java:comp/env/jdbc/maindb")
  DataSource m_data;

  public void prepDB() throws SQLException {
    try (
      Statement st = m_data.getConnection().createStatement();
    ) {
      st.executeUpdate("DELETE FROM rl.account");
      st.executeUpdate("DELETE FROM rl.pending_account");
    }
  }
}
