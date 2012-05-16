package org.kernely.invoice.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.model.Role;
import org.kernely.core.service.UserService;
import org.kernely.invoice.dto.InvoiceCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceDTO;
import org.kernely.invoice.dto.InvoiceLineCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceLineDTO;
import org.kernely.invoice.dto.VatDTO;
import org.kernely.invoice.service.InvoiceService;
import org.kernely.menu.Menu;
import org.kernely.project.dto.OrganizationDTO;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.service.OrganizationService;
import org.kernely.project.service.ProjectService;
import org.kernely.template.SobaTemplateRenderer;

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
	private SobaTemplateRenderer templateRenderer; 
	
	/**
	 * Retrieves the main page of the invoice
	 * @return The html page
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Menu("invoice")
	public Response getInvoicePanel(){
		Map<String, Object> map =new HashMap<String, Object>();
		return Response.ok(templateRenderer.render("templates/invoice_overview.html", map)).build();
	}
	
	/**
	 * Retrieves all the organizations in function of the role of the current user.
	 * If the user is Project Manager, retrieves all the organization of his projects
	 * Else, if he's Book Keeper, retrieve all the organization
	 * @return A list of all the linked organizations
	 */
	@GET
	@Path("/organizations")
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Produces({MediaType.APPLICATION_JSON})
	public List<OrganizationDTO> getOrganizationForRole(){
		if(userService.currentUserHasRole(Role.ROLE_BOOKKEEPER)){
			return organizationService.getAllOrganizationsWithProjects();
		}
		else{
			if(userService.currentUserHasRole(Role.ROLE_PROJECTMANAGER)){ 
				return organizationService.getOrganizationForProjectManager();
			}			
		}
		return new ArrayList<OrganizationDTO>();
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
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
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
		return new ArrayList<ProjectDTO>();
	}
	
	/**
	 * Gets the invoices in function of an organization and a project
	 * @param organizationId The organization id
	 * @param projectId The project id
	 * @return A JSON String representing the result data
	 */
	@GET
	@Path("/specific")
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
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
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces( { MediaType.TEXT_HTML })
	public Response createInvoice(MultivaluedMap<String, String> formParams){
		
		InvoiceCreationRequestDTO request = new InvoiceCreationRequestDTO();
		request.id = 0;
		request.object="";
		request.projectId = Integer.parseInt(formParams.get("project").get(0));
		request.datePublication = formParams.get("from").get(0);
		request.dateTerm = formParams.get("to").get(0);
		
		URI uri;
		try{
			InvoiceDTO invoice = invoiceService.createOrUpdateInvoice(request);
			uri = new URI("/invoice/" + invoice.id + "/edit");
			return Response.temporaryRedirect(uri).status(303).build();
		}
		catch(IllegalArgumentException iae){
			UriBuilder uriBuilder = UriBuilder.fromPath("/invoice");
			return Response.temporaryRedirect(uriBuilder.build()).status(303).build();
		}
		catch (URISyntaxException e) {
			UriBuilder uriBuilder = UriBuilder.fromPath("/invoice");
			return Response.temporaryRedirect(uriBuilder.build()).status(303).build();
		}
	}
	
	/**
	 * Creates a new invoice line
	 * @param request The request containing all needed informations
	 * @return A JSON String representing the new invoice line created
	 */
	@POST
	@Path("/line/create")
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
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
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Path("/delete")
	@Produces( { MediaType.TEXT_HTML })
	public Response deleteInvoice(@QueryParam("invoiceId") long invoiceId){
		invoiceService.deleteInvoice(invoiceId);
		URI uri;
		try {
			uri = new URI("/invoice");
			return Response.temporaryRedirect(uri).status(303).build();
		} catch (URISyntaxException e) {
			UriBuilder uriBuilder = UriBuilder.fromPath("/invoice");
			return Response.temporaryRedirect(uriBuilder.build()).status(303).build();
		}
	}
	
	/**
	 * Publish an invoice
	 * @param invoiceId The id of the invoice to publish
	 * @return The invoice updated
	 */
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Path("/publish")
	@Produces({MediaType.APPLICATION_JSON})
	public InvoiceDTO publishInvoice(@QueryParam("invoiceId") long invoiceId){
		return invoiceService.setInvoiceAsPublished(invoiceId);
	}
	
	/**
	 * Set an invoice as paid
	 * @param invoiceId The id of the invoice to pay
	 * @return The invoice updated
	 */
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Path("/paid")
	@Produces({MediaType.APPLICATION_JSON})
	public InvoiceDTO payInvoice(@QueryParam("invoiceId") long invoiceId){
		return invoiceService.setInvoiceAsPaid(invoiceId);
	}
	
	/**
	 * Set an invoice as unpaid
	 * @param invoiceId The id of the invoice to pay
	 * @return The invoice updated
	 */
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Path("/unpaid")
	@Produces({MediaType.APPLICATION_JSON})
	public InvoiceDTO unpayInvoice(@QueryParam("invoiceId") long invoiceId){
		return invoiceService.setInvoiceAsUnpaid(invoiceId);
	}
	
	/**
	 * Loads the html page displaying the needed invoice
	 * @param invoiceId The id of the invoice to display
	 * @return An html page
	 */
	@GET
	@Path("/{invoiceId}/view")
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Produces( { MediaType.TEXT_HTML })
	public Response visualizeInvoice(@PathParam("invoiceId") long invoiceId) {
		InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId);
		if(invoiceDTO != null){
			Map<String, Object> map =new HashMap<String, Object>();
			map.put("invoice", invoiceDTO);
			map.put("invoiceLines", invoiceDTO.lines);
			map.put("invoiceVats", invoiceDTO.vats);
			return Response.ok(templateRenderer.render("templates/invoice.html", map)).build();
		}
		UriBuilder uriBuilder = UriBuilder.fromPath("/invoice");
		return Response.temporaryRedirect(uriBuilder.build()).status(303).build();
	}
	
	/**
	 * Loads the html page displaying the needed invoice in edition mode
	 * @param invoiceId The id of the invoice to display
	 * @return An html page
	 */
	@GET
	@Path("/{invoiceId}/edit")
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Produces( { MediaType.TEXT_HTML })
	public Response editInvoice(@PathParam("invoiceId") long invoiceId) {
		InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId);
		if(invoiceDTO != null){
			
			Map<String, Object> map =new HashMap<String, Object>();
			map.put("invoice", invoiceDTO);
			map.put("invoiceLines", invoiceDTO.lines);
			return Response.ok(templateRenderer.render("templates/invoice_editable.html", map)).build();
		}	
		UriBuilder uriBuilder = UriBuilder.fromPath("/invoice");
		return Response.temporaryRedirect(uriBuilder.build()).status(303).build();
	}
	
	/**
	 * Gets all lines relative to an invoice
	 * @param invoiceId Id of the needed invoice
	 * @return A list of DTO containing the lines of the given invoice
	 */
	@GET
	@Path("/lines")
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
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
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.TEXT_HTML })
	public Response updateInvoice(MultivaluedMap<String, String> formParams, @PathParam("invoiceId") long invoiceId){
		InvoiceLineCreationRequestDTO lineRequest = new InvoiceLineCreationRequestDTO();
		float amountCalculated = 0.0F;
		for(int i = 0; i < formParams.get("designation-field[]").size(); i++){
			String id = formParams.get("id-field[]").get(i);
			if(id.equals("")){
				lineRequest.id = 0;
			}else{
				lineRequest.id = Integer.parseInt(id);
			}
			String designation = formParams.get("designation-field[]").get(i);
			String quantity = formParams.get("quantity-field[]").get(i);
			String unitPrice = formParams.get("unitprice-field[]").get(i);
			String vat = formParams.get("vat-field[]").get(i);
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
				lineRequest.vat = Float.parseFloat(vat);
				
				lineRequest.invoiceId = invoiceId;
				invoiceService.createOrUpdateInvoiceLine(lineRequest);
				amountCalculated += (lineRequest.quantity * lineRequest.unitPrice)*(1 + (lineRequest.vat/100));
			}
		}
		InvoiceCreationRequestDTO invoiceRequest = new InvoiceCreationRequestDTO();
		
		invoiceRequest.id = invoiceId;
		invoiceRequest.datePublication = formParams.get("invoice-sending").get(0);
		invoiceRequest.dateTerm = formParams.get("invoice-term").get(0);
		invoiceRequest.object = formParams.get("invoice-object").get(0);
		invoiceRequest.code = formParams.get("invoice-code").get(0);
		invoiceRequest.comment = formParams.get("invoice-comment").get(0);
		invoiceRequest.amount = amountCalculated;
		invoiceService.createOrUpdateInvoice(invoiceRequest);
		UriBuilder uriBuilder = UriBuilder.fromPath("/invoice/" + invoiceId + "/view");
		// Status 303 allows to redirect a request from POST to GET
		return Response.temporaryRedirect(uriBuilder.build()).status(303).build();
	}
	
	@GET
	@Path("/vat")
	@RequiresRoles(value = {Role.ROLE_PROJECTMANAGER, Role.ROLE_BOOKKEEPER}, logical = Logical.OR)
	@Produces({MediaType.APPLICATION_JSON})
	public List<VatDTO> getVat(){
		return invoiceService.getVAT();
	}
}
