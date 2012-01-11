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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.kernely.core.hibernate.AbstractModel;

/**
 * Entity in database is in Java version
 */
@Entity
@Table(name = "kernely_user")
public class User extends AbstractModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String username;
	private String password;
	private String salt;
	private boolean locked;

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "kernely_user_managers", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "manager_id"))
	@Cascade({org.hibernate.annotations.CascadeType.ALL})
	private Set<User> managers;

	@ManyToMany(mappedBy = "managers", fetch = FetchType.LAZY)
	private Set<User> users;

	@ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
	private Set<Group> groups;

	/**
	 * Roles of the user
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "kernely_user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	private Set<Role> roles;

	/**
	 * Permissions of the user
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "kernely_user_permissions", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	private Set<Permission> permissions;
	

	@OneToOne(fetch=FetchType.LAZY, mappedBy="user")
	private UserDetails userDetails;

	/**
	 * Get the id of the user.
	 * 
	 * @return The id of the user.
	 */
	public long getId() {
		return id;
	}
	
	
	public UserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}
	@Column(name = "locked")
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Get the username of the user.
	 * 
	 * @return The username of the user.
	 */
	@Column(name = "username")
	public String getUsername() {
		return username;
	}

	/**
	 * Get the password of the user. return The password of the user.
	 */
	@Column(name = "password")
	public String getPassword() {
		return password;
	}

	/**
	 * @return the roles
	 */
	public final Set<Role> getRoles() {
		return roles;
	}

	public Set<User> getUsers() {
		return users;
	}

	/**
	 * Return all user's roles, even his groups' roles
	 * 
	 * @return : A set containing all his roles
	 */
	public final Set<Role> getAllRoles() {
		Set<Role> allRoles = new HashSet<Role>();
		allRoles.addAll(roles);
		for (Group g : groups) {
			allRoles.addAll(g.getRoles());
		}
		return allRoles;
	}

	/**
	 * Return all user's permissions, even his groups' permissions
	 * 
	 * @return : A set containing all his permissions
	 */
	public final Set<Permission> getAllPermissions() {
		Set<Permission> allPermissions = new HashSet<Permission>();
		allPermissions.addAll(permissions);
		for (Group g : groups) {
			allPermissions.addAll(g.getPermissions());
		}
		return allPermissions;
	}

	/**
	 * Get the salt of the user
	 * 
	 * @return the salt of the user
	 */
	@Column(name = "salt")
	public String getSalt() {
		return salt;
	}

	/**
	 * @return the managers
	 */
	public Set<User> getManagers() {
		return managers;
	}

	/**
	 * Set or replace the username of the user.
	 * 
	 * @param newUsername
	 *            The new username.
	 */
	public void setUsername(String newUsername) {
		this.username = newUsername;
	}

	/**
	 * Set or replace the password of the user.
	 * 
	 * @param newPassword
	 *            The new password.
	 */
	public void setPassword(String newPassword) {
		this.password = newPassword;
	}

	/**
	 * set or replace the salt of the user
	 * 
	 * @param newSalt
	 *            the new salt
	 */
	public void setSalt(String newSalt) {
		this.salt = newSalt;
	}

	public void setLocked(boolean lock) {
		this.locked = lock;
	}

	/**
	 * Set the id of the user
	 * 
	 * @param id
	 *            The id of the user.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the permissions
	 */
	public final Set<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions
	 *            the permissions to set
	 */
	public final void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public final void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	/**
	 * Gets the groups of the user
	 * 
	 * @return the groups
	 */
	public final Set<Group> getGroups() {
		return groups;
	}

	/**
	 * Sets the groups of the user
	 * 
	 * @param groups
	 *            the groups to set
	 */
	public final void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	/**
	 * Sets the managers of the users
	 * @param newManagers
	 */
	public void setManager(Set<User> newManagers) {
		this.managers = newManagers;
	}
		
	public void setUsers(Set<User> users) {
		this.users = users;
	}

	/**
	 * Verify that the user has one of the role list
	 * 
	 * @param : the list of roles
	 * @return boolean : true if the user has one of these roles
	 */
	public final boolean hasOneOf(String... rolesList) {
		for (Role r : this.getAllRoles()) {
			for (String s : rolesList) {
				if (r.getName().equals(s)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Verify that the user has one of the roles in the set.
	 * 
	 * @param the
	 *            set of roles
	 * @return boolean true if the user has one of these roles
	 */
	public final boolean hasOneOf(Set<String> rolesSet) {
		for (Role r : this.getAllRoles()) {
			for (String s : rolesSet) {
				if (r.getName().equals(s)) {
					return true;
				}
			}
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (id != other.id) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

}
