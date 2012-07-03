package org.kernely.holiday.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO containing all informations about managed users and their requests
 */
@XmlRootElement
public class HolidayUsersManagerDTO {
	/**
	 * List of all users managed by the current user with their active requests
	 */
	public List<HolidayUserManagedDTO> usersManaged;
	
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
	 * A list of all not working days in the month
	 */
	public List<Integer> weekends = new ArrayList<Integer>();
	
	/**
	 * Default constructor
	 */
	public HolidayUsersManagerDTO(){}
	
	
	/**
	 * Constructor
	 * @param users List of all user managed by the current user
	 * @param balances List of all balances available by all users
	 */
	public HolidayUsersManagerDTO(List<HolidayUserManagedDTO> users){
		this.usersManaged = users;
	}
}
