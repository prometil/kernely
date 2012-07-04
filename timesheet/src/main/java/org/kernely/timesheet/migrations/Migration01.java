package org.kernely.timesheet.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.migrator.Command;
import org.kernely.migrator.CreateTable;
import org.kernely.migrator.DataBaseConstants;
import org.kernely.migrator.Migration;
import org.kernely.migrator.RawSql;

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
		timesheet.column("user_id", DataBaseConstants.LONG_NOT_NULL);
		RawSql userForeignKey= new RawSql("ALTER TABLE kernely_timesheet ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");

		commands.add(timesheet);
		commands.add(userForeignKey);
		
		// Table time sheet days
		CreateTable timeSheetDays = CreateTable.name("kernely_timesheet_day");
		timeSheetDays.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		timeSheetDays.column("day", DataBaseConstants.DATE);
		timeSheetDays.column("status", DataBaseConstants.INT);
		timeSheetDays.column("timesheet_id", DataBaseConstants.LONG_NOT_NULL);
		RawSql timeSheetForeignKey= new RawSql("ALTER TABLE kernely_timesheet_day ADD CONSTRAINT fk_timesheet_id FOREIGN KEY (timesheet_id) REFERENCES kernely_timesheet (id)");
		
		commands.add(timeSheetDays);
		commands.add(timeSheetForeignKey);

		// Association table which links the project to a detail
		CreateTable timeSheetDetailProject = CreateTable.name("kernely_timesheet_detail_project");
		timeSheetDetailProject.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		timeSheetDetailProject.column("timesheet_day_id", DataBaseConstants.LONG);
		timeSheetDetailProject.column("project_id", DataBaseConstants.LONG);
		timeSheetDetailProject.column("amount", DataBaseConstants.FLOAT);
		RawSql timeSheetDayForeignKey= new RawSql("ALTER TABLE kernely_timesheet_detail_project ADD CONSTRAINT fk_day_id FOREIGN KEY (timesheet_day_id) REFERENCES kernely_timesheet_day (id)");
		RawSql timeSheetProjectForeignKey= new RawSql("ALTER TABLE kernely_timesheet_detail_project ADD CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES kernely_project (id)");
		
		commands.add(timeSheetDetailProject);
		commands.add(timeSheetDayForeignKey);
		commands.add(timeSheetProjectForeignKey);

		// Table expense type
		CreateTable expenseType = CreateTable.name("kernely_expense_type");
		expenseType.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		expenseType.column("name", DataBaseConstants.VARCHAR_50);
		expenseType.column("direct", DataBaseConstants.BOOLEAN_DEFAULT_TRUE);
		expenseType.column("ratio", DataBaseConstants.FLOAT4);
		expenseType.column("description", DataBaseConstants.TEXT);
		
		commands.add(expenseType);
		
		// Table expense
		CreateTable expense = CreateTable.name("kernely_expense");
		expense.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		expense.column("amount", DataBaseConstants.FLOAT4);
		expense.column("comment", DataBaseConstants.TEXT);
		expense.column("type_name", DataBaseConstants.VARCHAR_100);
		expense.column("type_ratio", DataBaseConstants.FLOAT4);
		expense.column("timesheet_day_id", DataBaseConstants.LONG);
		RawSql expenseForeignKey= new RawSql("ALTER TABLE kernely_expense ADD CONSTRAINT fk_timesheet_day_id FOREIGN KEY (timesheet_day_id) REFERENCES kernely_timesheet_day (id)");
		
		commands.add(expense);
		commands.add(expenseForeignKey);
		
		
		return commands;
	}
}
