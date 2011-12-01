package org.kernely.stream.utils;

import java.io.Serializable;
import java.util.Comparator;

import org.kernely.stream.model.Message;

/**
 * The comparator for Messages, to order them by ID.
 */
@SuppressWarnings("serial")
public class MessageComparator implements Comparator<Message>, Serializable{

        /**
         * Compares two messages.
         * @param message1 A message.
         * @param message2 Another message.
         * @return a negative integer if message1 < message 2, a positive integer if message1 > message 2, 0 if the two messages are the same.
         */
        public final int compare(Message message1, Message message2) {
                return (int) (message2.getId() - message1.getId());
        }
        
}

