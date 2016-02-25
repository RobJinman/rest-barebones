package com.recursiveloop.webcommondemo.resources;

import com.recursiveloop.webcommondemo.models.UserCredentials;
import com.recursiveloop.webcommondemo.models.AuthToken;
import com.recursiveloop.webcommondemo.exceptions.InternalServerException;
import com.recursiveloop.webcommondemo.exceptions.UnauthorisedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;


@Path("/authtoken")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RcAuthToken {
  @POST
  public AuthToken doPost(UserCredentials credentials)
    throws SQLException, UnauthorisedException;
}
