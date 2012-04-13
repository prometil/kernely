<script type="text/javascript" src="/js/invoice_edit.js"></script>

<script type="text/html" id="invoice-line-template">
</script>

<script type="text/html" id="invoice-line-editable-template">
	<td><input type="text" name="designation-field[]" value="{{description}}"/></td>
	<td><input type="text" class="quantity-field" name="quantity-field[]" value="{{quantity}}"/></td>
	<td><input type="text" class="unitprice-field" name="unitprice-field[]" value="{{unitprice}}"/></td>
	<td class="line-amount">{{amount}}</td>
	<td><select name="vat-field[]" class="vat-field"></select> %</td>
	<td><img src="/img/delete.png" class="delete-invoice-line"/></td>
</script>

<form action="/invoice/update/${invoice.id}" method="POST" id="invoice-edition">
	
	<span id="invoice-visu-i" style="display:none;">${invoice.id}</span>
	<!-- Invoice header -->
	<div id="invoice-header">
		<h2><span><%= i18n.t("invoice") %></span> ${invoice.organizationName}</h2>
	</div>
	<!-- General informations -->
	<div id="invoice-general-informations">
		<%= i18n.t("invoice-code") %> : <input type="text" class="general-informations-fields" name="invoice-code" value="${invoice.code}"/><br/>
		<%= i18n.t("invoice-publication") %> : <input type="text" class="general-informations-fields" id="invoice-publication" name="invoice-sending" value="${invoice.datePublicationString}"/><br/>
		<%= i18n.t("invoice-term") %> : <input type="text" class="general-informations-fields" id="invoice-term" name="invoice-term" value="${invoice.dateTermString}"/>
		<input type="text" class="general-informations-fields" name="invoice-devise" value="&euro;"/><br/>
		<%= i18n.t("invoice-object") %> : <input type="text" class="general-informations-fields object-field" name="invoice-object" value="${invoice.object}"/>
	</div>
	<!-- Invoice details -->
	<br/>
	<div id="invoice-details">
		<input type="button" id="new-invoice-line" value="<%= i18n.t("invoice-new-line") %>"/>
		<table id="invoice-line-details">
			<tr>
				<th><%= i18n.t("invoice-line-description") %></th>
				<th><%= i18n.t("invoice-line-quantity") %></th>
				<th><%= i18n.t("invoice-line-unit-price") %></th>
				<th><%= i18n.t("invoice-line-amount") %></th>
				<th><%= i18n.t("invoice-line-vat") %></th>
				<th><!-- Column for the delete button --></th>
			</tr>
			<tbody id="invoice-lines">
				
			</tbody>
			<tbody id="additional-informations">
			
			</tbody>
		</table>
	</div>
	
	<%= i18n.t("invoice-comment") %><br/>
	<textarea id="invoice-comment" name="invoice-comment">${invoice.comment}</textarea>
	
	<!-- Footer button bar -->
	<div id="invoice-button-bar">
		<input type="button" id="export-invoice" value="<%= i18n.t("export") %>"/>
		<input type="button" id="cancel-invoice" value="<%= i18n.t("cancel") %>" onClick="window.location = '/invoice/view/${invoice.id}';"/>
		<input type="submit" id="validate-invoice" value="<%= i18n.t("ok") %>"/>
	</div>
</div>