package org.kernely.project.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Creation request DTO
 */
@XmlRootElement
public class ProjectCreationRequestDTO {
	/**
	 * The id of the project
	 */
	public int id;
	
	/**
	 * The  name of the project
	 */
	public String name;
	
	/**
	 * Default constructor
	 */
	public ProjectCreationRequestDTO(){
		
	}
	
	/**
	 * Constructor
	 */
	public ProjectCreationRequestDTO(String newName, int newId){
		this.id = newId;
		this.name = newName;
	}
}
