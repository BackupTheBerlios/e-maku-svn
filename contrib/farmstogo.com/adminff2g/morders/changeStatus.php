<?
require('../../functions.php');

#UPDATE ORDER INFO
	$updq = "UPDATE FACTURAS SET ID_ORDER_STATUS=4 WHERE ID_FACTURA=$id_factura";
	mysql_db_query($database,$updq);
?>
<script>
	alert ("Your order has been cancelled");
</script>
<?
if ($flag_page == "rose_farms") {
?>
<script language="Javascript">
  window.location="http://www.rosefarmstogo.com";
</script>
<?
} else {
?>
<script language="Javascript">
  window.location="http://www.flowerfarmstogo.com";
</script>
<?
}
?>