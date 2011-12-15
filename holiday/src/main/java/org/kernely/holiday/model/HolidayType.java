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

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;

@Entity
@Table(name = "kernely_holiday_type")
public class HolidayType extends AbstractModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;
	private float quantity;
	private int period_number;
	private String period_unit;
	private int effective_month;
	
	@OneToMany(mappedBy = "holidayType")
	private Set<HolidayBalance> balances;


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
	 * @return the type
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the period number :
	 */
	public int getPeriodNumber() {
		return period_number;
	}

	/**
	 * @param the
	 *            period number
	 */
	public void setPeriodNumber(int periodNumber) {
		this.period_number = periodNumber;
	}

	/**
	 * @return the unit of the period number
	 */
	public String getPeriodUnit() {
		return period_unit;
	}

	/**
	 * @param unity
	 *            the unity to set. Use constants of Holiday class.
	 */
	public void setPeriodUnit(String unit) {
		this.period_unit = unit;
	}

	/**
	 * Get the amount of holiday gained each periodNumber of periodUnit
	 * 
	 * @return the quantity
	 */
	public float getQuantity() {
		return quantity;
	}

	/**
	 * Get the amount of holiday gained each periodNumber of periodUnit
	 * 
	 * @return the quantity
	 */
	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	/**
	 * Get the month where anticipated holidays become available holidays.
	 * 
	 * @return the month where anticipated holidays become available.
	 */
	public int getEffectiveMonth() {
		return effective_month;
	}

	/**
	 * Set the month where anticipated holidays become available holidays.
	 * 
	 * @param the
	 *            month where anticipated holidays become available. User HolidayType constants.
	 */
	public void setEffectiveMonth(int effectiveMonth) {
		effective_month = effectiveMonth;
	}

	/**
	 * @return the balances associated to this type
	 */
	public Set<HolidayBalance> getBalances() {
		return balances;
	}

	/**
	 * @param balances the balances (associated to this type) to set
	 */
	public void setBalances(Set<HolidayBalance> balances) {
		this.balances = balances;
	}
	
	

}
