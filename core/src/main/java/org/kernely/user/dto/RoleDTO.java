package org.kernely.user.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for role data, contains only his name.
 */
@XmlRootElement
public class RoleDTO {
	
	/**
	 * Constructor which set datas of the role.
	 * @param pName The name of the role.
	 */
	public RoleDTO(String pName) {
		name = pName;
	}

	public String name;
}
