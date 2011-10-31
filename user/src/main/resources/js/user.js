

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
		      },
		
		}
		)
		window.App = new AppView;
});