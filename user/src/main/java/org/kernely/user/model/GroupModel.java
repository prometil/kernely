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
package org.kernely.user.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.kernely.core.hibernate.AbstractEntity;

@Entity
@Table(name = "kernely_group")
public class GroupModel extends AbstractEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="group_id")
	/**
	 * Group's id
	 */
	private int id;

	/**
	 * Group's name
	 */
	private String name;

	/**
	 * Users in the group
	 */
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinTable( name="kernely_user_group",
			joinColumns=@JoinColumn(name="fk_group_user_group"),
			inverseJoinColumns=@JoinColumn(name="fk_user_user_group"))
			private Set<UserModel> users;

	/**
	 * Roles of the group
	 */
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinTable( name="kernely_group_roles",
			joinColumns=@JoinColumn(name="fk_group"),
			inverseJoinColumns=@JoinColumn(name="fk_role"))
			private Set<RoleModel> roles;

	/**
	 * Permissions of the group
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable( name="kernely_group_permissions",
			joinColumns=@JoinColumn(name="fk_group"),
			inverseJoinColumns=@JoinColumn(name="fk_permission"))
			@Cascade( { org.hibernate.annotations.CascadeType.ALL})
			private Set<PermissionModel> permissions;

	/**
	 * Initialize a group with default values.
	 */
	public GroupModel(){
		this.roles = new HashSet<RoleModel>();
		this.id = 0;
		this.name = "";
		this.users = new HashSet<UserModel>();
		this.permissions = new HashSet<PermissionModel>();
	}

	/**
	 * @return the permissions
	 */
	public final Set<PermissionModel> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public final void setPermissions(Set<PermissionModel> permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the roles
	 */
	public final Set<RoleModel> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public final void setRoles(Set<RoleModel> roles) {
		this.roles = roles;
	}

	/**
	 * Get the group's ID
	 * @return the group's ID
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Set the group's ID
	 * @param id : the group's ID
	 */
	public final void setId(int id) {
		this.id = id;
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
	public final Set<UserModel> getUsers() {
		return users;
	}

	/**
	 * Sets the users
	 * @param users the users to set
	 */
	public final void setUsers(Set<UserModel> users) {
		this.users = users;
	}
}