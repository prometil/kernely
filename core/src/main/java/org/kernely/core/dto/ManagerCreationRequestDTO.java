package org.kernely.core.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ManagerCreationRequestDTO {
	public String manager;
	public List<UserDTO> users;
	
	public ManagerCreationRequestDTO(){
		
	}
	
	public ManagerCreationRequestDTO(String newManager, List<UserDTO> newUsers){
		this.manager = newManager;
		this.users = newUsers;
	}
	
	
}
