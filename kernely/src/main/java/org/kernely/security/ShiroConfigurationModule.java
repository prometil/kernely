package org.kernely.security;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Names;

/**
 * Shiro module configuration
 */
public class ShiroConfigurationModule extends ShiroWebModule {
	
	//logger
	private static Logger log = LoggerFactory
			.getLogger(ShiroConfigurationModule.class);
	/**
	 * Constructor of the module
	 * @param sc
	 */
	@Inject
	public ShiroConfigurationModule(ServletContext sc) {
		super(sc);
	}
	/**
	 * Configure the shiro web.
	 */
	@SuppressWarnings("unchecked")
	protected void configureShiroWeb() {
		log.trace("Configure shiro web module");
		bindRealm().to(KernelyRealm.class);
		bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to(
				"/user/login");
		this.addFilterChain("/favicon.ico", ANON);
		addFilterChain("/css/login.css", ANON);
		addFilterChain("/user/login", AUTHC);
		addFilterChain("/**", USER);
		
		
		
		

	}

}
