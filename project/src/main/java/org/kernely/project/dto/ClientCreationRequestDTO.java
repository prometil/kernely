package org.kernely.project.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Creation request DTO of client
 */
@XmlRootElement
public class ClientCreationRequestDTO {
	/**
	 * The id of the client
	 */
	public int id;
	
	/**
	 * The name of the client
	 */
	public String name;
	
	/**
	 * The address of the client
	 */
	public String address;
	
	/**
	 * The email of the client
	 */
	public String email;
	
	/**
	* The zip code of the address
 	*/
	public String zip;
	
	/**
	 * The city of  the  client
	 */
	public String city;
	
	/**
	 * The phone of  the client 
	 */
	public String phone;
	
	/**
	 * the fax of the client 
	 */
	public String fax;
	

	/**
	 * Default constructor
	 */
	public ClientCreationRequestDTO(){
		
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
	public ClientCreationRequestDTO(int id, String name, String address, String email, String zip, String city, String phone, String fax){
		this.address=address;
		this.city=city;
		this.email=email;
		this.fax=fax;
		this.id=id;
		this.name=name;
		this.phone=phone;
		this.zip=zip;
	}

}
