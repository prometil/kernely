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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;

/**
 * The mail model
 * @author b.grandperret
 *
 */
@Entity
@Table(name = "kernely_mail")
public class Mail extends AbstractModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "mail_id")
	private int id;

	private String subject;
	private String content;
	private String recipients;
	private String cc;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

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

}
