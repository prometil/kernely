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
package org.kernely.core.service.user;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.dto.UserDetailsUpdateRequestDTO;
import org.kernely.core.event.UserCreationEvent;
import org.kernely.core.model.Role;
import org.kernely.core.model.User;
import org.kernely.core.model.UserDetails;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * Service provided by the user plugin.
 */
@Singleton
public class UserService {

	@Inject
	private Provider<EntityManager> em;

	@Inject
	private EventBus eventBus;

	/**
	 * Create a new user in database.
	 * 
	 * @param request
	 *            The request, containing user data : passwod, username...
	 */
	@Transactional
	public void createUser(UserCreationRequestDTO request) {
		if(request==null){
			throw new IllegalArgumentException("Request cannot be null ");
		}
		
		if("".equals(request.username) || "".equals(request.password)){
			throw new IllegalArgumentException("Username or/and password cannot be null ");
		}
		
		if("".equals(request.username.trim()) || "".equals(request.password.trim())){
			throw new IllegalArgumentException("Username or/and password cannot be space character only ");
		}
		User user = new User();
		//user.setPassword(request.password.trim());
		user.setUsername(request.username.trim());
		
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		Object salt = rng.nextBytes();
		
		//Now hash the plain-text password with the random salt and multiple
		//iterations and then Base64-encode the value (requires less space than Hex):
		String hashedPasswordBase64 = new Sha256Hash(request.password.trim(), salt, 1024).toBase64();

		user.setPassword(hashedPasswordBase64);
		user.setSalt(salt.toString()); 
		
		em.get().persist(user);
		
		UserDetails userdetails = new UserDetails();
		userdetails.setName(request.lastname);
		userdetails.setFirstname(request.firstname);
		userdetails.setUser(user);
		
		em.get().persist(userdetails);
		eventBus.post(new UserCreationEvent(user.getId(), user.getUsername()));

	}
	
