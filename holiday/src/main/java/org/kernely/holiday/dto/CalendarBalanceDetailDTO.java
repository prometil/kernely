package org.kernely.holiday.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The dto for calendar detail
 * @author b.grandperret
 *
 */
@XmlRootElement
public class CalendarBalanceDetailDTO {
	/**
	 * The color of the day selected
	 */
	public String color;
	
	/**
	 * name of holiday type
	 */
	public String nameOfType;
	
	/**
	 * Number of day of holiday still available
	 */
	public int nbAvailable;
	
	/**
	 * Id of type of holiday
	 */
	public int idOfType;
	
	/**
	 * Default constructor
	 */
	public CalendarBalanceDetailDTO(){}
	
	/**
	 * Constructor
	 * @param name
	 * @param available
	 * @param color
	 * @param idType
	 */
	public CalendarBalanceDetailDTO(String name, int available, String color, int idType){
		this.color = color;
		this.nameOfType = name;
		this.nbAvailable = available;
		this.idOfType = idType;
	}
}
