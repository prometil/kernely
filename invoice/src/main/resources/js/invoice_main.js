AppInvoiceMain = (function($){
	
	var tableView = null;
	var lineSelected = null;
	
	InvoiceMainView = Backbone.View.extend({
		el:"#invoicing-main",
		
		events:{
		
		},
		
		initialize: function(){
		},
		
		render: function(){
			new InvoiceButtonsView().render();
			new InvoiceFilterView().render();
			tableView = new InvoiceTableView().render();
			return this;
		}
	})
	
	InvoiceButtonsView = Backbone.View.extend({
		el:"#invoicing-buttons-bar",
		
		creationWindow : null,
		
		events:{
			"click .invoice-publish" : "publish",
			"click .invoice-paid" : "paid",
			"click .invoice-unpaid" : "unpaid",
			"click #new-invoice" : "create"
		},
		
		initialize: function(){
			this.creationWindow = new InvoiceCreationView();
		},
		
		create: function(){
			this.creationWindow.render();
		},
		
		publish: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/invoice/publish",
				data:{invoiceId : lineSelected},
				success: function(data){
					$("#"+data.id).html($("#invoice-status-" + data.status).html());
				}
			});
		},
		
		paid: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/invoice/paid",
				data:{invoiceId : lineSelected},
				success: function(data){
					$("#"+data.id).html($("#invoice-status-" + data.status).html());
				}
			});
		},
		
		unpaid: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/invoice/unpaid",
				data:{invoiceId : lineSelected},
				success: function(data){
					$("#"+data.id).html($("#invoice-status-" + data.status).html());
				}
			});
		},
		
		render: function(){
			return this;
		}
	})
	
	InvoiceFilterView = Backbone.View.extend({
		el:"#invoice-filters",
		
		creationWindow : null,
		
		events:{
			"change #organization-selector" : "loadProjects",
			"change #organization-selector, #project-selector" : "refreshInvoices"
		},
		
		initialize: function(){
		},
		
		refreshInvoices: function(){
			tableView.render();
		},
		
		loadProjects: function(){
			$('#project-selector').removeAttr("disabled");
			$('#project-select-group').html("");
			$.ajax({
				type: "GET",
				url:"/invoice/projects",
				data:{organizationId : $('#organization-selector').val()},
				success: function(data){
					// Create the views
					if (data != null){
						if ($.isArray(data.projectDTO)){
							$.each(data.projectDTO, function(){
								$('#project-select-group')
						          .append($('<option>', { value : this.id })
						          .text(this.name));
							});

						} else if (data.projectDTO != null){
						     $('#project-select-group')
					          .append($('<option>', { value : data.projectDTO.id })
					          .text(data.projectDTO.name));
						}
					}
				}
			});
		},
		
		render: function(){
			$.ajax({
				type: "GET",
				url:"/invoice/organizations",
				success: function(data){
					// Create the views
					if (data != null){
						if ($.isArray(data.organizationDTO)){
							$.each(data.organizationDTO, function(){
								$('#organization-select-group')
									.append($('<option>', { value : this.id })
									.text(this.name));
							});

						} else if (data.organizationDTO != null){
							$('#organization-select-group')
								.append($('<option>', { value : data.organizationDTO.id })
								.text(data.organizationDTO.name));
						}
					}
				}
			});
			return this;
		}
	})
	
	InvoiceTableView = Backbone.View.extend({
		el:"#invoices-table",
		
		events:{
		
		},
		
		table: null,
		
		initialize: function(){
			var parent = this;
			
			var templateStatusColumn = $("#table-status-column").text();
			var templateNumberColumn = $("#table-number-column").text();
			var templateClientColumn = $("#table-client-column").text();
			var templateProjectColumn = $("#table-project-column").text();
			var templateAmountColumn = $("#table-amount-column").text();
			this.table = $(parent.el).kernely_table({
				columns:[
				       {"name":templateStatusColumn, "style":"text-center"},
				       {"name":templateNumberColumn, "style":""},
				       {"name":templateClientColumn, "style":""},
				       {"name":templateProjectColumn, "style":""},
				       {"name":templateAmountColumn, "style":"text-center"},
				       {"name":"", "style":["general-bg", "text-center", "no-border-right", "no-border-top", "no-border-bottom"]},
				       {"name":"", "style":["general-bg", "text-center", "no-border-right", "no-border-top", "no-border-bottom"]}],
				idField:"id",
				elements:["status", "code", "organizationName", "projectName", "amount", "buttonView", "buttonEdit"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				}
			});
		},
		selectLine : function(e){
			lineSelected = e.data.line;
		},
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/invoice/specific",
				data:{organizationId : $('#organization-selector').val(), projectId : $('#project-selector').val()},
				success: function(data){
					if(data != null){
						var dataInvoice = data.invoiceDTO;
						if($.isArray(dataInvoice)){
							$.each(dataInvoice, function(){
								this.status = '<span id="'+this.id+'">' + $("#invoice-status-"+this.status).html() + '<span>';
								this.buttonView = '<a href="/invoice/'+this.id+'/view">'+ $("#invoice-view-button").html() + '</a>';
								this.buttonEdit = '<a href="/invoice/'+this.id+'/edit">'+ $("#invoice-edit-button").html() + '</a>';
							});
						}
						else{
							dataInvoice.status = '<span id="'+this.id+'">' + $("#invoice-status-"+dataInvoice.status).html() + '<span>';
							dataInvoice.buttonView = '<a href="/invoice/'+dataInvoice.id+'/view">'+ $("#invoice-view-button").html() + '</a>';
							dataInvoice.buttonEdit = '<a href="/invoice/'+dataInvoice.id+'/edit">'+ $("#invoice-edit-button").html() + '</a>';
						}
						parent.table.reload(dataInvoice);
					}
				}
			});
			return this;
		}
	})
	
	//************************************************************//
	//					Invoice creation modal window			  //
	//************************************************************//
	
	InvoiceCreationView = Backbone.View.extend({
		el:"#modal_window_invoice",
		
		dates: null,
		
		events:{
			"click .cancel_invoice" : "cancelInvoice",
			"change #organization-selector-mod" : "loadProjects"
		},
	
		initialize: function(){
			var parent = this;
			var template = $("#invoice-creation-window").html();
			var titleTemplate = $("#create-template").html();
			$(this.el).kernely_dialog({
				title: titleTemplate,
				content: template,
				eventNames:'click',
				width:"395px"
			});
			
			this.dates = $( "#from, #to" ).datepicker({
				showOn: "both",
				buttonImage: "/images/icons/calendar_icon.png",
				buttonImageOnly: true,
				defaultDate: "+1w",
				changeMonth: true,
				onSelect: function( selectedDate ) {
				var option = this.id == "from" ? "minDate" : "maxDate",
						instance = $( this ).data( "datepicker" ),
						date = $.datepicker.parseDate(
								instance.settings.dateFormat ||
								$.datepicker._defaults.dateFormat,
								selectedDate, instance.settings );
					parent.dates.not( this ).datepicker( "option", option, date );
				}
			});
			
			$.ajax({
				type: "GET",
				url:"/invoice/organizations",
				success: function(data){
					// Create the views
					if (data != null){
						if ($.isArray(data.organizationDTO)){
							$.each(data.organizationDTO, function(){
								$('#organization-selector-mod')
									.append($('<option>', { value : this.id })
									.text(this.name));
							});

						} else if (data.organizationDTO != null){
							$('#organization-selector-mod')
								.append($('<option>', { value : data.organizationDTO.id })
								.text(data.organizationDTO.name));
						}
					}
				}
			});
		},
		
		cancelInvoice: function(){
			$(this.el).kernely_dialog("close");
		},
		
		loadProjects: function(){
			$('#project-selector-mod').removeAttr("disabled");
			$('#project-selector-mod').html("<option></option>");
			$.ajax({
				type: "GET",
				url:"/invoice/projects",
				data:{organizationId : $('#organization-selector-mod').val()},
				success: function(data){
					// Create the views
					if (data != null){
						if ($.isArray(data.projectDTO)){
							$.each(data.projectDTO, function(){
								$('#project-selector-mod')
						          .append($('<option>', { value : this.id })
						          .text(this.name));
							});

						} else if (data.projectDTO != null){
						     $('#project-selector-mod')
					          .append($('<option>', { value : data.projectDTO.id })
					          .text(data.projectDTO.name));
						}
					}
				}
			});
		},
		
		render: function(){
			$(this.el).kernely_dialog("open");			
			return this;
		}
	})
	
	
	// define the application initialization
	var self = {};
	self.start = function(){
		new InvoiceMainView().render();
	}
	return self;
});

$( function() {
    console.log("Starting invoice application")
    new AppInvoiceMain(jQuery).start();
})