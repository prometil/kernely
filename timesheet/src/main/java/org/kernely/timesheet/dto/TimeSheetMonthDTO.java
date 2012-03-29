package org.kernely.timesheet.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TimeSheetMonthDTO {

	/**
	 * The ordered list of time sheet calendars for the month.
	 */
	public List<TimeSheetCalendarDTO> calendars;
	
	/**
	 * Have the month been validated?
	 */
	public boolean validated;
	
	/**
	 * The value for the month (1 = January, 12 = December).
	 */
	public int month;

	/**
	 * The year.
	 */
	public int year;

	public TimeSheetMonthDTO() {}
	
	/**
	 * 
	 * @param calendars All calendars covering this month
	 * @param month The month.
	 * @param year The year.
	 * @param validated Have this month been validated?
	 */
	public TimeSheetMonthDTO(List<TimeSheetCalendarDTO> calendars, int month, int year, boolean validated) {
		super();
		this.calendars = calendars;
		this.month = month;
		this.year = year;
		this.validated = validated;
	}
	
}
