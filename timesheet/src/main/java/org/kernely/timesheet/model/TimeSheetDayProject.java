package org.kernely.timesheet.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.project.model.Project;

/**
 * TimeSheet Day Project model
 */
@Entity
@Table(name = "kernely_timesheet_day_project")
public class TimeSheetDayProject extends AbstractModel {

	private float amount;

	@ManyToOne
	@JoinColumn(name="timesheet_detail_id")
	private TimeSheetDetail timeSheetDetail;

	@ManyToOne
	@JoinColumn(name="project_id")
	private Project project;

	/**
	 * Initialize with default values
	 */
	public TimeSheetDayProject() {
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
	 * @return the timeSheetDetail
	 */
	public TimeSheetDetail getTimeSheetDetail() {
		return timeSheetDetail;
	}

	/**
	 * @param timeSheetDetail the timeSheetDetail to set
	 */
	public void setTimeSheetDetail(TimeSheetDetail timeSheetDetail) {
		this.timeSheetDetail = timeSheetDetail;
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
