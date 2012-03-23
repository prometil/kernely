AppInvoiceEdit = (function($){
	InvoiceVisualizationMainView = Backbone.View.extend({
		el:"#invoice-visualization",
		events:{
		
		},
		initialize: function(){
			
		},
		render: function(){
			new InvoiceGeneralInfoView().render();
			new InvoiceDetailsView().render();
			new InvoiceHistoryView().render();
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
			$(this.el).append(new InvoiceLineCreateView().render().el);
		},
		initialize: function(){
			
		},
		render: function(){
			return this;
		}
	})
	
	InvoiceLineCreateView = Backbone.View.extend({
		tagName:"tr",
		
		description : null,
		quantity : null,
		unitPrice : null,
		amount : null,
		
		events:{
			"click .valid-invoice-line" : "saveLine"
		},
		initialize: function(){
			
		},
		saveLine: function(){
			var parent = this;
			var json = '{"id" : 0, "designation" : "'+ $('input[name*="designation-field"]').val()
					+ '", "quantity" : "' + $('input[name*="quantity-field"]').val()
					+ '", "unitPrice" : "' + $('input[name*="unitprice-field"]').val()
					+ '", "invoiceId" : "'+ $('#invoice-visu-i').text() +'" }';
			console.log($('#invoice-visu-i').text());
			
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
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		}
	})
	
	InvoiceHistoryView = Backbone.View.extend({
		el:"#invoice-history",
		events:{
		
		},
		initialize: function(){
			
		},
		render: function(){
			
		}
	})
	
	InvoiceHistoryLineView = Backbone.View.extend({
		tagName:"tr",
		events:{
		
		},
		initialize: function(){
			
		},
		render: function(){
			
		}
	})
		
	// define the application initialization
	var self = {};
	self.start = function(){
		new InvoiceVisualizationMainView().render();
	}
	return self;
});

$( function() {
    console.log("Starting invoice edition application")
    new AppInvoiceEdit(jQuery).start();
})