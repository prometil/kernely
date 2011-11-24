package org.kernely.core.dto;

import java.util.List;

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
	public GroupDTO(String pName, int id, List<UserDTO> users) {
		this.id = id;
		this.name = pName;
		this.users = users;
	}

	public int id;
	public String name;
	public List<UserDTO> users;
}
