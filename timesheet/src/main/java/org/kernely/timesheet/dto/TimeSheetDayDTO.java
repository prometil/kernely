package org.kernely.timesheet.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This DTO contains the amount for the day and the id of the project.
 */
@XmlRootElement
public class TimeSheetDayDTO {

	/**
	 * Place in the week : 0 = monday, 6 = sunday
	 */
	public int index;
	
	/**
	 * Amount of time for this day and this project.
	 */
	public float amount;

	/**
	 * The date of the day
	 */
	public Date day;

	/**
	 * The id of the project associated to this amount and this day.
	 */
	public long projectId;

	/**
	 * The id of the detail, containing all dayProjects
	 */
	public long detailId;
	
	/**
	 * The id of the timesheet associated to this amount and this day.
	 */
	public long timeSheetId;
	

	/**
	 * Constructor using fields
	 * @param dayProjectId The id of the dayProject
	 * @param amount The amount of time.
	 * @param day The day.
	 * @param timeSheetId The id of the time sheet
	 * @param projectId The id of the project.
	 */
	public TimeSheetDayDTO(int index, long detailId, float amount, Date day, long timeSheetId, long projectId) {
		super();
		this.index = index;
		this.amount = amount;
		this.detailId = detailId;
		this.day = day;
		this.timeSheetId = timeSheetId;
		this.projectId = projectId;
	}



	/**
	 * Default constructor
	 */
	public TimeSheetDayDTO() {
	}
	
	

}
