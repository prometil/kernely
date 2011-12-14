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

@XmlRootElement
public class StreamMessageDTO {

	public long id;

	// the id of the stream containing this message
	public long streamId;

	public String streamName;

	public String author;

	// the stream message DTO
	public String message;

	// the message date
	public Date date;

	public String timeToDisplay;

	public int nbComments;

	// can the curent user delete this message?
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
		if (timebetweend1d2 < 60000) {
			timeToDisplay = "a few seconds ago";
		} else {
			// less than one hour
			if (timebetweend1d2 < 3600000) {
				long nbMin = timebetweend1d2 / 1000 / 60;
				timeToDisplay = nbMin + " minutes ago";
			} else {
				// less than one day
				if (timebetweend1d2 < 86400000) {
					long nbHour = timebetweend1d2 / 1000 / 60 / 60;
					timeToDisplay = nbHour + " hours ago";
				} else {
					// less than one week
					if (timebetweend1d2 < 604800000) {
						long nbDays = timebetweend1d2 / 1000 / 60 / 60 / 24;
						timeToDisplay = nbDays + " days ago";
					} else {
						timeToDisplay = dateFormat.format(this.date);
					}
				}
			}
		}

	}

}
