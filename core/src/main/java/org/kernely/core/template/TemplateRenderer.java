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
package org.kernely.core.template;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
import org.kernely.core.resourceLocator.ResourceLocator;
import org.kernely.core.service.user.UserService;
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
	private PluginsLoader pluginsLoader;

	@Inject
	private SimpleTemplateEngine engine;

	@Inject
	private UserService userService;

	@Inject
	private ResourceLocator resourceLocator;

	public final static String ADMIN_LAYOUT = "/templates/gsp/admin.gsp";

	public static final Logger log = LoggerFactory.getLogger(TemplateRenderer.class);

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
	public TemplateBuilder create(String URLFile) {
		if (URLFile == null) {
			throw new IllegalArgumentException("Cannot load the template");
		}
		log.trace("Engine {}, Url {}", engine, URLFile);
		return new TemplateBuilder(URLFile, engine);

	}

	public class TemplateBuilder {

		// the template
		private Template template;

		// the binding files
		HashMap<String, Object> binding;

		// the css files
		List<String> cssFiles;

		// the body
		String body;

		// the layout
		String otherLayout = null;

		boolean withLayout = true;

		private SimpleTemplateEngine engine;

		public TemplateBuilder(String pTemplate, SimpleTemplateEngine pEngine) {
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
		public TemplateBuilder with(String key, Object value) {
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
		public TemplateBuilder with(Map<String, Object> values) {
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
		public TemplateBuilder addCss(String file) {
			cssFiles.add(file);
			return this;
		}

		/**
		 * Remove the layout system from the renderered template
		 * 
		 * @return the template builder
		 */
		public TemplateBuilder withoutLayout() {
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
		public TemplateBuilder withLayout(String otherLayout) {
			this.otherLayout = otherLayout;
			return this;
		}

		/**
		 * Render the page with the default layout. If you want to insert the page in the admin layout for example, use the appropriate
		 * TemplateRenderer constant. Note that the layout must have a template variable called "extension", where the page will be included.
		 * 
		 * @return The html content.
		 */
		public String render() {
			URL kernelyLayout = TemplateRenderer.class.getResource("/templates/gsp/layout.gsp");
			if (body == null) {
				body = template.make(binding).toString();
			}
			if (withLayout) {
				HashMap<String, Object> layoutBinding = new HashMap<String, Object>();
				HashMap<String, String> menu = new HashMap<String, String>();
				for (AbstractPlugin plugin : pluginsLoader.getPlugins()) {
					String path = plugin.getPath();
					if (path != null) {
						menu.put(plugin.getName(), path);
					}
				}

				if (userService.currentUserIsAdministrator()) {
					layoutBinding.put("admin", "Administration");
				} else {
					layoutBinding.put("admin", "");
				}
				layoutBinding.put("groups", "Groups");
				layoutBinding.put("users", "Users");
				layoutBinding.put("currentUser", SecurityUtils.getSubject().getPrincipal().toString());
				// ============================================================//

				layoutBinding.put("content", body);
				layoutBinding.put("menu", menu);
				layoutBinding.put("css", cssFiles);

				try {
					if (otherLayout != null) {
						return create(otherLayout).with("extension", body).render();
					}

					return engine.createTemplate(kernelyLayout).make(layoutBinding).toString();
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
	}
}
