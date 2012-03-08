package org.kernely.timesheet.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TimeSheetCalendarDTO {

	
	/**
	 * The time sheet
	 */
	public TimeSheetDTO timeSheet;
	
	/**
	 * Dates of the week, in Date format
	 */
	public List<Date> dates;
	
	/**
	 * Dates of the week, in String format
	 */
	public List<String> stringDates;

	public TimeSheetCalendarDTO(TimeSheetDTO timeSheet, List<Date> dates, List<String> stringDates) {
		
		this.timeSheet = timeSheet;
		this.dates = dates;
		this.stringDates = stringDates;
	}

	/**
	 * Default constructor
	 */
	public TimeSheetCalendarDTO() {
	}

	
	
}
