package org.kernely.timesheet.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kernely.persistence.AbstractModel;
import org.kernely.project.model.Project;

/**
 * TimeSheet Day Project model
 */
@Entity
@Table(name = "kernely_timesheet_detail_project")
public class TimeSheetDetailProject extends AbstractModel {

	private float amount;

	@ManyToOne
	@JoinColumn(name="timesheet_day_id")
	private TimeSheetDay timeSheetDay;

	@ManyToOne
	@JoinColumn(name="project_id")
	private Project project;

	/**
	 * Initialize with default values
	 */
	public TimeSheetDetailProject() {
		this.id = 0;
	}

	/**
	 * @return the amount
	 */
	public float getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}

	/**
	 * @return the timeSheetDay
	 */
	public TimeSheetDay getTimeSheetDay() {
		return timeSheetDay;
	}

	/**
	 * @param timeSheetDay the timeSheetDay to set
	 */
	public void setTimeSheetDay(TimeSheetDay timeSheetDay) {
		this.timeSheetDay = timeSheetDay;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}


}
