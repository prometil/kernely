package org.kernely.timesheet.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.kernely.timesheet.model.TimeSheetDay;

/**
 * Representation for a day of a time sheet
 */
@XmlRootElement
public class TimeSheetDayDTO {

	/**
	 * Id of this day
	 */
	public long id;
	
	/**
	 * Day under String format
	 */
	public String dayString;
	
	/**
	 * Day under date format
	 */
	public Date day;
	
	/**
	 * Time sheet's id
	 */
	public long timeSheetId;
	
	
	/**
	 * Default constructor
	 */
	public TimeSheetDayDTO(){}

	/**
	 * Construct a DTO based on a model of TimeSheetDetail
	 * @param detail The model to represent in this DTO
	 */
	public TimeSheetDayDTO(TimeSheetDay detail){
		this.id = detail.getId();
		this.day = detail.getDay();
		this.dayString = new DateTime(this.day).toString("MM/dd/yyyy");
		this.timeSheetId = detail.getTimeSheet().getId();
	}
}