// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommondemo;

import com.recursiveloop.webcommondemo.resources.RcUnrestrictedResource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;


@RunWith(Arquillian.class)
public class RcUnrestrictedResourceTest {

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "RcUnrestrictedResourceTest.war")
      .addPackage("com/recursiveloop/webcommon/test")
      .addPackage("com/recursiveloop/webcommon")
      .addPackage("com/recursiveloop/webcommon/annotations")
      .addPackage("com/recursiveloop/webcommondemo")
      .addPackage("com/recursiveloop/webcommondemo/models")
      .addPackage("com/recursiveloop/webcommondemo/resources")
      .addPackage("com/recursiveloop/webcommondemo/exceptions")
      .addAsResource("config.properties")
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
      .addAsWebInfResource("jboss-web.xml", "jboss-web.xml")
      .setWebXML("web.xml");
  }

  @Test
  @RunAsClient
  @Consumes(MediaType.TEXT_PLAIN)
  public void request_resource(@ArquillianResteasyResource("rest") RcUnrestrictedResource rcUnrestrictedResource) {
    System.out.println("**((REQUEST_RESOURCE))**");

    String msg = rcUnrestrictedResource.doGet();
    Assert.assertNotNull(msg);
    Assert.assertEquals("Hello", msg);
  }
}
