// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommondemo.resources;

import com.recursiveloop.webcommondemo.models.UserEmail;
import com.recursiveloop.webcommondemo.exceptions.InternalServerException;
import com.recursiveloop.webcommondemo.exceptions.ConflictException;
import com.recursiveloop.webcommondemo.exceptions.BadRequestException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/pending_account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RcPendingAccount {
  @POST
  public Response doPost(UserEmail userEmail)
    throws InternalServerException, ConflictException, BadRequestException;
}
