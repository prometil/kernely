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
 * DTO for group data, contains only his name.
 */
@XmlRootElement
public class GroupDTO {

	/**
	 * The id of the group
	 */
	public long id;
	
	/**
	 * The name of the group
	 */
	public String name;
	
	/**
	 * The list of member of the group
	 */
	public List<UserDTO> users;
	
	/**
	 * Number of users in this group
	 */
	public int nbUser;

	/**
	 * Default Constructor
	 */
	public GroupDTO() {

	}

	/**
	 * Constructor which set datas of the group.
	 * 
	 * @param id
	 *            The id of the updated group. Null in the case of new group
	 * @param pName
	 *            The name of the group.
	 * @param users
	 *            Users associated to the current group
	 */
	public GroupDTO(String pName, long id, List<UserDTO> users) {
		this.id = id;
		this.name = pName;
		this.users = users;
		this.nbUser = users.size();
	}

}
