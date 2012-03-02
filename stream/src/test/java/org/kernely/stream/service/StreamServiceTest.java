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
package org.kernely.stream.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsUpdateRequestDTO;
import org.kernely.core.event.UserCreationEvent;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.PermissionService;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.stream.UserEventHandler;
import org.kernely.stream.dto.StreamCreationRequestDTO;
import org.kernely.stream.dto.StreamDTO;
import org.kernely.stream.dto.StreamMessageDTO;
import org.kernely.stream.model.Stream;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

/**
 * 
 * @author g.breton
 * 
 */
public class StreamServiceTest extends AbstractServiceTest {

	private static final String USERNAME = "test_username";
	private static final String USERNAME2 = "test2_username2";
	private static final String STREAM = "test_stream";
	private static final String STREAM2 = "test_stream2";
	private static final String MESSAGE = "test_messages";
	private static final String MESSAGE2 = "test_messages_two";
	private static final String COMMENT = "test_comments";
	private static final String COMMENT2 = "test_comments_two";

	@Inject
	private StreamService streamService;

	@Inject
	private RoleService roleService;

	@Inject
	private PermissionService permissionService;

	@Inject
	private UserService userService;

	@Inject
	private EventBus bus;

	@Inject
	private UserEventHandler handler;

	private long creationOfTestUser() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME;
		request.password = USERNAME;
		request.firstname = USERNAME;
		request.lastname = USERNAME;
		UserDTO userDTO = userService.createUser(request);

