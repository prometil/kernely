package org.kernely.holiday.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.holiday.model.HolidayType;
import org.kernely.holiday.model.HolidayTypeInstance;

/**
 * DTO representing an holiday type or instance type
 */
@XmlRootElement
public class HolidayTypeDTO {

	/**
	 * Id of this type or instance type
	 */
	public long id;
	
	/**
	 * Name of this type or instance type
	 */
	public String name;
	
	/**
	 * Defines if this DTO represents a real type or an instance
	 */
	public boolean isInstance = false;
	
	/**
	 * Default constructor
	 */
	public HolidayTypeDTO(){}
	
	/**
	 * Builds a DTO based on the holiday type in parameter
	 * @param type The type of which this DTO will be based
	 */
	public HolidayTypeDTO(HolidayType type){
		this.id= type.getId();
		this.name = type.getName();
		this.isInstance = false;
	}
	
	/**
	 * Builds a DTO based on the holiday type instance in parameter
	 * @param type The type instance of which this DTO will be based
	 */	
	public HolidayTypeDTO(HolidayTypeInstance typeInstance){
		this.id = typeInstance.getId();
		this.name = typeInstance.getName();
		this.isInstance = true;
	}
	
	
}
