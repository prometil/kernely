package org.kernely.timesheet.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.project.dto.ProjectDTO;
/**
 * This DTO represents a line of the time sheet: a project and values for the seven days.
 */
@XmlRootElement
public class TimeSheetRowDTO {

	/**
	 * The project
	 */
	public ProjectDTO project;

	/**
	 * Details associated to all days. Can contain empty cells.
	 */
	public List<TimeSheetDayDTO> timeSheetDays;

	/**
	 * Default constructor
	 */
	public TimeSheetRowDTO(ProjectDTO project, List<TimeSheetDayDTO> timeSheetDays) {
		this.project = project;
		this.timeSheetDays = timeSheetDays;
	}
	
	/**
	 * Default constructor without arguments
	 */
	public TimeSheetRowDTO(){
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeSheetRowDTO other = (TimeSheetRowDTO) obj;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (project.id != other.project.id)
			return false;
		return true;
	}
	
	
	
	

}
