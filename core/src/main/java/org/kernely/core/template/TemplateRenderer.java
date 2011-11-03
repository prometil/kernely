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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
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
		if(URLFile == null ){
			throw new IllegalArgumentException("Cannot load the template");
		}
		log.trace("Engine {}, Url {}",engine, URLFile);
		return new TemplateBuilder(URLFile, engine);

	}

	public class TemplateBuilder {

		//the template
		private Template template;

		//the binding files
		HashMap<String, Object> binding;
		
		//the css files
		List<String> cssFiles;
		
		boolean withLayout =true;
		

		private SimpleTemplateEngine engine;

		public TemplateBuilder(String pTemplate, SimpleTemplateEngine pEngine) {
			cssFiles = new ArrayList<String>();
			binding = new HashMap<String, Object>();
			engine = pEngine;
			URL resource = TemplateRenderer.class.getResource(pTemplate);
			if(resource == null){
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
		}

		public TemplateBuilder with(String key, Object value) {
			binding.put(key, value);
			return this;
		}
		
		public TemplateBuilder addCss(String file){
			cssFiles.add(file);
			return this;
		}
		
		public TemplateBuilder withoutLayout(){
			withLayout = false;
			return this;
		}
		

		public String render() {
			URL layout = TemplateRenderer.class.getResource("/templates/gsp/layout.gsp");
			String body = template.make(binding).toString();
			if(withLayout){
				HashMap<String, Object> layoutBinding = new HashMap<String, Object>();
				HashMap<String, String> menu = new HashMap<String, String>();
				for (AbstractPlugin plugin : pluginsLoader.getPlugins()) {
					menu.put(plugin.getName(), plugin.getPath());
				}
				
				//===========TODO : Create an extension point ================//
				menu.put(SecurityUtils.getSubject().getPrincipal().toString(),"user/profile");
				//============================================================//
				
				layoutBinding.put("content", body);
				layoutBinding.put("menu", menu);
				layoutBinding.put("css", cssFiles);
				try {
					return engine.createTemplate(layout).make(layoutBinding).toString();
				} catch (CompilationFailedException e) {
					log.error("Compilation error on {}", layout, e);
				} catch (ClassNotFoundException e) {
					log.error("Compilation error on {}", layout, e);
				} catch (IOException e) {
					log.error("Cannot find file {}", layout, e);
				}
				return "";
			}
			else{
				return body;
			}
			
		}
	}
}
