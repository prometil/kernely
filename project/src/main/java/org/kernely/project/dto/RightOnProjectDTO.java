package org.kernely.project.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * the project dto for the write
 * @author b.grandperret
 *
 */
@XmlRootElement
public class RightOnProjectDTO {

	/**
	 * The user or group id 
	 */
	public int id;

	/**
	 * The type of id : "group" or "user"
	 */
	public String idType;
	
	/**
	 * The type of permission for the user 
	 */
	public String permission;

	
	/**
	 * Default constructor
	 */
	public RightOnProjectDTO() {

	}

	/**
	 * Creates a RightOnProjectDTO
	 * 
	 * @param id
	 *            Id of the user or group
	 * @param idType
	 *            The type of id : "group" or "user"
	 * @param permission
	 *            Permission granted to the user ou group
	 */
	public RightOnProjectDTO(int id, String idType, String permission) {
		this.id = id;
		this.permission = permission;
		this.idType=idType;
	}

}