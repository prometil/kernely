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
package org.kernely.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.kernely.core.CorePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * Load all plugins
 */
public class PluginManager {
	private static Logger log = LoggerFactory.getLogger(PluginManager.class);

	private List<AbstractPlugin> plugins;

	private CombinedConfiguration configuration;

	private PluginManager() {
		plugins = findPlugins();
	}

	private static PluginManager instance;

	public static PluginManager getInstance() {
		if (instance == null) {
			instance = new PluginManager();
		}
		return instance;
	}

	/**
	 * Return the plugins list
	 * 
	 * @return a list of abstract plugin
	 */
	public static List<AbstractPlugin> getPlugins() {
		return getInstance().plugins;
	}

	/**
	 * Find the plugins
	 */
	@SuppressWarnings("unchecked")
	private List<AbstractPlugin> findPlugins() {
		List<AbstractPlugin> plugins = new ArrayList<AbstractPlugin>();

		ObjectMapper mapper = new ObjectMapper();
		configuration = new CombinedConfiguration();

		// add core plugin
		
		CorePlugin corePlugin = new CorePlugin();
		
		log.info("Loading plugin {}-{}", corePlugin.getName(), corePlugin.getVersion());
		plugins.add(corePlugin);
		String configFile = "core.xml";
		try {
			URL resource = getDefaultClassLoader().getResource(configFile);
			XMLConfiguration config = new XMLConfiguration(resource);
			configuration.addConfiguration(config);
			
			Enumeration<URL> resources;
			try {
				log.debug("Looking for plugin.json files");
				resources = getDefaultClassLoader().getResources("plugin.json");
				while (resources.hasMoreElements()) {
					URL u = resources.nextElement();

					BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
					try {
						Map<String, Object> pluginData = mapper.readValue(in, Map.class);
						Manifest m = new Manifest();
						m.name = (String) pluginData.get("name");
						m.author = (String) pluginData.get("version");
						m.description = (String) pluginData.get("definition");
						m.version = (String) pluginData.get("version");
						// load configuration

						log.info("Loading plugin {}-{}", m.name, m.version);

						Object object = pluginData.get("plugin");
						if (object != null) {
							String pluginClassName = (String) object;
							Class<? extends AbstractPlugin> pluginClass;
							try {
								pluginClass = (Class<? extends AbstractPlugin>) getDefaultClassLoader().loadClass(pluginClassName);
								AbstractPlugin plugin;
								try {
									plugin = pluginClass.getConstructor().newInstance();
									plugin.setManifest(m);
									plugins.add(plugin);
								} catch (NoSuchMethodException e1) {
									log.error("Cannot find contructor for [{}]", m.name);
								} catch (IllegalArgumentException e) {
									log.error("Cannot call contructor for [{}]", m.name);
								} catch (SecurityException e) {
									log.error("Cannot call contructor for [{}]", m.name);
								} catch (InstantiationException e) {
									log.error("Cannot call contructor for [{}]", m.name);
								} catch (IllegalAccessException e) {
									log.error("Cannot call contructor for [{}]", m.name);
								} catch (InvocationTargetException e) {
									log.error("Cannot call contructor for [{}]", m.name);
								}

							} catch (ClassNotFoundException e2) {
								log.error("Cannot find class [{}]", pluginClassName);
							}

							// plugins.put(plugin.name(), plugin);
							for (Map.Entry<String, Object> entry : pluginData.entrySet()) {
								if (entry.getKey().equals("configuration")) {
									configuration.addConfiguration(getConfiguration((Map<String, Object>) entry.getValue()));
								}
							}
						} else {
							log.warn("Cannot find plugin definition in plugin.json, nothing has been loaded");
						}

					} catch (JsonParseException e1) {
						log.error("Cannot parse json plug-in definition", e1);
					} catch (JsonMappingException e1) {
						log.error("Cannot convert json plug-in definition", e1);
					} catch (IOException e1) {
						log.error("Cannot read json plug-in definition", e1);
					}
					in.close();

				}
			} catch (IOException e) {
				log.error("Cannot find plug-in definition ", e);
			}
		} catch (ConfigurationException e3) {
			log.error("Cannot load {}, stopping", configFile);
		}

		return plugins;

	}

	/**
	 * Generate a configuration from a map
	 * 
	 * @param configurations
	 *            the map containing all key
	 * @return the generate configuration
	 */
	@SuppressWarnings("unchecked")
	private AbstractConfiguration getConfiguration(Map<String, Object> configurations) {
		if (configurations == null) {
			throw new IllegalArgumentException("Cannot convert a null map");
		}
		AbstractConfiguration c = new HierarchicalConfiguration();
		addConfiguration(c, configurations, Collections.EMPTY_LIST);
		return c;
	}

	/**
	 * Adds configurations key to an existing configuration
	 * 
	 * @param configuration
	 *            the configuration to upgrade
	 * @param configurations
	 *            the configuration to add
	 * @param prefixes
	 *            the prefix of the configuration key
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addConfiguration(AbstractConfiguration configuration, Map<String, Object> configurations, List<String> prefixes) {
		Joiner joiner = Joiner.on(".").skipNulls();
		for (Map.Entry<String, Object> config : configurations.entrySet()) {
			List<String> key = new ArrayList<String>(prefixes);
			key.add(config.getKey());

			if (config.getValue() instanceof Map) {
				addConfiguration(configuration, (Map) config.getValue(), key);
			} else {
				configuration.addProperty(joiner.join(key), config.getValue());
			}
		}
	}

	/**
	 * Returns the default class loader
	 * 
	 * @return the default class loader
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system
			// class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = PluginManager.class.getClassLoader();
		}
		return cl;
	}

	/**
	 * Returns the configuration, defined by a merge of all plugin
	 * configuration.
	 * 
	 * @return the combined configuration set
	 */
	public static CombinedConfiguration getConfiguration() {
		return getInstance().configuration;
	}

}
