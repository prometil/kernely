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
                                  var view = new PluginAdminLinkView(this.admin, this.adminpath);
                                  var html = view.render();
                            });
                   }
            });
			
		},
		
		render: function(){
			return this;
		}
	})

	PluginAdminLinkView = Backbone.View.extend({
		tagName:"a",
		classnam:"loader",
		
		vadmin: null,
		vadminpath: null,
		
		events: {
			"click" : "showAdmin",
		},
		initialize:function(admin, adminpath){
			this.vadmin = admin;
			this.vadminpath = adminpath;
		},
		render: function(){
			var template = "{{admin}}";
			var view = {admin : this.vadmin};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
            $(this.el).appendTo($("#admin_sidebar_container"));
			return this;			
		},
		showAdmin: function(){
			 $("#admin_panel_container").load(this.vadminpath);
			 return false;
		},
		
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