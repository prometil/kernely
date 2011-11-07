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
package org.kernely.stream.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.service.mail.Mailer;
import org.kernely.stream.dto.StreamMessageDTO;
import org.kernely.stream.model.StreamMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;


/**
 * 
 */
@Singleton
public class StreamService {
	
	@Inject
	EntityManager em;
	
	@Inject
	private Mailer mailService;
	
	private static final Logger log = LoggerFactory.getLogger(StreamService.class);
	
	/**
	 * Add a message to the database
	 * 
	 * @return the created message
	 */
	@Transactional
	public StreamMessageDTO addMessage(String pMessage) {
		if (pMessage==null){
			throw new IllegalArgumentException("Message cannot be null ");
		}
		if ("".equals(pMessage)){
			throw new IllegalArgumentException("Message cannot be empty ");
		}
		StreamMessage message = new StreamMessage();
		message.setMessage(pMessage);
		em.persist(message);
		//mailService.create("/templates/gsp/mail.gsp").subject("This is a test mail").to("breton.gy@gmail.com").send();
		return new StreamMessageDTO(message);
	}

	/**
	 * Returns the list of messages
	 * 
	 * @return the list of messages
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<StreamMessageDTO> getMessages() {
		
		Query query = em.createQuery("SELECT m FROM StreamMessage m order by m.date");
		List<StreamMessage> messages = (List<StreamMessage>) query.getResultList();
		List<StreamMessageDTO> messageDtos = new ArrayList<StreamMessageDTO>();
		log.debug("Found {} messages", messages.size());
	  	for(StreamMessage message: messages){
			
			StreamMessageDTO streamMessageDTO = new StreamMessageDTO(message);
			log.debug("Returned streamMessage <message: {}, date:{}>", streamMessageDTO.message, streamMessageDTO.date);
			messageDtos.add(streamMessageDTO);
		}
		return messageDtos;

	}
}
