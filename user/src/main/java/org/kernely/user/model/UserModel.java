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



//l'entité (LA table) dans la base de donnée en version java
@Entity
@Table(name = "user")
public class UserModel extends AbstractEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	private String username;
	private String password;
	
	@Column(name = "Username")
	public String getUsername() {
		return username;
	}
	
	@Column(name = "password")
	public String getPassword() {
		return password;
	}
	
	public void setUsername(String newUsername) {
		this.username = newUsername;
	}
	public void setPassword(String newPassword) {
		this.password = newPassword;
	}
}
