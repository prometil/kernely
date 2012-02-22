package org.kernely.holiday.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.holiday.model.HolidayProfile;
import org.kernely.holiday.model.HolidayType;

/**
 * The dto for holiday profile.
 */
@XmlRootElement
public class HolidayProfileDTO {
	
	/**
	 * The unique id of the profile
	 */
	public int id;
	
	/**
	 * The name of the profile.
	 */
	public String name;

	/**
	 * Types contained in the profile
	 */
	public List<HolidayDTO> holidayTypes;

	/**
	 * Number of users associated to this profile
	 */
	public int nbUsers;
	
	/**
	 * Default constructor
	 */
	public HolidayProfileDTO(){
		
	}
	
	/**
	 * Create an holiday profile, containing holiday types id.
	 * @param id The id of the holiday profile.
	 * @param name The name of the holiday profile.
	 * @param holidayTypes The types associated to this profile.
	 * @param nbUsers The number of users associated to this profile
	 */
	public HolidayProfileDTO(int id, String name, List<HolidayDTO> holidayTypes, int nbUsers){
		this.id=id;
		this.name=name;
		this.holidayTypes = holidayTypes;
		this.nbUsers = nbUsers;
	}
	
	public HolidayProfileDTO(HolidayProfile profile){
		this.id = profile.getId();
		this.name = profile.getName();
		
		List<HolidayDTO> types = new ArrayList<HolidayDTO>();
		for (HolidayType type : profile.getHolidayTypes()){
			types.add(new HolidayDTO(type));
		}
		
		this.holidayTypes = types;
		if (profile.getUsers() != null){
			this.nbUsers = profile.getUsers().size();
		} else {
			this.nbUsers = 0;
		}
	}
}
