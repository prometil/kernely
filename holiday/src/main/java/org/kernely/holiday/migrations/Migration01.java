package org.kernely.holiday.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.migrations.migrator.Command;
import org.kernely.core.migrations.migrator.CreateTable;
import org.kernely.core.migrations.migrator.Migration;
import org.kernely.core.migrations.migrator.RawSql;

/**
 * Holiday migration script
 * 
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

		CreateTable holidayType = CreateTable.name("kernely_holiday_type");
		holidayType.column("id", "int primary key");
		holidayType.column("name", "varchar(50)");
		holidayType.column("quantity", "int");
		holidayType.column("period_unit", "int");
		holidayType.column("effective_month", "int");
		holidayType.column("anticipated", "bool");
		holidayType.column("color", "varchar(10)");

		commands.add(holidayType);

		CreateTable holidayBalance = CreateTable.name("kernely_holiday_balance");
		holidayBalance.column("id", "int primary key");
		holidayBalance.column("available_balance", "int");
		holidayBalance.column("future_balance", "int");
		holidayBalance.column("last_update", "timestamp");
		holidayBalance.column("holiday_type_id", "int");
		holidayBalance.column("user_id", "bigint");

		RawSql holidayBalanceTypeForeignKey = new RawSql("ALTER TABLE kernely_holiday_balance ADD CONSTRAINT fk_holiday_type FOREIGN KEY (holiday_type_id) REFERENCES kernely_holiday_type (id)");
		RawSql holidayBalanceUserForeignKey = new RawSql("ALTER TABLE kernely_holiday_balance ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES kernely_user (id)");

		commands.add(holidayBalance);
		commands.add(holidayBalanceTypeForeignKey);
		commands.add(holidayBalanceUserForeignKey);

		CreateTable holidayRequest = CreateTable.name("kernely_holiday_request");
		holidayRequest.column("id", "int primary key");
		holidayRequest.column("begin_date", "timestamp");
		holidayRequest.column("end_date", "timestamp");
		holidayRequest.column("status", "int");
		holidayRequest.column("manager", "bigint");
		holidayRequest.column("requester_comment", "varchar(500)");
		holidayRequest.column("manager_comment", "varchar(500)");
		holidayRequest.column("user_id", "bigint");

		RawSql holidayRequestUserForeignKey = new RawSql("ALTER TABLE kernely_holiday_request ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES kernely_user (id)");

		commands.add(holidayRequest);
		commands.add(holidayRequestUserForeignKey);

		CreateTable holidayRequestDetail = CreateTable.name("kernely_holiday_request_detail");
		holidayRequestDetail.column("id", "int primary key");
		holidayRequestDetail.column("day", "timestamp");
		holidayRequestDetail.column("am", "boolean");
		holidayRequestDetail.column("pm", "boolean");
		holidayRequestDetail.column("holiday_request_id", "int");
		holidayRequestDetail.column("holiday_balance_id", "int");

		RawSql holidayRequestDetailRequestForeignKey = new RawSql("ALTER TABLE kernely_holiday_request_detail ADD CONSTRAINT fk_holiday_request FOREIGN KEY (holiday_request_id) REFERENCES kernely_holiday_request(id)");
		RawSql holidayRequestDetailBalanceForeignKey = new RawSql("ALTER TABLE kernely_holiday_request_detail ADD CONSTRAINT fk_holiday_balance FOREIGN KEY (holiday_balance_id) REFERENCES kernely_holiday_balance(id)");

		commands.add(holidayRequestDetail);
		commands.add(holidayRequestDetailRequestForeignKey);
		commands.add(holidayRequestDetailBalanceForeignKey);

		return commands;
	}
}
