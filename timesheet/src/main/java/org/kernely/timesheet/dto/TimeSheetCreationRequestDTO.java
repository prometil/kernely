package org.kernely.timesheet.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO to create a new time sheet.
 */
@XmlRootElement
public class TimeSheetCreationRequestDTO {

	/**
	 * Id of the time sheet.
	 */
	public long id;
	
	/**
	 * First day of the time sheet.
	 */
	public Date begin;
	
	/**
	 * Last day of the timesheet.
	 */
	public Date end;
	
	/**
	 * The id of the user associated to this time sheet.
	 */
	public long userId;
	
	/**
	 * The status of the time sheet, using TimeSheet model constants.
	 */
	public int status;
	
	/**
	 * The status of the fees associated to the time sheet, using TimeSheet model constants.
	 */
	public int feesStatus;

	/**
	 * Default constructor
	 */
	public TimeSheetCreationRequestDTO() {
	}
	
	
	
}
