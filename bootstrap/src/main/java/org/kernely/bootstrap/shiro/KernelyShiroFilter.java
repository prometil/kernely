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

package org.kernely.bootstrap.shiro;

import java.util.Map;

import org.apache.shiro.config.Ini;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.IniShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class KernelyShiroFilter extends IniShiroFilter {
	private static Logger log = LoggerFactory.getLogger(KernelyShiroFilter.class);

    static class SecurityManagerFactory extends WebIniSecurityManagerFactory {

      private final WebSecurityManager securityManager;

      /**
       * Constructor with the security manager.
       * @param securityManager The security manager.
       */
      public SecurityManagerFactory(WebSecurityManager securityManager) {
        this.securityManager = securityManager;
      }

      /**
       * Constructor with the security manager and a configuration.
       * @param securityManager The security manager.
       * @param ini The apache ini text.
       */
      public SecurityManagerFactory(WebSecurityManager securityManager, Ini ini) {
        super(ini);
        this.securityManager = securityManager;
      }

      /**
       * Returns the security manager passed in parameters at the {@link #SecurityManagerFactory(WebSecuritymanager) SecurityManagerFactory construction}.
       */
      @Override
      protected WebSecurityManager createDefaultInstance() {
        return securityManager;
      }
    }

    private final Provider<WebSecurityManager> securityManager;

    /**
     * Constructor of the shiro filter, which intercepts requests.
     * @param securityManager A provider of the web security manager.
     */
    @Inject
    KernelyShiroFilter(Provider<WebSecurityManager> securityManager) {
      super();
      this.securityManager = securityManager;
    }

    /**
     * Apply the web security manager from the ini file if its exists.
     * @param ini The ini text.
     */
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
