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
package org.kernely.core.service.user;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.kernely.core.dto.ManagerDTO;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.dto.UserDetailsUpdateRequestDTO;
import org.kernely.core.event.UserCreationEvent;
import org.kernely.core.model.Role;
import org.kernely.core.model.User;
import org.kernely.core.model.UserDetails;
import org.kernely.core.service.AbstractService;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * Service provided by the user plugin.
 */
@Singleton
public class UserService extends AbstractService {

	@Inject
	private EventBus eventBus;

	/**
	 * Create a new user in database.
	 * 
	 * @param request
	 *            The request, containing user data : password, username...
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void createUser(UserCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if ("".equals(request.username) || "".equals(request.password)) {
			throw new IllegalArgumentException("Username or/and password cannot be null ");
		}

		if ("".equals(request.username.trim()) || "".equals(request.password.trim())) {
			throw new IllegalArgumentException("Username or/and password cannot be space character only ");
		}

		Query verifExist = em.get().createQuery("SELECT u FROM User u WHERE username='" + request.username + "'");
		List<User> list = (List<User>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Another user with this username already exists");
		}

		User user = new User();
		user.setUsername(request.username.trim());

		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		Object salt = rng.nextBytes();

		// Now hash the plain-text password with the random salt and multiple
		// iterations and then Base64-encode the value (requires less space than
		// Hex):
		String hashedPasswordBase64 = new Sha256Hash(request.password.trim(), salt, 1024).toBase64();

		// Retrieve the role User, automatically given to a user.

		Query query = em.get().createQuery("SELECT r FROM Role r WHERE name=:role_user");
		query.setParameter("role_user", Role.ROLE_USER);
		Role roleUser = (Role) query.getSingleResult();

		user.setPassword(hashedPasswordBase64);
		user.setSalt(salt.toString());
		Set<Role> roles = new HashSet<Role>();
		roles.add(roleUser);
		user.setRoles(roles);

		UserDetails userdetails = new UserDetails();
		userdetails.setName(request.lastname);
		userdetails.setFirstname(request.firstname);
		userdetails.setUser(user);

		user.setUserDetails(userdetails);

		em.get().persist(userdetails);
		em.get().persist(user);

		eventBus.post(new UserCreationEvent(user.getId(), user.getUsername()));

	}

	/**
	 * Update the profile of the specific user with the informations contained in the DTO
	 * 
	 * @param u
	 *            The DTO containing all informations about the user to update
	 */
	@Transactional
	public UserDetailsDTO updateUserProfile(UserDetailsUpdateRequestDTO u) {
		if (u == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}
		if (u.birth != null) {
			if (u.birth.equals("")) {
				throw new IllegalArgumentException("A birth date must be like dd/MM/yyyy ");
			}
		}

		// parse the string date in class Date
		String date = u.birth;
		if (u.birth == null) {
			date = "00/00/0000";
		}
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date birth;
		try {
			birth = (Date) formatter.parse(date);
			UserDetails uDetails = em.get().find(UserDetails.class, u.id);
			uDetails.setMail(u.email);
			uDetails.setAdress(u.adress);
			uDetails.setBirth(birth);
			uDetails.setBusinessphone(u.businessphone);
			uDetails.setCity(u.city);
			uDetails.setFirstname(u.firstname);
			uDetails.setHomephone(u.homephone);
			uDetails.setMobilephone(u.mobilephone);
			uDetails.setName(u.lastname);
			uDetails.setNationality(u.nationality);
			uDetails.setSsn(u.ssn);
			uDetails.setZip(u.zip);
			uDetails.setCivility(u.civility);
			uDetails.setImage(u.image);
			return new UserDetailsDTO(uDetails);
		} catch (ParseException e) {
			birth = new Date();
			return new UserDetailsDTO();
		}
	}

	/**
	 * Lock the user who has the id 'id'
	 * 
	 * @param id
	 *            The id of the user to lock
	 */
	@Transactional
	public void lockUser(int id) {
		UserDetails ud = em.get().find(UserDetails.class, id);
		User u = em.get().find(User.class, ud.getUser().getId());
		u.setLocked(!u.isLocked());
	}

