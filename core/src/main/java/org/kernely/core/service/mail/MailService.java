/*
Copyright 2011 Prometil SARL

This file is part of Kernely.

Kernely is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Kernely is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Kernely.
If not, see <http://www.gnu.org/licenses/>.
*/
package org.kernely.core.service.mail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration.AbstractConfiguration;
import org.kernely.core.service.mail.builder.MailBuilder;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.core.template.TemplateRenderer.TemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * The mail service
 * 
 */
public class MailService  implements Mailer{

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	@Inject
	public AbstractConfiguration configuration;

	@Inject
	public TemplateRenderer renderer;

	/**
	 * Create mail from the given gsp template
	 * 
	 * @param templatePath
	 *            the template path
	 * @return a mail builder
	 */
	public MailBuilder create(String templatePath) {
		return new JavaMailBuilder(templatePath, renderer.create(templatePath));
	}

	/**
	 * 
	 * @author g.breton
	 * 
	 */
	public class JavaMailBuilder implements MailBuilder{

		// the recipients list
		private List<String> recipients;

		private List<String> ccs;

		private String subject;

		private TemplateBuilder builder;

		/**
		 * Create a mail builder from a template
		 * 
		 * @param templatePath
		 *            the template
		 */
		public JavaMailBuilder(String template, TemplateBuilder pBuilder) {
			recipients = new ArrayList<String>();
			ccs = new ArrayList<String>();
			builder = pBuilder;
		}

		public MailBuilder subject(String pSubject) {
			subject = pSubject;
			return this;
		}

		public MailBuilder to(String addresses) {
			recipients.add(addresses);
			return this;
		}

		public MailBuilder cc(String addresses) {
			ccs.add(addresses);
			return this;
		}

		/**
		 * Effectively send the email using the configuration and the recipients
		 * specified via the to() method.
		 */
		@SuppressWarnings("unchecked")
		public void send() {
			String body = builder.withoutLayout().render();
			Properties props = new Properties();

			Iterator<String> keys = (Iterator<String>) configuration.getKeys("mail");
			while (keys.hasNext()) {
				String key = keys.next();
				props.put(key, configuration.getProperty(key));
			}
			Session session;
			if (configuration.getBoolean("mail.smtp.auth")) {
				session = Session.getDefaultInstance(props, new Authenticator());
			} else {
				session = Session.getDefaultInstance(props);
			}

			MimeMessage message = new MimeMessage(session);
			try {

				for (String to : recipients) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				}
				for (String cc : ccs) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
				}

				message.setSubject(subject);
				message.setText(body);

				Transport.send(message);
			} catch (MessagingException ex) {
				log.error("Cannot send mail", ex);
			}

		}

		private class Authenticator extends javax.mail.Authenticator {
			private PasswordAuthentication authentication;

			public Authenticator() {
				authentication = new PasswordAuthentication(configuration.getString("mail.smtp.user"), configuration.getString("mail.smtp.password"));
			}

			protected PasswordAuthentication getPasswordAuthentication() {
				return authentication;
			}
		}

	}
}
