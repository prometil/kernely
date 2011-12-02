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
import java.util.TreeSet;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.kernely.core.dto.PermissionDTO;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.core.service.user.PermissionService;
import org.kernely.stream.dto.StreamCreationRequestDTO;
import org.kernely.stream.dto.StreamDTO;
import org.kernely.stream.dto.StreamMessageDTO;
import org.kernely.stream.model.Message;
import org.kernely.stream.model.Stream;
import org.kernely.stream.utils.MessageComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * 
 */
@Singleton
public class StreamService extends AbstractService {

	private static final Logger log = LoggerFactory.getLogger(StreamService.class);

	@Inject
	private PermissionService permissionService;

	/**
	 * Add a message to the database in a stream, the current user is the author.
	 * 
	 * @return the created message if the user can write on the stream, null otherwise
	 * @throws IllegalAccessException
	 */
	@Transactional
	public StreamMessageDTO addMessage(String pMessage, long streamId) {
		if (pMessage == null) {
			throw new IllegalArgumentException("Message cannot be null ");
		}
		if ("".equals(pMessage)) {
			throw new IllegalArgumentException("Message cannot be empty ");
		}
		if (!(currentUserHasRightsOnStream(Stream.RIGHT_WRITE, (int) streamId) || currentUserHasRightsOnStream(Stream.RIGHT_DELETE, (int) streamId))) {
			return null;
		}
		Message message = new Message();

		message.setStream(getStreamModel(streamId));
		message.setContent(pMessage);
		message.setUser(getAuthenticatedUserModel());

		em.get().persist(message);
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
		Query query = em.get().createQuery("SELECT m FROM Message m order by m.date");
		List<Message> messages = (List<Message>) query.getResultList();

		List<StreamMessageDTO> messageDtos = new ArrayList<StreamMessageDTO>();
		log.debug("Found {} messages", messages.size());
		for (Message message : messages) {

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
	@Transactional
	private Stream getStreamModel(long stream_id) {
		Query query = em.get().createQuery("SELECT s FROM Stream s WHERE id= :stream_id");
		query.setParameter("stream_id", (int) stream_id);
		Stream stream = (Stream) query.getSingleResult();
		log.debug("Found stream titled: {}", stream.getTitle());
		return stream;
	}

	/**
	 * Create a new stream.
	 * 
	 * @param title
	 *            The title of this stream.
	 * @param category
	 *            The category of this stream (use Stream class constants).
	 * @return the unique id of the stream.
	 */
	@Transactional
	public void createStream(String title, String category) {
		if (this.getStream(title, category) != null) {
			throw new IllegalArgumentException("Stream with the same title and the same category already exists.");
		}
		Stream newStream = new Stream();
		newStream.setTitle(title);
		newStream.setCategory(category);
		em.get().persist(newStream);
	}

	/**
	 * Update an existing stream in database
	 * 
	 * @param request
	 *            The request, containing title, category, and id of the stream
	 */
	@Transactional
	public void updateStream(StreamCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if ("".equals(request.name)) {
			throw new IllegalArgumentException("Stream title cannot be null ");
		}

		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("Stream title cannot be space character only ");
		}

		Stream stream = em.get().find(Stream.class, request.id);
		stream.setTitle(request.name);
		stream.setCategory(request.category);
	}

	/**
	 * Lock an existing stream in database
	 * 
	 * @param id
	 *            The id of the stream to lock.
	 */
	@Transactional
	public void lockStream(long stream_id) {
		Stream stream = getStreamModel(stream_id);
		stream.setLocked(true);
	}

	/**
	 * Lock an existing stream in database
	 * 
	 * @param id
	 *            The id of the stream to lock.
	 */
	@Transactional
	public void unlockStream(long stream_id) {
		Stream stream = getStreamModel(stream_id);
		stream.setLocked(false);
	}

	/**
	 * Get a stream by it's name and category.
	 * 
	 * @param title
	 *            The title of this stream.
	 * @param category
	 *            The category of this stream (use Stream class constants).
	 * @return the Stream DTO.
	 */
	@Transactional
	public StreamDTO getStream(String title, String category) {
		Query query = em.get().createQuery("SELECT s FROM Stream s WHERE title= :title AND category= :category");
		query.setParameter("title", title);
		query.setParameter("category", category);
		Stream result;
		try {
			result = (Stream) query.getSingleResult();
		} catch (NoResultException nre) {
			log.debug(nre.getMessage());
			return null;
		}
		log.debug("Found stream: {}", result.getTitle());
		StreamDTO dto = new StreamDTO(result);
		return dto;
	}

	/**
	 * Gets the list of all streams contained in the database.
	 * 
	 * @return the list of all streams contained in the database.
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public List<StreamDTO> getAllStreams() {
		Query query = em.get().createQuery("SELECT e FROM Stream e");
		List<Stream> collection = (List<Stream>) query.getResultList();
		List<StreamDTO> dtos = new ArrayList<StreamDTO>();
		for (Stream stream : collection) {
			dtos.add(new StreamDTO(stream));
		}
		log.debug("Found {} stream(s)", dtos.size());
		return dtos;
	}

	private List<Stream> getCurrentUserStreamModel() {
		User current = this.getAuthenticatedUserModel();
		List<PermissionDTO> permissions = permissionService.getTypeOfPermissionForOneUser(current.getId(), "streams");
		List<Stream> streams = new ArrayList<Stream>();
		for (PermissionDTO p : permissions) {
			streams.add(em.get().find(Stream.class, Integer.parseInt(p.resourceId)));
		}
		return streams;
	}

	/**
	 * Get all streams DTO for which the current user has one permission (read, write or delete).
	 * 
	 * @return a list of stream DTO. If the user has right to read, or write, or delete on a stream, the stream will be returned.
	 */
	@Transactional
	public List<StreamDTO> getCurrentUserStreams() {
		List<Stream> streams = this.getCurrentUserStreamModel();
		List<StreamDTO> streamsdto = new ArrayList<StreamDTO>();
		for (Stream s : streams) {
			streamsdto.add(new StreamDTO(s));
		}
		return streamsdto;
	}

	/**
	 * Get all messages which id in database is inferior to the flag.
	 * 
	 * @param flag
	 *            The max id of messages returned.
	 * @return 9 messages, which id is inferior to the flag passed in parameter. Messages are ordered by descendant id.
	 */
	@SuppressWarnings("unchecked")
	public List<StreamMessageDTO> getAllMessagesForCurrentUser(long flag) {
		if (flag == 0) {
			flag = (Long) em.get().createQuery("SELECT max(id) FROM Message m").getSingleResult();
			// We add 1 to flag to consider the last id too in the request with '<'
			flag++;
		}
		List<Stream> streams = this.getCurrentUserStreamModel();
		TreeSet<Message> messages = new TreeSet<Message>(new MessageComparator());
		Query query = em.get().createQuery("SELECT m FROM Message m  WHERE stream in (:streamSet) AND id < :flag ORDER BY id DESC");
		query.setParameter("streamSet", streams);
		query.setParameter("flag", flag);
		query.setMaxResults(9);

		messages.addAll((List<Message>) query.getResultList());
		List<StreamMessageDTO> messagesdto = new ArrayList<StreamMessageDTO>();
		for (Message m : messages) {
			messagesdto.add(new StreamMessageDTO(m));
		}
		return messagesdto;
	}

	/**
	 * Check if the current user has a specific right on a stream.
	 * 
	 * @param right
	 *            The right : use Stream constants.
	 * @param streamId
	 *            : The id of the stream.
	 * @return true if the user has this right, false otherwise.
	 */
	public boolean currentUserHasRightsOnStream(String right, int streamId) {
		User current = this.getAuthenticatedUserModel();
		return permissionService.userHasPermission((int) current.getId(), right, Stream.STREAM_RESOURCE, streamId);
	}

	/**
	 * Delete an existing Message in database
	 * 
	 * @param id
	 *            The id of the message to delete
	 */
	@Transactional
	public void deleteMessage(long messageId) {
		Message message = em.get().find(Message.class, messageId);
		em.get().remove(message);
	}
}
