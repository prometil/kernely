package org.kernely.timesheet.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.joda.time.DateTime;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.core.service.user.UserService;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.model.Project;
import org.kernely.timesheet.dto.TimeSheetCalendarDTO;
import org.kernely.timesheet.dto.TimeSheetCreationRequestDTO;
import org.kernely.timesheet.dto.TimeSheetDTO;
import org.kernely.timesheet.dto.TimeSheetDayDTO;
import org.kernely.timesheet.dto.TimeSheetRowDTO;
import org.kernely.timesheet.model.TimeSheet;
import org.kernely.timesheet.model.TimeSheetDayProject;
import org.kernely.timesheet.model.TimeSheetDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The service for organization pages
 * 
 */
@Singleton
public class TimeSheetService extends AbstractService {

	@Inject
	UserService userService;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Gets the lists of all timesheets contained in the database.
	 * 
	 * @return the list of all timesheets contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<TimeSheetDTO> getAllTimeSheets() {
		Query query = em.get().createQuery("SELECT t FROM TimeSheet t");
		List<TimeSheet> collection = (List<TimeSheet>) query.getResultList();
		List<TimeSheetDTO> dtos = new ArrayList<TimeSheetDTO>();
		for (TimeSheet timeSheet : collection) {
			dtos.add(new TimeSheetDTO(timeSheet));
		}
		return dtos;
	}

	/**
	 * Create a timesheet
	 * 
	 * @return the dto of the timesheet created
	 */
	@Transactional
	public TimeSheetDTO createTimeSheet(TimeSheetCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		TimeSheet timeSheet;

		long id = request.id;
		if (id == 0) {
			// Create a new type
			timeSheet = new TimeSheet();
		} else {
			// Type is already in database
			timeSheet = em.get().find(TimeSheet.class, id);
		}

		User user = em.get().find(User.class, request.userId);

		timeSheet.setBeginDate(request.begin);
		timeSheet.setEndDate(request.end);
		timeSheet.setFeesStatus(request.feesStatus);
		timeSheet.setStatus(request.status);
		timeSheet.setUser(user);
		
		// Build details : one day of the time sheet, from 0 (monday) to 6 (sunday)
		if (id == 0) {
			// Create a new time sheet
			em.get().persist(timeSheet);
		} else {
			// Update case
			em.get().merge(timeSheet);
		}
		
		List<TimeSheetDetail> defaultDetails = new ArrayList<TimeSheetDetail>();
		for (int i = 0; i < 7 ; i++){
			
			TimeSheetDetail detail = new TimeSheetDetail();
			detail.setDay(new DateTime(request.begin).plusDays(i).toDate());
			detail.setTimeSheet(timeSheet);
			defaultDetails.add(detail);
			
			em.get().persist(detail);
		}
		
		timeSheet.setDetails(defaultDetails);

		em.get().merge(timeSheet);
		return new TimeSheetDTO(timeSheet);
	}

