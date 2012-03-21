package org.kernely.timesheet.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;

@XmlRootElement
public class TotalExpenseDTO implements Comparable<TotalExpenseDTO> {
	public float total;
	public Date day;
	
	public TotalExpenseDTO(){}
	
	public TotalExpenseDTO(float total, Date day){
		this.total = total;
		this.day = day;
	}

	@Override
	public int compareTo(TotalExpenseDTO other) {
		DateTime currentDay = new DateTime(this.day);
		DateTime otherDay = new DateTime(other.day);
		if(currentDay.isBefore(otherDay)){
			return -1;
		}
		else{
			if(currentDay.isAfter(otherDay)){
				return 1;
			}
			else{
				return 0;
			}
		}
	}
}