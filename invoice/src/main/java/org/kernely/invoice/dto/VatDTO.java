package org.kernely.invoice.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO representing a Vat value
 */
@XmlRootElement
public class VatDTO {
	/**
	 * The value of this VAT
	 */
	public float value;
	
	/**
	 * The amount of lines for this VAT
	 */
	public float amount;
	
	/**
	 * Default constructor
	 */
	public VatDTO(){}
	
	/**
	 * Constructor
	 * @param value The value of this VAT
	 */
	public VatDTO(float value){
		this.value = value;
	}
	
	/**
	 * Constructor
	 * @param value Value of this VAT
	 * @param amount Amount of lines for this VAT
	 */
	public VatDTO(float value, float amount){
		this.value = value;
		this.amount = amount;
	}
}
