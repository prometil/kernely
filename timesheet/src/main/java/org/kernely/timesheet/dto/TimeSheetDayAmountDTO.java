package org.kernely.timesheet.dto;

import java.util.Date;

/**
 * A DTO representing a day and how many hours taken on it.
 */
public class TimeSheetDayAmountDTO {
	
	/**
	 * The day concerned
	 */
	public Date day;
	
	/**
	 * The quantity of hours worked on this day
	 */
	public float amount;
	
	/**
	 * Default constructor
	 */
	public TimeSheetDayAmountDTO(){}
	
	/**
	 * Constructs a TimeSHeetDayAmountDTO with a given day and amount
	 * @param day The concerned day
	 * @param amount the quantity of hours worked on this day
	 */
	public TimeSheetDayAmountDTO(Date day, float amount){
		this.day = day;
		this.amount = amount;
	}

}
