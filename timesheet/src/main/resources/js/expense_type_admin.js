AppExpenseType = (function($){
	var lineSelected = null;
    var tableView = null;
    
    ExpenseTypeAdminTableLineView = Backbone.View.extend({
        tagName: "tr",
        className: 'expense_type_list_line',
        
        vid: null,
        vname: null,
        vdirect: null,
        vration: null,
        
        events: {
                "click" : "selectLine",
                "mouseover" : "overLine",
                "mouseout" : "outLine"
        },
        
        initialize: function(id, name, direct, ratio){
                this.vid = id;
                this.vname = name;
                this.vdirect = direct;
                this.vratio = ratio;
        },
        selectLine : function(){
                $(".editButton").removeAttr('disabled');
                $(".deleteButton").removeAttr('disabled');
                $(this.el).css("background-color", "#8AA5A1");
                if(typeof(lineSelected) != "undefined"){
                        if(lineSelected != this && lineSelected != null){
                                $(lineSelected.el).css("background-color", "transparent");
                        }
                }
                lineSelected = this;
        },
        overLine : function(){
                if(lineSelected != this){
                        $(this.el).css("background-color", "#EEEEEE");
                }
        },
        outLine : function(){
                if(lineSelected != this){
                        $(this.el).css("background-color", "transparent");
                }
        },
        render:function(){
                var template = '<td>{{name}}</td><td>{{direct}}</td><td>{{ratio}}</td>';
                var view = {name : this.vname, direct: this.vdirect, ratio: this.vratio};
                var html = Mustache.to_html(template, view);
                
                $(this.el).html(html);
                $(this.el).appendTo($("#expense_type_admin_table"));
                return this;
        }
    })
    
    ExpenseTypeAdminTableView = Backbone.View.extend({
		el:"#expense_type_admin_table",
		events:{
		
		},
		initialize:function(){
			var parent = this;
			
			var tableHeader = $("#table-header-template").html();
			
			$(this.el).html(tableHeader);
			$.ajax({
				type:"GET",
				url:"/admin/expense/type/all",
				dataType:"json",
				success: function(data){
					if (data != null){
						if(data.expenseTypeDTO.length > 1){
				    		$.each(data.expenseTypeDTO, function() {
				    			var view = new ExpenseTypeAdminTableLineView(this.id, this.name, this.direct, this.ratio);
				    			view.render();
				    		});
						}
						else{
							var view = new ExpenseTypeAdminTableLineView(data.expenseTypeDTO.id, data.expenseTypeDTO.name, data.expenseTypeDTO.direct, data.expenseTypeDTO.ratio);
			    			view.render();
						}
					}
				}
			});
		},
		reload: function(){
			this.initialize();
			this.render();
		},
		render: function(){
			return this;
		}
	})
	
	ExpenseTypeAdminButtonsView = Backbone.View.extend({
		el:"#expense_type_admin_container",
		
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
		
		showModalWindow: function(){
			//Get the screen height and width
       		var maskHeight = $(window).height();
       		var maskWidth = $(window).width();


            //Set height and width to mask to fill up the whole screen
            $('#mask').css({'width':maskWidth,'height':maskHeight});

            //transition effect    
            $('#mask').fadeIn(500);   
            $('#mask').fadeTo("fast",0.7); 

            //Get the window height and width
            var winH = $(window).height();
            var winW = $(window).width();


        	//Set the popup window to center
       		$("#modal_window_expense_type").css('top',  winH/2-$("#modal_window_expense_type").height()/2);
     		$("#modal_window_expense_type").css('left', winW/2-$("#modal_window_expense_type").width()/2);
     		$("#modal_window_expense_type").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_expense_type").fadeIn(500);
		},
		
		createType: function(){
			this.showModalWindow();
			this.viewCreate.render();
		},
		
		editType: function(){
			this.showModalWindow();
			this.viewUpdate.setFields(lineSelected.vname,lineSelected.vdirect,lineSelected.vratio, lineSelected.vid);
			this.viewUpdate.render();
		},
		
		deleteType: function(){
			var template = $("#expense-type-delete-confirm-template").html();
			
			var answer = confirm(template);
			if (answer){
				$.ajax({
					type: "GET",
					url:"/admin/expense/type/delete",
					data:{idType: lineSelected.vid},
					success: function(){
						var successHtml = $("#success-message-template").html();
						$("#span_notifications").text(successHtml);
						$("#span_notifications").fadeIn(1000);
						$("#span_notifications").fadeOut(3000);
						tableView.reload();
					}
				});
			}
		},
		
		render:function(){
			return this;
		}
	})
	
	ExpenseTypeAdminCreateView = Backbone.View.extend({
		el: "#modal_window_expense_type",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .create_expense_type" : "createType",
			"click .expense-type-cb" : "manageDirect"
		},
		
		initialize:function(){
		},
		
		render : function(){
			var template = $("#popup-expense-type-admin-create-template").html();
			
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
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
		closemodal: function(){
			$('#modal_window_expense_type').hide();
       		$('#mask').hide();
		},
		
		createType: function(){
			var json = '{"id":"0", "name":"'+$('input[name*="name"]').val()+'","direct":"'+$('input[name*="direct"]').is(":checked")+'", "ratio":"'+$('input[name*="ratio"]').val()+'"}';
			$.ajax({
				url:"/admin/expense/type/create",
				data: json,
				type: "POST",
				dataType:"json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
				  if (data.result == "Ok"){
					$('#modal_window_expense_type').hide();
	       			$('#mask').hide();

	       			var successHtml = $("#success-message-template").html();
					$("#span_notifications").text(successHtml);
					$("#span_notifications").fadeIn(1000);
					$("#span_notifications").fadeOut(3000);
					tableView.reload();
				  } else {
                    $("#errors_message").text(data.result);
                    $("#errors_message").fadeIn(1000);
                    $("#errors_message").fadeOut(3000);
				  }
				}
			});
		}
	})
	
	ExpenseTypeAdminUpdateView = Backbone.View.extend({
		el: "#modal_window_expense_type",
		
		vid: null,
		vname: null,
		vdirect: null,
		vratio: null,
		
		events:{
			"click .closeModal" : "closemodal",
			"click .updateExpenseType" : "updateType",
			"click .expense-type-cb" : "manageDirect"
		},
		
		initialize:function(name, direct, ratio, id){
			this.vid = id;
			this.vname = name;
			this.vdirect = direct;
			this.vratio = ratio;
		},
		
		setFields: function(name, direct, ratio, id){
			this.vid = id;
			this.vname = name;
			this.vdirect = direct;
			this.vratio = ratio;
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
		render : function(){
			var template = $("#popup-expense-type-admin-update-template").html();
		
			
			
			var view = {name : this.vname, ratio: this.vratio};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			
			if(this.vdirect == "true"){
				$('input[name*="direct"]').attr('checked','checked');
				$('#ratio_field').attr("readonly", "readonly");
			}
			
			
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_expense_type').hide();
       		$('#mask').hide();
		},
		
		updateType: function(){
			var json = '{"id":"'+this.vid+'", "name":"'+$('input[name*="name"]').val()+'","direct":"'+$('input[name*="direct"]').is(":checked")+'", "ratio":"'+$('input[name*="ratio"]').val()+'}';
			$.ajax({
				url:"/admin/expense/type/create",
				data: json,
				type: "POST",
				dataType:"json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "Ok"){
						$('#modal_window_expense_type').hide();
						$('#mask').hide();

						var successHtml = $("#success-message-template").html();
						$("#span_notifications").text(successHtml);
						$("#span_notifications").fadeIn(1000);
						$("#span_notifications").fadeOut(3000);
						tableView.reload();
					} else {
						$("#errors_message").text(data.result);
						$("#errors_message").fadeIn(1000);
						$("#errors_message").fadeOut(3000);
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