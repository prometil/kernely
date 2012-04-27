package org.kernely.holiday.service.integration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.RoleService;
import org.kernely.core.service.UserService;
import org.kernely.holiday.dto.HolidayBalanceDTO;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayDetailCreationRequestDTO;
import org.kernely.holiday.dto.HolidayProfileCreationRequestDTO;
import org.kernely.holiday.dto.HolidayProfileDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayType;
import org.kernely.holiday.service.HolidayBalanceService;
import org.kernely.holiday.service.HolidayRequestService;
import org.kernely.holiday.service.HolidayService;

import com.google.inject.Inject;

public class HolidayServicesTest extends AbstractServiceTest {

	@Inject
	private HolidayRequestService holidayRequestService;

	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;

	@Inject
	private HolidayService holidayService;

	@Inject
	private HolidayBalanceService balanceService;

	// *********************************************************************//
	// Variables //
	// *********************************************************************//
	private static final String R_COMMENT = "I want my holidays!";

	private static final String MANAGER_COMMENT = "I'll give you your holidays!";

	private static final String USERNAME_MANAGER = "test_manager";
	private static final String USERNAME_USER1 = "test_user1";

	private static final Date HIRE_DATE = new DateTime().withMonthOfYear(6).withDayOfMonth(15).minusYears(3).toDateMidnight().toDate();

	private static final String NAME_PROFILE = "Profile1";

	private static final String NAME_TYPE1 = "type1";
	private static final String NAME_TYPE2 = "type2";
	private static final int QUANTITY1 = 10;
	private static final int QUANTITY2 = 25;
	private static final int PERIOD1 = HolidayType.PERIOD_MONTH;
	private static final int PERIOD2 = HolidayType.PERIOD_YEAR;
	private static final String COLOR1 = "#FFFFFF";
	private static final String COLOR2 = "#000000";

	private static final Date DATE_TODAY = new DateTime().withZone(DateTimeZone.UTC).toDate();
	private static final Date DATE_TOMORROW = new DateTime().withZone(DateTimeZone.UTC).plusDays(1).toDate();

	private HolidayDTO type1;
	private HolidayDTO type2;

	// *********************************************************************//
	// Helper //
	// *********************************************************************//
	private float getAvailableType1ForTheYear(HolidayBalanceDTO balance) {
		// In the tests, we don't use twelth of days because dtos are already
		// converted in days.
		return QUANTITY1 * (Months.monthsBetween(new DateTime(balance.beginDate), new DateTime(balance.endDate)).getMonths());
	}
	
	private float getAvailableType2ForTheYear(HolidayBalanceDTO balance) {
		// In the tests, we don't use twelth of days because dtos are already
		// converted in days.
		// We divide by 12 because this QUANTITY  is associated to a year period.
		return QUANTITY2 * (Months.monthsBetween(new DateTime(balance.beginDate), new DateTime(balance.endDate)).getMonths()) / 12F;
	}
	
