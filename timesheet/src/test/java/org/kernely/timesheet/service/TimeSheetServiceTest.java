package org.kernely.timesheet.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.project.dto.OrganizationCreationRequestDTO;
import org.kernely.project.dto.ProjectCreationRequestDTO;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.service.OrganizationService;
import org.kernely.project.service.ProjectService;
import org.kernely.timesheet.dto.TimeSheetCalendarDTO;
import org.kernely.timesheet.dto.TimeSheetDTO;
import org.kernely.timesheet.dto.TimeSheetDayDTO;
import org.kernely.timesheet.dto.TimeSheetDetailDTO;

import com.google.inject.Inject;

public class TimeSheetServiceTest extends AbstractServiceTest {

	private static final int WEEK = 10;
	private static final int YEAR = 2012;
	private static final String NAME = "test";
	private static final String NAME_2 = "AAA";
	private static final String NAME_3 = "BBB";
	private static final String NAME_4 = "CCC";
	private static final float AMOUNT_1 = 3;
	private static final float AMOUNT_2 = 5.25F;
	private static final float AMOUNT_3 = 1;
	private static final Date DATE_1 = new DateTime().withDayOfMonth(5).withMonthOfYear(3).withYear(2012).toDate();
	private static final Date SECOND_DAY_OF_WEEK = new DateTime(DATE_1).withDayOfWeek(2).toDate();
	private static final Date THIRD_DAY_OF_WEEK = new DateTime(DATE_1).withDayOfWeek(3).toDate();
	
	@Inject
	TimeSheetService timeSheetService;

	@Inject
	ProjectService projectService;

	@Inject
	OrganizationService organizationService;

	@Inject
	RoleService roleService;

	@Inject
	UserService userService;
	
