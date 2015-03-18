// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommon.test.mocks;

import com.recursiveloop.webcommon.Mailer;

import org.mockito.Mockito;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Alternative;


public class MailerProducer {
  @Produces
  @Alternative
  public Mailer createMailer() {
    return Mockito.mock(Mailer.class);
  }
}
