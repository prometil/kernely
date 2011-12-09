AppAdmin = (function($){
	// the admin view
	AdminView = Backbone.View.extend({
		el:"#admin_sidebar_container",
		
		initialize: function(){
			$.ajax({
				   type:"GET",
                   url:"/admin/plugins",
                   dataType:"json",
                   success: function(data){
                            $.each(data.pluginDTO, function() {
                            	if (this.adminPages.length > 1){
                            		$.each(this.adminPages, function() {
                            			var view = new PluginAdminLinkView(this.name, this.path);
                            			var html = view.render();
                            		});
                            	} else {
                        			var view = new PluginAdminLinkView(this.adminPages.name, this.adminPages.path);
                        			var html = view.render();
                            	}
                            });
					}
            });
			
		},
		
		render: function(){
			return this;
		}
	})

	PluginAdminLinkView = Backbone.View.extend({
		tagName:"div",
		classname:"loader",
		
		vadmin: null,
		vadminpath: null,
		
		events: {

		},
		initialize:function(admin, adminpath){
			this.vadmin = admin;
			this.vadminpath = adminpath;
		},
		render: function(){
			var template = "<a href='{{adminpath}}'>{{admin}}</a><br/>";
			var view = {adminpath : this.vadminpath , admin : this.vadmin};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
            $(this.el).appendTo($("#admin_sidebar_container"));
			return this;			
		}
		
	})

	// define the application initialization
	var self = {};
	self.start = function(){
		new AdminView().render()
	}
	return self;
})

$( function() {
	new AppAdmin(jQuery).start();
})