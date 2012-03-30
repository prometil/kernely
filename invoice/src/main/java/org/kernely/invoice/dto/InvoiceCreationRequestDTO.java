package org.kernely.invoice.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO allowing to create/update an invoice
 */
@XmlRootElement
public class InvoiceCreationRequestDTO {

	/**
	 * Id of this invoice
	 */
	public long id;
	
	/**
	 * Code of the invoice
	 */
	public String code;
	
	/**
	 * Object of this invoice
	 */
	public String object;
	
	/**
	 * Comment associated to this invoice
	 */
	public String comment;
	
	/**
	 * Project id concerned by this invoice
	 */
	public long projectId;
	
	/**
	 * String representation of the publication of this invoice
	 */
	public String datePublication ="";
	
	/**
	 * String representation of the ter date of this invoice
	 */
	public String dateTerm ="";
	
	/**
	 * Default constructor
	 */
	public InvoiceCreationRequestDTO(){}
	
	/**
	 * Creates a new DTO from all given informations
	 * @param id Id of the invoice, 0 if creation
	 * @param object Object of this invoice
	 * @param datePublication Publication date of this invoice
	 * @param dateTerm Term date of this invoice
	 * @param projectId Id of the Project concerned by this invoice
	 */
	public InvoiceCreationRequestDTO(long id, String code, String object, String datePublication, String dateTerm, long projectId, String comment){
		this.id = id;
		this.code = code;
		this.object = object;
		this.datePublication = datePublication;
		this.dateTerm = dateTerm;
		this.projectId = projectId;
		this.comment = comment;
	}
}
