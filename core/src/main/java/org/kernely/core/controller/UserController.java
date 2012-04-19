/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.core.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.SecurityUtils;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.dto.UserDetailsUpdateRequestDTO;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.SobaTemplateRenderer;
import org.kernely.core.template.TemplateRenderer;

import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * The controller of the user profile page.
 */
@Path("/user")
public class UserController extends AbstractController {

	@Inject
	private AbstractConfiguration configuration;

	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

	/**
	 * Display the list of users.
	 * 
	 * @return The html content to display the list.
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getText() {
		log.debug("Call to GET on all users");
		List<UserDTO> users = userService.getAllUsers();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("users", users);
		return Response.ok(templateRenderer.render("templates/users.html",map)).build();
	}

	/**
	 * Displays the login page.
	 * 
	 * @return The html content of the login page.details
	 */
	@GET
	@Path("/login")
	@Produces( { MediaType.TEXT_HTML })
	public Response login() {
		Map<String,Object> map = new HashMap<String,Object>();
		return Response.ok(templateRenderer.render("templates/login.html",map)).build();
	}

	/**
	 * Displays the login page.
	 * 
	 * @return The html content of the login page.
	 */
	@POST
	@Path("/login")
	@Produces( { MediaType.TEXT_HTML })
	public Response postLogin() {
		log.info("Login attempt : is authenticated {}", SecurityUtils.getSubject().isAuthenticated());

		Map<String,Object> map = new HashMap<String,Object>();
		return Response.ok(templateRenderer.render("templates/login.html",map)).build();
	}

	/**
	 * Logs out the user and redirect to the main page.
	 * 
	 * @return a redirection to the main page;
	 */
	@GET
	@Path("/logout")
	@Produces( { MediaType.TEXT_HTML })
	public Response logout() {
		log.info("Login attempt");
		SecurityUtils.getSubject().logout();
		return redirect("/");
	}

	/**
	 * Display the profile of the user who has the specified login
	 * 
	 * @param userLogin
	 *            The login of the user that profile is needed
	 * @return the profile page fill with associated informations
	 */
	@GET
	@Path("/{login}/profile")
	@Produces( { MediaType.TEXT_HTML })
	public Response profil(@PathParam("login") String userLogin) {
		String template = "templates/profile.html";
		UserDTO usercurrent = this.getCurrent();
		if (usercurrent.username.equals(userLogin)) {
			template = "templates/profile_editable.html";
		}
		UserDetailsDTO uddto = userService.getUserDetails(userLogin);
		if(uddto.image==null){
			uddto.image="default_user.png";
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("details", uddto);
		return Response.ok(templateRenderer.render(template, map)).build();
	}

	/**
	 * Edit the profile of the current user with the new informations contained
	 * in the UserDetailsUpdateRequestDTO user
	 * 
	 * @param user
	 *            the DTO which contained all new informations about the user to
	 *            update
	 * @return the DTO associated to the user updated
	 */
	@POST
	@Path("/{login}/profile/update")
	public UserDetailsDTO editProfil(UserDetailsUpdateRequestDTO user) {

		UserDetailsDTO ud = userService.getUserDetails(userService.getAuthenticatedUserDTO().username);
		// Match the user id (foreign key) with the userdetailid
		user.id = ud.id;
		
		// Call UserService to update informations
		return userService.updateUserProfile(user);
	}

	/**
	 * Upload a specified file
	 * 
	 * @param uploadedInputStream
	 *            the InputStream corresponding to the file uploaded
	 * @param fileDetail
	 *            the informations about file uploaded
	 */
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.TEXT_HTML})
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
		if (fileDetail.getFileName().equals("")) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("details",userService.getUserDetails(userService.getAuthenticatedUserDTO().username));
			return Response.ok(templateRenderer.render("templates/profile_editable.html",map)).build();
		}
		// get extension
		String[] extension = fileDetail.getFileName().split("\\.");
		if (extension.length < 2) {
			throw new IllegalArgumentException("The file need an extension");
		}

		SecureRandom random = new SecureRandom();
		String fileName = new BigInteger(130, random).toString(32) + "." + extension[extension.length - 1];
		String prefix = configuration.getString("workpath.url");
		String uploadedFileLocation = prefix + "/images/" + fileName; // /core/src/main/resources
		try {
			FileUtils.copyInputStreamToFile(uploadedInputStream, new File(uploadedFileLocation));
		} catch (IOException e) {
			log.debug(e.getMessage());
		}

		// up to date the database
		UserDetailsDTO user = userService.getUserDetails(userService.getAuthenticatedUserDTO().username);

		UserDetailsUpdateRequestDTO ud = new UserDetailsUpdateRequestDTO(user.firstname, user.lastname, fileName, user.email, user.adress, user.zip, user.city, user.homephone, user.mobilephone, user.businessphone, user.birth, user.nationality, user.ssn, user.id, user.civility);
		userService.updateUserProfile(ud);
		
		//get the dto modified
		String template="templates/profile_editable.html";
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("details",userService.getUserDetails(userService.getAuthenticatedUserDTO().username));
		return Response.ok(templateRenderer.render(template,map)).build();
	}
	
	/**
	 * Get the DTO associated to the current user
	 * 
	 * @return the DTO associated to the current user
	 */
	@GET
	@Path("/current")
	@Produces( { MediaType.APPLICATION_JSON })
	public UserDTO getCurrent() {
		return userService.getAuthenticatedUserDTO();
	}

	/**
	 * Get the DTO associated to the specified user who has the login 'login'
	 * 
	 * @param userLogin
	 *            the login of the needed user
	 * @return the DTO associated to the specified user
	 */
	@GET
	@Path("/{login}")
	@Produces( {MediaType.APPLICATION_JSON})
	public UserDetailsDTO getDetails(@PathParam("login") String userLogin) {
		return userService.getUserDetails(userLogin);

	}
}
