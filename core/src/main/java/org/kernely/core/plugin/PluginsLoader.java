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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginsLoader {
	private static Logger log = LoggerFactory
			.getLogger(PluginsLoader.class);

	/**
	 * 
	 */
	public List<AbstractPlugin> getPlugins() {

		List<AbstractPlugin> plugins = new ArrayList<AbstractPlugin>();

		ServiceLoader<AbstractPlugin> commandLoader = ServiceLoader
				.load(AbstractPlugin.class);
		// commandLoader.reload();
		Iterator<AbstractPlugin> it = commandLoader.iterator();
		while (it.hasNext()) {
			AbstractPlugin plugin = it.next();
			log.debug("Plugin {} found", plugin.getName());
			plugins.add(plugin);

		}
		return plugins;
	}

}
