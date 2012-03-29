AppInvoiceEdit = (function($){
	var invoiceLines = new Array();
	
	InvoiceEditionMainView = Backbone.View.extend({
		el:"#invoice-edition",
		events:{
		
		},
		initialize: function(){
			
		},
		render: function(){
			new InvoiceGeneralInfoView().render();
			new InvoiceDetailsView().render();
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
					if(data.invoiceLineDTO.length > 1){
						$.each(data.invoiceLineDTO, function(){
							view = new InvoiceLineCreateView(this.id, this.designation, this.quantity, this.unitPrice, this.amount).render();
							$(parent.el).append(view.el);
							invoiceLines.push(view);
						});
					}
					else{
						view = new InvoiceLineCreateView(data.invoiceLineDTO.id, data.invoiceLineDTO.designation, data.invoiceLineDTO.quantity, data.invoiceLineDTO.unitPrice, data.invoiceLineDTO.amount).render();
						$(parent.el).append(view.el);
						invoiceLines.push(view);
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
		
		events:{
			"click .delete-invoice-line" : "deleteLine",
			"change .quantity-field, .unitprice-field" : "processAmount"
				
		},
		initialize: function(id, description, quantity, unitPrice, amount){
			this.id = id;
			this.description = description;
			this.quantity = quantity;
			this.unitPrice = unitPrice;
			this.amount = amount;
		},
		deleteLine: function(){
			$(this.el).remove();
		},
		processAmount: function(){
			var amount = $(this.el).find(".quantity-field").val() * $(this.el).find(".unitprice-field").val();
			$(this.el).find(".line-amount").text(amount);
			console.log(this.id);
		},
		saveLine: function(){
			var parent = this;
			var json = '{"id" : 0, "designation" : "'+ $('input[name*="designation-field"]').val()
					+ '", "quantity" : "' + $('input[name*="quantity-field"]').val()
					+ '", "unitPrice" : "' + $('input[name*="unitprice-field"]').val()
					+ '", "invoiceId" : "'+ $('#invoice-visu-i').text() +'" }';
			
			$.ajax({
				type: "POST",
				url:"/invoice/line/create",
				data : json,
				dataType: "json",
				contentType: "application/json; charset=utf-8",
				processData: false,
				success: function(data){
					var template = $("#invoice-line-template").html();
					var view = {description:data.designation, quantity: data.quantity, unitprice: data.unitPrice, amount: data.amount};
					var html = Mustache.to_html(template, view);
					$(parent.el).html(html);
				}
				
			});
		},
		render: function(){
			var template = $("#invoice-line-editable-template").html();
			var view = {description: this.description, quantity: this.quantity, unitprice : this.unitPrice, amount: this.amount};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
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