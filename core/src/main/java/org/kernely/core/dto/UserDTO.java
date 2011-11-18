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
 * DTO for user data, contains only his name.
 */
@XmlRootElement
public class UserDTO {
	
	public UserDTO(){
		
	}
	
	/**
	 * Constructor which set datas of the user.
	 * @param pUsername The name of the user.
	 */
	public UserDTO(String pUsername, boolean pLocked) {
		username = pUsername;
		locked = pLocked;
	}

	public String username;
	public boolean locked;
}