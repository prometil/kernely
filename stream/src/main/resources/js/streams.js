//the stream app
App = (function($){
	
	// the stream message view
	StreamMessageView = Backbone.View.extend({
		tagName:  "div",
		message : null,
		
		 events: {
		    "mouseover "  : "showOptions",
		    "mouseout "  : "hideOptions"
		  },
		initialize: function(message){
			this.message = message
		},
		render:function(){			
			var template = $("#message-template").html();
			
			var view = {message : this.message.message, date: this.message.date, stream: this.message.streamName, author: this.message.author, date: this.message.timeToDisplay};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
			
		},
		showOptions: function(){
			$(this.el).addClass("selected")
		},
		hideOptions: function(){
			$(this.el).removeClass("selected")
		}
	})
	
	// the global view
	StreamView = Backbone.View.extend({
		flag: 0,
		el: "#streams-main",
		
		events: {
			"click .share-message" : "send"
		},
		initialize:function(){
			var parent = this
			$(window).scroll(function(){
		        if  ($(window).scrollTop() == $(document).height() - $(window).height()){
		        	parent.getMore();
		        }
			});
		},
	
		//add a message with the given element
		addMessage:function(message, before){
			var view = new StreamMessageView(message);
			if(before){
				this.$("#streams-messages").prepend(view.render().el)
			}
			else{
				this.$("#streams-messages").append(view.render().el);
			}
		},
		
		render: function(){
			this.initComboBox();
			this.getMore();
			return this;
		},
		
		send: function(){
			var parent = this
			if ($("#message-input").val()=="")
			{
				alert("You can't send an empty message ! ");
				$("#message-input").val("");
	    		$("#message-input").prop('disabled', false);
			}
			else {
			var message = new Backbone.Model({
				  message: $("#message-input").val(),
				  idStream : $("#combobox").val()				  
			}); 
			$("#message-input").prop('disabled', true)

			$.ajax({
					type:"POST",
					contentType: "application/json; charset=utf-8",
					url:"/streams", 
					data:JSON.stringify(message), 
					dataType:"json",
					success: function(data){
			    		parent.addMessage(data, true)
			    		$("#message-input").val("");
			    		$("#message-input").prop('disabled', false);
			  		}
			});
			
			}
		},
		getMore: function(){
			var parent = this
			var url = "";
			console.log(this.flag);
			if(this.flag == 0){
				url = "/streams/current/messages";
			}
			else{
				url = "/streams/current/messages?last=" + this.flag;
			}
			$.ajax({
				url: url,
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.streamMessageDTO.length > 1){
							$.each(data.streamMessageDTO, function(){
								parent.addMessage(this);
								parent.flag = this.id;
							});
						}
						else{
							parent.addMessage(data.streamMessageDTO);
							parent.flag = data.streamMessageDTO.id;
						}
					}
				}
			});
		},
		
		initComboBox: function(){
			$.ajax({
				type: "GET",
				url:"/streams/combobox",
				dataType:"json",
				success: function(data){
					if(data != null){
						var option = "";
						if(data.streamDTO.length > 1){
							$.each(data.streamDTO, function(index, value){
								option = option + '<option value="' + this.id + '">'+ this.title +'</option>' ;
							});
						}
						else{
							option = '<option value="' + data.streamDTO.id + '">'+ data.streamDTO.title +'</option>' ;
						}
						$("#combo").append('<select name="stream-choice" id="combobox">' + option + '</select>');
						$("#combo").append('<a id="share-message"  class="button share-message" href="javascript:void(0)" >Share</a>');
					}
				}
			});
		}
	})
	 
	// define the application initialization
	var self = {};
	self.start = function(){
		new StreamView().render()
	}
	return self;
})

$( function() {
	console.log("Starting streams application")
	new App(jQuery).start();
})