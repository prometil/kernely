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
package org.kernely.core.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.kernely.persistence.AbstractModel;

/**
 * The mail model
 */
@Entity
@Table(name = "kernely_mail")
public class Mail extends AbstractModel {
	
	/**
	 * Error status for a mail
	 */
	public static final int MAIL_ERROR = 2;
	
	/**
	 * Sended status for a mail
	 */
	public static final int MAIL_SENDED = 1;
	
	/**
	 * Waiting status for a mail
	 */
	public static final int MAIL_WAITING = 0;
	
	private String subject;
	private String content;
	private String recipients;
	private String cc;
	private int status;

	/**
	 * @return the object
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param object
	 *            the object to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the recipients
	 */
	public String getRecipients() {
		return recipients;
	}

	/**
	 * @param recipients
	 *            the recipients to set
	 */
	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	/**
	 * @return the cc
	 */
	public String getCc() {
		return cc;
	}

	/**
	 * @param cc
	 *            the cc to set
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
}
