package org.kernely.invoice.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.project.model.Project;

/**
 * Invoice model
 */
@Entity
@Table(name = "kernely_invoice")
public class Invoice extends AbstractModel {
	public static final int INVOICE_PAID = 1;
	public static final int INVOICE_UNPAID = 2;
	public static final int INVOICE_UNDEFINED = 3;
	public static final int INVOICE_PENDING = 0;
	
	private String code;
	
	@Column(name="date_creation")
	private Date dateCreation;
	
	@Column(name="date_publication")
	private Date datePublication;
	
	@Column(name="date_term")
	private Date dateTerm;
	
	private String object;
	private int status;
	
	@OneToMany(mappedBy ="invoice")
	private Set<InvoiceLine> lines;
	
	@ManyToOne
	@JoinColumn(name="project_id")
	private Project project;
	
	@OneToMany(mappedBy="invoice")
	private Set<InvoiceHistory> histories;
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the object
	 */
	public String getObject() {
		return object;
	}
	/**
	 * @param object the object to set
	 */
	public void setObject(String object) {
		this.object = object;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the lines
	 */
	public Set<InvoiceLine> getLines() {
		return lines;
	}
	/**
	 * @param lines the lines to set
	 */
	public void setLines(Set<InvoiceLine> lines) {
		this.lines = lines;
	}
	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}
	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}
	/**
	 * @return the dateCreation
	 */
	public Date getDateCreation() {
		return dateCreation;
	}
	/**
	 * @param dateCreation the dateCreation to set
	 */
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}
	/**
	 * @return the datePublication
	 */
	public Date getDatePublication() {
		return datePublication;
	}
	/**
	 * @param datePublication the datePublication to set
	 */
	public void setDatePublication(Date datePublication) {
		this.datePublication = datePublication;
	}
	/**
	 * @return the dateTerm
	 */
	public Date getDateTerm() {
		return dateTerm;
	}
	/**
	 * @param dateTerm the dateTerm to set
	 */
	public void setDateTerm(Date dateTerm) {
		this.dateTerm = dateTerm;
	}
	/**
	 * @return the histories
	 */
	public Set<InvoiceHistory> getHistories() {
		return histories;
	}
	/**
	 * @param histories the histories to set
	 */
	public void setHistories(Set<InvoiceHistory> histories) {
		this.histories = histories;
	}
	
	
}
