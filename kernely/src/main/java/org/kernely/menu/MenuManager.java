/**
 * 
 */
package org.kernely.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * The menu manager hold the menu items and generate a menu depending on roles
 */
public class MenuManager {

	// the log menu manager
	protected static final Logger log = LoggerFactory.getLogger(MenuManager.class);

	// the menu items
	private HashMap<String, Set<MenuItem>> items;
	

	public MenuManager() {
		items = new HashMap<String, Set<MenuItem>>();
	}

	/**
	 * Generate the menu depending on rights
	 * 
	 * @param rights
	 *            the rights
	 * @return
	 */
	public List<PluginMenu> generateMenu(Set<String> rights) {
		List<PluginMenu> menus = new ArrayList<PluginMenu>();
		for (Entry<String, Set<MenuItem>> entry : items.entrySet()) {
			List<MenuItem> generatedMenu = new ArrayList<MenuItem>();
			for (MenuItem item : entry.getValue()) {
				if (item.rights.size() == 0 || Sets.intersection(rights, item.rights).size() > 0) {
					generatedMenu.add(item);
				}

			}
			if (generatedMenu.size() > 0) {
				PluginMenu menu = new PluginMenu();
				menu.name = entry.getKey();
				menu.items = generatedMenu;
				menus.add(menu);
			}
		}
		return menus;

	}

	/**
	 * Adds a menu item to the list of menu
	 * 
	 * @param item
	 *            add the item to the menu list
	 */
	public void add(String category, MenuItem item) {
		log.debug("Add menu {} -> {}", item.nameKey, item.link);
		Set<MenuItem> subitems = items.get(category);
		if (subitems == null) {
			subitems = new HashSet<MenuItem>();
		}
		subitems.add(item);
		items.put(category, subitems);
	}

}
