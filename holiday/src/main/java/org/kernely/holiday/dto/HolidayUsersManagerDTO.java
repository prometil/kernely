package org.kernely.holiday.dto;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HolidayUsersManagerDTO {
	/**
	 * List of all users managed by the current user with their active requests
	 */
	public List<HolidayUserManagedDTO> usersManaged;
	
	/**
	 * List of all balances available
	 */
	public Set<CalendarBalanceDetailDTO> balances;
	
	/**
	 * Number of days in the month
	 */
	public int nbDays;
	
	/**
	 * Current month
	 */
	public int month;
	
	/**
	 * Current year
	 */
	public int year;
	
	/**
	 * Default constructor
	 */
	public HolidayUsersManagerDTO(){}
	
	
	/**
	 * Constructor
	 * @param users List of all user managed by the current user
	 * @param balances List of all balances available by all users
	 */
	public HolidayUsersManagerDTO(List<HolidayUserManagedDTO> users, Set<CalendarBalanceDetailDTO> balances){
		this.usersManaged = users;
		this.balances = balances;
	}
}
