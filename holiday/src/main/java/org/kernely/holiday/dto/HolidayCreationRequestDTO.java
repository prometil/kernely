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
 * 
 * @author b.grandperret
 *
 */
@XmlRootElement
public class HolidayCreationRequestDTO {
	public String type;
	public int quantity;
	public int unity;
	public int effectiveMonth;

	public HolidayCreationRequestDTO(){
		
	}
	
	/**
	 * create an holiday request
	 * @param newType
	 * @param newFrequency
	 */
	public HolidayCreationRequestDTO(String newType, int newQuantity, int newUnity, int effectiveMonth){
		this.type=newType;
		this.quantity = newQuantity;
		this.unity = newUnity;
		this.effectiveMonth = effectiveMonth;
	}
	
}
