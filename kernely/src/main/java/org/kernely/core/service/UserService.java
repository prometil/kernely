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
package org.kernely.core.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.UnauthorizedException;
import org.kernely.core.dto.ManagerDTO;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.dto.UserDetailsUpdateRequestDTO;
import org.kernely.core.event.UserCreationEvent;
import org.kernely.core.event.UserLockedEvent;
import org.kernely.core.model.Role;
import org.kernely.core.model.User;
import org.kernely.core.model.UserDetails;
import org.kernely.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * Service provided by the user plugin.
 */
@Singleton
public class UserService extends AbstractService {

	private static Logger log = LoggerFactory.getLogger(UserService.class);

	@Inject
	private EventBus eventBus;

	@Inject
	private PasswordService passwordService;

	/**
	 * Create a new user in database.
	 * 
	 * @param request
	 *            The request, containing user data : password, username...
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public UserDTO createUser(UserCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if ("".equals(request.username) || "".equals(request.password)) {
			throw new IllegalArgumentException("Username or/and password cannot be null ");
		}

		if ("".equals(request.username.trim()) || "".equals(request.password.trim())) {
			throw new IllegalArgumentException("Username or/and password cannot be space character only ");
		}

		Query verifExist = em.get().createQuery("SELECT u FROM User u WHERE username=:username");
		verifExist.setParameter("username", request.username);
		List<User> list = (List<User>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Another user with this username already exists");
		}

		User user = new User();
		user.setUsername(request.username.trim());

		// Retrieve the role User, automatically given to a user.

		Query query = em.get().createQuery("SELECT r FROM Role r WHERE name = :role_user");
		query.setParameter("role_user", Role.ROLE_USER);
		Role roleUser = (Role) query.getSingleResult();

		user.setPassword(passwordService.encryptPassword(request.password));
		Set<Role> roles = new HashSet<Role>();
		roles.add(roleUser);
		user.setRoles(roles);

		UserDetails userdetails = new UserDetails();
		userdetails.setName(request.lastname);
		userdetails.setFirstname(request.firstname);
		userdetails.setHire(request.hire);
		userdetails.setUser(user);
		userdetails.setImage("default_profile_user.png");

		user.setUserDetails(userdetails);
		em.get().persist(user);
		em.get().persist(userdetails);

		eventBus.post(new UserCreationEvent(user.getId(), user.getUsername()));

		// Return a DTO of the new user created
		UserDTO userDTO = new UserDTO(user);
		userDTO.userDetails = new UserDetailsDTO(userdetails);
		return userDTO;

	}

	/**
	 * Update the profile of the specific user with the informations contained
	 * in the DTO
	 * 
	 * @param u
	 *            The DTO containing all informations about the user to update
	 */
	@Transactional
	public UserDetailsDTO updateUserProfile(UserDetailsUpdateRequestDTO u) {
		if (u == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}
		
		// parse the string date in class Date
		String date = u.birth;
		if (u.birth == null || u.birth.equals("")) {
			date = "00/00/0000";
		}
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date birth = (Date) formatter.parse(date);
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
			if(u.image != null && !u.image.equals("")){
				uDetails.setImage(u.image);
			}
			return new UserDetailsDTO(uDetails);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Get the user model from his username
	 * 
	 * @param username
	 * @return user model
	 */
	@Transactional
	public User getUserByUsername(String username) {
		Query query = em.get().createQuery("SELECT u FROM User u WHERE username=:username");
		query.setParameter("username", username);
		return (User) query.getSingleResult();
	}

	/**
	 * Lock the user who has the id 'id'
	 * 
	 * @param id
	 *            The id of the user to lock
	 */
	@Transactional
	public void lockUser(long id) {
		UserDetails ud = em.get().find(UserDetails.class, id);
		User u = em.get().find(User.class, ud.getUser().getId());
		u.setLocked(!u.isLocked());
		UserLockedEvent ule = new UserLockedEvent(u.getId(), u.getUsername(), u.isLocked());
		ule.setUser(u);
		eventBus.post(ule);
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

		User u = em.get().find(User.class, ud.getUser().getId());

		ud.setFirstname(request.firstname);
		ud.setName(request.lastname);
		ud.setHire(request.hire);
		u.setUsername(request.username);

		// Retrieve the role User, automatically given to a user.
		Query query = em.get().createQuery("SELECT r FROM Role r WHERE name=:role");
		query.setParameter("role", Role.ROLE_USER);
		Role roleUser = (Role) query.getSingleResult();

		Set<Role> roles = new HashSet<Role>();
		if (!request.roles.isEmpty() && (!(request.roles.get(0).name == null))) {
			for (RoleDTO r : request.roles) {
				roles.add(em.get().find(Role.class, r.id));
				log.debug("User {} has now role {}", request.username, r.id);
			}
		}
		// Add the user Role.
		roles.add(roleUser);
		u.setRoles(roles);

		em.get().merge(u);
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
			dtos.add(new UserDTO(user));
		}
		return dtos;
	}

