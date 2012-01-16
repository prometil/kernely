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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.shiro.authz.UnauthorizedException;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.core.service.user.UserService;
import org.kernely.holiday.dto.CalendarBalanceDetailDTO;
import org.kernely.holiday.dto.CalendarDayDTO;
import org.kernely.holiday.dto.CalendarRequestDTO;
import org.kernely.holiday.dto.HolidayDetailCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDetailDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayBalance;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;
import org.kernely.holiday.model.HolidayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The service  for holiday request pages
 * @author b.grandperret
 *
 */
@Singleton
public class HolidayRequestService extends AbstractService{
	
	private static final int DAYS_IN_WEEK = 6;
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Inject
	private UserService userService;

	/**
	 * Construct a HolidayRequest
	 * @param details
	 * @return
	 */
	private HolidayRequest getHolidayRequestFromDetails(List<HolidayRequestDetail> details){
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
	 * @param request The request DTO to store.
	 */
	@Transactional
	public void registerRequestAndDetails(HolidayRequestCreationRequestDTO request){
		List<HolidayRequestDetail> detailsModels = new ArrayList<HolidayRequestDetail>();
		for(HolidayDetailCreationRequestDTO hdcr : request.details){
			HolidayRequestDetail detail = new HolidayRequestDetail();
			detail.setAm(hdcr.am);
			detail.setPm(hdcr.pm);
			DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
			DateTime day = fmt.parseDateTime(hdcr.day);
			detail.setDay(day.toDate());
			detail.setBalance(getBalanceWithTypeId(hdcr.typeId));
			em.get().persist(detail);
			log.debug("Holiday request detail registered for the day {}", hdcr.day);
			detailsModels.add(detail);
		}
		HolidayRequest hr = this.getHolidayRequestFromDetails(detailsModels);
		// Link the new request to all details
		for(HolidayRequestDetail rd : hr.getDetails()){
			rd.setRequest(hr);
		}
		hr.setRequesterComment(request.requesterComment);
		em.get().persist(hr);
		log.debug("Holiday Request registered !");
	}
	
	@Transactional
	private HolidayBalance getBalanceWithTypeId(int typeId){
		HolidayType type = em.get().find(HolidayType.class, typeId);
		Query balanceRequest = em.get().createQuery("SELECT b FROM HolidayBalance b WHERE user=:user AND holidayType=:type");
		balanceRequest.setParameter("user", this.getAuthenticatedUserModel());
		balanceRequest.setParameter("type", type);
		try {
			HolidayBalance balance = (HolidayBalance) balanceRequest.getSingleResult();
			log.debug("Balance found for the current user with the type id {}", typeId);
			return balance;
		} catch (NoResultException nre) {
			log.debug("There is no balance with the type id {} for the user {}", typeId , this.getAuthenticatedUserModel().getId());
			return null;
		}
	}

	/**
	 * Retrieve all request done by the current user
	 * @return A list of DTO corresponding to the request done by the current user
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getAllRequestsForCurrentUser(){
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  user=:user");
		query.setParameter("user", this.getAuthenticatedUserModel());
		try{
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for(HolidayRequest r : requests){
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		}
		catch(NoResultException e){
			log.debug("There is no holiday request for current user");
			return null;
		}
	}
	
	/**
	 * Retrieve all the request with a given status for the current user
	 * @param status the status of the request needed
	 * @return A list of DTO corresponding to all request with the given status
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getAllRequestsWithStatusForCurrentUser(int status){
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  status = :status AND user = :user");
		query.setParameter("status", status);
		query.setParameter("user", this.getAuthenticatedUserModel());
		try{
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for(HolidayRequest r : requests){				
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		}
		catch(NoResultException e){
			log.debug("There is no holiday waiting requests");
			return null;
		}
	}
	
	/**
	 * Retrieve all the request with a given status
	 * @param status the status of the request needed
	 * @return A list of DTO corresponding to all request with the given status
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getAllRequestsWithStatus(int status){
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  status = :status");
		query.setParameter("status", status);
		try{
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for(HolidayRequest r : requests){				
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		}
		catch(NoResultException e){
			log.debug("There is no holiday waiting requests");
			return null;
		}
	}
	
	/**
	 * Gets all the request for the current user between the two given dates
	 * @param date1 beginning of the needed interval
	 * @param date2 ending of the needed interval
	 * @return A list of DTO corresponding to the request located in the interval
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getRequestBetweenDatesForCurrentUser(Date date1, Date date2){
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE (beginDate between :date1 and :date2" +
										" OR endDate between :date1 and :date2)" +
										" and user = :user");
		query.setParameter("date1", date1);
		query.setParameter("date2", date2);
		query.setParameter("user", this.getAuthenticatedUserModel());
		try{
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for(HolidayRequest r : requests){
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		}
		catch(NoResultException e){
			log.debug("There is no holiday request for these dates");
			return null;
		}
	}
	
	/**
	 * Gets all the request for the given user between the two given dates
	 * @param date1 beginning of the needed interval
	 * @param date2 ending of the needed interval
	 * @param user user concerned by the request
	 * @return A list of DTO corresponding to the request located in the interval
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getRequestBetweenDates(Date date1, Date date2, User user){
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE (beginDate between :date1 and :date2" +
										" OR endDate between :date1 and :date2)" +
										" AND user = :user");
		query.setParameter("date1", date1);
		query.setParameter("date2", date2);
		query.setParameter("user", user);
		try{
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for(HolidayRequest r : requests){
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		}
		catch(NoResultException e){
			log.debug("There is no holiday request for these dates");
			return null;
		}
	}
	
	/**
	 * Gets all the request for the given user between the two given dates
	 * @param date1 beginning of the needed interval
	 * @param date2 ending of the needed interval
	 * @param user user concerned by the request
	 * @return A list of DTO corresponding to the request located in the interval
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getRequestBetweenDatesWithStatus(Date date1, Date date2, User user, int ... status){
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE (beginDate between :date1 and :date2" +
										" OR endDate between :date1 and :date2)" +
										" AND user = :user" +
										" AND status in :status");
		query.setParameter("date1", date1);
		query.setParameter("date2", date2);
		query.setParameter("user", user);
		List<Integer> statusList = new ArrayList<Integer>();
		for(int i : status){
			statusList.add(i);
		}
		query.setParameter("status", statusList);
		try{
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for(HolidayRequest r : requests){
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		}
		catch(NoResultException e){
			log.debug("There is no holiday request for these dates");
			return null;
		}
	}
	
	/**
	 * Accept a waiting request
	 * @param idRequest The request to accept
	 */
	@Transactional
	public void acceptRequest(int idRequest){
		if(!userService.isManager(this.getAuthenticatedUserModel().getUsername())){	
			throw new UnauthorizedException("Only managers can access to this functionality !");
		}
		log.debug("ACCEPT : Retrieving holiday request with id {}", idRequest);
		HolidayRequest request = em.get().find(HolidayRequest.class, idRequest);
		request.setStatus(HolidayRequest.ACCEPTED_STATUS);
		request.setManager(this.getAuthenticatedUserModel().getUsername());
		em.get().merge(request);
		log.debug("Holiday request with id {} has been accepted", idRequest);
	}
	
	/**
	 * Deny a waiting request
	 * @param idRequest The request to deny
	 */
	@Transactional
	public void denyRequest(int idRequest){
		if(!userService.isManager(this.getAuthenticatedUserModel().getUsername())){	
			throw new UnauthorizedException("Only managers can access to this functionality !");
		}
		log.debug("DENY : Retrieving holiday request with id {}", idRequest);
		HolidayRequest request = em.get().find(HolidayRequest.class, idRequest);
		request.setStatus(HolidayRequest.DENIED_STATUS);
		request.setManager(this.getAuthenticatedUserModel().getUsername());
		em.get().merge(request);
		log.debug("Holiday request with id {} has been denied", idRequest);
	}
	
	/**
	 * Cancel a waiting request
	 * @param idRequest
	 */
	@Transactional 
	public void cancelRequest(int idRequest){
		log.debug("CANCEL : Retrieving holiday request with id {}", idRequest);
		HolidayRequest request = em.get().find(HolidayRequest.class, idRequest);
		Set<HolidayRequestDetail> holidayRequestDetails = request.getDetails();
		for (HolidayRequestDetail hrd : holidayRequestDetails){
			em.get().remove(hrd);
		}
		em.get().remove(request);
		log.debug("Holiday request with id {} has been canceled", idRequest);
	}
	
	/**
	 * Add a manager commentary to the request 
	 * @param idRequest The request to comment
	 * @param managerComment the comment of the manager
	 */
	@Transactional
	public void addManagerCommentary(int idRequest, String managerComment){
		if(!userService.isManager(this.getAuthenticatedUserModel().getUsername())){	
			throw new UnauthorizedException("Only managers can access to this functionality !");
		}
		HolidayRequest request = em.get().find(HolidayRequest.class, idRequest);
		request.setManagerComment(managerComment);
		em.get().merge(request);
		log.debug("Holiday request with id {} has been commented", idRequest);
	}
	
	/**
	 * Get all request to process for a manager
	 * @param status The status of requests : pending, accepted or denied. User HolidayRequest constants.
	 * @return A list of DTO corresponding to all request done by managed users of the current user manager
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getSpecificRequestsForManagers(int status){
		if(!userService.isManager(this.getAuthenticatedUserModel().getUsername())){        
			throw new UnauthorizedException("Only managers can access to this functionality!");
		}
		
		User current = this.getAuthenticatedUserModel();
		Set<User> managed = current.getUsers();
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  status = :status AND user in :users");
		query.setParameter("status", status);
		query.setParameter("users", managed);
		try{
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for(HolidayRequest r : requests){
				requestsDTO.add(new HolidayRequestDTO(r));
			}
			return requestsDTO;
		}
		catch(NoResultException e){
			log.debug("There is no holiday waiting requests to process");
			return null;
		}
	}

	/**
	 * Build the calendar for request holidays. Returns the weeks concerned by the dates given in param.
	 * Verify in existing request if the days are available.
	 * Finally, build the color picker containing all balance available with their associated color
	 * @param date1 begin date for the request
	 * @param date2 end date for the request
	 * @return A DTO containing all days concerned by the request
	 */
	@Transactional
	public CalendarRequestDTO getCalendarRequest(DateTime date1, DateTime date2) {
		if (date1.isAfter(date2)) {
			throw new IllegalArgumentException("First Date must be anterior to second !!");
		}
		
		CalendarRequestDTO calendar = new CalendarRequestDTO();
		int dayOfWeek1 = date1.getDayOfWeek();
		int dayOfWeek2 = date2.getDayOfWeek();
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		List<CalendarDayDTO> daysDTO = new ArrayList<CalendarDayDTO>();
		
		DateTime dtmaj;
		
		List<HolidayRequestDTO> currentRequests = this.getRequestBetweenDatesForCurrentUser(date1.toDate(), date2.toDate());
		
		List<HolidayDetailDTO> allDayReserved = new ArrayList<HolidayDetailDTO>();
		
		// Retrieve all days non available in order to disable them in the UI
		for(HolidayRequestDTO req : currentRequests){
			allDayReserved.addAll(req.details);
		}
		
		// We add the first days of the week in not available for the graphic interface
		for(int i = 1; i < dayOfWeek1; i++){
			dtmaj = date1.minusDays(dayOfWeek1 - i);
			// The end of the week is not displayed
			if(dtmaj.getDayOfWeek() < DAYS_IN_WEEK){
				daysDTO.add(new CalendarDayDTO(dtmaj.toString(fmt), false, false,  dtmaj.getWeekOfWeekyear()));
			}
		}
		
		// We add one day to date2 to consider the date2's day. Else, it doesn't consider the last day.
		Days days = Days.daysBetween(date1.toDateMidnight(), date2.plusDays(1).toDateMidnight());
		
		boolean am;
		boolean pm;
		for(int i = 0; i < days.getDays(); i++){
			am = true;
			pm = true;
			dtmaj = date1.plusDays(i);
			// The end of the week is not displayed
			if(dtmaj.getDayOfWeek() < DAYS_IN_WEEK){
				for(HolidayDetailDTO detail : allDayReserved){
					if(new DateTime(detail.day).toDateMidnight().isEqual(dtmaj.toDateMidnight())){
						if(detail.am){
							am = !detail.am;
						}
						if(detail.pm){
							pm = !detail.pm;
						}
					}
				}
				daysDTO.add(new CalendarDayDTO(dtmaj.toString(fmt), am, pm, dtmaj.getWeekOfWeekyear()));
			}
		}
		
		// We add the last days of the week in not available for the graphic interface
		for(int i = dayOfWeek2 +1; i <= DAYS_IN_WEEK+1; i++){
			dtmaj = date2.plusDays(i - dayOfWeek2);
			// The end of the week is not displayed
			if(dtmaj.getDayOfWeek() < DAYS_IN_WEEK){
				daysDTO.add(new CalendarDayDTO(dtmaj.toString(fmt), false, false, dtmaj.getWeekOfWeekyear()));
			}
		}
		
		
		int weekPlace1 = date1.getWeekOfWeekyear();
		int weekPlace2 = date2.getWeekOfWeekyear();
		// We add +1 to consider 2 weeks
		// IE : Week 52 - Week 51 = 2 week and not 1
		if(weekPlace1 <= weekPlace2){
			calendar.nbWeeks = ((weekPlace2 - weekPlace1) + 1);
		}
		// We consider the fact that an interval can be on 2 different years
		// IE : Week 52 of 2011 and Week 1 of 2012.
		else{
			calendar.nbWeeks = ((weekPlace2 + (52 - weekPlace1) + 1));
		}
		
		calendar.startWeek = date1.getWeekOfWeekyear();
		calendar.days = daysDTO;
		calendar.details = this.buildColorPickerForRequest();
		return calendar;
	}
	/**
	 * Create the color picker
	 * @return a list of calendar balance detail dto
	 */
	@SuppressWarnings("unchecked")
	private List<CalendarBalanceDetailDTO> buildColorPickerForRequest() {
		Query balanceRequest = em.get().createQuery("SELECT b FROM HolidayBalance b WHERE user=:user");
		balanceRequest.setParameter("user", this.getAuthenticatedUserModel());
		try {
			List<HolidayBalance> balance = (List<HolidayBalance>) balanceRequest.getResultList();

			List<CalendarBalanceDetailDTO> details = new ArrayList<CalendarBalanceDetailDTO>();
			for(HolidayBalance b : balance){
				details.add(new CalendarBalanceDetailDTO(b.getHolidayType().getName(), b.getAvailableBalance(), b.getHolidayType().getColor(), b.getHolidayType().getId()));
			}
			return details;
			
		} catch (NoResultException nre) {
			return null;
		}
	}
	
}
