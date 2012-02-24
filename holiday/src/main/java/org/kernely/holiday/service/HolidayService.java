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

package org.kernely.holiday.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.core.service.user.UserService;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayProfileCreationRequestDTO;
import org.kernely.holiday.dto.HolidayProfileDTO;
import org.kernely.holiday.model.HolidayProfile;
import org.kernely.holiday.model.HolidayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The service for holiday types and profiles.
 */
@Singleton
public class HolidayService extends AbstractService {

	@Inject
	UserService userService;

	@Inject
	HolidayBalanceService balanceService;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Gets the lists of all groups contained in the database.
	 * 
	 * @return the list of all groups contained in the database.
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public List<HolidayDTO> getAllHoliday() {

		Query query = em.get().createQuery("SELECT e FROM HolidayType e ORDER BY Name");
		List<HolidayType> collection = (List<HolidayType>) query.getResultList();
		List<HolidayDTO> dtos = new ArrayList<HolidayDTO>();
		log.debug("HolidayService found {} holiday types", collection.size());
		for (HolidayType holiday : collection) {
			dtos.add(new HolidayDTO(holiday.getName(), holiday.isUnlimited(), holiday.getQuantity(), holiday.getPeriodUnit(), holiday.getId(),
					holiday.isAnticipated(), holiday.getEffectiveMonth(), holiday.getColor()));
			log.debug("Creation of Holiday Type {}", holiday.getName());
		}
		return dtos;
	}

	/**
	 * Gets the holiday DTO for the holiday type with the id passed in parameter.
	 * 
	 * @param id
	 *            The id of the holiday typ
	 * @return the holiday type dto.
	 */
	@Transactional
	public HolidayDTO getHolidayDTO(int id) {
		Query query = em.get().createQuery("SELECT  h from HolidayType h WHERE  h.id=:id");
		query.setParameter("id", id);
		HolidayType holiday = (HolidayType) query.getSingleResult();
		HolidayDTO hdto = new HolidayDTO(holiday.getName(), holiday.isUnlimited(), holiday.getQuantity(), holiday.getPeriodUnit(), holiday.getId(),
				holiday.isAnticipated(), holiday.getEffectiveMonth(), holiday.getColor());

		return hdto;
	}

	/**
	 * Delete an existing holiday in database
	 * 
	 * @param id
	 *            The id of the group to delete
	 */
	@Transactional
	public void deleteHoliday(int id) {
		HolidayType holiday = em.get().find(HolidayType.class, id);
		em.get().remove(holiday);
	}

	/**
	 * Create or update a new Holiday in database.
	 * If the id requested is 0, create a new type.
	 * If the id is not 0, update the type with the requested id.
	 * 
	 * @param request Request containing data to set.
	 * 
	 */
	@Transactional
	public HolidayDTO createOrUpdateHoliday(HolidayCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if (request.name == null) {
			throw new IllegalArgumentException("holiday type cannot be null ");
		}
		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("holiday type cannot be space character only ");
		}

		HolidayType holidayType;
		
		log.debug("Creation or update of Holiday Type. id:"+request.id+" name"+request.name+" qty "+request.quantity+" unlimited: "+request.unlimited+" anticipated:"+request.anticipation);
		
		int id = request.id;
		if (id == 0){
			// Create a new type
			holidayType = new HolidayType();
		} else {
			// Type is already in database
			Query verifExist = em.get().createQuery("SELECT g FROM HolidayType g WHERE id=:id");
			verifExist.setParameter("id", id);
			holidayType = (HolidayType) verifExist.getSingleResult();
		}

		holidayType.setName(request.name.trim());
		holidayType.setUnlimited(request.unlimited);
		if (!request.unlimited) { // These fields are useless if the type is unlimited
			holidayType.setQuantity(request.quantity);
			holidayType.setPeriodUnit(request.unity);
			holidayType.setEffectiveMonth(request.effectiveMonth);
			holidayType.setAnticipated(request.anticipation);
		}
		holidayType.setColor(request.color);

		if (id == 0){
			// Create
			em.get().persist(holidayType);
			log.debug("HolidayService: new holiday type created ({})",request.name);
		} else {
			// Update
			em.get().merge(holidayType);
			log.debug("HolidayService: holiday type updated ({})",request.name);
		}
		
