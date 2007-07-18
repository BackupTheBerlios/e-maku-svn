<?php
require('../../functions.php');
$session_id = $ss; 
$delete_producto_shopping_cart = <<< EOI
    delete from SHOPPING_CART
       where SESSION_ID='$session_id' and ID_SHOPPING_CART=$id_shopping_cart
EOI;

$result_delete_producto_shopping_cart = mysql_db_query($database,$delete_producto_shopping_cart,$shopping_db_link);
$href_sc = "take_orders.php";
if(isset($_GET['so'])){
$href_sc = "take_ordersso.php";
}
header ("Location: $href_sc");
?>
