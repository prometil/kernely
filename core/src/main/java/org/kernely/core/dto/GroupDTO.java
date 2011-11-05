package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for group data, contains only his name.
 */
@XmlRootElement
public class GroupDTO {
	
	/**
	 * Constructor which set datas of the group.
	 * @param pName The name of the group.
	 */
	public GroupDTO(String pName) {
		name = pName;
	}

	public String name;
}