		UserDetailsUpdateRequestDTO request2 = new UserDetailsUpdateRequestDTO();
		request2.email = "test@test.com";
		request2.id = userService.getUserDetails(USERNAME).id;
		userService.updateUserProfile(request2);
		return userDTO.id;
	}

	private void creationOfSecondTestUser() {
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME2;
		request.password = USERNAME2;
		request.firstname = USERNAME2;
		request.lastname = USERNAME2;
		userService.createUser(request);

		UserDetailsUpdateRequestDTO request2 = new UserDetailsUpdateRequestDTO();
		request2.email = "test2@test2.com";
		request2.id = userService.getUserDetails(USERNAME2).id;
		userService.updateUserProfile(request2);
	}
	
	@Test
	public void getStreamTest(){
		streamService.createStream(STREAM, Stream.CATEGORY_PLUGINS);
		assertEquals(STREAM, streamService.getStream(STREAM, Stream.CATEGORY_PLUGINS).title);
		assertEquals(Stream.CATEGORY_PLUGINS,streamService.getStream(STREAM, Stream.CATEGORY_PLUGINS).category);
	}

	@Test
	public void getStreamFromIdTest(){
		streamService.createStream(STREAM, Stream.CATEGORY_PLUGINS);
		StreamDTO sdto = streamService.getAllStreams().get(0);
		assertEquals(STREAM, streamService.getStream(sdto.id).title);
		assertEquals(Stream.CATEGORY_PLUGINS,streamService.getStream(sdto.id).category);
	}

	
	@Test
	public void getNullMessages() {
		assertEquals(0, streamService.getMessages().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void addVoidMessage() {
		streamService.addMessage("", 0);
	}
	
	@Test
	public void updateStreamTest(){
		streamService.createStream(STREAM, Stream.CATEGORY_PLUGINS);
		StreamDTO createdStream = streamService.getStream(STREAM, Stream.CATEGORY_PLUGINS);
		streamService.updateStream(new StreamCreationRequestDTO(createdStream.id, STREAM2, Stream.CATEGORY_OTHERS));
		StreamDTO updatedStream = streamService.getAllStreams().get(0);
		
		assertEquals(STREAM2, updatedStream.title);
		assertEquals(Stream.CATEGORY_OTHERS, updatedStream.category);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateStreamTestNull(){
		streamService.updateStream(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateStreamTestEmty(){
		streamService.createStream(STREAM, Stream.CATEGORY_PLUGINS);
		StreamDTO createdStream = streamService.getStream(STREAM, Stream.CATEGORY_PLUGINS);
		streamService.updateStream(new StreamCreationRequestDTO(createdStream.id, "  ", Stream.CATEGORY_OTHERS));
	}

	@Test
	public void addMessageOnStreamUser() {
		this.creationOfTestUser();
		this.creationOfSecondTestUser();

		authenticateAs(USERNAME);

		streamService.createStream(STREAM, Stream.CATEGORY_USERS);
		StreamDTO createdStream = streamService.getStream(STREAM, Stream.CATEGORY_USERS);

		// Give rights to the current user
		int userId = (int) userService.getAllUsers().get(0).id;
		int user2Id = (int) userService.getAllUsers().get(1).id;
		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, createdStream.id);
		permissionService.grantPermission(user2Id, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, createdStream.id);

		StreamMessageDTO message = streamService.addMessage(MESSAGE, createdStream.id);
		assertNotNull(message);
		assertEquals(MESSAGE, message.message);
		assertEquals(streamService.getMessages().size(), 1);
	}

	@Test
	public void addMessageOnOtherStream() {
		this.creationOfTestUser();
		this.creationOfSecondTestUser();

		authenticateAs(USERNAME);

		streamService.createStream(STREAM, Stream.CATEGORY_PLUGINS);
		StreamDTO createdStream = streamService.getStream(STREAM, Stream.CATEGORY_PLUGINS);

		// Give rights to the current user
		int userId = (int) userService.getAllUsers().get(0).id;
		int user2Id = (int) userService.getAllUsers().get(1).id;
		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, createdStream.id);
		permissionService.grantPermission(user2Id, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, createdStream.id);

		StreamMessageDTO message = streamService.addMessage(MESSAGE, createdStream.id);
		assertNotNull(message);
		assertEquals(MESSAGE, message.message);
		assertEquals(streamService.getMessages().size(), 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNullMessage() {
		streamService.addMessage(null, 0);
	}

	@Test
	public void getNullMessages2() {
		assertEquals(0, streamService.getMessages().size());
	}

	@Test
	public void noMessages() {
		assertEquals(0, streamService.getMessages().size());
	}

	@Test
	public void handlingEvent() {
		bus.register(handler);
		bus.post(new UserCreationEvent(1, USERNAME));

		StreamDTO dto = streamService.getStream("Stream of " + USERNAME, Stream.CATEGORY_USERS);
		assertNotNull(dto);
	}

	// Tests on streams

	@Test
	public void getAllStreams() {
		streamService.createStream(STREAM + "1", Stream.CATEGORY_OTHERS);
		streamService.createStream(STREAM + "2", Stream.CATEGORY_OTHERS);
		streamService.createStream(STREAM + "3", Stream.CATEGORY_OTHERS);
		assertEquals(streamService.getAllStreams().size(), 3);
		assertNotNull(streamService.getStream(STREAM + "1", Stream.CATEGORY_OTHERS));
		assertNotNull(streamService.getStream(STREAM + "2", Stream.CATEGORY_OTHERS));
		assertNotNull(streamService.getStream(STREAM + "3", Stream.CATEGORY_OTHERS));
	}

	@Test
	public void streamLock() {
		streamService.createStream(STREAM, Stream.CATEGORY_OTHERS);
		StreamDTO stream = streamService.getStream(STREAM, Stream.CATEGORY_OTHERS);
		assertEquals(false, stream.locked);
		streamService.lockStream(stream.id);
		stream = streamService.getStream(STREAM, Stream.CATEGORY_OTHERS);
		assertEquals(true, stream.locked);
	}

	@Test
	public void streamUnlock() {
		streamService.createStream(STREAM, Stream.CATEGORY_OTHERS);
		StreamDTO stream = streamService.getStream(STREAM, Stream.CATEGORY_OTHERS);
		assertEquals(false, stream.locked);
		streamService.lockStream(stream.id);
		streamService.unlockStream(stream.id);
		stream = streamService.getStream(STREAM, Stream.CATEGORY_OTHERS);
		assertEquals(false, stream.locked);
	}

	@Test
	public void createStream() {
		streamService.createStream(STREAM, Stream.CATEGORY_USERS);

		assertNotNull(streamService.getStream(STREAM, Stream.CATEGORY_USERS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createExistingString() {
		streamService.createStream(STREAM, Stream.CATEGORY_OTHERS);
		streamService.createStream(STREAM, Stream.CATEGORY_OTHERS);
	}

	@Test
	public void createExistingInDifferentCategory() {
		streamService.createStream(STREAM, Stream.CATEGORY_OTHERS);
		streamService.createStream(STREAM, Stream.CATEGORY_PLUGINS);
		streamService.createStream(STREAM, Stream.CATEGORY_USERS);
		assertNotNull(streamService.getStream(STREAM, Stream.CATEGORY_OTHERS));
		assertNotNull(streamService.getStream(STREAM, Stream.CATEGORY_USERS));
		assertNotNull(streamService.getStream(STREAM, Stream.CATEGORY_PLUGINS));
	}

	@Test
	public void writeOnPluginStream() {
		this.creationOfTestUser();
		authenticateAs(USERNAME);

		// Create the stream
		streamService.createStream(STREAM, Stream.CATEGORY_PLUGINS);
		assertNotNull(streamService.getStream(STREAM, Stream.CATEGORY_PLUGINS));
		StreamDTO stream = streamService.getStream(STREAM, Stream.CATEGORY_PLUGINS);
		assertEquals(0, streamService.getMessages().size());

		// Give rights to the current user
		int userId = (int) userService.getAllUsers().get(0).id;
		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, stream.id);

		// Adds a message as the current user
		streamService.addMessage(STREAM, stream.id);
		List<StreamMessageDTO> messages = streamService.getMessages();
		StreamMessageDTO message = messages.get(0);
		assertEquals(message.message, STREAM);
	}

	@Test
	public void getCurrentUserStreams() {
		this.creationOfTestUser();

		authenticateAs(USERNAME);

		// Create a stream and give rights on it to the user
		streamService.createStream(STREAM, Stream.CATEGORY_USERS);
		int userId = (int) userService.getAllUsers().get(0).id;
		int streamId = (int) streamService.getStream(STREAM, Stream.CATEGORY_USERS).id;

		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, streamId);

		List<StreamDTO> list = streamService.getCurrentUserStreams();
		StreamDTO stream = list.get(0);
		assertEquals(STREAM, stream.title);
		assertEquals(Stream.CATEGORY_USERS, stream.category);
	}

	@Test
	public void getAllMessagesForCurrentUser() {
		this.creationOfTestUser();

		authenticateAs(USERNAME);

		// Create streams and give right to write for the user
		streamService.createStream(STREAM, Stream.CATEGORY_USERS);
		streamService.createStream(STREAM2, Stream.CATEGORY_USERS);

		int userId = (int) userService.getAllUsers().get(0).id;
		int streamId = (int) streamService.getStream(STREAM, Stream.CATEGORY_USERS).id;
		int stream2Id = (int) streamService.getStream(STREAM2, Stream.CATEGORY_USERS).id;

		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, streamId);
		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, stream2Id);

		// Add some messages to the stream
		streamService.addMessage(MESSAGE, streamId);
		streamService.addMessage(MESSAGE2, stream2Id);

		List<StreamMessageDTO> messages = streamService.getAllMessagesForCurrentUser(0);
		assertEquals(2, messages.size());
		assertEquals(MESSAGE2, messages.get(0).message);
		assertEquals(MESSAGE, messages.get(1).message);
	}

	@Test
	public void postComment(){
		this.creationOfTestUser();
		this.creationOfSecondTestUser();

		authenticateAs(USERNAME);

		streamService.createStream(STREAM, Stream.CATEGORY_USERS);
		streamService.createStream(STREAM2, Stream.CATEGORY_USERS);

		int userId = (int) userService.getAllUsers().get(0).id;
		int user2Id = (int) userService.getAllUsers().get(1).id;

		int streamId = (int) streamService.getStream(STREAM, Stream.CATEGORY_USERS).id;
		int stream2Id = (int) streamService.getStream(STREAM2, Stream.CATEGORY_USERS).id;

		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, streamId);
		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, stream2Id);

		permissionService.grantPermission(user2Id, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, streamId);
		permissionService.grantPermission(user2Id, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, stream2Id);

		// Add some messages to the stream
		StreamMessageDTO smd1 = streamService.addMessage(MESSAGE, streamId);
		StreamMessageDTO smd2 = streamService.addMessage(MESSAGE2, stream2Id);
		StreamMessageDTO comment0 = streamService.addComment(COMMENT, streamId, smd1.id);

		authenticateAs(USERNAME2);
		StreamMessageDTO comment1 = streamService.addComment(COMMENT, streamId, smd1.id);
		StreamMessageDTO comment2 = streamService.addComment(COMMENT, streamId, smd2.id);

		StreamMessageDTO smd3 = streamService.addMessage(MESSAGE, streamId);

		authenticateAs(USERNAME);
		StreamMessageDTO comment3 = streamService.addComment(COMMENT, streamId, smd3.id);

		assertNotNull(comment1);
		assertNotNull(comment2);
		assertEquals(COMMENT, comment0.message);
		assertEquals(COMMENT, comment1.message);
		assertEquals(COMMENT, comment2.message);
		assertEquals(COMMENT, comment3.message);
	}
	
	@Test
	public void addCommentDelete(){
		long id = this.creationOfTestUser();
		authenticateAs(USERNAME);
		streamService.createStream(STREAM, Stream.CATEGORY_USERS);
		long streamId = streamService.getAllStreams().get(0).id;
		permissionService.grantPermission((int)id, Stream.RIGHT_DELETE , Stream.STREAM_RESOURCE, streamId);
		
		StreamMessageDTO smd1 = streamService.addMessage(MESSAGE, streamId);
		StreamMessageDTO comment0 = streamService.addComment(COMMENT, streamId, smd1.id);
		
		assertEquals(true, comment0.deletion);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addCommentNull(){
		streamService.addComment(null, 1, 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addCommentEmpty(){
		streamService.addComment("", 1, 1);
	}
	

	@Test
	public void getAllCommentForMessage(){
		this.creationOfTestUser();

		authenticateAs(USERNAME);

		streamService.createStream(STREAM, Stream.CATEGORY_USERS);
		streamService.createStream(STREAM2, Stream.CATEGORY_USERS);

		int userId = (int) userService.getAllUsers().get(0).id;
		int streamId = (int) streamService.getStream(STREAM, Stream.CATEGORY_USERS).id;
		int stream2Id = (int) streamService.getStream(STREAM2, Stream.CATEGORY_USERS).id;

		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, streamId);
		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, stream2Id);

		// Add some messages to the stream
		StreamMessageDTO smd1 = streamService.addMessage(MESSAGE, streamId);
		StreamMessageDTO smd2 = streamService.addMessage(MESSAGE2, stream2Id);

		streamService.addComment(COMMENT, streamId, smd1.id);
		streamService.addComment(COMMENT, streamId, smd1.id);
		streamService.addComment(COMMENT2, streamId, smd1.id);

		streamService.addComment(COMMENT, streamId, smd2.id);
		streamService.addComment(COMMENT2, streamId, smd2.id);

		List<StreamMessageDTO> comments = streamService.getAllCommentsForMessage(smd1.id);
		assertEquals(3, comments.size());
		assertEquals(COMMENT, comments.get(0).message);
		assertEquals(COMMENT, comments.get(1).message);
		assertEquals(COMMENT2, comments.get(2).message);

		List<StreamMessageDTO> comments2 = streamService.getAllCommentsForMessage(smd2.id);
		assertEquals(2, comments2.size());
		assertEquals(COMMENT, comments2.get(0).message);
		assertEquals(COMMENT2, comments2.get(1).message);
	}

	@Test
	public void testUserHasNoRight() {
		this.creationOfTestUser();
		authenticateAs(USERNAME);

		assertEquals(false, streamService.currentUserHasRightsOnStream(Stream.RIGHT_READ, 1));
		assertEquals(false, streamService.currentUserHasRightsOnStream(Stream.RIGHT_WRITE, 1));
		assertEquals(false, streamService.currentUserHasRightsOnStream(Stream.RIGHT_DELETE, 1));
	}

	@Test
	public void userHasRights() {
		this.creationOfTestUser();
		authenticateAs(USERNAME);

		// Create streams and give rights
		streamService.createStream(STREAM, Stream.CATEGORY_USERS);
		streamService.createStream(STREAM2, Stream.CATEGORY_USERS);

		int userId = (int) userService.getAllUsers().get(0).id;
		int streamId = (int) streamService.getStream(STREAM, Stream.CATEGORY_USERS).id;
		int stream2Id = (int) streamService.getStream(STREAM2, Stream.CATEGORY_USERS).id;

		permissionService.grantPermission(userId, Stream.RIGHT_DELETE, Stream.STREAM_RESOURCE, streamId);
		permissionService.grantPermission(userId, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, stream2Id);

		assertEquals(true, streamService.currentUserHasRightsOnStream(Stream.RIGHT_DELETE, streamId));
		assertEquals(true, streamService.currentUserHasRightsOnStream(Stream.RIGHT_READ, stream2Id));
	}

	@Test
	public void writeOnUnexpectedStream() {
		this.creationOfTestUser();
		authenticateAs(USERNAME);

		// Create stream
		streamService.createStream(STREAM, Stream.CATEGORY_USERS);

		int userId = (int) userService.getAllUsers().get(0).id;
		int streamId = (int) streamService.getStream(STREAM, Stream.CATEGORY_USERS).id;

		permissionService.grantPermission(userId, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, streamId);

		assertEquals(null, streamService.addMessage(MESSAGE, streamId));
	}

	@Test
	public void messageDeletion(){
		this.creationOfTestUser();
		authenticateAs(USERNAME);

		// Create stream
		streamService.createStream(STREAM, Stream.CATEGORY_USERS);

		int userId = (int) userService.getAllUsers().get(0).id;
		int streamId = (int) streamService.getStream(STREAM, Stream.CATEGORY_USERS).id;

		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, streamId);

		StreamMessageDTO message = streamService.addMessage(MESSAGE, streamId);
		assertEquals(1, streamService.getMessages().size());
		streamService.deleteMessage(message.id);
		assertEquals(0, streamService.getMessages().size());
	}
	
	@Test 
	public void getCurrentNbMessageTest(){
		this.creationOfTestUser();
		authenticateAs(USERNAME);
		streamService.createStream(STREAM, Stream.CATEGORY_USERS);
		int userId = (int) userService.getAllUsers().get(0).id;
		int streamId = (int) streamService.getStream(STREAM, Stream.CATEGORY_USERS).id;
		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, streamId);
		streamService.addMessage(MESSAGE, streamId);
		assertEquals(Long.valueOf(1), streamService.getCurrentNbMessages()); 
	}
	
	@Test
	public void getCurrentNbMessage0Test(){
		this.creationOfTestUser();
		authenticateAs(USERNAME);
		streamService.createStream(STREAM, Stream.CATEGORY_USERS);
		int userId = (int) userService.getAllUsers().get(0).id;
		int streamId = (int) streamService.getStream(STREAM, Stream.CATEGORY_USERS).id;
		permissionService.grantPermission(userId, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, streamId);	
		assertEquals(Long.valueOf(0), streamService.getCurrentNbMessages());
	}
}
