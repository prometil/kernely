<script type="text/javascript" src="/js/profile.js"></script>

<div id="profile_header" style="height:50px;text-align:center;font-size:20px;font-weight:bold;">${details.firstname} ${details.lastname}'s profile page</div>
<div id="profile_top_container" style="height:560px;">

<script type="text/html" id="profile">
		<td>{{input}}</td>
		<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
		<td><span id="button_{{input}}"><img class="button edit_button" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td>
</script>


	<div id="profile_image" style="padding-top:10px;height:550px;width:380px;float:left;text-align:center;border-top:1px solid black;">
		<img src="/images/${details.image}" id="image_name" name="${details.image}" style="max-width:295px; max-height: 245px;"/>
		<form action="/user/upload" method="post" enctype="multipart/form-data">
 			<input type="file" name="file"/>
		   <input type="submit" value="Upload It" />
		</form>
	</div>
	<div id="profile_information" style="height:550px;width:545px;float:left;text-align:left;border-left:1px solid black; border-top:1px solid black; padding:15px;">
		<table style="width:100%" id="profile_table">
			<tr><td>Civility</td><td><span id="profile_civility" style="display: none;">${details.civility}</span><input type="radio" class="edit_button_civility" name="civility" value="1"> Mister <input type="radio" class="edit_button_civility" name="civility" value="2"> Madam <input type="radio" class="edit_button_civility" name="civility" value="3"> Miss </td><td></td></tr>		
		</table>
	</div>
</div>
<div style="clear:both;"></div>
<div id="profile_bottom_container"><div>