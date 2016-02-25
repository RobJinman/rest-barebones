package com.recursiveloop.webcommondemo.resources;

import com.recursiveloop.webcommondemo.models.UserCredentials;
import com.recursiveloop.webcommondemo.models.AuthToken;
import com.recursiveloop.webcommondemo.Authentication;
import com.recursiveloop.webcommondemo.exceptions.UnauthorisedException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;
import javax.inject.Inject;


public class RcAuthTokenImpl implements RcAuthToken {
  private final static Logger m_logger = Logger.getLogger(RcAuthTokenImpl.class.getName());

  @Inject
  Authentication m_auth;

  @Override
  public AuthToken doPost(UserCredentials credentials)
    throws SQLException, UnauthorisedException {

    try {
      String token = m_auth.authenticate(credentials.getUsername(), credentials.getPassword());

      if (token == null) {
        throw new UnauthorisedException();
      }

      return new AuthToken(token);
    }
    catch (SQLException ex) {
      m_logger.log(Level.SEVERE, "Error retrieving auth token", ex);
      throw ex;
    }
  }
}
