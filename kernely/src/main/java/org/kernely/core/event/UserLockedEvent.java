package org.kernely.core.event;

import org.kernely.core.model.User;

/**
 * Event linked to the lock of an user.
 */
public class UserLockedEvent {
	private long id;
	private String username;
	private boolean locked;
	private User user;
	
	/**
	 * Constructor
	 * @param id
	 * @param username
	 */
	public UserLockedEvent(long id, String username, boolean locked) {
		super();
		this.id = id;
		this.username = username;
		this.locked = locked;
	}

	/**
	 * The method returns the id
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * The method set the id
	 * @param pId the new id to set
	 */
	public void setId(long pId) {
		this.id = pId;
	}

	/** 
	 * The method returns the username
	 * @return the userrname
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * The method set the username
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
