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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;

/**
 * Holiday profile model, which contains holiday types
 */
@Entity
@Table(name = "kernely_holiday_profile")
public class HolidayProfile extends AbstractModel {
	private String name;

	@OneToMany(mappedBy = "holidayProfile")
	private Set<HolidayType> holidayTypes;

	@OneToMany
	@JoinTable(name = "kernely_holiday_profile_users", joinColumns = @JoinColumn(name = "holiday_profile_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> users;

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
	 * @return the holidayTypes
	 */
	public Set<HolidayType> getHolidayTypes() {
		return holidayTypes;
	}

	/**
	 * @param holidayTypes
	 *            the holidayTypes to set
	 */
	public void setHolidayTypes(Set<HolidayType> holidayTypes) {
		this.holidayTypes = holidayTypes;
	}

	/**
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
