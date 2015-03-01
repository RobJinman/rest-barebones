package com.recursiveloop;

import com.recursiveloop.annotations.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;


@ApplicationScoped
public class Authentication {
  private static final Logger m_logger = Logger.getLogger(Authentication.class.getName());
  private static final String UNAUTHORISED_ROLE = "UNAUTHORISED";

  @Inject
  DataConnection m_data;

  @Inject @Config("authSchema")
  String m_schema;

  /**
  * @Return An authentication token
  */
  public String authenticate(String username, String password)
    throws SQLException, Exception {

    String token = "";

    if (m_data.isGood()) {
      try {
        String q = "{ ? = call " + m_schema + ".pwauthenticate(?, ?) }";

        CallableStatement cs = m_data.getConnection().prepareCall(q);

        cs.registerOutParameter(1, Types.BINARY);
        cs.setString(2, username);
        cs.setString(3, password);
        cs.execute();

        m_data.getConnection().commit();

        token = Common.toHex(cs.getBytes(1));
      }
      catch (SQLException ex) {
        try {
          m_data.getConnection().rollback();
        }
        catch (SQLException ex2) {
          m_logger.log(Level.SEVERE, "Error performing rollback", ex2);
        }

        // If username or password is wrong
        if (ex.getSQLState() != null && (ex.getSQLState().equals("00001") || ex.getSQLState().equals("00002"))) {
          return null;
        }
        else {
          m_logger.log(Level.SEVERE, "Error", ex);
          throw ex;
        }
      }
    }
    else {
      m_logger.log(Level.SEVERE, "Error authenticating user; bad database connection");
      throw new Exception("Error authenticating user; bad database connection");
    }

    return token;
  }

  /**
  * @Returns The role to which the user belongs, or the value of UNAUTHORISED_ROLE on authentication failure
  */
  public String getRole(String username, String token)
    throws SQLException, Exception {

    if (username == null || token == null) {
      return UNAUTHORISED_ROLE;
    }

    if (m_data.isGood()) {
      try {
        String q = "{ ? = call " + m_schema + ".tkAuthenticate(?, ?) }";

        CallableStatement cs = m_data.getConnection().prepareCall(q);

        cs.registerOutParameter (1, Types.BOOLEAN);
        cs.setString(2, username);
        cs.setBytes(3, Common.fromHex(token));
        cs.execute();

        m_data.getConnection().commit();

        // TODO
        return cs.getBoolean(1) ? "AUTHORISED_USER" : UNAUTHORISED_ROLE;
      }
      catch (SQLException ex) {
        try {
          m_data.getConnection().rollback();
        }
        catch (SQLException ex2) {
          m_logger.log(Level.SEVERE, "Error performing rollback", ex2);
        }

        m_logger.log(Level.SEVERE, "Error authorising user");
        throw ex;
      }
    }
    else {
      m_logger.log(Level.SEVERE, "Error authorising user; bad database connection");
      throw new Exception("Error authorising user; bad database connection");
    }
  }
}
