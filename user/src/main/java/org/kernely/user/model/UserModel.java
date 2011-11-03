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
package org.kernely.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractEntity;


/**
 * Entity in database is in Java version
 */
@Entity
@Table(name = "kernely_user")
public class UserModel extends AbstractEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	/**
	 * Get the id of the user.
	 * @return The id of the user.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the id of the user
	 * @param id The id of the user.
	 */
	public void setId(long id) {
		this.id = id;
	}

	
	private String username;
	private String password;
	
	/**
	 * Get the username of the user.
	 * @return The username of the user.
	 */
	@Column(name = "username")
	public String getUsername() {
		return username;
	}
	
	/**
	 * Get the password of the user.
	 * return The password of the user.
	 */
	@Column(name = "password")
	public String getPassword() {
		return password;
	}
	
	/**
	 * Set or replace the username of the user.
	 * @param newUsername The new username.
	 */
	public void setUsername(String newUsername) {
		this.username = newUsername;
	}
	
	/**
	 * Set or replace the password of the user.
	 * @param newPassword The new password.
	 */
	public void setPassword(String newPassword) {
		this.password = newPassword;
	}
}
