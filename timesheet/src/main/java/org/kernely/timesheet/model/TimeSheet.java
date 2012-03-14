package org.kernely.timesheet.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;

/**
 * Time sheet model
 * 
 */
@Entity
@Table(name = "kernely_timesheet")
public class TimeSheet extends AbstractModel {
	public static final int TIMESHEET_PENDING = 0;
	public static final int TIMESHEET_TO_VALIDATE = 1;
	public static final int TIMESHEET_VALIDATED = 2;
	public static final int FEES_TO_VALIDATE = 0;
	public static final int FEES_VALIDATED = 1;

	private Date beginDate;
	private Date endDate;
	private int status;
	private int feesStatus;

	/**
	 * User concerned by this timesheet
	 */
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;


	@OneToMany
	@JoinColumn( name="timesheet_id")
	private List<TimeSheetDetail> details;
	
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
	 * @param status
	 *            The status of the timesheet. Use constants in TimeSheet model.
	 * @param feesStatus
	 *            The status of the timesheet. Uses constants in TimeSheet model.
	 * @param user
	 *            The user associated to this timesheet.
	 */
	public TimeSheet(Date begin, Date end, int status, int feesStatus, User user) {
		super();
		this.beginDate = begin;
		this.endDate = end;
		this.status = status;
		this.feesStatus = feesStatus;
		this.user = user;
		this.details = new ArrayList<TimeSheetDetail>(7);
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
	 * @return the feesStatus
	 */
	public int getFeesStatus() {
		return feesStatus;
	}

	/**
	 * @param feesStatus
	 *            the feesStatus to set
	 */
	public void setFeesStatus(int feesStatus) {
		this.feesStatus = feesStatus;
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
	 * @return the details
	 */
	public List<TimeSheetDetail> getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(List<TimeSheetDetail> details) {
		this.details = details;
	}

	
}
