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

package org.kernely.holiday.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.kernely.holiday.model.HolidayBalance;

/**
 * Dto for holiday
 * 
 * @author b.grandperret
 * 
 */
@XmlRootElement
public class HolidayBalanceDTO implements Comparable<HolidayBalanceDTO> {

	/**
	 * The holiday id
	 */
	public int id;

	/**
	 * Available balance for this type of holiday
	 */
	public float availableBalance;

	/**
	 * Available balance updated with pending and accepted requests
	 */
	public float availableBalanceUpdated;

	/**
	 * The effective month
	 */
	public int effectiveMonth;

	/**
	 * The last update
	 */
	public Date lastUpdate;

	/**
	 * Begin effective date of this balance
	 */
	public Date beginDate;

	/**
	 * End effective date of this balance
	 */
	public Date endDate;

	/**
	 * Default constructor
	 */
	public HolidayBalanceDTO() {

	}

	/**
	 * Constructor
	 * 
	 * @param balance
	 *            the HolidayBalance model
	 */
	public HolidayBalanceDTO(HolidayBalance balance) {
		this.id = balance.getId();

		// Divide balances by 12 because in database, balances are in twelths of
		// days.
		this.availableBalance = ((float) balance.getAvailableBalance()) / 12.0F;
		this.availableBalanceUpdated = ((float) balance.getAvailableBalanceUpdated()) / 12.0F;
		this.lastUpdate = balance.getLastUpdate();
		this.effectiveMonth = balance.getHolidayType().getEffectiveMonth();
		this.beginDate = balance.getBeginDate();
		this.endDate = balance.getEndDate();
	}

	@Override
	public int compareTo(HolidayBalanceDTO other) {
		DateTime currentBegin = new DateTime(this.beginDate);
		DateTime currentEnd = new DateTime(this.endDate);
		DateTime otherBegin = new DateTime(other.beginDate);
		DateTime otherEnd = new DateTime(other.endDate);
		if (currentBegin.toDateMidnight().isBefore(otherBegin.toDateMidnight()) && currentEnd.toDateMidnight().isBefore(otherEnd.toDateMidnight())) {
			return -1;
		} else {
			if (currentBegin.toDateMidnight().isAfter(otherBegin.toDateMidnight()) && currentEnd.toDateMidnight().isAfter(otherEnd.toDateMidnight())) {
				return 1;
			} else {
				return 0;
			}

		}
	}

}
