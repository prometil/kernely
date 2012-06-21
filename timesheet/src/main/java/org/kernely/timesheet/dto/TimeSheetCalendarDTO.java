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
	 * List of projects id used last week, ordered alphabetically
	 */
	public List<Long> lastWeekProjectsId;
	
	/**
	 * Dates of the week, in Date format
	 */
	public List<Date> dates;
	
	/**
	 * Dates of the week, in String format
	 */
	public List<String> stringDates;

	/**
	 * Week concerned by this dto
	 */
	public int week;
	
	/**
	 * Year concerned by this dto
	 */
	public int year;

	/**
	 * Are the dates available?
	 * Non available dates are, for example, holidays.
	 */
	public List<Float> availableDates;
	
	public TimeSheetCalendarDTO(int week, int year, TimeSheetDTO timeSheet, List<Date> dates, List<String> stringDates, List<Float> availableDates, List<Long> projectsId, List<Long> lastWeekProjectsId) {
		this.week = week;
		this.year = year;
		this.timeSheet = timeSheet;
		this.dates = dates;
		this.stringDates = stringDates;
		this.projectsId = projectsId;
		this.lastWeekProjectsId = lastWeekProjectsId;
		this.availableDates = availableDates;
	}

	/**
	 * Default constructor
	 */
	public TimeSheetCalendarDTO() {
	}

	
	
}
