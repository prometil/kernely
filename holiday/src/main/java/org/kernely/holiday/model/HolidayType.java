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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kernely.persistence.AbstractModel;

/**
 * holiday type model
 */
@Entity
@Table(name = "kernely_holiday_type")
public class HolidayType extends AbstractModel {

	@ManyToOne
	@JoinColumn(name = "holiday_profile_id")
	private HolidayProfile holidayProfile;
	
	private String name;
	private int quantity;
	
	@Column(name="period_unit")
	private int periodUnit;

	@Column(name="effective_month")
	private int effectiveMonth;
	
	@OneToOne
	@JoinColumn(name="current_instance")
	private HolidayTypeInstance currentInstance;
	
	@OneToOne
	@JoinColumn(name="next_instance")
	private HolidayTypeInstance nextInstance;
	
	private boolean anticipated;
	
	private boolean unlimited;
	
	private String color;
	
	public static final int PERIOD_YEAR = 1;
	public static final int PERIOD_MONTH = 12;
	
	public static final int JANUARY = 1;
	public static final int FEBRUARY = 2;
	public static final int MARCH = 3;
	public static final int APRIL = 4;
	public static final int MAY = 5;
	public static final int JUNE = 6;
	public static final int JULY = 7;
	public static final int AUGUST = 8;
	public static final int SEPTEMBER = 9;
	public static final int OCTOBER = 10;
	public static final int NOVEMBER = 11;
	public static final int DECEMBER = 12;
	public static final int ALL_MONTH = 0;

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

	/**
	 * @return the unlimited
	 */
	public boolean isUnlimited() {
		return unlimited;
	}

	/**
	 * @param unlimited the unlimited to set
	 */
	public void setUnlimited(boolean unlimited) {
		this.unlimited = unlimited;
	}

	/**
	 * @return the profile
	 */
	public HolidayProfile getProfile() {
		return holidayProfile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(HolidayProfile profile) {
		this.holidayProfile = profile;
	}

	/**
	 * @return the currentInstance
	 */
	public HolidayTypeInstance getCurrentInstance() {
		return currentInstance;
	}

	/**
	 * @param currentInstance the currentInstance to set
	 */
	public void setCurrentInstance(HolidayTypeInstance currentInstance) {
		this.currentInstance = currentInstance;
	}

	/**
	 * @return the nextInstance
	 */
	public HolidayTypeInstance getNextInstance() {
		return nextInstance;
	}

	/**
	 * @param nextInstance the nextInstance to set
	 */
	public void setNextInstance(HolidayTypeInstance nextInstance) {
		this.nextInstance = nextInstance;
	}
}
