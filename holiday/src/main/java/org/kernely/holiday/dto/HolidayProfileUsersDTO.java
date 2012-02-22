package org.kernely.holiday.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.core.dto.UserDetailsDTO;

/**
 * The dto for holiday profile.
 */
@XmlRootElement
public class HolidayProfileUsersDTO {
	
	/**
	 * The unique id of the profile
	 */
	public int id;
	
	/**
	 * The name of the profile.
	 */
	public String name;

	/**
	 * Users which are associated to the profile
	 */
	public List<UserDetailsDTO> in;

	/**
	 * Users which are not associated to the profile
	 */
	public List<UserDetailsDTO> out;
	
	/**
	 * Default constructor
	 */
	public HolidayProfileUsersDTO(){
		
	}
	
	/**
	 * Create a profiles users DTO
	 * @param id the id of the profile
	 * @param in the list of users associated to the profile
	 * @param out the list of users not associated to the profile
	 * 
	 */
	public HolidayProfileUsersDTO(int id, List<UserDetailsDTO> in, List<UserDetailsDTO> out){
		this.id=id;
		this.in = in;
		this.out = out;
	}
}
