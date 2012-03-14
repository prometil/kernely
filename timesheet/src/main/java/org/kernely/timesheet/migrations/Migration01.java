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
		timeSheetDetails.column("timesheet_id", DataBaseConstants.LONG_NOT_NULL);
		RawSql timeSheetForeignKey= new RawSql("ALTER TABLE kernely_timesheet_details ADD CONSTRAINT fk_timesheet_id FOREIGN KEY (timesheet_id) REFERENCES kernely_timesheet (id)");
		
		commands.add(timeSheetDetails);
		commands.add(timeSheetForeignKey);

		// Association table which links the project to a detail
		CreateTable timeSheetDayProject = CreateTable.name("kernely_timesheet_day_project");
		timeSheetDayProject.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		timeSheetDayProject.column("timesheet_detail_id", DataBaseConstants.LONG_NOT_NULL);
		timeSheetDayProject.column("project_id", DataBaseConstants.LONG_NOT_NULL);
		timeSheetDayProject.column("amount", DataBaseConstants.FLOAT);
		RawSql timeSheetDayForeignKey= new RawSql("ALTER TABLE kernely_timesheet_day_project ADD CONSTRAINT fk_detail_id FOREIGN KEY (timesheet_detail_id) REFERENCES kernely_timesheet_details (id)");
		RawSql timeSheetProjectForeignKey= new RawSql("ALTER TABLE kernely_timesheet_day_project ADD CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES kernely_project (id)");
		
		commands.add(timeSheetDayProject);
		commands.add(timeSheetDayForeignKey);
		commands.add(timeSheetProjectForeignKey);
		
		return commands;
	}
}
