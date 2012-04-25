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
package org.kernely.core.plugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.kernely.core.resource.ResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load all plugins
 */
public class PluginManager {
	private static Logger log = LoggerFactory.getLogger(PluginManager.class);
	
	private List<AbstractPlugin> plugins ;

	/**
	 * Find the plugins
	 */
	public List<AbstractPlugin> getPlugins() {
		if(plugins == null){
			plugins = new ArrayList<AbstractPlugin>();

			ServiceLoader<AbstractPlugin> commandLoader = ServiceLoader.load(AbstractPlugin.class);
			// commandLoader.reload();
			Iterator<AbstractPlugin> it = commandLoader.iterator();
			while (it.hasNext()) {
				AbstractPlugin plugin = it.next();
				log.debug("Plugin {} found", plugin.getMenus().get(0));
				plugins.add(plugin);

			}
		}
		return plugins;
	}
	/**
	 * Create and set the configuration from a xml file
	 * 
	 * @param plugins
	 *            list of plugins
	 * @return the combinedconfiguration set
	 */
	public  CombinedConfiguration getConfiguration() {
		List<AbstractPlugin> plugins = getPlugins();
		ResourceLocator resourceLocator = new ResourceLocator();
		CombinedConfiguration combinedConfiguration = new CombinedConfiguration();
		// Bind all Jersey resources detected in plugins
		for (AbstractPlugin plugin : plugins) {
			
			String filepath = plugin.getName()+".xml";
			log.debug("Searching configuration file {}",filepath);
			if (filepath != null) {
				try {
					AbstractConfiguration configuration;
					try {
						URL resource = resourceLocator.getResource("../config", filepath);
						if(resource != null){
							configuration = new XMLConfiguration(resource);
							log.info("Found configuration file {} for plugin {}", filepath, plugin.getName());
							combinedConfiguration.addConfiguration(configuration);
						}
					} catch (MalformedURLException e) {
						log.error("Cannot find configuration file : {}", filepath);
					}

				} catch (ConfigurationException e) {
					log.error("Cannot find configuration file {} for plugin {}", filepath, plugin.getName());
				}
			}
		}
		return combinedConfiguration;
	}

}
