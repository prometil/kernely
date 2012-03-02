package org.kernely.holiday.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.migrations.migrator.Command;
import org.kernely.core.migrations.migrator.CreateTable;
import org.kernely.core.migrations.migrator.Migration;
import org.kernely.core.migrations.migrator.RawSql;

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
		holidayProfile.column("id", "bigint primary key");
		holidayProfile.column("name", "varchar(50)");
		
		commands.add(holidayProfile);
		
		CreateTable holidayProfileUsers = CreateTable.name("kernely_holiday_profile_users");
		holidayProfileUsers.column("holiday_profile_id", "bigint");
		holidayProfileUsers.column("user_id", "bigint");

		RawSql holidayProfileForeignKey = new RawSql("ALTER TABLE kernely_holiday_profile_users ADD CONSTRAINT fk_holiday_profile FOREIGN KEY (holiday_profile_id) REFERENCES kernely_holiday_profile (id)");
		RawSql userForeignKey = new RawSql("ALTER TABLE kernely_holiday_profile_users ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES kernely_user (id)");
		RawSql tableProfilePk = new RawSql("ALTER TABLE kernely_holiday_profile_users ADD PRIMARY KEY (holiday_profile_id, user_id)");
		
		
		commands.add(holidayProfileUsers);
		commands.add(holidayProfileForeignKey);
		commands.add(tableProfilePk);
		commands.add(userForeignKey);
		
		CreateTable holidayTypeInstance = CreateTable.name("kernely_holiday_type_instance");
		holidayTypeInstance.column("id", "bigint primary key");
		holidayTypeInstance.column("name", "varchar(50)");
		holidayTypeInstance.column("color", "varchar(10)");
		holidayTypeInstance.column("anticipated", "bool");
		holidayTypeInstance.column("quantity", "int");
		holidayTypeInstance.column("period_unit", "int");

		commands.add(holidayTypeInstance);
		
		CreateTable holidayType = CreateTable.name("kernely_holiday_type");
		holidayType.column("id", "bigint primary key");
		holidayType.column("name", "varchar(50)");
		holidayType.column("unlimited", "bool");
		holidayType.column("quantity", "int");
		holidayType.column("period_unit", "int");
		holidayType.column("effective_month", "int");
		holidayType.column("anticipated", "bool");
		holidayType.column("color", "varchar(10)");
		holidayType.column("holiday_profile_id", "bigint");
		holidayType.column("current_instance", "bigint");

		RawSql holidayTypeForeignKey = new RawSql("ALTER TABLE kernely_holiday_type ADD CONSTRAINT fk_holiday_profile FOREIGN KEY (holiday_profile_id) REFERENCES kernely_holiday_profile (id)");
		RawSql holidayTypeInstanceForeignKey = new RawSql("ALTER TABLE kernely_holiday_type ADD CONSTRAINT fk_current_type_instance FOREIGN KEY (current_instance) REFERENCES kernely_holiday_type_instance (id)");
		
		commands.add(holidayType);
		commands.add(holidayTypeForeignKey);
		commands.add(holidayTypeInstanceForeignKey);
		
		CreateTable holidayTypeInstanceUser = CreateTable.name("kernely_holiday_type_instance_user");
		holidayTypeInstanceUser.column("user_id", "bigint NOT NULL");
		holidayTypeInstanceUser.column("type_instance_id", "bigint NOT NULL");

		RawSql userFk = new RawSql("ALTER TABLE kernely_holiday_type_instance_user ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");
		RawSql typeInstanceFk = new RawSql("ALTER TABLE kernely_holiday_type_instance_user ADD CONSTRAINT fk_type_instance_id FOREIGN KEY (type_instance_id) REFERENCES kernely_holiday_type_instance (id)");
		RawSql tableTypeInstUserPk = new RawSql("ALTER TABLE kernely_holiday_type_instance_user ADD PRIMARY KEY (user_id, type_instance_id)");
		
		commands.add(holidayTypeInstanceUser);
		commands.add(userFk);
		commands.add(typeInstanceFk);
		commands.add(tableTypeInstUserPk);
		
		CreateTable holidayBalance = CreateTable.name("kernely_holiday_balance");
		holidayBalance.column("id", "bigint primary key");
		holidayBalance.column("available_balance", "int");
		holidayBalance.column("available_balance_updated", "int");
		holidayBalance.column("last_update", "timestamp");
		holidayBalance.column("holiday_type_instance_id", "bigint");
		holidayBalance.column("begin_date", "timestamp");
		holidayBalance.column("end_date", "timestamp");
		holidayBalance.column("user_id", "bigint");

		RawSql holidayBalanceTypeForeignKey = new RawSql("ALTER TABLE kernely_holiday_balance ADD CONSTRAINT fk_holiday_type_instance FOREIGN KEY (holiday_type_instance_id) REFERENCES kernely_holiday_type_instance (id)");
		RawSql holidayBalanceUserForeignKey = new RawSql("ALTER TABLE kernely_holiday_balance ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES kernely_user (id)");

		commands.add(holidayBalance);
		commands.add(holidayBalanceTypeForeignKey);
		commands.add(holidayBalanceUserForeignKey);

		CreateTable holidayRequest = CreateTable.name("kernely_holiday_request");
		holidayRequest.column("id", "bigint primary key");
		holidayRequest.column("begin_date", "timestamp");
		holidayRequest.column("end_date", "timestamp");
		holidayRequest.column("status", "int");
		holidayRequest.column("manager", "bigint");
		holidayRequest.column("requester_comment", "text");
		holidayRequest.column("manager_comment", "text");
		holidayRequest.column("user_id", "bigint");

		RawSql holidayRequestManagerForeignKey = new RawSql("ALTER TABLE kernely_holiday_request ADD CONSTRAINT fk_user FOREIGN KEY (manager) REFERENCES kernely_user (id)");
		RawSql holidayRequestUserForeignKey = new RawSql("ALTER TABLE kernely_holiday_request ADD CONSTRAINT fk_manager FOREIGN KEY (user_id) REFERENCES kernely_user (id)");

		commands.add(holidayRequest);
		commands.add(holidayRequestUserForeignKey);
		commands.add(holidayRequestManagerForeignKey);

		CreateTable holidayRequestDetail = CreateTable.name("kernely_holiday_request_detail");
		holidayRequestDetail.column("id", "bigint primary key");
		holidayRequestDetail.column("day", "timestamp");
		holidayRequestDetail.column("am", "boolean");
		holidayRequestDetail.column("pm", "boolean");
		holidayRequestDetail.column("holiday_request_id", "bigint");
		holidayRequestDetail.column("holiday_type_instance_id", "bigint");

		RawSql holidayRequestDetailRequestForeignKey = new RawSql("ALTER TABLE kernely_holiday_request_detail ADD CONSTRAINT fk_holiday_request FOREIGN KEY (holiday_request_id) REFERENCES kernely_holiday_request(id)");
		RawSql holidayRequestDetailBalanceForeignKey = new RawSql("ALTER TABLE kernely_holiday_request_detail ADD CONSTRAINT fk_holiday_type_instance FOREIGN KEY (holiday_type_instance_id) REFERENCES kernely_holiday_type_instance(id)");

		commands.add(holidayRequestDetail);
		commands.add(holidayRequestDetailRequestForeignKey);
		commands.add(holidayRequestDetailBalanceForeignKey);
		
		return commands;
	}
}
