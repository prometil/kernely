package org.kernely.timesheet.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.timesheet.model.TimeSheet;

@XmlRootElement
public class TimeSheetDTO {

	/**
	 * User details of the user associated to this timesheet
	 */
	public UserDetailsDTO userDetails;

	/**
	 * First day of the timesheet.
	 */
	public Date begin;

	/**
	 * Last day of the timesheet.
	 */
	public Date end;

	/**
	 * Status of the time sheet, using TimeSheet constants.
	 */
	public int status;

	/**
	 * Status of fees associated to the time sheet, using TimeSheet constants.
	 */
	public int feesStatus;

	/**
	 * Default constructor of the DTO
	 * 
	 * @param timeSheet
	 *            The time sheet model
	 */
	public TimeSheetDTO(TimeSheet timeSheet) {
		this.userDetails = new UserDetailsDTO(timeSheet.getUser().getUserDetails());
		
		this.begin = timeSheet.getBeginDate();
		this.end = timeSheet.getEndDate();
		this.status = timeSheet.getStatus();
		this.feesStatus = timeSheet.getFeesStatus();

	}

	/**
	 * Default constructor
	 */
	public TimeSheetDTO() {
	}
	
	

}
