package org.kernely.invoice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kernely.core.service.AbstractService;
import org.kernely.invoice.dto.InvoiceCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceDTO;
import org.kernely.invoice.dto.InvoiceLineCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceLineDTO;
import org.kernely.invoice.model.Invoice;
import org.kernely.invoice.model.InvoiceLine;
import org.kernely.project.model.Organization;
import org.kernely.project.model.Project;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The Invoice service
 */
@Singleton
public class InvoiceService extends AbstractService{
	
	/**
	 * Creates or updates an invoice from a request containing all needed informations
	 * Creation date is set to the current date and the current hour.
	 * Create or update mode is defined in function of the value of the request id
	 * @param request The DTO containing all  informations about the new invoice.
	 * @return A DTO representing the new invoice created
	 */
	@Transactional
	public InvoiceDTO createOrUpdateInvoice(InvoiceCreationRequestDTO request){
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}
		if (request.datePublication.equals("")) {
			throw new IllegalArgumentException("Publication date must be defined ");
		}
		if (request.dateTerm.equals("")) {
			throw new IllegalArgumentException("Term date must be defined ");
		}
		if (request.id == 0 && request.projectId == 0) {
			throw new IllegalArgumentException("This invoice has to be associated to a project");
		}
		
		Invoice invoice;
		
		if(request.id == 0){
			invoice = new Invoice();
		}
		else{
			invoice = em.get().find(Invoice.class, request.id);
		}
		
		invoice.setCode(request.code);
		invoice.setObject(request.object);
		invoice.setComment(request.comment);
		
		invoice.setStatus(Invoice.INVOICE_UNDEFINED);
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		Date publication = DateTime.parse(request.datePublication, fmt).toDateMidnight().toDate();
		Date term = DateTime.parse(request.dateTerm, fmt).toDateMidnight().toDate();
		invoice.setDatePublication(publication);
		invoice.setDateTerm(term);
		invoice.setDateCreation(DateTime.now().toDate());
		
		if(request.id == 0){
			Project project = em.get().find(Project.class, request.projectId);
			invoice.setProject(project);
			invoice.setOrganizationAddress(project.getOrganization().getAddress());
			invoice.setOrganizationName(project.getOrganization().getName());
			invoice.setOrganizationCity(project.getOrganization().getCity());
			invoice.setOrganizationZip(project.getOrganization().getZip());
			
			em.get().persist(invoice);
		}
		else{
			em.get().merge(invoice);
		}
		
