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
package org.kernely.core.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.migrations.migrator.Command;
import org.kernely.core.migrations.migrator.Migration;

/**
 * Core plugin migration script for version 0.1
 * 
 * @author g.breton
 * 
 */
public class Migration01 extends Migration {

	public Migration01() {
		super("0.1");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kernely.core.migrations.migrator.Migration#getList()
	 */
	@Override
	public List<Command> getList() {
		ArrayList<Command> commands = new ArrayList<Command>();
		/*CreateTable table = CreateTable.name("kernely_user");
		table.column("id", "int primary key");
		table.column("username", "varchar(30)");
		table.column("password", "varchar(80)");
		table.column("salt", "varchar(300)");
		table.column("locked", "boolean default false");
		table.column("fk_manager", "int");
		
		
		

		Insert insertBoby = Insert.into("kernely_user");
		insertBoby.set("id", "1");
		insertBoby.set("username", "bobby");
		insertBoby.set("password", "2ty4LmflO9cRBKi1liWj3WvSrmtf2EnL67SoTa0bNuM=");
		insertBoby.set("salt", "gNc1mOUoQxGmCzoV2W7YP3CJj9oDML/SfABujWDrBmvx9xN5if4Y0jMckDNK1we/kMRGR75uQggRgr5dKgnd6ZGIVxG0Zr3EiYxiXBU9aDyZkYvBqy9ffwZ9JScQ5Wke1NarH/lZevTgOUMaLMYVV7q/QvzH42rYek3mF0F1ykM=");
		insertBoby.set("locked", "false");

		Insert insertJohn = Insert.into("kernely_user");
		insertJohn.set("id", "2");
		insertJohn.set("username", "john");
		insertJohn.set("password", "vAT9Kr/2bSbWoxFj3iinD783xrTez+lE2G/HSGaDzVk=");
		insertJohn.set("salt", "8EiKXghisVxqZ74Nwen+/5NanikCV0DRB9J31tC0jWGip79G1ZCrkwsFYOkD/aw1ggYA8r/nsYHnWXofR7x0nFU8CK87aiZ3BzXyzH4AEu9pzV/YWfWhq1d0W3gAB36gHsVQ6mZubI5UYforzdATLAAGOlQAa4BXF7Cwxs8wuf0=");
		insertJohn.set("locked", "false");

		commands.add(table);
		commands.add(insertBoby);
		commands.add(insertJohn);*/
		return commands;

	}

}
