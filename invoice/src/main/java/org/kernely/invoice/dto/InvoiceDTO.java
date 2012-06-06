package org.kernely.invoice.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.kernely.invoice.model.Invoice;

/**
 * DTO representing an invoice
 */
@XmlRootElement
public class InvoiceDTO {
	
	private String dateFormat = "MM/dd/yyyy";

	/**
	 * Id of the invoice
	 */
	public long id;
	
	/**
	 * Code number of this invoice
	 */
	public String code;
	
	/**
	 * Creation of this invoice
	 */
	public Date dateCreation;
	
	/**
	 * Publication of this invoice
	 */
	public Date datePublication;
	
	/**
	 * Term of this invoice
	 */
	public Date dateTerm;
	
	/**
	 * String representation of the creation of this invoice
	 */
	public String dateCreationString;
	
	/**
	 * String representation of the publication of this invoice
	 */
	public String datePublicationString;
	
	/**
	 * String representation of the term of this invoice
	 */
	public String dateTermString;
	
	/**
	 * Object of this invoice
	 */
	public String object;
	
	/**
	 * Comment associated to this invoice
	 */
	public String comment;
	
	/**
	 * Status of this invoice
	 */
	public int status;
	
	/**
	 * Delay of this invoice
	 */
	public int delay;
	
	/**
	 * Organization name which will recieve this invoice
	 */
	public String organizationName;
	
	/**
	 * Organization zip
	 */
	public String organizationZip;
	
	/**
	 * Organization address
	 */
	public String organizationAddress;
	
	/**
	 * Organization city
	 */
	public String organizationCity;
	
	
	/**
	 * Project relative to this invoice
	 */
	public String projectName;
	
	/**
	 * Amount of this invoice
	 */
	public float amountDf;
	
	/**
	 * Amount of this invoice
	 */
	public float amount;
	
	/**
	 * Lines of this invoices
	 */
	public List<InvoiceLineDTO> lines;
	
	/**
	 * Amount for each different vat for this invoice
	 */
	public List<VatDTO> vats;
	
	/**
	 * Default constructor
	 */
	public InvoiceDTO(){}
	
	/**
	 * Creates this DTO from an Invoice model
	 * @param invoice The model of invoice to create
	 */
	public InvoiceDTO(Invoice invoice, String dateFormat){
		this.dateFormat = dateFormat;
		this.id = invoice.getId();
		this.code = invoice.getCode();
		this.dateCreation = invoice.getDateCreation();
		this.datePublication = invoice.getDatePublication();
		this.dateTerm = invoice.getDateTerm();
		this.object = invoice.getObject();
		this.status = invoice.getStatus();
		this.delay = Days.daysBetween(new DateTime(this.datePublication), new DateTime(this.dateTerm)).getDays();
		this.organizationName = invoice.getOrganizationName();
		this.organizationAddress = invoice.getOrganizationCity();
		this.organizationZip = invoice.getOrganizationZip();
		this.organizationCity = invoice.getOrganizationCity();
		this.projectName = invoice.getProject().getName();
		this.comment = invoice.getComment(); 
		this.amount = invoice.getAmount();
		this.getDateStringified();		
	}
	
	public void setDateFormat(String dateFormat){
		this.dateFormat = dateFormat;
		this.getDateStringified();
	}
	
	private void getDateStringified(){
		this.dateCreationString = new DateTime(this.dateCreation).toString(this.dateFormat);
		this.datePublicationString = new DateTime(this.datePublication).toString(dateFormat);
		this.dateTermString = new DateTime(this.dateTerm).toString(dateFormat);
	}
	
}
