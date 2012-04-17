/**
 * 
 */
package org.kernely.core.template;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.helpers.I18n;
import org.kernely.core.template.helpers.SobaI18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soba.context.Context;
import soba.node.Template;

import com.google.inject.Inject;

/**
 * @author g.breton
 *
 */
public class SobaTemplateRenderer {

	private static Logger log = LoggerFactory.getLogger(TemplateRenderer.class);
	
	@Inject
	private soba.javaops.SobaEngineFacade engine;
	
	@Inject
	private UserService userService;
	@Inject
	private PluginsLoader pluginsLoader;
	
	@Inject
	private SobaI18n i18n;

	
	
	/**
	 * Load a template base on the filepath
	 * @param filePath the filepath to load
	 * @return the loaded template
	 */
	public void render(String filePath, Writer writer, Map<String,Object> bindings){
		engine.registerExtension(i18n);
		if (filePath == null ) {
			log.error("Cannot load a null file path");
			throw new IllegalArgumentException("Cannot load the template");
		}
		else{
			engine.render(filePath, writer, enhanceBinding(bindings));
		}
	}
	/**
	 * Enhanced the binding, to add constants
	 * 
	 * @param binding
	 *            the bind
	 * @return the binding enhanced
	 */
	private Map<String, Object> enhanceBinding(Map<String, Object> binding) {
		ArrayList<Menu> menus = new ArrayList<Menu>();
		for (AbstractPlugin plugin : pluginsLoader.getPlugins()) {
			if (plugin.getPath() != null){
				List<String> path = plugin.getPath();
				int i=0;
				for (String pPath : path){
					if (pPath != null) {
							menus.add(new Menu(plugin.getName().get(i), pPath));
							i++;
					}
				}
			}
		}
		binding.put("menu", menus);
		if (userService.currentUserIsAdministrator()) {
			binding.put("admin", true);
		} else {
			binding.put("admin", false);
		}
		Subject subject = SecurityUtils.getSubject();
		if (subject.getPrincipal() != null) {

			binding.put("currentUser", subject.getPrincipal().toString());
		}
		/*String lang = configuration.getString("locale.lang");
		String country = configuration.getString("locale.country");
		binding.put("i18n", new I18n(new Locale(lang,country)));*/
		return binding;
	}
	

}
