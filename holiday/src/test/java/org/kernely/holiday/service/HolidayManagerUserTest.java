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
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.model.HolidayType;

import com.google.inject.Inject;

public class HolidayManagerUserTest extends AbstractServiceTest  {
	
	private static final String USERNAME_MANAGER = "test_manager";
	private static final String USERNAME_USER1 = "test_user1";
	private static final String USERNAME_USER2 = "test_user2";
	
	private static final String DATE1_USER = "01/01/2012";
	private static final String DATE2_USER = "01/02/2012";
	
	private static final String R_COMMENT = "I want my holidays !";
	
	private static final String TEST_STRING = "type";
	private static final int QUANTITY = 3;
	
	@Inject
	private HolidayManagerUserService managerUserService;
	
	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;
	
	@Inject
	private HolidayService holidayService;
	
	@Inject
	private HolidayRequestService holidayRequestService;
	
	private void createUserRoleForTest(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
	}
	
	private UserDTO createManagerForTest(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME_MANAGER;
		request.password = USERNAME_MANAGER;
		return userService.createUser(request);
	}
	
	private UserDTO createUser1ForTest(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME_USER1;
		request.password = USERNAME_USER1;
		return userService.createUser(request);
	}
	
	private UserDTO createUser2ForTest(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME_USER2;
		request.password = USERNAME_USER2;
		return userService.createUser(request);
	}
	
	private HolidayDTO createHolidayTypeForTest(){
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		return holidayService.createHoliday(request);
	}
	
	private void createHolidayRequestForUser(long userId, int typeId){
		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();
		
		detailDTO1.day = DATE1_USER;
		detailDTO2.day = DATE2_USER;
		
		detailDTO1.typeId = typeId;
		detailDTO2.typeId = typeId;
		
		List<HolidayDetailCreationRequestDTO> list = new ArrayList<HolidayDetailCreationRequestDTO>();
		list.add(detailDTO1);
		list.add(detailDTO2);
		
		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		request.details = list;
		request.requesterComment = R_COMMENT;
		
		holidayRequestService.registerRequestAndDetails(request);
	}
	
	@Test
	public void getHolidayForAllManagedTestWithCurrentManager(){
		this.createUserRoleForTest();
		this.createManagerForTest();
		UserDTO user1DTO = this.createUser1ForTest();
		UserDTO user2DTO = this.createUser2ForTest();
		
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user1DTO);
		managed.add(user2DTO);
		userService.updateManager(USERNAME_MANAGER, managed);
		
		HolidayDTO type = this.createHolidayTypeForTest();
		
		authenticateAs(USERNAME_USER1);
		this.createHolidayRequestForUser(user1DTO.id, type.id);
		
		authenticateAs(USERNAME_USER2);
		this.createHolidayRequestForUser(user2DTO.id, type.id);
		
		authenticateAs(USERNAME_MANAGER);
		HolidayUsersManagerDTO dto = managerUserService.getHolidayForAllManagedUsersForMonth(1, 2012);
		DateTime currentDate = new DateTime();
		
		assertEquals(dto.month, 1);
		assertEquals(dto.year, currentDate.getYear());
		assertEquals(dto.nbDays, 31);
		assertEquals(dto.usersManaged.size(), 2);
		assertEquals(2, dto.usersManaged.get(0).details.size());
		assertEquals(dto.usersManaged.get(1).details.size(), 2);
	}
	
	@Test(expected = UnauthorizedException.class)
	public void getHolidayWithCurrentNotManager(){
		this.createUserRoleForTest();
		this.createUser1ForTest();
		authenticateAs(USERNAME_USER1);
		managerUserService.getHolidayForAllManagedUsersForMonth(0, 0);
	}
}
