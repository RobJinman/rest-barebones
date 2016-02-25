package com.recursiveloop.webcommondemo.resources;

import com.recursiveloop.webcommondemo.models.RestrictedResource;
import com.recursiveloop.webcommondemo.exceptions.UnauthorisedException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.HeaderParam;
import java.sql.SQLException;


@Path("/restrictedresource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RcRestrictedResource {
  @GET
  public RestrictedResource doGet(@HeaderParam("X-Auth-Token") String token, @HeaderParam("X-Username") String username)
    throws SQLException, UnauthorisedException;
}
