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
 * @author b.grandperret
 *
 */
@XmlRootElement
public class HolidayCreationRequestDTO {
	
	/**
	 * The type of holiday
	 */
	public String type;
	
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
	 * create an holiday request
	 * @param newType
	 * @param newFrequency
	 */
	public HolidayCreationRequestDTO(String newType, int newQuantity, int newUnity, int effectiveMonth, boolean anticipation, String color){
		this.type=newType;
		this.quantity = newQuantity;
		this.unity = newUnity;
		this.effectiveMonth = effectiveMonth;
		this.anticipation = anticipation;
		this.color = color;
	}
	
}
