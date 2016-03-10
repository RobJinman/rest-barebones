package com.recursiveloop.webcommondemo;

import com.recursiveloop.webcommon.config.ConfigParam;
import com.recursiveloop.webcommon.Common;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;


@ApplicationScoped
public class Authentication {
  /**
   * @return An authentication token
   */
  public String authenticate(String username, String password) throws SQLException {
    String q = "{ ? = call " + m_schema + ".pwauthenticate(?, ?) }";

    try (
      Connection con = m_data.getConnection();
      CallableStatement cs = con.prepareCall(q);
    ) {
      cs.registerOutParameter(1, Types.BINARY);
      cs.setString(2, username);
      cs.setString(3, password);
      cs.execute();

      return Common.toHex(cs.getBytes(1));
    }
    catch (SQLException ex) {
      // If username or password is wrong
      if (ex.getSQLState() != null && (ex.getSQLState().equals("00001") || ex.getSQLState().equals("00002"))) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }

  /**
   * @return The role to which the user belongs, or the value of UNAUTHORISED_ROLE on authentication failure
   */
  public String getRole(String username, String token) throws SQLException {
    if (username == null || token == null) {
      return UNAUTHORISED_ROLE;
    }

    String q = "{ ? = call " + m_schema + ".tkAuthenticate(?, ?) }";

    try (
      Connection con = m_data.getConnection();
      CallableStatement cs = con.prepareCall(q);
    ) {
      cs.registerOutParameter (1, Types.BOOLEAN);
      cs.setString(2, username);
      cs.setBytes(3, Common.fromHex(token));
      cs.execute();

      // TODO
      return cs.getBoolean(1) ? "AUTHORISED_USER" : UNAUTHORISED_ROLE;
    }
  }

  private static final Logger m_logger = Logger.getLogger(Authentication.class.getName());
  private static final String UNAUTHORISED_ROLE = "UNAUTHORISED";

  @Resource(lookup="java:comp/env/jdbc/maindb")
  DataSource m_data;

  @Inject @ConfigParam(key="authSchema")
  String m_schema;
}
