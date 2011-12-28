package org.kernely.holiday.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayType;

import com.google.inject.Inject;

public class HolidayRequestServiceTest extends AbstractServiceTest{

	private static final String DATE1 = "01/01/2012";
	private static final String DATE2 = "01/03/2012";
	private static final String DATE3 = "01/05/2012";
	private static final String R_COMMENT = "I want my holidays !";
	private static final String USERNAME = "test_username";
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

	private UserDTO createUserForTest(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);


		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME;
		request.password = USERNAME;
		userService.createUser(request);
		return userService.getAllUsers().get(0);
	}


	private HolidayDTO createHolidayTypeForTest(){
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		holidayService.createHoliday(request);
		return holidayService.getAllHoliday().get(0);
	}

	@Test
	public void registerRequestTest(){
		HolidayDTO hdto = this.createHolidayTypeForTest();

		UserDTO udto = this.createUserForTest();
		authenticateAs(USERNAME);

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

		UserDTO udto = this.createUserForTest();
		authenticateAs(USERNAME);

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

		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
		assertEquals(1, dtos.size());
	}

	@Test
	public void acceptRequestTest(){
		HolidayDTO hdto = this.createHolidayTypeForTest();

		UserDTO udto = this.createUserForTest();
		authenticateAs(USERNAME);

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

		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
		assertEquals(1, dtos.size());
		
		holidayRequestService.acceptRequest(dtos.get(0).id);
		dtos = holidayRequestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
		assertEquals(0, dtos.size());
		
		List<HolidayRequestDTO> acceptedDtos = holidayRequestService.getAllRequestsWithStatus(HolidayRequest.ACCEPTED_STATUS);
		assertEquals(1, acceptedDtos.size());
	}
	
	@Test
	public void denyRequestTest(){
		HolidayDTO hdto = this.createHolidayTypeForTest();

		UserDTO udto = this.createUserForTest();
		authenticateAs(USERNAME);

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

		List<HolidayRequestDTO> dtos = holidayRequestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
		assertEquals(1, dtos.size());
		
		holidayRequestService.denyRequest(dtos.get(0).id);
		dtos = holidayRequestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
		assertEquals(0, dtos.size());
		
		List<HolidayRequestDTO> deniedDtos = holidayRequestService.getAllRequestsWithStatus(HolidayRequest.DENIED_STATUS);
		assertEquals(1, deniedDtos.size());
	}
}
