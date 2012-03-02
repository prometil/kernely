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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.kernely.core.hibernate.AbstractModel;

/**
 * The group model
 */
@Entity
@Table(name = "kernely_group")
public class Group extends AbstractModel{

	/**
	 * Group's name
	 */
	private String name;

	/**
	 * Users in the group
	 */
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinTable( name="kernely_user_group",
			joinColumns=@JoinColumn(name="group_id"),
			inverseJoinColumns=@JoinColumn(name="user_id"))
			private Set<User> users;

	/**
	 * Roles of the group
	 */
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinTable( name="kernely_group_roles",
			joinColumns=@JoinColumn(name="group_id"),
			inverseJoinColumns=@JoinColumn(name="role_id"))
			private Set<Role> roles;

	/**
	 * Permissions of the group
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable( name="kernely_group_permissions",
			joinColumns=@JoinColumn(name="group_id"),
			inverseJoinColumns=@JoinColumn(name="permission_id"))
			@Cascade( { org.hibernate.annotations.CascadeType.ALL})
			private Set<Permission> permissions;

	/**
	 * Initialize a group with default values.
	 */
	public Group(){
		this.roles = new HashSet<Role>();
		this.id = 0;
		this.name = "";
		this.users = new HashSet<User>();
		this.permissions = new HashSet<Permission>();
	}

	/**
	 * @return the permissions
	 */
	public final Set<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public final void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the roles
	 */
	public final Set<Role> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public final void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	/**
	 * Get the group's name
	 * @return the group's name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the group's name
	 * @param name : the group's name
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the users in the group.
	 * @return the users
	 */
	public final Set<User> getUsers() {
		return users;
	}

	/**
	 * Sets the users
	 * @param users the users to set
	 */
	public final void setUsers(Set<User> users) {
		this.users = users;
	}
}