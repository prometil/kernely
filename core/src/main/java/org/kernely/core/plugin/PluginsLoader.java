package org.kernely.core.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginsLoader {
	public static final Logger log = LoggerFactory.getLogger(PluginsLoader.class);

	/**
	 * 
	 */
	public List<AbstractPlugin> getPlugins() {

		List<AbstractPlugin> plugins = new ArrayList<AbstractPlugin>();

		ServiceLoader<AbstractPlugin> commandLoader = ServiceLoader.load(AbstractPlugin.class);
		// commandLoader.reload();
		Iterator<AbstractPlugin> it = commandLoader.iterator();
		while (it.hasNext()) {
			AbstractPlugin plugin = it.next();
			plugins.add(plugin);

		}
		return plugins;
	}

}
