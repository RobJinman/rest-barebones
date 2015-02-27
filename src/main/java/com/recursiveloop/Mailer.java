// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop;

import com.recursiveloop.annotations.Config;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;
import java.util.logging.Level;


@ApplicationScoped
public class Mailer {
  private static final Logger m_logger = Logger.getLogger(Mailer.class.getName());

  @Inject @Config("address")
  String m_addr;

  @Inject @Config("password")
  String m_pw;

  @Inject @Config("smtp.auth")
  String m_smtpAuth;

  @Inject @Config("smtp.starttls.enable")
  String m_smtpStarttlsEnable;

  @Inject @Config("smtp.host")
  String m_smtpHost;

  @Inject @Config("smtp.port")
  String m_smtpPort;

  public void sendToSelf(String sender, String subject, String body) {
    try {
      Message message = new MimeMessage(getSession());
      message.setFrom(new InternetAddress(sender));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(m_addr));
      message.setSubject(subject);
      message.setText(body);

      Transport.send(message);
    }
    catch (MessagingException ex) {
      m_logger.log(Level.WARNING, "Error dispatching email", ex);
    }
  }

  public void sendToRecipient(String recipient, String subject, String body) {
    try {
      Message message = new MimeMessage(getSession());
      message.setFrom(new InternetAddress(m_addr));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
      message.setSubject(subject);
      message.setText(body);

      Transport.send(message);
    }
    catch (MessagingException ex) {
      m_logger.log(Level.WARNING, "Error dispatching email", ex);
    }
  }

  private Session getSession() {
    Properties props = new Properties();

    props.put("mail.smtp.auth", m_smtpAuth);
    props.put("mail.smtp.starttls.enable", m_smtpStarttlsEnable);
    props.put("mail.smtp.host", m_smtpHost);
    props.put("mail.smtp.port", m_smtpPort);

    return Session.getInstance(props, new javax.mail.Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(m_addr, m_pw);
      }
    });
  }
}