	/**
	 * Update an user from the administration
	 * 
	 * @param request
	 *            The DTO containing all informations about the user to update
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void updateUser(UserCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if ("".equals(request.username)) {
			throw new IllegalArgumentException("Username cannot be null ");
		}

		if ("".equals(request.username.trim())) {
			throw new IllegalArgumentException("Username cannot be space character only ");
		}

		// Retrieve the updated user
		UserDetails ud = em.get().find(UserDetails.class, request.id);

		Query verifExist = em.get().createQuery("SELECT u FROM User u WHERE username='" + request.username + "' AND id != " + ud.getUser().getId());
		List<User> list = (List<User>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Another user with this username already exists");
		}

		// Retrieve the role User, automatically given to a user.
		Query query = em.get().createQuery("SELECT r FROM Role r WHERE name='" + Role.ROLE_USER + "'");
		Role roleUser = (Role) query.getSingleResult();

		Set<Role> roles = new HashSet<Role>();
		if (!request.roles.isEmpty()) {
			if (!(request.roles.get(0).name == null)) {
				for (RoleDTO r : request.roles) {
					roles.add(em.get().find(Role.class, r.id));
				}
			}
		}
		// Add the user Role.
		roles.add(roleUser);

		ud.setFirstname(request.firstname);
		ud.setName(request.lastname);
		User u = em.get().find(User.class, ud.getUser().getId());
		u.setUsername(request.username);

		u.setRoles(roles);
	}

	/**
	 * Gets the lists of all users contained in the database.
	 * 
	 * @return the list of all users contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<UserDTO> getAllUsers() {
		Query query = em.get().createQuery("SELECT e FROM User e");
		List<User> collection = (List<User>) query.getResultList();
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for (User user : collection) {
			dtos.add(new UserDTO(user.getUsername(), user.isLocked(), user.getId()));
		}
		return dtos;

	}

	/**
	 * Get the details about the user specified
	 * 
	 * @param u
	 *            The User that details are needed
	 * @return The DTO associated to the details of this user
	 */
	@Transactional
	public UserDetailsDTO getUserDetails(User u) {
		Query query = em.get().createQuery("SELECT e FROM UserDetails, User WHERE User.id =" + u.getId());
		UserDetails ud = (UserDetails) query.getResultList().get(0);

		UserDetailsDTO dto = new UserDetailsDTO(ud);

		return dto;

	}

	/**
	 * Get the details about the user who has the specified login
	 * 
	 * @param login
	 *            The login of the user that details are needed
	 * @return The DTO associated to the details of this user
	 */
	@Transactional
	public UserDetailsDTO getUserDetails(String login) {
		Query query = em.get().createQuery("SELECT e FROM User  e WHERE username ='" + login + "'");
		User u = (User) query.getSingleResult();
		query = em.get().createQuery("SELECT e FROM UserDetails e , User u WHERE e.user = u AND u.id =" + u.getId());
		UserDetails ud = (UserDetails) query.getSingleResult();

		UserDetailsDTO dto = new UserDetailsDTO(ud);
		return dto;

	}

	/**
	 * Get the current user authenticated in the application
	 * 
	 * @return The DTO associated to the current user
	 */
	@Transactional
	public UserDTO getAuthenticatedUserDTO() {
		Query query = em.get().createQuery("SELECT e FROM User e WHERE username ='" + SecurityUtils.getSubject().getPrincipal() + "'");
		User u = (User) query.getSingleResult();
		return new UserDTO(u.getUsername(), u.isLocked(), u.getId());
	}

	/**
	 * Get all Details about all users in the database
	 * 
	 * @return A list of DTO associated to all details stored in the database
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public List<UserDetailsDTO> getAllUserDetails() {
		Query query = em.get().createQuery("SELECT e FROM UserDetails e");
		List<UserDetails> collection = (List<UserDetails>) query.getResultList();
		List<UserDetailsDTO> dtos = new ArrayList<UserDetailsDTO>();
		for (UserDetails user : collection) {
			dtos.add(new UserDetailsDTO(user));
		}
		return dtos;
	}

	/**
	 * Verify if the current user has the role of administrator.
	 * 
	 * @return true if the current user has the role of administrator, false otherwise.
	 */
	public boolean currentUserIsAdministrator() {
		return SecurityUtils.getSubject().hasRole(Role.ROLE_ADMINISTRATOR);
	}

