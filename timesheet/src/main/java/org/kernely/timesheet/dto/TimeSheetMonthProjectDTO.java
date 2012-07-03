package org.kernely.timesheet.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *	Represents the calendar for one timesheet, containing dates of the whole week.
 */
@XmlRootElement
public class TimeSheetMonthProjectDTO {

	/**
	 * Id of the project
	 */
	public long projectId;
	
	/**
	 * Name of the project
	 */
	public String projectName;
	
	/**
	 * List of days.
	 */
	public List<TimeSheetDetailDTO> details;

	public TimeSheetMonthProjectDTO(long projectId, String projectName, List<TimeSheetDetailDTO> details) {
		this.projectId = projectId;
		this.projectName = projectName;
		this.details = details;
	}

	/**
	 * Default constructor
	 */
	public TimeSheetMonthProjectDTO() {
	}

	
	
}
