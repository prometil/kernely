package org.kernely.holiday.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CalendarDayDTO {
	public int id;
	public String day;
	public boolean morningAvailable;
	public boolean afternoonAvailable;
	public int week;
	
	public CalendarDayDTO(){}
	
	public CalendarDayDTO(String day, boolean mAvailable, boolean aAvailable,int week){
		this.day = day;
		this.morningAvailable = mAvailable;
		this.afternoonAvailable = aAvailable;
		this.week = week;
	}
}
