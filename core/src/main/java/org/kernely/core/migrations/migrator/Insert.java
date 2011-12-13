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

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Joiner;

/**
 * @author g.breton
 * 
 */
public class Insert extends Command {

	// the name of the table in which data will be inserted
	private String name;

	private Map<String, Object> values;

	/**
	 * Constructs a new insert command for a given table name
	 * 
	 * @param tableName
	 *            the given table name
	 */
	private Insert(String tableName) {
		name = tableName;
		values = new HashMap<String, Object>();
	}

	/**
	 * Constructs a new Insert command for a new table name
	 * 
	 * @param tableName
	 *            the table name in which the insert will be perform
	 * @return a new Insert.
	 */
	public static Insert into(String tableName) {
		return new Insert(tableName);
	}

	/**
	 * Set a column value for the insert
	 * 
	 * @param name
	 *            the name of the column
	 * @param value
	 *            the value to give to the column
	 * @return the insert value
	 */
	public Insert set(String name, Object value) {
		if (value instanceof String) {
			values.put(name, "'" + value + "'");
		} else {
			values.put(name, value);
		}
		return this;
	}

	/**
	 * Generates the query
	 */
	@Override
	public String build() {
		StringBuilder b = new StringBuilder();
		b.append("INSERT INTO ");
		b.append(name);
		b.append("(");
		b.append(Joiner.on(",").join(values.keySet()));
		b.append(")");
		b.append(" VALUES ");
		b.append("(");
		b.append(Joiner.on(",").join(values.values()));
		b.append(");");
		return b.toString();

	}

}
