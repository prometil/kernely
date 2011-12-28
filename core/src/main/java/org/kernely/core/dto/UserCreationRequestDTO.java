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
 * DTO to stock data for the creation of a new user.
 */
@XmlRootElement
public class UserCreationRequestDTO {

	/**
	 * The id of the user
	 */
	public int id;
	
	/**
	 * The  firstname of the user
	 */
	public String firstname;
	
	/**
	 * the lastname of the user 
	 */
	public String lastname;
	
	/**
	 * the username of the user
	 */
	public String username;
	
	/**
	 * the password of the user
	 */
	public String password;
	
	/**
	 * the indication if an user  is locked or not
	 */
	public boolean locked;
	
	/**
	 * the list of   roles of an user
	 */
	public List<RoleDTO> roles;

	/**
	 * Default constructor 
	 */
	public UserCreationRequestDTO() {

	}

	/**
	 * Constructor
	 * @param id
	 * @param fName
	 * @param lName
	 * @param username
	 * @param password
	 * @param locked
	 * @param roles
	 */
	public UserCreationRequestDTO(int id, String fName, String lName, String username, String password, boolean locked, List<RoleDTO> roles) {
		this.id = id;
		this.firstname = fName;
		this.lastname = lName;
		this.username = username;
		this.password = password;
		this.locked = locked;
		this.roles = roles;
	}

}
