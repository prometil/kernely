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
	 * The list of member of the project
	 */
	public List<UserDTO> users;
	
	/**
	 * Default constructor
	 */
	public ProjectDTO(){
		
	}
	
	/**
	 * Constructor
	 */
	public ProjectDTO(String newName, int newId, List<UserDTO> newUsers){
		this.id = newId;
		this.name = newName;
		this.users  = newUsers;

	}
}
