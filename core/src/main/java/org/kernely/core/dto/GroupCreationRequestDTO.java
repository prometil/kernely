package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GroupCreationRequestDTO {
	public GroupCreationRequestDTO(){
		
	}
	
	public GroupCreationRequestDTO(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public int id;
	public String name;
}
