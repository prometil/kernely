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
package org.kernely.core.migrations.migrator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author g.breton
 * 
 */
public abstract class Command {
	private static Logger log = LoggerFactory.getLogger(Migration.class);

	/**
	 * Execute a command on a given jdbc connection
	 * 
	 * @param conn
	 *            the connection
	 * @return true if the command has been sucessfully updated else false.
	 * @throws SQLException
	 */
	public boolean execute(Connection conn) throws SQLException {
		String query = build();
		log.debug("{}", query);
		Statement createStatement = conn.createStatement();
		createStatement.execute(query);
		createStatement.close();
		return true;

	}

	/**
	 * Generates the query
	 * 
	 * @return generates the query
	 */
	protected abstract String build();
}
