package org.kernely.bootstrap.shiro;

import java.util.Map;

import org.apache.shiro.config.Ini;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.IniShiroFilter;
import org.kernely.bootstrap.error.KernelyErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class SimpleShiroFilter extends IniShiroFilter {
	private static final Logger log = LoggerFactory.getLogger(SimpleShiroFilter.class);

    static class SecurityManagerFactory extends WebIniSecurityManagerFactory {

      private final WebSecurityManager securityManager;

      public SecurityManagerFactory(WebSecurityManager securityManager) {
        this.securityManager = securityManager;
      }

      public SecurityManagerFactory(WebSecurityManager securityManager, Ini ini) {
        super(ini);
        this.securityManager = securityManager;
      }

      @Override
      protected WebSecurityManager createDefaultInstance() {
        return securityManager;
      }
    }

    private final Provider<WebSecurityManager> securityManager;

    @Inject
    SimpleShiroFilter(Provider<WebSecurityManager> securityManager) {
      super();
      this.securityManager = securityManager;
    }


    protected Map<String, ?> applySecurityManager(Ini ini) {
      SecurityManagerFactory factory;
      if (ini == null || ini.isEmpty()) {
        factory = new SecurityManagerFactory(securityManager.get());
      } else {
    	  log.debug("Ini found!");
        factory = new SecurityManagerFactory(securityManager.get(), ini);
      }
      setSecurityManager((WebSecurityManager) factory.getInstance());
      return factory.getBeans();
    }
  }
