package org.kernely.holiday.dto;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO corresponding to a user managed, containg his fullname and a list of details of requests made
 */
@XmlRootElement
public class HolidayUserManagedDTO {
	/**
	 * Full name of the concerned user
	 */
	public String fullName;
	
	/**
	 * All details of the requests made by this user.
	 */
	public Set<HolidayManagedDetailsDTO> details;
	
	/**
	 * Default constructor
	 */
	public HolidayUserManagedDTO(){}
	
	/**
	 * Constructor 
	 * @param name Fullname of this user
	 * @param details Detail of the requests made by this user
	 */
	public HolidayUserManagedDTO(String name, Set<HolidayManagedDetailsDTO> details){
		this.fullName = name;
		this.details = details;
		
	}
}
