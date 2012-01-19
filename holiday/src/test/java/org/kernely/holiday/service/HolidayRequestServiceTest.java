package org.kernely.holiday.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsUpdateRequestDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.holiday.dto.CalendarRequestDTO;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayDetailCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayType;

import com.google.inject.Inject;

public class HolidayRequestServiceTest extends AbstractServiceTest{

	private static final String DATE1 = "01/01/2012";
	private static final String DATE2 = "01/03/2012";
	private static final String DATE3 = "01/05/2012";
	private static final String DATE4 = "12/30/2011";
	private static final String DATE5 = "01/20/2012";
	
	private static final String DATE1_USER = "01/01/2012";
	private static final String DATE2_USER = "01/02/2012";
	
	private static final String R_COMMENT = "I want my holidays!";
	
	private static final String MANAGER_COMMENT = "I'll give you your holidays!";
	
	private static final String USERNAME_MANAGER = "test_manager";
	private static final String USERNAME_USER1 = "test_user1";
	
	private static final String TEST_STRING = "type";
	private static final int QUANTITY = 3;

	@Inject
	private HolidayRequestService holidayRequestService;

	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;

	@Inject
	private HolidayService holidayService;

	@Inject
	private HolidayBalanceService holidayBalanceService;

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
	
	private void createHoliday2RequestForUser(long userId, int typeId){
		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();
		
		detailDTO1.day = "01/17/2012";
		detailDTO2.day = "02/05/2012";
		
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
	public void registerRequestTest(){
		HolidayDTO hdto = this.createHolidayTypeForTest();

		this.createUserRoleForTest();
		UserDTO udto = this.createUser1ForTest();
		authenticateAs(USERNAME_USER1);

		holidayBalanceService.createHolidayBalance(udto.id, hdto.id);

		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO3 = new HolidayDetailCreationRequestDTO();
		detailDTO1.day = DATE1;
		detailDTO2.day = DATE2;
		detailDTO3.day = DATE3;

		detailDTO1.typeId = hdto.id;
		detailDTO2.typeId = hdto.id;
		detailDTO3.typeId = hdto.id;

		List<HolidayDetailCreationRequestDTO> list = new ArrayList<HolidayDetailCreationRequestDTO>();
		list.add(detailDTO1);
		list.add(detailDTO2);
		list.add(detailDTO3);

		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		request.details = list;
		request.requesterComment = R_COMMENT;

		holidayRequestService.registerRequestAndDetails(request);

		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsForCurrentUser();
		HolidayRequestDTO testDto = dtos.get(0);

		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		assertEquals(new DateTime(testDto.beginDate).toString(fmt), DATE1);
		assertEquals(new DateTime(testDto.endDate).toString(fmt), DATE3);
		assertEquals(testDto.details.size(), 3);
		assertEquals(testDto.requesterComment, R_COMMENT);
	}


	@Test
	public void waitingRequestTest(){
		HolidayDTO hdto = this.createHolidayTypeForTest();

		this.createUserRoleForTest();
		UserDTO udto = this.createUser1ForTest();
		authenticateAs(USERNAME_USER1);

		holidayBalanceService.createHolidayBalance(udto.id, hdto.id);

		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO3 = new HolidayDetailCreationRequestDTO();
		detailDTO1.day = DATE1;
		detailDTO2.day = DATE2;
		detailDTO3.day = DATE3;

		detailDTO1.typeId = hdto.id;
		detailDTO2.typeId = hdto.id;
		detailDTO3.typeId = hdto.id;

		List<HolidayDetailCreationRequestDTO> list = new ArrayList<HolidayDetailCreationRequestDTO>();
		list.add(detailDTO1);
		list.add(detailDTO2);
		list.add(detailDTO3);

		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		request.details = list;
		request.requesterComment = R_COMMENT;

		holidayRequestService.registerRequestAndDetails(request);

		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.PENDING_STATUS);
		assertEquals(1, dtos.size());
	}

	@Test
	public void acceptRequestTest(){
		this.createUserRoleForTest();
		this.createManagerForTest();
		UserDTO user1DTO = this.createUser1ForTest();
		
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user1DTO);
		userService.updateManager(USERNAME_MANAGER, managed);
		
		HolidayDTO type = this.createHolidayTypeForTest();
		
