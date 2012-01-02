package org.kernely.holiday.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The creation request for holiday
 * @author b.grandperret
 *
 */
@XmlRootElement
public class HolidayRequestCreationRequestDTO {

	/**
	 * The comment of the requester
	 */
	public String requesterComment;
	
	/**
	 * The holiday details list 
	 */
	public List<HolidayDetailCreationRequestDTO> details;
	
	/**
	 * Default constructor
	 */
	public HolidayRequestCreationRequestDTO(){
		
	}
	
	
}
