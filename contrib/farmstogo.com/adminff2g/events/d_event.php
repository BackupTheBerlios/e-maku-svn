<?php
require ('../../functions.php'); 
mysql_select_db($db,$shopping_db_link);
$rs_query = mysql_query("SELECT msg,image from event_logs WHERE id=".$_GET['id'],$shopping_db_link) or die(mysql_error()); 
$rs = mysql_fetch_assoc($rs_query);
echo $rs["msg"];
if ($rs["image"]) { ?>
	<p><img src="http://www.flowerfarmstogo.com/s/events/<?echo $rs["image"]?>">
<?
}
?>