package com.recursiveloop.webcommondemo.resources;

import com.recursiveloop.webcommondemo.models.UserCredentials;
import com.recursiveloop.webcommondemo.exceptions.UnauthorisedException;
import com.recursiveloop.webcommon.config.ConfigParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.sql.DataSource;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.UUID;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Connection;
import java.sql.CallableStatement;


public class RcAccountImpl implements RcAccount {
  @Override
  public Response doPost(UserCredentials credentials)
    throws SQLException, UnauthorisedException {

    if (!credentials.areValid()) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    String q = "{ ? = call " + m_schema + ".confirmUser(?, ?, ?) }";

    try (
      Connection con = m_data.getConnection();
      CallableStatement cs = con.prepareCall(q);
    ) {
      cs.registerOutParameter (1, Types.OTHER);
      cs.setString(2, credentials.getUsername());
      cs.setString(3, credentials.getPassword());
      cs.setString(4, credentials.getActivationCode());
      cs.execute();

      UUID accountId = (UUID)cs.getObject(1);

      if (accountId == null) {
        throw new UnauthorisedException();
      }
    }
    catch (SQLException ex) {
      m_logger.log(Level.SEVERE, "Error confirming user", ex);
      throw ex;
    }

    return Response.ok().build();
  }

  private final static Logger m_logger = Logger.getLogger(RcAccountImpl.class.getName());

  @Inject @ConfigParam(key="authSchema")
  String m_schema;

  @Resource(lookup="java:comp/env/jdbc/maindb")
  DataSource m_data;
}
