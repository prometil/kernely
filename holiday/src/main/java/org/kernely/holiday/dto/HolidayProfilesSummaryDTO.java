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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Dto for holiday requests summary about one profile
 */
@XmlRootElement
public class HolidayProfilesSummaryDTO {

	/**
	 * The profile name
	 */
	public String name;

	/**
	 * The list of users summaries
	 */
	public List<HolidayUserSummaryDTO> usersSummaries;

	public HolidayProfilesSummaryDTO(String name, List<HolidayUserSummaryDTO> usersSummaries) {
		this.name = name;
		this.usersSummaries = usersSummaries;
	}

	/**
	 * Default constructor
	 */
	public HolidayProfilesSummaryDTO() {
	}



}
