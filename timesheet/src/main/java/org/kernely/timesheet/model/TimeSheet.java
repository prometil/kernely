package org.kernely.timesheet.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kernely.core.model.User;
import org.kernely.persistence.AbstractModel;

/**
 * Time sheet model
 * 
 */
@Entity
@Table(name = "kernely_timesheet")
public class TimeSheet extends AbstractModel {

	private Date beginDate;
	private Date endDate;

	/**
	 * User concerned by this timesheet
	 */
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;


	@OneToMany
	@JoinColumn( name="timesheet_id")
	private Set<TimeSheetDay> days;
	
	/**
	 * Default constructor
	 */

	public TimeSheet() {

	}

	/**
	 * Constructor with fields.
	 * 
	 * @param begin
	 *            The first dayof the timesheet
	 * @param end
	 *            The last day of the timesheet
	 * @param user
	 *            The user associated to this timesheet.
	 */
	public TimeSheet(Date begin, Date end, User user) {
		super();
		this.beginDate = begin;
		this.endDate = end;
		this.user = user;
		this.days = new HashSet<TimeSheetDay>(7);
	}

	/**
	 * @return the first day of the timesheet
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @param begin
	 *            the first day of the timesheet
	 */
	public void setBeginDate(Date begin) {
		this.beginDate = begin;
	}

	/**
	 * @return the last day of the timesheet
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param end
	 *            the last day of the timesheet
	 */
	public void setEndDate(Date end) {
		this.endDate = end;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the days
	 */
	public Set<TimeSheetDay> getDays() {
		return new TreeSet<TimeSheetDay>(days);
	}

	/**
	 * @param days the days to set
	 */
	public void setDays(Set<TimeSheetDay> days) {
		this.days = days;
	}

	
}
