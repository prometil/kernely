package org.kernely.timesheet.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.kernely.persistence.AbstractModel;

/**
 * TimeSheetDetail model
 * 
 */
@Entity
@Table(name = "kernely_timesheet_day")
public class TimeSheetDay extends AbstractModel implements Comparable<TimeSheetDay> {

	public static final int DAY_TO_VALIDATE = 0;
	public static final int DAY_VALIDATED = 1;

	private Date day;

	private int status;

	@ManyToOne
	@JoinColumn(name = "timesheet_id")
	private TimeSheet timeSheet;

	@OneToMany
	@JoinColumn(name = "timesheet_day_id")
	private Set<TimeSheetDetailProject> detailsProjects;

	@OneToMany(mappedBy = "timeSheetDay")
	private Set<Expense> expenses;

	/**
	 * Initializes detail with default values
	 */
	public TimeSheetDay() {
		this.id = 0;
		this.timeSheet = null;
		this.day = null;
		this.detailsProjects = new HashSet<TimeSheetDetailProject>();
	}

	/**
	 * @return the day
	 */
	public Date getDay() {
		return day;
	}

	/**
	 * @param day
	 *            the day to set
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
	 * @param timeSheet
	 *            the timeSheet to set
	 */
	public void setTimeSheet(TimeSheet timeSheet) {
		this.timeSheet = timeSheet;
	}

	/**
	 * @return the detailsProjects
	 */
	public Set<TimeSheetDetailProject> getDetailsProjects() {
		return detailsProjects;
	}

	/**
	 * @param detailsProjects
	 *            the detailsProjects to set
	 */
	public void setDetailsProjects(Set<TimeSheetDetailProject> detailsProjects) {
		this.detailsProjects = detailsProjects;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the expenses
	 */
	public Set<Expense> getExpenses() {
		return expenses;
	}

	/**
	 * @param expenses
	 *            the expenses to set
	 */
	public void setExpenses(Set<Expense> expenses) {
		this.expenses = expenses;
	}

	@Override
	public int compareTo(TimeSheetDay tsd) {
		DateTime currentDay = new DateTime(this.day);
		DateTime otherDay = new DateTime(tsd.getDay());
		if (currentDay.isBefore(otherDay)) {
			return -1;
		} else {
			if (currentDay.isAfter(otherDay)) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
