<script type="text/javascript" src="/js/expense_type_admin.js"></script>

<div id="expense_type_header"></div>

<div id="mask"></div>

<div id="modal_window_expense_type"></div>

<script type="text/html" id="table-header-template">
<tr>
	<th><%= i18n.t("expense_type_name") %></th>
	<th><%= i18n.t("expense_type_direct") %></th>
	<th><%= i18n.t("expense_type_ratio") %></th>
</tr>
</script>


<script type="text/html" id="success-message-template"><%= i18n.t("success_message") %></script>

<script type="text/html" id="expense-type-delete-confirm-template">
<%= i18n.t("confirm_expense_type_deletion") %>
</script>

<!-- Yes and no -->
<script type="text/html" id="yes-template"><%= i18n.t("yes") %></script>
<script type="text/html" id="no-template"><%= i18n.t("no") %></script>

<script type="text/html" id="popup-expense-type-admin-create-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<fieldset>
	<legend><%= i18n.t("expense_type_informations") %></legend>
	<%= i18n.t("expense_type_name") %>: <input type="text" name="name"/><br/>
	<%= i18n.t("expense_type_direct") %>: <input type="checkbox" class="expense-type-cb" name="direct"/> 
	<%= i18n.t("expense_type_ratio") %>: <input type="text" name="ratio" id="ratio_field"/>
	</fieldset>
	<br/>
	
	<input type="button" value="<%= i18n.t("create") %>" class="create_expense_type"/>
	<span id="errors_message"></span>
</script>

<script type="text/html" id="popup-expense-type-admin-update-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<fieldset>
	<legend><%= i18n.t("expense_type_informations") %></legend>
	<%= i18n.t("expense_type_name") %>: <input type="text" name="name" value="{{name}}"/><br/>
	<%= i18n.t("expense_type_direct") %>: <input type="checkbox" name="direct"/> 
	<%= i18n.t("expense_type_ratio") %>: <input type="text" name="ratio" value="{{ratio}}" id="ratio_field"/>
	</fieldset>
	<br/>
	
	<input type="button" value="<%= i18n.t("create") %>" class="create_expense_type"/>
	<span id="errors_message"></span>
</script>

<div id="expense_type_admin_container">
        <div id="expense_type_admin_buttons">
                <input type="button" class="createButton" value="<%= i18n.t("create") %>"/>
                <input type="button" class="editButton" value="<%= i18n.t("edit") %>" disabled="disabled"/>
                <input type="button" class="deleteButton" value="<%= i18n.t("delete") %>" disabled="disabled"/>
                <span id="span_notifications"></span>
        </div>
        <table id="expense_type_admin_table">
                
        </table>
</div>