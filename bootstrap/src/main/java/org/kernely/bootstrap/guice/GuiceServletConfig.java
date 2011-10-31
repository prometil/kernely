package org.kernely.bootstrap.guice;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.kernely.bootstrap.MediaServlet;
import org.kernely.bootstrap.shiro.SimpleShiroFilter;
import org.kernely.bootstrap.shiro.SimpleShiroRealm;
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
				bind(Realm.class).to(SimpleAccountRealm.class).in(Singleton.class);

				// Bind all path with shiro filter
				filter("/*").through(SimpleShiroFilter.class);

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
			public WebSecurityManager securityManager(Realm realm) {
				log.debug("Create security manager");
				SimpleShiroRealm r = new SimpleShiroRealm();
				CredentialsMatcher customMatcher = new SimpleCredentialsMatcher();
				r.setCredentialsMatcher(customMatcher);
				return new DefaultWebSecurityManager(r);
			}

		});

		list.add(new ServletModule());
		return Guice.createInjector(list);
	}
}
