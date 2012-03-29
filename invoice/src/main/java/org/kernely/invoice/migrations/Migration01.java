package org.kernely.invoice.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.migrations.migrator.Command;
import org.kernely.core.migrations.migrator.CreateTable;
import org.kernely.core.migrations.migrator.DataBaseConstants;
import org.kernely.core.migrations.migrator.Migration;
import org.kernely.core.migrations.migrator.RawSql;

/**
 * Invoice database migration
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

		CreateTable invoice = CreateTable.name("kernely_invoice");
		invoice.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		invoice.column("code", DataBaseConstants.VARCHAR_30);
		invoice.column("date_creation", DataBaseConstants.DATE);
		invoice.column("date_publication", DataBaseConstants.DATE);
		invoice.column("date_term", DataBaseConstants.DATE);
		invoice.column("object", DataBaseConstants.VARCHAR_100);
		invoice.column("status", DataBaseConstants.INT);
		invoice.column("organization_name", DataBaseConstants.VARCHAR_50);
		invoice.column("organization_address", DataBaseConstants.VARCHAR_300);
		invoice.column("organization_zip", DataBaseConstants.VARCHAR_5);
		invoice.column("organization_city", DataBaseConstants.VARCHAR_50);
		invoice.column("project_id", DataBaseConstants.LONG_NOT_NULL);
			
		commands.add(invoice);
		
		CreateTable invoiceLine = CreateTable.name("kernely_invoice_line");
		invoiceLine.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		invoiceLine.column("designation", DataBaseConstants.VARCHAR_100);
		invoiceLine.column("quantity", DataBaseConstants.FLOAT4);
		invoiceLine.column("unit_price", DataBaseConstants.FLOAT4);
		invoiceLine.column("invoice_id", DataBaseConstants.LONG_NOT_NULL);

		RawSql invoiceLineForeignKey = new RawSql("ALTER TABLE kernely_invoice_line ADD CONSTRAINT fk_invoice FOREIGN KEY (invoice_id) REFERENCES kernely_invoice (id)");
		
		commands.add(invoiceLine);
		commands.add(invoiceLineForeignKey);
		
		CreateTable invoiceHistory = CreateTable.name("kernely_invoice_history");
		invoiceHistory.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		invoiceHistory.column("date", DataBaseConstants.DATE);
		invoiceHistory.column("content", DataBaseConstants.TEXT);
		invoiceHistory.column("invoice_id", DataBaseConstants.LONG_NOT_NULL);

		RawSql invoiceHistoryForeignKey = new RawSql("ALTER TABLE kernely_invoice_history ADD CONSTRAINT fk_invoice FOREIGN KEY (invoice_id) REFERENCES kernely_invoice (id)");
		
		commands.add(invoiceHistory);
		commands.add(invoiceHistoryForeignKey);
		
		return commands;
	}
}
