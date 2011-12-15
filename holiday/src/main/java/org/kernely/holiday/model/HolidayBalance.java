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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;

@Entity
@Table(name = "kernely_holiday_balance")
public class HolidayBalance extends AbstractModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private float available_balance;
	private float future_balance;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date last_update;

	@ManyToOne
    @JoinColumn(name = "holiday_type_id")
	private HolidayType holidayType;

	@ManyToOne
    @JoinColumn(name = "user_id")
	private User user;

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
	 * @return the available balance
	 */
	public float getAvailableBalance() {
		return available_balance;
	}

	/**
	 * @param availableBalance
	 *            the available balance to set
	 */
	public void setAvailableBalance(float availableBalance) {
		available_balance = availableBalance;
	}

	/**
	 * @return the future_balance
	 */
	public float getFutureBalance() {
		return future_balance;
	}

	/**
	 * @param futureBalance
	 *            the future balance to set
	 */
	public void setFuturebalance(float futureBalance) {
		future_balance = futureBalance;
	}

	/**
	 * @return the last_update
	 */
	public Date getLastUpdate() {
		return last_update;
	}

	/**
	 * @param lastUpdate
	 *            the last date when the balance was updated
	 */
	public void setLastUpdate(Date lastUpdate) {
		last_update = lastUpdate;
	}

	/**
	 * @return the holidayType
	 */
	public HolidayType getHolidayType() {
		return holidayType;
	}

	/**
	 * @param holidayType
	 *            the holidayType to set
	 */
	public void setHolidayType(HolidayType holidayType) {
		this.holidayType = holidayType;
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

}
