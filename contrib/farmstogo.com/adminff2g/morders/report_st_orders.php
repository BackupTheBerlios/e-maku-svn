<?php
session_start();
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("Standing Orders Report");
?>
<html>
<head>
<title>Flower Farms To Go .com</title>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" bottommargin="0" rightmargin="0" bgcolor="#FFFFFF" text="#000000" link="#000099" vlink="#000099" alink="#000099">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <tr>
    <td valign="top" height="2" colspan="2" align="center">
<?php

# CONECTARSE A LA BASE DE DATOS

# Ponemos ORDER BY ID_STANDING_ORDER_INFO si no hay uno definido

if (!$orderby) $orderby = "ID_STANDING_ORDER_INFO";


# CONSULTA PARA MOSTRAR LAS ORDENES FIJAS

$consulta_st_orders = <<< EOM
    select STANDING_ORDERS_INFO.CLIENT_NAME,
           STANDING_ORDERS_INFO.CLIENT_ADDRESS,
           STANDING_ORDERS_INFO.CLIENT_ZIPCODE,
           STANDING_ORDERS_INFO.CLIENT_PHONE,
           STANDING_ORDERS_INFO.CLIENT_FAX,
           STANDING_ORDERS_INFO.CLIENT_EMAIL,
           STANDING_ORDERS_INFO.CLIENT_CITY,STANDING_ORDERS_INFO.DATE_FROM,
           STATES.STATE,
           COUNTRIES.COUNTRY,
           STANDING_ORDERS_INFO.SHIPPING_CITY,
           STANDING_ORDERS_INFO.SHIPPING_NAME,
           STANDING_ORDERS_INFO.SHIPPING_PHONE,
           STANDING_ORDERS_INFO.SHIPPING_ZIPCODE,
           STANDING_ORDERS_INFO.SHIPPING_ADDRESS,
           STANDING_ORDERS_INFO.GRAND_TOTAL,
           STANDING_ORDERS_INFO.ID_STANDING_ORDER_INFO,
           STANDING_ORDERS_INFO.STANDING_TICKET,
           STANDING_ORDERS_INFO.DATE_TIME,
           STANDING_ORDERS_INFO.NEXT_ARRIVAL_DATE,
           STANDING_ORDERS_INFO.ID_ORDER_STATUS,
           ORDERS_STATUS.ORDER_STATUS
       from STANDING_ORDERS_INFO,STATES,COUNTRIES,ORDERS_STATUS
         where STANDING_ORDERS_INFO.ID_STATE=STATES.ID_STATE
         and STANDING_ORDERS_INFO.ID_COUNTRY=COUNTRIES.ID_COUNTRY
         and STANDING_ORDERS_INFO.ID_ORDER_STATUS=ORDERS_STATUS.ID_ORDER_STATUS
       order by $orderby
EOM;



# SE SACAN DATOS DE LA ORDEN FIJA

echo <<< EOE

    <table border=0 cellpadding=3 cellspacing=1 bgcolor=cecece>
EOE;

$result_consulta_st_order =   mysql_db_query($database,$consulta_st_orders,$shopping_db_link);
?>
<tr>
     <td align="center"><font color="#FFFFFF"><b><a href=report_st_orders.php>Ticket #</a></b></td>
     <td align="center"><font color="#FFFFFF"><b><a href=report_st_orders.php?orderby=CLIENT_NAME>Client Name</a></b></td>
     <td align="center"><font color="#FFFFFF"><b>Phone</b></td>
     <td align="center"><font color="#FFFFFF"><b>Shipping Address</b></td>
     <td align="center"><font color="#FFFFFF"><b>Start Date</b></td>
     <td align="center"><font color="#FFFFFF"><b><a href=report_st_orders.php?orderby=NEXT_ARRIVAL_DATE>Next Order Date</a></b></td>
     <td align="center"><font color="#FFFFFF"><b><a href=report_st_orders.php?orderby=GRAND_TOTAL>Order Amount</a></b></font></td>
     <td align="center"><font color="#FFFFFF"><b><a href=report_st_orders.php?orderby=ORDER_STATUS>Order Status</a></b></font></td>
		<td></td>
