package org.kernely.project.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.User;
import org.kernely.project.model.Organization;

/**
 * The organization DTO
 */
@XmlRootElement
public class OrganizationDTO {
	
	/**
	 * The id of the organization
	 */
	public int id;
	
	/**
	 * The name of the organization
	 */
	public String name;
	
	/**
	 * The address of the organization
	 */
	public String address;
	
	/**
	* The zip code of the address
 	*/
	public String zip;
	
	/**
	 * The city of  the  organization
	 */
	public String city;
	
	/**
	 * The phone of  the organization 
	 */
	public String phone;
	
	/**
	 * the fax of the organization 
	 */
	public String fax;
	
	/**
	 * The list of member of the project
	 */
	public List<UserDTO> users;
	
	/**
	 * Default constructor
	 */
	public OrganizationDTO(){
		
	}
	
	/**
	 *  constructor
	 * @param organization
	 */
	public OrganizationDTO(Organization organization){
		this.address=organization.getAddress();
		this.city=organization.getCity();
		this.fax=organization.getFax();
		this.id=organization.getId();
		this.name=organization.getName();
		this.phone=organization.getPhone();
		this.zip=organization.getZip();
		this.users=new ArrayList<UserDTO>();
		for (User user : organization.getUsers()){
			this.users.add(new UserDTO(user));
		}
	}
}
