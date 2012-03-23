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
	 * The value for the month (1 = January, 12 = December).
	 */
	public int month;

	/**
	 * The year.
	 */
	public int year;

	public TimeSheetMonthDTO() {}
	
	public TimeSheetMonthDTO(List<TimeSheetCalendarDTO> calendars, int month, int year) {
		super();
		this.calendars = calendars;
		this.month = month;
		this.year = year;
	}
	
}
