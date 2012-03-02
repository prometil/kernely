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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.core.service.user.UserService;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayDetailDTO;
import org.kernely.holiday.dto.HolidayProfileCreationRequestDTO;
import org.kernely.holiday.dto.HolidayProfileDTO;
import org.kernely.holiday.dto.HolidayProfilesSummaryDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.dto.HolidayUserSummaryDTO;
import org.kernely.holiday.dto.HolidayUserTypeSummaryDTO;
import org.kernely.holiday.model.HolidayProfile;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayType;
import org.kernely.holiday.model.HolidayTypeInstance;
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

	@Inject
	HolidayRequestService requestService;

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

	@SuppressWarnings("unchecked")
	@Transactional
	protected List<HolidayProfile> getAllProfile() {
		Query query = em.get().createQuery("SELECT e FROM HolidayProfile e");
		try {
			List<HolidayProfile> collection = (List<HolidayProfile>) query.getResultList();
			return collection;
		} catch (NoResultException nre) {
			return new ArrayList<HolidayProfile>();
		}
	}

	/**
	 * Gets the holiday DTO for the holiday type with the id passed in
	 * parameter.
	 * 
	 * @param id
	 *            The id of the holiday typ
	 * @return the holiday type dto.
	 */
	@Transactional
	public HolidayDTO getHolidayDTO(long id) {
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
	public void deleteHoliday(long id) {
		HolidayType holiday = em.get().find(HolidayType.class, id);
		em.get().remove(holiday);
	}

	/**
	 * Create or update a new Holiday in database. If the id requested is 0,
	 * create a new type. If the id is not 0, update the type with the requested
	 * id.
	 * 
	 * @param request
	 *            Request containing data to set.
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

		log.debug("Creation or update of Holiday Type. id:" + request.id + " name" + request.name + " qty " + request.quantity + " unlimited: "
				+ request.unlimited + " anticipated:" + request.anticipation);

		String oldName = "";
		String oldColor = "";
		boolean oldAnticipated = false;
		int oldQuantity = 0;
		int oldPeriodUnit = 0;
		int oldEffectiveMonth = 0;
		boolean oldUnlimited = false;

		long id = request.id;
		if (id == 0) {
			// Create a new type
			holidayType = new HolidayType();
		} else {
			// Type is already in database
			holidayType = em.get().find(HolidayType.class, id);
			oldName = holidayType.getName();
			oldColor = holidayType.getColor();
			oldAnticipated = holidayType.isAnticipated();
			oldQuantity = holidayType.getQuantity();
			oldPeriodUnit = holidayType.getPeriodUnit();
			oldEffectiveMonth = holidayType.getEffectiveMonth();
			oldUnlimited = holidayType.isUnlimited();
		}

		holidayType.setName(request.name.trim());
		holidayType.setUnlimited(request.unlimited);
		if (!request.unlimited) { // These fields are useless if the type is
			// unlimited
			holidayType.setQuantity(request.quantity);
			holidayType.setPeriodUnit(request.unity);
			holidayType.setEffectiveMonth(request.effectiveMonth);
			holidayType.setAnticipated(request.anticipation);
		}
		holidayType.setColor(request.color);

		if (id == 0) {
			// Create a new instance of this type
			HolidayTypeInstance instance = new HolidayTypeInstance();
			instance.setName(holidayType.getName());
			instance.setColor(holidayType.getColor());
			instance.setPeriodUnit(holidayType.getPeriodUnit());
			instance.setQuantity(holidayType.getQuantity());
			instance.setAnticipated(holidayType.isAnticipated());
			instance.setUnlimited(holidayType.isUnlimited());
			em.get().persist(instance);
			holidayType.setCurrentInstance(instance);
			em.get().persist(holidayType);
			log.debug("HolidayService: new holiday type created ({})", request.name);
		} else {
			// Update case
			// If the effective month, quantity, the period unit or the
			// anticipated mode has
			// changed, we have to create a new instance of this type
			if (oldEffectiveMonth != holidayType.getEffectiveMonth() || oldAnticipated != holidayType.isAnticipated()
					|| oldQuantity != holidayType.getQuantity() || oldPeriodUnit != holidayType.getPeriodUnit()
					|| oldUnlimited != holidayType.isUnlimited()) {
				HolidayTypeInstance instance = new HolidayTypeInstance();
				instance.setName(holidayType.getName());
				instance.setColor(holidayType.getColor());
				instance.setPeriodUnit(holidayType.getPeriodUnit());
				instance.setQuantity(holidayType.getQuantity());
				instance.setAnticipated(holidayType.isAnticipated());
				instance.setUnlimited(holidayType.isUnlimited());
				em.get().persist(instance);
				holidayType.setCurrentInstance(instance);
			}
			// If only the name or the color have changed, we have to just
			// update the existing instance of this type
			else {
				if (!oldName.equals(holidayType.getName()) || !oldColor.equals(holidayType.getColor())) {
					HolidayTypeInstance currentInstance = holidayType.getCurrentInstance();
					currentInstance.setName(holidayType.getName());
					currentInstance.setColor(holidayType.getColor());
				}
			}
			em.get().merge(holidayType);
			log.debug("HolidayService: holiday type updated ({})", request.name);
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
	 * Create a new holiday profile in database. A holiday profile contains
	 * several holiday types.
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
		long id = request.id;
		if (id == 0) {
			profile = new HolidayProfile();
		} else {
			Query verifExist = em.get().createQuery("SELECT hp FROM HolidayProfile hp WHERE id=:id");
			verifExist.setParameter("id", id);
			profile = (HolidayProfile) verifExist.getSingleResult();

			// Detach types from profile
			for (HolidayType type : profile.getHolidayTypes()) {
				type.setProfile(null);
				em.get().merge(type);
			}

		}

		// Get all holiday types from this holiday profile
		Set<HolidayType> types = new HashSet<HolidayType>();
		for (long typeId : request.holidayTypesId) {
			Query query = em.get().createQuery("SELECT  h from HolidayType h WHERE  h.id=:id");
			query.setParameter("id", typeId);
			HolidayType holiday = (HolidayType) query.getSingleResult();
			types.add(holiday);
			log.debug("Added holiday type (id:{}) to holiday profile (name:{})", holiday.getId(), request.name.trim());
		}

		profile.setName(request.name.trim());
		profile.setHolidayTypes(types);

		if (request.id == 0) {
			em.get().persist(profile);
		} else {
			em.get().merge(profile);
		}

		// Update holiday types
		for (long typeId : request.holidayTypesId) {
			Query query = em.get().createQuery("SELECT  h from HolidayType h WHERE  h.id=:id");
			query.setParameter("id", typeId);
			HolidayType holiday = (HolidayType) query.getSingleResult();
			holiday.setProfile(profile);
			em.get().merge(holiday);
		}

		// Create

		return new HolidayProfileDTO(profile);
	}

	/**
	 * Get all holiday profiles, containing holiday types.
	 * 
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
			for (HolidayType type : holidayProfile.getHolidayTypes()) {
				holidayTypes.add(new HolidayDTO(type));
			}
			Collections.sort(holidayTypes);
			log.debug("Detected holiday profile {} containing {} types", holidayProfile.getName(), holidayTypes.size());
			profiles.add(new HolidayProfileDTO(holidayProfile.getId(), holidayProfile.getName(), holidayTypes, holidayProfile.getUsers().size()));
		}
		return profiles;
	}

	/**
	 * Get all users associated to the profile.
	 */
	@Transactional
	public List<UserDetailsDTO> getUsersInProfile(long id) {
		Query query = em.get().createQuery("SELECT hp FROM HolidayProfile hp WHERE hp.id=:id");
		query.setParameter("id", id);

		HolidayProfile profile = (HolidayProfile) query.getSingleResult();

		Set<User> users = profile.getUsers();
		// Get user details
		List<UserDetailsDTO> usersDTO = new ArrayList<UserDetailsDTO>();
		for (User u : users) {
			usersDTO.add(userService.getUserDetails(u.getUsername()));
		}

		return usersDTO;
	}

	/**
	 * Get all users which are not associated to the profile.
	 */
	public List<UserDetailsDTO> getUsersNotInProfile(int profileId) {
		List<UserDetailsDTO> users = userService.getEnabledUserDetails();
		users.removeAll(this.getUsersInProfile(profileId));

		return users;
	}

	/**
	 * Associates users with a profile (and remove previous users from the
	 * profile).
	 * 
	 * @param id
	 *            Id of the profile.
	 * @param usernames
	 *            The list of usernames to associate to the profile.
	 */
	@Transactional
	public void updateProfileUsers(long id, List<String> usernames) {
		// Get profile
		Query profileQuery = em.get().createQuery("SELECT hp FROM HolidayProfile hp WHERE id=:id");
		profileQuery.setParameter("id", id);
		HolidayProfile profile = (HolidayProfile) profileQuery.getSingleResult();
		// Get users
		Set<User> associatedUsers = new HashSet<User>();
		for (String username : usernames) {
			if (!username.equals("")) {
				associatedUsers.add(userService.getUserByUsername(username));
			}
		}

		profile.setUsers(associatedUsers);
		HolidayTypeInstance currentInstance;
		for (HolidayType type : profile.getHolidayTypes()) {
			currentInstance = type.getCurrentInstance();
			currentInstance.setUsers(associatedUsers);
			log
					.debug("Type instance with id {} has been associated to the users of the profile with id {}", currentInstance.getId(), profile
							.getId());
			em.get().merge(currentInstance);
		}

		// Create one balance for each type and each user.
		DateTime hireDate;
		for (User user : associatedUsers) {
			for (HolidayType type : profile.getHolidayTypes()) {
				hireDate = new DateTime(user.getUserDetails().getHire());
				if (hireDate.isAfterNow()) {
					balanceService.createHolidayBalanceForNewUser(type.getId(), user.getId());
				} else {
					balanceService.createHolidayBalance(type.getId(), user.getId());
				}
				log.debug("Balance created for the user {} and the type {}", user.getId(), type.getId());
			}
		}

		em.get().persist(profile);

		log.debug("Profile {} has now {} associated users", id, associatedUsers.size());

	}

	/**
	 * Retrieves all the profiles associated to an user
	 * 
	 * @param userId
	 *            The id of the concerned user
	 * @return A list of HolidayProfileDTO.
	 */
	@SuppressWarnings("unchecked")
	public List<HolidayProfileDTO> getProfilesForUser(long userId) {
		Query profileQuery = em.get().createQuery("SELECT hp FROM HolidayProfile hp WHERE :user member of hp.users");
		profileQuery.setParameter("user", em.get().find(User.class, userId));

		try {
			List<HolidayProfile> profiles = (List<HolidayProfile>) profileQuery.getResultList();
			List<HolidayProfileDTO> profilesDTO = new ArrayList<HolidayProfileDTO>();
			for (HolidayProfile p : profiles) {
				profilesDTO.add(new HolidayProfileDTO(p));
			}
			return profilesDTO;
		} catch (NoResultException nre) {
			log.debug("There is no profile associated to the user with id {}", userId);
			return null;
		}
	}

	/**
	 * Get all holidays taken by users for all types for a specific month.
	 * 
	 * @param month
	 *            The requested month (use HolidayType constants). If 0, will be
	 *            considered as today month.
	 * @param year
	 *            The requested month. If 0, will be considered as today month.
	 * 
	 * @return the summary for requested the month.
	 */
	@Transactional
	public List<HolidayProfilesSummaryDTO> getSummmaryForAllProfiles(int month, int year) {

		List<HolidayProfilesSummaryDTO> summary = new ArrayList<HolidayProfilesSummaryDTO>();

		if (month == 0) {
			month = DateTime.now().getMonthOfYear();
		}
		if (year == 0) {
			year = DateTime.now().getYear();
		}

		Date begin = new DateTime().withMonthOfYear(month).withYear(year).withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0)
				.withSecondOfMinute(0).minus(1).toDate();
		Date end = new DateTime().withYear(year).withMonthOfYear(month).plusMonths(1).withDayOfMonth(1).toDateMidnight().toDate();

		log.debug("Get summary for all profiles from {} to {}", begin, end);

		List<HolidayProfileDTO> profiles = this.getAllProfiles();

		for (HolidayProfileDTO profile : profiles) {
			log.debug("Summary build: profile {}", profile.name);

			HolidayProfilesSummaryDTO profileSummary = new HolidayProfilesSummaryDTO();
			profileSummary.name = profile.name;
			profileSummary.usersSummaries = new ArrayList<HolidayUserSummaryDTO>();
			List<UserDetailsDTO> users = this.getUsersInProfile(profile.id);

			for (UserDetailsDTO userDetails : users) {
				Map<String, Float> taken = new HashMap<String, Float>();
				Map<String, Float> pending = new HashMap<String, Float>();

				HolidayUserSummaryDTO userSummary = new HolidayUserSummaryDTO();

				userSummary.details = userDetails;
				userSummary.typesSummaries = new ArrayList<HolidayUserTypeSummaryDTO>();

				User userModel = em.get().find(User.class, userDetails.user.id);

				// Set all amounts to 0
				for (HolidayDTO type : profile.holidayTypes) {
					log.debug("Summary build: Map taken/pending for holiday type {}", type.name);
					taken.put(type.name, 0F);
					pending.put(type.name, 0F);
				}

				// For each request, look at every detail, to add the detail to
				// one of the types
				List<HolidayRequestDTO> requests = requestService.getRequestBetweenDatesWithStatus(begin, end, userModel,
						HolidayRequest.ACCEPTED_STATUS, HolidayRequest.PAST_STATUS, HolidayRequest.PENDING_STATUS);
				log.debug("Summary build: user {} has {} requests.", userDetails.user.id, requests.size());
				for (HolidayRequestDTO request : requests) {
					log.debug("Summary build: Request {} contains {} details", request.id, request.details.size());
					for (HolidayDetailDTO detail : request.details) {
						// Add the detail to the concerned type, only if the
						// request is between the requested dates
						if ((new DateTime(detail.day).getMonthOfYear() == month) && (new DateTime(detail.day).getYear() == year)) {
							log.debug("Summary build: Detail  has type {}", detail.type);
							if (request.status == HolidayRequest.ACCEPTED_STATUS || request.status == HolidayRequest.PAST_STATUS) {
								taken.put(detail.type, taken.get(detail.type) + 0.5F);
							} else if (request.status == HolidayRequest.PENDING_STATUS) {
								pending.put(detail.type, pending.get(detail.type) + 0.5F);
							}
						}
					}
				}

				// Build the userSummary
				for (HolidayDTO type : profile.holidayTypes) {
					log.debug("Summary build: Type {} has {} taken", type.name, taken.get(type.name));
					log.debug("Summary build: Type {} has {} pending", type.name, taken.get(type.name));
					HolidayUserTypeSummaryDTO typeSummary = new HolidayUserTypeSummaryDTO(type, taken.get(type.name), pending.get(type.name));
					userSummary.typesSummaries.add(typeSummary);
				}
				log.debug("Summary build: User summary for profile {} has size {}", userSummary.details.user.username, userSummary.typesSummaries
						.size());
				profileSummary.usersSummaries.add(userSummary);
			}
			profileSummary.year = year;
			profileSummary.month = month;
			summary.add(profileSummary);
		}
		return summary;
	}

}