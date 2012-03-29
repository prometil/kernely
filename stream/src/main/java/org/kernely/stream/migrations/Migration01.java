package org.kernely.stream.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.migrations.migrator.Command;
import org.kernely.core.migrations.migrator.CreateTable;
import org.kernely.core.migrations.migrator.DataBaseConstants;
import org.kernely.core.migrations.migrator.Migration;
import org.kernely.core.migrations.migrator.RawSql;

/**
 * The migration script
 */
public class Migration01 extends Migration {

	/**
	 * The constructor
	 */
	public Migration01() {
		super("0.1");
	}

	/**
	 * The script
	 * @return list of commands
	 */
	@Override
	public List<Command> getList() {
		ArrayList<Command> commands = new ArrayList<Command>();
		CreateTable stream = CreateTable.name("kernely_stream");
		stream.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		stream.column("category", DataBaseConstants.VARCHAR_50);
		stream.column("title", DataBaseConstants.VARCHAR_50);
		stream.column("locked", DataBaseConstants.BOOLEAN_DEFAULT_FALSE);
		
		commands.add(stream);
		
		CreateTable message = CreateTable.name("kernely_message");
		message.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		message.column("content", DataBaseConstants.TEXT);
		message.column("message_parent", DataBaseConstants.LONG);
		message.column("stream_id", DataBaseConstants.LONG_NOT_NULL);
		message.column("date", DataBaseConstants.DATE);
		message.column("commentable", DataBaseConstants.BOOLEAN_DEFAULT_TRUE);
		message.column("user_id", DataBaseConstants.LONG_NOT_NULL);
		
		RawSql messageForeignKeyUser= new RawSql("ALTER TABLE kernely_message ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");
		RawSql messageForeignKeyParent= new RawSql("ALTER TABLE kernely_message ADD CONSTRAINT fk_parent_id FOREIGN KEY (message_parent) REFERENCES kernely_message (id)");
		RawSql messageForeignKeyStream= new RawSql("ALTER TABLE kernely_message ADD CONSTRAINT fk_stream_id FOREIGN KEY (stream_id) REFERENCES kernely_stream (id)");
		
		commands.add(message);
		commands.add(messageForeignKeyParent);
		commands.add(messageForeignKeyStream);
		commands.add(messageForeignKeyUser);
		
		return commands; 
	}


}