		authenticateAs(USERNAME_USER1);
		this.createHolidayRequestForUser(user1DTO.id, type.id);
		
		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.PENDING_STATUS);
		assertEquals(1, dtos.size());

		authenticateAs(USERNAME_MANAGER);
		dtos = holidayRequestService.getSpecificRequestsForManagers(HolidayRequest.PENDING_STATUS);
		assertEquals(1, dtos.size());
		
		holidayRequestService.acceptRequest(dtos.get(0).id);
		dtos = holidayRequestService.getSpecificRequestsForManagers(HolidayRequest.PENDING_STATUS);
		assertEquals(0, dtos.size());
		
		authenticateAs(USERNAME_USER1);
		List<HolidayRequestDTO> acceptedDtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.ACCEPTED_STATUS);
		assertEquals(1, acceptedDtos.size());
	}
	
	@Test(expected = UnauthorizedException.class)
	public void acceptRequestWhenNoManagerTest(){
		this.createUserRoleForTest();
		UserDTO user1DTO = this.createUser1ForTest();
		
		HolidayDTO type = this.createHolidayTypeForTest();
		
		authenticateAs(USERNAME_USER1);
		this.createHolidayRequestForUser(user1DTO.id, type.id);

		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.PENDING_STATUS);
		
		holidayRequestService.acceptRequest(dtos.get(0).id);
	}
	
	
	@Test
	public void commentManagerRequestTest(){
		this.createUserRoleForTest();
		this.createManagerForTest();
		UserDTO user1DTO = this.createUser1ForTest();
		
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user1DTO);
		userService.updateManager(USERNAME_MANAGER, managed);
		
		HolidayDTO type = this.createHolidayTypeForTest();
		
		authenticateAs(USERNAME_USER1);
		this.createHolidayRequestForUser(user1DTO.id, type.id);

		authenticateAs(USERNAME_MANAGER);
		List<HolidayRequestDTO> dtos = holidayRequestService.getSpecificRequestsForManagers(HolidayRequest.PENDING_STATUS);
		holidayRequestService.addManagerCommentary(dtos.get(0).id, MANAGER_COMMENT);
		
		authenticateAs(USERNAME_USER1);
		dtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.PENDING_STATUS);
		assertEquals(MANAGER_COMMENT, dtos.get(0).managerComment);
	}
	
	@Test(expected = UnauthorizedException.class)
	public void commentManagerRequestWhenNoManagerTest(){
		this.createUserRoleForTest();
		
		UserDTO user1DTO = this.createUser1ForTest();
		
		HolidayDTO type = this.createHolidayTypeForTest();
		
		authenticateAs(USERNAME_USER1);
		this.createHolidayRequestForUser(user1DTO.id, type.id);

		List<HolidayRequestDTO> dtos = holidayRequestService.getSpecificRequestsForManagers(HolidayRequest.PENDING_STATUS);
		holidayRequestService.addManagerCommentary(dtos.get(0).id, MANAGER_COMMENT);
	}
	
	@Test
	public void denyRequestTest(){
		this.createUserRoleForTest();
		this.createManagerForTest();
		UserDTO user1DTO = this.createUser1ForTest();
		
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user1DTO);
		userService.updateManager(USERNAME_MANAGER, managed);
		
		HolidayDTO type = this.createHolidayTypeForTest();
		
		authenticateAs(USERNAME_USER1);
		this.createHolidayRequestForUser(user1DTO.id, type.id);

		authenticateAs(USERNAME_MANAGER);
		List<HolidayRequestDTO> dtos = holidayRequestService.getSpecificRequestsForManagers(HolidayRequest.PENDING_STATUS);
		assertEquals(1, dtos.size());
		
		holidayRequestService.denyRequest(dtos.get(0).id);
		dtos = holidayRequestService.getSpecificRequestsForManagers(HolidayRequest.PENDING_STATUS);
		assertEquals(0, dtos.size());
		
		authenticateAs(USERNAME_USER1);
		List<HolidayRequestDTO> deniedDtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.DENIED_STATUS);
		assertEquals(1, deniedDtos.size());
	}
	
	@Test(expected = UnauthorizedException.class)
	public void denyRequestWhenNoManagerTest(){
		this.createUserRoleForTest();
		UserDTO user1DTO = this.createUser1ForTest();
		
		HolidayDTO type = this.createHolidayTypeForTest();
		
		authenticateAs(USERNAME_USER1);
		this.createHolidayRequestForUser(user1DTO.id, type.id);

		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.PENDING_STATUS);
		
		holidayRequestService.denyRequest(dtos.get(0).id);
	}
	
	@Test
	public void cancelRequestTest(){
		this.createUserRoleForTest();
		UserDTO user1DTO = this.createUser1ForTest();
		
		HolidayDTO type = this.createHolidayTypeForTest();
		
		authenticateAs(USERNAME_USER1);
		this.createHolidayRequestForUser(user1DTO.id, type.id);

		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.PENDING_STATUS);
		assertEquals(1, dtos.size());
		
		holidayRequestService.cancelRequest(dtos.get(0).id);
		
		dtos = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.PENDING_STATUS);
		dtos.addAll(holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.DENIED_STATUS));
		dtos.addAll(holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.ACCEPTED_STATUS));
		assertEquals(0, dtos.size());
	}
	
	@Test
	public void getCalendarIntervalTest(){
		this.createUserRoleForTest();
		this.createUser1ForTest();
		authenticateAs(USERNAME_USER1);
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		CalendarRequestDTO calendar = holidayRequestService.getCalendarRequest(fmt.parseDateTime(DATE3), fmt.parseDateTime(DATE5));
		// Verify there is 3 different weeks in the interval
		assertEquals(3, calendar.nbWeeks);
		// 01/01/2012 is the Sunday of the 52th week of 2011
		assertEquals(1, calendar.startWeek);
		// Expect 20 for 4 weeks * 5 days (week end are not considered !)
		assertEquals(15, calendar.days.size());
	}
	
	@Test
	public void getCalendarIntervalOnTwoYearsTest(){
		this.createUserRoleForTest();
		this.createUser1ForTest();
		authenticateAs(USERNAME_USER1);
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		CalendarRequestDTO calendar = holidayRequestService.getCalendarRequest(fmt.parseDateTime(DATE4), fmt.parseDateTime(DATE5));
		// Verify there is 4 different weeks in the interval
		assertEquals(4, calendar.nbWeeks);
		// 30/12/2011 is the Friday of the 52th week of 2011
		assertEquals(52, calendar.startWeek);
		// Expect 20 for 4 weeks * 5 days (week end are not considered !)
		assertEquals(20, calendar.days.size());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getCalendarWithWrongArgumentsTest(){
		this.createUserRoleForTest();
		this.createUser1ForTest();
		authenticateAs(USERNAME_USER1);
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		holidayRequestService.getCalendarRequest(fmt.parseDateTime(DATE5), fmt.parseDateTime(DATE4));
	}
	
}
