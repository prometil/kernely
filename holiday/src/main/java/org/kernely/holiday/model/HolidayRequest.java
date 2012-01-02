/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.kernely.holiday.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;

/**
 * The holiday request model
 * @author b.grandperret
 *
 */
@Entity
@Table(name = "kernely_holiday_request")
public class HolidayRequest extends AbstractModel {
	
	public static final int DENIED_STATUS = 0;
	public static final int ACCEPTED_STATUS = 1;
	public static final int PENDING_STATUS = 2;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name="begin_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date beginDate;
	
	@Column(name="end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	private int status;
	
	@Column(name="requester_comment")
	private String requesterComment;

	@Column(name="manager_comment")
	private String managerComment;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(mappedBy = "request")
	private Set<HolidayRequestDetail> details;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	
	
	/**
	 * @return the beginDate
	 */
	public Date getBeginDate() {
		return new DateTime(beginDate).withZone(DateTimeZone.UTC).toDate();
	}

	/**
	 * @param beginDate the beginDate to set
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = new DateTime(beginDate).withZone(DateTimeZone.UTC).toDate();
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return (Date)endDate.clone();
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = (Date)endDate.clone();
	}

	/**
	 * @return the requesterComment
	 */
	public String getRequesterComment() {
		return requesterComment;
	}

	/**
	 * @param requesterComment the requesterComment to set
	 */
	public void setRequesterComment(String requesterComment) {
		this.requesterComment = requesterComment;
	}

	/**
	 * @return the managerComment
	 */
	public String getManagerComment() {
		return managerComment;
	}

	/**
	 * @param managerComment the managerComment to set
	 */
	public void setManagerComment(String managerComment) {
		this.managerComment = managerComment;
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
	public Set<HolidayRequestDetail> getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(Set<HolidayRequestDetail> details) {
		this.details = details;
	}

	
}
