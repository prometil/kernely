package org.kernely.invoice.service;

import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.configuration.AbstractConfiguration;
import org.kernely.invoice.model.Invoice;
import org.kernely.invoice.model.InvoiceLine;
import org.kernely.resource.ResourceLocator;
import org.kernely.service.AbstractService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ExportManager extends AbstractService {
	
	@Inject
	ResourceLocator resourceLocator;
	
	@Inject
	AbstractConfiguration configuration;
	
	private static final String HEADER_IMAGE = "jasper/invoiceHeader.png";

	/**
	 * Exports the invoice to pdf and return the stream of the pdf.
	 * @return 
	 */
	public OutputStream exportInvoiceToPdf(Long invoiceId, OutputStream output) throws java.lang.Exception {
		// Get the bill object
		Invoice invoice = em.get().find(Invoice.class, invoiceId);
		if (invoice == null) {
			return null;
		}
		// Fill the invoice line collection
		List<HashMap<String, Object>> invoiceLines = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		Map<Float,Float> vatAmounts = new HashMap<Float,Float>();
		float totalLine;
		long totalHt = 0;
		float totalVAT = 0;

		// Get all invoice lines for the invoices
		Collection<InvoiceLine> invoiceLineForExport = invoice.getLines(); 
		

		// Fill the bill lines parameters
		for (InvoiceLine line : invoiceLineForExport) {
			map = new HashMap<String, Object>();
			map.put("designation", line.getDesignation());
			map.put("up", ""+line.getUnitPrice());
			map.put("qty", ""+line.getQuantity());
			map.put("vatForLine", line.getVat()+"%");
			totalLine = line.getUnitPrice() * line.getQuantity();
			map.put("totalLine",""+totalLine);
			totalHt += totalLine ;
			totalVAT += line.getUnitPrice() * line.getQuantity() * line.getVat() / 100;
			
			invoiceLines.add(map);
			
			// Get the vat and actualize the amount of the tax
			if (vatAmounts.containsKey(line.getVat())){
				float toAdd = line.getQuantity() * line.getUnitPrice() * line.getVat() / 100;
				vatAmounts.put(line.getVat(), line.getVat() + toAdd);
			} else {
				vatAmounts.put(line.getVat(), line.getQuantity() * line.getUnitPrice() * line.getVat() / 100);
			}
		}
		
		// Fill the parameters
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(JRParameter.REPORT_LOCALE,Locale.ENGLISH);
		
		List<Map<String,String>> vatList = new ArrayList<Map<String,String>>();
		
		// Fill VAT subdata
		for (Float rate : vatAmounts.keySet()) {
			Map<String,String> vatMap = new HashMap<String,String>();
			vatMap.put("vat_rate", ""+rate);
			vatMap.put("vat_total", ""+vatAmounts.get(rate));
			vatList.add(vatMap);
		}
		params.put("vatList", vatList);
		
		// If avoir
		if(totalHt < 0 )
		{
			params.put("libelle_code", configuration.getProperty("company.avoir"));
			
			// remove all the negative signs for the amounts displayed in the pdf export
			for(HashMap<String, Object> billLine : invoiceLines){
			  billLine.put("up", ((String) billLine.get("up")).replaceAll("-(.*)", "$1"));
			  billLine.put("totalLine", ((String) billLine.get("totalLine")).replaceAll("-(.*)", "$1"));
			}
		}
		else{
			params.put("libelle_code", configuration.getProperty("company.creditnote"));
		}
		params.put("libelle_code","Facture");
		params.put("code", invoice.getCode());
		params.put("date", invoice.getDateCreation());
		if (invoice.getProject() != null) {
			params.put("clientName", invoice.getOrganizationName());
			params.put("clientAddress", invoice.getOrganizationAddress());
			params.put("clientZip", invoice.getOrganizationZip());
			params.put("clientCity", invoice.getOrganizationCity());
		}

		params.put("devise", "€");
		params.put("object", invoice.getObject());
		params.put("comments", invoice.getComment());
		params.put("totalHt", ""+totalHt);

		params.put("total", ""+(totalHt + totalVAT));
		String payCond = configuration.getString("company.beforedate");

		params.put("paymentConditions", payCond+ " "+invoice.getDateTerm());
		params.put("header", HEADER_IMAGE);

	    params.put("company_catchword", configuration.getString("company.catchword"));
	    params.put("company_designation", configuration.getString("company.designation"));
	    params.put("company_address", configuration.getString("company.address"));
	    params.put("company_phoneNumber", configuration.getString("company.phoneNumber"));
	    params.put("company_APE", configuration.getString("company.APE"));
	    params.put("company_SIRET", configuration.getString("company.SIRET"));
	    params.put("company_TVAInterCom", configuration.getString("company.TVAInterCom"));
	    
		// Fill Report
	    URL jasperSubReportURL = resourceLocator.getResource("jasper/bill_vat_subreport.jasper");
	    params.put("path_subReport1", jasperSubReportURL.getPath());
	    
	    URL jasperReportURL = resourceLocator.getResource("jasper/billreport.jasper");
	    
		JasperPrint jp = JasperFillManager.fillReport(jasperReportURL.getPath(), params, new JRBeanCollectionDataSource(invoiceLines));
		
		// Export to PDF
		if (jp != null) {
			JasperExportManager.exportReportToPdfStream(jp, output);
			return output;
		}
		else {
			throw new Exception("Error while filling the report.");
		}
	}
}