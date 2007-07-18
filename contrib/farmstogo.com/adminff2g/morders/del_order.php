<?
	require ('../../functions.php'); 
	if($oid) {
		mysql_db_query($database,"DELETE FROM FACTURAS WHERE ID_FACTURA=$oid");
		echo mysql_error();
		mysql_db_query($database,"DELETE FROM ITEMS_FACTURA WHERE ID_FACTURA=$oid");		
		echo mysql_error();
	}	
?>	
<script>
	this.opener.location.reload();
	//window.close();
</script>