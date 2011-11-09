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
package org.kernely.bootstrap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.kernely.bootstrap.classpath.ClasspathUpdater;
import org.kernely.bootstrap.error.KernelyErrorHandler;
import org.kernely.bootstrap.guice.GuiceServletConfig;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
import org.kernely.core.resourceLocator.ResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;

/**
 * The project bootstrapper
 * 
 * 
 */
public class KernelyBootstrap {
	// Root of web content directory (jsp, css, js...)
	private static final Logger log = LoggerFactory.getLogger(KernelyBootstrap.class);

	public static void main(String[] args) throws IOException {
		log.info("Bootstrapping kernely");

		// Update the class loader with the plugins directory
		ClasspathUpdater p = new ClasspathUpdater("plugins");
		p.update();

		// Load all detected plugins
		PluginsLoader pluginLoad = new PluginsLoader();
		List<AbstractPlugin> plugins = pluginLoad.getPlugins();

		// configure
		setTheConfiguration(plugins);

		// Create the server
		Server server = new Server(setTheConfiguration(plugins).getInt("server.port"));

		// Retrieve resources located at the web content directory
		final String warUrlString = new URL("file://.").toExternalForm();
		// Register a listener
		ServletHandler handler = createServletHandler();
		WebAppContext webApp = new WebAppContext(warUrlString, "/");
		webApp.addEventListener(new GuiceServletConfig(plugins, setTheConfiguration(plugins)));
		webApp.setServletHandler(handler);
		webApp.setErrorHandler(new KernelyErrorHandler());
		server.setHandler(webApp);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			log.error("Error at start {}", e);
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

	/**
	 * create and set the configuration from a xml file
	 * 
	 * @param plugins
	 *            list of plugins
	 * @return the combinedconfiguration set
	 */
	private static CombinedConfiguration setTheConfiguration(List<AbstractPlugin> plugins) {
		ResourceLocator resourceLocator = new ResourceLocator();

		CombinedConfiguration combinedConfiguration = new CombinedConfiguration();
		// Bind all Jersey resources detected in plugins
		for (AbstractPlugin plugin : plugins) {
			String filepath = plugin.getConfigurationFilepath();
			if (filepath != null) {
				try {
					AbstractConfiguration configuration;
					try {
						configuration = new XMLConfiguration(resourceLocator.getResource("../config",filepath));
						log.info("Found configuration file {} for plugin {}", filepath, plugin.getName());
						combinedConfiguration.addConfiguration(configuration);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (ConfigurationException e) {
					log.error("Cannot find configuration file {} for plugin {}", filepath, plugin.getName());
				}
			}
		}
		return combinedConfiguration;
	}
}
