package org.kernely.stream.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StreamCreationRequestDTO {
	public StreamCreationRequestDTO(){
		
	}
	
	public StreamCreationRequestDTO(int id, String name, String category){
		this.id = id;
		this.name = name;
		this.category = category;
	}
	
	public int id;
	public String name;
	public String category;
}
