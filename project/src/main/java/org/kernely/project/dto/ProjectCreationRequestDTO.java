package org.kernely.project.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Creation request DTO of project
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
	 * The icon of the project
	 */
	public String icon ;
	
	/**
	 * The organization that the project belong
	 */
	public String organization;
	
	/**
	 * Default constructor
	 */
	public ProjectCreationRequestDTO(){
		
	}
	
	/**
	 * Constructor
	 */
	public ProjectCreationRequestDTO(String newName, int newId, String newIcon, String newOrganization){
		this.id = newId;
		this.name = newName;
		this.icon = newIcon; 
		this.organization = newOrganization;
	}
}
