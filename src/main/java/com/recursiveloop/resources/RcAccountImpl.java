// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.resources;

import com.recursiveloop.DataConnection;
import com.recursiveloop.models.UserCredentials;
import com.recursiveloop.annotations.Config;
import com.recursiveloop.exceptions.InternalServerException;
import com.recursiveloop.exceptions.UnauthorisedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.UnsupportedEncodingException;
import java.sql.Types;
import java.sql.CallableStatement;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import javax.inject.Inject;


public class RcAccountImpl implements RcAccount {
  private final static Logger m_logger = Logger.getLogger(RcAccountImpl.class.getName());

  @Inject
  DataConnection m_data;

  @Inject @Config("authSchema")
  String m_schema;

  @Override
  public Response doPost(UserCredentials credentials)
    throws InternalServerException, UnauthorisedException {

    if (!credentials.areValid()) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    if (m_data.isGood()) {
      try {
        String q = "{ ? = call " + m_schema + ".confirmUser(?, ?, ?) }";

        CallableStatement cs = m_data.getConnection().prepareCall(q);

        cs.registerOutParameter (1, Types.OTHER);
        cs.setString(2, credentials.getUsername());
        cs.setString(3, credentials.getPassword());
        cs.setString(4, credentials.getActivationCode());
        cs.execute();

        UUID accountId = (UUID)cs.getObject(1);

        m_data.getConnection().commit();

        if (accountId == null) {
          throw new UnauthorisedException();
        }
      }
      catch (SQLException ex) {
        m_logger.log(Level.SEVERE, "Error confirming user", ex);

        try {
          m_data.getConnection().rollback();
        }
        catch (SQLException ex2) {
          m_logger.log(Level.SEVERE, "Error performing rollback", ex2);
        }

        throw new InternalServerException("Error confirming user", ex);
      }
    }
    else {
      String errMsg = "Error confirming user; bad database connection";
      m_logger.log(Level.SEVERE, errMsg);
      throw new InternalServerException(errMsg);
    }

    return Response.ok().build();
  }
}
