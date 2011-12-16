package org.kernely.holiday.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HolidayDetailCreationRequestDTO {
	public Date day;
	public boolean am;
	public boolean pm;
	public int typeId;

	public HolidayDetailCreationRequestDTO(){
		
	}
	
	
}
