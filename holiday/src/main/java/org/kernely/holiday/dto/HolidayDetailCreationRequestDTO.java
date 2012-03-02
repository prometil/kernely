package org.kernely.holiday.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Dto for the creation of holiday detail
 * @author b.grandperret
 *
 */
@XmlRootElement
public class HolidayDetailCreationRequestDTO {
	
	/**
	 * The day of holiday
	 */
	public String day;
	
	/**
	 * If the morning is take or not
	 */
	public boolean am;
	
	/**
	 * If the afternoon is take or not
	 */
	public boolean pm;
	
	/**
	 * The id of holiday type
	 */
	public long typeInstanceId;

	/**
	 * Default constructor
	 */
	public HolidayDetailCreationRequestDTO(){
		
	}
	
	
}
