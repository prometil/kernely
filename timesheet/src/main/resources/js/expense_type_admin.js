AppExpenseType = (function($){
	var lineSelected = null;
    var tableView = null;
    
    ExpenseTypeAdminTableView = Backbone.View.extend({
		el:"#expense_type_admin_table",
		events:{
		
		},
		
		table:null,
		
		initialize:function(){
			var parent = this;
			
			var templateNameColumn = $("#table-expense-name-column").text();
			var templateDirectColumn = $("#table-expense-direct-column").text();
			var templateRatioColumn = $("#table-expense-ratio-column").text();
			this.table = $(parent.el).kernely_table({
				columns:[
				      {"name":templateNameColumn,"style":""},
				      {"name":templateDirectColumn,"style":""},
				      {"name":templateRatioColumn,"style":""}
				],
				idField:"id",
				elements:["name", "direct", "ratio"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				},
				editable:true
			});
		},
		selectLine : function(e){
			$(".editButton").removeAttr('disabled');
			$(".deleteButton").removeAttr('disabled');
			lineSelected = e.data.line;
		},
		reload: function(){
			this.render();
		},
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/admin/expense/type/all",
				dataType:"json",
				success: function(data){
					if (data != null){
						var dataExpense = data.expenseTypeDTO;
						parent.table.reload(dataExpense);
					}
					else{
						parent.table.clear();
						parent.table.noData();
					}
				}
			});
			return this;
		}
	})
	
	ExpenseTypeAdminButtonsView = Backbone.View.extend({
		el:"#expense_type_admin_buttons",
		
		events: {
			"click .createButton" : "createType",
			"click .editButton" : "editType",
			"click .deleteButton" : "deleteType"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreate =  new ExpenseTypeAdminCreateView();
			this.viewUpdate = new ExpenseTypeAdminUpdateView("","","",0);
		},
		
		createType: function(){
			this.viewCreate.render();
		},
		
		editType: function(){
			this.viewUpdate.render();
		},
		
		deleteType: function(){
			var title = $("#delete-template").html();
			var template = $("#expense-type-delete-confirm-template").html();
			$.kernelyConfirm(title,template,this.confirmDeleteType);
		},
		
		confirmDeleteType: function(){
			$.ajax({
				type: "GET",
				url:"/admin/expense/type/delete",
				data:{idType: lineSelected},
				success: function(){
					var successHtml = $("#success-message-template").html();
					$.writeMessage("success",successHtml);
					tableView.reload();
				}
			});
		},
		
		render:function(){
			return this;
		}
	})
	
	ExpenseTypeAdminCreateView = Backbone.View.extend({
		el: "#modal_window_expense_type",
		
		render : function(){
			var parent = this;
			var html = $("#popup-expense-type-admin-create-template").html();
			var title = $("#create-template").html();
			
			$("#modal_window_expense_type").kernely_dialog({
				title: title,
				content: html,
				eventNames: ['click .create_expense_type','click .expense-type-cb'],
				events:{
					 'click .create_expense_type' : parent.createType,
					  'click .expense-type-cb' : parent.manageDirect
				}
			});
			$("#modal_window_expense_type").kernely_dialog("open");
			
			return this;
		},
		manageDirect: function(){
			if($('input[name*="direct"]').is(":checked")){
				$('#ratio_field').val(1.0);
				$('#ratio_field').attr("readonly", "readonly");
			}
			else{
				$('#ratio_field').removeAttr("readonly");
			}
		},
		
		createType: function(){
			var json = '{"id":"0", "name":"'+$('input[name*="name"]').val()+'","direct":"'+$('input[name*="direct"]').is(":checked")+'", "ratio":"'+$('input[name*="ratio"]').val()+'", "description":"'+$("#description-field").val().replace('\n', "\\n")+'"}';
			$.ajax({
				url:"/admin/expense/type/create",
				data: json,
				type: "POST",
				dataType:"json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
				  if (data.result == "Ok"){
					  $("#modal_window_expense_type").kernely_dialog("close");
	       			var successHtml = $("#success-message-template").html();
					$.writeMessage("success",successHtml);
					tableView.reload();
				  } else {
                    $.writeMessage("error",data.result);
				  }
				}
			});
		}
	})
	
	ExpenseTypeAdminUpdateView = Backbone.View.extend({
		el: "#modal_window_expense_type",
		
		render : function(){
			var parent = this;
			$.ajax({
				url:"/admin/expense/type/"+lineSelected,
				type: "GET",
				dataType: "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					var template = $("#popup-expense-type-admin-update-template").html();
					var view = {name : data.name, ratio: data.ratio, description:data.description};
					var html = Mustache.to_html(template, view);
					var title = $("#edit-template").html();
					
					$("#modal_window_expense_type").kernely_dialog({
						title: title,
						content: html,
						eventNames: ['click .updateExpenseType', 'click .expense-type-cb'],
						events:{
								"click .updateExpenseType" : parent.updateType,
								"click .expense-type-cb" : parent.manageDirect
						}
					});
					if(data.direct == "true"){
						$('input[name*="direct"]').attr('checked','checked');
						$('#ratio_field').attr("readonly", "readonly");
					}
					
					$("#modal_window_expense_type").kernely_dialog("open");
				}
			});
			
			return this;
		},
		
		manageDirect: function(){
			if($('input[name*="direct"]').is(":checked")){
				$('#ratio_field').val(1.0);
				$('#ratio_field').attr("readonly", "readonly");
			}
			else{
				$('#ratio_field').removeAttr("readonly");
			}
		},
		
		updateType: function(){
			var json = '{"id":"'+lineSelected+'", "name":"'+$('input[name*="name"]').val()+'","direct":"'+$('input[name*="direct"]').is(":checked")+'", "ratio":'+$('input[name*="ratio"]').val()+'}';
			$.ajax({
				url:"/admin/expense/type/create",
				data: json,
				type: "POST",
				dataType:"json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "Ok"){
						$("#modal_window_expense_type").kernely_dialog("close");
						var successHtml = $("#success-message-template").html();
						$.writeMessage("success",successHtml);
						tableView.reload();
					} else {
						$.writeMessage("error",data.result);
					}
				}
			});
		}
	})
	
	// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new ExpenseTypeAdminTableView().render();
		new ExpenseTypeAdminButtonsView().render();
	}
	return self;
});

$( function() {
    console.log("Starting expense type administration application")
    new AppExpenseType(jQuery).start();
})