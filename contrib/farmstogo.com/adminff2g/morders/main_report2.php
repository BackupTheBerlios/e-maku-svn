<? include ('../../functions.php');

	#poner en status 3 (delivered)
	for($i=0; $i<count($idl); $i++) {
		mysql_db_query($database,"UPDATE FACTURAS SET ID_ORDER_STATUS=$sts WHERE ID_FACTURA=$idl[$i]");
		echo mysql_error();
	}
?>
	<script>
		window.location.href='./<?echo $url?>';
	</script>