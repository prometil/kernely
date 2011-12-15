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

import javax.persistence.Column;
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
	private int id;

	private String name;
	private float quantity;
	
	@Column(name="period_number")
	private int periodNumber;
	
	@Column(name="period_unit")
	private String periodUnit;

	@Column(name="effective_month")
	private int effectiveMonth;
	
	@OneToMany(mappedBy = "holidayType")
	private Set<HolidayBalance> balances;


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

	/**
	 * @return the periodNumber
	 */
	public int getPeriodNumber() {
		return periodNumber;
	}

	/**
	 * @param periodNumber the periodNumber to set
	 */
	public void setPeriodNumber(int periodNumber) {
		this.periodNumber = periodNumber;
	}

	/**
	 * @return the periodUnit
	 */
	public String getPeriodUnit() {
		return periodUnit;
	}

	/**
	 * @param periodUnit the periodUnit to set
	 */
	public void setPeriodUnit(String periodUnit) {
		this.periodUnit = periodUnit;
	}

	/**
	 * @return the effectiveMonth
	 */
	public int getEffectiveMonth() {
		return effectiveMonth;
	}

	/**
	 * @param effectiveMonth the effectiveMonth to set
	 */
	public void setEffectiveMonth(int effectiveMonth) {
		this.effectiveMonth = effectiveMonth;
	}
	
	
	

}
