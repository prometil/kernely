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

package org.kernely.error;

import groovy.text.SimpleTemplateEngine;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Error handler to trace error in logs instead of displaying it on screen.
 */
public class KernelyErrorHandler extends ErrorHandler {
	
	
	//the logger
	private static Logger log = LoggerFactory.getLogger(KernelyErrorHandler.class);
	

	/**
	 * When an error is detected, display a custom page instead of displaying stack trace on screen.
	 */
	@Override
	protected void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException {
		
		log.error("{} - {} - {}",new Object[]{message, code, request.getRequestURI()});
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		URL layout = KernelyErrorHandler.class.getResource("/templates/gsp/error.gsp");
		try {
			String body = engine.createTemplate(layout).make().toString();
			IOUtils.write(body, writer);
		} catch (CompilationFailedException e) {
			log.error("Oops something went wrong on the error page", e);
		} catch (ClassNotFoundException e) {
			log.error("Oops something went wrong on the error page", e);
		}
		
	}

	

}
