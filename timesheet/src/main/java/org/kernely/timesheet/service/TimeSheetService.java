package org.kernely.timesheet.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.configuration.AbstractConfiguration;
import org.hibernate.annotations.Synchronize;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kernely.core.model.User;
import org.kernely.extension.Extender;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.model.Project;
import org.kernely.service.AbstractService;
import org.kernely.service.mail.Mailer;
import org.kernely.timesheet.dto.TimeSheetCalendarDTO;
import org.kernely.timesheet.dto.TimeSheetColumnDTO;
import org.kernely.timesheet.dto.TimeSheetCreationRequestDTO;
import org.kernely.timesheet.dto.TimeSheetDTO;
import org.kernely.timesheet.dto.TimeSheetDayAmountDTO;
import org.kernely.timesheet.dto.TimeSheetDayDTO;
import org.kernely.timesheet.dto.TimeSheetDetailDTO;
import org.kernely.timesheet.dto.TimeSheetMonthDTO;
import org.kernely.timesheet.dto.TimeSheetMonthProjectDTO;
import org.kernely.timesheet.model.Expense;
import org.kernely.timesheet.model.TimeSheet;
import org.kernely.timesheet.model.TimeSheetDay;
import org.kernely.timesheet.model.TimeSheetDetailProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The service for time sheet pages
 * 
 */
@Singleton
public class TimeSheetService extends AbstractService {

	@Inject
	private Mailer mailService;

	@Inject
	private AbstractConfiguration configuration;

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
		log.debug("Create timesheet from {} to {}", request.begin, request.end);
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
		timeSheet.setUser(user);

		// Build days : one day of the time sheet, from 0 (monday) to 6 (sunday)
		if (id == 0) {
			// Create a new time sheet
			em.get().persist(timeSheet);
		} else {
			// Update case
			em.get().merge(timeSheet);
		}
		Set<TimeSheetDay> defaultDays = new HashSet<TimeSheetDay>();
		TimeSheetDay day;
		for (int i = 0; i < 7; i++) {
			day = getTimeSheetDay(new DateTime(request.begin).plusDays(i).toDateMidnight().toDate(), timeSheet.getId());
			day.setDay(new DateTime(request.begin).plusDays(i).toDate());
			defaultDays.add(day);

			em.get().persist(day);
		}

