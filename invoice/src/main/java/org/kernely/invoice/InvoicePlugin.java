package org.kernely.invoice;

import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.invoice.controller.InvoiceController;
import org.kernely.invoice.migrations.Migration01;
import org.kernely.invoice.model.Invoice;
import org.kernely.invoice.model.InvoiceLine;
import org.kernely.invoice.service.InvoiceService;

/**
 * Plugin for Invoice
 */
public class InvoicePlugin extends AbstractPlugin {
	public static final String NAME = "invoice";

	/**
	 * Default constructor
	 */
	public InvoicePlugin() {
		super();
		registerName(NAME);
		registerPath("/invoice");
		registerController(InvoiceController.class);
		registerModel(Invoice.class);
		registerModel(InvoiceLine.class);
		registerMigration(new Migration01());
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.kernely.core.plugin.AbstractPlugin#start()
	 */
	@Override
	public void start() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.kernely.core.plugin.AbstractPlugin#configure()
	 */
	@Override
	protected void configure() {
		bind(InvoiceService.class);
	}

}
