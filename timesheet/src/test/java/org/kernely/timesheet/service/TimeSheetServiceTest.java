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
import org.kernely.timesheet.dto.TimeSheetCalendarDTO;
import org.kernely.timesheet.dto.TimeSheetDTO;

import com.google.inject.Inject;

public class TimeSheetServiceTest extends AbstractServiceTest {

	private static final int WEEK = 10;
	private static final int YEAR = 2012;
	
	@Inject
	TimeSheetService timeSheetService;

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
		int yearOfFirstDay = new DateTime(timeSheet.begin).getYearOfCentury();
		int dayOfEndDay = new DateTime(timeSheet.end).getDayOfMonth();
		int mounthOfEndDay = new DateTime(timeSheet.end).getMonthOfYear();
		int yearOfEndDay = new DateTime(timeSheet.end).getYearOfCentury();
		
		assertEquals(5,dayOfFirstDay);
		assertEquals(3,mounthOfFirstDay);
		assertEquals(12,yearOfFirstDay);
		assertEquals(11,dayOfEndDay);
		assertEquals(3,mounthOfEndDay);
		assertEquals(12,yearOfEndDay);
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
}
