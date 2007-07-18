<?php
session_start();

# HEADERS PARA NO CACHE

header ("Expires: Mon, 26 Jul 1997 05:00:00 GMT");    // Date in the past
header ("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
                                                      // always modified
header ("Cache-Control: no-cache, must-revalidate");  // HTTP/1.1
header ("Pragma: no-cache");                          // HTTP/1.0

# CONECTARSE A LA BASE DE DATOS
require('../../functions.php');
# COMANDO DELETE PARA BORRAR EL PRODUCTO ELEGIDO

$delete_producto_shopping_cart = <<< EOI
    delete from SHOPPING_CART
       where SESSION_ID='$session_id'
EOI;


# BORRAMOS EL PRODUCTO EN EL SHOPPING CART

$result_delete_producto_shopping_cart = mysql_db_query($database,$delete_producto_shopping_cart,$shopping_db_link);
# mysql_free_result ($result_update_delivery_date_shopping_cart);

# HEADER PARA REDIRECCIONAR

session_destroy();
session_unregister("session_id");
session_unregister("OID");
$href_sc = "take_orders_nopublic.php";
header ("Location: $href_sc");

?>
</body>
</html>
