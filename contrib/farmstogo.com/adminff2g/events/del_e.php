<?php
require ('../../functions.php'); 
	
$rs = mysql_db_query($db,"delete from event_logs WHERE id=$id"); 

?>
<script>
	this.opener.location.reload();
	window.close();
</script>
