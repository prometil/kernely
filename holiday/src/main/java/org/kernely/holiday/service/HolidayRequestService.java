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

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.kernely.core.service.AbstractService;
import org.kernely.holiday.dto.HolidayDetailCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayBalance;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;
import org.kernely.holiday.model.HolidayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class HolidayRequestService extends AbstractService{
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

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
			detail.setBalance(getBalanceWithTypeId(hdcr.typeId));
			em.get().persist(detail);
			log.debug("Holiday request detail registered for the day {}", hdcr.day);
			detailsModels.add(detail);
		}
		HolidayRequest hr = this.getHolidayRequestFromDetails(detailsModels);
		hr.setRequesterComment(request.requesterComment);
		em.get().persist(hr);
		log.debug("Holiday Request registered !");
	}
	
	@Transactional
	private HolidayBalance getBalanceWithTypeId(int typeId){
		HolidayType type = em.get().find(HolidayType.class, typeId);
		Query balanceRequest = em.get().createQuery("SELECT b FROM HolidayBalance b WHERE user=:user AND holidayType=:type");
		balanceRequest.setParameter("user", this.getAuthenticatedUserModel());
		balanceRequest.setParameter("type", type);
		try {
			HolidayBalance balance = (HolidayBalance) balanceRequest.getSingleResult();
			log.debug("Balance found for the current user with the type id {}", typeId);
			return balance;
		} catch (NoResultException nre) {
			log.debug("There is no balance with the type id {} for the user {}", typeId , this.getAuthenticatedUserModel().getId());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<HolidayRequestDTO> getAllRequestsForCurrentUser(){
		Query query = em.get().createQuery("SELECT  r from HolidayRequest r WHERE  user=:user");
		query.setParameter("user", this.getAuthenticatedUserModel());
		try{
			List<HolidayRequest> requests = (List<HolidayRequest>) query.getResultList();
			List<HolidayRequestDTO> requestsDTO = new ArrayList<HolidayRequestDTO>();
			for(HolidayRequest r : requests){
				requestsDTO.add(new HolidayRequestDTO(r));
			}

			return requestsDTO;
		}
		catch(NoResultException e){
			log.debug("There is no holiday request for current user");
			return null;
		}
	}
}
