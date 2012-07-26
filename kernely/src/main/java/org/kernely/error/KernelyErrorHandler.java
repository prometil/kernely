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

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.actors.threadpool.Arrays;
import soba.javaops.SobaEngineFacade;

/**
 * Error handler to trace error in logs instead of displaying it on screen.
 */
public class KernelyErrorHandler extends ErrorHandler {

	// the logger
	private static Logger log = LoggerFactory.getLogger(KernelyErrorHandler.class);

	private SobaEngineFacade renderer;

	/**
	 * Construct the error handler and initialise the debug mode at false;
	 */
	public KernelyErrorHandler() {
		renderer = new SobaEngineFacade();
		this.setShowStacks(false);
	}

	public KernelyErrorHandler(Boolean pDebug) {
		super();
		this.setShowStacks(pDebug);
	}

	/**
	 * When an error is detected, display a custom page instead of displaying
	 * stack trace on screen.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException {
		log.error("{} - {} - {}", new Object[] { code, request.getRequestURI(), message });

		Map<String, Object> binding = new HashMap<String, Object>();
		binding.put("code", code);
		binding.put("message", message);
		binding.put("debug", showStacks);
		
		StackTraceElement[] stackTrace = ( (Throwable)request.getAttribute(Dispatcher.ERROR_EXCEPTION)).getStackTrace();
		binding.put("stack", Arrays.asList(stackTrace));
		binding.put("exception_type", ((Class)request.getAttribute(Dispatcher.ERROR_EXCEPTION_TYPE)).getName());
		
		writer.write(renderer.render("templates/" + code + ".html", binding));
	}

	@Override
	protected void writeErrorPageStacks(HttpServletRequest request, Writer writer) throws IOException {
		super.writeErrorPageStacks(request, writer);
		writer.write("This is sparta");
	}

}
