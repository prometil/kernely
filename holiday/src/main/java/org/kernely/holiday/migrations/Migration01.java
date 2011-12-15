 package org.kernely.holiday.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.migrations.migrator.Command;
import org.kernely.core.migrations.migrator.CreateTable;
import org.kernely.core.migrations.migrator.Migration;

public class Migration01 extends Migration {
	public Migration01() {
		super("0.1");
	}

	@Override
	public List<Command> getList() {
		ArrayList<Command> commands = new ArrayList<Command>();
		CreateTable holiday = CreateTable.name("kernely_holiday");
		holiday.column("id", "int primary key");
		holiday.column("type", "varchar(20)");
		holiday.column("frequency", "int");
		holiday.column("unity","varchar(20)");
		
		commands.add(holiday);
		return commands;
	}
}
