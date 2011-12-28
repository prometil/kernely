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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for permission data, contains only his name.
 */
@XmlRootElement
public class PermissionDTO {

	/**
	 * The right
	 */
	public String right;
	
	/**
	 * The type of permission
	 */
	public String type;
	
	/**
	 * The resourceID
	 */
	public String resourceId;

	/**
	 * Constructor which set datas of the permission.
	 * 
	 * @param pName
	 *            The name of the permission.
	 */
	public PermissionDTO(String pName) {
		name = pName;
		String[] splitted = pName.split(":");
		this.right = splitted[0];
		this.type = splitted[1];
		this.resourceId = splitted[2];
	}

	public String name;

}