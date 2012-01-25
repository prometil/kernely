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
package org.kernely.stream.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Group rights on stream DTO 
 */
@XmlRootElement
public class GroupRightOnStreamDTO {

	/**
	 * The group id 
	 */
	public int groupid;
	
	/**
	 * The type of permission for the user 
	 */
	public String permission;

	/**
	 * Default constructor
	 */
	public GroupRightOnStreamDTO() {

	}

	/**
	 * Creates a GroupRightOnStreamDTO
	 * 
	 * @param groupid
	 *            Id of the group
	 * @param permission
	 *            Permission granted to the group
	 */
	public GroupRightOnStreamDTO(int groupid, String permission) {
		this.groupid = groupid;
		this.permission = permission;
	}

}
