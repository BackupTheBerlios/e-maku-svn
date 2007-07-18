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


# COMANDO UPDATE PARA ACTUALIZAR EL CAMPO ELEGIDO CON EL PARAMETRO WHERE ELEGIDO

$update_field_change_data = <<< EOI
    update $target_table
       set $target_field=$target_value
     where $where_field=$where_value
EOI;


# ACTUALIZAMOS EL CAMPO CHANGE DATA

$result_update_field_change_data = mysql_db_query($database,$update_field_change_data,$shopping_db_link);

# HEADER PARA REDIRECCIONAR

if (!$redirect_page) $redirect_page=$HTTP_REFERER;

header ("Location: $redirect_page");

?>