	/**
	 * Return the timesheet corresponding to the week and the user.
	 * 
	 * @param week
	 *            The week (from 1 to 52) in the year.
	 * @param year
	 *            The year.
	 * @param userIdThe
	 *            id of the user.
	 * @parma withCreation If true, will create the timesheet if it does not exists, and return it.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public TimeSheetDTO getTimeSheet(int week, int year, long userId, boolean withCreation) {
		Query query = em.get().createQuery("SELECT t FROM TimeSheet t WHERE user = :user");
		User user = em.get().find(User.class, userId);

		query.setParameter("user", user);
		List<TimeSheet> timeSheets;
		timeSheets = (List<TimeSheet>) query.getResultList();
		DateTime weekDate = new DateTime().withYear(year).withWeekOfWeekyear(week);
		DateTime firstDay = weekDate.withDayOfWeek(1).toDateMidnight().toDateTime();
		DateTime lastDay = weekDate.withDayOfWeek(7).toDateMidnight().toDateTime();

		for (TimeSheet sheet : timeSheets) {

			// Search the timesheet corresponding to the dates
			if (firstDay.isEqual(new DateTime(sheet.getBeginDate()).toDateMidnight().toDateTime())
					&& lastDay.isEqual(new DateTime(sheet.getEndDate()).toDateMidnight().toDateTime())){
				TimeSheetDTO toReturn = new TimeSheetDTO(sheet);
				// Build rows of the DTO: get dayProjects for all day of the week, from 0 (monday) to 6 (sunday)
				List<TimeSheetRowDTO> calculatedRows = new ArrayList<TimeSheetRowDTO>(7);
				for (int i = 0; i < 7; i++) {
					Set<TimeSheetDayProject> dayProjects = sheet.getDetails().get(i).getDayProjects();
					for (TimeSheetDayProject dayProject : dayProjects) {
						TimeSheetDayDTO dayDTO = new TimeSheetDayDTO(i, dayProject.getTimeSheetDetail().getId(), dayProject.getAmount(), sheet.getDetails().get(i).getDay(),
								sheet.getId(), dayProject.getProject().getId());
						boolean found = false; // To know if we have found the row
						// Add the day to the correct row or, if don't exists, create the row
						for (TimeSheetRowDTO row : calculatedRows) {
							if (row.project.id == dayProject.getProject().getId()) {
								found = true;
								row.timeSheetDays.add(dayDTO);
							}
						}
						if (!found) {
							// Creates the row associated to the project
							List<TimeSheetDayDTO> newList = new ArrayList<TimeSheetDayDTO>();
							newList.add(dayDTO);
							calculatedRows.add(new TimeSheetRowDTO(new ProjectDTO(dayProject.getProject()), newList));
						}
					}
				}

				toReturn.rows = calculatedRows;

				return toReturn;
			}
		}
		if (withCreation) {
			// Create the time sheet if not founded.
			TimeSheetCreationRequestDTO creationRequest = new TimeSheetCreationRequestDTO();
			creationRequest.begin = firstDay.toDate();
			creationRequest.end = lastDay.toDate();
			creationRequest.feesStatus = TimeSheet.FEES_VALIDATED;
			creationRequest.status = TimeSheet.TIMESHEET_PENDING;
			creationRequest.userId = user.getId();
			
			return this.createTimeSheet(creationRequest);
		}
		return null;
	}

	/**
	 * Return the timesheet corresponding to the week and the user.
	 * 
	 * @param week
	 *            The week (from 1 to 52) in the year.
	 * @param year
	 *            The year.
	 * @param userIdThe
	 *            id of the user.
	 * @parma withCreation If true, will create the timesheet if it does not exists, and return it.
	 */
	public TimeSheetCalendarDTO getTimeSheetCalendar(int week, int year, long userId) {
		TimeSheetDTO timeSheet = this.getTimeSheet(week, year, userId, true);
		
		List<Date> dates = new ArrayList<Date>();
		List<String> stringDates = new ArrayList<String>();

		for (int i = 0; i <= 6; i++) {
			dates.add(new DateTime(timeSheet.begin).plusDays(i).toDate());
			stringDates.add(new DateTime(timeSheet.begin).plusDays(i).toString("MM/dd/yy"));
		}

		return new TimeSheetCalendarDTO(timeSheet, dates, stringDates);
	}

	/**
	 * Create or update amount of time for a specific project, a specific day and a specific timesheet
	 */
	@Transactional
	public TimeSheetDayDTO createOrUpdateDayAmountForProject(TimeSheetDayDTO timeSheetDay) {
		TimeSheetDayProject dayProject;
		Project project = em.get().find(Project.class, timeSheetDay.projectId);
		TimeSheetDetail timeSheetDetail = em.get().find(TimeSheetDetail.class, timeSheetDay.detailId);
		TimeSheet timeSheet = em.get().find(TimeSheet.class, timeSheetDay.timeSheetId);
		if (timeSheetDay.detailId != 0 && timeSheetDay.projectId != 0) {
			// Update
			Query query = em.get().createQuery("SELECT t FROM TimeSheetDayProject t WHERE project = :project AND timeSheetDetail = :timeSheetDetail");
			query.setParameter("project", project);
			query.setParameter("timeSheetDetail", timeSheetDetail);
			dayProject = (TimeSheetDayProject) query.getSingleResult();
			dayProject.setAmount(timeSheetDay.amount);

			em.get().merge(dayProject);
		} else {
			// Get detail of the timesheet corresponding to the day
			timeSheetDetail = timeSheet.getDetails().get(timeSheetDay.index);
			
			dayProject = new TimeSheetDayProject();
			dayProject.setAmount(timeSheetDay.amount);
			dayProject.setProject(project);
			dayProject.setTimeSheetDetail(timeSheetDetail);
			em.get().persist(dayProject);
			// Get the place in week
			
			timeSheetDetail.getDayProjects().add(dayProject);
			em.get().merge(timeSheetDetail);
			timeSheetDay.detailId = timeSheetDetail.getId();
		}
		
		return timeSheetDay;
		
	}

	/**
	 * Removes a line from a timesheet, therefore remove all amounts of time of this line.
	 * @param timeSheetId The id of the time sheet.
	 * @param projectId The id of the project matching the line.
	 */
	@Transactional
	public void removeLine(long timeSheetId, long projectId) {
		TimeSheet timeSheet = em.get().find(TimeSheet.class, timeSheetId);
		
		// Get all details from timesheet
		for (TimeSheetDetail detail : timeSheet.getDetails()){
			for (TimeSheetDayProject day : detail.getDayProjects()){
				if (day.getProject().getId() == projectId){
					em.get().remove(day);
				}
			}
		}
	}
}
