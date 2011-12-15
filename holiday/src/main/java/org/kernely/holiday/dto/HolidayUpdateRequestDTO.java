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

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class HolidayUpdateRequestDTO {
	public HolidayUpdateRequestDTO() {

	}
	/**
	 * create a dto  for update 
	 * 	@param newId
 	* @param newType
 	* @param newFrequency
 	*/
	public HolidayUpdateRequestDTO(int newId, String newType, int newFrequency, String newUnity) {
		this.id = newId;
		this.type = newType;
		this.frequency = newFrequency;
		this.unity = newUnity;
	}

	public int id;
	public String type;
	public int frequency;
	public String unity;
}
