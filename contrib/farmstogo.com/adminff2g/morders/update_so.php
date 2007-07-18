<?
	require ('../../functions.php'); 
	if($id) {
		mysql_db_query($database,"UPDATE STANDING_ORDERS_INFO SET NEXT_ARRIVAL_DATE='$dt' WHERE ID_STANDING_ORDER_INFO=$id");
		echo mysql_error();
	}	
?>	
<script>
window.location.href='report_st_orders.php';
</script>