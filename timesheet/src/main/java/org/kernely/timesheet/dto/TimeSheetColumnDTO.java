package org.kernely.timesheet.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This DTO represents a line of the time sheet: a project and values for the seven days.
 */
@XmlRootElement
public class TimeSheetColumnDTO {

	/**
	 * The project
	 */
	public TimeSheetDayDTO day;

	/**
	 * Details associated to all days. Can contain empty cells.
	 */
	public List<TimeSheetDetailDTO> timeSheetDetails;

	/**
	 * Default constructor
	 */
	public TimeSheetColumnDTO(TimeSheetDayDTO day, List<TimeSheetDetailDTO> timeSheetDetails) {
		this.day = day;
		this.timeSheetDetails = timeSheetDetails;
	}
	
	/**
	 * Default constructor without arguments
	 */
	public TimeSheetColumnDTO(){
	}
}