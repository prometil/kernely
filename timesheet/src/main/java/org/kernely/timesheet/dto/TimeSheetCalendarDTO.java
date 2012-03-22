package org.kernely.timesheet.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *	Represents the calendar for one timesheet, containing dates of the whole week.
 */
@XmlRootElement
public class TimeSheetCalendarDTO {

	
	/**
	 * The time sheet
	 */
	public TimeSheetDTO timeSheet;
	
	/**
	 * List of projects id, ordered alphabetically
	 */
	public List<Long> projectsId;
	
	/**
	 * Dates of the week, in Date format
	 */
	public List<Date> dates;
	
	/**
	 * Dates of the week, in String format
	 */
	public List<String> stringDates;

	public TimeSheetCalendarDTO(TimeSheetDTO timeSheet, List<Date> dates, List<String> stringDates, List<Long> projectsId) {
		
		this.timeSheet = timeSheet;
		this.dates = dates;
		this.stringDates = stringDates;
		this.projectsId = projectsId;
	}

	/**
	 * Default constructor
	 */
	public TimeSheetCalendarDTO() {
	}

	
	
}
