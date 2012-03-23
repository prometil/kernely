package org.kernely.invoice.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO allowing to create/update a line of an invoice
 */
@XmlRootElement
public class InvoiceLineCreationRequestDTO {
	/**
	 * Id of the line, 0 if creation
	 */
	public long id;
	
	/**
	 * Designation of the line
	 */
	public String designation;
	
	/**
	 * Quantity of the line
	 */
	public float quantity;
	
	/**
	 * Unit price of the line
	 */
	public float unitPrice;
	
	/**
	 * Id of the invoice containing this line
	 */
	public long invoiceId;
	
	/**
	 * Default constructor
	 */
	public InvoiceLineCreationRequestDTO(){}
	
	/**
	 * Creates a new DTO from all informations
	 * @param id Id of the line
	 * @param designation Designation of the line
	 * @param quantity Quantity of the line
	 * @param unitPrice Unit price of the line
	 * @param invoiceId Invoice id containing this line
	 */
	public InvoiceLineCreationRequestDTO(long id, String designation, float quantity, float unitPrice, long invoiceId){
		this.id = id;
		this.designation = designation;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.invoiceId = invoiceId;
	}
}
