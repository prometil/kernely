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

import org.kernely.core.hibernate.AbstractModel;

/**
 * The role model
 */
@Entity
@Table(name="kernely_role")
public class Role extends AbstractModel {
	public static final String ROLE_USER = "User";
	public static final String ROLE_ADMINISTRATOR = "Administrator";
	public static final String ROLE_HUMANRESOURCE = "Human resource";
	public static final String ROLE_PROJECTMANAGER = "Project manager";
	public static final String ROLE_CLIENT = "Client";
	public static final String ROLE_BOOKKEEPER = "Book keeper";

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
	private Set<Group> groups;

	/**
	 * Users having this role
	 */
	@ManyToMany(
			mappedBy = "roles",
			fetch=FetchType.LAZY
	)
	private Set<User> users;

	/**
	 * Constructor, with default values
	 */
	public Role(){
		this.groups = new HashSet<Group>();
		this.id = 0;
		this.name = "";
		this.users = new HashSet<User>();
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
	public final Set<Group> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
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
	 * @param users the users to set
	 */
	public final void setUsers(Set<User> roleUsers) {
		this.users = roleUsers;
	}

	/**
	 * return the hashcode of the object code 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Equals 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		Role other = (Role) obj;
		if (id != other.id){
			return false;
		}
		if (name == null) {
			if (other.name != null){
				return false;
			}
		} else if (!name.equals(other.name)){
			return false;
		}
		return true;
	}

	
}
