package org.kernely.invoice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.Query;

import org.apache.commons.configuration.AbstractConfiguration;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kernely.core.model.Role;
import org.kernely.core.service.UserService;
import org.kernely.invoice.dto.InvoiceCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceDTO;
import org.kernely.invoice.dto.InvoiceLineCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceLineDTO;
import org.kernely.invoice.dto.VatDTO;
import org.kernely.invoice.model.Invoice;
import org.kernely.invoice.model.InvoiceLine;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.model.Organization;
import org.kernely.project.model.Project;
import org.kernely.project.service.ProjectService;
import org.kernely.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The Invoice service
 */
@Singleton
public class InvoiceService extends AbstractService{
	
	private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);
	
	@Inject
	private UserService userService;
	
	@Inject
	private ProjectService projectService;
	
	@Inject
	private AbstractConfiguration configuration;

	
	/**
	 * Creates or updates an invoice from a request containing all needed informations
	 * Creation date is set to the current date and the current hour.
	 * Create or update mode is defined in function of the value of the request id
	 * @param request The DTO containing all  informations about the new invoiceStop.
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
		
		
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		Date publication = DateTime.parse(request.datePublication, fmt).toDateMidnight().toDate();
		Date term = DateTime.parse(request.dateTerm, fmt).toDateMidnight().toDate();
		invoice.setDatePublication(publication);
		invoice.setDateTerm(term);
		invoice.setDateCreation(DateTime.now().toDate());
		invoice.setAmount(request.amount);
		
		if(request.id == 0){
			invoice.setStatus(Invoice.INVOICE_UNDEFINED);
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
		invoiceLine.setVat(request.vat);
		
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
			if(userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
				
				request = em.get().createQuery("SELECT i FROM Invoice i");
				
			}
			else{
				// Here, the user must have the project manager role, so we have to retrieve his project and organizations.
				List<ProjectDTO> projects = projectService.getProjectsForProjectManagerLinkedToOrganization(0);
				if(projects != null && !projects.isEmpty()){
					request = em.get().createQuery("SELECT i FROM Invoice i WHERE project in :project");
					List<Project> projectsModel = new ArrayList<Project>();
					for(ProjectDTO p : projects){
						projectsModel.add(em.get().find(Project.class, p.id));
					}
					request.setParameter("project", projectsModel);
				}
				else{
					throw new IllegalArgumentException("This user is not link to any project as Project Manager !");
				}
			}
		}
		else{
			if(projectId == 0){
				if(userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
					request = em.get().createQuery("SELECT i FROM Invoice i WHERE project in :project");
					request.setParameter("project", em.get().find(Organization.class, organizationId).getProjects());
				}
				else{
					List<ProjectDTO> projects = projectService.getProjectsForProjectManagerLinkedToOrganization(organizationId);
					if(projects != null && !projects.isEmpty()){
						request = em.get().createQuery("SELECT i FROM Invoice i WHERE project in :project");
						List<Project> projectsModel = new ArrayList<Project>();
						for(ProjectDTO p : projects){
							projectsModel.add(em.get().find(Project.class, p.id));
						}
						request.setParameter("project", projectsModel);
					}
					else{
						throw new IllegalArgumentException("The organization with id "+ organizationId +" doesn't have any project !");
					}
				}
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
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		if(invoice != null){
			InvoiceDTO invoiceDTO = new InvoiceDTO(invoice);
			invoiceDTO.lines = this.getLinesForInvoice(invoiceId);
			float amountDf = this.getInvoiceTotalAmountDutyFree(invoiceId);
			invoiceDTO.amountDf = amountDf;
			List<VatDTO> vats = this.getAmountByVAT(invoiceId);
			invoiceDTO.vats = vats;
			float amountTotal = amountDf;
			for(VatDTO v : vats){
				amountTotal += v.amount;
			}
			invoiceDTO.amount = amountTotal;
			return invoiceDTO;
		}
		return null;
	}
	
	/**
	 * Retrieves the total amount of an invoice
	 * @param invoiceId The id of the invoice concerned
	 * @return A float representing the total amount of this invoice
	 */
	@Transactional
	public float getInvoiceTotalAmountDutyFree(long invoiceId){
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
		if(invoice != null){
			em.get().remove(invoice);
		}
		else{
			throw new IllegalArgumentException("The invoice with the ID " + invoiceId + " doesn't exist ! ");
		}
	}
	
	/**
	 * Sets an invoice in the paid status
	 * @param invoiceId The id of the concerned invoice
	 * @return The DTO updated with the invoice
	 */
	@Transactional
	public InvoiceDTO setInvoiceAsPaid(long invoiceId){
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		if(invoice != null){
			if(invoice.getStatus() != Invoice.INVOICE_PAID &&  invoice.getStatus() != Invoice.INVOICE_UNDEFINED){
				invoice.setStatus(Invoice.INVOICE_PAID);
				em.get().merge(invoice);
			}
			return new InvoiceDTO(invoice);
		}
		else{
			throw new IllegalArgumentException("The invoice with the ID " + invoiceId + " doesn't exist ! ");
		}
	}
	
	/**
	 * Sets an invoice in the unpaid status
	 * @param invoiceId The id of the concerned invoice
	 * @return The DTO updated with the invoice
	 */
	@Transactional
	public InvoiceDTO setInvoiceAsUnpaid(long invoiceId){
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		if(invoice != null){
			if(invoice.getStatus() != Invoice.INVOICE_UNPAID &&  invoice.getStatus() != Invoice.INVOICE_UNDEFINED){
				invoice.setStatus(Invoice.INVOICE_UNPAID);
				em.get().merge(invoice);
			}
			return new InvoiceDTO(invoice);
		}
		else{
			throw new IllegalArgumentException("The invoice with the ID " + invoiceId + " doesn't exist ! ");
		}
	}

	/**
	 * Sets an invoice in the published status
	 * @param invoiceId The id of the concerned invoice
	 * @return The DTO updated with the invoice
	 */
	@Transactional
	public InvoiceDTO setInvoiceAsPublished(long invoiceId){
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		if(invoice != null){
			if(invoice.getStatus() != Invoice.INVOICE_PAID &&  invoice.getStatus() != Invoice.INVOICE_UNPAID && invoice.getStatus() != Invoice.INVOICE_PENDING){
				invoice.setStatus(Invoice.INVOICE_PENDING);
				em.get().merge(invoice);
			}
			return new InvoiceDTO(invoice);
		}
		else{
			throw new IllegalArgumentException("The invoice with the ID " + invoiceId + " doesn't exist ! ");
		}
	}
	
	/**
	 * Constructs a list of DTO representing lines of an invoices
	 * @param invoiceId The id of the invoice
	 * @return A list of DTO representing the lines of this invoice
	 */
	@Transactional
	public List<InvoiceLineDTO> getLinesForInvoice(long invoiceId){
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		List<InvoiceLineDTO> lines = new ArrayList<InvoiceLineDTO>();
		if(invoice != null){
			if(invoice.getLines() != null){
				for(InvoiceLine line : invoice.getLines()){
					lines.add(new InvoiceLineDTO(line));
				}
			}
		}
		return lines;
	}
	
	/**
	 * Retrieves all the registered VAT values in the configuration file
	 * @return A list of VatDTOs representing all the value configured
	 */
	public List<VatDTO> getVAT(){
		List<VatDTO> vats = new ArrayList<VatDTO>();
		configuration.setListDelimiter(',');
		for(String s : configuration.getStringArray("vat")){
			vats.add(new VatDTO(Float.parseFloat(s)));
		}
		return vats;
	}
	
	/**
	 * Process the amount of the lines for each value added taxes for an invoice
	 * @param invoiceId The id of the concerned invoice
	 * @return A list of VatDTO representing the amount by VAT 
	 */
	@Transactional
	public List<VatDTO> getAmountByVAT(long invoiceId){
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		Map<Float, Float> vatAmounts = new HashMap<Float, Float>();
		List<VatDTO> vats = new ArrayList<VatDTO>();
		for(InvoiceLine l : invoice.getLines()){
			float newAmount;
			if(vatAmounts.containsKey(l.getVat())){
				newAmount = vatAmounts.get(l.getVat()) + (l.getQuantity() * l.getUnitPrice())*(l.getVat()/100);
			}
			else{
				newAmount = (l.getQuantity() * l.getUnitPrice())*(l.getVat()/100);
			}
			
			vatAmounts.put(l.getVat(), newAmount);
		}
		
		for(Entry<Float, Float> e : vatAmounts.entrySet()){
			vats.add(new VatDTO(e.getKey(), e.getValue()));
		}
		return vats;
	}
}
