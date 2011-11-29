/*
Copyright 2011 Prometil SARL

This file is part of Kernely.

Kernely is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Kernely is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Kernely.
If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.core.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

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
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.dto.UserDetailsUpdateRequestDTO;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;

import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/user")
public class UserController extends AbstractController {

	@Inject
	private AbstractConfiguration configuration;

	@Inject
	private TemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

	/**
	 * Display the list of users.
	 * 
	 * @return The html content to display the list.
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getText() {
		log.debug("Call to GET on all users");
		List<UserDTO> users = userService.getAllUsers();
		return templateRenderer.create("/templates/gsp/users.gsp").with("users", users).render();
	}

	/**
	 * Create a new user with a random username and as password : "password".
	 * 
	 * @return "Ok", not useful.
	 */
	@GET
	@Path("/create")
	@Produces( { MediaType.TEXT_PLAIN })
	public String create() {
		log.debug("Create a user");
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = "user";
		request.password = "password";
		userService.createUser(request);
		return "User created";
	}

	/**
	 * Displays the login page.
	 * 
	 * @return The html content of the login page.
	 */
	@GET
	@Path("/login")
	@Produces( { MediaType.TEXT_HTML })
	public String login() {
		return templateRenderer.create("/templates/gsp/login.gsp").withoutLayout().render();
	}

	/**
	 * Displays the login page.
	 * 
	 * @return The html content of the login page.
	 */
	@POST
	@Path("/login")
	@Produces( { MediaType.TEXT_HTML })
	public String postLogin() {
		log.info("Login attempt : is authenticated {}", SecurityUtils.getSubject().isAuthenticated());

		return templateRenderer.create("/templates/gsp/login.gsp").withoutLayout().render();
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
	public String profil(@PathParam("login") String userLogin) {
		String template = "/templates/gsp/profile.gsp";
		UserDTO usercurrent = this.getCurrent();
		if (usercurrent.username.equals(userLogin)) {
			template = "/templates/gsp/profile_editable.gsp";
		}
		UserDetailsDTO uddto = userService.getUserDetails(userLogin);
		String imagePath = "/images/default_user.png";
		if (uddto.image != null && uddto.image.equals("null")) {
			uddto.image = null;
		}
		if (uddto.image != null) {
			imagePath = "/images/" + uddto.image;
		}

		return templateRenderer.create(template).with("username", uddto.firstname + " " + uddto.lastname).with("lastname", uddto.lastname).with("firstname", uddto.firstname).with("email", uddto.email).with("image", imagePath).with("imagename", uddto.image).with("adress", uddto.adress).with("zip",
				uddto.zip).with("city", uddto.city).with("homephone", uddto.homephone).with("mobilephone", uddto.mobilephone).with("businessphone", uddto.businessphone).with("birth", uddto.birth).with("nationality", uddto.nationality).with("ssn", uddto.ssn).with("civility", uddto.civility).render();
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

		UserDetailsDTO ud = userService.getUserDetails(userService.getCurrentUser().username);
		UserDetailsDTO uddto = new UserDetailsDTO(user.firstname, user.lastname, user.image, user.email, user.adress, user.zip, user.city, user.homephone, user.mobilephone, user.businessphone, user.birth, user.nationality, user.ssn, user.civility, ud.id, new UserDTO());

		// Match the user id (foreign key) with the userdetailid
		user.userDetailsId = ud.id;
		// Call UserService to update informations
		userService.updateUserProfile(user);
		return uddto;
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
	public void uploadFile(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
		if (fileDetail.getFileName().equals("")) {
			return;
		}
		// get extension
		String[] extension = fileDetail.getFileName().split("\\.");
		if (extension.length < 2) {
			throw new IllegalArgumentException("the file need an extansion");
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
		UserDetailsDTO user = userService.getUserDetails(userService.getCurrentUser().username);

		UserDetailsUpdateRequestDTO ud = new UserDetailsUpdateRequestDTO(user.firstname, user.lastname, fileName, user.email, user.adress, user.zip, user.city, user.homephone, user.mobilephone, user.businessphone, user.birth, user.nationality, user.ssn, user.id, user.civility);
		userService.updateUserProfile(ud);

	}

	/**
	 * Get the DTO associated to the current user
	 * 
	 * @return the DTO associated to the current user
	 */
	@GET
	@Path("/current")
	@Produces( { "application/json" })
	public UserDTO getCurrent() {
		return userService.getCurrentUser();
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
	@Produces( { "application/json" })
	public UserDetailsDTO getDetails(@PathParam("login") String userLogin) {
		return userService.getUserDetails(userLogin);

	}
}
