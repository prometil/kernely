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
 * The dto for the creation request of a manager
 * @author b.grandperret
 *
 */
@XmlRootElement
public class ManagerCreationRequestDTO {

	public String manager;
	public List<UserDTO> users;

	/**
	 * Default constructor
	 */
	public ManagerCreationRequestDTO() {

	}

	/**
	 * Constructor with the manager username and his list of users
	 * @param newManager
	 * @param newUsers
	 */
	public ManagerCreationRequestDTO(String newManager, List<UserDTO> newUsers) {
		this.manager = newManager;
		this.users = newUsers;
	}

}
