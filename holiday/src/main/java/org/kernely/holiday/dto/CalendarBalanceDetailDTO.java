package org.kernely.holiday.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CalendarBalanceDetailDTO {
	public String color;
	public String nameOfType;
	public int nbAvailable;
	
	public CalendarBalanceDetailDTO(){}
	
	public CalendarBalanceDetailDTO(String name, int available, String color){
		this.color = color;
		this.nameOfType = name;
		this.nbAvailable = available;
	}
}
