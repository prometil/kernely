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
package org.kernely.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.kernely.persistence.AbstractModel;

/**
 * The permission model
 */
@Entity
@Table(name = "kernely_permission")
public class Permission extends AbstractModel {

	/**
	 * Permission's Name (rights:resourceType:resourceID)
	 */
	private String name;

	/**
	 * Groups having the permission
	 */
	@ManyToMany(mappedBy = "permissions", fetch = FetchType.EAGER)
	private Set<Group> groups;

	/**
	 * Users having the permission
	 */
	@ManyToMany(mappedBy = "permissions", fetch = FetchType.EAGER)
	private Set<User> users;

	/**
	 * Default constructor.
	 */
	public Permission() {
		this.groups = new HashSet<Group>();
		this.id = 0;
		this.name = "";
		this.users = new HashSet<User>();
	}

	/**
	 * @return the groups
	 */
	public final Set<Group> getGroups() {
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public final void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	/**
	 * @return the users
	 */
	public final Set<User> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public final void setUsers(Set<User> users) {
		this.users = users;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the type of the resource concerned by the permission.
	 * 
	 * @return The type of the resource.
	 */
	public final String getResourceType() {
		return this.name.substring(this.name.indexOf(":") + 1, this.name
				.lastIndexOf(":"));
	}

	/**
	 * Get the rights of the permission.
	 * 
	 * @return The rights.
	 */
	public final String getRights() {
		return this.name.substring(0, this.name.indexOf(":"));
	}

	/**
	 * Gets the resource ID concerned by the permission.
	 * 
	 * @return The resource ID
	 */
	public final String getResourceID() {
		return this.name.substring(this.name.lastIndexOf(":"));
	}

}
