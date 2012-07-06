package org.kernely.holiday.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.kernely.holiday.model.HolidayDonation;

/**
 * DTO representing an holiday donation
 */
@XmlRootElement
public class HolidayDonationDTO implements Comparable<HolidayDonationDTO>{
	
	/**
	 * Comment of this Holiday donation
	 */
	public String comment;
	
	/**
	 * Id of this holiday donation
	 */
	public long id;
	
	/**
	 * Amount of this holiday donation
	 */
	public float amount;
	
	/**
	 * Id of the manager who makes this donation
	 */
	public long managerId;
	
	/**
	 * Username of the manager who makes this donation
	 */
	public String managerUsername;
	
	/**
	 * Id of the receiver of this donation
	 */
	public long receiverId;
	
	/**
	 * Username of the receiver of this donation
	 */
	public String receiverUsername;
	
	/**
	 * Id of the holiday type linked to this donation
	 */
	public long typeInstanceId;
	
	/**
	 * Name of the holiday type linked to this donation
	 */
	public String typeInstanceName;
	
	/**
	 * Date of this donation
	 */
	public Date date;
	
	/**
	 * Default constructor
	 */
	public HolidayDonationDTO(){}
	
	/**
	 * Builds a DTO from a model of HolidayDonation.
	 * @param donation The model on which will be based the DTO
	 */
	public HolidayDonationDTO(HolidayDonation donation){
		this.comment = donation.getComment();
		this.id = donation.getId();
		this.amount = donation.getAmount();
		this.managerId = donation.getManager().getId();
		this.managerUsername = donation.getManager().getUsername();
		this.receiverId = donation.getReceiver().getId();
		this.receiverUsername = donation.getReceiver().getUsername();
		this.typeInstanceId = donation.getHolidayTypeInstance().getId();
		this.typeInstanceName = donation.getHolidayTypeInstance().getName();
		this.date = donation.getDate();
	}

	@Override
	public int compareTo(HolidayDonationDTO other) {
		DateTime dateThis = new DateTime(this.date);
		DateTime dateOther = new DateTime(other.date);
		if(dateThis.isBefore(dateOther)){
			return -1;
		}
		else{
			return 1;
		}
	}
	
}
