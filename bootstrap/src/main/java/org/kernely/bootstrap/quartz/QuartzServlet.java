/**
 * 
 */
package org.kernely.bootstrap.quartz;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hsqldb.Servlet;

/**
 * @author g.breton
 *
 */
@SuppressWarnings("serial")
public class QuartzServlet extends Servlet{

	@Override
	public void doGet(HttpServletRequest arg0, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		super.doGet(arg0, response);
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	@Override
	public void doPost(HttpServletRequest arg0, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		super.doPost(arg0, response);
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	@Override
	public void init(ServletConfig arg0) {
		// TODO Auto-generated method stub
		super.init(arg0);
		
		//create
		
	}

}
