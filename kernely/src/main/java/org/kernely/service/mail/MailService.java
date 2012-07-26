/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.service.mail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.Query;

import org.apache.commons.configuration.AbstractConfiguration;
import org.kernely.core.model.Mail;
import org.kernely.service.AbstractService;
import org.kernely.service.mail.builder.MailBuilder;
import org.kernely.template.SobaTemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * The mail service
 * 
 */
public class MailService extends AbstractService implements Mailer {

	private static Logger log = LoggerFactory.getLogger(MailService.class);

	@Inject
	private AbstractConfiguration configuration;

	@Inject
	private SobaTemplateRenderer renderer;

	/**
	 * Create mail from the given gsp template
	 * 
	 * @param templatePath
	 *            the template path
	 * @return a mail builder
	 */
	public MailBuilder create(String templatePath) {
		return new JavaMailBuilder(templatePath);
	}

	/**
	 * Build a model of a mail 
	 */
	public class JavaMailBuilder implements MailBuilder {

		private List<String> recipients;

		private List<String> ccs;

		private String subject;

		private Map<String, Object> binding;
		
		private String templatePath;

		/**
		 * Create a mail builder from a template
		 * 
		 * @param templatePath
		 *            the template
		 */
		public JavaMailBuilder(String pTemplatePath) {
			recipients = new ArrayList<String>();
			ccs = new ArrayList<String>();
			templatePath = pTemplatePath;
			
		}

		/**
		 * Set the subject of the mail
		 * @param pSubject The subject of the mail
		 * @return The mail builder
		 */
		public MailBuilder subject(String pSubject) {
			subject = pSubject;
			return this;
		}

		/**
		 * Set the recipients of the mail
		 * @param addresses The addresses of the recipients of this mail
		 * @return The mail builder
		 */
		public MailBuilder to(String addresses) {
			recipients.add(addresses);
			return this;
		}
		
		/**
		 * Set the recipients of the mail
		 * @param addresses A list of all recipients' addresses
		 * @return The mail builder
		 */
		public MailBuilder to(List<String> addresses){
			recipients.addAll(addresses);
			return this;
		}

		/**
		 * Set the CC of the mail
		 * @param addresses Recipients' addresses to put in copy of this mail
		 * @return The mail builder
		 */
		public MailBuilder cc(String addresses) {
			ccs.add(addresses);
			return this;
		}
		
		/**
		 * Set the CC of the mail
		 * @param addresses List of recipients' addresses to put in copy of this mail
		 * @return The mail builder
		 */
		public MailBuilder cc(List<String> addresses) {
			ccs.addAll(addresses);
			return this;
		}

		/**
		 * Fill the content of the mail
		 * @param content Map containing association key - value for the content of the mail
		 * @return The mail builder
		 */
		public MailBuilder with(Map<String, Object> content) {
			binding.putAll(content);
			return this;
		}

		/**
		 * Fill the content of the mail
		 * @param content Containing association key - value for the content of the mail
		 * @return The mail builder
		 */
		public MailBuilder with(String key, String value) {
			binding.put(key, value);
			return this;
		}
		
		/**
		 * Save the mail in BDD, waiting to be processed by the batch.
		 * @return The result of the operation
		 */
		@Transactional
		public boolean registerMail(){
			
			String body = renderer.render(templatePath,binding);
			StringBuilder recipString = new StringBuilder("");
			String recipInString="";

			for (String to : recipients) {
				recipString.append(to);
				recipString.append(",");
			}
			if(!recipString.toString().equals("")){
				// Remove the last coma
				recipInString = recipString.toString().substring(0, recipString.toString().lastIndexOf(','));
			}
			StringBuilder ccBuildString = new StringBuilder("");
			String ccString ="" ;
			for (String cc : ccs) {
				ccBuildString.append(cc);
				ccBuildString.append(",");
			}
			if(!ccBuildString.toString().equals("")){
				// Remove the last coma
				ccString = ccBuildString.toString().substring(0, ccBuildString.toString().lastIndexOf(','));
			}
			
			Mail mail = new Mail();
			if(!ccString.equals("")){
				mail.setCc(ccString);
			}
			mail.setContent(body);
			if(!recipInString.equals("")){
				mail.setRecipients(recipInString);
			}
			mail.setSubject(subject);
			mail.setStatus(Mail.MAIL_WAITING);
			
			em.get().persist(mail);
			return true;
		}
	}
	
	@Transactional
	private void deleteMail(Mail mail){
		Mail m = em.get().find(Mail.class, mail.getId());
		em.get().remove(m);
	}
	
	/**
	 * Retrieve waiting mails stored in bd
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> getMailsToSend(){
		Query query = em.get().createQuery("SELECT m FROM Mail m WHERE status = :status");
		query.setParameter("status", Mail.MAIL_WAITING);
		return (List<Mail>) query.getResultList();
	}
	
	/**
	 * Effectively send the email using the configuration and the recipients specified via the to() method.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public boolean send(Mail mail) {
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
			if(mail.getRecipients() == null || mail.getRecipients().equals("")){
				throw new IllegalArgumentException("Recipients are undefined.");
			}
			
			String[] rec = mail.getRecipients().split(",");
			for (String to : rec) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			}

			if(mail.getCc() != null){
				String[] ccsS = mail.getCc().split(",");
				for (String cc : ccsS) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
				}
			}

			message.setSubject(mail.getSubject());
			message.setContent(mail.getContent(), "text/html");

			Transport.send(message);
			log.debug("Mail sended ! ");
			mail.setStatus(Mail.MAIL_SENDED);
			em.get().merge(mail);
			this.deleteMail(mail);
			return true;
		} catch (MessagingException ex) {
			log.error("Cannot send mail", ex);
			mail.setStatus(Mail.MAIL_ERROR);
			em.get().merge(mail);
			return false;
		} catch (IllegalArgumentException ex){
			log.error("Impossible to send mails", ex);
			mail.setStatus(Mail.MAIL_ERROR);
			em.get().merge(mail);
			return false;
		}

	}

	private class Authenticator extends javax.mail.Authenticator {
		private PasswordAuthentication authentication;

		/**
		 * Default constructor
		 */
		public Authenticator() {
			authentication = new PasswordAuthentication(configuration.getString("mail.smtp.user"), configuration.getString("mail.smtp.password"));
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}
}