		timeSheet.setDays(defaultDays);

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
	@Transactional
	public TimeSheetDTO getTimeSheet(int week, int year, long userId, boolean withCreation) {
		log.debug("Searching timesheet for week {}, year {} and user {}",new Object[]{week,year,userId});
		Date firstDay = new DateTime().withWeekOfWeekyear(week).withYear(year).withDayOfWeek(1).toDateMidnight().toDate();
		Date lastDay = new DateTime().withWeekOfWeekyear(week).withYear(year).withDayOfWeek(7).toDateMidnight().toDate();
		Query query = em.get().createQuery("SELECT t FROM TimeSheet t WHERE user = :user AND beginDate = :beginWeek AND endDate = :endWeek");
		User user = em.get().find(User.class, userId);

		query.setParameter("user", user);
		query.setParameter("beginWeek", firstDay);
		query.setParameter("endWeek", lastDay);

		try {
			TimeSheet timeSheet = (TimeSheet) query.getSingleResult();

			DateTime firstDayOfWeek = new DateTime(timeSheet.getBeginDate());

			TimeSheetDTO toReturn = new TimeSheetDTO(timeSheet);
			// Build rows of the DTO: get detailsProjects for all day of the
			// week, from 0 (monday) to 6 (sunday)
			List<TimeSheetColumnDTO> calculatedColumn = new ArrayList<TimeSheetColumnDTO>();
			List<TimeSheetDay> days = new ArrayList<TimeSheetDay>();
			days.addAll(timeSheet.getDays());
			TimeSheetDayDTO dayDTO;
			List<TimeSheetDetailDTO> detailsDTO;
			TimeSheetColumnDTO column;
			int dayIndex = 0;

			for (int i = 0; i < 7; i++) {
				if (dayIndex >= days.size()) {
					detailsDTO = new ArrayList<TimeSheetDetailDTO>();
					dayDTO = new TimeSheetDayDTO();
					column = new TimeSheetColumnDTO(dayDTO, detailsDTO);
					calculatedColumn.add(column);
				} else {
					Date day = new DateTime(days.get(dayIndex).getDay()).withZone(DateTimeZone.UTC).toDateMidnight().toDate();
					if (day.equals(firstDayOfWeek.plusDays(i).withZone(DateTimeZone.UTC).toDateMidnight().toDate())) {
						dayDTO = new TimeSheetDayDTO(days.get(dayIndex));
						detailsDTO = new ArrayList<TimeSheetDetailDTO>();
						Set<TimeSheetDetailProject> details = days.get(dayIndex).getDetailsProjects();

						for (TimeSheetDetailProject detail : details) {
							TimeSheetDetailDTO detailDTO = new TimeSheetDetailDTO(detail);
							detailDTO.index = i;
							detailsDTO.add(detailDTO);
						}
						column = new TimeSheetColumnDTO(dayDTO, detailsDTO);
						calculatedColumn.add(column);
						dayIndex++;
					} else {
						detailsDTO = new ArrayList<TimeSheetDetailDTO>();
						dayDTO = new TimeSheetDayDTO();
						column = new TimeSheetColumnDTO(dayDTO, detailsDTO);
						calculatedColumn.add(column);
					}
				}
			}

			toReturn.columns = calculatedColumn;

			return toReturn;
		} catch (NoResultException nre) {
			log.debug("No timesheet found for week {} and user {}", week, userId);
			if (withCreation) {
				log.debug("Creating timesheet for week {} and user {}", week, userId);
				// Create the time sheet if not founded.
				TimeSheetCreationRequestDTO creationRequest = new TimeSheetCreationRequestDTO();
				creationRequest.begin = firstDay;
				creationRequest.end = lastDay;
				creationRequest.userId = user.getId();

				log.debug("There is no timesheet for period {} to {}, creation of the timesheet.", creationRequest.begin, creationRequest.end);
				return this.createTimeSheet(creationRequest);
			}
			return null;
		}
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
	 */
	@SuppressWarnings("unchecked")
	public TimeSheetCalendarDTO getTimeSheetCalendar(int week, int year, long userId) {

		TimeSheetDTO timeSheet = this.getTimeSheet(week, year, userId, false);

		List<Date> dates = new ArrayList<Date>();
		List<String> stringDates = new ArrayList<String>();
		Date firstDayOfWeek = new DateTime().withWeekOfWeekyear(week).withYear(year).withDayOfWeek(1).toDateMidnight().toDate();
		for (int i = 0; i <= 6; i++) {
			dates.add(new DateTime(firstDayOfWeek).plusDays(i).toDate());
			stringDates.add(new DateTime(firstDayOfWeek).plusDays(i).toString(configuration.getString("locale.dateformat")));
		}

		// Build the list of id project, ordered by alphabetical order of project names
		Set<ProjectDTO> projects = new TreeSet<ProjectDTO>();
		List<Long> projectsId = new ArrayList<Long>();
		ProjectDTO foundProject;
		if (timeSheet != null && timeSheet.columns != null) {
			for (TimeSheetColumnDTO column : timeSheet.columns) {
				for (TimeSheetDetailDTO detail : column.timeSheetDetails) {
					foundProject = new ProjectDTO(detail.projectName, detail.projectId, null, null, null);
					projects.add(foundProject);
				}
			}
		}
		for (ProjectDTO project : projects) {
			projectsId.add(Long.valueOf(project.id));
		}

		// Get projects for the last week, to help user
		// Build the list of id project, ordered by alphabetical order of project names
		TimeSheetDTO lastWeekTimeSheet = null;
		if (week == 1) {
			lastWeekTimeSheet = this.getTimeSheet(52, year - 1, userId, false);
		} else {
			lastWeekTimeSheet = this.getTimeSheet(week - 1, year, userId, false);
		}
		Set<ProjectDTO> lastWeekProjects = new TreeSet<ProjectDTO>();
		List<Long> lastWeekProjectsId = new ArrayList<Long>();
		ProjectDTO lastWeekFoundProject;
		if (lastWeekTimeSheet != null && lastWeekTimeSheet.columns != null) {
			for (TimeSheetColumnDTO column : lastWeekTimeSheet.columns) {
				for (TimeSheetDetailDTO detail : column.timeSheetDetails) {
					lastWeekFoundProject = new ProjectDTO(detail.projectName, detail.projectId, null, null, null);
					lastWeekProjects.add(lastWeekFoundProject);
				}
			}
		}
		for (ProjectDTO project : lastWeekProjects) {
			lastWeekProjectsId.add(Long.valueOf(project.id));
		}

		// Build the list of dates which can not receive amount of time (holiday...)
		log.debug("Searching for unavailable dates in timesheet...");
		HashMap<String, Object> args = new HashMap<String, Object>();
		HashMap<Date, Float> result = new HashMap<Date, Float>();

		Map<Date, Float> unavailable = new HashMap<Date, Float>();
		args.put("start", firstDayOfWeek);
		args.put("end", new DateTime(firstDayOfWeek).plusDays(7).toDate());

		List<Extender> datesExtenders = org.kernely.plugin.PluginManager.getExtenders("date");
		for (Extender dateExtender : datesExtenders) {
			log.debug("Date extender found");
			result = (HashMap<Date, Float>) dateExtender.call(args).get("dates");
			for (Date d : result.keySet()) {
				// If the day is marked as "true", the day is not available
				if (result.get(d) > 0) {
					unavailable.put(d, result.get(d));
				}
			}
		}

		List<Float> available = new ArrayList<Float>();

		log.debug("{} unavailable dates found for this timesheet", unavailable.size());

		for (Date d : dates) {
			if (unavailable.containsKey(d)) {
				available.add(1 - unavailable.get(d));
			} else {
				available.add(1F);
			}
		}

		return new TimeSheetCalendarDTO(week, year, timeSheet, dates, stringDates, available, projectsId, lastWeekProjectsId, configuration
				.getFloat("maxDayValue"));
	}

	/**
	 * Create or update amount of time for a specific project, a specific day and a specific timesheet
	 */
	@Transactional
	public TimeSheetDetailDTO createOrUpdateDayAmountForProject(TimeSheetDetailDTO timeSheetDetailDTO) {

		if (getTimeSheetForDateForCurrentUser(timeSheetDetailDTO.day) == null) {
			log.debug("TimeSheet doesn't exist for this day! Create the time sheet for the day : {}", timeSheetDetailDTO.day);
			Date firstDay = new DateTime(timeSheetDetailDTO.day).withDayOfWeek(1).toDateMidnight().toDate();
			Date lastDay = new DateTime(timeSheetDetailDTO.day).withDayOfWeek(7).toDateMidnight().toDate();

			TimeSheetCreationRequestDTO creationRequest = new TimeSheetCreationRequestDTO();
			creationRequest.begin = firstDay;
			creationRequest.end = lastDay;
			creationRequest.userId = this.getAuthenticatedUserModel().getId();

			this.createTimeSheet(creationRequest);
		}

		TimeSheetDetailProject detailProject;
		Project project = em.get().find(Project.class, timeSheetDetailDTO.projectId);
		TimeSheetDay timeSheetDay = em.get().find(TimeSheetDay.class, timeSheetDetailDTO.dayId);
		TimeSheet timeSheet;
		// Try to find the time sheet with its id
		if (timeSheetDetailDTO.timeSheetId != 0) {
			timeSheet = em.get().find(TimeSheet.class, timeSheetDetailDTO.timeSheetId);
		} else {
			// Get the time sheet with the date
			long timeSheetId = this.getTimeSheet(new DateTime(timeSheetDetailDTO.day).getWeekOfWeekyear(), new DateTime(timeSheetDetailDTO.day)
					.getYear(), getAuthenticatedUserModel().getId(), false).id;
			timeSheet = em.get().find(TimeSheet.class, timeSheetId);
		}

		if (timeSheetDetailDTO.dayId != 0 && timeSheetDetailDTO.projectId != 0) {
			// Update
			Query query = em.get().createQuery("SELECT t FROM TimeSheetDetailProject t WHERE project = :project AND timeSheetDay = :timeSheetDay");
			query.setParameter("project", project);
			query.setParameter("timeSheetDay", timeSheetDay);
			detailProject = (TimeSheetDetailProject) query.getSingleResult();
			detailProject.setAmount(timeSheetDetailDTO.amount);

			em.get().merge(detailProject);
		} else {
			// Create a new detail.
			// Get detail of the timesheet corresponding to the day
			timeSheetDay = new ArrayList<TimeSheetDay>(timeSheet.getDays()).get(timeSheetDetailDTO.index);

			// If a detail for the same project exists for this day, the existing detail should have been updated
			for (TimeSheetDetailProject existingDetailProject : timeSheetDay.getDetailsProjects()) {
				if (project.getId() == existingDetailProject.getId()) {
					throw new IllegalArgumentException("An existing detail for project " + project.getId() + " and day " + timeSheetDay.getId()
							+ " already exists, creation aborted.");
				}
			}

			detailProject = new TimeSheetDetailProject();
			detailProject.setAmount(timeSheetDetailDTO.amount);
			detailProject.setProject(project);
			detailProject.setTimeSheetDay(timeSheetDay);
			em.get().persist(detailProject);

			timeSheetDay.getDetailsProjects().add(detailProject);
			em.get().merge(timeSheetDay);
			timeSheetDetailDTO.dayId = timeSheetDay.getId();
		}
		timeSheetDetailDTO.timeSheetId = timeSheet.getId();
		return timeSheetDetailDTO;

	}

	@Transactional
	private TimeSheetDay getTimeSheetDay(Date day, long timeSheetId) {

		DateTime datetime = new DateTime(day).toDateMidnight().toDateTime();
		Query query = em.get().createQuery("SELECT d FROM TimeSheetDay d WHERE day = :day AND timeSheet = :timeSheet");
		query.setParameter("day", datetime.toDate());
		TimeSheet timeSheet = em.get().find(TimeSheet.class, timeSheetId);
		query.setParameter("timeSheet", timeSheet);
		try {
			// If the detail for this day exists, returns it.
			return (TimeSheetDay) query.getSingleResult();
		} catch (NoResultException nre) {
			// The detail doesn't exist, we have to create it.
			TimeSheetDay detail = new TimeSheetDay();
			detail.setDay(datetime.toDate());
			detail.setTimeSheet(timeSheet);
			em.get().persist(detail);
			return detail;
		}
	}

	@Transactional
	private synchronized TimeSheetDay getTimeSheetDayForUserWithCreation(Date day, long userId) {
		return getTimeSheetDayForUser(day,userId, true);
	}
	
	@Transactional
	private synchronized TimeSheetDay getTimeSheetDayForUserWithoutCreation(Date day, long userId) {
		return getTimeSheetDayForUser(day,userId,false);
	}
	
	@Transactional
	private synchronized TimeSheetDay getTimeSheetDayForUser(Date day, long userId, boolean withCreation){

		DateTime datetime = new DateTime(day).toDateMidnight().toDateTime();

		TimeSheetDTO timeSheetDTO = this.getTimeSheet(datetime.getWeekOfWeekyear(), datetime.getYear(), userId, withCreation);
		
		if (timeSheetDTO == null){
			return null;
		}

		TimeSheet timeSheet = em.get().find(TimeSheet.class, timeSheetDTO.id);

		Query query = em.get().createQuery("SELECT d FROM TimeSheetDay d WHERE day = :day AND timeSheet = :timeSheet");
		query.setParameter("day", datetime.toDate());
		query.setParameter("timeSheet", timeSheet);
		try {
			// If the detail for this day exists, returns it.
			return (TimeSheetDay) query.getSingleResult();
		} catch (NoResultException nre) {
			// The detail doesn't exist, we have to create it if needeed.
			if (withCreation){
				TimeSheetDay detail = new TimeSheetDay();
				detail.setDay(datetime.toDate());
				detail.setTimeSheet(timeSheet);
				em.get().persist(detail);
				return detail;
			} else {
				return null;
			}
		}

	}

	@Transactional
	public List<TimeSheetDayAmountDTO> getTimeSheetDayAmountForUserBetweenDates(Date begin, Date end, long userId) {
		DateTime dateTimeBegin = new DateTime(begin).toDateMidnight().toDateTime();
		DateTime dateTimeEnd = new DateTime(end).toDateMidnight().toDateTime();

		List<TimeSheetDayAmountDTO> dayAmountDTOs = new ArrayList<TimeSheetDayAmountDTO>();

		TimeSheetDay timeSheetDay;
		Set<TimeSheetDetailProject> timeSheetDetails;
		float amount;
		for (DateTime dt = dateTimeBegin; dt.isBefore(dateTimeEnd); dt = dt.plusDays(1)) {
			amount = 0;
			timeSheetDay = this.getTimeSheetDayForUserWithCreation(dt.toDate(), userId);
			timeSheetDetails = timeSheetDay.getDetailsProjects();
			for (TimeSheetDetailProject tsdp : timeSheetDetails) {
				amount += tsdp.getAmount();
			}
			dayAmountDTOs.add(new TimeSheetDayAmountDTO(dt.toDate(), amount));
		}

		return dayAmountDTOs;
	}

	private TimeSheet getTimeSheetForDateForCurrentUser(Date date) {

		Query query = em.get().createQuery("SELECT t FROM TimeSheet t WHERE beginDate <= :date AND endDate >= :date AND user = :user");
		query.setParameter("date", date);
		query.setParameter("user", this.getAuthenticatedUserModel());
		try {
			return (TimeSheet) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * Retrieve the time sheet day for a given day and a given time sheet's id. If this day doesn't exist for this time sheet, it will be created
	 * automatically. If the global timesheet doesn't exist for the current user, it will be created automatically too.
	 * 
	 * @param day
	 *            The day concerned
	 * @param timeSheetId
	 *            The id of the concerned time sheet
	 * @return A DTO representing the day for the given time sheet
	 */
	@Transactional
	public TimeSheetDayDTO getTimeSheetDayDTO(Date day) {

		TimeSheet timesheet = getTimeSheetForDateForCurrentUser(day);
		if (timesheet == null) {
			log.debug("TimeSheet doesn't exist for this day ! Create the time sheet for the day : {}", day);
			Date firstDay = new DateTime(day).withDayOfWeek(1).toDateMidnight().toDate();
			Date lastDay = new DateTime(day).withDayOfWeek(7).toDateMidnight().toDate();

			TimeSheetCreationRequestDTO creationRequest = new TimeSheetCreationRequestDTO();
			creationRequest.begin = firstDay;
			creationRequest.end = lastDay;
			creationRequest.userId = this.getAuthenticatedUserModel().getId();

			TimeSheetDTO tsDto = this.createTimeSheet(creationRequest);
			return new TimeSheetDayDTO(this.getTimeSheetDay(day, tsDto.id));

		} else {
			return new TimeSheetDayDTO(this.getTimeSheetDay(day, timesheet.getId()));
		}
	}

	/**
	 * Removes a line from a timesheet, therefore remove all amounts of time of this line.
	 * 
	 * @param timeSheetId
	 *            The id of the time sheet.
	 * @param projectId
	 *            The id of the project matching the line.
	 */
	@Transactional
	public void removeLine(long timeSheetId, long projectId) {

		TimeSheet timeSheet = em.get().find(TimeSheet.class, timeSheetId);

		boolean rowExists = false;

		// Get all details from timesheet
		for (TimeSheetDay detail : timeSheet.getDays()) {
			for (TimeSheetDetailProject day : detail.getDetailsProjects()) {
				if (day.getProject().getId() == projectId) {
					// Delete the entity
					em.get().remove(day);
					rowExists = true;
				}
			}
		}

		if (!rowExists) {
			throw new IllegalArgumentException("Time sheet with id " + timeSheetId + " do not have project row for project " + projectId + ".");
		}
	}

	/**
	 * Get all time sheet calendars for a month.
	 * 
	 * @param month
	 *            The month.
	 * @param year
	 *            The year.
	 * @param id
	 *            Id of the user.
	 * @return
	 */
	public TimeSheetMonthDTO getMonthTimeSheet(int month, int year, long userId) {
		DateTime dateTimeBegin = new DateTime().withDayOfMonth(1).withMonthOfYear(month).withYear(year).toDateMidnight().toDateTime();
		DateTime dateTimeEnd = new DateTime(dateTimeBegin).plusMonths(1).toDateMidnight().toDateTime();

		TimeSheetDay timeSheetDay;
		TimeSheetDetailDTO timeSheetDetail;
		Set<TimeSheetDetailProject> timeSheetDetails;
		Map<Long, Boolean> projectFound = new HashMap<Long, Boolean>();
		Map<Long, String> projectNames = new HashMap<Long, String>();
		Map<Long, List<TimeSheetDetailDTO>> projectDetails = new HashMap<Long, List<TimeSheetDetailDTO>>();
		long projectId;

		// Get unavailable dates
		// Build the list of dates which can not receive amount of time (holiday...)
		log.debug("Searching for unavailable dates in timesheet from {} to {}...", dateTimeBegin, dateTimeEnd);
		HashMap<String, Object> args = new HashMap<String, Object>();
		HashMap<Date, Float> busyDates = new HashMap<Date, Float>();

		Map<Date, Float> unavailable = new HashMap<Date, Float>();
		args.put("start", dateTimeBegin.toDate());
		args.put("end", dateTimeEnd.toDate());

		List<Extender> datesExtenders = org.kernely.plugin.PluginManager.getExtenders("date");
		for (Extender dateExtender : datesExtenders) {
			log.debug("Date extender found");
			busyDates = (HashMap<Date, Float>) dateExtender.call(args).get("dates");
			for (Date d : busyDates.keySet()) {
				// If the day is marked as "true", the day is not available
				if (busyDates.get(d) > 0) {
					unavailable.put(d, busyDates.get(d));
				}
			}
		}

		List<Integer> daysOfWeek = new ArrayList<Integer>();
		List<Float> expenses = new ArrayList<Float>();
		// For each day
		for (DateTime dt = dateTimeBegin; dt.isBefore(dateTimeEnd); dt = dt.plusDays(1)) {
			daysOfWeek.add(dt.getDayOfWeek());
			timeSheetDay = this.getTimeSheetDayForUserWithoutCreation(dt.toDate(), userId);
			

			// Get expenses
			float sum = 0;
			if (timeSheetDay != null && timeSheetDay.getExpenses() != null) {
				for (Expense expense : timeSheetDay.getExpenses()) {
					sum += expense.getAmount() * expense.getTypeRatio();
				}
			}
			expenses.add(sum);
			
			if (timeSheetDay != null){
				timeSheetDetails = timeSheetDay.getDetailsProjects();
				// For each detail in the day
				for (TimeSheetDetailProject tsdp : timeSheetDetails) {
					projectId = tsdp.getProject().getId();
					projectFound.put(projectId, true);

					// If the project don't exists, we have to add it, and amounts from first day to day before actual day have to be empty details
					if (!projectDetails.containsKey(projectId)) {
						List<TimeSheetDetailDTO> emptyFilledList = new ArrayList<TimeSheetDetailDTO>();
						for (DateTime i = dateTimeBegin; i.isBefore(dt); i = i.plusDays(1)) {

							timeSheetDetail = new TimeSheetDetailDTO();
							timeSheetDetail.status = calculateDayStatus(unavailable, i.toDate());

							emptyFilledList.add(timeSheetDetail);
						}
						projectDetails.put(projectId, emptyFilledList);

						// Save the name
						projectNames.put(projectId, tsdp.getProject().getName());
					}

					// Add the detail for the day
					projectDetails.get(projectId).add(new TimeSheetDetailDTO(tsdp, calculateDayStatus(unavailable, dt.toDate())));
				}
			}

			// Each project not found must be filled with an empty detail
			for (long pId : projectFound.keySet()) {
				if (!projectFound.get(pId)) {
					timeSheetDetail = new TimeSheetDetailDTO();
					timeSheetDetail.status = calculateDayStatus(unavailable, dt.toDate());
					projectDetails.get(pId).add(timeSheetDetail);
				}
				// Reset to false
				projectFound.put(pId, false);
			}
		}

		List<TimeSheetMonthProjectDTO> projectsDTO = new ArrayList<TimeSheetMonthProjectDTO>();

		TimeSheetMonthProjectDTO projectDTO;

		// Build DTO
		for (Long pId : projectDetails.keySet()) {
			projectDTO = new TimeSheetMonthProjectDTO(pId, projectNames.get(pId), projectDetails.get(pId));
			projectsDTO.add(projectDTO);
		}

		TimeSheetMonthDTO monthTimeSheet = new TimeSheetMonthDTO(daysOfWeek, projectsDTO, expenses, month, year, checkMonthTimeSheetValidation(month,
				year, userId), timeSheetCanBeValidated(month, year, userId));
		return monthTimeSheet;
	}

	private String calculateDayStatus(Map<Date, Float> unavailable, Date date) {
		String status = "available";
		if (unavailable.get(date) != null) {
			if (unavailable.get(date).equals(0.5F)) {
				status = "half_available";
			} else if (unavailable.get(date).equals(1.0F)) {
				status = "unavailable";
			}
		}
		return status;
	}

	/**
	 * Validates all days of the month in time sheets, for a specific user.
	 * 
	 * @param month
	 *            The month. 1 = January, 12 = December
	 * @param year
	 *            The year.
	 * @param userId
	 *            The id of the user.
	 * @return True if the timesheet has been validated, false otherwise
	 */
	@Transactional
	public boolean validateMonth(int month, int year, long userId) {
		if (!timeSheetCanBeValidated(month, year, userId)) {
			return false;
		} else {
			log.debug("Validating timesheet of month {} for user {}.", month, userId);

			DateTime firstDayOfMonth = new DateTime().withDayOfMonth(1).withMonthOfYear(month).withYear(year).toDateMidnight().toDateTime();
			DateTime lastDayOfMonth = new DateTime(firstDayOfMonth).plusMonths(1).minusDays(1).toDateMidnight().toDateTime();

			for (DateTime day = firstDayOfMonth; !day.isAfter(lastDayOfMonth); day = day.plusDays(1)) {
				TimeSheetDay dayModel = this.getTimeSheetDayForUserWithCreation(day.toDate(), userId);
				dayModel.setStatus(TimeSheetDay.DAY_VALIDATED);
			}

			// Notify managers by mail
			User currentUser = getAuthenticatedUserModel();
			DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM");
			String stringMonth = formatter.print(firstDayOfMonth);
			String contentString = "The user <span style='font-style:italic;'>" + currentUser.getUserDetails().getFirstname() + " "
					+ currentUser.getUserDetails().getName() + "</span> has validated his time sheet for <span style='font-style:italic;'>"
					+ stringMonth + "</span>";

			List<String> recipients = new ArrayList<String>();
			for (User manager : currentUser.getManagers()) {
				recipients.add(manager.getUserDetails().getMail());
				log.debug("Adds {} in the mail recipients!", manager.getUserDetails().getMail());
			}

			mailService.create("/templates/gsp/timesheet_mail.gsp").with("content", contentString).subject("[Kernely] Time sheet validated").to(
					recipients).registerMail();
			log.debug("Mail registered.");
			return true;
		}
	}

	/**
	 * Check if a user has validated his monthly timesheet
	 * 
	 * @param month
	 *            The mounth. 1 = January, 12 = December.
	 * @param year
	 *            The year
	 * @param userId
	 *            the concerned userId
	 * @return true if the month has been validated, false otherwise.
	 */
	public boolean checkMonthTimeSheetValidation(int month, int year, long userId) {

		DateTime firstDayOfMonth = new DateTime().withDayOfMonth(1).withMonthOfYear(month).withYear(year).toDateMidnight().toDateTime();
		DateTime lastDayOfMonth = new DateTime().withDayOfMonth(1).withMonthOfYear(month).plusMonths(1).minusDays(1).withYear(year).toDateMidnight()
				.toDateTime();

		for (DateTime day = firstDayOfMonth; !day.isAfter(lastDayOfMonth); day = day.plusDays(1)) {
			TimeSheetDay dayModel = this.getTimeSheetDayForUserWithoutCreation(day.toDate(), userId);
			if (dayModel != null && dayModel.getStatus() == TimeSheetDay.DAY_TO_VALIDATE) {
				return false;
			}
		}
		return true;
	}

	private boolean timeSheetCanBeValidated(int month, int year, long userId) {
		float maxDayValue = configuration.getFloat("maxDayValue");

		DateTime firstDayOfMonth = new DateTime().withDayOfMonth(1).withMonthOfYear(month).withYear(year).toDateMidnight().toDateTime();
		DateTime lastDayOfMonth = new DateTime(firstDayOfMonth).plusMonths(1).minusDays(1).toDateMidnight().toDateTime();

		// Get unavailable dates
		HashMap<Date, Float> result = new HashMap<Date, Float>();

		log.debug("Searching for unavailable dates in timesheet...");
		HashMap<String, Object> args = new HashMap<String, Object>();
		Map<Date, Float> unavailable = new HashMap<Date, Float>();
		args.put("start", firstDayOfMonth.toDate());
		args.put("end", new DateTime(lastDayOfMonth.plusDays(1).toDate()).toDate());

		List<Extender> datesExtenders = org.kernely.plugin.PluginManager.getExtenders("date");
		for (Extender dateExtender : datesExtenders) {
			log.debug("Date extender found");
			result = (HashMap<Date, Float>) dateExtender.call(args).get("dates");
			for (Date d : result.keySet()) {
				// If the day is greater than 0, the day is not available
				if (result.get(d) > 0) {
					unavailable.put(d, result.get(d));
				}
			}
		}

		Set<Date> pending = new HashSet<Date>();

		datesExtenders = org.kernely.plugin.PluginManager.getExtenders("pending_holidays_dates");
		for (Extender dateExtender : datesExtenders) {
			pending.addAll((HashSet<Date>) (dateExtender.call(args).get("dates")));
		}

		TimeSheetDay dayModel;
		// Check every day
		for (DateTime day = firstDayOfMonth; !day.isAfter(lastDayOfMonth); day = day.plusDays(1)) {
			if (pending.contains(day.toDate())) {
				// If the day is in a pending request, the timesheet can not be validated
				return false;
			}
			float amount = 0;
			dayModel = this.getTimeSheetDayForUserWithoutCreation(day.toDate(), userId);
			if (dayModel != null){
				for (TimeSheetDetailProject detail : dayModel.getDetailsProjects()) {
					amount += detail.getAmount();
				}
			}

			// Check if the amount is correct
			if (unavailable.containsKey(day.toDate())) {
				amount += unavailable.get(day.toDate()) * maxDayValue;
			}

			if ((maxDayValue - amount ) > 0.001) {
				// The timesheet can not be validated if the difference between amount and max value is too different
				return false;
			}
		}
		return true;
	}
}