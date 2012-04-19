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

                            	// If the plugin doesn't have an admin page, then do nothing
                            	if(this.adminPages != null && typeof(this.adminPages) != "undefined"){
	                            	if (this.adminPages.length > 1){
	                            		$.each(this.adminPages, function() {
	                            			var view = new PluginAdminLinkView(this.name, this.path);
	                            			var html = view.render();
	                            		});
	                            	} else {
	                        			var view = new PluginAdminLinkView(this.adminPages.name, this.adminPages.path);
	                        			var html = view.render();
	                            	}
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
			console.log(admin+" found : "+adminpath);


			this.vadmin = admin;
			this.vadminpath = adminpath;
		},
		render: function(){
			var template = "<a href='{{adminpath}}'>{{admin}}</a><br/>";
			var view = {adminpath : this.vadminpath , admin : this.vadmin};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
            $(this.el).appendTo($("#admin_sidebar_container"));
            console.log($(this.el).html());
			console.log($("#admin_sidebar_container").html());
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