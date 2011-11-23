package org.kernely.core.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GroupCreationRequestDTO {
	public GroupCreationRequestDTO(){
		
	}
	
	public GroupCreationRequestDTO(int id, String name, List<UserDTO> users){
		this.id = id;
		this.name = name;
		this.users = users;
	}
	
	public int id;
	public String name;
	public List<UserDTO> users;
}
