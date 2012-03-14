package org.kernely.timesheet;

import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.timesheet.controller.TimeSheetController;
import org.kernely.timesheet.migrations.Migration01;
import org.kernely.timesheet.model.TimeSheet;
import org.kernely.timesheet.model.TimeSheetDayProject;
import org.kernely.timesheet.model.TimeSheetDetail;
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
		registerModel(TimeSheetDetail.class);
		registerModel(TimeSheet.class);
		registerModel(TimeSheetDayProject.class);
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
