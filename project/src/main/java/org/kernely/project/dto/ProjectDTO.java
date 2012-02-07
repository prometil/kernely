package org.kernely.project.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.core.dto.UserDTO;

/**
 * The project DTO
 */
@XmlRootElement
public class ProjectDTO {
	
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
	public String icon;
		
	/**
	 * The list of member of the project
	 */
	public List<UserDTO> users;
	
	/**
	 * The organization that the project belong
	 */
	public OrganizationDTO organization;
	
	/**
	 * Default constructor
	 */
	public ProjectDTO(){
		
	}
		
	/**
	 * Constructor
	 */
	public ProjectDTO(String newName, int newId, String newIcon, List<UserDTO> newUsers, OrganizationDTO newOrganization){
		this.id = newId;
		this.name = newName;
		this.users  = newUsers;
		this.icon = newIcon;
		this.organization  = newOrganization;
	}
}
