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

/**
 * DTO for role data, contains only his name.
 */
@XmlRootElement
public class RoleDTO {
	
	/**
	 * Default Constructor
	 */
	public RoleDTO(){
		
	}
	
	/**
	 * Constructor which set datas of the role.
	 * @param pName The name of the role.
	 * @param id The id of the current role
	 */
	public RoleDTO(int id, String pName) {
		this.id = id;
		name = pName;
	}

	public int id;
	public String name;
}
