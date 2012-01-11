package org.kernely.holiday.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO corresponding to a day of a request for a given user managed
 */
@XmlRootElement
public class HolidayManagedDetailsDTO implements Comparable<HolidayManagedDetailsDTO> {
	/**
	 * Color corresponding to the type of holiday linked to this detail
	 */
	public String color;
	
	/**
	 * Day of the month
	 */
	public int dayOfMonth;
	
	/**
	 * If this am has been reserved in the request
	 */
	public boolean am;

	/**
	 * If this am has been reserved in the request
	 */
	public boolean pm;
	
	/**
	 * Default constructor
	 */
	public HolidayManagedDetailsDTO(){}
	
	/**
	 * Constructor
	 * @param color the color associated to the type of this detail
	 * @param day the index of this day in the month
	 * @param am true if morning has been reserved
	 * @param pm true if afternoon has been reserved
	 */
	public HolidayManagedDetailsDTO(String color, int day, boolean am, boolean pm){
		this.color = color;
		this.dayOfMonth = day;		
		this.am = am;
		this.pm = pm;
	}

	@Override
	public int compareTo(HolidayManagedDetailsDTO d) {
		if(this.dayOfMonth < d.dayOfMonth){
			return -1;
		}
		if(this.dayOfMonth > d.dayOfMonth){
			return 1;
		}
		// this.dayOfMonth == d.dayOfMonth
		if((this.am && !d.am) || (!this.pm && d.pm)){
			return -1;
		}
		if((!this.am && d.am) || (this.pm && !d.pm)){
			return 1;
		}
		return 0;
	}
}
