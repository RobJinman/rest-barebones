// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.resources;

import com.recursiveloop.models.UserCredentials;
import com.recursiveloop.models.AuthToken;
import com.recursiveloop.Authentication;
import com.recursiveloop.exceptions.InternalServerException;
import com.recursiveloop.exceptions.UnauthorisedException;

import javax.ws.rs.core.Response.Status;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.inject.Inject;


public class RcAuthTokenImpl implements RcAuthToken {
  private final static Logger m_logger = Logger.getLogger(RcAuthTokenImpl.class.getName());

  @Inject
  Authentication m_auth;

  @Override
  public AuthToken doPost(UserCredentials credentials)
    throws InternalServerException, UnauthorisedException {

    String token = null;

    try {
      token = m_auth.authenticate(credentials.getUsername(), credentials.getPassword());
    }
    catch (Exception ex) {
      String errMsg = "Error retrieving auth token";
      m_logger.log(Level.SEVERE, errMsg, ex);
      throw new InternalServerException(errMsg, ex);
    }

    if (token == null) {
      throw new UnauthorisedException();
    }
    else {
      return new AuthToken(token);
    }
  }
}