	@Transactional
	public List<RoleDTO> getUserRoles(int id) {
		UserDetails ud = em.get().find(UserDetails.class, id);

		Query query = em.get().createQuery("SELECT r FROM Role r WHERE name='" + Role.ROLE_USER + "'");
		Role roleUser = (Role) query.getSingleResult();

		List<RoleDTO> dtos = new ArrayList<RoleDTO>();
		Set<Role> userRoles = ud.getUser().getRoles();
		userRoles.remove(roleUser);

		for (Role role : userRoles) {
			dtos.add(new RoleDTO(role.getId(), role.getName()));
		}

		return dtos;
	}

	/**
	 * retrieve an users list belong to the managers.
	 * 
	 * @param managerId
	 * @return the users
	 */
	@Transactional
	public List<UserDTO> getUsers(String manager) {
		if (manager == null) {
			throw new IllegalArgumentException("The manager cannot be null");
		}
		if (manager.equals("")) {
			throw new IllegalArgumentException("Manager cannot be an empty string");
		}
		Query query = em.get().createQuery("Select u FROM User u WHERE u.username=:manager");
		query.setParameter("manager", manager);
		User userManager = (User) query.getSingleResult();
		Set<User> usersSet = userManager.getUsers();
		List<UserDTO> users = new ArrayList<UserDTO>();
		for (User user : usersSet) {
			users.add(new UserDTO(user.getUsername(), user.getId()));
		}
		return users;
	}

	/**
	 * Update all user of the list with the new manager
	 * 
	 * @param manager
	 * @param list
	 */
	@Transactional
	public void updateManager(String manager, List<UserDTO> list) {
		if (manager == null) {
			throw new IllegalArgumentException("The manager cannot be null");
		}
		if (list == null) {
			throw new IllegalArgumentException("The list cannot be null");
		}
		if (manager.equals("")) {
			throw new IllegalArgumentException("Manager cannot be an empty string");
		}

		Set<User> users = new HashSet<User>();
		Query query = em.get().createQuery("Select u FROM User u WHERE u.username=:manager");
		query.setParameter("manager", manager);
		User userManager = (User) query.getSingleResult();

		// retrieve the id and the Users managed
		for (UserDTO user : list) {
			Long idparam = user.id;
			Query query2 = em.get().createQuery("Select u FROM User u WHERE u.id=:idparam");
			query2.setParameter("idparam", idparam);
			User userManaged = (User) query2.getSingleResult();
			users.add(userManaged);
		}
		// add the new users
		userManager.setUsers(users);
		// add the manager for each user
		for (User user : users) {
			user.setManager(userManager);
			em.get().merge(user);
		}
	}

	/**
	 * Delete an existing manager in database
	 * 
	 * @param id
	 *            The id of the group to delete
	 */
	@Transactional
	public void deleteManager(String username) {
		if (username == null) {
			throw new IllegalArgumentException("The manager cannot be null");
		}
		if (username.equals("")) {
			throw new IllegalArgumentException("Manager cannot be an empty string");
		}
		Query query = em.get().createQuery("Select u FROM User u WHERE u.username=:username");
		query.setParameter("username", username);
		User userManager = (User) query.getSingleResult();
		Set<User> managed = userManager.getUsers();
		userManager.setUsers(new HashSet<User>());
		for (User user : managed) {
			user.setManager(null);
			em.get().merge(user);
		}

	}

	/**
	 * get all the manager of the data base
	 * 
	 * @return all the manager
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<ManagerDTO> getAllManager() {
		Query query = em.get().createQuery("SELECT u.manager FROM User u");
		List<User> idManager = (List<User>) query.getResultList();
		Set<ManagerDTO> collection = new HashSet<ManagerDTO>();
		for (User manager : idManager) {
			Set<User> users = manager.getUsers();
			ArrayList<UserDTO> usersList = new ArrayList<UserDTO>();
			for (User user : users) {
				usersList.add(new UserDTO(user.getUsername(), user.getId()));
			}
			collection.add(new ManagerDTO(manager.getUsername(), usersList));
		}
		return collection;
	}

}
