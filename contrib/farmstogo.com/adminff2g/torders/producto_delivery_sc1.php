<?php
session_start();

# HEADERS PARA NO CACHE

header ("Expires: Mon, 26 Jul 1997 05:00:00 GMT");    // Date in the past
header ("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
                                                      // always modified
header ("Cache-Control: no-cache, must-revalidate");  // HTTP/1.1
header ("Pragma: no-cache");                          // HTTP/1.0

$session_id = $OID;
# CONECTARSE A LA BASE DE DATOS
require('../../functions.php');
# DETERMINAMOS SI EL DIA DE ENTREGA TIENE CARGOS DE ENVIO

if ($f1) { 
	$dd = mysql_db_query($db,"SELECT UNIX_TIMESTAMP('$f1') FROM PRODUCTOS LIMIT 1");
	$dd = mysql_fetch_row($dd);
	$current_day = date("w",$dd[0]);
	$day_charge=0;

	$consulta_day_charge_shopping_cart = <<< EOCS
	        select DAY_CHARGE from WEEKDAYS where ID_WEEKDAY = $current_day+1
EOCS;

	$result_consulta_day_charge_shopping_cart = mysql_db_query($database,$consulta_day_charge_shopping_cart,$shopping_db_link);

	$fila_day_charge_shopping_cart = mysql_fetch_array ($result_consulta_day_charge_shopping_cart);
	$day_charge = $fila_day_charge_shopping_cart["DAY_CHARGE"];

	mysql_free_result ($result_consulta_day_charge_shopping_cart);

	$s_delivery = ",DELIVERY_DATE=$dd[0],DAY_CHARGE=$day_charge";
}
# COMANDO UPDATE PARA ACTUALIZAR LA FECHA DE ENTREGA DEL PRODUCTO

//if ($price) { $sprice = "PRICE=$price,design_choice='$dch'"; }
if ($price) {
	if ($surcharge){
		$price=$price+$surcharge;
		$sprice = "PRICE=$price,design_choice='$dch'";
	}else{
 		$sprice = "PRICE=$price,design_choice='$dch'"; 
	}
}
$update_delivery_date_shopping_cart = <<< EOI
    update SHOPPING_CART set $sprice $s_delivery
             where SESSION_ID='$session_id' and ID_SHOPPING_CART=$id_shopping_cart
EOI;


# ACTUALIZAMOS LA FECHA DE ENTREGA DEL PRODUCTO EN EL SHOPPING CART
//echo $update_delivery_date_shopping_cart;
$result_update_delivery_date_shopping_cart = mysql_db_query($database,$update_delivery_date_shopping_cart,$shopping_db_link);
echo mysql_error();
# mysql_free_result ($result_update_delivery_date_shopping_cart);

# HEADER PARA REDIRECCIONAR

$href_sc = "take_orders.php?surcharge=$surcharge";
header ("Location: $href_sc");

?>
