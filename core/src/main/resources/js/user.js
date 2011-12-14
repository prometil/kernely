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


$(function(){
	console.log("Init");
	window.Todo = Backbone.Model.extend({
	    defaults: function() {
	      return {
	        done:  false,
	        order: Todos.nextOrder()
	      };
	    },
	    toggle: function() {
	      this.save({done: !this.get("done")});
	    }

	});
	window.AppView = Backbone.View.extend({
			el: $("#users"),
			events:{
				"click .create" : "create"
			},
			create: function() {
				console.log("Create");
		    },
		    render: function() {
		    	console.log("Render");
		    }
		
	})
	window.App = new AppView;
});