</tr>

<?
while ($fila_st_order =   mysql_fetch_array ($result_consulta_st_order))
 {

$client_name =   $fila_st_order["CLIENT_NAME"];
$client_address =   $fila_st_order["CLIENT_ADDRESS"];
$client_zipcode =   $fila_st_order["CLIENT_ZIPCODE"];
$client_phone =   $fila_st_order["CLIENT_PHONE"];
$client_email =   $fila_st_order["CLIENT_EMAIL"];
$client_city =   $fila_st_order["CLIENT_CITY"];
$client_state_desc =   $fila_st_order["STATE"];
$client_country_desc =   $fila_st_order["COUNTRY"];
$credit_card_owner =   $fila_st_order["CREDIT_CARD_OWNER"];
$shipping_address =   $fila_st_order["SHIPPING_ADDRESS"];
$shipping_zipcode =   $fila_st_order["SHIPPING_ZIPCODE"];
$shipping_phone =   $fila_st_order["SHIPPING_PHONE"];
$shipping_city =   $fila_st_order["SHIPPING_CITY"];
$grand_total_factura =   $fila_st_order["GRAND_TOTAL"];
$id_standing_order_info =   $fila_st_order["ID_STANDING_ORDER_INFO"];
$order_date =   $fila_st_order["DATE_TIME"];
$order_next_arrival_date =   $fila_st_order["NEXT_ARRIVAL_DATE"];
$start_date =   $fila_st_order["DATE_FROM"];
$id_order_status =   $fila_st_order["ID_ORDER_STATUS"];
$order_status =   $fila_st_order["ORDER_STATUS"];
$standing_ticket =   $fila_st_order["STANDING_TICKET"];

$action_text = "";

$status_data_confirm = 1;
$status_undelivered = 2;
$status_delivered = 3;
$status_cancelled = 4;
$status_pending = 5;
$status_open = 6;
$status_closed = 7;

  switch ($id_order_status)
       {
           case $status_closed:    $action_text = "<a href=\"delete_st_order.php?id_factura=$id_standing_order_info\">Delete </a>";
                                      break;
           case $status_open:      $action_text .= "<a href=\"change_data.php?target_value=$status_closed&target_table=STANDING_ORDERS_INFO&target_field=ID_ORDER_STATUS&where_field=ID_STANDING_ORDER_INFO&where_value=$id_standing_order_info&redirect_page=report_st_orders.php\">Close </a>";
                                      break;
        }


echo <<< EOD
     <tr bgcolor=ffffff>
      <td><b>$standing_ticket</td>
      <!-- <td align=right>$id_standing_order_info&nbsp;</td> -->
      <td>$client_name<br><b>$client_email</b></td>
      <td>$client_phone</td>
      <td>$shipping_address ($shipping_zipcode) - $shipping_city</td>
      <td align=center>$start_date</td>
	  <form>
      <td align=center bgcolor=ffffbb><input type=text name=ord value="$order_next_arrival_date" size=10 maxlenght=10><input type=button value="Upd" Onclick="window.location.href='./update_so.php?id=$id_standing_order_info&dt='+this.form.ord.value"></td>
      </form>
	  <td align=right><b>$grand_total_factura</td>
      <td>$order_status</td>
	  <td>$action_text</td></tr>
EOD;

# SE SACAN DATOS DE LA FACTURA
//echo "</td></tr>\n";

}

mysql_free_result ($result_consulta_st_order);

echo "</td></tr></table>\n";


 echo "</td></tr>\n";
 echo "</table>\n";
?>
        <p><a href="../reports.php">Go Back to Menu</a>
</body>
</html>
