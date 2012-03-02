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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;

/**
 * Model holiday balance
 * 
 * @author b.grandperret
 * 
 */
@Entity
@Table(name = "kernely_holiday_balance")
public class HolidayBalance extends AbstractModel {
	@Column(name = "available_balance")
	private int availableBalance;

	@Column(name = "available_balance_updated")
	private int availableBalanceUpdated;

	@Column(name = "last_update")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

	@Column(name = "begin_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date beginDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@ManyToOne
	@JoinColumn(name = "holiday_type_instance_id")
	private HolidayTypeInstance holidayTypeInstance;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	/**
	 * @return the available balance
	 */
	public int getAvailableBalance() {
		return availableBalance;
	}

	/**
	 * @param availableBalance
	 *            the available balance to set
	 */
	public void setAvailableBalance(int availableBalance) {
		this.availableBalance = availableBalance;
	}

	/**
	 * @return the availableBalanceUpdated
	 */
	public int getAvailableBalanceUpdated() {
		return availableBalanceUpdated;
	}

	/**
	 * @param availableBalanceUpdated
	 *            the availableBalanceUpdated to set
	 */
	public void setAvailableBalanceUpdated(int availableBalanceUpdated) {
		this.availableBalanceUpdated = availableBalanceUpdated;
	}

	/**
	 * @return the holidayType
	 */
	public HolidayTypeInstance getHolidayTypeInstance() {
		return holidayTypeInstance;
	}

	/**
	 * @param holidayType
	 *            the holidayType to set
	 */
	public void setHolidayTypeInstance(HolidayTypeInstance holidayType) {
		this.holidayTypeInstance = holidayType;
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
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return (Date) lastUpdate.clone();
	}

	/**
	 * @param lastUpdate
	 *            the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = (Date) lastUpdate.clone();
	}

	/**
	 * @return the beginDate
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate
	 *            the beginDate to set
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
