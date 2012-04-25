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
package org.kernely.bootstrap.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.io.IOUtils;
import org.kernely.core.resource.ResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * MediaServlet is used to detect resources type : css, png, ico ///
 */
public class MediaServlet extends HttpServlet {

	private static final long serialVersionUID = 3840583598946361059L;

	private static Logger log = LoggerFactory.getLogger(MediaServlet.class);

	@Inject
	private ResourceLocator resourceLocator;

	@Inject
	private AbstractConfiguration configuration;

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String servletPath = request.getServletPath();
		URL url;
		try {
			// retrieve the path in the config file.
			String prefix = configuration.getString("workpath.url");
			url = resourceLocator.getResource(prefix, servletPath);
			if (url == null) {
				log.error("Cannot find url {}", servletPath);
			} else {
				String type = this.getServletContext().getMimeType(servletPath);
				response.setContentType(type);

				InputStream input;
				try {
					input = url.openStream();
					OutputStream pw;
					try {
						pw = response.getOutputStream();
						IOUtils.copy(input, pw);
						input.close();
						pw.flush();
					} catch (IOException e) {
						log.error("Cannot write file {}", servletPath);
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					log.error("Cannot write file {}",e1);
				}

			}

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			log.error("malformed url {}", e1);
		}

	}

}