	@Transactional
	public void updateUserProfile(UserDetailsUpdateRequestDTO u)
	{ 
		if(u==null){
			throw new IllegalArgumentException("Request cannot be null ");
		}
		if(u.birth.equals("")){
			throw new IllegalArgumentException("A birth date must be like dd/MM/yyyy ");
		}
		
		// parse the string date in class Date
		String date =u.birth;
		if (u.birth==null){
			date = "00/00/0000";
		}
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date birth;
		try {
			birth = (Date)formatter.parse(date);
			UserDetails uDetails =  em.get().find(UserDetails.class, u.id);
			uDetails.setMail(u.mail);
			uDetails.setAdress(u.adress);
			uDetails.setBirth(birth);
			uDetails.setBusinessphone(u.businessphone);
			uDetails.setCity(u.city);
			uDetails.setFirstname(u.firstname);
			uDetails.setHomephone(u.homephone);
			uDetails.setMobilephone(u.mobilephone);
			uDetails.setName(u.name);
			uDetails.setNationality(u.nationality);
			uDetails.setSsn(u.ssn);
			uDetails.setZip(u.zip);
			uDetails.setCivility(u.civility);
			uDetails.setImage(u.image);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Transactional 
	public void lockUser(int id){
		UserDetails ud = em.get().find(UserDetails.class, id);
		User u = em.get().find(User.class, ud.getUser().getId());
		u.setLocked(!u.isLocked());
	}
	
	@Transactional
	public void updateUser(UserCreationRequestDTO request) {
		if(request==null){
			throw new IllegalArgumentException("Request cannot be null ");
		}
		
		if("".equals(request.username)){
			throw new IllegalArgumentException("Username cannot be null ");
		}
		
		if("".equals(request.username.trim())){
			throw new IllegalArgumentException("Username cannot be space character only ");
		}
		
		UserDetails ud = em.get().find(UserDetails.class, request.id);
		ud.setFirstname(request.firstname);
		ud.setName(request.lastname);
		User u = em.get().find(User.class, ud.getUser().getId());
		u.setUsername(request.username);
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
			dtos.add(new UserDTO(user.getUsername(), user.isLocked()));
		}
		return dtos;

	}
	
	@Transactional
	public UserDetailsDTO getUserDetails(User u) {
		Query query = em.get().createQuery("SELECT e FROM UserDetails, User WHERE User.id =" + u.getId());
		UserDetails ud = (UserDetails)query.getResultList().get(0);

		/// handle date
		String newDateString;
		if (ud.getBirth()!=null){
			Date date =ud.getBirth();
			String newPattern = "dd/MM/yyyy" ; 
			newDateString = (new SimpleDateFormat( newPattern )).format( date ) ; 
		}
		else{
			newDateString="00/00/0000"; 
		}
		UserDetailsDTO dto = new UserDetailsDTO(ud.getFirstname(), ud.getName(), ud.getImage(), ud.getMail(), ud.getAdress(), ud.getZip(), ud.getCity(), ud.getHomephone(), ud.getMobilephone(), ud.getBusinessphone(), newDateString, ud.getNationality(), ud.getSsn(),ud.getCivility(),ud.getId_user_detail(), new UserDTO(u.getUsername(), u.isLocked()));
		return dto;

	}
	
	@Transactional
	public UserDetailsDTO getUserDetails(String login) {
		Query query = em.get().createQuery("SELECT e FROM User  e WHERE username ='" + login + "'");
		User u = (User)query.getSingleResult();
		query = em.get().createQuery("SELECT e FROM UserDetails e , User u WHERE e.user = u AND u.id =" + u.getId());
		UserDetails ud = (UserDetails)query.getSingleResult();

		/// handle date
		String newDateString;
		if (ud.getBirth()!=null){
			Date date =ud.getBirth();
			String newPattern = "dd/MM/yyyy" ; 
			newDateString = (new SimpleDateFormat( newPattern )).format( date ) ; 
		}
		else{
			newDateString="00/00/0000"; 
		}
		UserDetailsDTO dto = new UserDetailsDTO( ud.getFirstname(), ud.getName(), ud.getImage(), ud.getMail(), ud.getAdress(), ud.getZip(), ud.getCity(), ud.getHomephone(), ud.getMobilephone(), ud.getBusinessphone(), newDateString, ud.getNationality(), ud.getSsn(),ud.getCivility(),ud.getId_user_detail(),new UserDTO(u.getUsername(), u.isLocked()));

		return dto;

	}
	
	@Transactional
	public UserDTO getCurrentUser(){
		Query query = em.get().createQuery("SELECT e FROM User e WHERE username ='"+ SecurityUtils.getSubject().getPrincipal() +"'");
		User u = (User)query.getSingleResult();
		return new UserDTO(u.getUsername(), u.isLocked());
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public List<UserDetailsDTO> getAllUserDetails(){
		Query query = em.get().createQuery("SELECT e FROM UserDetails e");
		List<UserDetails> collection = (List<UserDetails>) query.getResultList();
		List<UserDetailsDTO> dtos = new ArrayList<UserDetailsDTO>();
		for (UserDetails user : collection) {
			dtos.add(new UserDetailsDTO( user.getFirstname(), user.getName(), user.getImage(), user.getMail(), user.getAdress(), user.getZip(), user.getCity(), user.getHomephone(), user.getMobilephone(), user.getBusinessphone(), null, user.getNationality(), user.getSsn(),user.getCivility(),user.getId_user_detail(),new UserDTO(user.getUser().getUsername(), user.getUser().isLocked())));

		}
		return dtos;
	}
	
	/**
	 * Verify if the current user has the role of administrator.
	 * @return true if the current user has the role of administrator, false otherwise.
	 */
	@Transactional
	public boolean currentUserIsAdministrator(){
		return SecurityUtils.getSubject().hasRole(Role.ROLE_ADMINISTRATOR);
	}
}
