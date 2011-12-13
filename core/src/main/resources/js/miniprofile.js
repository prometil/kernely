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
 */
AppMiniProfile = (function($){
        // the mini profile view at the top-right on each page
        MenuProfileView = Backbone.View.extend({
                el:"#menu_header_profile",
                
                vfullname: null,
                vimage: null,
                vmail: null,
                vuser: null,
                
                events: {
                        "click .displayProfilePU" : "showMiniProfile",
                        "mouseover .displayProfilePU" : "showOver",
                        "mouseout .displayProfilePU" : "hideOver",
                },
                initialize: function(){
                        $.ajax({
                                url:"/user/current",
                                success:function(data){
                                        vuser = data.username;
                                        $.ajax({
                                                url:"/user/" + data.username,
                                                success:function(data){
                                                        vfullname = data.firstname + " " + data.lastname;
                                                        vimage = "/images/"+data.image;
                                                        vmail = data.email;
                                                        
                                                        if(data.image == null || data.image =='undefined' || data.image == '' || data.image=='null'){
                                                                vimage = "/images/default_user.png"
                                                        }
                                                        $("#username_menu").text(vfullname + " (" + vuser + ")");
                                                        $("#userimg_menu").html("<img class='img_miniprofile' style='width:28px;height:28px;' src='"+ vimage +"'/>");
                                                }
                                        });
                                }
                        });
                        
                },
                showOver: function(){
                        // Put some style here.
                },
                hideOver: function(){
                        // Put some style here.
                },
                showMiniProfile: function(){
                        if ($("#profile_popup").is(':hidden')){
                                //$("#profile_popup").slideDown(200)
                                $("#profile_popup").show()
                                var view = new ProfilePopUpView(vfullname, vuser, vimage, vmail);
                                view.render();
                                
                        }
                        else{
                                //$("#profile_popup").slideUp(200)
                                $("#profile_popup").hide()
                        }
                },
                render: function(){
                        return this;
                }
        })
        
        
        ProfilePopUpView = Backbone.View.extend({
                el: "#profile_popup",
                
                vusername: null,
                vimage: null,
                vmail: null,
                vfullname: null,
                
                initialize:function(fullname, username, image, mail){
                
                        this.vfullname = fullname;
                        this.vusername = username;
                        this.vimage = image;
                        this.vmail = mail;
                },
                render: function(){
                        var template = $("#profile-template").html();
                        var view = {fullname : this.vfullname, mail: this.vmail, image: this.vimage, username: this.vusername};
                        var html = Mustache.to_html(template, view);
                        $(this.el).html(html);
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