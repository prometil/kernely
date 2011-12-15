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

import org.kernely.core.hibernate.AbstractModel;

@Entity
@Table(name = "kernely_holiday_balance")
public class HolidayBalance extends AbstractModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private float available_balance;
	private float future_balance;
	private Date last_update;
	private String period_unit;
	private int effective_month;

	@ManyToOne
	@JoinColumn(name = "fk_holiday_type")
	private HolidayType holidayType;

	@ManyToOne
	@JoinColumn(name = "fk_user")
	private HolidayType user;

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
	 * @return the period unit
	 */
	public String getPeriodUnit() {
		return period_unit;
	}

	/**
	 * @param periodUnit
	 *            the period unit to set
	 */
	public void setPeriodUnit(String periodUnit) {
		period_unit = periodUnit;
	}

	/**
	 * @return the effective_month
	 */
	public int getEffectiveMonth() {
		return effective_month;
	}

	/**
	 * @param effectiveMonth
	 *            the effective month to set
	 */
	public void setEffectiveMonth(int effectiveMonth) {
		effective_month = effectiveMonth;
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
	public HolidayType getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(HolidayType user) {
		this.user = user;
	}

}
