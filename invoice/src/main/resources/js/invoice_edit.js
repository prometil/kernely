AppInvoiceEdit = (function($){
	var invoiceLines = new Array();
	var invoiceDetails = null;
	
	InvoiceEditionMainView = Backbone.View.extend({
		el:"#invoice-edition",
		events:{
			"click #delete-invoice" : "deleteInvoice"
		},
		initialize: function(){
			
		},
		deleteInvoice: function(){
			var parent = this;
			var template = $("#confirm-remove-invoice-template").html();
			
			var view = {invoiceNumber: this.number};
			var html = Mustache.to_html(template, view);
			
			$.kernelyConfirm($("#delete-template").text(), html,this.confirmDeleteInvoice, this);
		},
		confirmDeleteInvoice: function(parent){
			$.ajax({
				type: "GET",
				url:"/invoice/delete",
				data:{invoiceId : $("#invoice-visu-i").text()},
				contentType: "application/json; charset=utf-8",
				success: function(data){
					window.location = "/invoice";
				}
			});
		},
		render: function(){
			new InvoiceGeneralInfoView().render();
			invoiceDetails = new InvoiceDetailsView().render();
		}
	})
	
	InvoiceGeneralInfoView = Backbone.View.extend({
		el:"#invoice-general-informations",
		
		dates: null,
		
		events:{
			
		},
		initialize: function(){
			
		},
		render: function(){
			var parent = this;
			this.dates = $( "#invoice-publication, #invoice-term" ).datepicker({
				showOn: "both",
				buttonImage: "/images/icons/calendar_icon.png",
				buttonImageOnly: true,
				defaultDate: "+1w",
				changeMonth: true,
				onSelect: function( selectedDate ) {
				var option = this.id == "invoice-publication" ? "minDate" : "maxDate",
						instance = $( this ).data( "datepicker" ),
						date = $.datepicker.parseDate(
								instance.settings.dateFormat ||
								$.datepicker._defaults.dateFormat,
								selectedDate, instance.settings );
				parent.dates.not( this ).datepicker( "option", option, date );
			}
			});
		}
	})
	
	InvoiceDetailsView = Backbone.View.extend({
		el:"#invoice-details",
		
		tableView : null,
		
		events:{
			"click #new-invoice-line" : "addLine"
		},
		initialize: function(){
			tableView = new InvoiceLineTableView();
		},
		addLine:function(){
			tableView.addLine();
		},
		render: function(){
			tableView.render();
			return this;
		}
	})
	
	InvoiceLineTableView = Backbone.View.extend({
		el:"#invoice-lines",
		events:{
		
		},
		addLine: function(){
			var view = new InvoiceLineCreateView(0).render();
			$(this.el).append(view.el);
			invoiceLines.push(view);
		},
		initialize: function(){
			
		},
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/invoice/lines",
				data:{invoiceId: $("#invoice-visu-i").text()},
				success: function(data){
					var view;
					if(data != null){
						if(data.invoiceLineDTO.length > 1){
							$.each(data.invoiceLineDTO, function(){
								view = new InvoiceLineCreateView(this.id, this.designation, this.quantity, this.unitPrice, this.amount, this.vat).render();
								$(parent.el).append(view.el);
								invoiceLines.push(view);
							});
						}
						else{
							view = new InvoiceLineCreateView(data.invoiceLineDTO.id, data.invoiceLineDTO.designation, data.invoiceLineDTO.quantity, data.invoiceLineDTO.unitPrice, data.invoiceLineDTO.amount, data.invoiceLineDTO.vat).render();
							$(parent.el).append(view.el);
							invoiceLines.push(view);
						}
					}
				}
			});
			return this;
		}
	})
	
	InvoiceLineCreateView = Backbone.View.extend({
		tagName:"tr",
		
		id:0,
		description : "",
		quantity : 0,
		unitPrice : 0,
		amount : 0,
		vat : 0,
		
		events:{
			"click .delete-invoice-line" : "deleteLine",
			"change .quantity-field, .unitprice-field" : "processAmount"
				
		},
		initialize: function(id, description, quantity, unitPrice, amount, vat){
			this.id = id;
			this.description = description;
			this.quantity = quantity;
			this.unitPrice = unitPrice;
			this.amount = $.round(amount);
			this.vat = vat;
		},
		deleteLine: function(){
			$(this.el).remove();
		},
		processAmount: function(){
			var amount = $.round($(this.el).find(".quantity-field").val() * $(this.el).find(".unitprice-field").val());
			$(this.el).find(".line-amount").text(amount);
		},
		render: function(){
			var template = $("#invoice-line-editable-template").html();
			var view = {id: this.id, description: this.description, quantity: this.quantity, unitprice : this.unitPrice, amount: this.amount};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			var parent = this;
			$.ajax({
				url:"/invoice/vat",
				dataType:"json",
				success:function(data){
					var options;
					var valueInConf = false;
					if($.isArray(data.vatDTO)){
						$.each(data.vatDTO, function(){
							if(this.value == parent.vat){
								options += '<option selected="selected" value="'+this.value+'">'+this.value+'</option>';
								valueInConf = true;
							}
							else{
								options += '<option value="'+this.value+'">'+this.value+'</option>';
							}
						});
						if(!valueInConf && typeof(parent.vat) != "undefined"){
							options += '<option selected="selected" value="'+parent.vat+'">'+parent.vat+'</option>';
						}
						var select = document.createElement("select");
						$(select).addClass("vat-field");
						$(select).addClass("kernely_input");
						$(select).attr("name", "vat-field[]");
						$(select).append(options);
						$(parent.el).find(".vat-column").append($(select)).append("%");
					}
					else{
						if(data.vatDTO.value != parent.vat && parent.vat != null){
							options += '<option selected="selected" value="'+parent.vat+'">'+parent.vat+'</option>';
							options += '<option value="'+data.vatDTO.value+'">'+data.vatDTO.value+'</option>';
							var select = document.createElement("select");
							$(select).addClass("vat-field");
							$(select).addClass("kernely_input");
							$(select).attr("name", "vat-field[]");
							$(select).append(options);
							$(parent.el).find(".vat-column").append($(select)).append("%");
						}
						else{
							var input = document.createElement("input");
							$(input).addClass("vat-field");
							$(input).addClass("kernely_input");
							$(input).attr("name", "vat-field[]");
							$(input).attr("readonly", "readonly");
							$(input).attr("value", data.vatDTO.value);
							$(input).css("width","40px");
							$(parent.el).find(".vat-column").append($(input)).append("%");
						}
					}
				}
			});
			return this;
		}
	})
	
	
		
	// define the application initialization
	var self = {};
	self.start = function(){
		new InvoiceEditionMainView().render();
	}
	return self;
});

$( function() {
    console.log("Starting invoice edition application")
    new AppInvoiceEdit(jQuery).start();
})