		return new HolidayDTO(holidayType);
	}

	/**
	 * Get a holiday type with its name
	 * 
	 * @name The name of the holiday
	 */
	@Transactional
	public HolidayDTO getHolidayDTO(String holidayName) {
		Query verifExist = em.get().createQuery("SELECT h FROM HolidayType h WHERE name=:name");
		verifExist.setParameter("name", holidayName);

		HolidayType holiday = (HolidayType) verifExist.getSingleResult();
		return new HolidayDTO(holiday);
	}

	/**
	 * Create a new holiday profile in database. A holiday profile contains several holiday types.
	 * 
	 * @param request
	 * @return 
	 * 
	 */
	@Transactional
	public HolidayProfileDTO createOrUpdateHolidayProfile(HolidayProfileCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}
		
		if (request.name == null) {
			throw new IllegalArgumentException("holiday profile name cannot be null ");
		}

		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("holiday profile cannot be space character only ");
		}
		
		HolidayProfile profile;
		int id = request.id;
		if (id == 0){
			profile = new HolidayProfile();
		} else {
			Query verifExist = em.get().createQuery("SELECT hp FROM HolidayProfile hp WHERE id=:id");
			verifExist.setParameter("id", id);
			profile = (HolidayProfile) verifExist.getSingleResult();
			
			// Detach types from profile
			for (HolidayType type : profile.getHolidayTypes()){
				type.setProfile(null);
				em.get().merge(type);
			}
			
		}
		

		// Get all holiday types from this holiday profile
		Set<HolidayType> types = new HashSet<HolidayType>();
		for (int typeId : request.holidayTypesId) {
			Query query = em.get().createQuery("SELECT  h from HolidayType h WHERE  h.id=:id");
			query.setParameter("id", typeId);
			HolidayType holiday = (HolidayType) query.getSingleResult();
			types.add(holiday);
			log.debug("Added holiday type (id:{}) to holiday profile (name:{})",holiday.getId(),request.name.trim());
		}

		profile.setName(request.name.trim());
		profile.setHolidayTypes(types);

		if (request.id == 0){
			em.get().persist(profile);
		} else {
			em.get().merge(profile);
		}
		
		// Update holiday types
		for (int typeId : request.holidayTypesId) {
			Query query = em.get().createQuery("SELECT  h from HolidayType h WHERE  h.id=:id");
			query.setParameter("id", typeId);
			HolidayType holiday = (HolidayType) query.getSingleResult();
			holiday.setProfile(profile);
			em.get().merge(holiday);
		}
		
		return new HolidayProfileDTO(profile);
	}

	/**
	 * Get all holiday profiles, containing holiday types.
	 * @return all holiday profiles
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public List<HolidayProfileDTO> getAllProfiles() {
		Query query = em.get().createQuery("SELECT hp FROM HolidayProfile hp ORDER BY Name");
		List<HolidayProfile> collection = (List<HolidayProfile>) query.getResultList();
		List<HolidayProfileDTO> profiles = new ArrayList<HolidayProfileDTO>();
		log.debug("HolidayService found {} holiday profiles", collection.size());
		
		for (HolidayProfile holidayProfile : collection) {
			// Get DTO of holiday types
			List<HolidayDTO> holidayTypes = new ArrayList<HolidayDTO>();
			for (HolidayType type : holidayProfile.getHolidayTypes()){
				holidayTypes.add(new HolidayDTO(type));
			}
			log.debug("Detected holiday profile {} containing {} types", holidayProfile.getName(),holidayTypes.size());
			profiles.add(new HolidayProfileDTO(holidayProfile.getId(),holidayProfile.getName(),holidayTypes,holidayProfile.getUsers().size()));
		}
		return profiles;
	}
	
	/**
	 * Get all users associated to the profile.
	 */
	@Transactional
	public List <UserDetailsDTO> getUsersInProfile(int profileId){
		Query query = em.get().createQuery("SELECT hp FROM HolidayProfile hp WHERE hp.id=:id");
		query.setParameter("id", profileId);
		
		HolidayProfile profile = (HolidayProfile) query.getSingleResult();

		Set<User> users = profile.getUsers();
		// Get user details
		List<UserDetailsDTO> usersDTO = new ArrayList<UserDetailsDTO>();
		for (User u : users){
			usersDTO.add(userService.getUserDetails(u.getUsername()));
		}

		return usersDTO;
	}
	
	/**
	 * Get all users which are not associated to the profile.
	 */
	public List<UserDetailsDTO> getUsersNotInProfile(int profileId){
		List<UserDetailsDTO> users = userService.getEnabledUserDetails();
		users.removeAll(this.getUsersInProfile(profileId));

		return users;
	}

	/**
	 * Associates users with a profile (and remove previous users from the profile).
	 * @param id Id of the profile.
	 * @param usernames The list of usernames to associate to the profile.
	 */
	@Transactional
	public void updateProfileUsers(int id, List<String> usernames) {
		// Get profile
		Query profileQuery = em.get().createQuery("SELECT hp FROM HolidayProfile hp WHERE id=:id");
		profileQuery.setParameter("id", id);
		HolidayProfile profile = (HolidayProfile) profileQuery.getSingleResult();
		// Get users
		Set<User> associatedUsers = new HashSet<User>();
		for (String username : usernames){
			if (! username.equals("")){
				associatedUsers.add(userService.getUserByUsername(username));
			}
		}
		
		profile.setUsers(associatedUsers);
	
		em.get().persist(profile);
		
		log.debug("Profile {} has now {} associated users", id, associatedUsers.size());

	}
	
	@SuppressWarnings("unchecked")
	public List<HolidayProfileDTO> getProfilesForUser(long userId){
		Query profileQuery = em.get().createQuery("SELECT hp FROM HolidayProfile hp WHERE :user member of hp.users");
		profileQuery.setParameter("user", em.get().find(User.class, userId));
		
		try{
			List<HolidayProfile> profiles = (List<HolidayProfile>)profileQuery.getResultList();
			List<HolidayProfileDTO> profilesDTO = new ArrayList<HolidayProfileDTO>();
			for(HolidayProfile p : profiles){
				profilesDTO.add(new HolidayProfileDTO(p));
			}
			return profilesDTO;
		}
		catch(NoResultException nre){
			log.debug("There is no profile associated to the user with id {}", userId);
			return null;
		}
	}

}