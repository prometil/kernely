package org.kernely.timesheet.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;

/**
 * TimeSheetDetail model
 * 
 */
@Entity
@Table(name = "kernely_timesheet_details")
public class TimeSheetDetail extends AbstractModel {
	private Date day;

	@ManyToOne
	@JoinColumn(name="timesheet_id")
	private TimeSheet timeSheet;

	
	@OneToMany
	@JoinColumn(name="timesheet_detail_id")
	private Set<TimeSheetDayProject> dayProjects;

	/**
	 * Initializes detail with default values
	 */
	public TimeSheetDetail() {
		this.id = 0;
		this.timeSheet = null;
		this.day = null;
		this.dayProjects = new HashSet<TimeSheetDayProject>();
	}

	/**
	 * @return the day
	 */
	public Date getDay() {
		return day;
	}

	/**
	 * @param day the day to set
	 */
	public void setDay(Date day) {
		this.day = day;
	}

	/**
	 * @return the timeSheet
	 */
	public TimeSheet getTimeSheet() {
		return timeSheet;
	}

	/**
	 * @param timeSheet the timeSheet to set
	 */
	public void setTimeSheet(TimeSheet timeSheet) {
		this.timeSheet = timeSheet;
	}

	/**
	 * @return the dayProjects
	 */
	public Set<TimeSheetDayProject> getDayProjects() {
		return dayProjects;
	}

	/**
	 * @param dayProjects the dayProjects to set
	 */
	public void setDayProjects(Set<TimeSheetDayProject> dayProjects) {
		this.dayProjects = dayProjects;
	}

	
}
