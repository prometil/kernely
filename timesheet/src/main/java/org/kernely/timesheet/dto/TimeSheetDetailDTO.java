package org.kernely.timesheet.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.timesheet.model.TimeSheetDetailProject;

/**
 * This DTO contains the amount for the day and the id of the project.
 */
@XmlRootElement
public class TimeSheetDetailDTO {

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
	 * The id of the day, containing all detailProjects
	 */
	public long dayId;
	
	/**
	 * The id of the timesheet associated to this amount and this day.
	 */
	public long timeSheetId;
	
	/**
	 * The name of the project concerned
	 */
	public String projectName;
	
	/**
	 * Status of the days (unavailable, taken by holidays, not working day...
	 */
	public String status;

	
	/**
	 * Constructor using fields
	 * @param dayId The id of the day
	 * @param amount The amount of time.
	 * @param day The day.
	 * @param timeSheetId The id of the time sheet
	 * @param projectId The id of the project.
	 */
	public TimeSheetDetailDTO(TimeSheetDetailProject detail) {
		super();
		this.amount = detail.getAmount();
		this.dayId = detail.getTimeSheetDay().getId();
		this.day = detail.getTimeSheetDay().getDay();
		this.timeSheetId = detail.getTimeSheetDay().getTimeSheet().getId();
		this.projectId = detail.getProject().getId();
		this.projectName = detail.getProject().getName();
	}
	
	/**
	 * Constructor using fields and setting the status of the detail.
	 * @param dayId The id of the day
	 * @param amount The amount of time.
	 * @param day The day.
	 * @param timeSheetId The id of the time sheet
	 * @param projectId The id of the project.
	 * @param status The status of the detail.
	 */
	public TimeSheetDetailDTO(TimeSheetDetailProject detail, String status) {
		super();
		this.amount = detail.getAmount();
		this.dayId = detail.getTimeSheetDay().getId();
		this.day = detail.getTimeSheetDay().getDay();
		this.timeSheetId = detail.getTimeSheetDay().getTimeSheet().getId();
		this.projectId = detail.getProject().getId();
		this.projectName = detail.getProject().getName();
		this.status = status;
	}


	/**
	 * Default constructor
	 */
	public TimeSheetDetailDTO() {
	}
	
	

}
