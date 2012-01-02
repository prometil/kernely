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
package org.kernely.stream.dto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.stream.model.Message;

/**
 * The message DTO
 * @author b.grandperret
 *
 */
@XmlRootElement
public class StreamMessageDTO {

	private static final int HOUR_IN_A_DAY = 24;

	private static final int MINUTES_OR_SECONDS = 60;

	private static final int UNITY_1000 = 1000;

	private static final int WEEK = 604800000;

	private static final int DAY = 86400000;

	private static final int HOUR = 3600000;

	private static final int MINUTE = 60000;

	/**
	 * The id of the message
	 */
	public int id;

	/**
	 *  The id of the stream containing this message
	 */
	public int streamId;

	/**
	 * The name of the stream
	 */
	public String streamName;

	/**
	 * Thr author of the message
	 */
	public String author;

	/**
	 * the stream message DTO
	 */
	public String message;

	/**
	 * the message date
	 */
	public Date date;

	/**
	 * time to display the message 	
	 */
	public String timeToDisplay;
	
	/**
	 * The number of comments
	 */
	public int nbComments;

	/**
	 * Can the curent user delete this message?
	 */
	public boolean deletion;

	/**
	 * The message stream constructor
	 */
	public StreamMessageDTO() {

	}

	/**
	 * The message stream
	 * 
	 * @param pMessage
	 *            the message
	 */
	public StreamMessageDTO(Message pMessage) {
		this.id = pMessage.getId();
		this.message = pMessage.getContent();
		this.date = pMessage.getDate();
		this.streamId = pMessage.getStream().getId();
		this.streamName = pMessage.getStream().getTitle();
		String fullname = pMessage.getUser().getUserDetails().getFirstname() + " " + pMessage.getUser().getUserDetails().getName() + " ("
				+ pMessage.getUser().getUsername() + ")";

		this.author = fullname;
		if (pMessage.getComments() != null) {
			this.nbComments = pMessage.getComments().size();
		} else {
			this.nbComments = 0;
		}

		this.determinateTime();
	}

	/**
	 * Determinate the time when the message was posted
	 */
	private void determinateTime() {
		// Date
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		Calendar date1 = new GregorianCalendar();
		date1.setTime(this.date);
		Calendar date2 = new GregorianCalendar();
		long date1long = date1.getTimeInMillis();
		long date2long = date2.getTimeInMillis();

		// Difference between the two dates
		long timebetweend1d2 = date2long - date1long;
		// less than one minute
		if (timebetweend1d2 < MINUTE) {
			timeToDisplay = "a few seconds ago";
		} else {
			// less than one hour
			if (timebetweend1d2 < HOUR) {
				long nbMin = timebetweend1d2 / UNITY_1000 / MINUTES_OR_SECONDS;
				timeToDisplay = nbMin + " minutes ago";
			} else {
				// less than one day
				if (timebetweend1d2 < DAY) {
					long nbHour = timebetweend1d2 / UNITY_1000 / MINUTES_OR_SECONDS / MINUTES_OR_SECONDS;
					timeToDisplay = nbHour + " hours ago";
				} else {
					// less than one week
					if (timebetweend1d2 < WEEK) {
						long nbDays = timebetweend1d2 / UNITY_1000 / MINUTES_OR_SECONDS / MINUTES_OR_SECONDS / HOUR_IN_A_DAY;
						timeToDisplay = nbDays + " days ago";
					} else {
						timeToDisplay = dateFormat.format(this.date);
					}
				}
			}
		}

	}

}
