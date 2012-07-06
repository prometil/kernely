package org.kernely.holiday.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.migrator.Command;
import org.kernely.migrator.CreateTable;
import org.kernely.migrator.DataBaseConstants;
import org.kernely.migrator.Migration;
import org.kernely.migrator.RawSql;

/**
 * Holiday migration script
 */
public class Migration01 extends Migration {
	/**
	 * constructor 
	 */
	public Migration01() {
		super("0.1");
	}

	/**
	 * migration script
	 * @return the list of command
	 */
	@Override
	public List<Command> getList() {
		ArrayList<Command> commands = new ArrayList<Command>();

		CreateTable holidayProfile = CreateTable.name("kernely_holiday_profile");
		holidayProfile.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		holidayProfile.column("name", DataBaseConstants.VARCHAR_50);
		
		commands.add(holidayProfile);
		
		CreateTable holidayProfileUsers = CreateTable.name("kernely_holiday_profile_users");
		holidayProfileUsers.column("holiday_profile_id", DataBaseConstants.LONG_NOT_NULL);
		holidayProfileUsers.column("user_id", DataBaseConstants.LONG_NOT_NULL);

		RawSql holidayProfileForeignKey = new RawSql("ALTER TABLE kernely_holiday_profile_users ADD CONSTRAINT fk_holiday_profile FOREIGN KEY (holiday_profile_id) REFERENCES kernely_holiday_profile (id)");
		RawSql userForeignKey = new RawSql("ALTER TABLE kernely_holiday_profile_users ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES kernely_user (id)");
		RawSql tableProfilePk = new RawSql("ALTER TABLE kernely_holiday_profile_users ADD PRIMARY KEY (holiday_profile_id, user_id)");
		
		
		commands.add(holidayProfileUsers);
		commands.add(holidayProfileForeignKey);
		commands.add(tableProfilePk);
		commands.add(userForeignKey);
		
		CreateTable holidayTypeInstance = CreateTable.name("kernely_holiday_type_instance");
		holidayTypeInstance.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		holidayTypeInstance.column("name", DataBaseConstants.VARCHAR_50);
		holidayTypeInstance.column("color", DataBaseConstants.VARCHAR_10);
		holidayTypeInstance.column("anticipated", DataBaseConstants.BOOLEAN_DEFAULT_FALSE);
		holidayTypeInstance.column("unlimited", DataBaseConstants.BOOLEAN_DEFAULT_FALSE);
		holidayTypeInstance.column("quantity", DataBaseConstants.INT);
		holidayTypeInstance.column("period_unit", DataBaseConstants.INT);

		commands.add(holidayTypeInstance);
		
		CreateTable holidayType = CreateTable.name("kernely_holiday_type");
		holidayType.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		holidayType.column("name", DataBaseConstants.VARCHAR_50);
		holidayType.column("unlimited", DataBaseConstants.BOOLEAN_DEFAULT_FALSE);
		holidayType.column("quantity", DataBaseConstants.INT);
		holidayType.column("period_unit", DataBaseConstants.INT);
		holidayType.column("effective_month", DataBaseConstants.INT);
		holidayType.column("anticipated", DataBaseConstants.BOOLEAN_DEFAULT_FALSE);
		holidayType.column("color", DataBaseConstants.VARCHAR_10);
		holidayType.column("holiday_profile_id", DataBaseConstants.LONG);
		holidayType.column("current_instance", DataBaseConstants.LONG_NOT_NULL);
		holidayType.column("next_instance", DataBaseConstants.LONG_NOT_NULL);

		RawSql holidayTypeForeignKey = new RawSql("ALTER TABLE kernely_holiday_type ADD CONSTRAINT fk_holiday_profile FOREIGN KEY (holiday_profile_id) REFERENCES kernely_holiday_profile (id)");
		RawSql holidayTypeInstanceForeignKey = new RawSql("ALTER TABLE kernely_holiday_type ADD CONSTRAINT fk_current_type_instance FOREIGN KEY (current_instance) REFERENCES kernely_holiday_type_instance (id)");
		RawSql holidayTypeNextInstanceForeignKey = new RawSql("ALTER TABLE kernely_holiday_type ADD CONSTRAINT fk_next_type_instance FOREIGN KEY (next_instance) REFERENCES kernely_holiday_type_instance (id)");
		
		commands.add(holidayType);
		commands.add(holidayTypeForeignKey);
		commands.add(holidayTypeInstanceForeignKey);
		commands.add(holidayTypeNextInstanceForeignKey);
		
		
		CreateTable holidayTypeInstanceUser = CreateTable.name("kernely_holiday_type_instance_user");
		holidayTypeInstanceUser.column("user_id", DataBaseConstants.LONG_NOT_NULL);
		holidayTypeInstanceUser.column("type_instance_id", DataBaseConstants.LONG_NOT_NULL);

		RawSql userFk = new RawSql("ALTER TABLE kernely_holiday_type_instance_user ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");
		RawSql typeInstanceFk = new RawSql("ALTER TABLE kernely_holiday_type_instance_user ADD CONSTRAINT fk_type_instance_id FOREIGN KEY (type_instance_id) REFERENCES kernely_holiday_type_instance (id)");
		RawSql tableTypeInstUserPk = new RawSql("ALTER TABLE kernely_holiday_type_instance_user ADD PRIMARY KEY (user_id, type_instance_id)");
		
		commands.add(holidayTypeInstanceUser);
		commands.add(userFk);
		commands.add(typeInstanceFk);
		commands.add(tableTypeInstUserPk);
		
		CreateTable holidayBalance = CreateTable.name("kernely_holiday_balance");
		holidayBalance.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		holidayBalance.column("available_balance", DataBaseConstants.INT);
		holidayBalance.column("available_balance_updated", DataBaseConstants.INT);
		holidayBalance.column("last_update", DataBaseConstants.DATE);
		holidayBalance.column("holiday_type_instance_id", DataBaseConstants.LONG_NOT_NULL);
		holidayBalance.column("begin_date", DataBaseConstants.DATE);
		holidayBalance.column("end_date", DataBaseConstants.DATE);
		holidayBalance.column("user_id", DataBaseConstants.LONG_NOT_NULL);

		RawSql holidayBalanceTypeForeignKey = new RawSql("ALTER TABLE kernely_holiday_balance ADD CONSTRAINT fk_holiday_type_instance FOREIGN KEY (holiday_type_instance_id) REFERENCES kernely_holiday_type_instance (id)");
		RawSql holidayBalanceUserForeignKey = new RawSql("ALTER TABLE kernely_holiday_balance ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES kernely_user (id)");

		commands.add(holidayBalance);
		commands.add(holidayBalanceTypeForeignKey);
		commands.add(holidayBalanceUserForeignKey);

		CreateTable holidayRequest = CreateTable.name("kernely_holiday_request");
		holidayRequest.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		holidayRequest.column("begin_date", DataBaseConstants.DATE);
		holidayRequest.column("end_date", DataBaseConstants.DATE);
		holidayRequest.column("status", DataBaseConstants.INT);
		holidayRequest.column("manager", DataBaseConstants.LONG);
		holidayRequest.column("requester_comment", DataBaseConstants.TEXT);
		holidayRequest.column("manager_comment", DataBaseConstants.TEXT);
		holidayRequest.column("user_id", DataBaseConstants.LONG_NOT_NULL);

		RawSql holidayRequestManagerForeignKey = new RawSql("ALTER TABLE kernely_holiday_request ADD CONSTRAINT fk_user FOREIGN KEY (manager) REFERENCES kernely_user (id)");
		RawSql holidayRequestUserForeignKey = new RawSql("ALTER TABLE kernely_holiday_request ADD CONSTRAINT fk_manager FOREIGN KEY (user_id) REFERENCES kernely_user (id)");

		commands.add(holidayRequest);
		commands.add(holidayRequestUserForeignKey);
		commands.add(holidayRequestManagerForeignKey);

		CreateTable holidayRequestDetail = CreateTable.name("kernely_holiday_request_detail");
		holidayRequestDetail.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		holidayRequestDetail.column("day", DataBaseConstants.DATE);
		holidayRequestDetail.column("am", DataBaseConstants.BOOLEAN_DEFAULT_FALSE);
		holidayRequestDetail.column("pm", DataBaseConstants.BOOLEAN_DEFAULT_FALSE);
		holidayRequestDetail.column("holiday_request_id", DataBaseConstants.LONG);
		holidayRequestDetail.column("holiday_type_instance_id", DataBaseConstants.LONG_NOT_NULL);

		RawSql holidayRequestDetailRequestForeignKey = new RawSql("ALTER TABLE kernely_holiday_request_detail ADD CONSTRAINT fk_holiday_request FOREIGN KEY (holiday_request_id) REFERENCES kernely_holiday_request(id)");
		RawSql holidayRequestDetailBalanceForeignKey = new RawSql("ALTER TABLE kernely_holiday_request_detail ADD CONSTRAINT fk_holiday_type_instance FOREIGN KEY (holiday_type_instance_id) REFERENCES kernely_holiday_type_instance(id)");

		commands.add(holidayRequestDetail);
		commands.add(holidayRequestDetailRequestForeignKey);
		commands.add(holidayRequestDetailBalanceForeignKey);
		
		CreateTable holidayDonation = CreateTable.name("kernely_holiday_donation");
		holidayDonation.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		holidayDonation.column("amount", DataBaseConstants.FLOAT4);
		holidayDonation.column("comment", DataBaseConstants.TEXT);
		holidayDonation.column("date", DataBaseConstants.DATE);
		holidayDonation.column("manager_id", DataBaseConstants.LONG_NOT_NULL);
		holidayDonation.column("receiver_id", DataBaseConstants.LONG_NOT_NULL);
		holidayDonation.column("holiday_type_instance_id", DataBaseConstants.LONG_NOT_NULL);

		RawSql holidayDonationManagerForeignKey = new RawSql("ALTER TABLE kernely_holiday_donation ADD CONSTRAINT fk_holiday_donation_manager FOREIGN KEY (manager_id) REFERENCES kernely_user(id)");
		RawSql holidayDonationReceiverForeignKey = new RawSql("ALTER TABLE kernely_holiday_donation ADD CONSTRAINT fk_holiday_donation_receiver FOREIGN KEY (receiver_id) REFERENCES kernely_user(id)");
		RawSql holidayDonationTypeForeignKey = new RawSql("ALTER TABLE kernely_holiday_donation ADD CONSTRAINT fk_holiday_donation_type FOREIGN KEY (holiday_type_instance_id) REFERENCES kernely_holiday_type_instance(id)");

		
		commands.add(holidayDonation);
		commands.add(holidayDonationManagerForeignKey);
		commands.add(holidayDonationReceiverForeignKey);
		commands.add(holidayDonationTypeForeignKey);
		
		return commands;
	}
}
