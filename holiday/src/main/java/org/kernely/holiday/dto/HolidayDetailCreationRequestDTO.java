package org.kernely.holiday.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HolidayDetailCreationRequestDTO {
	public String day;
	public boolean am;
	public boolean pm;
	public int typeId;

	public HolidayDetailCreationRequestDTO(){
		
	}
	
	
}
