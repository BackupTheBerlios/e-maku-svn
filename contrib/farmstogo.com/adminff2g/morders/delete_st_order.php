<?
	require ('../../functions.php'); 
	if($id_factura) {
		mysql_db_query($database,"DELETE FROM STANDING_ORDERS WHERE ID_STANDING_ORDER_INFO=$id_factura");
		echo mysql_error();
		mysql_db_query($database,"DELETE FROM STANDING_ORDERS_INFO WHERE ID_STANDING_ORDER_INFO=$id_factura");		
		mysql_db_query($database,"DELETE FROM STANDING_ORDERS_PRODUCTS WHERE ID_STANDING_ORDER_INFO=$id_factura");		
		echo mysql_error();
	}	
?>	
<script>
	window.location.href='report_st_orders.php';
</script>