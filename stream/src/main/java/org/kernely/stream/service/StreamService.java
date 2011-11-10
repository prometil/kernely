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
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.event.UserCreationEvent;
import org.kernely.core.service.mail.Mailer;
import org.kernely.stream.dto.StreamDTO;
import org.kernely.stream.dto.StreamMessageDTO;
<<<<<<< HEAD
import org.kernely.stream.model.Message;
import org.kernely.stream.model.Stream;
=======
import org.kernely.stream.model.StreamMessage;
import org.quartz.Scheduler;
>>>>>>> b32140e8f60de95b42a4067311d87bbc122ecaa3
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;


/**
 * 
 */
@Singleton
public class StreamService {
	
<<<<<<< HEAD
	private static final Logger log = LoggerFactory.getLogger(StreamService.class);

=======
	
>>>>>>> b32140e8f60de95b42a4067311d87bbc122ecaa3
	@Inject
	Provider<EntityManager> em;
	
	@Inject
	private Mailer mailService;
	


	
	/**
	 * Add a message to the database in a stream.
	 * 
	 * @return the created message
	 */
	@Transactional
	public StreamMessageDTO addMessage(String pMessage, long streamId) {
		if (pMessage==null){
			throw new IllegalArgumentException("Message cannot be null ");
		}
		if ("".equals(pMessage)){
			throw new IllegalArgumentException("Message cannot be empty ");
		}
		Message message = new Message();
		
		message.setStream(getStreamModel(streamId));
		
		message.setContent(pMessage);
		em.get().persist(message);
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
<<<<<<< HEAD
		
		Query query = em.get().createQuery("SELECT m FROM Message m order by m.date");
		List<Message> messages = (List<Message>) query.getResultList();

=======
		Query query = em.get().createQuery("SELECT m FROM StreamMessage m order by m.date");
		List<StreamMessage> messages = (List<StreamMessage>) query.getResultList();
>>>>>>> b32140e8f60de95b42a4067311d87bbc122ecaa3
		List<StreamMessageDTO> messageDtos = new ArrayList<StreamMessageDTO>();
		log.debug("Found {} messages", messages.size());
	  	for(Message message: messages){
			
			StreamMessageDTO streamMessageDTO = new StreamMessageDTO(message);
			log.debug("Returned message <message: {}, date:{}>", streamMessageDTO.message, streamMessageDTO.date);
			messageDtos.add(streamMessageDTO);
		}
		return messageDtos;
	}
	

	/**
	 * Returns a stream DTO by its id.
	 * 
	 * @return the stream
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public StreamDTO getStream(long stream_id) {
		Stream stream = getStreamModel(stream_id);
		StreamDTO dto = new StreamDTO();
		dto.setId(stream.getId());
		dto.setTitle(stream.getTitle());
		dto.setMessages(getMessages());
		return dto;
	}
	
	/**
	 * Returns a stream by its id.
	 * 
	 * @return the stream
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	private Stream getStreamModel(long stream_id) {
		Query query = em.get().createQuery("SELECT s FROM Stream s WHERE id="+stream_id);
		Stream stream = (Stream) query.getSingleResult();
		log.debug("Found stream titled: {}", stream.getTitle());
		return stream;
	}

	/**
	 * Create a new stream.
	 * @param title The title of this stream.
	 * @param category The category of this stream (use Stream class constants).
	 * @return the unique id of the stream.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void createStream(String title, String category) {
		Stream newStream = new Stream();
		newStream.setTitle(title);
		newStream.setCategory(category);
		em.get().persist(newStream);
	}
	
	/**
	 * Get a stream by it's name and categry.
	 * @param title The title of this stream.
	 * @param category The category of this stream (use Stream class constants).
	 * @return the Stream DTO.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public StreamDTO getStream(String title, String category) {
		Query query = em.get().createQuery("SELECT s FROM Stream s WHERE title='"+title+"' AND category='"+category+"'");
		Stream stream = (Stream) query.getSingleResult();
		log.debug("Found stream: {}", stream.getTitle());
		StreamDTO dto = new StreamDTO(stream);
		return dto;
	}
}
