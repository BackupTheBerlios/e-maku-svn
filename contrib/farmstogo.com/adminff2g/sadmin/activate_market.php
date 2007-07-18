<?php
	session_start();
	include('../../functions.php'); 

# COMANDO INSERT PARA AGREGAR EL MARKET ELEGIDO CON PRODUCTO
mysql_db_query($db,"DELETE FROM TMP_PRODUCTOS");
echo mysql_error();

mysql_db_query($db,"INSERT INTO TMP_PRODUCTOS SELECT * FROM PRODUCTOS WHERE ID_PRODUCTO=$producto");
echo mysql_error();

mysql_db_query($db,"UPDATE TMP_PRODUCTOS SET ID_PRODUCTO=NULL, ID_MARKET=$market");
echo mysql_error();

mysql_db_query($db,"INSERT INTO PRODUCTOS SELECT * FROM TMP_PRODUCTOS");
echo mysql_error();


# HEADER PARA REDIRECCIONAR

RetornarOL("edt_product.php?id=".$producto);

?>
