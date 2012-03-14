package org.kernely.timesheet.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for the creation of an expense type
 */
@XmlRootElement
public class ExpenseTypeCreationDTO {
	
	/**
	 * Id of this expense type. 0 if new type
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
	public ExpenseTypeCreationDTO(){}
	
	/**
	 * Constructs an expense type dto from an expense type model
	 * @param type The model to represent in this DTO
	 */
	public ExpenseTypeCreationDTO(long id, String name, boolean direct, float ratio){
		this.id = id;
		this.name = name;
		this.direct = direct;
		this.ratio = ratio;
	}
}
