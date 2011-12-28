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

/**
 * holiday type model
 * @author b.grandperret
 *
 */
@Entity
@Table(name = "kernely_holiday_type")
public class HolidayType extends AbstractModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String name;
	private int quantity;
	
	@Column(name="period_unit")
	private int periodUnit;

	@Column(name="effective_month")
	private int effectiveMonth;
	
	private boolean anticipated;
	
	@OneToMany(mappedBy = "holidayType")
	private Set<HolidayBalance> balances;
	
	private String color;
	
	public static final int PERIOD_YEAR = 1;
	public static final int PERIOD_MONTH = 12;
	
	public static final int JANUARY = 0;
	public static final int FEBRUARY = 1;
	public static final int MARCH = 2;
	public static final int APRIL = 3;
	public static final int MAY = 4;
	public static final int JUNE = 5;
	public static final int JULY = 6;
	public static final int AUGUST = 7;
	public static final int SEPTEMBER = 8;
	public static final int OCTOBER = 9;
	public static final int NOVEMBER = 10;
	public static final int DECEMBER = 11;
	public static final int ALL_MONTH = 12;

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
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Get the amount of holiday gained each periodNumber of periodUnit
	 * 
	 * @return the quantity
	 */
	public void setQuantity(int quantity) {
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
	 * @return the periodUnit, a constant in this class
	 */
	public int getPeriodUnit() {
		return periodUnit;
	}

	/**
	 * @param periodUnit the periodUnit to set: use constants in this class
	 */
	public void setPeriodUnit(int periodUnit) {
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

	/**
	 * Return the state of the holiday: with anticipation or not.
	 * @return true if holidays can be taken with anticipation, false otherwise.
	 */
	public boolean isAnticipated() {
		return anticipated;
	}

	/**
	 * @param anticipated the anticipated to set
	 */
	public void setAnticipated(boolean anticipated) {
		this.anticipated = anticipated;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
	
	
	

}
