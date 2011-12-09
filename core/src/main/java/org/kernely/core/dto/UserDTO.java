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
package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.core.model.User;

/**
 * DTO for user data, contains only his name.
 */
@XmlRootElement
public class UserDTO {

	public long id;
	public String username;
	public boolean locked;
	public UserDetailsDTO userDetails;

	public UserDTO() {

	}

	/**
	 * Constructor which set datas of the user.
	 * 
	 * @param pUsername
	 *            The name of the user, the id of the user.
	 */
	public UserDTO(String pUsername, long id) {
		this.username = pUsername;
		this.id = id;
	}

	/**
	 * Constructor which set datas of the user.
	 * 
	 * @param pUsername
	 *            The name of the user.
	 */
	public UserDTO(String pUsername, boolean pLocked, long id) {
		username = pUsername;
		locked = pLocked;
		this.id = id;
	}

	public UserDTO(User u) {
		this.username = u.getUsername();
		this.locked = u.isLocked();
		this.id = u.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UserDTO other = (UserDTO) obj;
		if (id != other.id) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}
}