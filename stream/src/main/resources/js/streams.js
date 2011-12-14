/*
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */
//the stream app
App = (function($){
	
	// the stream message view
	StreamMessageView = Backbone.View.extend({
		tagName:  "div",
		message : null,
		commentLoaded : false,
		
		events: {
		    "mouseover "  : "showOptions",
		    "mouseout "  : "hideOptions",
		    "mouseover .message-buttons" : "showButtons",
		    "mouseout .message-buttons" : "hideButtons",
		    "click .loadcomment": "loadComment",
		    "click .hidecomment": "hideComment",
		    "click .input-comment-field-dis": "showInputComment",
		    "click .cancelButton" : "hideInputComment",
		    "click .share-comment" : "sendComment",
		    "click .deleteButton" : "deleteMessage"
		},
		initialize: function(message){
			this.message = message;
		},
		render:function(){			
			var template = $("#message-template").html();
			
			var view = {id: this.message.id, message : this.message.message, stream: this.message.streamName, author: this.message.author, date: this.message.timeToDisplay, comments: this.message.nbComments};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			
			if (this.message.deletion == "false"){
				var chain = "#delete"+this.message.id;
				this.$(chain).remove();
			}
			return this;
		},
		showInputComment: function(){
			if(!this.commentLoaded){
				this.loadComment();
			}
			$("#input_comment"+this.message.id).html("<textarea class='comment-input' id='comment-input"+this.message.id+"' style='width:540px; margin-bottom:10px;'></textarea><a style='cursor:pointer;' class='cancelButton'>Cancel</a>  <a class='button share-comment' href='javascript:void(0)' >Comment</a>");
			$("#comment-input"+this.message.id).focus();
			
		},
		hideInputComment: function(){
			$("#input_comment"+this.message.id).html("<input type='text' value='Comment this message here...' class='input-comment-field-dis' style='width:100%;' />");
		},
		loadComment: function(){
			var parent = this;
			if(!this.commentLoaded){
				$.ajax({
					url:"/streams/" + parent.message.id + "/comments",
					dataType:"json",
					success: function(data){
						var comment;
						if(data != null){
							if(data.streamMessageDTO.length > 1){
								$.each(data.streamMessageDTO, function(){
									comment = new StreamCommentView(this);
									$("#comments-" + parent.message.id).append(comment.render().el);
								});
							}
							else{
								$("#comments-" + parent.message.id).append(new StreamCommentView(data.streamMessageDTO).render().el);
							}
						}
						parent.commentLoaded = true;
					}
				});
			}
			else{
				$("#comments-" + parent.message.id).slideDown(1000);
			}
			$("#other_comment" + parent.message.id).html("<span style='cursor:pointer;color:grey;' class='hidecomment'>Hide the comment(s)<span/>");
		},
		hideComment: function(){
			$("#comments-" + this.message.id).slideUp(1000);
			$("#other_comment" + this.message.id).html("<span style='cursor:pointer;color:grey;' class='loadcomment'>View the "+this.message.nbComments+" comment(s)<span/>");
			parent.commentLoaded = false;
		},
		showButtons: function(){
			$("#buttons" + this.message.id).fadeTo("fast",1);
		},
		hideButtons: function(){
			$("#buttons" + this.message.id).fadeTo("fast",0.2);
		},
		showOptions: function(){
			$(this.el).addClass("selected")
		},
		hideOptions: function(){
			$(this.el).removeClass("selected")
		},
		sendComment: function(){
			var parent = this
			if ($("#comment-input"+this.message.id).val()=="")
			{
				alert("You can't send an empty message!");
				$("#comment-input"+this.message.id).val("");
	    		$("#comment-input"+this.message.id).prop('disabled', false);
			}
			else {
			var message = new Backbone.Model({
				  message: $("#comment-input"+parent.message.id).val(),
				  idStream : parent.message.streamId,
				  idMessageParent : parent.message.id
			}); 
			$("#comment-input"+parent.message.id).prop('disabled', true)

			$.ajax({
					type:"POST",
					contentType: "application/json; charset=utf-8",
					url:"/streams/comment", 
					data:JSON.stringify(message), 
					dataType:"json",
					success: function(data){
						$("#comments-" + parent.message.id).append(new StreamCommentView(data).render().el);
			    		$("#comment-input"+parent.message.id).val("");
			    		$("#comment-input"+parent.message.id).prop('disabled', false);
			  		}
			});
			
			}
		},
		deleteMessage: function(){
			var parent = this;
			if (this.message.deletion == "false"){
				alert("You can't delete this message.");
			} else {
				var answer = confirm("Do yo really want to delete this message ?");
				if (answer){
					$.ajax({
						type:"POST",
						url:"/streams/delete/"+parent.message.id, 
						dataType:"json",
						success: function(data){
				  		}
					});
					$("#mess-"+parent.message.id).remove();
				}
			}
		}
	})
	
	// the stream comment view
	StreamCommentView = Backbone.View.extend({
		tagName:  "div",
		comment : null,
		
		events: {
		    "mouseover "  : "showOptions",
		    "mouseout "  : "hideOptions",
		    "mouseover .comment-buttons" : "showButtons",
		    "mouseout .comment-buttons" : "hideButtons",
		    "click .deleteCommentButton" : "deleteComment"
		},
		initialize: function(comment){
			this.comment = comment
		},
		render:function(){			
			var template = $("#comment-template").html();
			
			var view = {id: this.comment.id, commentPicture: "/img/picture.png", comment : this.comment.message, author: this.comment.author, date: this.comment.timeToDisplay};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			
			if (this.comment.deletion == "false"){
				var chain = "#delete"+this.comment.id;
				this.$(chain).remove();
			}
			
			return this;
		},
		showButtons: function(){
			$("#comm_buttons" + this.comment.id).fadeTo("fast",1);
		},
		hideButtons: function(){
			$("#comm_buttons" + this.comment.id).fadeTo("fast",0.2);
		},
		showOptions: function(){
			$(this.el).addClass("comment_selected")
		},
		hideOptions: function(){
			$(this.el).removeClass("comment_selected")
		},
		deleteComment: function(){
			var parent = this;
			if (this.comment.deletion == "false"){
				alert("You can't delete this comment.");
			} else {
				var answer = confirm("Do yo really want to delete this comment?");
				if (answer){
					$.ajax({
						type:"POST",
						url:"/streams/delete/"+parent.comment.id, 
						dataType:"json",
						success: function(data){
				  		}
					});
					$("#comment-"+parent.comment.id).remove();
				}
			}
		}
	})
	
	
	// the global view
	StreamView = Backbone.View.extend({
		flag: 0,
		el: "#streams-main",
		nbMessages:0,
		nbMessagesLoaded:0,
		
		events: {
			"click .share-message" : "send"
		},
		initialize:function(){
			var parent = this
			$.ajax({
				type: 'GET',
				url:'/streams/current/nb',
				dataType:"json",
				success: function(data){
					parent.nbMessages = data.count;
				}
			});
			$(window).scroll(function(){
		        if  ($(window).scrollTop() == $(document).height() - $(window).height() && parent.nbMessagesLoaded < parent.nbMessages){
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
				alert("You can't send an empty message!");
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
			var parent = this;
			var url = "";
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
								parent.nbMessagesLoaded ++;
							});
						}
						else{
							parent.addMessage(data.streamMessageDTO);
							parent.flag = data.streamMessageDTO.id;
							parent.nbMessagesLoaded ++;
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
		new StreamView().render();
	}
	return self;
})

$( function() {
	console.log("Starting streams application")
	new App(jQuery).start();
})