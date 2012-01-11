package org.kernely.holiday.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Query;

import org.joda.time.DateTime;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.holiday.dto.CalendarBalanceDetailDTO;
import org.kernely.holiday.dto.HolidayDetailDTO;
import org.kernely.holiday.dto.HolidayManagedDetailsDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.dto.HolidayUserManagedDTO;
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.model.HolidayBalance;
import org.kernely.holiday.model.HolidayType;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * Service managing the manager page for users' holidays
 */
@Singleton
public class HolidayManagerUserService extends AbstractService{
	
	@Inject
	private HolidayRequestService holidayRequestService;
	
	
	/**
	 * Retrieves all holidays for all users managed by the current user for the given month
	 * @param month The number corresponding to the month needed, IE : January = 1, February = 2 ...
	 */
	@Transactional
	public HolidayUsersManagerDTO getHolidayForAllManagedUsersForMonth(int month, int year){
		// =============== Verify Manager !!! ================//
		
		
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
		
		Set<User> usersManaged = this.getAuthenticatedUserModel().getUsers();

		List<HolidayDetailDTO> detailsDTO = new ArrayList<HolidayDetailDTO>();
		
		Set<HolidayManagedDetailsDTO> detailManagedDTO = new TreeSet<HolidayManagedDetailsDTO>();
		
		for(User u : usersManaged){
			// Clear all the list for the new user
			detailsDTO = new ArrayList<HolidayDetailDTO>();
			detailManagedDTO = new TreeSet<HolidayManagedDetailsDTO>();
			List<HolidayRequestDTO> requests = holidayRequestService.getRequestBetweenDates(first.toDate(), last.toDate(), u);
			for(HolidayRequestDTO req : requests){
				detailsDTO.addAll(req.details);
			}
			for(HolidayDetailDTO det : detailsDTO){
				DateTime current = new DateTime(det.day);
				// We remove 1 to first and add 1 to last in order to consider these day in the detail interval
				if(current.toDateMidnight().isAfter(first.toDateMidnight().minusDays(1)) && current.toDateMidnight().isBefore(last.toDateMidnight().plusDays(1))){
					detailManagedDTO.add(new HolidayManagedDetailsDTO(det.color, current.getDayOfMonth(), det.am, det.pm));
				}
				HolidayType type = this.getHolidayTypeFromBalanceId(det.balanceId);
				balancesDTO.add(new CalendarBalanceDetailDTO(type.getName(), 0, type.getColor() , type.getId()));
			}
			String fullname = u.getUserDetails().getFirstname() + " " + u.getUserDetails().getName();
			managedDTO.add(new HolidayUserManagedDTO(fullname, detailManagedDTO));
		}
		HolidayUsersManagerDTO mainDTO = new HolidayUsersManagerDTO(managedDTO, balancesDTO);
		mainDTO.nbDays = last.getDayOfMonth();
		mainDTO.month = monthNeeded;
		mainDTO.year = yearNeeded;
		return mainDTO;
	}
	
	private HolidayType getHolidayTypeFromBalanceId(int id){
		HolidayBalance balance = em.get().find(HolidayBalance.class, id);
		return balance.getHolidayType();
	}
}