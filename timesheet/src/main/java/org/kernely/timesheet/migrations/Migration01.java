package org.kernely.timesheet.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.migrations.migrator.Command;
import org.kernely.core.migrations.migrator.CreateTable;
import org.kernely.core.migrations.migrator.Migration;
import org.kernely.core.migrations.migrator.RawSql;

/**
 * Time sheet migration script
 * 
 */
public class Migration01 extends Migration {
	/**
	 * constructor 
	 */
	public Migration01() {
		super("0.1");
	}

	/**
	 * migration script
	 * @return the list of command
	 */
	@Override
	public List<Command> getList() {
		ArrayList<Command> commands = new ArrayList<Command>();
		
		// Table time sheet
		CreateTable timesheet = CreateTable.name("kernely_timesheet");
		timesheet.column("id", "bigint primary key");
		timesheet.column("beginDate", "timestamp");
		timesheet.column("endDate", "timestamp");
		timesheet.column("status", "int");
		timesheet.column("feesStatus", "int");
		timesheet.column("user_id", "bigint");
		RawSql userForeignKey= new RawSql("ALTER TABLE kernely_timesheet ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");

		commands.add(timesheet);
		commands.add(userForeignKey);
		
		// Table time sheet details
		CreateTable timeSheetDetails = CreateTable.name("kernely_timesheet_details");
		timeSheetDetails.column("id", "bigint primary key");
		timeSheetDetails.column("day", "timestamp");
		timeSheetDetails.column("amount", "float");
		timeSheetDetails.column("timesheet", "bigint");
		RawSql timeSheetForeignKey= new RawSql("ALTER TABLE kernely_timesheet_details ADD CONSTRAINT fk_timesheet_id FOREIGN KEY (timesheet) REFERENCES kernely_timesheet (id)");
		
		commands.add(timeSheetDetails);
		commands.add(timeSheetForeignKey);

		return commands;
	}
}
