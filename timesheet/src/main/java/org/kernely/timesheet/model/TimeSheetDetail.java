package org.kernely.timesheet.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;

/**
 * TimeSheetDetail model
 * 
 */
@Entity
@Table(name = "kernely_project")
public class TimeSheetDetail extends AbstractModel {
	
}
