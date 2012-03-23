package org.kernely.invoice.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.invoice.dto.InvoiceCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceDTO;
import org.kernely.invoice.dto.InvoiceLineCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceLineDTO;
import org.kernely.invoice.service.InvoiceService;

import com.google.inject.Inject;

/**
 * The Invoice controller
 */
@Path("/invoice")
public class InvoiceController extends AbstractController{

	@Inject
	private InvoiceService invoiceService;
	
	@Inject
	private TemplateRenderer templateRenderer; 
	
	/**
	 * Retrieves the main page of the invoice
	 * @return The html page
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getInvoicePanel(){
		return templateRenderer.create("/templates/gsp/invoice_overview.gsp").addCss("/css/invoice.css").render();
	}
	
	/**
	 * Retrieves all the existing invoice
	 * @return A JSON String representing the result data
	 */
	@GET
	@Path("/all")
	@Produces({MediaType.APPLICATION_JSON})
	public List<InvoiceDTO> getAllInvoices(){
		return invoiceService.getAllInvoices();
	}
	
	/**
	 * Gets the invoices in function of an organization and a project
	 * @param organizationId The organization id
	 * @param projectId The project id
	 * @return A JSON String representing the result data
	 */
	@GET
	@Path("/specific")
	@Produces({MediaType.APPLICATION_JSON})
	public List<InvoiceDTO> getInvoicesPerOrganizationAndProject(@QueryParam("organizationId") long organizationId, @QueryParam("projectId") long projectId){
		return invoiceService.getInvoicesPerOrganizationAndProject(organizationId, projectId);
	}
	
	/**
	 * Creates an invoice
	 * @param request The request containing all needed informations
	 * @return A JSON String representing the new invoice created
	 */
	@POST
	@Path("/create")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public InvoiceDTO createInvoice(InvoiceCreationRequestDTO request){
		return invoiceService.createOrUpdateInvoice(request);
	}
	
	/**
	 * Creates a new invoice line
	 * @param request The request containing all needed informations
	 * @return A JSON String representing the new invoice line created
	 */
	@POST
	@Path("/line/create")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public InvoiceLineDTO createInvoiceLine(InvoiceLineCreationRequestDTO request){
		return invoiceService.createOrUpdateInvoiceLine(request);
	}
	
	/**
	 * Delete an invoice
	 * @param invoiceId The id of the invoice to delete
	 * @return The result of the operation
	 */
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/delete")
	@Produces({MediaType.APPLICATION_JSON})
	public String deleteInvoice(@QueryParam("invoiceId") long invoiceId){
		invoiceService.deleteInvoice(invoiceId);
		return "{\"result\":\"Ok\"}";
	}
	
	/**
	 * Loads the html page displaying the needed invoice
	 * @param invoiceId The id of the invoice to display
	 * @return An html page
	 */
	@GET
	@Path("/view/{invoiceId}")
	@Produces( { MediaType.TEXT_HTML })
	public Response visualizeInvoice(@PathParam("invoiceId") long invoiceId) {
		InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId);
		return ok(templateRenderer.create("/templates/gsp/invoice.gsp").with("invoice", invoiceDTO).addCss("/css/profile.css"));
	}
	
	
}
