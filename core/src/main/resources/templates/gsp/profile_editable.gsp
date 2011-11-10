<script type="text/javascript" src="/js/profile.js"></script>
<div id="profile_header" style="height:50px;text-align:center;font-size:20px;font-weight:bold;">${username}'s profile page</div>
<div id="profile_top_container" style="height:250px;">
	<div id="profile_image" style="height:249px;width:300px;float:left;text-align:center;">
		<img src="${image}" style="max-width:295px; max-height: 245px;"/>
	</div>
	<div id="profile_information" style="height:249px;width:655px;float:left;">
		Mail address : <span id="profile_mail"><a href="mailto:${mail}">${mail}</a></span><span id="button_mail"><img class="button edit_button_mail" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		<textarea readonly="readonly" style="width:650px;">${description}</textarea>
	</div>
</div>
<div id="profile_bottom_container"><div>