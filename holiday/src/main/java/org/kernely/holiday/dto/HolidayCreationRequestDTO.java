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
 * The dto for Holiday version  
 */
@XmlRootElement
public class HolidayCreationRequestDTO {
	
	/**
	 * The id of holiday type
	 */
	public int id;
	
	/**
	 * The name of the holiday type
	 */
	public String name;

	/**
	 * Is this type of holiday unlimited ?
	 */
	public boolean unlimited;
	
	/**
	 * The quantity of day of holiday per month/year
	 */
	public int quantity;
	
	/**
	 * Month or year
	 */
	public int unity;
	
	/**
	 * The month where the holiday are had
	 */
	public int effectiveMonth;
	
	/**
	 * If the holiday can be take in advance or not 
	 */
	public boolean anticipation;
	
	/**
	 * The color associated to the Type
	 */
	public String color;

	/**
	 * Default constructor
	 */
	public HolidayCreationRequestDTO(){
		
	}
	
	/**
	 * DTO for the creation or update of a holiday type
	 * @param name The name of the type.
	 * @param unlimited Is this type of holidays unlimited (can be take at will) ?
	 * @param quantity Quantity, in days, of earn (each unity)
	 * @param unity year or month. Use HolidayType constants.
	 * @param effectiveMonth The month when earned days become available. Use HolidayType constants.
	 * @param anticipation Can users take this type of holiday with anticipation ?
	 * @param color The color of the holiday, displayed when the user make a request for holidays. Hexadecimal value (#FFFFFF for example).
	 */
	public HolidayCreationRequestDTO(String name, boolean unlimited, int quantity, int unity, int effectiveMonth, boolean anticipation, String color){
		this.name=name;
		this.unlimited=unlimited;
		this.quantity = quantity;
		this.unity = unity;
		this.effectiveMonth = effectiveMonth;
		this.anticipation = anticipation;
		this.color = color;
	}
	
}
