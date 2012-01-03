package org.kernely.holiday.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.holiday.model.HolidayRequestDetail;

/**
 * The holiday Detail dto
 * @author b.grandperret
 *
 */
@XmlRootElement
public class HolidayDetailDTO {

	/**
	 * The day of holiday 
	 */
	public Date day;
	
	/**
	 * If the holiday is take the morning or not
	 */
	public boolean am;
	
	/**
	 * If the holiday is take the afternoon or not
	 */
	public boolean pm;
	
	/**
	 * Type of holiday
	 */
	public String type;
	
	/**
	 * The id of the balance
	 */
	public int balanceId;
	
	/**
	 * Default constructor
	 */
	public HolidayDetailDTO(){
		
	}
	
	/**
	 * Constructor
	 * @param detail
	 */
	public HolidayDetailDTO(HolidayRequestDetail detail){
		this.day = detail.getDay();
		this.pm = detail.isPm();
		this.am = detail.isAm();
		this.type = detail.getBalance().getHolidayType().getName();
		this.balanceId = detail.getBalance().getId();
	}
}
