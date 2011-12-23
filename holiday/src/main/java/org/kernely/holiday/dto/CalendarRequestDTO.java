package org.kernely.holiday.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CalendarRequestDTO {
	public List<CalendarDayDTO> days;
	public int nbWeeks;
	public int startWeek;
	public List<CalendarBalanceDetailDTO> details;
}
