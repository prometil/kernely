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
package org.kernely.stream.model;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;


@Entity
@Table(name = "kernely_stream")
public class Stream extends AbstractModel {
	
	
	public Stream() {
		super();
		this.category = "";
		this.locked = false;
		this.messages = new HashSet<Message>();
		this.title="";
		this.user_id = 1;
	}
	
	private int user_id;

	public int getUserId() {
		return user_id;
	}

	public void setUserId(int userId) {
		this.user_id = userId;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * Category for users' streams
	 */
	public static final String CATEGORY_USERS = "streams/users";
	/**
	 * Category for bundles' streams
	 */
	public static final String CATEGORY_PLUGINS = "streams/plugins";
	/**
	 * Category for other streams
	 */
	public static final String CATEGORY_OTHERS = "streams/others";

	/**
	 * The resource for streams to give rights on the streams.
	 */
	public static final String STREAM_RESOURCE = "streams";

	/**
	 * The right for an user to see the stream, and to subscribe to it.
	 */
	public static final String RIGHT_READ = "read";

	/**
	 * The right for an user to write messages on the stream.
	 * This right includes the right to see the stream.
	 */
	public static final String RIGHT_WRITE = "write";


	/**
	 * The right for an user to delete messages of the stream.
	 * This right includes the right to write on the stream.
	 */
	public static final String RIGHT_DELETE = "delete";

	
	/**
	 * The category of the stream.
	 */
	private String category;
	
	
	/**
	 * Stream's title, which can be displayed.
	 */
	private String title;
	
	/**
	 * Stream's state : locked or unlocked.
	 * When a stream is locked, users can't write on it.
	 */
	private boolean locked;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	/**
	 * The method return the id.
	 * @return the long
	 */
	public long getId() {
		return id;
	}

	/**
	 * The method set the id.
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * Return the title of the stream, which can be displayed.
	 * @return the title
	 */
	public String getTitle() {
		
		return title;
	}
	/**
	 * Sets the title of the stream
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * The message date
	 * @return the message date
	 */
	public boolean isLocked() {
		return this.locked;
	}

	/**
	 * Lock the stream. When a stream is locked, it is impossible to write on it.
	 */
	public void lock() {
		this.locked = true;
	}
	
	/**
	 * Unlock the stream. Users which have the right to write on it can write on it.
	 */
	public void unlock() {
		this.locked = false;
	}
	
	/**
	 * All the messages contained by the stream.
	 */
	@OneToMany(fetch=FetchType.LAZY, mappedBy="stream")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<Message> messages;
	
	/**
	 * Users who subscribed to this stream
	 */
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinTable( name="kernely_stream_subscription",
				joinColumns=@JoinColumn(name="fk_stream"),
				inverseJoinColumns=@JoinColumn(name="fk_user"))
	private Set<User> subscriptors;
	
	/**
	 * Get users which follow this stream.
	 */
	public Set<User> getSubscriptors() {
		return subscriptors;
	}

	/**
	 * Set users which follow this stream.
	 */
	public void setSubscriptors(Set<User> subscriptors) {
		this.subscriptors = subscriptors;
	}

	/**
	 * @return the messages contained in the stream.
	 */
	public final Set<Message> getMessages() {
		return messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public final void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
	
	

}
