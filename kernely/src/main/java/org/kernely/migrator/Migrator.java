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
package org.kernely.migrator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.configuration.AbstractConfiguration;
import org.kernely.plugin.AbstractPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The migrator.
 */
public class Migrator {

	private static Logger log = LoggerFactory.getLogger(Migrator.class);

	private AbstractConfiguration configuration;

	private List<AbstractPlugin> plugins;

	/**
	 * Constructor
	 * 
	 * @param pConfiguration
	 * @param pluginList
	 */
	public Migrator(AbstractConfiguration pConfiguration, List<AbstractPlugin> pluginList) {
		configuration = pConfiguration;
		plugins = pluginList;
	}

	/**
	 * Returns a list of sorted version
	 * 
	 * @param connection
	 * @param name
	 * @return
	 */
	public SortedSet<Version> getCurrentSchemaVersion(Connection connection, String name) {

		SortedSet<Version> versions = new TreeSet<Version>();
		String request = "SELECT version from kernely_schema_version where plugin = ?";
		PreparedStatement createStatement;
		try {
			createStatement = connection.prepareStatement(request);
			createStatement.setString(1, name);
			ResultSet executeQuery = createStatement.executeQuery();
			while (executeQuery.next()) {
				String version = executeQuery.getString(1);
				versions.add(new Version(version));
			}
			executeQuery.close();
		} catch (SQLException e) {
			log.error("Cannot get current schema migration", e);
		}
		return versions;
	}

	/**
	 * Add a version to the kernely_schema_version plugin
	 * 
	 * @param connection
	 * @param version
	 * @param name
	 */
	public void addVersion(Connection connection, Version version, String name) {
		String request = "INSERT INTO kernely_schema_version (version, plugin) VALUES (?, ?)";
		PreparedStatement createStatement;
		try {
			createStatement = connection.prepareStatement(request);
			createStatement.setString(1, version.getVersion());
			createStatement.setString(2, name);
			createStatement.execute();
		} catch (SQLException e) {
			log.error("Cannot add version {} for plugin ", new Object[] { version, name }, e);
		}

	}

	/**
	 * Migrate the data base
	 */
	public void migrate() {
		Connection conn = null;
		Properties connectionProps = new Properties();

		String hibernateDriverName = configuration.getString("hibernate.connection.driver_class");
		if (hibernateDriverName != null) {
			log.debug("Hibernate driver : {}", hibernateDriverName);
			try {
				Class.forName(hibernateDriverName);
				connectionProps.put("user", configuration.getString("hibernate.connection.username"));
				connectionProps.put("password", configuration.getString("hibernate.connection.password"));
				conn = DriverManager.getConnection(configuration.getString("hibernate.connection.url"), connectionProps);
			} catch (ClassNotFoundException e1) {
				log.error("Hibernate driver class is not defined");
				System.exit(0);
			} catch (SQLException e) {
				log.error("Cannot connect to postgresql");
				System.exit(0);
			}
			
			try {
				// initialise the database
				DatabaseMetaData metaData = conn.getMetaData();
				ResultSet rs = metaData.getTables(null, null, "kernely_schema_version", null);
				if (!rs.next()) {
					log.info("Initialising database");
					CreateTable name = CreateTable.name("kernely_schema_version");
					name.column("version", "varchar(20)");
					name.column("plugin", "varchar(50)");
					name.execute(conn);
				}
				rs.close();

				for (AbstractPlugin plugin : plugins) {
					SortedSet<Version> versions = getCurrentSchemaVersion(conn, plugin.getName());
					if (versions.size() > 0) {
						Version currentVersion = versions.last();
						log.debug("plugin [{}] is in version [{}]", plugin.getName(), currentVersion);
						for (Migration migration : plugin.getMigrations()) {
							int compareTo = currentVersion.compareTo(migration.getVersion());
							if (compareTo < 0) {
								log.info("Migrating [{}] form [{}] to [{}]", new Object[] { plugin.getName(), currentVersion, migration.getVersion() });
								if (migration.apply(conn)) {

									addVersion(conn, migration.getVersion(), plugin.getName());
								} else {
									log.error("Cannot apply migration {} due to previous errors", migration.getVersion());
									return;
								}
							}
						}
					} else {
						for (Migration migration : plugin.getMigrations()) {
							log.info("Applying version {} for plugin {}", migration.getVersion(), plugin.getName());
							if (migration.apply(conn)) {
								addVersion(conn, migration.getVersion(), plugin.getName());
							} else {
								log.error("Cannot apply migration {} due to previous errors", migration.getVersion());
								return;
							}
						}
					}

				}
			} catch (SQLException e) {
				log.error("SQL error ", e);
			} finally {
				if (conn != null) {
					try {
						conn.rollback();
						conn.close();
					} catch (SQLException e) {
						// nothing to do
					}
				}
			}
		} else {
			log.error("Hibernate driver class is not defined");
			System.exit(0);
		}

	}
}
