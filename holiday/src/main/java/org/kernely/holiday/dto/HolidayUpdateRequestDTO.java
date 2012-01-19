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

/**
 * The holiday update request DTO
 * @author b.grandperret
 *
 */
@XmlRootElement
public class HolidayUpdateRequestDTO {

	/**
	 * The id of holiday
	 */
	public int id;
	
	/**
	 * The type of holiday
	 */
	public String type;
	
	/**
	 * The quantity of day of holiday
	 */
	public int quantity;
	
	/**
	 * The frequency 
	 */
	public int frequency;
	
	/**lidayUpdateRequestDTO
	 * Unity of the quantity month / year
	 */
	public int unity;
	
	/**
	 * The month when the quantity of holiday is give to an employee
	 */
	public int effectiveMonth;
	
	/**
	 * Is the holiday have anticipation 
	 */
	public boolean anticipation;
	
	/**
	 * The color for the Type
	 */
	public String color;

	/**
	 * Default constructor 
	 */
	public HolidayUpdateRequestDTO() {

	}

	/**
	 * create a dto for update
	 * 
	 * @param newId
	 * @param newType
	 * @param newFrequency
	 */
	public HolidayUpdateRequestDTO(long newId, String newType, int quantity, int newUnity, int effectiveMonth, boolean anticipation, String color) {
		this.id = (int) newId;
		this.type = newType;
		this.quantity = quantity;
		this.unity = newUnity;
		this.effectiveMonth = effectiveMonth;
		this.anticipation = anticipation;
		this.color=color;
	}

}
