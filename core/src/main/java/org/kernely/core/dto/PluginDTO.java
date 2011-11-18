package org.kernely.core.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PluginDTO {
	public PluginDTO(){
		
	}
	
	public PluginDTO(String name, String path, String img, List<AdminPageDTO> adminPages) {
		this.name = name ;
		this.path = path;
		this.img = img;
		this.adminPages = adminPages;
	}

	public List<AdminPageDTO> adminPages;
	public String name;
	public String path;
	public String img;
	public String email;
	
}
