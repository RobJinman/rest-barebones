package com.recursiveloop.webcommondemo.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.sql.SQLException;


@Provider
public class DatabaseExceptionHandler implements ExceptionMapper<SQLException> {
  @Override
  public Response toResponse(SQLException ex) {
    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
  }
}
