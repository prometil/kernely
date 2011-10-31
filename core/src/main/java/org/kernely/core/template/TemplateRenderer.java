package org.kernely.core.template;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.groovy.control.CompilationFailedException;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * The template engine base on a groovy
 * 
 * @author g.breton
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
