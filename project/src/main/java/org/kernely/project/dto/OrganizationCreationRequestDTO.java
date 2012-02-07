package org.kernely.project.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.core.dto.UserDTO;

/**
 * Creation request DTO of organization
 */
@XmlRootElement
public class OrganizationCreationRequestDTO {
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
	public OrganizationCreationRequestDTO(){
		
	}
	
	/**
	 * constructor
	 * @param id
	 * @param name
	 * @param address
	 * @param email
	 * @param zip
	 * @param city
	 * @param phone
	 * @param fax
	 */
	public OrganizationCreationRequestDTO(int id, String name, String address, String zip, String city, String phone, String fax, List<UserDTO> list){
		this.address=address;
		this.city=city;
		this.fax=fax;
		this.id=id;
		this.name=name;
		this.phone=phone;
		this.zip=zip;
		this.users=list;
	}

}
