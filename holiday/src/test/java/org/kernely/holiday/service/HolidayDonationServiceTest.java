package org.kernely.holiday.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.RoleService;
import org.kernely.core.service.UserService;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayDonationDTO;
import org.kernely.holiday.model.HolidayType;

import com.google.inject.Inject;

public class HolidayDonationServiceTest extends AbstractServiceTest {
	
	@Inject
	private HolidayDonationService holidayDonationService;
	
	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;
	
	
	@Inject
	private HolidayService holidayService;
	
	private static final String USERNAME_MANAGER = "test_manager";
	private static final String USERNAME_USER1 = "test_user1";
	
	private static final Date HIRE_DATE = new DateTime().withMonthOfYear(6).withDayOfMonth(15).minusYears(3).toDateMidnight().toDate();
	
	private static final String TEST_STRING = "type";
	private static final int PERIOD = HolidayType.PERIOD_MONTH;

	
	private void createUserRoleForTest(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
	}
	
	private HolidayDTO createHolidayTypeManuallyManagedForTest(){
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.name = TEST_STRING;
		request.quantity = 0; // A 0 in quantity means manually managed
		request.unity = PERIOD;
		request.effectiveMonth = HolidayType.ALL_MONTH;
		request.unlimited = false;
		request.anticipation = false;
		return holidayService.createOrUpdateHoliday(request);
	}
	
	private UserDTO createManagerForTest(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME_MANAGER;
		request.password = USERNAME_MANAGER;
		request.hire = HIRE_DATE;
		return userService.createUser(request);
	}
	
	private UserDTO createUser1ForTest(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME_USER1;
		request.password = USERNAME_USER1;
		request.hire = HIRE_DATE;
		return userService.createUser(request);
	}

	@Test(expected=UnauthorizedException.class)
	public void createHolidayDonationNoManagerTest(){
		this.createUserRoleForTest();
		UserDTO udto = this.createUser1ForTest();
		authenticateAs(USERNAME_USER1);
		
		HolidayDonationDTO request = new HolidayDonationDTO();
		request.amount = 1;
		request.comment= "";
		request.managerId = udto.id;
		request.receiverId = udto.id;
		request.typeInstanceId = 0; // Doesn't matter actually
		
		holidayDonationService.createDonation(request);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createHolidayDonationWithWrongForGoodTypeParamsTest(){
		HolidayDTO hdto = this.createHolidayTypeManuallyManagedForTest();

		this.createUserRoleForTest();
		this.createManagerForTest();
		UserDTO udto = this.createUser1ForTest();
		
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(udto);
		userService.updateManager(USERNAME_MANAGER, managed);
		
		authenticateAs(USERNAME_MANAGER);
		
		HolidayDonationDTO request = new HolidayDonationDTO();
		request.amount = 12;
		request.comment= "";
		request.receiverId = udto.id;
		request.typeInstanceId = hdto.instanceId;
		
		holidayDonationService.createDonation(request);
		
	}
	
	@Test
	public void createHolidayDonationTest(){
		HolidayDTO hdto = this.createHolidayTypeManuallyManagedForTest();

		this.createUserRoleForTest();
		UserDTO managerDTO = this.createManagerForTest();
		UserDTO udto = this.createUser1ForTest();
		
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(udto);
		userService.updateManager(USERNAME_MANAGER, managed);
		
		authenticateAs(USERNAME_MANAGER);
		
		HolidayDonationDTO request = new HolidayDonationDTO();
		request.amount = 2;
		request.comment= "COMMENT";
		request.receiverId = udto.id;
		request.typeInstanceId = hdto.instanceId;
		
		HolidayDonationDTO donation = holidayDonationService.createDonation(request);
		assertEquals(2, donation.amount, 0);
		assertEquals(managerDTO.id, donation.managerId);
		assertEquals(managerDTO.username, donation.managerUsername);
		assertEquals(udto.id, donation.receiverId);
		assertEquals(udto.username, donation.receiverUsername);
		assertEquals("COMMENT", donation.comment);
	}

	@Test
	public void getAllHolidayDonationTest(){
		HolidayDTO hdto = this.createHolidayTypeManuallyManagedForTest();

		this.createUserRoleForTest();
		this.createManagerForTest();
		UserDTO udto = this.createUser1ForTest();
		
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(udto);
		userService.updateManager(USERNAME_MANAGER, managed);
		
		authenticateAs(USERNAME_MANAGER);
		
		HolidayDonationDTO request1 = new HolidayDonationDTO();
		request1.amount = 2;
		request1.comment= "COMMENT";
		request1.receiverId = udto.id;
		request1.typeInstanceId = hdto.instanceId;
		
		HolidayDonationDTO request2 = new HolidayDonationDTO();
		request2.amount = 3;
		request2.comment= "COMMENT";
		request2.receiverId = udto.id;
		request2.typeInstanceId = hdto.instanceId;
		
		HolidayDonationDTO donation1 = holidayDonationService.createDonation(request1);
		HolidayDonationDTO donation2 = holidayDonationService.createDonation(request2);
		
		List<HolidayDonationDTO> donations = holidayDonationService.getAllDonationForCurrentManager();
		HolidayDonationDTO d1 = donations.get(0);
		HolidayDonationDTO d2 = donations.get(1);
		
		assertEquals(donation2.amount, d1.amount, 0);
		assertEquals(donation2.managerId, d1.managerId);
		assertEquals(donation2.managerUsername, d1.managerUsername);
		assertEquals(donation2.receiverId, d1.receiverId);
		assertEquals(donation2.receiverUsername, d1.receiverUsername);
		assertEquals(donation2.comment, d1.comment);
		
		assertEquals(donation1.amount, d2.amount, 0);
		assertEquals(donation1.managerId, d2.managerId);
		assertEquals(donation1.managerUsername, d2.managerUsername);
		assertEquals(donation1.receiverId, d2.receiverId);
		assertEquals(donation1.receiverUsername, d2.receiverUsername);
		assertEquals(donation1.comment, d2.comment);
	}
	
}
