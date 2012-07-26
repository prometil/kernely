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
package org.kernely.core.common;


import java.util.Properties;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.kernely.plugin.PluginManager;
import org.kernely.service.mail.Mailer;
import org.kernely.service.mail.builder.MailBuilder;
import org.mockito.Mockito;

import com.google.common.eventbus.EventBus;
import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.persist.jpa.JpaPersistModule;

/**
 * @author g.breton
 *
 */
public class GuiceJunitModule extends AbstractModule {

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void configure() {
		install(new GuiceBerryModule());
		//creates the hibernate util
		Properties properties = new Properties();
		properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		properties.put("hibernate.connection.url", "jdbc:hsqldb:mem:aname");
		properties.put("hibernate.connection.username", "sa");
		properties.put("hibernate.connection.password",  "");
		properties.put("hibernate.connection.pool_size", "10");
		properties.put("show_sql", "true");
		properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		properties.put("hibernate.hbm2ddl.auto",  "update");
		
		install(new JpaPersistModule("kernelyUnit").properties(properties));
		bind(Initializer.class);
		bind(PluginManager.class).toInstance(PluginManager.getInstance());
		bind(PasswordService.class).to(DefaultPasswordService.class);
		
		//creates a mail moker
		Mailer mailerMock = Mockito.mock(Mailer.class);
		MailBuilder mailBuilderMock = Mockito.mock(MailBuilder.class);
		Mockito.when(mailBuilderMock.cc(Mockito.anyString())).thenReturn(mailBuilderMock);
		Mockito.when(mailBuilderMock.cc(Mockito.anyList())).thenReturn(mailBuilderMock);
		Mockito.when(mailBuilderMock.to(Mockito.anyString())).thenReturn(mailBuilderMock);
		Mockito.when(mailBuilderMock.to(Mockito.anyList())).thenReturn(mailBuilderMock);
		Mockito.when(mailBuilderMock.subject(Mockito.anyString())).thenReturn(mailBuilderMock);
		Mockito.when(mailBuilderMock.with(Mockito.anyString(), Mockito.anyString())).thenReturn(mailBuilderMock);
		Mockito.when(mailBuilderMock.registerMail()).thenReturn(true);
		Mockito.when(mailerMock.create(Mockito.anyString())).thenReturn(mailBuilderMock);

		bind(Mailer.class).toInstance(mailerMock);
		
		bind(EventBus.class);
		
	}
	
	@Provides
	protected AbstractConfiguration getAbstractConfiguration(){
		BaseConfiguration baseConfig = new BaseConfiguration();
		baseConfig.addProperty("locale.dateformat", "MM/dd/yyyy");
		return baseConfig;
	}

}
