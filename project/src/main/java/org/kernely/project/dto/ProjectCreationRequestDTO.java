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
	public long id;
	
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
	public ProjectCreationRequestDTO(String newName, long id, String newIcon, String newOrganization){
		this.id = id;
		this.name = newName;
		this.icon = newIcon; 
		this.organization = newOrganization;
	}
}
