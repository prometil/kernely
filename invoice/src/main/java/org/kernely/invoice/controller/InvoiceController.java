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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.invoice.dto.InvoiceCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceDTO;
import org.kernely.invoice.dto.InvoiceLineCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceLineDTO;
import org.kernely.invoice.service.InvoiceService;
import org.kernely.project.dto.OrganizationDTO;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.service.OrganizationService;
import org.kernely.project.service.ProjectService;

import com.google.inject.Inject;

/**
 * The Invoice controller
 */
@Path("/invoice")
public class InvoiceController extends AbstractController{

	@Inject
	private InvoiceService invoiceService;
	
	@Inject
	private OrganizationService organizationService;
	
	@Inject
	private ProjectService projectService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private TemplateRenderer templateRenderer; 
	
	/**
	 * Retrieves the main page of the invoice
	 * @return The html page
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getInvoicePanel(){
		if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER) || userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			return templateRenderer.create("/templates/gsp/invoice_overview.gsp").addCss("/css/invoice.css").render();
		}
		return null;
	}
	
	/**
	 * Retrieves all the organizations in function of the role of the current user.
	 * If the user is Project Manager, retrieves all the organization of his projects
	 * Else, if he's Book Keeper, retrieve all the organization
	 * @return A list of all the linked organizations
	 */
	@GET
	@Path("/organizations")
	@Produces({MediaType.APPLICATION_JSON})
	public List<OrganizationDTO> getOrganizationForRole(){
		if(userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			return organizationService.getAllOrganizations();
		}
		else{
			if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER)){ 
				return organizationService.getOrganizationForProjectManager();
			}			
		}
		return null;
	}
	
	/**
	 * Retrieves all the projects in function of the role of the current user.
	 * If the user is Project Manager, retrieves all his projects
	 * Else, if he's Book Keeper, retrieve all the projects
	 * @param organizationId The id of the organization
	 * @return A list of all the linked projects
	 */
	@GET
	@Path("/projects")
	@Produces({MediaType.APPLICATION_JSON})
	public List<ProjectDTO> getProjectForRole(@QueryParam("organizationId") long organizationId){
		
		if(userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			return projectService.getProjectsLinkedToOrganization(organizationId);
		}
		else{
			if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER)){
				return projectService.getProjectsForProjectManagerLinkedToOrganization(organizationId);
			}
		}
		return null;
	}
	
	/**
	 * Retrieves all the existing invoice
	 * @return A JSON String representing the result data
	 */
	@GET
	@Path("/all")
	@Produces({MediaType.APPLICATION_JSON})
	public List<InvoiceDTO> getAllInvoices(){
		if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER) || userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			return invoiceService.getAllInvoices();
		}
		return null;
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
		if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER) || userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			return invoiceService.getInvoicesPerOrganizationAndProject(organizationId, projectId);
		}
		return null;
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
	public String createInvoice(InvoiceCreationRequestDTO request){
		if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER) || userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			try{
				invoiceService.createOrUpdateInvoice(request);
				return "{\"result\":\"Ok\"}";
			}
			catch(IllegalArgumentException iae){
				return "{\"result\":\""+iae.getMessage()+"\"}";
			}
		}
		return null;
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
		if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER) || userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			return invoiceService.createOrUpdateInvoiceLine(request);
		}
		return null;
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
		if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER) || userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			invoiceService.deleteInvoice(invoiceId);
			return "{\"result\":\"Ok\"}";
		}
		return null;
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
		if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER) || userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId);
			return ok(templateRenderer.create("/templates/gsp/invoice.gsp").with("invoice", invoiceDTO).addCss("/css/invoice.css"));
		}
		return null;
	}
	
	/**
	 * Loads the html page displaying the needed invoice in edition mode
	 * @param invoiceId The id of the invoice to display
	 * @return An html page
	 */
	@GET
	@Path("/edit/{invoiceId}")
	@Produces( { MediaType.TEXT_HTML })
	public Response editInvoice(@PathParam("invoiceId") long invoiceId) {
		if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER) || userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId);
			return ok(templateRenderer.create("/templates/gsp/invoice_editable.gsp").with("invoice", invoiceDTO).addCss("/css/invoice.css"));
		}
		return null;
	}
	
	/**
	 * Gets all lines relative to an invoice
	 * @param invoiceId Id of the needed invoice
	 * @return A list of DTO containing the lines of the given invoice
	 */
	@GET
	@Path("/lines")
	@Produces({MediaType.APPLICATION_JSON})
	public List<InvoiceLineDTO> getInvoiceLine(@QueryParam("invoiceId") long invoiceId){
		return invoiceService.getLinesForInvoice(invoiceId);
	}
	
	/**
	 * Update an invoice with the given informations
	 * @param formParams The values retrieved from the form.
	 * @param invoiceId Id of the invoice to update
	 * @return the visualization page of the invoice
	 */
	@POST
	@Path("/update/{invoiceId}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.TEXT_HTML })
	public Response updateInvoice(MultivaluedMap<String, String> formParams, @PathParam("invoiceId") long invoiceId){
		invoiceService.deleteAllInvoiceLines(invoiceId);
		InvoiceLineCreationRequestDTO lineRequest = new InvoiceLineCreationRequestDTO();
		for(int i = 0; i < formParams.get("designation-field[]").size(); i++){
			String designation = formParams.get("designation-field[]").get(i);
			String quantity = formParams.get("quantity-field[]").get(i);
			String unitPrice = formParams.get("unitprice-field[]").get(i);
			if(!designation.equals("")){
				lineRequest.designation = designation;
				if(quantity.equals("")){
					lineRequest.quantity = 0.0F;
				}else{
					lineRequest.quantity = Float.parseFloat(quantity);
				}
				if(unitPrice.equals("")){
					lineRequest.unitPrice = 0.0F;
				}else{
					lineRequest.unitPrice = Float.parseFloat(unitPrice);
				}
				lineRequest.invoiceId = invoiceId;
				invoiceService.createOrUpdateInvoiceLine(lineRequest);
			}
		}
		InvoiceCreationRequestDTO invoiceRequest = new InvoiceCreationRequestDTO();
		invoiceRequest.id = invoiceId;
		invoiceRequest.datePublication = formParams.get("invoice-sending").get(0);
		invoiceRequest.dateTerm = formParams.get("invoice-term").get(0);
		invoiceRequest.object = formParams.get("invoice-object").get(0);
		invoiceRequest.code = formParams.get("invoice-code").get(0);
		invoiceRequest.comment = formParams.get("invoice-comment").get(0);
		invoiceService.createOrUpdateInvoice(invoiceRequest);
		UriBuilder uriBuilder = UriBuilder.fromPath("/invoice/view/" + invoiceId);
		// Status 303 allows to redirect a request from POST to GET
		return Response.temporaryRedirect(uriBuilder.build()).status(303).build();
	}
}
