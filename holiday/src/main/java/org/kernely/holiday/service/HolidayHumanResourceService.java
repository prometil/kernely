package org.kernely.holiday.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.shiro.authz.UnauthorizedException;
import org.joda.time.DateTime;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.User;
import org.kernely.core.service.UserService;
import org.kernely.holiday.dto.CalendarBalanceDetailDTO;
import org.kernely.holiday.dto.HolidayDetailDTO;
import org.kernely.holiday.dto.HolidayManagedDetailsDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.dto.HolidayUserManagedDTO;
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayTypeInstance;
import org.kernely.service.AbstractService;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;


/**
 * Service managing the human resource page for users' holidays
 */
@Singleton
public class HolidayHumanResourceService extends AbstractService{

	@Inject
	private HolidayRequestService holidayRequestService;

	@Inject
	private UserService userService;


	/**
	 * Retrieves all holidays for all users by the current human resource for the given month
	 * @param month The number corresponding to the month needed, IE : January = 1, February = 2 ..., if 0, this is the current month
	 * @param year The year needed, if 0, this is the current year.
	 * @throws UnauthorizedException if the current user is not human resource  
	 */
	@Transactional
	public HolidayUsersManagerDTO getHolidayForAllUsersForMonth(int month, int year){

		int monthNeeded;
		int yearNeeded;
		if(month == 0){
			monthNeeded = new DateTime().getMonthOfYear();
		}
		else{
			monthNeeded = month;
		}

		if(year == 0){
			yearNeeded = new DateTime().getYear();
		}
		else{
			yearNeeded = year;
		}

		// Retrieve the first day and the last day of the month
		DateTime monthDate = new DateTime().withMonthOfYear(monthNeeded).withYear(yearNeeded);
		DateTime first = monthDate.withDayOfMonth(1);
		DateTime last = monthDate.withDayOfMonth(monthDate.dayOfMonth().getMaximumValue());

		List<HolidayUserManagedDTO> managedDTO = new ArrayList<HolidayUserManagedDTO>();
		Set<CalendarBalanceDetailDTO> balancesDTO = new HashSet<CalendarBalanceDetailDTO>();

		Set<UserDTO> users = new HashSet<UserDTO>(userService.getAllUsers());

		List<HolidayDetailDTO> detailsDTO ;

		Set<HolidayManagedDetailsDTO> detailManagedDTO;

		for(UserDTO u : users){
			// Clear all the list for the new user
			detailsDTO = new ArrayList<HolidayDetailDTO>();
			detailManagedDTO = new TreeSet<HolidayManagedDetailsDTO>();
			User us = em.get().find(User.class, u.id);
			List<HolidayRequestDTO> requests = holidayRequestService.getRequestBetweenDatesWithStatus(first.toDate(), last.toDate(), us,  HolidayRequest.ACCEPTED_STATUS);
			for(HolidayRequestDTO req : requests){
				detailsDTO.addAll(req.details);
			}
			for(HolidayDetailDTO det : detailsDTO){
				DateTime current = new DateTime(det.day);
				// We remove 1 to first and add 1 to last in order to consider these day in the detail interval
				if(current.toDateMidnight().isAfter(first.toDateMidnight().minusDays(1)) && current.toDateMidnight().isBefore(last.toDateMidnight().plusDays(1))){
					detailManagedDTO.add(new HolidayManagedDetailsDTO(det.color, current.getDayOfMonth(), det.am, det.pm));
				}
				HolidayTypeInstance instance = em.get().find(HolidayTypeInstance.class, det.typeInstanceId);
				balancesDTO.add(new CalendarBalanceDetailDTO(instance.getName(), 0, instance.getColor() , instance.getId(),0));
			}
			String fullname = us.getUserDetails().getFirstname() + " " + us.getUserDetails().getName();
			managedDTO.add(new HolidayUserManagedDTO(fullname, detailManagedDTO));
		}
		HolidayUsersManagerDTO mainDTO = new HolidayUsersManagerDTO(managedDTO, balancesDTO);
		mainDTO.nbDays = last.getDayOfMonth();
		mainDTO.month = monthNeeded;
		mainDTO.year = yearNeeded;
		return mainDTO;
	}
}