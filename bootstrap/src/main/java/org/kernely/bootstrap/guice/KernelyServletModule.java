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
package org.kernely.bootstrap.guice;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.kernely.bootstrap.MediaServlet;
import org.kernely.core.controller.AbstractController;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.resource.ResourceLocator;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * @author g.breton
 * 
 */
public class KernelyServletModule extends JerseyServletModule {

	private static Logger log = LoggerFactory.getLogger(KernelyServletModule.class);

	private List<? extends AbstractPlugin> plugins;
	
	//configurations
	private final CombinedConfiguration combinedConfiguration;

	/**
	 * Constructor.
	 * 
	 * @param plugins
	 *            The list of plugins to configure.
	 */
	public KernelyServletModule(List<? extends AbstractPlugin> plugins, CombinedConfiguration combinedConfiguration) {
		this.plugins = plugins;
		this.combinedConfiguration = combinedConfiguration;
	}

	/**
	 * Bind the servlet of the application
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void configureServlets() {

		// Bind all Jersey resources detected in plugins
		for (AbstractPlugin plugin : plugins) {
			for (Class<? extends AbstractController> controllerClass : plugin.getControllers()) {
				log.debug("Register controller {}", controllerClass);
				bind(controllerClass);
			}
		}
		bind(AbstractConfiguration.class).toInstance(combinedConfiguration);
		bind(ResourceLocator.class);
		
		
		// persistence
		Iterator<String> keys = combinedConfiguration.getKeys("hibernate");
		Properties properties = new Properties();
		while (keys.hasNext()) {
			String key = keys.next();
			properties.put(key, combinedConfiguration.getProperty(key));
		}

		// the jpa persiste module
		JpaPersistModule module = new JpaPersistModule("kernelyUnit").properties(properties);
		install(module);
		filter("/*").through(PersistFilter.class);

		//bind the scheduler factory
		bind(SchedulerFactory.class).to(StdSchedulerFactory.class);

		// Allows to retrieve resources .js, .css, .png
		bind(DefaultServlet.class).in(Singleton.class);
		bind(MediaServlet.class).in(Singleton.class);
	
		//serve resource type
		serve("*.ico").with(MediaServlet.class);
		serve("*.js").with(MediaServlet.class);
		serve("*.css").with(MediaServlet.class);
		serve("*.png").with(MediaServlet.class);
		serve("*.jpg").with(MediaServlet.class);
		serve("/*").with(GuiceContainer.class);
		
	}
}
