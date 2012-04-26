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
package org.kernely.core.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for manager data, contains only his name and the number of users he owns.
 */
@XmlRootElement
public class ManagerDTO {

	/**
	 * The members managed by the manager 
	 */
	public List<UserDTO> users;
	
	/**
	 * The number of users managed by this manager
	 */
	public int nbUsers;
	
	/**
	 * The username of the manager
	 */
	public String name;
	
	/**
	 * The id of this manager
	 */
	public long id;

	/**
	 * Manager DTO default constructor
	 */
	public ManagerDTO() {

	}

	/**
	 * Manager constructor with his username and the list of users managed
	 * @param newName
	 * @param newUsers
	 */
	public ManagerDTO(long id, String newName, List<UserDTO> newUsers) {
		this.users = newUsers;
		this.id = id;
		this.name = newName;
		this.nbUsers = newUsers.size();
	}

	/**
	 * Return the hashcode of the object
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((users == null) ? 0 : users.hashCode());
		return result;
	}

	/**
	 * Equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ManagerDTO other = (ManagerDTO) obj;
		if (name == null) {
			if (other.name != null){
				return false;
			}
		} else if (!name.equals(other.name)){
			return false;
		}
		return true;
	}

}
