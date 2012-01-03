package org.kernely.holiday.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author b.grandperret
 *
 */
@XmlRootElement
public class CalendarDayDTO {
	
	/**
	 * Id of the calendar day
	 */
	public int id;
	
	/**
	 * Name of the day
	 */
	public String day;
	
	/**
	 * If the morning is available or not? 
	 */
	public boolean morningAvailable;
	
	/**
	 * If the afternoon is Available
	 */
	public boolean afternoonAvailable;
	
	/**
	 * The week number
	 */
	public int week;
	
	/**
	 * Default constructor 
	 */
	public CalendarDayDTO(){
		
	}
	
	/**
	 * Constructor
	 * @param day
	 * @param mAvailable
	 * @param aAvailable
	 * @param week
	 */
	public CalendarDayDTO(String day, boolean mAvailable, boolean aAvailable,int week){
		this.day = day;
		this.morningAvailable = mAvailable;
		this.afternoonAvailable = aAvailable;
		this.week = week;
	}
}
