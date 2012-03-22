package org.kernely.timesheet.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.timesheet.model.TimeSheet;

/**
 * This DTO represents the time sheet of a user, containing all details.
 */
@XmlRootElement
public class TimeSheetDTO {
	/**
	 * Unique id of the timesheet
	 */
	public long id;
	
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
	 * Columns for the timesheet
	 */
	public List<TimeSheetColumnDTO> columns;
	
	/**
	 * Default constructor of the DTO
	 * 
	 * @param timeSheet
	 *            The time sheet model. Don't fill rows.
	 */
	public TimeSheetDTO(TimeSheet timeSheet) {
		this.id = timeSheet.getId();
		this.userDetails = new UserDetailsDTO(timeSheet.getUser().getUserDetails());
		this.begin = timeSheet.getBeginDate();
		this.end = timeSheet.getEndDate();
	}
	
	/**
	 * Default constructor
	 */
	public TimeSheetDTO() {
	}
	
	

}
