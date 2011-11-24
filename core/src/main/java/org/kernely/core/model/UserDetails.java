package org.kernely.core.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;

@Entity
@Table(name = "kernely_user_details")
public class UserDetails extends AbstractModel{
	 @Id
     @GeneratedValue(strategy = GenerationType.AUTO)
	 private int id_user_detail;
		private String name;
		private String firstname;
		private String mail;
		private String image;
		private String adress;
		private String zip;
		private String city;
		private String homephone;
		private String mobilephone;
		private String businessphone;
		private Date birth;
		private String nationality;
		private String ssn;
		private Integer civility;

     
     
     
     /**
      * Retrieve the user's image
      * @return the user's image
      */
     public final String getImage() {
             return image;
     }

     
     /**
      * Set the user's image
      * @param image : the user's image
      */
     public final void setImage(String image) {
             this.image = image;
     }

     @OneToOne
     @JoinColumn(name="fk_user_id")
     private User user;
     
     /**
      * Get the user's name
      * @return the user's name
      */
     public final String getName() {
             return name;
     }

     /**
      * Set the User's name
      * @param name : the user's name
      */
     public final void setName(String name) {
             this.name = name;
     }

     /**
      * Get the user's firstname
      * @return : the user's firstname
      */
     public final String getFirstname() {
             return firstname;
     }

     /**
      * Set the user's firstname
      * @param firstname : the user's firstname
      */
     public final void setFirstname(String firstname) {
             this.firstname = firstname;
     }

     /**
      * Get the user's mail
      * @return the user's mail
      */
     public final String getMail() {
             return mail;
     }

     /**
      * Set the user's mail
      * @param mail : the user's mail
      */
     public final void setMail(String mail) {
             this.mail = mail;
     }

     /**
      * Get the userdetails' id
      * @return : the userdetails' id
      */
     public final int getId_user_detail() {
             return id_user_detail;
     }

     /**
      * Set the userdetails' id
      * @param idUserDetail : the userdetails' id
      */
     public final void setId_user_detail(int idUserDetail) {
             id_user_detail = idUserDetail;
     }

     /**
      * Get the User associated to this userDetails
      * @return : the User associated
      */
     public final User getUser() {
             return user;
     }

     /**
      * Set the User associated to this Userdetails
      * @param user : the User associated
      */
     public final void setUser(User user) {
             this.user = user;
     }
     
 	public final String getAdress() {
		return adress;
	}

	public final void setAdress(String adress) {
		this.adress = adress;
	}

	public final String getZip() {
		return zip;
	}

	public final void setZip(String zip) {
		this.zip = zip;
	}

	public final String getCity() {
		return city;
	}

	public final void setCity(String city) {
		this.city = city;
	}

	public final String getHomephone() {
		return homephone;
	}

	public final void setHomephone(String homephone) {
		this.homephone = homephone;
	}

	public final String getMobilephone() {
		return mobilephone;
	}

	public final void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public final String getBusinessphone() {
		return businessphone;
	}

	public final void setBusinessphone(String businessphone) {
		this.businessphone = businessphone;
	}

	public final Date getBirth() {
		return birth;
	}

	public final void setBirth(Date birth) {
		this.birth = birth;
	}

	public final String getNationality() {
		return nationality;
	}

	public final void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public final String getSsn() {
		return ssn;
	}

	public final void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public final Integer getCivility(){
		return this.civility;
	}
	
	public final void setCivility(Integer civility){
		this.civility=civility;
	}
}