	/**
	 * Gets the lists of all clients contained in the database.
	 * 
	 * @return the list of all clients contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<UserDetailsDTO> getAllClients() {
		Query query = em.get().createQuery("SELECT e FROM User e");
		List<User> collection = (List<User>) query.getResultList();
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for (User user : collection) {
			for (Role role : user.getAllRoles()) {
				if (role.getName().equals(Role.ROLE_CLIENT)) {
					dtos.add(new UserDTO(user.getUsername(), user.isLocked(), user.getId()));
				}
			}
		}
		List<UserDetailsDTO> userDetailsDTO = new ArrayList<UserDetailsDTO>();
		for (UserDTO user : dtos) {
			userDetailsDTO.add(this.getUserDetails(user.username));
		}
		return userDetailsDTO;
	}

	/**
	 * Gets the lists of all users contained in the database.
	 * 
	 * @return the list of all users contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<UserDTO> getEnabledUsers() {
		Query query = em.get().createQuery("SELECT u FROM User u WHERE locked = false");
		List<User> collection = (List<User>) query.getResultList();
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for (User user : collection) {
			UserDTO udto = new UserDTO(user.getUsername(), user.isLocked(), user.getId());
			udto.name = user.getUserDetails().getName();
			udto.firstname = user.getUserDetails().getFirstname();
			dtos.add(udto);
		}
		return dtos;

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
		Query query = em.get().createQuery("SELECT e FROM User  e WHERE username=:login");
		query.setParameter("login", login);
		User u = (User) query.getSingleResult();
		query = em.get().createQuery("SELECT e FROM UserDetails e , User u WHERE e.user = u AND u.id =:id");
		query.setParameter("id", u.getId());
		UserDetails ud = (UserDetails) query.getSingleResult();
		return new UserDetailsDTO(ud);
	}

	/**
	 * Get details by id.
	 * 
	 * @param id
	 *            The id of the userDetails
	 * @return The DTO associated to this details
	 */
	@Transactional
	public UserDetailsDTO getUserDetails(long userDetailsId) {
		Query query = em.get().createQuery("SELECT e FROM UserDetails e WHERE id=:detailsId");
		query.setParameter("detailsId", userDetailsId);
		UserDetails ud = (UserDetails) query.getSingleResult();
		return new UserDetailsDTO(ud);
	}

	/**
	 * Get the current user authenticated in the application
	 * 
	 * @return The DTO associated to the current user
	 */
	@Transactional
	public UserDTO getAuthenticatedUserDTO() {
		log.debug("Looking for {}", SecurityUtils.getSubject().getPrincipal());
		Query query = em.get().createQuery("SELECT e FROM User e WHERE username =:principal");
		query.setParameter("principal", SecurityUtils.getSubject().getPrincipal());
		User u = (User) query.getSingleResult();
		return new UserDTO(u.getUsername(), u.isLocked(), u.getId());
	}

