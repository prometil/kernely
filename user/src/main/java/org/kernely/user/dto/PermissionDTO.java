package org.kernely.user.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for permission data, contains only his name.
 */
@XmlRootElement
public class PermissionDTO {
	
	/**
	 * Constructor which set datas of the permission.
	 * @param pName The name of the permission.
	 */
	public PermissionDTO(String pName) {
		name = pName;
	}

	public String name;
}