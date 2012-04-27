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

import java.util.List;

import org.kernely.core.model.Mail;
import org.kernely.service.mail.builder.MailBuilder;

/**
 * Interface for the creation and sending of mail 
 * @author g.breton
 *
 */
public interface Mailer {
	
	/**
	 * Create a mail builder
	 * @param String templatePath
	 * @return A mail builder
	 */
	 MailBuilder create(String templatePath);
	 
	 /**
	  * Retrieve the mails to send 
	  * @return a list of mail
	  */
	 List<Mail> getMailsToSend();
	 
	 /**
	  * Send a mail
	  * @param mail
	  * @return true if the mail is send else false
	  */
	 boolean send(Mail mail);
}