	/**
	 * Get all Details about all users in the database
	 * 
	 * @return A list of DTO associated to all details stored in the database
	 */
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
	 * Get all Details about all users enabled in the database
	 * 
	 * @return A list of DTO associated to all details stored in the database
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public List<UserDetailsDTO> getEnabledUserDetails() {
		Query query = em.get().createQuery("SELECT e FROM UserDetails e, User u WHERE e.user.id = u.id and u.locked = false");
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
	 * @return true if the current user has the role of administrator, false
	 *         otherwise.
	 */
	public boolean currentUserIsAdministrator() {
		return SecurityUtils.getSubject().hasRole(Role.ROLE_ADMINISTRATOR);
	}

	/**
	 * Verify if the current user has the role of human resource
	 * 
	 * @return true if the current user has the role of human resource, false
	 *         otherwise.
	 */
	public boolean currentUserIsHumanResource() {
		return SecurityUtils.getSubject().hasRole(Role.ROLE_HUMANRESOURCE);
	}

	/**
	 * Verify if the current user has the role of project manager
	 * 
	 * @return true if the current user has the role of project manager, false
	 *         otherwise.
	 */
	public boolean currentUserIsProjectManager() {
		return SecurityUtils.getSubject().hasRole(Role.ROLE_PROJECTMANAGER);
	}

	/**
	 * Verify if the current user has the role of client
	 * 
	 * @return true if the current user has the role of client, false otherwise.
	 */
	public boolean currentUserIsClient() {
		return SecurityUtils.getSubject().hasRole(Role.ROLE_CLIENT);
	}

	public boolean currentUserHasRole(String role) {
		return SecurityUtils.getSubject().hasRole(role);
	}

	/**
	 * Retrieve the list of RoleDTO from an userdetails id
	 * 
	 * @param id
	 *            of <u>userDetails</u>
	 * @return list of RoleDTO
	 */
	@Transactional
	public List<RoleDTO> getUserRoles(long id) {
		UserDetails ud = em.get().find(UserDetails.class, id);

		Query query = em.get().createQuery("SELECT r FROM Role r WHERE name=:role");
		query.setParameter("role", Role.ROLE_USER);
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
	 * Adds a role with the given name to the user with the given id
	 * 
	 * @param userId
	 *            Id of the concerned user
	 * @param role
	 *            Name of the concerned role
	 */
	@Transactional
	public void addRoleToUser(long userId, String role) {
		User user = em.get().find(User.class, userId);
		Query roleQuery = em.get().createQuery("SELECT r FROM Role r WHERE name = :roleName");
		roleQuery.setParameter("roleName", role);
		try {
			Role r = (Role) roleQuery.getSingleResult();
			Set<Role> roles = user.getRoles();
			roles.add(r);
			user.setRoles(roles);
			em.get().merge(user);
		} catch (NoResultException nre) {
			log.debug("There is no role existing with the name {} !", role);
		}
	}

	/**
	 * Removes the role with the given name to the user with the given id
	 * 
	 * @param userId
	 *            Id of the concerned user
	 * @param role
	 *            Name of the concerned role
	 */
	@Transactional
	public void removeRoleToUser(long userId, String role) {
		User user = em.get().find(User.class, userId);
		Query roleQuery = em.get().createQuery("SELECT r FROM Role r WHERE name = :roleName");
		roleQuery.setParameter("roleName", role);
		try {
			Role r = (Role) roleQuery.getSingleResult();
			Set<Role> roles = user.getRoles();
			roles.remove(r);
			user.setRoles(roles);
			em.get().merge(user);
		} catch (NoResultException nre) {
			log.debug("There is no role existing with the name {} !", role);
		}
	}

	/**
	 * Get all users managed by a manager.
	 * 
	 * @param The
	 *            username of the manager
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
	 * Get the manager and his users
	 * 
	 * @param the
	 *            id of the manager to get
	 * @return The manager DTO, containing his users
	 */
	public ManagerDTO getManager(long id) {
		Query query = em.get().createQuery("Select u FROM User u WHERE u.id=:id");
		query.setParameter("id", id);
		User m = (User) query.getSingleResult();
		ManagerDTO manager = new ManagerDTO(id, m.getUsername(), this.getUsers(m.getUsername()));
		return manager;
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
		// If we update with an empty list, we delete the manager
		if (list.get(0).id == 0 || list.size() == 0) {
			deleteManager(manager);
			return;
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

		Set<User> ancientManaged = userManager.getUsers();
		if (ancientManaged != null) {
			for (User user : ancientManaged) {
				user.getManagers().remove(userManager);
			}
		}

		// add the manager for each user
		for (User user : users) {
			Set<User> temp = new HashSet<User>();
			if (user.getManagers() != null) {
				temp = user.getManagers();
			}
			temp.add(userManager);
			user.setManager(temp);
			em.get().merge(user);
		}

		// add the new users
		userManager.setUsers(users);
		
		if(users.size()>0){
			this.addRoleToUser(userManager.getId(), Role.ROLE_USERMANAGER);
		}
		
		em.get().merge(userManager);
	}

	/**
	 * Delete an existing manager in database
	 * 
	 * @param id
	 *            The id of the manager to delete
	 */
	@Transactional
	public void deleteManager(long id) {
		Query query = em.get().createQuery("Select u FROM User u WHERE u.id=:id");
		query.setParameter("id", id);
		User userManager = (User) query.getSingleResult();
		Set<User> managed = userManager.getUsers();
		userManager.setUsers(new HashSet<User>());
		this.removeRoleToUser(userManager.getId(), Role.ROLE_USERMANAGER);
		for (User user : managed) {
			user.getManagers().remove(userManager);
			em.get().merge(user);
		}
	}

	/**
	 * Delete an existing manager in database
	 * 
	 * @param id
	 *            The username of the manager to delete
	 */
	@Transactional
	public void deleteManager(String username) {
		Query query = em.get().createQuery("Select u FROM User u WHERE u.username=:username");
		query.setParameter("username", username);
		User userManager = (User) query.getSingleResult();
		Set<User> managed = userManager.getUsers();
		this.removeRoleToUser(userManager.getId(), Role.ROLE_USERMANAGER);
		userManager.setUsers(new HashSet<User>());
		for (User user : managed) {
			user.getManagers().remove(userManager);
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
		Query query = em.get().createQuery("SELECT DISTINCT u.managers FROM User u");
		List<User> idManager = (List<User>) query.getResultList();
		Set<ManagerDTO> collection = new HashSet<ManagerDTO>();
		Set<User> users;
		ArrayList<UserDTO> usersList;
		for (User manager : idManager) {
			users = manager.getUsers();
			usersList = new ArrayList<UserDTO>();
			for (User user : users) {
				usersList.add(new UserDTO(user.getUsername(), user.getId()));
			}
			collection.add(new ManagerDTO(manager.getId(), manager.getUsername(), usersList));
		}
		return collection;
	}

	/**
	 * Verify if the user is a manager
	 * 
	 * @param user
	 *            String username
	 * @return true if the user is a manager
	 */
	@Transactional
	public boolean isManager(String user) {
		Set<String> allManagerUsername = new HashSet<String>();
		for (ManagerDTO manager : this.getAllManager()) {
			allManagerUsername.add(manager.name);
		}
		if (allManagerUsername.contains(user)) {
			return true;
		}
		return false;
	}

	/**
	 * Retrieve all users managed by the current user authorized to access the
	 * application
	 * 
	 * @return A set of DTO according to all user authorized managed
	 */
	@Transactional
	public Set<UserDTO> getUsersAuthorizedManaged() {
		if (!this.isManager(this.getAuthenticatedUserModel().getUsername())) {
			throw new UnauthorizedException("Only managers can access to this functionality!");
		}
		Set<UserDTO> managedCleaned = new HashSet<UserDTO>();
		for (User u : this.getAuthenticatedUserModel().getUsers()) {
			if (!u.isLocked()) {
				managedCleaned.add(new UserDTO(u));
			}
		}
		return managedCleaned;
	}

	/**
	 * Get all users that manage the user.
	 * 
	 * @param username
	 *            of the user
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<UserDTO> getManagers(String username) {
		Query query = em.get().createQuery("SELECT u.managers FROM User u WHERE u.username=:username");
		query.setParameter("username", username);

		List<User> managers = (List<User>) query.getResultList();

		List<UserDTO> managersDTO = new ArrayList<UserDTO>();

		for (User manager : managers) {
			managersDTO.add(new UserDTO(manager));
		}

		return managersDTO;
	}

	/**
	 * Return all the roles of the current user
	 * @return a non-null set of roles, which coul be empty
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getCurrentUserRoles() {
		if(SecurityUtils.getSubject().getPrincipal() == null){
			return Collections.EMPTY_SET;
		}
		Query query = em.get().createQuery("Select u FROM User u WHERE u.username=:name");
		query.setParameter("name", SecurityUtils.getSubject().getPrincipal());
		User user = (User) query.getSingleResult();

		Set<String> roles = new HashSet<String>();
		for (Role role : user.getAllRoles()) {
			roles.add(role.getName());
		}
		return roles;
	}

}
