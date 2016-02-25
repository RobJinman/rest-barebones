package com.recursiveloop.webcommon;

import com.recursiveloop.webcommon.config.ConfigParam;
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
  public void sendToSelf(String sender, String subject, String body) throws MessagingException {
    Message message = new MimeMessage(getSession());
    message.setFrom(new InternetAddress(m_addr));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(m_addr));
    message.setSubject(subject);
    message.setText(body);

    Transport.send(message);
  }

  public void sendToRecipient(String recipient, String subject, String body) throws MessagingException {
    Message message = new MimeMessage(getSession());
    message.setFrom(new InternetAddress(m_addr));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
    message.setSubject(subject);
    message.setText(body);

    Transport.send(message);
  }

  private static final Logger m_logger = Logger.getLogger(Mailer.class.getName());

  @Inject @ConfigParam(key="address")
  String m_addr;

  @Inject @ConfigParam(key="password")
  String m_pw;

  @Inject @ConfigParam(key="smtp.auth")
  String m_smtpAuth;

  @Inject @ConfigParam(key="smtp.startssl.enable")
  String m_smtpStartsslEnable;

  @Inject @ConfigParam(key="smtp.starttls.enable")
  String m_smtpStarttlsEnable;

  @Inject @ConfigParam(key="smtp.host")
  String m_smtpHost;

  @Inject @ConfigParam(key="smtp.port")
  String m_smtpPort;

  private Session getSession() {
    Properties props = new Properties();

    if (m_smtpAuth != null) {
      props.setProperty("mail.smtp.auth", m_smtpAuth);
    }

    if (m_smtpHost != null) {
      props.setProperty("mail.smtp.host", m_smtpHost);
    }

    if (m_smtpPort != null) {
      props.setProperty("mail.smtp.port", m_smtpPort);
//      props.setProperty("mail.smtp.socketFactory.port", m_smtpPort);
    }

//    props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//    props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//    props.setProperty("mail.smtp.socketFactory.fallback", "false");

    if (m_smtpStarttlsEnable != null) {
      props.setProperty("mail.smtp.starttls.enable", m_smtpStarttlsEnable);
    }

    if (m_smtpStartsslEnable != null) {
      props.setProperty("mail.smtp.startssl.enable", m_smtpStartsslEnable);
    }

    return Session.getInstance(props, new javax.mail.Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(m_addr, m_pw);
      }
    });
  }
}
