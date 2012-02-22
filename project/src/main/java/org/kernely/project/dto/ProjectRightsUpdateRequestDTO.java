package org.kernely.project.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * The project rigth update dto
 * @author b.grandperret
 *
 */
@XmlRootElement
public class ProjectRightsUpdateRequestDTO {
	/**
	 * Default Constructor
	 */
	public ProjectRightsUpdateRequestDTO() {

	}

	/**
	 * Creates a StreStreamRightsUpdateRequestDTO
	 * 
	 * @param projectid
	 *            Id of the concerned project
	 * @param rights
	 *            rights associated to the project
	 */
	public ProjectRightsUpdateRequestDTO(int projectid, List<RightOnProjectDTO> rights) {
		this.projectid = projectid;
		this.rights = rights;
	}

	/**
	 * The id of the project
	 */
	public int projectid;
	
	/**
	 * The list of right 
	 */
	public List<RightOnProjectDTO> rights;
	
}
