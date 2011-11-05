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
package org.kernely.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractEntity;

@Entity
@Table(name="kernely_permission")
public class PermissionModel extends AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="permission_id")
	/**
	 * Permission's id
	 */
	private int id;

	/**
	 * Permission's Name (resourceType:rights:resourceID)
	 */
	private String name;

	/**
	 * Groups having the permission
	 */
	@ManyToMany(
			mappedBy = "permissions",
			fetch=FetchType.LAZY
	)
	private Set<GroupModel> groups;

	/**
	 * Users having the permission
	 */
	@ManyToMany(
			mappedBy = "permissions",
			fetch=FetchType.LAZY
	)
	private Set<UserModel> users;

	/**
	 * Default constructor.
	 */
	public PermissionModel(){
		this.groups = new HashSet<GroupModel>();
		this.id = 0;
		this.name = "";
		this.users = new HashSet<UserModel>();
	}

	/**
	 * @return the groups
	 */
	public final Set<GroupModel> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public final void setGroups(Set<GroupModel> groups) {
		this.groups = groups;
	}

	/**
	 * @return the users
	 */
	public final Set<UserModel> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public final void setUsers(Set<UserModel> users) {
		this.users = users;
	}

	/**
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the type of the resource concerned by the permission.
	 * @return The type of the resource.
	 */
	public final String getResourceType(){
		return this.name.substring(0, this.name.indexOf(":"));
	}

	/**
	 * Get the rights of the permission.
	 * @return The rights.
	 */
	public final String getRights(){
		return this.name.substring(this.name.indexOf(":") + 1 , this.name.lastIndexOf(":"));
	}

	/**
	 * Gets the resource ID concerned by the permission.
	 * @return The resource ID
	 */
	public final String getResourceID(){
		return this.name.substring(this.name.lastIndexOf(":"));
	}

}
