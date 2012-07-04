package org.kernely.timesheet.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TimeSheetMonthDTO {

	/**
	 * The list of projects for the month.
	 */
	public List<TimeSheetMonthProjectDTO> projects;

	/**
	 * Expenses for this month.
	 */
	public List<Float> expenses;

	/**
	 * The position in week : 1 = monday, 7 = sunday
	 */
	public List<Integer> daysOfWeek;

	/**
	 * Are days full of time?
	 */
	public List<Boolean> daysAreFilled;

	/**
	 * Have the month been validated?
	 */
	public boolean validated;

	/**
	 * Can the month been validated?
	 */
	public boolean toValidate;

	/**
	 * The value for the month (1 = January, 12 = December).
	 */
	public int month;

	/**
	 * The year.
	 */
	public int year;

	public TimeSheetMonthDTO() {
	}

	/**
	 * DTO of all days of the monthe.
	 * 
	 * @param daysOfWeek
	 *            Place of days in the week (1=Monday, 7=Sunday)
	 * @param projects
	 *            Lines of the timesheet, containing amouns of times for projects
	 * @param expenses
	 *            Expenses for the month
	 * @param month
	 *            The month : 1 = January, 12 = December.
	 * @param year
	 *            The year of the timesheets.
	 * @param validated
	 *            Is this month validated?
	 * @param daysAreFilled
	 * 			  Sorted 
	 */
	public TimeSheetMonthDTO(List<Integer> daysOfWeek, List<TimeSheetMonthProjectDTO> projects, List<Float> expenses, int month, int year,
			boolean validated, List<Boolean> daysAreFilled) {
		super();
		this.daysOfWeek = daysOfWeek;
		this.projects = projects;
		this.expenses = expenses;
		this.month = month;
		this.year = year;
		this.validated = validated;
		if (daysAreFilled.contains(false)) {
			this.toValidate = false;
		} else {
			this.toValidate = true;
		}
		this.daysAreFilled = daysAreFilled;
	}

}
