/**
 * 
 */
package org.kernely.stream.model;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.kernely.core.hibernate.AbstractEntity;

/**
 * @author yak
 *
 */
@Entity
@Table(name = "stream_messages")
public class StreamMessage extends AbstractEntity {
	
	
	public StreamMessage() {
		super();
		DateTimeZone zoneUTC = DateTimeZone.UTC;
		date = new DateTime().withZone(zoneUTC).toDate();
	}


	private String message;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date  date;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	/**
	 * The method return the id.
	 * @return the long
	 */
	public long getId() {
		return id;
	}

	/**
	 * The method set the id.
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}


	/**
	 * Return the message
	 * @return the messsage
	 */
	public String getMessage() {
		
		return message;
	}
	/**
	 * THe method set the message
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * The message date
	 * @return the message date
	 */
	public Date getDate() {
		return date;
	}
	
	
	/**
	 * The message set the date
	 * @param date the date of the message
	 */
	public void setDate(Date pDate) {
		this.date = pDate;
	}
}
