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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;

@Entity
@Table(name = "kernely_holiday_request")
public class HolidayRequest extends AbstractModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private Date begin_date;
	private Date end_date;
	private int status;
	private String requester_comment;
	private String manager_comment;

	@ManyToOne
	@JoinColumn(name = "fk_user")
	private User user;
	
	@OneToMany(mappedBy = "request")
	private Set<HolidayRequestDetail> details;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the begin_date
	 */
	public Date getBeginDate() {
		return begin_date;
	}

	/**
	 * @param beginDate
	 *            the begin_date to set
	 */
	public void setBeginDate(Date beginDate) {
		begin_date = beginDate;
	}

	/**
	 * @return the end_date
	 */
	public Date getEndDate() {
		return end_date;
	}

	/**
	 * @param endDate
	 *            the end_date to set
	 */
	public void setEndDate(Date endDate) {
		end_date = endDate;
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
	 * @return the requester_comment
	 */
	public String getRequesterComment() {
		return requester_comment;
	}

	/**
	 * @param requesterComment
	 *            the requester_comment to set
	 */
	public void setRequesterComment(String requesterComment) {
		requester_comment = requesterComment;
	}

	/**
	 * @return the manager_comment
	 */
	public String getManagerComment() {
		return manager_comment;
	}

	/**
	 * @param managerComment
	 *            the manager_comment to set
	 */
	public void setManagerComment(String managerComment) {
		manager_comment = managerComment;
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
