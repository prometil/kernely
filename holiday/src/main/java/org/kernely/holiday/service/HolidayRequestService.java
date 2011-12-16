/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.kernely.holiday.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.Query;

import org.kernely.core.service.AbstractService;
import org.kernely.holiday.dto.HolidayDetailCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class HolidayRequestService extends AbstractService{

//	@Inject
//	private HolidayBalanceService balanceService;
	
	/**
	 * Construct a HolidayRequest
	 * @param details
	 * @return
	 */
	public HolidayRequest getHolidayRequestFromDetails(List<HolidayRequestDetail> details){
		TreeSet<HolidayRequestDetail> orderedDetails = new TreeSet<HolidayRequestDetail>();
		orderedDetails.addAll(details);
		
		HolidayRequest request = new HolidayRequest();
		
		request.setDetails(orderedDetails);
		request.setStatus(HolidayRequest.PENDING_STATUS);
		request.setUser(this.getAuthenticatedUserModel());
		
		request.setBeginDate(orderedDetails.first().getDay());
		request.setEndDate(orderedDetails.last().getDay());
		
		return request;
	}
	
	@Transactional
	public void registerRequestAndDetails(HolidayRequestCreationRequestDTO request){
		List<HolidayRequestDetail> detailsModels = new ArrayList<HolidayRequestDetail>();
		for(HolidayDetailCreationRequestDTO hdcr : request.details){
			HolidayRequestDetail detail = new HolidayRequestDetail();
			detail.setAm(hdcr.am);
			detail.setPm(hdcr.pm);
			detail.setDay(hdcr.day);
//			detail.setBalance(balanceService.getHolidayBalance(this.getAuthenticatedUserModel().getId(), request.typeId));
			em.get().persist(detail);
			detailsModels.add(detail);
		}
		HolidayRequest hr = this.getHolidayRequestFromDetails(detailsModels);
		hr.setRequesterComment(request.requesterComment);
		em.get().persist(hr);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getAllRequestsForCurrentUser(){
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  user=:user");
		query.setParameter("user", this.getAuthenticatedUserModel());
		List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
		List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
		
		for(HolidayRequest r : requests){
			requestsDTO.add(new HolidayRequestDTO(r));
		}
		
		return requestsDTO;
	}
}
