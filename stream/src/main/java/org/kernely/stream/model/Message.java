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
package org.kernely.stream.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.kernely.core.model.User;
import org.kernely.persistence.AbstractModel;

/**
 * The message model
 */
@Entity
@Table(name = "kernely_message")
public class Message extends AbstractModel {

	/**
	 * Constructor
	 */
	public Message() {
		super();
		DateTimeZone zoneUTC = DateTimeZone.UTC;
		date = new DateTime().withZone(zoneUTC).toDate();
		this.commentable = true;
		this.content = "";
		this.id = 0;
		this.stream = new Stream();
	}

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	/**
	 * Get the user associated to this message
	 * 
	 * @return : the user associated
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * Set the user associated to this message
	 * 
	 * @param the
	 *            new user
	 */
	public void setUser(User u) {
		this.user = u;
	}

	private String content;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private boolean commentable;

	/**
	 * Can users comment this message ?
	 * 
	 * @return true if users can comment, false otherwise.
	 */
	public boolean isCommentable() {
		return this.commentable;
	}

	/**
	 * Set comment option.
	 * 
	 * @param commentable
	 *            true if the message ca be commented, false otherwise.
	 */
	public void setCommentable(boolean commentable) {
		this.commentable = commentable;
	}

	@ManyToOne
	@JoinColumn(name = "stream_id")
	private Stream stream;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "message")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	private Set<Message> comments;

	@ManyToOne
	@JoinColumn(name = "message_parent", nullable = true)
	private Message message;

	/**
	 * Gets all the comments associated to the message.
	 * 
	 * @return the comments
	 */
	public final Set<Message> getComments() {
		return comments;
	}

	/**
	 * Set comments to the message.
	 * 
	 * @param comments
	 *            the comments to set
	 */
	public final void setComments(Set<Message> comments) {
		this.comments = comments;
	}

	/**
	 * Return the content of the message.
	 * 
	 * @return the content of the message.
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Sets the content of the message.
	 * 
	 * @param content
	 *            the text to set as content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * The message date
	 * 
	 * @return the message date
	 */
	public Date getDate() {
		return (Date) date.clone();
	}

	/**
	 * Get the id of the stream containing this message.
	 * 
	 * @return the stream's id.
	 */
	public Stream getStream() {
		return stream;
	}

	/**
	 * Set the id of the stream containing this message.
	 * 
	 * @param stream
	 */
	public void setStream(Stream stream) {
		this.stream = stream;
	}

	/**
	 * The message set the date
	 * 
	 * @param date
	 *            the date of the message
	 */
	public void setDate(Date pDate) {
		this.date = (Date) pDate.clone();
	}

	/**
	 * Set the parent message of the comment.
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * Get the parent message of the comment.
	 */
	public Message getMessage() {
		return this.message;
	}

}
