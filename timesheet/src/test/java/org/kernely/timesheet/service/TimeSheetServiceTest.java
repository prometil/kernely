package org.kernely.timesheet.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;

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

import com.google.inject.Inject;

public class TimeSheetServiceTest extends AbstractServiceTest {

	private static final int WEEK = 10;
	private static final int YEAR = 2012;
	private static final String NAME = "test";
	private static final int AMOUNT_1 = 3;
	private static final int AMOUNT_2 = 5;
	private static final int AMOUNT_3 = 1;
	private static final Date DATE_1 = new DateTime().withDayOfMonth(5).withMonthOfYear(3).withYear(2012).toDate();
	
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
	public void getCalendarWeek(){
		
		UserDTO user = createUserForTest();
		
		TimeSheetCalendarDTO calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id, true);
		
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
	public void setNewAmountForDay(){
		UserDTO user = createUserForTest();
		OrganizationCreationRequestDTO organizationRequest = new OrganizationCreationRequestDTO(1, NAME, null, null, null, null, null, null);
		organizationService.createOrganization(organizationRequest);
		ProjectCreationRequestDTO projectRequest = new ProjectCreationRequestDTO(NAME, 1, NAME, NAME);
		ProjectDTO project = projectService.createProject(projectRequest);
		TimeSheetCalendarDTO calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id, true);
		TimeSheetDayDTO timeSheetDay = new TimeSheetDayDTO();
		timeSheetDay.amount = AMOUNT_1;
		timeSheetDay.projectId = project.id;
		timeSheetDay.timeSheetId = calendar.timeSheet.id;
		timeSheetDay.day = DATE_1;
		timeSheetService.createOrUpdateDayAmountForProject(timeSheetDay);
		calendar = timeSheetService.getTimeSheetCalendar(WEEK, YEAR, user.id, true);
		
		int expectedDay = new DateTime(DATE_1).getDayOfMonth();
		int expectedMonth = new DateTime(DATE_1).getMonthOfYear();
		int expectedYear = new DateTime(DATE_1).getYear();
		
		assertEquals(expectedDay,new DateTime(calendar.dates.get(0)).getDayOfMonth());
		assertEquals(expectedMonth,new DateTime(calendar.dates.get(0)).getMonthOfYear());
		assertEquals(expectedYear,new DateTime(calendar.dates.get(0)).getYear());
		assertEquals(1, calendar.timeSheet.rows.size());
		assertEquals(NAME, calendar.timeSheet.rows.get(0).project.name);
		assertEquals(1, calendar.timeSheet.rows.get(0).timeSheetDays.size());
		assertEquals(AMOUNT_1, calendar.timeSheet.rows.get(0).timeSheetDays.get(0).amount, 0F);
	}
}
