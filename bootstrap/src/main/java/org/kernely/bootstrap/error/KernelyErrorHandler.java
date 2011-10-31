/**
 * 
 */
package org.kernely.bootstrap.error;

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
 * @author g.breton
 *
 */
public class KernelyErrorHandler extends ErrorHandler {
	
	
	//the logger
	private static final Logger log = LoggerFactory.getLogger(KernelyErrorHandler.class);
	

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
