<script type="text/javascript" src="/js/invoice_main.js"></script>

<div id="mask"></div>

<div id="modal_window_invoice"></div>

<script type="text/html" id="confirm-remove-invoice-template"><%= i18n.t("invoice_remove_confirm") %></script>

<script type="text/html" id="invoice-line-template">
	<td>{{status}}</td>
	<td>{{number}}</td>
	<td>{{client}}</td>
	<td>{{project}}</td>
	<td></td>
	<td>{{amount}}</td>
	<td><a href="/invoice/view/{{invoiceId}}"><img src="/images/edit.png" class="edit-invoice" /></a></td>
	<td><img src="/img/delete.png" class="delete-invoice" /></td>
</script>

<script type="text/html" id="invoice-creation-window">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<fieldset>
		<legend><%= i18n.t("new-invoice") %></legend>
		<span><%= i18n.t("choose-organization") %></span><br/>
		<select id="organization-selector-mod">
			<option></option>
		</select><br/>
		<span><%= i18n.t("choose-project") %></span><br/>
		<select id="project-selector-mod" disabled="disabled">
			<option></option>
		</select><br/>
		<span><%= i18n.t("choose-period") %></span><br/>
		<label for="from"><%= i18n.t("from_date") %> </label><input readonly="readonly" type="text" id="from" name="from"/><br/>
		<label for="to"> <%= i18n.t("to_date") %> </label><input readonly="readonly" type="text" id="to" name="to"/>
	</fieldset>
	<input type="button" value="<%= i18n.t("cancel") %>" class="cancel_invoice"/>
	<input type="button" value="<%= i18n.t("create") %>" class="create_invoice"/>
	<br/>
	<span id="errors_message"></span>
</script>

<div id="invoicing-main">
	<div id="invoicing-header">
	</div>
	<div id="invoicing-buttons-bar">
		<select id="organization-selector">
			<option value="0">All</option>
			<optgroup id="organization-select-group" label="<%= i18n.t("organizations") %>">
				
			</optgroup>
		</select>
		<select id="project-selector" disabled="disabled">
			<option value="0">All</option>
			<optgroup id="project-select-group" label="<%= i18n.t("projects") %>">
			
			</optgroup>
		</select>
		<input type="button" value="<%= i18n.t("new-invoice") %>" id="new-invoice">
	</div>
	<div id="invoices-list">
		<table id="invoices-table">
			<tr>
				<th><%= i18n.t("invoice-status") %></th>
				<th><%= i18n.t("invoice-number") %></th>
				<th><%= i18n.t("invoice-client") %></th>
				<th><%= i18n.t("invoice-project") %></th>
				<th></th>
				<th><%= i18n.t("invoice-amount") %></th>
				<th></th>
				<th></th>
			</tr>
			<tbody id="invoices">
			
			</tbody>
		</table>
	</div>
	
</div>