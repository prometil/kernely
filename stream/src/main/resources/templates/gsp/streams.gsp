<script type="text/javascript" src="/js/streams.js"></script>
<script type="text/html" id="message-template">
<div id="mess-{{id}}" class='mess' style="height:auto;width:630px;border-top:1px solid grey;">
	<div class="mess-left-side" style="width:80px;float:left;text-align:center;">
		<div class="message-image" style="height:50px;width:79px;">
			<img style="width:48px;height:48px;" src='/img/picture.png'/>
		</div>
	</div>
	<div class="mess-right-side" style="width:549px;float:left;">
		<div  class='message-content' style="margin-left:0px;width:548px;min-height:60px;height:auto;">
			<div class="message-buttons" id="buttons{{id}}" style="position:absolute;opacity:0.2;margin-left:410px;margin-top:-5px;width:135px; text-align:right;">
				<img class="favButton" src="/img/favourite.png"/>  <img id="delete{{id}}" class="deleteButton" src="/img/delete.png"/>
			</div>
			{{message}}
		</div>
		<div class="message-author-info" style="text-align:right;padding-right:10px;width:548px;height:20px;">
			<span style="font-style:italic;color:grey;">Posted by {{author}} on {{stream}}, {{date}}</span>
		</div>
		<div class="comment-field" style="width:548px; height:auto;">
			<div id="comments-{{id}}"></div>
			<div id="other_comment{{id}}" style="border-bottom:1px solid grey;padding:3px;"><span style="cursor:pointer;color:grey;" class="loadcomment">View the {{comments}} comment(s)<span/></div>
			<div id="input_comment{{id}}" style="height:auto;padding-top:5px;text-align:right;"><input type="text" value="Comment this message here..." class="input-comment-field-dis" style="width:100%;" /></div>
		</div>
	</div>
	<div style="clear:both;"></div>	
</div>
</script>

<script type="text/html" id="comment-template">
<div id="comment-{{id}}" class='comment' style="margin-top:10px;height:auto;width:540px;border-bottom:1px solid grey;">
	<div class="comment-left-side" style="width:80px;float:left;text-align:center;">
		<div class="comment-image" style="height:50px;width:79px;">
			<img style="width:48px;height:48px;" src='{{commentPicture}}'/>
		</div>
	</div>
	<div class="comment-right-side" style="width:459px;float:left;">
		<div class='comment-content' style="margin-left:0px;width:468px;min-height:40px;height:auto;">
			<div class="comment-buttons" id="comm_buttons{{id}}" style="position:absolute;opacity:0.2;margin-left:330px;margin-top:-5px;width:135px; text-align:right;">
				<img class="deleteCommentButton" src="/img/delete.png"/>
			</div>
			{{comment}}
		</div>
		<div class="comment-author-info" style="text-align:right;padding-right:10px;width:468px;height:20px;">
			<span style="font-style:italic;color:grey;">Posted by {{author}}, {{date}}</span>
		</div>
	</div>
	<div style="clear:both;"></div>
</div>
</script>

<div id="streams">
	<div id="streams-main">
		<h1>Streams</h1>
		<div>
			<textarea id="message-input" style="width:630px;"></textarea>		
			<div class="button-bar" id="combo">
			
			</div>
		</div>
		<div id="streams-messages">
		</div>
	</div>
	<div id="streams-sidebar">
	</div>
	<div id="streams-footer">
	</div>
</div>