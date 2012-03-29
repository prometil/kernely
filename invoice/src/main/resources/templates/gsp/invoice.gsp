<script type="text/html" id="invoice-line-template">
	<td>{{description}}</td>
	<td>{{quantity}}</td>
	<td>{{unitprice}}</td>
	<td>{{amount}}</td>
</script>

<div id="invoice-visualization">
	<!-- Invoice button bar -->
	<div id="invoice-button-bar">
		<input type="button" id="export-invoice" value="<%= i18n.t("export") %>"/>
		<input type="button" id="edit-invoice" value="<%= i18n.t("edit") %>" onClick="window.location = '/invoice/edit/${invoice.id}';"/>
	</div>
	<!-- Invoice header -->
	<div id="invoice-header">
		<h2><span><%= i18n.t("invoice") %></span> ${invoice.organizationName}</h2>
	</div>
	<!-- General informations -->
	<div id="invoice-general-informations">
		<%= i18n.t("invoice-code") %> : ${invoice.code}<br/>
		<%= i18n.t("invoice-publication") %> : ${invoice.datePublicationString}<br/>
		<%= i18n.t("invoice-term") %> : ${invoice.dateTermString}<br/>
		<%= i18n.t("invoice-object") %> : ${invoice.object}<br/><br/>
		
		<%= i18n.t("invoice-address-invoicing") %> : <br/>
		<b>${invoice.organizationName}</b><br/>
		${invoice.organizationAddress}<br/>
		${invoice.organizationZip} ${invoice.organizationCity}
	</div>
	<!-- Invoice details -->
	<br/>
	<div id="invoice-details">
		<table id="invoice-line-details">
			<tr>
				<th><%= i18n.t("invoice-line-description") %></th>
				<th><%= i18n.t("invoice-line-quantity") %></th>
				<th><%= i18n.t("invoice-line-unit-price") %></th>
				<th><%= i18n.t("invoice-line-amount") %></th>
			</tr>
			<tbody id="invoice-lines">
				<% invoice.lines.each() { value -> %>
					<tr>
						<td>${value.designation}</td>
						<td>${value.quantity}</td>
						<td>${value.unitPrice}</td>
						<td>${value.amount}</td>
					</tr>
				<% };%>
			</tbody>
			<tbody id="additional-informations">
				
			</tbody>
		</table>
		<br/>
		<br/>
		<%= i18n.t("invoice-total") %> : ${invoice.amount}
	</div>
</div>