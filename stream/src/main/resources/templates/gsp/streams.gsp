<script type="text/javascript" src="/js/streams.js"></script>
<script type="text/html" id="message-template">
<div class='mess'>
	<div class='message-image'>
		<img style="width:48px;height:48px;" src='/img/picture.png'/>
	</div>
	<div class='message-content'>
		{{message}}
	</div>
	<div style="text-align:right;padding-right:10px;">
		<span style="font-style:italic;color:grey;">Posted by {{author}} on {{stream}}, {{date}}</span>
	</div>
</div>
</script>


<div id="streams">
	<div id="streams-main">
		<h1>Streams</h1>
		<div>
			<textarea id="message-input"></textarea>		
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