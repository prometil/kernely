package org.kernely.invoice.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	
	private String currency;
	
	@Column(name="date_creation")
	private Date dateCreation;
	
	@Column(name="date_publication")
	private Date datePublication;
	
	@Column(name="date_term")
	private Date dateTerm;
	
	private String object;
	private int status;
	private String comment;
	
	private float amount;
	
	@Column(name="organization_name")
	private String organizationName;
	
	@Column(name="organization_address")
	private String organizationAddress;
	
	@Column(name="organization_zip")
	private String organizationZip;
	
	@Column(name="organization_city")
	private String organizationCity;
	
	
	@OneToMany(mappedBy ="invoice",fetch = FetchType.LAZY)
	private Set<InvoiceLine> lines;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="project_id")
	private Project project;
	
	/**
	 * Default constructor
	 */
	public Invoice(){
		this.lines = new HashSet<InvoiceLine>();
	}
	
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
	 * @return the organizationName
	 */
	public String getOrganizationName() {
		return organizationName;
	}
	/**
	 * @param organizationName the organizationName to set
	 */
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	/**
	 * @return the organizationAddress
	 */
	public String getOrganizationAddress() {
		return organizationAddress;
	}
	/**
	 * @param organizationAddress the organizationAddress to set
	 */
	public void setOrganizationAddress(String organizationAddress) {
		this.organizationAddress = organizationAddress;
	}
	/**
	 * @return the organizationZip
	 */
	public String getOrganizationZip() {
		return organizationZip;
	}
	/**
	 * @param organizationZip the organizationZip to set
	 */
	public void setOrganizationZip(String organizationZip) {
		this.organizationZip = organizationZip;
	}
	/**
	 * @return the organizationCity
	 */
	public String getOrganizationCity() {
		return organizationCity;
	}
	/**
	 * @param organizationCity the organizationCity to set
	 */
	public void setOrganizationCity(String organizationCity) {
		this.organizationCity = organizationCity;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((dateCreation == null) ? 0 : dateCreation.hashCode());
		result = prime * result + ((datePublication == null) ? 0 : datePublication.hashCode());
		result = prime * result + ((dateTerm == null) ? 0 : dateTerm.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((organizationAddress == null) ? 0 : organizationAddress.hashCode());
		result = prime * result + ((organizationCity == null) ? 0 : organizationCity.hashCode());
		result = prime * result + ((organizationName == null) ? 0 : organizationName.hashCode());
		result = prime * result + ((organizationZip == null) ? 0 : organizationZip.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Invoice other = (Invoice) obj;
		if (code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!code.equals(other.code)) {
			return false;
		}
		if (dateCreation == null) {
			if (other.dateCreation != null) {
				return false;
			}
		} else if (!dateCreation.equals(other.dateCreation)) {
			return false;
		}
		if (datePublication == null) {
			if (other.datePublication != null) {
				return false;
			}
		} else if (!datePublication.equals(other.datePublication)) {
			return false;
		}
		if (dateTerm == null) {
			if (other.dateTerm != null) {
				return false;
			}
		} else if (!dateTerm.equals(other.dateTerm)) {
			return false;
		}
		if (object == null) {
			if (other.object != null) {
				return false;
			}
		} else if (!object.equals(other.object)) {
			return false;
		}
		if (organizationAddress == null) {
			if (other.organizationAddress != null) {
				return false;
			}
		} else if (!organizationAddress.equals(other.organizationAddress)) {
			return false;
		}
		if (organizationCity == null) {
			if (other.organizationCity != null) {
				return false;
			}
		} else if (!organizationCity.equals(other.organizationCity)) {
			return false;
		}
		if (organizationName == null) {
			if (other.organizationName != null) {
				return false;
			}
		} else if (!organizationName.equals(other.organizationName)) {
			return false;
		}
		if (organizationZip == null) {
			if (other.organizationZip != null) {
				return false;
			}
		} else if (!organizationZip.equals(other.organizationZip)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the amount
	 */
	public float getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}
}
