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
 * dto for holiday  
 * @author b.grandperret
 *
 */
@XmlRootElement
public class HolidayDTO {
	public HolidayDTO(){
		
	}
	/**
	 * holiday dto
	 * @param newType
	 * @param newFrequency
	 * @param newId
	 */
	public HolidayDTO(String newType, int newFrequency, String newUnity, long  newId){
		this.type = newType ; 
		this.frequency = newFrequency;
		this.unity = newUnity;
		this.id=newId;
	}
	
	public String type;
	public int frequency;
	public String unity;
	public long id;
}