		return new InvoiceDTO(invoice);
	}
	
	/**
	 * Creates or updates an invoice line from a request containing all needed informations
	 * Create or update mode is defined in function of the value of the request id
	 * @param request The DTO containing all needed informations about the nesw line
	 * @return A DTO representing the new line created
	 */
	@Transactional
	public InvoiceLineDTO createOrUpdateInvoiceLine(InvoiceLineCreationRequestDTO request){
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if (request.designation == null) {
			throw new IllegalArgumentException("An invoice line can't have a null designation.");
		}
		if ("".equals(request.designation.trim())) {
			throw new IllegalArgumentException("An invoice line can't have an empty designation.");
		}
		
		InvoiceLine invoiceLine;
		
		if(request.id == 0){
			invoiceLine = new InvoiceLine();
		}
		else{
			invoiceLine = em.get().find(InvoiceLine.class, request.id);
		}
		
		invoiceLine.setDesignation(request.designation);
		Invoice invoice = em.get().find(Invoice.class, request.invoiceId);
		invoiceLine.setInvoice(invoice);
		invoiceLine.setQuantity(request.quantity);
		invoiceLine.setUnitPrice(request.unitPrice);
		
		if(request.id == 0){
			em.get().persist(invoiceLine);
		}
		else{
			em.get().merge(invoiceLine);
		}
		
		Set<InvoiceLine> lines = invoice.getLines();
		lines.add(invoiceLine);
		invoice.setLines(lines);
		em.get().merge(invoice);
		
		return new InvoiceLineDTO(invoiceLine);
	}
	
	/**
	 * Retrieves all existing invoices
	 * @return A list containing all existing invoices
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<InvoiceDTO> getAllInvoices(){
		Query request = em.get().createQuery("SELECT i FROM Invoice i");
		List<Invoice> invoices = (List<Invoice>)request.getResultList();
		List<InvoiceDTO> invoicesDTO = new ArrayList<InvoiceDTO>();
		InvoiceDTO invoiceDTO;
		for(Invoice i : invoices){
			invoiceDTO = new InvoiceDTO(i);
			invoiceDTO.amount = this.getInvoiceTotalAmount(i.getId());
			invoicesDTO.add(invoiceDTO);
		}
		return invoicesDTO;
	}
	
	/**
	 * Retrieves invoices in function of the given organization and project.
	 * If no organization is specified (two params set to 0), retrieves all the invoices
	 * If only organization is specified, retrieves all invoices of this organization
	 * If all parameters are set, retrieves the invoices for the given organization and project.
	 * @param organizationId Organization's Id, 0 if no needed
	 * @param projectId Project's id, 0 if no needed
	 * @return A list of DTO representing all invoices retrieved.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<InvoiceDTO> getInvoicesPerOrganizationAndProject(long organizationId, long projectId){
		Query request;
		if(organizationId == 0){
			request = em.get().createQuery("SELECT i FROM Invoice i");
		}
		else{
			if(projectId == 0){
				request = em.get().createQuery("SELECT i FROM Invoice i WHERE project in :project");
				request.setParameter("project", em.get().find(Organization.class, organizationId).getProjects());
			}
			else{
				// Verify that the given project is really one of the given organization
				Organization organization = em.get().find(Organization.class, organizationId);
				Project project = em.get().find(Project.class, projectId);
				
				if(!organization.getProjects().contains(project)){
					throw new IllegalArgumentException("The given project ("+ projectId +") doesn't match to the given organization (" + organizationId + ")");
				}
				
				request = em.get().createQuery("SELECT i FROM Invoice i WHERE project = :project");
				request.setParameter("project", em.get().find(Project.class, projectId));
			}
		}
		List<Invoice> invoices = (List<Invoice>)request.getResultList();
		List<InvoiceDTO> invoicesDTO = new ArrayList<InvoiceDTO>();
		InvoiceDTO invoiceDTO;
		for(Invoice i : invoices){
			invoiceDTO = new InvoiceDTO(i);
			invoiceDTO.amount = this.getInvoiceTotalAmount(i.getId());
			invoicesDTO.add(invoiceDTO);
		}
		return invoicesDTO;
	}
	
	/**
	 * Retrieves an invoices in function of its id
	 * @param invoiceId The id of the invoice to retrieve
	 * @return A DTO representing the invoice retrieved
	 */
	@Transactional
	public InvoiceDTO getInvoiceById(long invoiceId){
		InvoiceDTO invoiceDTO = new InvoiceDTO(em.get().find(Invoice.class, invoiceId));
		invoiceDTO.lines = this.getLinesForInvoice(invoiceId);
		invoiceDTO.amount = this.getInvoiceTotalAmount(invoiceId);
		return invoiceDTO;
	}
	
	/**
	 * Retrieves the total amount of an invoice
	 * @param invoiceId The id of the invoice concerned
	 * @return A float representing the total amount of this invoice
	 */
	@Transactional
	public float getInvoiceTotalAmount(long invoiceId){
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		float amount = 0.0F;
		for(InvoiceLine line : invoice.getLines()){
			amount += (line.getQuantity() * line.getUnitPrice());
		}
		return amount;
	}
	
	/**
	 * Deletes a line of an invoice
	 * @param lineId The id of the line to delete
	 */
	@Transactional
	public void deleteLine(long lineId){
		InvoiceLine invoiceLine = em.get().find(InvoiceLine.class, lineId);
		em.get().remove(invoiceLine);
	}
	
	/**
	 * Deletes an invoice
	 * @param invoiceId The id of the invoice to delete
	 */
	@Transactional
	public void deleteInvoice(long invoiceId){
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		em.get().remove(invoice);
	}
	
	/**
	 * Constructs a list of DTO reprensenting lines of an invoices
	 * @param invoiceId The id of the invoice
	 * @return A list of DTO representing the lines of this invoice
	 */
	@Transactional
	public List<InvoiceLineDTO> getLinesForInvoice(long invoiceId){
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		List<InvoiceLineDTO> lines = new ArrayList<InvoiceLineDTO>();
		for(InvoiceLine line : invoice.getLines()){
			lines.add(new InvoiceLineDTO(line));
		}
		return lines;
	}
	
	/**
	 * Delete all the existing lines linked to the given invoice
	 * @param invoiceId The id of the given invoice
	 */
	@Transactional
	public void deleteAllInvoiceLines(long invoiceId){
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		Set<InvoiceLine> oldLines = invoice.getLines();
		//Remove all the old lines
		for(InvoiceLine line : oldLines){
			em.get().remove(line);
		}
		invoice.setLines(new HashSet<InvoiceLine>());
		em.get().merge(invoice);
	}
}
