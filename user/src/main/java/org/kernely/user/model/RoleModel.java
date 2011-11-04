package org.kernely.user.model;

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
@Table(name="kernely_role")
public class RoleModel extends AbstractEntity {
	public final static String ROLE_ADMINISTRATOR = "Administrator";

	/**
	 * Role's id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="role_id")
	private int id;

	/**
	 * Role Name
	 */
	private String name;

	/**
	 * Groups having this role
	 */
	@ManyToMany(
			mappedBy = "roles",
			fetch=FetchType.LAZY
	)
	private Set<GroupModel> groups;

	/**
	 * Users having this role
	 */
	@ManyToMany(
			mappedBy = "roles",
			fetch=FetchType.LAZY
	)
	private Set<UserModel> users;

	/**
	 * Constructor, with default values
	 */
	public RoleModel(){
		this.groups = new HashSet<GroupModel>();
		this.id = 0;
		this.name = "";
		this.users = new HashSet<UserModel>();
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
	public final void setUsers(Set<UserModel> roleUsers) {
		this.users = roleUsers;
	}

}
