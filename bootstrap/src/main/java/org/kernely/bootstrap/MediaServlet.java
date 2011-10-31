package org.kernely.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 */
public class MediaServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3840583598946361059L;

	private static final Logger log = LoggerFactory.getLogger(MediaServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		String servletPath = request.getServletPath();
		URL url = MediaServlet.class.getResource(servletPath);
		if (url == null) {
			log.error("Cannot find url {}", servletPath);
		} else {
			//String extension = servletPath.substring(servletPath.lastIndexOf(".")) ;
			
			String type = this.getServletContext().getMimeType(servletPath);
			response.setContentType(type);
			InputStream input = MediaServlet.class.getResourceAsStream(servletPath);
			OutputStream pw;
			try {
				pw = response.getOutputStream();
				IOUtils.copy(input, pw);
				input.close();
				pw.flush();
			} catch (IOException e) {
				log.error("Cannot write file {}", servletPath);
			}

			
		}

	}

}