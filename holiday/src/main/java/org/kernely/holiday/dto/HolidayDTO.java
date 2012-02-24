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

import org.kernely.holiday.model.HolidayType;


/**
 * dto for holiday  
 * @author b.grandperret
 *
 */
@XmlRootElement
public class HolidayDTO {

	/**
	 * The id of the holiday
	 */
	public int id;
	
	/**
	 * THe name of the holiday
	 */
	public String name;
	
	/**
	 * An unlimited holiday type should not have quantity or period unit set.
	 * Unlimited holiday types are for example maternity leave or sick leave.
	 */
	public boolean unlimited;
	
	/**
	 * the quantity of day earn by month / year
	 */
	public int quantity;
	
	/**
	 * The unity, month or year
	 */
	public int periodUnit;
	
	/**
	 * The anticipation is available, yes or no 
	 */
	public boolean anticipation;
	
	/**
	 * The month where all the holiday are gain
	 */
	public int effectiveMonth;
	
	/**
	 * The color associated to the type
	 */
	public String color;
	
	/**
	 * default constructor
	 */
	public HolidayDTO(){
		
	}

	/**
	 * constructor
	 * @param name
	 * @param quantity
	 * @param periodUnit
	 * @param id
	 * @param anticipation
	 * @param effectiveMonth
	 */
	public HolidayDTO(String name, boolean unlimited, int quantity, int periodUnit, int id, boolean anticipation, int effectiveMonth, String color) {
		super();
		
		this.id = id;
		this.name = name;
		this.unlimited = unlimited;
		this.quantity = quantity;
		this.periodUnit = periodUnit;
		this.anticipation = anticipation;
		this.effectiveMonth = effectiveMonth;
		this.color = color;
	}
	
	/**
	 * Contruct with a HolidayType as parameter
	 * @param type The model HolidayType to construct the DTO
	 */
	public HolidayDTO(HolidayType type){
		this.id = type.getId();
		this.name = type.getName();
		this.quantity = type.getQuantity();
		this.periodUnit = type.getPeriodUnit();
		this.anticipation = type.isAnticipated();
		this.unlimited = type.isUnlimited();
		this.effectiveMonth = type.getEffectiveMonth();
		this.color = type.getColor();
	}
	
}
