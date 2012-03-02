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
public class HolidayDetailDTO implements Comparable<HolidayDetailDTO>{

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
	 * The id of the type
	 */
	public int typeInstanceId;
	
	/**
	 * The color associated to the type of balance
	 */
	public String color;
	
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
		this.type = detail.getTypeInstance().getName();
		this.typeInstanceId = detail.getTypeInstance().getId();
		this.color = detail.getTypeInstance().getColor();
	}

	/**
	 * @param another
	 * @return
	 * @see java.util.Date#compareTo(java.util.Date)
	 */
	@Override
	public int compareTo(HolidayDetailDTO another) {
		return this.day. compareTo(another.day);
	}
}
