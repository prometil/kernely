package org.kernely.core.dto;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for manager data, contains only his name and the number of users he owns.
 */
@XmlRootElement
public class ManagerDTO {
	public List<UserDTO> users;
	public String name;
	
	public ManagerDTO(){
		
	}
	

	public ManagerDTO(String newName, List<UserDTO> newUsers){
		this.users=newUsers;
		this.name=newName; 
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((users == null) ? 0 : users.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ManagerDTO other = (ManagerDTO) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
