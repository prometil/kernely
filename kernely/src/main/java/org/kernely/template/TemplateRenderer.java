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
package org.kernely.template;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.codehaus.groovy.control.CompilationFailedException;
import org.kernely.core.service.UserService;
import org.kernely.plugin.AbstractPlugin;
import org.kernely.plugin.PluginManager;
import org.kernely.resource.ResourceLocator;
import org.kernely.template.helpers.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * The template engine base on a groovy
 * 
 * 
 */
public class TemplateRenderer {

	@Inject
	private PluginManager pluginsLoader;

	@Inject
	private SimpleTemplateEngine engine;

	@Inject
	private UserService userService;

	@Inject
	private ResourceLocator resourceLocator;
	
	@Inject
	private AbstractConfiguration configuration;

	public static final String ADMIN_LAYOUT = "/templates/gsp/admin.gsp";

	private static Logger log = LoggerFactory.getLogger(TemplateRenderer.class);

	/**
	 * The template renderer
	 */
	public TemplateRenderer() {

	}

	/**
	 * Render a template
	 * 
	 * @param m
	 * @param URLFile
	 * @return
	 */
	public KernelyTemplate create(String urlFile) {
		if (urlFile == null) {
			throw new IllegalArgumentException("Cannot load the template");
		}
		log.trace("Engine {}, Url {}", engine, urlFile);
		return new KernelyTemplate(urlFile, engine);

	}

	/**
	 * The template handler
	 * 
	 */
	public class KernelyTemplate {

		// the template
		private Template template;

		// the binding files
		private Map<String, Object> binding;

		// the css files
		private List<String> cssFiles;

		// the body
		private String body;

		// the layout
		private String otherLayout = null;

		private boolean withLayout = true;

		private boolean forMail = false;
		
		private SimpleTemplateEngine engine;

		/**
		 * 
		 * @param pTemplate
		 * @param pEngine
		 */
		public KernelyTemplate(String pTemplate, SimpleTemplateEngine pEngine) {
			cssFiles = new ArrayList<String>();
			binding = new HashMap<String, Object>();
			engine = pEngine;
			URL resource;

			try {
				resource = resourceLocator.getResource(pTemplate);
				if (resource == null) {
					log.error("Cannot find template {}", pTemplate);
				}
				try {
					template = engine.createTemplate(resource);
				} catch (CompilationFailedException e) {
					log.error("Compilation error on {}", pTemplate, e);
				} catch (ClassNotFoundException e) {
					log.error("Compilation error on {}", pTemplate, e);
				} catch (IOException e) {
					log.error("Cannot find file {}", pTemplate);
				}
			} catch (MalformedURLException e1) {
				log.error("Cannot get the template {}", pTemplate);
			}

		}

		/**
		 * Adds a variable and its definition to the template
		 * 
		 * @param key
		 *            the name of the variable
		 * @param value
		 *            the value of the variable
		 * @return the template builder
		 */
		public KernelyTemplate with(String key, Object value) {
			binding.put(key, value);
			return this;
		}

		/**
		 * Add a set of values to the bindings.
		 * 
		 * @param values
		 *            the value
		 * @return the template renderer.
		 */
		public KernelyTemplate with(Map<String, Object> values) {
			binding.putAll(values);
			return this;
		}

		/**
		 * Adds a css file to the renderering
		 * 
		 * @param file
		 *            the css file
		 * @return the template builder
		 */
		public KernelyTemplate addCss(String file) {
			cssFiles.add(file);
			return this;
		}

		/**
		 * Remove the layout system from the renderered template
		 * 
		 * @return the template builder
		 */
		public KernelyTemplate withoutLayout() {
			withLayout = false;
			return this;
		}

		/**
		 * Insert the page in this layout
		 * 
		 * @param the
		 *            layout to use
		 * @return the template builder
		 */
		public KernelyTemplate withLayout(String otherLayout) {
			this.otherLayout = otherLayout;
			return this;
		}
		
		/**
		 * A Template for mails don't care about connected users and don't have a layout.
		 * @return the template builder
		 */
		public KernelyTemplate forMail() {
			this.forMail = true;
			this.withLayout = false;
			return this;
		}
		

		/**
		 * Render the page with the default layout. If you want to insert the
		 * page in the admin layout for example, use the appropriate
		 * TemplateRenderer constant. Note that the layout must have a template
		 * variable called "extension", where the page will be included.
		 * 
		 * @return The htmlbinding.put("content", body); content.
		 */
		public String render() {
			URL kernelyLayout = TemplateRenderer.class.getResource("/templates/gsp/layout.gsp");
			if (! forMail){
				binding = enhanceBinding((HashMap<String, Object>) binding);
			}
			if (body == null) {
				body = template.make(binding).toString();
			}
			if (withLayout) {
				binding.put("content", body);
				try {
					if (otherLayout != null) {
						return create(otherLayout).with(binding).with("extension", body).render();
					}
					return engine.createTemplate(kernelyLayout).make(binding).toString();
				} catch (CompilationFailedException e) {
					log.error("Compilation error on {}", kernelyLayout, e);
				} catch (ClassNotFoundException e) {
					log.error("Compilation error on {}", kernelyLayout, e);
				} catch (IOException e) {
					log.error("Cannot find file {}", kernelyLayout, e);
				}
				return "";
			} else {
				return body;
			}

		}

		/**
		 * Enhanced the binding, to add constants
		 * 
		 * @param binding
		 *            the bind
		 * @return the binding enhanced
		 */
		private Map<String, Object> enhanceBinding(HashMap<String, Object> binding) {
			Map<String, String> menu = new HashMap<String, String>();
			for (AbstractPlugin plugin : pluginsLoader.getPlugins()) {
				if (plugin.getPath() != null){
					List<String> path = plugin.getPath();
					int i=0;
					for (String pPath : path){
						if (pPath != null) {
								menu.put(plugin.getMenus().get(i), pPath);
								i++;
						}
					}
				}
			}
			binding.put("menu", menu);
			if (userService.currentUserIsAdministrator()) {
				binding.put("admin", "Administration");
			} else {
				binding.put("admin", "");
			}
			Subject subject = SecurityUtils.getSubject();
			if (subject.getPrincipal() != null) {

				binding.put("currentUser", subject.getPrincipal().toString());
			}
			if (!binding.containsKey("css")) {
				binding.put("css", cssFiles);
			}
			String lang = configuration.getString("locale.lang");
			String country = configuration.getString("locale.country");

			binding.put("i18n", new I18n(new Locale(lang,country)));
			return binding;
		}
	}
}
