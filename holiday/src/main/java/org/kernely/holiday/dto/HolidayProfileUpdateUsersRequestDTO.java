package org.kernely.holiday.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The dto for Holiday version  
 */
@XmlRootElement
public class HolidayProfileUpdateUsersRequestDTO {
	

	/**
	 * Id of the profile.
	 */
	public int id;

	/**
	 * Usernames of users associated to the profile.
	 */
	public List<String> usernames;
	
	
	/**
	 * Default constructor
	 */
	public HolidayProfileUpdateUsersRequestDTO(){
		
	}
	
	/**
	 * create an holiday request
	 * @param newType
	 * @param newFrequency
	 */
	public HolidayProfileUpdateUsersRequestDTO(int id, List<String> usernames){
		this.id = id;
		this.usernames = usernames;
	}
}
