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
AppMiniProfile = (function($){
        // the mini profile view at the top-right on each page
        MenuProfileView = Backbone.View.extend({
                el:"#menu_header_profile",
                
                events: {
                        "click .displayProfilePU" : "showMiniProfile",
                },
                initialize: function(){
                	var view = new ProfilePopUpView();
                    view.render();   
                },
                showMiniProfile: function(){
                        if ($("#profile_popup").is(':hidden')){
                                $("#profile_popup").show();
                                
                        }
                        else{
                                $("#profile_popup").hide();
                        }
                },
                test: function(){console.log("tests")},
                render: function(){
                        return this;
                }
        })
        
        
        ProfilePopUpView = Backbone.View.extend({
                el: "#profile_popup",
                
                initialize:function(){
        			var parent = this;
		        	$(this.el).hover(function() {
		        		// nothing to do
		    			}, function(){
		    				$(parent.el).stop(true,true);
		    				$(parent.el).slideUp(1500);
		    			});
        		},
                
                hide: function(){
                	$(this.el).hide();
                },
                
                render: function(){
                        var template = $("#profile-template").html();
                        $(this.el).html(template);
                        return this;                        
                }
                
        })

        // define the application initialization
        var self = {};
        self.start = function(){
                new MenuProfileView().render()
        }
        return self;
})

$( function() {
        console.log("Starting profile menu application");          
        new AppMiniProfile(jQuery).start();
})