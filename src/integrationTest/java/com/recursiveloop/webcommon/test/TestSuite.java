// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommon.test;

import com.recursiveloop.webcommon.DataConnection;

import java.sql.Statement;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class TestSuite {
  @Inject
  DataConnection m_data;

  public void prepDB() throws SQLException {
    Statement st = m_data.getConnection().createStatement();
    st.executeUpdate("DELETE FROM rl.account; DELETE FROM rl.pending_account");

    m_data.getConnection().commit();
  }
}
