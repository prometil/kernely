package org.kernely.project.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.User;
import org.kernely.project.model.Project;

/**
 * The project DTO
 */
@XmlRootElement
public class ProjectDTO implements Comparable<ProjectDTO>{

	/**
	 * The id of the project
	 */
	public long id;

	/**
	 * The name of the project
	 */
	public String name;

	/**
	 * The icon of the project
	 */
	public String icon;

	/**
	 * The list of member of the project
	 */
	public List<UserDTO> users;

	/**
	 * The organization that the project belong
	 */
	public OrganizationDTO organization;

	/**
	 * Default constructor
	 */
	public ProjectDTO() {

	}

	/**
	 * Constructor
	 */
	public ProjectDTO(String newName, long id, String newIcon, List<UserDTO> newUsers, OrganizationDTO newOrganization) {
		this.id = id;
		this.name = newName;
		this.users = newUsers;
		this.icon = newIcon;
		this.organization = newOrganization;
	}
	
	/**
	 * Constructor using model
	 */
	public ProjectDTO(Project project){
		this.id = project.getId();
		this.name = project.getName();
		this.users = new ArrayList<UserDTO>();
		for (User u : project.getUsers()){
			this.users.add(new UserDTO(u));
		}
		this.icon = project.getIcon();
		this.organization = new OrganizationDTO(project.getOrganization());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}
		ProjectDTO other = (ProjectDTO) obj;
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Compare name between project DTO.
	 */
	@Override
	public int compareTo(ProjectDTO p) {
		return this.name.compareTo(p.name);
	}

}
