package org.kernely.invoice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.invoice.model.InvoiceLine;

/**
 * DTO representing an invoice line
 */
@XmlRootElement
public class InvoiceLineDTO {

	/**
	 * Id of the line
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
	 * Unit Price of the line
	 */
	public float unitPrice;
	
	/**
	 * Id of the invoice containing this line
	 */
	public long invoiceId;
	
	/**
	 * Amount of this line
	 */
	public float amount;
	
	/**
	 * Vat of this line
	 */
	public float vat;
	
	/**
	 * Default constructor
	 */
	public InvoiceLineDTO(){}
	
	/**
	 * Creates a InvoiceLineDTO from a InvoiceLine model
	 * @param line The model of the line
	 */
	public InvoiceLineDTO(InvoiceLine line){
		this.id = line.getId();
		this.designation = line.getDesignation();
		this.quantity = line.getQuantity();
		this.unitPrice = line.getUnitPrice();
		this.invoiceId = line.getInvoice().getId();
		this.amount = this.quantity * this.unitPrice;
		this.vat = line.getVat();
	}
}