	private void createUserRoleForTest() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
	}

	private UserDTO createManagerForTest() {
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME_MANAGER;
		request.password = USERNAME_MANAGER;
		request.hire = HIRE_DATE;
		return userService.createUser(request);
	}

	private UserDTO createUser1ForTest() {
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME_USER1;
		request.password = USERNAME_USER1;
		request.hire = HIRE_DATE;
		return userService.createUser(request);
	}

	private HolidayProfileDTO createHolidayProfileForTest() {
		HolidayCreationRequestDTO creation = new HolidayCreationRequestDTO();
		creation.name = NAME_TYPE1;
		creation.unlimited = false;
		creation.effectiveMonth = HolidayType.ALL_MONTH;
		creation.quantity = QUANTITY1;
		creation.unity = PERIOD1;
		creation.anticipation = true;
		creation.color = COLOR1;

		this.type1 = holidayService.createOrUpdateHoliday(creation);
		
		creation.name = NAME_TYPE2;
		creation.unlimited = false;
		creation.effectiveMonth = HolidayType.JUNE;
		creation.quantity = QUANTITY2;
		creation.unity = PERIOD2;
		creation.anticipation = true;
		creation.color = COLOR2;

		this.type2 = holidayService.createOrUpdateHoliday(creation);

		HolidayProfileCreationRequestDTO profileCreation = new HolidayProfileCreationRequestDTO();
		profileCreation.name = NAME_PROFILE;
		List<Long> typesId = new ArrayList<Long>();
		typesId.add(this.type1.id);
		typesId.add(this.type2.id);
		profileCreation.holidayTypesId = typesId;

		return holidayService.createOrUpdateHolidayProfile(profileCreation);
	}
	
	private HolidayProfileDTO createHolidayProfileWithUnlimitedTypeForTest() {
		HolidayCreationRequestDTO creation = new HolidayCreationRequestDTO();
		creation.name = NAME_TYPE1;
		creation.unlimited = true;
		creation.color = COLOR1;

		this.type1 = holidayService.createOrUpdateHoliday(creation);

		HolidayProfileCreationRequestDTO profileCreation = new HolidayProfileCreationRequestDTO();
		profileCreation.name = NAME_PROFILE;
		List<Long> typesId = new ArrayList<Long>();
		typesId.add(this.type1.id);
		profileCreation.holidayTypesId = typesId;

		return holidayService.createOrUpdateHolidayProfile(profileCreation);
	}

	private HolidayRequestDTO createHolidayRequestForUser(long userId, long id) {
		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();

		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		detailDTO1.day = new DateTime(DATE_TODAY).toString(fmt);
		detailDTO2.day = new DateTime(DATE_TOMORROW).toString(fmt);

		detailDTO1.am = true;
		detailDTO1.pm = true;
		detailDTO2.am = true;
		detailDTO2.pm = true;

		detailDTO1.typeInstanceId = id;
		detailDTO2.typeInstanceId = id;

		List<HolidayDetailCreationRequestDTO> list = new ArrayList<HolidayDetailCreationRequestDTO>();
		list.add(detailDTO1);
		list.add(detailDTO2);

		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		request.details = list;
		request.requesterComment = R_COMMENT;

		return holidayRequestService.registerRequestAndDetails(request);
	}

	// *********************************************************************//
	// Integration test														//
	// *********************************************************************//

	@Test
	public void simulateProfileTypeBalanceAndRequestCreation() {
		// Creation the role User
		this.createUserRoleForTest();

		// Creation of the user
		UserDTO user = this.createUser1ForTest();
		this.createManagerForTest();

		// Link the user to the manager
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user);
		userService.updateManager(USERNAME_MANAGER, managed);

		// Creation of the profile with types inside.
		HolidayProfileDTO profile = this.createHolidayProfileForTest();

		assertEquals(NAME_PROFILE, profile.name);
		assertEquals(2, profile.holidayTypes.size());

		// Links users to this profile
		List<String> usernames = new ArrayList<String>();
		usernames.add(user.username);
		holidayService.updateProfileUsers(profile.id, usernames);

		List<UserDetailsDTO> users = holidayService.getUsersInProfile(profile.id);
		assertEquals(1, users.size());
		assertEquals(user.username, users.get(0).user.username);

		// Verify that balances have been successfully created.
		Set<HolidayBalanceDTO> balanceType1 = balanceService.getHolidayBalancesAvailable(type1.instanceId, user.id);
		Set<HolidayBalanceDTO> balanceType2 = balanceService.getHolidayBalancesAvailable(type2.instanceId, user.id);

		assertEquals(1, balanceType1.size());
		assertEquals(1, balanceType2.size());

		HolidayBalanceDTO balance1 = new ArrayList<HolidayBalanceDTO>(balanceType1).get(0);
		HolidayBalanceDTO balance2 = new ArrayList<HolidayBalanceDTO>(balanceType2).get(0);
		
		assertEquals(getAvailableType1ForTheYear(balance1), balance1.availableBalanceUpdated, 0);
		assertEquals(getAvailableType2ForTheYear(balance2), balance2.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);
		assertEquals(0, balance2.availableBalance, 0);

		// Register an holiday request
		authenticateAs(USERNAME_USER1);
		HolidayRequestDTO request = this.createHolidayRequestForUser(user.id, type1.instanceId);

		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(getAvailableType1ForTheYear(balance1) - 2, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Cancel the request
		holidayRequestService.cancelRequest(request.id);

		// Verify that the days have been restored
		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(getAvailableType1ForTheYear(balance1), balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Register an holiday request
		request = this.createHolidayRequestForUser(user.id, type1.instanceId);

		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(getAvailableType1ForTheYear(balance1) - 2, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Deny the request
		authenticateAs(USERNAME_MANAGER);
		holidayRequestService.denyRequest(request.id);

		authenticateAs(USERNAME_USER1);
		// Verify that the days have been restored
		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(getAvailableType1ForTheYear(balance1), balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Register an holiday request
		request = this.createHolidayRequestForUser(user.id, type1.instanceId);

		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(getAvailableType1ForTheYear(balance1) - 2, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Accept the request
		authenticateAs(USERNAME_MANAGER);
		holidayRequestService.addManagerCommentary(request.id, MANAGER_COMMENT);
		holidayRequestService.acceptRequest(request.id);

		authenticateAs(USERNAME_USER1);
		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.ACCEPTED_STATUS);
		assertEquals(MANAGER_COMMENT, dtos.get(0).managerComment);

		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(getAvailableType1ForTheYear(balance1) - 2, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Remove holidays
		balanceService.removePastHolidays();
		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(-2, balance1.availableBalance, 0); 
		assertEquals(getAvailableType1ForTheYear(balance1) - 2, balance1.availableBalanceUpdated, 0);

		assertEquals(1, holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.PAST_STATUS).size());
	}
	
	@Test
	public void simulateUnlimitedHolidayRequestTest(){
		// Creation the role User
		this.createUserRoleForTest();

		// Creation of the user
		UserDTO user = this.createUser1ForTest();
		this.createManagerForTest();
		
		// Link the user to the manager
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user);
		userService.updateManager(USERNAME_MANAGER, managed);

		// Creation of the profile with types inside.
		HolidayProfileDTO profile = this.createHolidayProfileWithUnlimitedTypeForTest();

		assertEquals(NAME_PROFILE, profile.name);
		assertEquals(1, profile.holidayTypes.size());

		// Links users to this profile
		List<String> usernames = new ArrayList<String>();
		usernames.add(user.username);
		holidayService.updateProfileUsers(profile.id, usernames);

		List<UserDetailsDTO> users = holidayService.getUsersInProfile(profile.id);
		assertEquals(1, users.size());
		assertEquals(user.username, users.get(0).user.username);
		
		// Register an holiday request
		authenticateAs(USERNAME_USER1);
		HolidayRequestDTO request = this.createHolidayRequestForUser(user.id, type1.instanceId);
		
		HolidayBalanceDTO balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(0, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);
		
		// Cancel the request
		holidayRequestService.cancelRequest(request.id);

		// Verify that the days have been restored
		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(0, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Register an holiday request
		request = this.createHolidayRequestForUser(user.id, type1.instanceId);

		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(0, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Deny the request
		authenticateAs(USERNAME_MANAGER);
		holidayRequestService.denyRequest(request.id);

		authenticateAs(USERNAME_USER1);
		// Verify that the days have been restored
		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(0, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		authenticateAs(USERNAME_USER1);
		// Verify that the days have been restored
		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(0, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Register an holiday request
		request = this.createHolidayRequestForUser(user.id, type1.instanceId);

		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(0, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Accept the request
		authenticateAs(USERNAME_MANAGER);
		holidayRequestService.addManagerCommentary(request.id, MANAGER_COMMENT);
		holidayRequestService.acceptRequest(request.id);

		authenticateAs(USERNAME_USER1);
		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.ACCEPTED_STATUS);
		assertEquals(MANAGER_COMMENT, dtos.get(0).managerComment);

		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(0, balance1.availableBalanceUpdated, 0);
		assertEquals(0, balance1.availableBalance, 0);

		// Remove holidays
		balanceService.removePastHolidays();
		balance1 = balanceService.getProcessedBalance(type1.instanceId, user.id);
		assertEquals(0, balance1.availableBalance, 0); 
		assertEquals(0, balance1.availableBalanceUpdated, 0);

		assertEquals(1, holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.PAST_STATUS).size());
	}

	
	
}
