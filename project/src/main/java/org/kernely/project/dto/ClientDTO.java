package org.kernely.project.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.project.model.Client;

/**
 * The client DTO
 */
@XmlRootElement
public class ClientDTO {
	
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
	 * The activity of the client
	 */
	public int active;
	
	/**
	 * Default constructor
	 */
	public ClientDTO(){
		
	}
	
	/**
	 *  constructor
	 * @param client
	 */
	public ClientDTO(Client client){
		this.active=client.getActive();
		this.address=client.getAddress();
		this.city=client.getCity();
		this.email=client.getEmail();
		this.fax=client.getFax();
		this.id=client.getId();
		this.name=client.getName();
		this.phone=client.getPhone();
		this.zip=client.getZip();
	}
}
