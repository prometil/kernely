package org.kernely.holiday.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The calendar request DTO
 * @author b.grandperret
 *
 */
@XmlRootElement
public class CalendarRequestDTO {
	/**
	 * List of day of the calendar
	 */
	public List<CalendarDayDTO> days;
	
	/**
	 * The number of weeks 
	 */
	public int nbWeeks;
	
	/**
	 * The week of start
	 */
	public int startWeek;
	
	/**
	 * The list of details of the calendar
	 */
	public List<CalendarBalanceDetailDTO> details;
}
