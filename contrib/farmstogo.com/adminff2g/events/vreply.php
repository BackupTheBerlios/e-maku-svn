<?php
require ('../../functions.php'); 
	
$rs = mysql_db_query($db,"SELECT reply from event_logs WHERE id=$id"); 
$rs = mysql_fetch_array($rs);
echo $rs["reply"];
?>