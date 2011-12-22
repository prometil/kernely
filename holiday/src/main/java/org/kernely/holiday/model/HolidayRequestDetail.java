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

import org.joda.time.DateTime;
import org.kernely.core.hibernate.AbstractModel;

@Entity
@Table(name = "kernely_holiday_request_detail")
public class HolidayRequestDetail extends AbstractModel implements Comparable<HolidayRequestDetail> {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date day;
	private boolean am;
	private boolean pm;

	@ManyToOne
    @JoinColumn(name = "holiday_request_id")
	private HolidayRequest request;

	@ManyToOne
    @JoinColumn(name = "holiday_balance_id")
	private HolidayBalance balance;


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
	 * @return the am
	 */
	public boolean isAm() {
		return am;
	}

	/**
	 * @param am
	 *            the am to set
	 */
	public void setAm(boolean am) {
		this.am = am;
	}

	/**
	 * @return the pm
	 */
	public boolean isPm() {
		return pm;
	}

	/**
	 * @param pm
	 *            the pm to set
	 */
	public void setPm(boolean pm) {
		this.pm = pm;
	}

	/**
	 * @return the request
	 */
	public HolidayRequest getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(HolidayRequest request) {
		this.request = request;
	}

	/**
	 * @return the type
	 */
	public HolidayBalance getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(HolidayBalance balance) {
		this.balance = balance;
	}

	@Override
	public int compareTo(HolidayRequestDetail otherRequest) {
		DateTime dt1 = new DateTime(this.day);
		DateTime dt2 = new DateTime(otherRequest.getDay());
		if(dt1.isBefore(dt2)){
			return -1;
		}
		else{
			if(dt1.isAfter(dt2)){
				return 1;
			}
			else{
				return 0;
			}
		}
	}

	
}
