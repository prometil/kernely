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
package org.kernely.core.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kernely.persistence.AbstractModel;

/**
 * The user details model
 */
@Entity
@Table(name = "kernely_user_details")
public class UserDetails extends AbstractModel {
	private String name;
	private String firstname;
	private String mail;
	private String image;
	private String address;
	private String zip;
	private String city;
	private String homephone;
	private String mobilephone;
	private String businessphone;
	private Date birth;
	private String nationality;
	private String ssn;
	private Integer civility;
	private Date hire;

	/**
	 * Retrieve the user's image
	 * 
	 * @return the user's image
	 */
	public final String getImage() {
		return image;
	}

	/**
	 * Set the user's image
	 * 
	 * @param image
	 *            : the user's image
	 */
	public final void setImage(String image) {
		this.image = image;
	}

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	/**
	 * Get the user's name
	 * 
	 * @return the user's name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Set the User's name
	 * 
	 * @param name
	 *            : the user's name
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the user's firstname
	 * 
	 * @return : the user's firstname
	 */
	public final String getFirstname() {
		return firstname;
	}

	/**
	 * Set the user's firstname
	 * 
	 * @param firstname
	 *            : the user's firstname
	 */
	public final void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * Get the user's mail
	 * 
	 * @return the user's mail
	 */
	public final String getMail() {
		return mail;
	}

	/**
	 * Set the user's mail
	 * 
	 * @param mail
	 *            : the user's mail
	 */
	public final void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * Get the User associated to this userDetails
	 * 
	 * @return : the User associated
	 */
	public final User getUser() {
		return user;
	}

	/**
	 * Set the User associated to this Userdetails
	 * 
	 * @param user
	 *            : the User associated
	 */
	public final void setUser(User user) {
		this.user = user;
	}

	/**
	 * Get the adress associated to this userDetails
	 * 
	 * @return : the adress associated
	 */
	public final String getAdress() {
		return address;
	}


	/**
	 * Set the adress associated to this Userdetails
	 * 
	 * @param adress
	 *            : the adress associated
	 */
	public final void setAdress(String adress) {
		this.address = adress;
	}

	/**
	 * Get the zip associated to this userDetails
	 * 
	 * @return : the zip associated
	 */
	public final String getZip() {
		return zip;
	}


	/**
	 * Set the zip associated to this Userdetails
	 * 
	 * @param zip
	 *            : the zip associated
	 */
	public final void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * Get the city associated to this userDetails
	 * @return : the city associated
	 */
	public final String getCity() {
		return city;
	}


	/**
	 * Set the city associated to this Userdetails
	 * 
	 * @param city
	 *            : the city associated
	 */
	public final void setCity(String city) {
		this.city = city;
	}

	/**
	 * Get the homephone associated to this userDetails
	 * 
	 * @return : the homepone associated
	 */
	public final String getHomephone() {
		return homephone;
	}


	/**
	 * Set the homephone associated to this Userdetails
	 * 
	 * @param homephone
	 *            : the homepone associated
	 */
	public final void setHomephone(String homephone) {
		this.homephone = homephone;
	}

	/**
     * Get the mobilephone associated to this userDetails
     * @return : the mobilephone associated
     */
	public final String getMobilephone() {
		return mobilephone;
	}


	/**
	 * Set the mobilephone associated to this Userdetails
	 * 
	 * @param mobilephone
	 *            : the mobilephone associated
	 */
	public final void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	/**
     * gGet the business associated to this userDetails
     * @return : the adress associated
     */
	public final String getBusinessphone() {
		return businessphone;
	}


	/**
	 * Set the businesspone associated to this Userdetails
	 * 
	 * @param businessphone
	 *            : the businessphone associated
	 */
	public final void setBusinessphone(String businessphone) {
		this.businessphone = businessphone;
	}

	/**
     * Get the birth associated to this userDetails
     * @return : the birth associated
     */
	public final Date getBirth() {
		return birth;
	}


	/**
	 * Set the birth associated to this Userdetails
	 * 
	 * @param birth
	 *            : the birth associated
	 */
	public final void setBirth(Date birth) {
		this.birth = birth;
	}

	/**
     * Get the nationality associated to this userDetails
     * @return : the  nationality associated
     */
	public final String getNationality() {
		return nationality;
	}


	/**
	 * Set the nationality associated to this Userdetails
	 * 
	 * @param nationality
	 *            : the nationality associated
	 */
	public final void setNationality(String nationality) {
		this.nationality = nationality;
	}

	/**
     * Get the security social number associated to this userDetails
     * @return : the ssn associated
     */
	public final String getSsn() {
		return ssn;
	}

	public final void setSsn(String ssn) {
		this.ssn = ssn;
	}

	/**
     * Get the civility associated to this userDetails
     * @return : the civility associated
     */
	public final Integer getCivility() {
		return this.civility;
	}


	/**
	 * Set the civility associated to this Userdetails
	 * 
	 * @param civility
	 *            : the civility associated
	 */
	public final void setCivility(Integer civility) {
		this.civility = civility;
	}

	/**
	 * @return the hire
	 */
	public Date getHire() {
		return hire;
	}

	/**
	 * @param hire the hire to set
	 */
	public void setHire(Date hire) {
		this.hire = hire;
	}
}
