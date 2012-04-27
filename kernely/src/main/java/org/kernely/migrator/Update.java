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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

/**
 * Update command
 * 
 */
public class Update extends Command {

	// the name of the table to update
	private String name;

	// the where condition
	private String where;

	// the column
	private List<String> columns;

	/**
	 * Creates an update command for the table name
	 * 
	 * @param tableName
	 *            the table name
	 */
	private Update(String tableName) {
		name = tableName;
		columns = new ArrayList<String>();
	}

	/**
	 * 
	 * @param tableName
	 * @return
	 */
	public static Update table(String tableName) {
		return new Update(tableName);
	}

	/**
	 * The method set the a column value
	 * 
	 * @param columnName
	 *            the column to update
	 * @param columnValue
	 *            the value to set
	 * @return the update
	 */
	public Update set(String columnName, Object columnValue) {
		if (columnValue instanceof String) {
			columns.add(name + " '" + columnValue + "'");
		} else {
			columns.add(columnName + " " + columnValue);
		}
		return this;
	}

	/**
	 * The method set the where condition
	 * 
	 * @param pWhere
	 *            the condition
	 * @return the update
	 */
	public Update where(String pWhere) {
		where = pWhere;
		return this;
	}

	/**
	 * Build an update request
	 */
	@Override
	protected String build() {
		StringBuilder b = new StringBuilder();
		b.append("UPDATE ");
		b.append(" SET ");
		b.append(Joiner.on(",").join(columns));
		b.append(" WHERE ");
		b.append(where);
		return b.toString();

	}

}
