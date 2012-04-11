package org.kernely.invoice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;

/**
 * InvoiceLine model
 *
 */
@Entity
@Table(name = "kernely_invoice_line")
public class InvoiceLine extends AbstractModel {

	private String designation;
	private float quantity;
	
	@Column(name="VAT")
	private float vat;
	
	@Column(name="unit_price")
	private float unitPrice;
	
	@ManyToOne
	@JoinColumn(name="invoice_id")
	private Invoice invoice;

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the quantity
	 */
	public float getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the unitPrice
	 */
	public float getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the invoice
	 */
	public Invoice getInvoice() {
		return invoice;
	}

	/**
	 * @param invoice the invoice to set
	 */
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	/**
	 * @return the vat
	 */
	public float getVat() {
		return vat;
	}

	/**
	 * @param vat the vat to set
	 */
	public void setVat(float vat) {
		this.vat = vat;
	}
	
	
}
