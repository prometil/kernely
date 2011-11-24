package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for role data, contains only his name.
 */
@XmlRootElement
public class RoleDTO {
	
	/**
	 * Default Constructor
	 */
	public RoleDTO(){
		
	}
	
	/**
	 * Constructor which set datas of the role.
	 * @param pName The name of the role.
	 */
	public RoleDTO(int id, String pName) {
		this.id = id;
		name = pName;
	}

	public int id;
	public String name;
}
