AppInvoiceMain = (function($){
	
	var tableView = null;
	
	InvoiceMainView = Backbone.View.extend({
		el:"#invoicing-main",
		
		events:{
		
		},
		
		initialize: function(){
		},
		
		render: function(){
			new InvoiceButtonView().render();
			tableView = new InvoiceTableView().render();
			return this;
		}
	})
	
	InvoiceButtonView = Backbone.View.extend({
		el:"#invoicing-buttons-bar",
		
		creationWindow : null,
		
		events:{
			"change #organization-selector" : "loadProjects",
			"change #organization-selector, #project-selector" : "refreshInvoices",
			"click #new-invoice" : "openCreationWindow"
		},
		
		initialize: function(){
			this.creationWindow = new InvoiceCreationView();
		},
		
		openCreationWindow: function(){
			this.creationWindow.render();
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
		el:"#invoices",
		
		events:{
		
		},
		
		initialize: function(){
			
		},
		
		render: function(){
			var parent = this;
			$(this.el).html("");
			$.ajax({
				type:"GET",
				url:"/invoice/specific",
				data:{organizationId : $('#organization-selector').val(), projectId : $('#project-selector').val()},
				success: function(data){
					if(data != null){
						if(data.invoiceDTO.length > 1){
							var elt = $(parent.el);
							$.each(data.invoiceDTO, function(){
								elt.append(new InvoiceTableLineView(this.id, this.status, this.code, this.organizationName, this.projectName, this.amount).render().el);
							});
						}
						else{
							$(parent.el).append(new InvoiceTableLineView(data.invoiceDTO.id, data.invoiceDTO.status, data.invoiceDTO.code, data.invoiceDTO.organizationName, data.invoiceDTO.projectName, data.invoiceDTO.amount).render().el);
						}
					}
				}
			});
			return this;
		}
	})
	
	InvoiceTableLineView = Backbone.View.extend({
		tagName:"tr",
		
		id: null,
		status: null,
		number: null,
		client: null,
		project: null,
		amount: null,
		
		events:{
			"click .delete-invoice" : "deleteinvoice",
			"click .invoice-publish" : "publishinvoice",
			"click .invoice-paid" : "paidinvoice",
			"click .invoice-unpaid" : "unpaidinvoice"
		},
		
		initialize: function(id, status, number, client, project, amount){
			this.id = id;
			this.status = status;
			this.number = number;
			this.client = client;
			this.project = project;
			this.amount = amount;
		},
		
		publishinvoice: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/invoice/publish",
				data:{invoiceId : parent.id},
				success: function(data){
					parent.status = data.status;
					parent.render();
				}
			});
		},
		
		paidinvoice: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/invoice/paid",
				data:{invoiceId : parent.id},
				success: function(data){
					parent.status = data.status;
					parent.render();
				}
			});
		},
		
		unpaidinvoice: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/invoice/unpaid",
				data:{invoiceId : parent.id},
				success: function(data){
					parent.status = data.status;
					parent.render();
				}
			});
		},
		
		deleteinvoice: function(){
			var parent = this;
			var template = $("#confirm-remove-invoice-template").html();
			
			var view = {invoiceNumber: this.number};
			var html = Mustache.to_html(template, view);
			
			var answer = confirm(html);
			if (answer){
				$.ajax({
					type: "GET",
					url:"/invoice/delete",
					data:{invoiceId : parent.id},
					success: function(data){
						$(parent.el).remove();
					}
				});		
			}
		},
				
		render: function(){
			var template = $("#invoice-line-template").html();
			var templateStatus = $("#invoice-status-" + this.status).html();
			var statusStyle;

			if(this.status == 0){
				statusStyle = "pending";
			}
			else if(this.status == 1){
				statusStyle = "paid";
			}
			else if(this.status == 2){
				statusStyle = "unpaid";
			}
			else if(this.status == 3){
				statusStyle = "unpublished";
			}
			var view = {status : templateStatus, statusStyle: statusStyle, number : this.number, client : this.client, project : this.project, amount : this.amount, invoiceId: this.id};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			
			if(this.status == 0){
				$(this.el).find(".invoice-publish").attr("disabled", "disabled");
			}
			else if(this.status == 1){
				$(this.el).find(".invoice-paid").attr("disabled", "disabled");
				$(this.el).find(".invoice-publish").attr("disabled", "disabled");
			}
			else if(this.status == 2){
				$(this.el).find(".invoice-publish").attr("disabled", "disabled");
				$(this.el).find(".invoice-unpaid").attr("disabled", "disabled");
			}
			else if(this.status == 3){
				$(this.el).find(".invoice-paid").attr("disabled", "disabled");
				$(this.el).find(".invoice-unpaid").attr("disabled", "disabled");
			}
			
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
			"click .cancel_invoice,  .closeModal" : "closemodal",
			"click .create_invoice" : "createinvoice",
			"change #organization-selector-mod" : "loadProjects"
		},
	
		initialize: function(){
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
       		$("#modal_window_invoice").css('top',  winH/2-$("#modal_window_invoice").height()/2);
     		$("#modal_window_invoice").css('left', winW/2-$("#modal_window_invoice").width()/2);
     		$("#modal_window_invoice").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_invoice").fadeIn(500);
		},
		
		closemodal: function(){
			$('#modal_window_invoice').hide();
       		$('#mask').hide();
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
		
		createinvoice: function(){
			var parent = this;
			var json = '{"id":"0","object":"","projectId":"'+ $('#project-selector-mod').val()
			           +'","datePublication":"'+ this.dates[0].value
			           +'","dateTerm":"'+ this.dates[1].value
			           +'"}';
			$.ajax({
				type:"POST",
				url:"/invoice/create",
				data: json,
				dataType: "json",
				contentType: "application/json; charset=utf-8",
				processData: false,
				success: function(data){
				console.log(data);
					if(data.result=="Ok"){
						parent.closemodal();
						var successHtml = $("#invoice-creation-success-template").html();
						$.writeMessage("success",successHtml);
						tableView.render();
					}
					else{
						$.writeMessage("error", data.result, "#errors_message");
					}
				}
			});
		},
		
		render: function(){
			var parent = this;
			this.showModalWindow();
			var html = $("#invoice-creation-window").html();
			$(this.el).html(html);
			
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
			this.dates = $( "#from, #to" ).datepicker({
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