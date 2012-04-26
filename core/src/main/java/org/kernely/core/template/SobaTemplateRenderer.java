/**
 * 
 */
package org.kernely.core.template;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginManager;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.helpers.SobaI18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author g.breton
 * 
 */
public class SobaTemplateRenderer {

	private static Logger log = LoggerFactory.getLogger(SobaTemplateRenderer.class);

	@Inject
	private soba.javaops.SobaEngineFacade engine;

	@Inject
	private UserService userService;
	@Inject
	private PluginManager pluginsLoader;
	
	@Inject
	private SobaI18n i18n;
	
	@PostConstruct
	public void configure(){
		engine.registerExtension(i18n);
	}

	/**
	 * Load a template base on the filepath
	 * 
	 * @param filePath
	 *            the filepath to load
	 * @return the loaded template
	 */
	public void render(String filePath, Writer writer, Map<String, Object> bindings) {
		if (filePath == null) {
			log.error("Cannot load a null file path");
			throw new IllegalArgumentException("Cannot load the template");
		} else {
			engine.render(filePath, writer, enhanceBinding(bindings));
		}
	}
	/**
	 * Load a template base on the filepath
	 * 
	 * @param filePath
	 *            the filepath to load
	 * @return the loaded template
	 */
	public String render(String filePath) {
		return render(filePath, new HashMap<String, Object>());
	}

	/**
	 * Load a template base on the filepath
	 * 
	 * @param filePath
	 *            the filepath to load
	 * @param
	 * @return the loaded template
	 */
	public String render(String filePath, Map<String, Object> bindings) {
		StringWriter w = new StringWriter();
		engine.render(filePath, w, enhanceBinding(bindings));
		return w.toString();
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
			if (plugin.getPath() != null) {
				List<String> path = plugin.getPath();
				int i = 0;
				for (String pPath : path) {
					if (pPath != null) {

							menus.add(new Menu(plugin.getMenus().get(i), pPath));
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
			binding.put("currentUser", userService.getUserDetails(userService.getAuthenticatedUserDTO().username));
			binding.put("currentUserLogin", userService.getAuthenticatedUserDTO().username);
		}
		/*
		 * String lang = configuration.getString("locale.lang"); String country
		 * = configuration.getString("locale.country"); binding.put("i18n", new
		 * I18n(new Locale(lang,country)));
		 */
		return binding;
	}

}
