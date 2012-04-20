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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.shiro.authz.UnauthorizedException;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.core.service.mail.Mailer;
import org.kernely.core.service.user.UserService;
import org.kernely.holiday.dto.CalendarBalanceDetailDTO;
import org.kernely.holiday.dto.CalendarDayDTO;
import org.kernely.holiday.dto.CalendarRequestDTO;
import org.kernely.holiday.dto.HolidayBalanceDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayDetailCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDetailDTO;
import org.kernely.holiday.dto.HolidayProfileDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;
import org.kernely.holiday.model.HolidayType;
import org.kernely.holiday.model.HolidayTypeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The service for holiday request pages
 * 
 */
@Singleton
public class HolidayRequestService extends AbstractService {

	private static final int DAYS_IN_WEEK = 6;
	private static final float HALF_DAY = 0.5F;
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private UserService userService;

	@Inject
	private Mailer mailService;

	@Inject
	private HolidayBalanceService balanceService;

	@Inject
	private HolidayService holidayService;

	@Inject
	private AbstractConfiguration configuration;

	/**
	 * Construct a HolidayRequest
	 * 
	 * @param details
	 * @return
	 */
	private HolidayRequest getHolidayRequestFromDetails(List<HolidayRequestDetail> details) {
		TreeSet<HolidayRequestDetail> orderedDetails = new TreeSet<HolidayRequestDetail>();
		orderedDetails.addAll(details);

		HolidayRequest request = new HolidayRequest();

		request.setDetails(orderedDetails);
		request.setStatus(HolidayRequest.PENDING_STATUS);
		request.setUser(this.getAuthenticatedUserModel());

		request.setBeginDate(orderedDetails.first().getDay());
		request.setEndDate(orderedDetails.last().getDay());

		return request;
	}

	/**
	 * Register an Holiday request with his details.
	 * 
	 * @param request
	 *            The request DTO to store.
	 */
	@Transactional
	public HolidayRequestDTO registerRequestAndDetails(HolidayRequestCreationRequestDTO request) {
		List<HolidayRequestDetail> detailsModels = new ArrayList<HolidayRequestDetail>();
		Map<HolidayTypeInstance, Float> typeToUpdate = new HashMap<HolidayTypeInstance, Float>();
		for (HolidayDetailCreationRequestDTO hdcr : request.details) {
			HolidayRequestDetail detail = new HolidayRequestDetail();
			detail.setAm(hdcr.am);
			detail.setPm(hdcr.pm);
			DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
			DateTime day = fmt.parseDateTime(hdcr.day);
			detail.setDay(day.toDate());
			HolidayTypeInstance typeInstance = em.get().find(HolidayTypeInstance.class, hdcr.typeInstanceId);
			detail.setTypeInstance(typeInstance);
			float taken = 0F;
			// We increase the value by 0.5 because we're counting in 12th of a
			// day
			if (detail.isAm()) {
				taken += HALF_DAY;
			}
			if (detail.isPm()) {
				taken += HALF_DAY;
			}
			if (typeToUpdate.containsKey(typeInstance)) {
				typeToUpdate.put(typeInstance, typeToUpdate.get(typeInstance) + taken);
			} else {
				typeToUpdate.put(typeInstance, taken);

			}
			em.get().persist(detail);
			log.debug("Holiday request detail registered for the day {}", hdcr.day);
			detailsModels.add(detail);
		}
		HolidayRequest hr = this.getHolidayRequestFromDetails(detailsModels);
		// Link the new request to all details
		for (HolidayRequestDetail rd : hr.getDetails()) {
			rd.setRequest(hr);
		}
		hr.setRequesterComment(request.requesterComment);
		em.get().persist(hr);

		// Update temporary balance
		Set<Entry<HolidayTypeInstance, Float>> entries = typeToUpdate.entrySet();
		for (Entry<HolidayTypeInstance, Float> e : entries) {
			this.balanceService.removeDaysInAvailableUpdatedFromRequest(e.getKey().getId(), hr.getUser().getId(), e.getValue());
		}

		log.debug("Holiday Request registered !");
		return new HolidayRequestDTO(hr);
	}

