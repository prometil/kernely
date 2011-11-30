<script type="text/javascript" src="/js/streams.js"></script>
<script type="text/html" id="message-template">
<div class='mess'>
	<div class='message-image'>
		<img src='/images/picture.jpg'/>
	</div>
	<div class='message-content'>
		{{message}}
	</div>
</div>
</script>


<div id="streams">
	<div id="streams-main">
		<h1>Streams</h1>
		<div>
			<textarea id="message-input"></textarea>
			<div class="button-bar">
				<a id="share-message"  class="button share-message" href="javascript:void(0)" >Share</a>
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