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
package org.kernely.core.event;

/**
 * 
 * @author b.grandperret
 *
 */
public class UserCreationEvent{
	private long id;
	private String username;
	
	/**
	 * Constructor
	 * @param id
	 * @param username
	 */
	public UserCreationEvent(long id, String username) {
		super();
		this.id = id;
		this.username = username;
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
	
	
}