	/**
	 * Get the holiday detail from his id
	 * 
	 * @param idRequest
	 * @return an holidaydetaildto
	 */
	@Transactional
	public List<HolidayDetailDTO> getHolidayRequestDetails(long idRequest) {
		Query holidayRequest = em.get().createQuery("SELECT h FROM HolidayRequest h WHERE id=:idRequest");
		holidayRequest.setParameter("idRequest", idRequest);
		HolidayRequest holiday = (HolidayRequest) holidayRequest.getSingleResult();
		Set<HolidayRequestDetail> holidayDetails = holiday.getDetails();
		ArrayList<HolidayDetailDTO> list = new ArrayList<HolidayDetailDTO>();
		for (HolidayRequestDetail hrd : holidayDetails) {
			list.add(new HolidayDetailDTO(hrd));
		}
		return list;
	}

	/**
	 * Get the holiday detail by order
	 * 
	 * @param idRequest
	 * @return an holiday detail DTO
	 */
	@Transactional
	public List<HolidayDetailDTO> getHolidayRequestDetailsByOrder(long idRequest) {
		Query holidayRequest = em.get().createQuery("SELECT h FROM HolidayRequest h WHERE id=:idRequest");
		holidayRequest.setParameter("idRequest", idRequest);
		HolidayRequest holiday = (HolidayRequest) holidayRequest.getSingleResult();
		Set<HolidayRequestDetail> holidayDetails = holiday.getDetails();
		ArrayList<HolidayDetailDTO> list = new ArrayList<HolidayDetailDTO>();
		for (HolidayRequestDetail hrd : holidayDetails) {
			list.add(new HolidayDetailDTO(hrd));
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * Retrieve all request done by the current user
	 * 
	 * @return A list of DTO corresponding to the request done by the current
	 *         user
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getAllRequestsForCurrentUser() {
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  user=:user");
		query.setParameter("user", this.getAuthenticatedUserModel());
		try {
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for (HolidayRequest r : requests) {
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		} catch (NoResultException e) {
			log.debug("There is no holiday request for current user");
			return null;
		}
	}

	/**
	 * Retrieve all request done by the specified user
	 * 
	 * @return A list of DTO corresponding to the request done by the current
	 *         user
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getAllRequestsForSpecificUser(long userId) {
		try {
			Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  user=:user");
			User u = em.get().find(User.class, userId);

			if (!u.isLocked()) {
				query.setParameter("user", u);
			}
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for (HolidayRequest r : requests) {
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		} catch (NoResultException e) {
			log.debug("There is no holiday request for this user");
			return null;
		} catch (IllegalArgumentException e) {
			log.debug("This user is locked, impossible to access to his requests.");
			return null;
		}
	}

	/**
	 * Retrieve all the request with a given status for the current user
	 * 
	 * @param status
	 *            the status of the request needed
	 * @return A list of DTO corresponding to all request with the given status
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getAllRequestsWithStatusForCurrentUser(int status) {
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  status = :status AND user = :user");
		query.setParameter("status", status);
		query.setParameter("user", this.getAuthenticatedUserModel());
		try {
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for (HolidayRequest r : requests) {
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		} catch (NoResultException e) {
			log.debug("There is no holiday waiting requests");
			return null;
		}
	}

	/**
	 * Retrieve all the request with a given status
	 * 
	 * @param status
	 *            the status of the request needed
	 * @return A list of DTO corresponding to all request with the given status
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getAllRequestsWithStatus(int status) {
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  status = :status");
		query.setParameter("status", status);
		try {
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for (HolidayRequest r : requests) {
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		} catch (NoResultException e) {
			log.debug("There is no holiday waiting requests");
			return null;
		}
	}

	/**
	 * Gets all the request for the current user between the two given dates
	 * 
	 * @param date1
	 *            beginning of the needed interval
	 * @param date2
	 *            ending of the needed interval
	 * @return A list of DTO corresponding to the request located in the
	 *         interval
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getRequestBetweenDatesForCurrentUser(Date date1, Date date2) {
		Query query = em.get().createQuery(
				"SELECT  r from HolidayRequest r WHERE (beginDate between :date1 and :date2" + " OR endDate between :date1 and :date2)"
						+ " and user = :user");
		query.setParameter("date1", date1);
		query.setParameter("date2", date2);
		query.setParameter("user", this.getAuthenticatedUserModel());
		try {
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for (HolidayRequest r : requests) {
				requestsDTO.add(new HolidayRequestDTO(r));
			}
			return requestsDTO;
		} catch (NoResultException e) {
			log.debug("There is no holiday request for these dates");
			return null;
		}
	}

	/**
	 * Gets all the request for the given user between the two given dates
	 * 
	 * @param date1
	 *            beginning of the needed interval
	 * @param date2
	 *            ending of the needed interval
	 * @param user
	 *            user concerned by the request
	 * @return A list of DTO corresponding to the request located in the
	 *         interval
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getRequestBetweenDates(Date date1, Date date2, User user) {
		Query query = em.get().createQuery(
				"SELECT  r from HolidayRequest r WHERE (beginDate between :date1 and :date2" + " OR endDate between :date1 and :date2)"
						+ " AND user = :user");
		query.setParameter("date1", date1);
		query.setParameter("date2", date2);
		query.setParameter("user", user);
		try {
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for (HolidayRequest r : requests) {
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		} catch (NoResultException e) {
			log.debug("There is no holiday request for these dates");
			return null;
		}
	}

	/**
	 * Gets all the request for the given user between the two given dates
	 * 
	 * @param date1
	 *            beginning of the needed interval
	 * @param date2
	 *            ending of the needed interval
	 * @param user
	 *            user concerned by the request
	 * @return A list of DTO corresponding to the request located in the
	 *         interval
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getRequestBetweenDatesWithStatus(Date date1, Date date2, User user, int... status) {
	
		DateTime begin = new DateTime(date1).toDateMidnight().minus(1).toDateTime();
		DateTime end = new DateTime(date2).toDateMidnight().toDateTime().plusDays(1);
		
		DateTime requestBeginDate;
		DateTime requestEndDate;
		
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE user = :user" + " AND status in :status");
		query.setParameter("user", user);
		List<Integer> statusList = new ArrayList<Integer>();
		for (int i : status) {
			statusList.add(i);
		}
		query.setParameter("status", statusList);
		try {
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();

			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for (HolidayRequest r : requests) {
				
				requestBeginDate = new DateTime(r.getBeginDate());
				requestEndDate = new DateTime(r.getEndDate());
				
				boolean requestBeginsBetweenDates = requestBeginDate.toDateMidnight().isAfter(begin) && requestBeginDate.toDateMidnight().isBefore(end);
				boolean requestEndsBetweenDates = requestEndDate.toDateMidnight().isAfter(begin) && requestEndDate.toDateMidnight().isBefore(end);
				if (requestBeginsBetweenDates || requestEndsBetweenDates){
					requestsDTO.add(new HolidayRequestDTO(r));
				}
			}

			return requestsDTO;
		} catch (NoResultException e) {
			log.debug("There is no holiday request for these dates");
			return null;

		}
	}

	/**
	 * Gets all the request for the given user after the given date
	 * 
	 * @param date1
	 *            date where to start research
	 * @param user
	 *            user concerned by the request
	 * @return A list of DTO corresponding to the request located in the search
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getRequestAfterDateWithStatus(Date date, User user, int... status) {
		Query query = em.get().createQuery(
				"SELECT  r from HolidayRequest r WHERE (beginDate > :date" + " OR endDate > :date)" + " AND user = :user" + " AND status in :status");
		query.setParameter("date", date);
		query.setParameter("user", user);
		List<Integer> statusList = new ArrayList<Integer>();
		for (int i : status) {
			statusList.add(i);
		}
		query.setParameter("status", statusList);
		try {
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for (HolidayRequest r : requests) {
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		} catch (NoResultException e) {
			log.debug("There is no holiday request for these dates");
			return null;
		}
	}

	/**
	 * Accept a waiting request
	 * 
	 * @param id
	 *            The request to accept
	 */
	@Transactional
	public void acceptRequest(long id) {
		if (!userService.isManager(this.getAuthenticatedUserModel().getUsername())) {
			throw new UnauthorizedException("Only managers can access to this functionality !");
		}
		log.debug("ACCEPT : Retrieving holiday request with id {}", id);
		HolidayRequest request = em.get().find(HolidayRequest.class, id);
		request.setStatus(HolidayRequest.ACCEPTED_STATUS);
		request.setManager(this.getAuthenticatedUserModel());
		em.get().merge(request);
		log.debug("Holiday request with id {} has been accepted", id);
	}

	/**
	 * Deny a waiting request
	 * 
	 * @param id
	 *            The request to deny
	 */
	@Transactional
	public void denyRequest(long id) {
		if (!userService.isManager(this.getAuthenticatedUserModel().getUsername())) {
			throw new UnauthorizedException("Only managers can access to this functionality !");
		}
		log.debug("DENY : Retrieving holiday request with id {}", id);
		HolidayRequest request = em.get().find(HolidayRequest.class, id);
		request.setStatus(HolidayRequest.DENIED_STATUS);
		request.setManager(this.getAuthenticatedUserModel());
		em.get().merge(request);

		// Update the temporary balance
		Set<HolidayRequestDetail> details = request.getDetails();
		Map<HolidayTypeInstance, Float> typeToUpdate = new HashMap<HolidayTypeInstance, Float>();
		for (HolidayRequestDetail d : details) {
			float taken = 0F;
			// We increase the value by 6 because we're counting in 12th of a
			// day, so 6 represents an half day.
			if (d.isAm()) {
				taken += HALF_DAY;
			}
			if (d.isPm()) {
				taken += HALF_DAY;
			}

			if (typeToUpdate.containsKey(d.getTypeInstance())) {
				typeToUpdate.put(d.getTypeInstance(), typeToUpdate.get(d.getTypeInstance()) + taken);
			} else {
				typeToUpdate.put(d.getTypeInstance(), taken);
			}
		}

		// Update temporary balance

		Set<Entry<HolidayTypeInstance, Float>> entries = typeToUpdate.entrySet();
		for (Entry<HolidayTypeInstance, Float> e : entries) {
			this.balanceService.addDaysInAvailableUpdatedFromRequest(e.getKey().getId(), request.getUser().getId(), e.getValue());
		}

		log.debug("Holiday request with id {} has been denied", id);
	}

	/**
	 * Archive a request (set it as "past")
	 * 
	 * @param id
	 *            The request to archive
	 */
	@Transactional
	public void archiveRequest(long id) {
		log.debug("ARCHIVE : Retrieving holiday request with id {}", id);
		HolidayRequest request = em.get().find(HolidayRequest.class, id);
		request.setStatus(HolidayRequest.PAST_STATUS);
		em.get().merge(request);
		log.debug("Holiday request with id {} has been archived", id);
	}

	/**
	 * Cancel a waiting request
	 * 
	 * @param id
	 */
	@Transactional
	public void cancelRequest(long id) {
		log.debug("CANCEL : Retrieving holiday request with id {}", id);
		HolidayRequest request = em.get().find(HolidayRequest.class, id);
		Set<HolidayRequestDetail> holidayRequestDetails = request.getDetails();

		// Update the temporary balance
		Map<HolidayTypeInstance, Float> typeToUpdate = new HashMap<HolidayTypeInstance, Float>();
		for (HolidayRequestDetail d : holidayRequestDetails) {
			float taken = 0F;
			// We increase the value by 6 because we're counting in 12th of a
			// day, so 6 represents an half day.
			if (d.isAm()) {
				taken += HALF_DAY;
			}
			if (d.isPm()) {
				taken += HALF_DAY;
			}

			if (typeToUpdate.containsKey(d.getTypeInstance())) {
				typeToUpdate.put(d.getTypeInstance(), typeToUpdate.get(d.getTypeInstance()) + taken);
			} else {
				typeToUpdate.put(d.getTypeInstance(), taken);
			}
		}

		// Update temporary balance
		Set<Entry<HolidayTypeInstance, Float>> entries = typeToUpdate.entrySet();
		for (Entry<HolidayTypeInstance, Float> e : entries) {
			this.balanceService.addDaysInAvailableUpdatedFromRequest(e.getKey().getId(), request.getUser().getId(), e.getValue());
		}

		for (HolidayRequestDetail hrd : holidayRequestDetails) {
			em.get().remove(hrd);
		}
		em.get().remove(request);
		log.debug("Holiday request with id {} has been canceled", id);
	}

	/**
	 * Add a manager commentary to the request
	 * 
	 * @param id
	 *            The request to comment
	 * @param managerComment
	 *            the comment of the manager
	 */
	@Transactional
	public void addManagerCommentary(long id, String managerComment) {
		if (!userService.isManager(this.getAuthenticatedUserModel().getUsername())) {
			throw new UnauthorizedException("Only managers can access to this functionality !");
		}
		HolidayRequest request = em.get().find(HolidayRequest.class, id);
		request.setManagerComment(managerComment);
		em.get().merge(request);
		log.debug("Holiday request with id {} has been commented", id);
	}

	/**
	 * Get all request to process for a manager
	 * 
	 * @param status
	 *            The status of requests : pending, accepted or denied. User
	 *            HolidayRequest constants.
	 * @return A list of DTO corresponding to all request done by managed users
	 *         of the current user manager
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getSpecificRequestsForManagers(int status) {
		if (!userService.isManager(this.getAuthenticatedUserModel().getUsername())) {
			throw new UnauthorizedException("Only managers can access to this functionality!");
		}
		Set<UserDTO> managedDTO = userService.getUsersAuthorizedManaged();
		Set<User> managed = new TreeSet<User>();
		for (UserDTO udto : managedDTO) {
			managed.add(em.get().find(User.class, udto.id));
		}
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  status = :status AND user in :users");
		query.setParameter("status", status);
		query.setParameter("users", managed);
		try {
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for (HolidayRequest r : requests) {
				requestsDTO.add(new HolidayRequestDTO(r));
			}
			return requestsDTO;
		} catch (NoResultException e) {
			log.debug("There is no holiday waiting requests to process");
			return null;
		}
	}

	/**
	 * Build the calendar for request holidays. Returns the weeks concerned by
	 * the dates given in param. Verify in existing request if the days are
	 * available. Finally, build the color picker containing all balance
	 * available with their associated color
	 * 
	 * @param date1
	 *            begin date for the request
	 * @param date2
	 *            end date for the request
	 * @return A DTO containing all days concerned by the request
	 */
	@Transactional
	public CalendarRequestDTO getCalendarRequest(DateTime date1, DateTime date2) {
		if (date1.isAfter(date2)) {
			throw new IllegalArgumentException("First Date must be anterior to the second !!");
		}

		CalendarRequestDTO calendar = new CalendarRequestDTO();
		int dayOfWeek1 = date1.getDayOfWeek();
		int dayOfWeek2 = date2.getDayOfWeek();
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		List<CalendarDayDTO> daysDTO = new ArrayList<CalendarDayDTO>();

		DateTime dtmaj;

		List<HolidayRequestDTO> currentRequests = this.getRequestBetweenDatesWithStatus(date1.toDate(), date2.toDate(), this
				.getAuthenticatedUserModel(), HolidayRequest.PENDING_STATUS, HolidayRequest.ACCEPTED_STATUS);

		List<HolidayDetailDTO> allDayReserved = new ArrayList<HolidayDetailDTO>();

		// Retrieve all days non available in order to disable them in the UI
		for (HolidayRequestDTO req : currentRequests) {
			allDayReserved.addAll(req.details);
		}

		// We add the first days of the week in not available for the graphic
		// interface
		for (int i = 1; i < dayOfWeek1; i++) {
			dtmaj = date1.minusDays(dayOfWeek1 - i);
			// The end of the week is not displayed
			if (dtmaj.getDayOfWeek() < DAYS_IN_WEEK) {
				daysDTO.add(new CalendarDayDTO(dtmaj.toString(fmt), false, false, dtmaj.getWeekOfWeekyear()));
			}
		}

		// We add one day to date2 to consider the date2's day. Else, it doesn't
		// consider the last day.
		Days days = Days.daysBetween(date1.toDateMidnight(), date2.plusDays(1).toDateMidnight());

		boolean am;
		boolean pm;
		for (int i = 0; i < days.getDays(); i++) {
			am = true;
			pm = true;
			dtmaj = date1.plusDays(i);
			// The end of the week is not displayed
			if (dtmaj.getDayOfWeek() < DAYS_IN_WEEK) {
				for (HolidayDetailDTO detail : allDayReserved) {
					if (new DateTime(detail.day).toDateMidnight().isEqual(dtmaj.toDateMidnight())) {
						if (detail.am) {
							am = !detail.am;
						}
						if (detail.pm) {
							pm = !detail.pm;
						}
					}
				}
				daysDTO.add(new CalendarDayDTO(dtmaj.toString(fmt), am, pm, dtmaj.getWeekOfWeekyear()));
			}
		}

		// We add the last days of the week in not available for the graphic
		// interface
		for (int i = dayOfWeek2 + 1; i <= DAYS_IN_WEEK + 1; i++) {
			dtmaj = date2.plusDays(i - dayOfWeek2);
			// The end of the week is not displayed
			if (dtmaj.getDayOfWeek() < DAYS_IN_WEEK) {
				daysDTO.add(new CalendarDayDTO(dtmaj.toString(fmt), false, false, dtmaj.getWeekOfWeekyear()));
			}
		}

		int weekPlace1 = date1.getWeekOfWeekyear();
		int weekPlace2 = date2.getWeekOfWeekyear();
		// We add +1 to consider 2 weeks
		// IE : Week 52 - Week 51 = 2 week and not 1
		if (weekPlace1 <= weekPlace2) {
			calendar.nbWeeks = ((weekPlace2 - weekPlace1) + 1);
		}
		// We consider the fact that an interval can be on 2 different years
		// IE : Week 52 of 2011 and Week 1 of 2012.
		else {
			calendar.nbWeeks = ((weekPlace2 + (52 - weekPlace1) + 1));
		}

		calendar.startWeek = date1.getWeekOfWeekyear();
		calendar.days = daysDTO;
		calendar.details = this.buildColorPickerForRequest();
		return calendar;
	}

	/**
	 * Create the color picker Retrieve all the profiles associated to the
	 * current user and for all type of each profile, calculate the available
	 * balance.
	 * 
	 * @return a list of calendar balance detail dto
	 */
	@SuppressWarnings("unchecked")
	private List<CalendarBalanceDetailDTO> buildColorPickerForRequest() {
		long userId = this.getAuthenticatedUserModel().getId();
		List<CalendarBalanceDetailDTO> details = new ArrayList<CalendarBalanceDetailDTO>();
		float availableDays;

		Query typeRequest = em.get().createQuery("SELECT t FROM HolidayTypeInstance t WHERE :user member of t.users");
		typeRequest.setParameter("user", this.getAuthenticatedUserModel());
		try{
			List<HolidayTypeInstance> types = typeRequest.getResultList();
			// Retrieve first all limited 
			List<HolidayBalanceDTO> balancesList;
			HolidayBalanceDTO balanceAnticipated;
			for (HolidayTypeInstance type : types) {
				if(!type.isUnlimited()){
					availableDays = 0.0F;
					balancesList = new ArrayList<HolidayBalanceDTO>(balanceService.getHolidayBalancesAvailable(type.getId(), userId));
					if(type.isAnticipated()){
						balanceAnticipated = balancesList.get(balancesList.size() - 1);
						balancesList.remove(balancesList.size() - 1);
						details.add(new CalendarBalanceDetailDTO(type.getName() + "(anticipation)", balanceAnticipated.availableBalanceUpdated, type.getColor(), type.getId()));
					}
					for (HolidayBalanceDTO hb : balancesList) {
						availableDays += hb.availableBalanceUpdated;
					}
					if(availableDays > 0.0F){
						details.add(new CalendarBalanceDetailDTO(type.getName(), availableDays, type.getColor(), type.getId()));					
					}
				}
			}
			// Now, retrieve all unlimited types associated to the current profiles of the user.
			List<HolidayProfileDTO> profiles = holidayService.getProfilesForUser(userId);
			HolidayType type;
			HolidayTypeInstance instance;
			for(HolidayProfileDTO p : profiles){
				for(HolidayDTO t : p.holidayTypes){
					if(t.unlimited){
						type = em.get().find(HolidayType.class, t.id);
						instance = type.getCurrentInstance();
						details.add(new CalendarBalanceDetailDTO(instance.getName(), -1F, instance.getColor(), instance.getId()));
					}
				}
			}
			return details;
		}
		catch(NoResultException nre){
			return null;
		}
	}

	/**
	 * Send a mail to all managers which have to accept or deny a request.
	 */
	@Transactional
	public void sendRecallToManagers() {
		log.debug("HolidayRequest: Recall");
		int period = configuration.getInt("holidayRequests.recallTime");

		// Select all requests which are pending and should be resolved by a
		// manager
		List<HolidayRequestDTO> pendingRequests = this.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);

		log.debug("Pending requests: " + pendingRequests.size());
		// The map contains, for each manager (UserDTO) a list of requests he has to respond
		Map<UserDTO, List<HolidayRequestDTO>> map = new HashMap<UserDTO, List<HolidayRequestDTO>>();

		for (HolidayRequestDTO request : pendingRequests) {
			int daysBetween = Days.daysBetween(DateTime.now().toDateMidnight(), new DateTime(request.beginDate)).getDays();
			log.debug("Pending request: first day: {} => in {} days", request.beginDate, daysBetween);
			if (daysBetween == period) {
				// Add the request for all concerned managers
				List<UserDTO> managers = userService.getManagers(request.user);

				for (UserDTO manager : managers) {

					List<HolidayRequestDTO> requestsList;

					if (map.containsKey(manager)) {
						requestsList = map.get(manager);
					} else {
						requestsList = new ArrayList<HolidayRequestDTO>();
					}

					requestsList.add(request);
					map.put(manager, requestsList);
					log.debug("Prepare recall for holiday request {} to manager {}.", request.id, manager.username);
				}
			}
		}
		for (UserDTO manager : map.keySet()) {
			StringBuffer content = new StringBuffer("Some holiday requests wait for your response.<br/>");

			for (HolidayRequestDTO request : map.get(manager)) {
				content.append(userService.getUserDetails(request.user).firstname + " " + userService.getUserDetails(request.user).lastname + " from " + request.beginDate + " to " + request.endDate + "\n");
			}

			mailService.create("/templates/gsp/recallMail.gsp").with("content", content.toString()).subject("[Kernely] Holiday requests pending").to(
					userService.getUserDetails(manager.username).email).registerMail();

			log.info("Recall mail to manager {}: {}", manager.username, content);

		}
	}
}
