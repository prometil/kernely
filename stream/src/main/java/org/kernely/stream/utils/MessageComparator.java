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
package org.kernely.stream.utils;

import java.io.Serializable;
import java.util.Comparator;

import org.kernely.stream.model.Message;

/**
 * The comparator for Messages, to order them by ID.
 */
public class MessageComparator implements Comparator<Message>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2540722370197723628L;

	/**
	 * Compares two messages.
	 * 
	 * @param message1
	 *            A message.
	 * @param message2
	 *            Another message.
	 * @return a negative integer if message1 < message 2, a positive integer if
	 *         message1 > message 2, 0 if the two messages are the same.
	 */
	public final int compare(Message message1, Message message2) {
		return (int) (message2.getId() - message1.getId());
	}

}
