package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for group data, contains only his name.
 */
@XmlRootElement
public class GroupDTO {
	
	public GroupDTO(){
		
	}
	
	/**
	 * Constructor which set datas of the group.
	 * @param pName The name of the group.
	 */
	public GroupDTO(String pName, int id) {
		this.id = id;
		this.name = pName;
	}

	public int id;
	public String name;
}
