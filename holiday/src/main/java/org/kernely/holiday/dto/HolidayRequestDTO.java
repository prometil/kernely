package org.kernely.holiday.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.kernely.core.dto.UserDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;

/**
 * Dto for holiday request 
 */
@XmlRootElement
public class HolidayRequestDTO implements Comparable<HolidayRequestDTO> {
	private final static String dateFormat = "MM/dd/yyyy";

	/**
	 * The id of the holiday request DTO 
	 */
	public long id;
	
	/**
	 * True if we have the possibility to cancel this request;
	 */
	public boolean cancelable;
	
	/**
	 * The begin date of the request
	 */
	public Date beginDate;
	
	/**
	 * The end date of the request
	 */
	public Date endDate;
	
	/**
	 * The begin date stringified of holiday
	 */
	public String beginDateString;
	
	/**
	 * The end date stringified of holiday
	 */
	public String endDateString;
	
	/**
	 * The status of the request
	 */
	public int status;
	
	/**
	 * The comment of the request
	 */
	public String requesterComment;
	
	/**
	 * The username of the requester
	 */
	public String user;
	
	/**
	 * The manager who post the comment
	 */
	public String manager;
	
	/**
	 * The comment of the manager
	 */
	public String managerComment;
	
	/**
	 * The list of holiday details
	 */
	public List<HolidayDetailDTO> details = new ArrayList<HolidayDetailDTO>();
	
	/**
	 * The DTO associated to the owner of this request
	 */
	public UserDTO userDTO;
	
	/**
	 * Default constructor 
	 */
	public HolidayRequestDTO(){
		
	}
	
	/**
	 * The constructor 
	 * @param request the model of HolidayRequest
	 */
	public HolidayRequestDTO(HolidayRequest request){
		this.id = request.getId();
		this.beginDate = request.getBeginDate();
		this.endDate = request.getEndDate();
		this.beginDateString = new DateTime(request.getBeginDate()).toString(dateFormat);
		this.endDateString = new DateTime(request.getEndDate()).toString(dateFormat);
		this.status = request.getStatus();
		this.requesterComment = request.getRequesterComment();
		this.user = request.getUser().getUsername();
		this.userDTO = new UserDTO(request.getUser());
		if (request.getManager() != null){
			this.manager = request.getManager().getUsername();
		}
		this.managerComment = request.getManagerComment(); 
		for(HolidayRequestDetail hd : request.getDetails()){
			this.details.add(new HolidayDetailDTO(hd));
		}
	}
	
	/** 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		HolidayRequestDTO other = (HolidayRequestDTO) obj;
		if (id != other.id){
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(HolidayRequestDTO o) {
		DateTime beginThis = new DateTime(this.beginDate);
		DateTime beginOther = new DateTime(o.beginDate);
		if(beginThis.isAfter(beginOther)){
			return -1;
		}
		else{
			return 1;
		}
	}
}
