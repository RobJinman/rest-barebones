// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommondemo.resources;

import com.recursiveloop.webcommondemo.Authentication;
import com.recursiveloop.webcommondemo.models.RestrictedResource;
import com.recursiveloop.webcommondemo.exceptions.InternalServerException;
import com.recursiveloop.webcommondemo.exceptions.UnauthorisedException;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.MediaType;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Arrays;
import javax.inject.Inject;


public class RcRestrictedResourceImpl implements RcRestrictedResource {
  private static final Logger m_logger = Logger.getLogger(RcRestrictedResourceImpl.class.getName());
  private static final String[] m_roles = { "ADMINISTRATOR", "AUTHORISED_USER" };

  @Inject
  Authentication m_auth;

  @Override
  public RestrictedResource doGet(@HeaderParam("X-Auth-Token") String token, @HeaderParam("X-Username") String username)
    throws InternalServerException, UnauthorisedException {

    String role = null;

    try {
      role = m_auth.getRole(username, token);
    }
    catch (Exception ex) {
      String errMsg = "Error retrieving resource";
      m_logger.log(Level.SEVERE, errMsg, ex);
      throw new InternalServerException(errMsg, ex);
    }

    if (!Arrays.asList(m_roles).contains(role)) {
      throw new UnauthorisedException();
    }

    return new RestrictedResource("eggplant");
  }
}
