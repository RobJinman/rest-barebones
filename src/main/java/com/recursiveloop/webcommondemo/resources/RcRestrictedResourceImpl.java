package com.recursiveloop.webcommondemo.resources;

import com.recursiveloop.webcommondemo.Authentication;
import com.recursiveloop.webcommondemo.models.RestrictedResource;
import com.recursiveloop.webcommondemo.exceptions.UnauthorisedException;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.MediaType;
import javax.servlet.http.HttpServletRequest;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Arrays;


public class RcRestrictedResourceImpl implements RcRestrictedResource {
  private static final Logger m_logger = Logger.getLogger(RcRestrictedResourceImpl.class.getName());
  private static final String[] m_roles = { "ADMINISTRATOR", "AUTHORISED_USER" };

  @Inject
  Authentication m_auth;

  @Override
  public RestrictedResource doGet(@HeaderParam("X-Auth-Token") String token, @HeaderParam("X-Username") String username)
    throws SQLException, UnauthorisedException {

    String role = null;

    try {
      role = m_auth.getRole(username, token);

      if (!Arrays.asList(m_roles).contains(role)) {
        throw new UnauthorisedException();
      }
    }
    catch (SQLException ex) {
      m_logger.log(Level.SEVERE, "Error retrieving resource");
      throw ex;
    }

    return new RestrictedResource("eggplant");
  }
}
