package org.kernely.holiday.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDetailsUpdateRequestDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.holiday.dto.HolidayDetailCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;

import com.google.inject.Inject;

public class HolidayRequestServiceTest extends AbstractServiceTest{
	
	private static Date DATE1 = new DateTime().toDate();
	private static Date DATE2 = new DateTime().plusDays(2).toDate();
	private static Date DATE3 = new DateTime().plusDays(5).toDate();
	private static String R_COMMENT = "I want my holidays !";
	private static final String USERNAME = "test_username";

	@Inject
	private HolidayRequestService holidayRequestService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private RoleService roleService;
	
	
	
	private void creationOfTestUser() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME;
		request.password = USERNAME;
		request.firstname = USERNAME;
		request.lastname = USERNAME;
		userService.createUser(request);

		UserDetailsUpdateRequestDTO request2 = new UserDetailsUpdateRequestDTO();
		request2.email = "test@test.com";
		request2.id = userService.getUserDetails(USERNAME).id;
		userService.updateUserProfile(request2);
	}
	
	@Test
	public void getRequestCorrespondingToDetails(){
		
		this.creationOfTestUser();
		authenticateAs(USERNAME);
		
		HolidayRequestDetail detail1 = new HolidayRequestDetail();
		HolidayRequestDetail detail2 = new HolidayRequestDetail();
		HolidayRequestDetail detail3 = new HolidayRequestDetail();
		
		detail1.setDay(DATE1);
		detail2.setDay(DATE2);
		detail3.setDay(DATE3);
		
		
		List<HolidayRequestDetail> list = new ArrayList<HolidayRequestDetail>();
		list.add(detail3);
		list.add(detail1);
		list.add(detail2);
		
		HolidayRequest request = holidayRequestService.getHolidayRequestFromDetails(list);
		assertEquals(request.getBeginDate(), DATE1);
		assertEquals(request.getEndDate(), DATE3);
		assertEquals(request.getDetails().size(), 3);
	}
	
	@Test
	public void registerRequestTest(){
		this.creationOfTestUser();
		authenticateAs(USERNAME);
		
		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO3 = new HolidayDetailCreationRequestDTO();
		detailDTO1.day = DATE1;
		detailDTO2.day = DATE2;
		detailDTO3.day = DATE3;
		
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
		
		assertEquals(testDto.beginDate, DATE1);
		assertEquals(testDto.endDate, DATE3);
		assertEquals(testDto.details.size(), 3);
		assertEquals(testDto.requesterComment, R_COMMENT);
		
	}
	
}
