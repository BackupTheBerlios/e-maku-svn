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

mysql_db_query($db,"UPDATE SHOPPING_CART SET DAY_CHARGE=(DAY_CHARGE+$charge) WHERE SESSION_ID='$OID' LIMIT 1");
echo mysql_error();

$href_sc = "take_orders_nopublic.php";
header ("Location: $href_sc");

?>
</body>
</html>
