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

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;

@Entity
@Table(name = "kernely_holiday_balance")
public class HolidayBalance extends AbstractModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private float available_balance;
	
	@Column(name="future_balance")
	private float futureBalance;
	
	@Column(name="last_update")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

	@ManyToOne
    @JoinColumn(name = "holiday_type_id")
	private HolidayType holidayType;

	@ManyToOne
    @JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(mappedBy = "balance")
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

	/**
	 * @return the available_balance
	 */
	public float getAvailable_balance() {
		return available_balance;
	}

	/**
	 * @param availableBalance the available_balance to set
	 */
	public void setAvailable_balance(float availableBalance) {
		available_balance = availableBalance;
	}

	/**
	 * @return the futureBalance
	 */
	public float getFutureBalance() {
		return futureBalance;
	}

	/**
	 * @param futureBalance the futureBalance to set
	 */
	public void setFutureBalance(float futureBalance) {
		this.futureBalance = futureBalance;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
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
