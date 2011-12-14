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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

/**
 * @author g.breton
 * 
 */
public class CreateTable extends Command {

	// the name of the table
	String name;

	// the column
	List<String> columns;

	
	/**
	 * Create a new command with the table name.
	 * @param tableName the table name.
	 */
	public CreateTable(String tableName) {
		name = tableName;
		columns = new ArrayList<String>();
	}

	/**
	 * add a column to the command
	 * 
	 * @param pName
	 *            the name of the column
	 * @param attributes
	 *            the attributes of the commandes (type, size etc)
	 * @return
	 */
	public CreateTable column(String pName, String attributes) {
		columns.add(pName + " " + attributes);
		return this;
	}

	/**
	 * 
	 * @param tableName
	 *            the table name
	 * @return the table creation
	 */
	public static CreateTable name(String tableName) {
		CreateTable createTable = new CreateTable(tableName);
		return createTable;

	}

	@Override
	public String build() {
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE");
		b.append(" ");
		b.append(name);
		b.append("(");
		b.append(Joiner.on(",").join(columns));
		b.append(");");
		return b.toString();

	}
}
