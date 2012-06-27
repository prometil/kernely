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
	 * Id of the holiday type concerned by the morning of this day
	 */
	public long morningHolidayTypeId;
	
	/**
	 * Id of the holiday type concerned by the afternoon of this day
	 */
	public long afternoonHolidayTypeId;
	
	/**
	 * Color of the holiday type concerned by the morning of this day
	 */
	public String morningHolidayTypeColor;
	
	/**
	 * Color of the holiday type concerned by the afternoon of this day
	 */
	public String afternoonHolidayTypeColor;
	
	/**
	 * Name of the holiday type concerned by the morning of this day
	 */
	public String morningHolidayTypeName;
	
	/**
	 * Name of the holiday type concerned by the afternoon of this day
	 */
	public String afternoonHolidayTypeName;
	
	/**
	 * Defines if this morning is charged or not in the timesheet
	 */
	public boolean morningCharged = false;
	
	/**
	 * Defines if this morning is charged or not in the timesheet
	 */
	public boolean afternoonCharged = false;
		
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
