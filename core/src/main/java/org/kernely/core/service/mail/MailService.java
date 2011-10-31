/**
 * 
 */
package org.kernely.core.service.mail;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.kernely.core.template.TemplateRenderer;
import org.kernely.core.template.TemplateRenderer.TemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * The mail service
 * 
 * @author g.breton
 * 
 */
public class MailService {

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
	public MailBuilder make(String templatePath) {
		return new MailBuilder(templatePath, renderer.create(templatePath));
	}

	/**
	 * 
	 * @author g.breton
	 * 
	 */
	public class MailBuilder {

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
		public MailBuilder(String template, TemplateBuilder pBuilder) {
			recipients = new ArrayList<String>();
			ccs = new ArrayList<String>();
			builder = pBuilder;
		}

		public MailBuilder subject(String pSubject) {
			subject = pSubject;
			return this;
		}

		public MailBuilder to(String... addresses) {
			recipients.addAll(Arrays.asList(addresses));
			return this;
		}

		public MailBuilder cc(String... addresses) {
			ccs.addAll(Arrays.asList(addresses));
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
