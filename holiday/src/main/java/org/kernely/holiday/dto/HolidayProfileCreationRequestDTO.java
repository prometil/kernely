package org.kernely.holiday.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The dto for Holiday version  
 */
@XmlRootElement
public class HolidayProfileCreationRequestDTO {
	

	/**
	 * Id of the profile.
	 */
	public int id;

	/**
	 * The name of the profile.
	 */
	public String name;

	/**
	 * Types contained in the profile
	 */
	public List<Long> holidayTypesId;
	
	
	/**
	 * Default constructor
	 */
	public HolidayProfileCreationRequestDTO(){
		
	}
	
	/**
	 * create an holiday request
	 * @param newType
	 * @param newFrequency
	 */
	public HolidayProfileCreationRequestDTO(int id, String name, List<Long> holidayTypesId){
		this.id = id;
		this.name=name;
		this.holidayTypesId = holidayTypesId;
	}
}
