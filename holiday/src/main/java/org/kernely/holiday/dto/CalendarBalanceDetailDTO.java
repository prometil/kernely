package org.kernely.holiday.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CalendarBalanceDetailDTO {
	public String color;
	public String nameOfType;
	public int nbAvailable;
	public int idOfType;
	
	public CalendarBalanceDetailDTO(){}
	
	public CalendarBalanceDetailDTO(String name, int available, String color, int idType){
		this.color = color;
		this.nameOfType = name;
		this.nbAvailable = available;
		this.idOfType = idType;
	}
}