	private UserDTO createUserForTest() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = "a";
		request.password = "A";
		return userService.createUser(request);
	}
	
	@Test
	public void getWeek(){
		
		UserDTO user = createUserForTest();
		
		TimeSheetDTO timeSheet = timeSheetService.getTimeSheet(WEEK, YEAR, user.id, true);
		
		int dayOfFirstDay = new DateTime(timeSheet.begin).getDayOfMonth();
		int mounthOfFirstDay = new DateTime(timeSheet.begin).getMonthOfYear();
		int yearOfFirstDay = new DateTime(timeSheet.begin).getYear();
		int dayOfEndDay = new DateTime(timeSheet.end).getDayOfMonth();
		int mounthOfEndDay = new DateTime(timeSheet.end).getMonthOfYear();
		int yearOfEndDay = new DateTime(timeSheet.end).getYear();
		
		assertEquals(5,dayOfFirstDay);
		assertEquals(3,mounthOfFirstDay);
		assertEquals(2012,yearOfFirstDay);
		assertEquals(11,dayOfEndDay);
		assertEquals(3,mounthOfEndDay);
		assertEquals(2012,yearOfEndDay);
	}
	
	@Test
	public void getWeekWithoutCreation(){
		
		UserDTO user = createUserForTest();
		
		TimeSheetDTO timeSheet = timeSheetService.getTimeSheet(WEEK, YEAR, user.id, false);
		
		assertEquals(null,timeSheet);
	}
	
	@Test
	public void getCalendarWeek(){
		
		UserDTO user = createUserForTest();
		
		TimeSheetCalendarDTO calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id);
		
		assertEquals(7,calendar.dates.size());
		
		int dayOfFirstDay = new DateTime(calendar.dates.get(0)).getDayOfMonth();
		int mounthOfFirstDay = new DateTime(calendar.dates.get(0)).getMonthOfYear();
		int yearOfFirstDay = new DateTime(calendar.dates.get(0)).getYearOfCentury();
		int dayOfEndDay = new DateTime(calendar.dates.get(6)).getDayOfMonth();
		int mounthOfEndDay = new DateTime(calendar.dates.get(6)).getMonthOfYear();
		int yearOfEndDay = new DateTime(calendar.dates.get(6)).getYearOfCentury();
		assertEquals(5,dayOfFirstDay);
		assertEquals(3,mounthOfFirstDay);
		assertEquals(12,yearOfFirstDay);
		assertEquals(11,dayOfEndDay);
		assertEquals(3,mounthOfEndDay);
		assertEquals(12,yearOfEndDay);
	}
	
	@Test
	public void getUniqueTimeSheetWithMultipleRequests(){
		
		UserDTO user = createUserForTest();
		
		// The timesheet service should create only once the timesheet
		TimeSheetDTO firstTimeSheet = timeSheetService.getTimeSheet(WEEK, YEAR, user.id, true);
		for (int i = 0; i < 100 ; i++){
			timeSheetService.getTimeSheet(WEEK, YEAR, user.id, true);
		}
		TimeSheetDTO lastTimeSheet = timeSheetService.getTimeSheet(WEEK, YEAR, user.id, true);
		
		List<TimeSheetDTO> allTimeSheets = timeSheetService.getAllTimeSheets();
		
		assertEquals(1, allTimeSheets.size());
		assertEquals(firstTimeSheet.id, lastTimeSheet.id);
	}
	
	@Test
	public void setNewAmountForDay(){
		UserDTO user = createUserForTest();
		authenticateAs(user.username);
		OrganizationCreationRequestDTO organizationRequest = new OrganizationCreationRequestDTO(1, NAME, null, null, null, null, null, null);
		organizationService.createOrganization(organizationRequest);
		ProjectCreationRequestDTO projectRequest = new ProjectCreationRequestDTO(NAME, 1, NAME, NAME);
		ProjectDTO project = projectService.createProject(projectRequest);
		TimeSheetCalendarDTO calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id);
		TimeSheetDetailDTO timeSheetDetail = new TimeSheetDetailDTO();
		timeSheetDetail.amount = AMOUNT_1;
		timeSheetDetail.projectId = project.id;
		timeSheetDetail.day = DATE_1;
		timeSheetService.createOrUpdateDayAmountForProject(timeSheetDetail);
		calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id);
		
		int expectedDay = new DateTime(DATE_1).getDayOfMonth();
		int expectedMonth = new DateTime(DATE_1).getMonthOfYear();
		int expectedYear = new DateTime(DATE_1).getYear();
		
		assertEquals(expectedDay,new DateTime(calendar.dates.get(0)).getDayOfMonth());
		assertEquals(expectedMonth,new DateTime(calendar.dates.get(0)).getMonthOfYear());
		assertEquals(expectedYear,new DateTime(calendar.dates.get(0)).getYear());
		assertEquals(NAME, calendar.timeSheet.columns.get(0).timeSheetDetails.get(0).projectName);
		assertEquals(1, calendar.timeSheet.columns.get(0).timeSheetDetails.size());
		assertEquals(AMOUNT_1, calendar.timeSheet.columns.get(0).timeSheetDetails.get(0).amount, 0F);
	}
	
	@Test
	public void updateAmountForDay(){
		UserDTO user = createUserForTest();
		authenticateAs(user.username);
		OrganizationCreationRequestDTO organizationRequest = new OrganizationCreationRequestDTO(1, NAME, null, null, null, null, null, null);
		organizationService.createOrganization(organizationRequest);
		ProjectCreationRequestDTO projectRequest = new ProjectCreationRequestDTO(NAME, 1, NAME, NAME);
		ProjectDTO project = projectService.createProject(projectRequest);
		TimeSheetCalendarDTO calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id);
		TimeSheetDetailDTO timeSheetDetail = new TimeSheetDetailDTO();
		timeSheetDetail.amount = AMOUNT_1;
		timeSheetDetail.projectId = project.id;
		timeSheetDetail.day = DATE_1;
		TimeSheetDetailDTO newDTO = timeSheetService.createOrUpdateDayAmountForProject(timeSheetDetail);
		newDTO.amount = AMOUNT_2;
		timeSheetService.createOrUpdateDayAmountForProject(newDTO);
		calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id);
		
		int expectedDay = new DateTime(DATE_1).getDayOfMonth();
		int expectedMonth = new DateTime(DATE_1).getMonthOfYear();
		int expectedYear = new DateTime(DATE_1).getYear();
		
		assertEquals(expectedDay,new DateTime(calendar.dates.get(0)).getDayOfMonth());
		assertEquals(expectedMonth,new DateTime(calendar.dates.get(0)).getMonthOfYear());
		assertEquals(expectedYear,new DateTime(calendar.dates.get(0)).getYear());
		assertEquals(NAME, calendar.timeSheet.columns.get(0).timeSheetDetails.get(0).projectName);
		assertEquals(1, calendar.timeSheet.columns.get(0).timeSheetDetails.size());
		assertEquals(AMOUNT_2, calendar.timeSheet.columns.get(0).timeSheetDetails.get(0).amount, 0F);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void setTwiceIndexAmountForDay(){
		UserDTO user = createUserForTest();
		authenticateAs(user.username);
		OrganizationCreationRequestDTO organizationRequest = new OrganizationCreationRequestDTO(1, NAME, null, null, null, null, null, null);
		organizationService.createOrganization(organizationRequest);
		ProjectCreationRequestDTO projectRequest = new ProjectCreationRequestDTO(NAME, 1, NAME, NAME);
		ProjectDTO project = projectService.createProject(projectRequest);
		TimeSheetCalendarDTO calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id);
		TimeSheetDetailDTO timeSheetDetail = new TimeSheetDetailDTO();
		timeSheetDetail.amount = AMOUNT_1;
		timeSheetDetail.projectId = project.id;
		timeSheetDetail.day = DATE_1;
		timeSheetService.createOrUpdateDayAmountForProject(timeSheetDetail);
		// The second call should fail : the detailId is not set,
		// therefore a new detail should be created for this project, this time sheet and this day.
		// But such a detail already exists so it should be updated and not created.
		
		TimeSheetDetailDTO twice = new TimeSheetDetailDTO();
		twice.amount = AMOUNT_1;
		twice.projectId = project.id;
		twice.day = DATE_1;
		
		timeSheetService.createOrUpdateDayAmountForProject(twice);

		calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id);
		
		int expectedDay = new DateTime(DATE_1).getDayOfMonth();
		int expectedMonth = new DateTime(DATE_1).getMonthOfYear();
		int expectedYear = new DateTime(DATE_1).getYear();
		
		assertEquals(expectedDay,new DateTime(calendar.dates.get(0)).getDayOfMonth());
		assertEquals(expectedMonth,new DateTime(calendar.dates.get(0)).getMonthOfYear());
		assertEquals(expectedYear,new DateTime(calendar.dates.get(0)).getYear());
		assertEquals(NAME, calendar.timeSheet.columns.get(0).timeSheetDetails.get(0).projectName);
		assertEquals(1, calendar.timeSheet.columns.get(0).timeSheetDetails.size());
		assertEquals(AMOUNT_1, calendar.timeSheet.columns.get(0).timeSheetDetails.get(0).amount, 0F);
	}
	
	@Test
	public void getOrderedProjects(){
		UserDTO user = createUserForTest();
		authenticateAs(user.username);
		OrganizationCreationRequestDTO organizationRequest = new OrganizationCreationRequestDTO(1, NAME, null, null, null, null, null, null);
		organizationService.createOrganization(organizationRequest);
		
		// Project 1
		ProjectCreationRequestDTO projectRequest = new ProjectCreationRequestDTO(NAME, 1, NAME, NAME);
		ProjectDTO project = projectService.createProject(projectRequest);
		
		// Project 2
		projectRequest = new ProjectCreationRequestDTO(NAME_2, 2, NAME_2, NAME);
		ProjectDTO project2 = projectService.createProject(projectRequest);

		// Project 3
		projectRequest = new ProjectCreationRequestDTO(NAME_3, 3, NAME_3, NAME);
		ProjectDTO project3 = projectService.createProject(projectRequest);

		// Project 4
		projectRequest = new ProjectCreationRequestDTO(NAME_4, 4, NAME_4, NAME);
		ProjectDTO project4 = projectService.createProject(projectRequest);

		TimeSheetCalendarDTO calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id);

		// Fill time sheet with the four projects
		TimeSheetDetailDTO detail1 = new TimeSheetDetailDTO();
		detail1.amount = AMOUNT_3;
		detail1.projectId = project.id;
		detail1.projectName = project.name;
		detail1.day = SECOND_DAY_OF_WEEK;
		detail1.index = 1;
		detail1 = timeSheetService.createOrUpdateDayAmountForProject(detail1);
		
		TimeSheetDetailDTO detail2 = new TimeSheetDetailDTO();
		detail2.amount = AMOUNT_3;
		detail2.projectId = project2.id;
		detail2.projectName = project2.name;
		detail2.day = THIRD_DAY_OF_WEEK;
		detail2.index = 2;
		detail2 = timeSheetService.createOrUpdateDayAmountForProject(detail2);

		TimeSheetDetailDTO detail3 = new TimeSheetDetailDTO();
		detail3.amount = AMOUNT_3;
		detail3.projectId = project3.id;
		detail3.projectName = project3.name;
		detail3.day = THIRD_DAY_OF_WEEK;
		detail3.index = 2;
		timeSheetService.createOrUpdateDayAmountForProject(detail3);

		TimeSheetDetailDTO detail4 = new TimeSheetDetailDTO();
		detail4.amount = AMOUNT_3;
		detail4.projectId = project4.id;
		detail4.projectName = project4.name;
		detail4.day = THIRD_DAY_OF_WEEK;
		detail4.index = 2;
		timeSheetService.createOrUpdateDayAmountForProject(detail4);

		TimeSheetDetailDTO detail5 = new TimeSheetDetailDTO();
		detail5.amount = AMOUNT_3;
		detail5.projectId = project3.id;
		detail5.projectName = project3.name;
		detail5.day = SECOND_DAY_OF_WEEK;
		detail5.index = 1;
		timeSheetService.createOrUpdateDayAmountForProject(detail5);

		// Get calendar and verify the order
		calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id);

		assertEquals(4,calendar.projectsId.size());
		assertEquals(project2.id,calendar.projectsId.get(0).intValue());	
		assertEquals(project3.id,calendar.projectsId.get(1).intValue());	
		assertEquals(project4.id,calendar.projectsId.get(2).intValue());	
		assertEquals(project.id,calendar.projectsId.get(3).intValue());	
	}
	
	@Test
	public void getTimeSheetDayDTOTest(){
		UserDTO user = createUserForTest();
		authenticateAs(user.username);
		
		timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id);
		TimeSheetDayDTO day = timeSheetService.getTimeSheetDayDTO(DATE_1);
		TimeSheetDayDTO day_2nd = timeSheetService.getTimeSheetDayDTO(DATE_1);
		TimeSheetDayDTO day_3rd = timeSheetService.getTimeSheetDayDTO(DATE_1);
		
		assertEquals(new DateTime(DATE_1).toDateMidnight().toDate(), day.day);
		assertEquals(day.id, day_2nd.id);
		assertEquals(day_2nd.id, day_3rd.id);
		assertEquals(day.id, day_3rd.id);
	}
}
