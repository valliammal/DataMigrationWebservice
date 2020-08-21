package com.cpimsws.appcode;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Generic class for Sending Email.
 * 
 * @EMP ID:104
 * @date 	08-07-2014
 */
public class SendEmail extends Object {

	/**
	 * Generic Method to Send Email
	 * @param from
	 * @param to
	 * @param subject
	 * @param body
	 */
	public static void sendMail(String from, String  to, String cc, String  subject, String  body) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com"); 
			props.put("mail.smtp.auth", "true");
			props.put("mail.debug", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.port", "465");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");

			Session mailSession = Session.getInstance(props,
					new javax.mail.Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(
							"", "");
				}
			});
			mailSession.setDebug(true); // Enable the debug mode

			Message msg = new MimeMessage(mailSession);
			msg.setFrom(new InternetAddress(from));
			msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
			msg.setRecipients(Message.RecipientType.CC,InternetAddress.parse(cc));
			msg.setSentDate(new Date());
			msg.setSubject(subject);
			msg.setText(body);
			Transport.send(msg);

		} catch (Exception E) {
			System.out.println("Oops something has gone pearshaped!");
			System.out.println(E);
		}
	}
}