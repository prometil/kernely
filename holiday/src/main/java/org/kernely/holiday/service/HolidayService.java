/*
Copyright 2011 Prometil SARL

This file is part of Kernely.

Kernely is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Kernely is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Kernely.
If not, see <http://www.gnu.org/licenses/>.
 */

package org.kernely.holiday.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Group;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayUpdateRequestDTO;
import org.kernely.holiday.model.Holiday;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * 
 * @author b.grandperret
 *
 */
@Singleton
public class HolidayService extends AbstractService {

	/**
	 * Gets the lists of all groups contained in the database.
	 * 
	 * @return the list of all groups contained in the database.
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public List<HolidayDTO> getAllHoliday() {
		Query query = em.get().createQuery("SELECT e FROM Holiday e");
		List<Holiday> collection = (List<Holiday>) query.getResultList();
		List<HolidayDTO> dtos = new ArrayList<HolidayDTO>();
		for (Holiday holiday : collection) {

			dtos.add(new HolidayDTO(holiday.getType(), holiday.getFrequency(),
					holiday.getId()));
		}
		return dtos;
	}

	/**
	 * Delete an existing holiday in database
	 * 
	 * @param id
	 *            The id of the group to delete
	 */
	@Transactional
	public void deleteHoliday(long id) {
		Holiday holiday = em.get().find(Holiday.class, id);
		em.get().remove(holiday);
	}


	/**
	 * Create a new Holiday in database
	 * @param request
	 * 	
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void createHoliday(HolidayCreationRequestDTO request) {
		if(request==null){
			throw new IllegalArgumentException("Request cannot be null ");
		}
		
		if(request.type==null){
			throw new IllegalArgumentException("holiday type cannot be null ");
		}
		if(request.frequency<0){
			throw new IllegalArgumentException("holiday frequency cannot be under 0 ");
		}
		if("".equals(request.type.trim())){
			throw new IllegalArgumentException("holiday type cannot be space character only ");
		}
		String type=request.type; 
		Query verifExist = em.get().createQuery("SELECT g FROM Holiday g WHERE type=:type");
		verifExist.setParameter("type", type);
		List<Holiday> list = (List<Holiday>)verifExist.getResultList();
		if(!list.isEmpty()){
			throw new IllegalArgumentException("Another holiday with this name already exists");
		}
		
		Holiday holiday = new Holiday();
		holiday.setType(request.type.trim());
		holiday.setFrequency(request.frequency);
		em.get().persist(holiday);
	}
	
	/**
	 * Update an existing holiday in database
	 * @param request
	 * 			The request, containing group name and id of the needed group
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void updateHoliday(HolidayUpdateRequestDTO request) {
		if(request==null){
			throw new IllegalArgumentException("Request cannot be null ");
		}
		
		if(request.type==null){
			throw new IllegalArgumentException("holiday cannot be null ");
		}
		if(request.frequency<0){
			throw new IllegalArgumentException("holiday frequency cannot be under 0 ");
		}
		if("".equals(request.type.trim())){
			throw new IllegalArgumentException("holiday  type  cannot be space character only ");
		}
		String type = request.type;
		long id = request.id;
		int frequency = request.frequency;
		Query verifExist = em.get().createQuery("SELECT g FROM Holiday g WHERE type=:type AND id=:id AND frequency=:frequency");
		verifExist.setParameter("type",type);
		verifExist.setParameter("id", id);
		verifExist.setParameter("frequency", frequency);
		List<Holiday> list = (List<Holiday>)verifExist.getResultList();
		if(!list.isEmpty()){
			throw new IllegalArgumentException("Another holiday  with this type already exists");
		}

		Holiday holiday = em.get().find(Holiday.class, request.id);
		holiday.setType(request.type);
		holiday.setFrequency(request.frequency);
		em.get().merge(holiday);
		
	}
}