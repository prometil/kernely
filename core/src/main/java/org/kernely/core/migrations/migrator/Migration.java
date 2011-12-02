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
package org.kernely.core.migrations.migrator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author g.breton
 * 
 */
public abstract class Migration implements Comparable<Migration> {
	
	private static Logger log = LoggerFactory.getLogger(Migration.class);

	private Version version;

	/**
	 * The constructor
	 * 
	 * @param pPluginName
	 * @param pVersion
	 */
	public Migration(String pVersion) {
		version = new Version(pVersion);
	}

	/**
	 * Apply the migration
	 * 
	 * @param conn
	 *            the Jdbc connection on which migration wil be applied
	 * @throws SQLException
	 */
	public boolean apply(Connection conn) throws SQLException {
		conn.setAutoCommit(false);
		for (Command command : getList()) {
			try {
				command.execute(conn);
				conn.commit();
				return true;
			} catch (SQLException e) {
				log.error("Cannot execute command",e);
				conn.rollback();
				break;
				
			}
			finally{
				conn.setAutoCommit(true);
			}
		}
		
		
		return false;
	}

	public abstract List<Command> getList();

	/**
	 * Returns the migration version
	 * 
	 * @return the migration version
	 */
	public Version getVersion() {
		return version;
	}

	@Override
	public int compareTo(Migration other) {
		return getVersion().compareTo(other.getVersion());
	}

}
