package org.kernely.holiday.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayDetailCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayType;

import com.google.inject.Inject;

public class HolidayHumanResourceServiceTest extends AbstractServiceTest {
	private static final String USERNAME_MANAGER = "test_manager";
	private static final String USERNAME_USER1 = "test_user1";
	private static final String USERNAME_USER2 = "test_user2";

	private static final String DATE1_USER = "01/01/2012";
	private static final String DATE2_USER = "01/02/2012";

	private static final String R_COMMENT = "I want my holidays !";

	private static final String TEST_STRING = "type";
	private static final int QUANTITY = 3;

	@Inject
	private HolidayHumanResourceService holidayHumanResourceForTest;

	@Inject
	private HolidayService holidayService;

	@Inject
	private UserService userService;

	@Inject
	private HolidayBalanceService holidayBalanceService;
	
	@Inject
	private RoleService roleService;

	@Inject
	private HolidayRequestService requestService;

	private void createUserRoleForTest() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
	}

	private void createHumanRoleForTest() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_HUMANRESOURCE);
		roleService.createRole(requestRole);
	}

	private UserDTO createUser1ForTest() {
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME_USER1;
		request.password = USERNAME_USER1;
		return userService.createUser(request);
	}

	private UserDTO createUser2ForTest() {
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME_USER2;
		request.password = USERNAME_USER2;
		return userService.createUser(request);
	}

	private HolidayDTO createHolidayTypeForTest() {
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.name = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		return holidayService.createOrUpdateHoliday(request);
	}

	private void createHolidayRequestForUser(long userId, int typeInstanceId) {
		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();

		detailDTO1.day = DATE1_USER;
		detailDTO2.day = DATE2_USER;

		detailDTO1.typeInstanceId = typeInstanceId;
		detailDTO2.typeInstanceId = typeInstanceId;

		List<HolidayDetailCreationRequestDTO> list = new ArrayList<HolidayDetailCreationRequestDTO>();
		list.add(detailDTO1);
		list.add(detailDTO2);

		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		request.details = list;
		request.requesterComment = R_COMMENT;

		requestService.registerRequestAndDetails(request);
	}
	
	private UserDTO createManagerForTest(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME_MANAGER;
		request.password = USERNAME_MANAGER;
		return userService.createUser(request);
	}

//	@Test 
//	public void getHolidayForAllUsersForMonthTest(){
//		this.createHumanRoleForTest();
//		this.createUserRoleForTest();
//
//		UserDTO user1DTO = this.createUser1ForTest();              
//		List<RoleDTO> r =  roleService.getAllRoles();
//		UserCreationRequestDTO user = new UserCreationRequestDTO((int)user1DTO.id, user1DTO.userDetails.firstname, user1DTO.userDetails.lastname, user1DTO.username,USERNAME_USER1, user1DTO.locked, r);
//		userService.updateUser(user);
//		List<UserDTO> usr = userService.getAllUsers();
//		user1DTO = usr.get(0); 
//		
//		this.createManagerForTest();
//		UserDTO user2DTO = this.createUser2ForTest();
//		List<UserDTO> managed = new ArrayList<UserDTO>();
//		managed.add(user1DTO);
//		managed.add(user2DTO);
//		userService.updateManager(USERNAME_MANAGER, managed);
//				
//		HolidayDTO type = this.createHolidayTypeForTest();
//
//		authenticateAs(USERNAME_USER2);
//		this.createHolidayRequestForUser(user2DTO.id, type.instanceId);
//		
//		authenticateAs(USERNAME_USER1);
//		this.createHolidayRequestForUser(user1DTO.id, type.instanceId);
//		
//		authenticateAs(USERNAME_MANAGER);
//		userService.currentUserIsHumanResource();
//		
//		List<HolidayRequestDTO> hrdto = requestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
//		requestService.acceptRequest(hrdto.get(0).id);
//		requestService.acceptRequest(hrdto.get(1).id);
//		
//		authenticateAs(user1DTO.username);
//		HolidayUsersManagerDTO dto = holidayHumanResourceForTest.getHolidayForAllUsersForMonth(0, 0);
//		DateTime currentDate = new DateTime();
//		
//		assertEquals(dto.month, currentDate.getMonthOfYear());
//		assertEquals(dto.year, currentDate.getYear());
//		assertEquals(dto.nbDays, currentDate.dayOfMonth().getMaximumValue());
//		assertEquals(dto.usersManaged.size(), 3);
//	}

	@Test(expected = UnauthorizedException.class)
	public void getHolidayForAllUsersForMonthUnauthorizedTest() {
		this.createUserRoleForTest();
		this.createUser1ForTest();
		this.authenticateAs(USERNAME_USER1);
		holidayHumanResourceForTest.getHolidayForAllUsersForMonth(0, 0);
	}

}
