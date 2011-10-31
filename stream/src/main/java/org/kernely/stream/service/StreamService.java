/**
 * 
 */
package org.kernely.stream.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.hibernate.HibernateUtil;
import org.kernely.core.service.mail.MailService;
import org.kernely.stream.dto.StreamMessageDTO;
import org.kernely.stream.model.StreamMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author g.breton
 * 
 */
public class StreamService {
	
	//the hibernate util
	@Inject
	private HibernateUtil hibernateUtil;
	
	@Inject
	private MailService mailService;
	
	private static final Logger log = LoggerFactory.getLogger(StreamService.class);
	
	/**
	 * Add a message to the database
	 * 
	 * @return the created message
	 */
	public StreamMessageDTO addMessage(String pMessage) {
		StreamMessage message = new StreamMessage();
		message.setMessage(pMessage);
		EntityManager em = hibernateUtil.getEM();
		em.persist(message);
		em.getTransaction().begin();
		em.getTransaction().commit();
		em.close();
		mailService.make("/templates/gsp/mail.gsp").subject("This is a test mail").to("breton.gy@gmail.com").send();
		return new StreamMessageDTO(message);
	}

	/**
	 * Returns the list of messages
	 * 
	 * @return the list of messages
	 */
	@SuppressWarnings("unchecked")
	public List<StreamMessageDTO> getMessages() {
		
		EntityManager em = hibernateUtil.getEM();
		Query query = em.createQuery("SELECT m FROM StreamMessage m order by m.date");
		List<StreamMessage> messages = (List<StreamMessage>) query.getResultList();
		List<StreamMessageDTO> messageDtos = new ArrayList<StreamMessageDTO>();
		log.debug("Found {} messages", messages.size());
		for(StreamMessage message: messages){
			
			StreamMessageDTO streamMessageDTO = new StreamMessageDTO(message);
			log.debug("Returned streamMessage <message: {}, date:{}>", streamMessageDTO.message, streamMessageDTO.date);
			messageDtos.add(streamMessageDTO);
		}
		em.close();
		return messageDtos;

	}
}
