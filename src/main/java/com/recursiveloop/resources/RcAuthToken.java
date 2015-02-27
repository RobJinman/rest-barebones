// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.resources;

import com.recursiveloop.models.UserCredentials;
import com.recursiveloop.models.AuthToken;
import com.recursiveloop.exceptions.InternalServerException;
import com.recursiveloop.exceptions.UnauthorisedException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;


@Path("/authtoken")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RcAuthToken {
  @POST
  public AuthToken doPost(UserCredentials credentials)
    throws InternalServerException, UnauthorisedException;
}
