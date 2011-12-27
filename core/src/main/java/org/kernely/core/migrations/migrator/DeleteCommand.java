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

/**
 * A delete sql command
 * 
 * @author g.breton
 * 
 */
public class DeleteCommand extends Command {

	// the table name
	protected String name;

	// the delete where clause
	protected String where;

	/**
	 * Construcor
	 * 
	 * @param tableName
	 *            the table name to delete from
	 */
	private DeleteCommand(String tableName) {
		name = tableName;

	}

	/**
	 * Create a new delete command for the given table name
	 * 
	 * @param name
	 *            the name of the table to delete from
	 * @return a new delete command
	 */
	public static DeleteCommand from(String name) {
		return new DeleteCommand(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kernely.core.migrations.migrator.Command#build()
	 */
	@Override
	protected String build() {
		StringBuilder b = new StringBuilder();
		b.append("DELETE FROM ");
		b.append(name);
		if (where != null && !where.equals("")) {
			b.append("WHERE ");
			b.append(where);
			b.append(";");
		}
		return b.toString();
	}
}
