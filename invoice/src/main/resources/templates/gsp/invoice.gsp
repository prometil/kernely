<script type="text/javascript" src="/js/invoice_edit.js"></script>

<script type="text/html" id="invoice-line-template">
	<td><select id="invoice-line-type-select"></select></td>
	<td>{{description}}</td>
	<td>{{quantity}}</td>
	<td>{{unitprice}}</td>
	<td>{{amount}}</td>
	<td><img src="/images/edit.png" class="edit-invoice-line" /></td>
	<td><img src="/img/delete.png" class="delete-invoice-line" /></td>
</script>

<script type="text/html" id="invoice-line-editable-template">
	<td><select id="invoice-line-type-select"></select></td>
	<td><input type="text" name="designation-field" /></td>
	<td><input type="text" name="quantity-field" /></td>
	<td><input type="text" name="unitprice-field" /></td>
	<td>0.0</td>
	<td><img src="/images/icons/save.png" class="valid-invoice-line" /></td>
	<td></td>
</script>

<div id="invoice-visualization">
	<span id="invoice-visu-i" style="display:none;">${invoice.id}</span>
	<!-- Invoice header -->
	<div id="invoice-header">
		<h2><span><%= i18n.t("invoice") %></span> ${invoice.organizationName}</h2>
	</div>
	<!-- General informations -->
	<div id="invoice-general-informations">
		<input type="text" class="general-informations-fields" name="invoice-code" value="${invoice.code}"/><br/>
		<input type="text" class="general-informations-fields" id="invoice-publication" name="invoice-sending" value="${invoice.datePublicationString}"/><br/>
		<input type="text" class="general-informations-fields" id="invoice-term" name="invoice-maturity" value="${invoice.dateTermString}"/>
		<input type="text" class="general-informations-fields" name="invoice-devise" value="&euro;"/><br/>
		<input type="text" class="general-informations-fields object-field" name="invoice-object" value="${invoice.object}"/>
	</div>
	<!-- Invoice details -->
	<br/>
	<div id="invoice-details">
		<input type="button" id="new-invoice-line" value="<%= i18n.t("invoice-new-line") %>"/>
		<table id="invoice-line-details">
			<tr>
				<th><%= i18n.t("invoice-line-type") %></th>
				<th><%= i18n.t("invoice-line-description") %></th>
				<th><%= i18n.t("invoice-line-quantity") %></th>
				<th><%= i18n.t("invoice-line-unit-price") %></th>
				<th><%= i18n.t("invoice-line-amount") %></th>
				<th><!-- Empty, column for edition button --><th>
				<th><!-- Empty, column for deletion button --><th>
			</tr>
			<tbody id="invoice-lines">
				<% invoice.lines.each() { value -> %>
					<tr>
						<td><select id="invoice-line-type-select"></select></td>
						<td>${value.designation}</td>
						<td>${value.quantity}</td>
						<td>${value.unitPrice}</td>
						<td>${value.amount}</td>
						<td><img src="/images/edit.png" class="edit-invoice-line" /></td>
						<td><img src="/img/delete.png" class="delete-invoice-line" /></td>
					</tr>
				<% };%>
			</tbody>
			<tbody id="additional-informations">
			
			</tbody>
		</table>
	</div>
	<!-- Invoice history -->
	<div id="invoice-history">
		History !
	</div>
	<!-- Footer button bar -->
	<div id="invoice-button-bar">
		<input type="button" id="export-invoice" value="<%= i18n.t("export") %>"/>
		<input type="button" id="cancel-invoice" value="<%= i18n.t("cancel") %>"/>
		<input type="button" id="validate-invoice" value="<%= i18n.t("ok") %>"/>
	</div>
</div>