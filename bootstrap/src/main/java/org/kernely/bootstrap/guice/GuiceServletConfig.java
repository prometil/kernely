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
package org.kernely.bootstrap.guice;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.kernely.bootstrap.MediaServlet;
import org.kernely.bootstrap.shiro.KernelyRealm;
import org.kernely.bootstrap.shiro.KernelyShiroFilter;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.resources.AbstractController;
import org.kernely.core.template.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceServletConfig extends GuiceServletContextListener {

	public static final Logger log = LoggerFactory.getLogger(TemplateRenderer.class);
	private List<? extends AbstractPlugin> plugins;

	public GuiceServletConfig(List<? extends AbstractPlugin> plugins) {
		this.plugins = plugins;
	}

	@Override
	protected Injector getInjector() {
		List<Module> list = new ArrayList<Module>();
		
		for (AbstractPlugin plugin : plugins) {
			Module module = plugin.getModule();
			if (module != null) {
				list.add(module);
			}
			
		}
		
		list.add(new JerseyServletModule() {
			@Override
			protected void configureServlets() {
				final CombinedConfiguration combinedConfiguration = new CombinedConfiguration();
				
				// Bind all Jersey resources detected in plugins
				for (AbstractPlugin plugin : plugins) {
					for (Class<? extends AbstractController> controllerClass : plugin.getControllers()) {
						log.debug("Register controller {}", controllerClass);
						bind(controllerClass);
					}
					
					String filepath = plugin.getConfigurationFilepath();
					if (filepath != null) {
						try {
							AbstractConfiguration configuration = new XMLConfiguration(filepath);
							log.info("Found configuration file {} for plugin {}", filepath, plugin.getName());
							combinedConfiguration.addConfiguration(configuration);
						} catch (ConfigurationException e) {
							log.error("Cannot find configuration file {} for plugin {}", filepath, plugin.getName());
						}

					}
					
				}
				bind(AbstractConfiguration.class).toInstance(combinedConfiguration);
				

				/*bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
				bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

				// Allows annotations with Shiro in Jersey resources
				MethodInterceptor interceptor = new AopAllianceAnnotationsAuthorizingMethodInterceptor();
				bindInterceptor(any(), annotatedWith(RequiresRoles.class), interceptor);
				bindInterceptor(any(), annotatedWith(RequiresPermissions.class), interceptor);
				bindInterceptor(any(), annotatedWith(RequiresAuthentication.class), interceptor);
				bindInterceptor(any(), annotatedWith(RequiresUser.class), interceptor);
				bindInterceptor(any(), annotatedWith(RequiresGuest.class), interceptor);*/

				
				//TODO understand this
				//bind(Realm.class).to(SimpleAccountRealm.class).in(Singleton.class);
				bind(Realm.class).to(KernelyRealm.class).in(Singleton.class);

				// Bind all path with shiro filter
				filter("/*").through(KernelyShiroFilter.class);

				// Allows to retrieve resources .js, .css, .png
				bind(DefaultServlet.class).in(Singleton.class);
				bind(MediaServlet.class).in(Singleton.class);
				
				serve("*.js").with(MediaServlet.class);
				serve("*.css").with(MediaServlet.class);
				serve("*.png").with(MediaServlet.class);
				serve("*.jpg").with(MediaServlet.class);
				serve("/*").with(GuiceContainer.class);
			}

			@SuppressWarnings("unused")
			@Provides
			@Singleton
			public WebSecurityManager securityManager(KernelyRealm realm) {
				log.debug("Create security manager");
				//Configure encrypting password matcher
				CredentialsMatcher customMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
				realm.setCredentialsMatcher(customMatcher);
				return new DefaultWebSecurityManager(realm);
			}
			
			

		});

		list.add(new ServletModule());
		return Guice.createInjector(list);
	}
}
