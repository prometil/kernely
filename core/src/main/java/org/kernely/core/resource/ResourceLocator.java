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

package org.kernely.core.resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows to retrieve resource in a media folder if they exist or in the jar by
 * default
 * 
 */
public class ResourceLocator {

	private static final Logger log = LoggerFactory.getLogger(ResourceLocator.class);

	/**
	 * Find the resource in the folder with a specific path
	 * 
	 * @param prefix
	 *            the folder like "../media"
	 * @param resource
	 *            the resource like /gsp/login.gsp
	 * @return the URL if the file is found
	 * @throws MalformedURLException
	 */
	public URL getResource(String prefix, String resource) throws MalformedURLException {
		log.trace("Looking for resource {} in {}", resource, prefix);
		if (resource == null || "".equals(resource)) {
			throw new IllegalArgumentException("file path cannot be null or empty");
		}

		// handle if the first character is a / or not
		if (resource.charAt(0) != '/') {
			resource = '/' + resource;
		}

		// add media directory to the url of the ressource
		String fullURL = prefix + resource;
		File file = new File(fullURL);
		if (!file.exists()) {
				return ResourceLocator.class.getResource(resource);
		}
		URL url = file.toURI().toURL();
		return url;
	}

	/**
	 * find a resource in media
	 * 
	 * @param resource
	 *            the path of the resource
	 * @return the url if the file exist
	 * @throws MalformedURLException
	 */
	public URL getResource(String resource) throws MalformedURLException {
		return getResource("../media", resource);
	}
}
