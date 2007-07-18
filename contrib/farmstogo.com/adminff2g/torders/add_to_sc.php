<?php
session_start();
require ('../../functions.php'); 

$consulta_price_productos = <<< EOQ
    select MARKETS.MARKET,TIPO_PRODUCTOS.TIPO_PRODUCTO,
           PRODUCTOS.PRODUCTO,SIZES.SIZE,
           SUB_TIPO_PRODUCTOS.SUB_TIPO_PRODUCTO,
           PRICE_BUNCH*BUNCHES_BOX as PRICE_PRODUCTO
      from PRODUCTOS,SIZES,TIPO_PRODUCTOS,MARKETS,SUB_TIPO_PRODUCTOS
     where PRODUCTOS.ID_SUB_TIPO_PRODUCTO=SUB_TIPO_PRODUCTOS.ID_SUB_TIPO_PRODUCTO
       and PRODUCTOS.ID_SIZE=SIZES.ID_SIZE
       and PRODUCTOS.ID_MARKET=MARKETS.ID_MARKET
       and PRODUCTOS.ID_TIPO_PRODUCTO=TIPO_PRODUCTOS.ID_TIPO_PRODUCTO
       and ID_PRODUCTO=$it
EOQ;

//echo $consulta_price_productos;

#EXTRAEMOS EL PRECIO

$result_price_productos = mysql_db_query($database,$consulta_price_productos,$shopping_db_link);
$fila_price_producto = mysql_fetch_array ($result_price_productos);
$tipo_producto=$fila_price_producto["TIPO_PRODUCTO"];
$sub_tipo_producto=$fila_price_producto["SUB_TIPO_PRODUCTO"];
$producto_producto=$fila_price_producto["PRODUCTO"];
$size_producto=$fila_price_producto["SIZE"];
$market_producto=$fila_price_producto["MARKET"];
$product_price=(float)$fila_price_producto["PRICE_PRODUCTO"];
mysql_free_result ($result_price_productos);

$product_desc = $tipo_producto." - ".$sub_tipo_producto." ".$producto_producto." (".$size_producto.") - ".$market_producto;


#################################### DELIVERY DAY
if ($f1) { 
		$dtmp = mysql_db_query($database,"select UNIX_TIMESTAMP('$f1') FROM PRODUCTOS limit 1");
		echo mysql_error();
		$dtmp = mysql_fetch_row($dtmp);
		$dd = $dtmp[0];
}  
$current_day = date("w",$dd);
$day_charge="0";

$consulta_day_charge_shopping_cart = <<< EOCS
        select DAY_CHARGE from WEEKDAYS where ID_WEEKDAY = $current_day+1
EOCS;

$result_consulta_day_charge_shopping_cart = mysql_db_query($database,$consulta_day_charge_shopping_cart,$shopping_db_link);

$fila_day_charge_shopping_cart = mysql_fetch_array ($result_consulta_day_charge_shopping_cart);
$day_charge = $fila_day_charge_shopping_cart["DAY_CHARGE"];

mysql_free_result ($result_consulta_day_charge_shopping_cart);


#EJECUTAMOS EL INSERT EN LA TABLA SHOPPING CART CON PARAMETROS ID_SESSION, ID_PRODUCTO, DAY_CHARGE, QUANTITY Y PRICE

$insert_shopping_cart = <<< EOI
    insert into SHOPPING_CART (SESSION_ID,PRODUCT_DESC,PRODUCT_IN_PROMOTION,ID_PROMOTION,ID_PRODUCTO,DAY_CHARGE,ID_QUANTITY,DELIVERY_DATE,PRICE,TIME)
             values ('$OID','$product_desc',0,0,$it,$day_charge,$qty,$dd,$product_price,NOW())
EOI;

//echo $insert_shopping_cart;
# INSERTAMOS EL PRODUCTO EN EL SHOPPING CART

$result_insert_shopping_cart = mysql_db_query($database,$insert_shopping_cart,$shopping_db_link);
# mysql_free_result ($result_insert_shopping_cart);
echo mysql_error();
$href_sc = "take_orders.php";
if(isset($_POST['so'])){
$href_sc = "take_ordersso.php";
}
header ("Location: $href_sc");

?>
