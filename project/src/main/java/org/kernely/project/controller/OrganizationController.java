package org.kernely.project.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kernely.core.controller.AbstractController;
import org.kernely.project.dto.OrganizationDTO;
import org.kernely.project.service.OrganizationService;

import com.google.inject.Inject;

@Path("/organization")
public class OrganizationController extends AbstractController {

	@Inject 
	private OrganizationService organizationService;
	
	/**
	 * Get all existing organizations in the database
	 * 
	 * @return A list of all DTO associated to the existing organizations in the
	 *         database
	 */
	@GET
	@Path("/all")
	@Produces({MediaType.APPLICATION_JSON})
	public List<OrganizationDTO> displayAllOrganizations() {
		log.debug("Call to GET on all organizations");
		return organizationService.getAllOrganizations();
	}
}
