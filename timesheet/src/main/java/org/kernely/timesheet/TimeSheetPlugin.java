package org.kernely.timesheet;

import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.timesheet.controller.ExpenseAdminController;
import org.kernely.timesheet.controller.ExpenseController;
import org.kernely.timesheet.controller.TimeSheetController;
import org.kernely.timesheet.migrations.Migration01;
import org.kernely.timesheet.model.Expense;
import org.kernely.timesheet.model.ExpenseType;
import org.kernely.timesheet.model.TimeSheet;
import org.kernely.timesheet.model.TimeSheetDay;
import org.kernely.timesheet.model.TimeSheetDetailProject;
import org.kernely.timesheet.service.TimeSheetService;

/**
 * Plugin for project
 */
public class TimeSheetPlugin extends AbstractPlugin {
	public static final String NAME = "timesheet";

	/**
	 * Default constructor
	 */
	public TimeSheetPlugin() {
		super();
		registerName(NAME);
		registerPath("/timesheet");
		registerController(TimeSheetController.class);
		registerController(ExpenseAdminController.class);
		registerController(ExpenseController.class);
		
		registerModel(TimeSheetDay.class);
		registerModel(TimeSheet.class);
		registerModel(TimeSheetDetailProject.class);
		registerModel(ExpenseType.class);
		registerModel(Expense.class);
		registerAdminPage("Expense type admin", "/admin/expense");
		registerMigration(new Migration01());
	}

	@Override
	public void start() {

	}

	@Override
	protected void configure() {
		bind(TimeSheetService.class);
	}

}
