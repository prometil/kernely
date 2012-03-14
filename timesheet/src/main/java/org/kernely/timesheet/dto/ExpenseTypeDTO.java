package org.kernely.timesheet.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.timesheet.model.ExpenseType;

/**
 * DTO representing an expense type for the time sheet
 */
@XmlRootElement
public class ExpenseTypeDTO {

	/**
	 * Id of this expense type
	 */
	public long id;
	
	/**
	 * Name of this expense type
	 */
	public String name;
	
	/**
	 * Define if this type has to do a conversion
	 */
	public boolean direct;
	
	/**
	 * Convertion ratio to have the amount of the expense
	 */
	public float ratio;
	
	/**
	 * Default constructor
	 */
	public ExpenseTypeDTO(){}
	
	/**
	 * Constructs an expense type dto from an expense type model
	 * @param type The model to represent in this DTO
	 */
	public ExpenseTypeDTO(ExpenseType type){
		this.id = type.getId();
		this.name = type.getName();
		this.direct = type.isDirect();
		this.ratio = type.getRatio();
	}
}
