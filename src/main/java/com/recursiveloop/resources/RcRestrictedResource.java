// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.resources;

import com.recursiveloop.models.RestrictedResource;
import com.recursiveloop.exceptions.InternalServerException;
import com.recursiveloop.exceptions.UnauthorisedException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.HeaderParam;


@Path("/restrictedresource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RcRestrictedResource {
  @GET
  public RestrictedResource doGet(@HeaderParam("X-Auth-Token") String token, @HeaderParam("X-Username") String username)
    throws InternalServerException, UnauthorisedException;
}
