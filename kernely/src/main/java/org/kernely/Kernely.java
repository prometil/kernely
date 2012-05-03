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

package org.kernely;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.configuration.CombinedConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.kernely.error.KernelyErrorHandler;
import org.kernely.guice.GuiceServletConfig;
import org.kernely.migrator.Migrator;
import org.kernely.plugin.AbstractPlugin;
import org.kernely.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;

/**
 * The project bootstraper
 * 
 * 
 */
public class Kernely {
	// Root of web content directory (jsp, css, js...)
	private static Logger log = LoggerFactory.getLogger(Kernely.class);

	/**
	 * Main function
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		log.info("Bootstrapping kernely");

		// Update the class loader with the plugins directory
		/*ClasspathUpdater p = new ClasspathUpdater("plugins");
		p.update();*/

		// Load all detected plugins
		List<AbstractPlugin> plugins = PluginManager.getPlugins();

		// configure
		CombinedConfiguration combinedConfiguration = PluginManager.getConfiguration();

		// update database using configuration
		Migrator m = new Migrator(combinedConfiguration, plugins);
		m.migrate();

		// create the upload directory (you can modify the url in core-conf.xml
		String directoryUrl = combinedConfiguration.getString("workspace");
		File workDirectory = new File(directoryUrl);
		try {
			if (workDirectory.mkdir()) {
				log.info("Create work directory : {}", workDirectory);
			}
			else{
				log.info("Using work directory : {}", workDirectory);
			}
			// Create the server
			int port = combinedConfiguration.getInt("server.port");
			Server server = new Server(port);
			
			// Retrieve resources located at the web content directory
			final String warUrlString = new URL("file://.").toExternalForm();
			// Register a listener
			ServletHandler handler = createServletHandler();
			WebAppContext webApp = new WebAppContext(warUrlString, "/");
			webApp.setServletHandler(handler);
			webApp.addEventListener(new GuiceServletConfig());
			webApp.setErrorHandler(new KernelyErrorHandler());
			server.setHandler(webApp);

			try {
				log.info("Starting server on {}", port);
				server.start();
				server.join();
			} catch (Exception e) {
				log.error("Error at start {}", e);
			}
		} catch (Exception e) {
			log.error("Cannot create working directory {}", workDirectory, e);
		}

	}

	/**
	 * Creates the servlet handler, with a guice filter holder which maps all
	 * pages.
	 * 
	 * @see #createGuiceFilterHolder()
	 * @see #createFilterMapping(String, FilterHolder)
	 * 
	 * @return the servlet handler
	 */
	private static ServletHandler createServletHandler() {
		ServletHandler servletHandler = new ServletHandler();

		FilterHolder guiceFilterHolder = createGuiceFilterHolder();
		servletHandler.addFilter(guiceFilterHolder, createFilterMapping("/*", guiceFilterHolder));

		return servletHandler;
	}

	/**
	 * Creates the guice filter holder.
	 * 
	 * @return the filter holder.
	 */
	private static FilterHolder createGuiceFilterHolder() {
		FilterHolder filterHolder = new FilterHolder(GuiceFilter.class);
		filterHolder.setName("guice");
		return filterHolder;
	}

	/**
	 * Creates the filter mapping based on the path spec and the filter holder
	 * 
	 * @param pathSpec
	 *            the path spec
	 * @param filterHolder
	 *            the filter holder
	 * @return the filter mapping.
	 */
	private static FilterMapping createFilterMapping(String pathSpec, FilterHolder filterHolder) {
		FilterMapping filterMapping = new FilterMapping();
		filterMapping.setPathSpec(pathSpec);
		filterMapping.setFilterName(filterHolder.getName());
		return filterMapping;
	}
}
