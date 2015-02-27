// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.integrationtest;

import com.recursiveloop.resources.RcAuthToken;
import com.recursiveloop.models.UserCredentials;
import com.recursiveloop.models.AuthToken;
import com.recursiveloop.exceptions.InternalServerException;
import com.recursiveloop.exceptions.UnauthorisedException;

import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import java.net.URL;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.NotAuthorizedException;


@RunWith(Arquillian.class)
public class RcAuthTokenTest {

  @Deployment(testable = false)
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "RcAuthTokenTest.war")
      .addPackage("com/recursiveloop")
      .addPackage("com/recursiveloop/models")
      .addPackage("com/recursiveloop/resources")
      .addPackage("com/recursiveloop/annotations")
      .addPackage("com/recursiveloop/exceptions")
      .addAsResource("config.properties")
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
      .addAsWebInfResource("jboss-web.xml", "jboss-web.xml")
      .setWebXML("web.xml");
  }

  @ArquillianResource
  private URL deploymentURL;

  @Test
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_good_credentials(@ArquillianResteasyResource("rest") RcAuthToken rcAuthToken)
    throws InternalServerException, UnauthorisedException {

    UserCredentials credentials = new UserCredentials();
    credentials.setUsername("mrjim");
    credentials.setPassword("pineapple");

    AuthToken token = rcAuthToken.doPost(credentials);

    Assert.assertNotNull(token);
    Assert.assertEquals("51E70432C19EA04640977F2B3B7322EB", token.getToken());
  }

  @Test
  @Consumes(MediaType.APPLICATION_JSON)
  public void with_bad_credentials(@ArquillianResteasyResource("rest") RcAuthToken rcAuthToken) {
    UserCredentials credentials = new UserCredentials();
    credentials.setUsername("mrjim");
    credentials.setPassword("pinapple");

    try {
      rcAuthToken.doPost(credentials);
    }
    catch (NotAuthorizedException ex) {
      Assert.assertTrue(true);
      return;
    }
    catch (Exception ex) {}

    Assert.assertTrue(false);
  }
}
