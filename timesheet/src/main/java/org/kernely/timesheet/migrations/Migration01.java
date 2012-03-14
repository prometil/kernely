package org.kernely.timesheet.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.migrations.migrator.Command;
import org.kernely.core.migrations.migrator.CreateTable;
import org.kernely.core.migrations.migrator.DataBaseConstants;
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
		timesheet.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		timesheet.column("beginDate", DataBaseConstants.DATE);
		timesheet.column("endDate", DataBaseConstants.DATE);
		timesheet.column("status", DataBaseConstants.INT);
		timesheet.column("feesStatus", DataBaseConstants.INT);
		timesheet.column("user_id", DataBaseConstants.LONG_NOT_NULL);
		RawSql userForeignKey= new RawSql("ALTER TABLE kernely_timesheet ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");

		commands.add(timesheet);
		commands.add(userForeignKey);
		
		// Table time sheet details
		CreateTable timeSheetDetails = CreateTable.name("kernely_timesheet_details");
		timeSheetDetails.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		timeSheetDetails.column("day", DataBaseConstants.DATE);
		timeSheetDetails.column("timesheet", DataBaseConstants.LONG_NOT_NULL);
		RawSql timeSheetForeignKey= new RawSql("ALTER TABLE kernely_timesheet_details ADD CONSTRAINT fk_timesheet_id FOREIGN KEY (timesheet) REFERENCES kernely_timesheet (id)");
				
		commands.add(timeSheetDetails);
		commands.add(timeSheetForeignKey);

		// Table expense type
		CreateTable expenseType = CreateTable.name("kernely_expense_type");
		expenseType.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		expenseType.column("name", DataBaseConstants.VARCHAR_50);
		expenseType.column("direct", DataBaseConstants.BOOLEAN_DEFAULT_TRUE);
		expenseType.column("ratio", DataBaseConstants.FLOAT4);
		
		commands.add(expenseType);
		
		return commands;
	}
}
