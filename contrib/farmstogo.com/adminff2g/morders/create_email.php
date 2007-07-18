<?php
//require_once('../include/head_pc.php');
# CONSULTA PARA SACAR EL MONTO TOTAL DE LO ADQUIRIDO
$ssl = 1;

$consulta_factura_total = <<< EOM
     select FACTURAS.GRAND_TOTAL
       from FACTURAS
      where FACTURAS.ID_FACTURA=$id_factura_facturas
EOM;
//,CLIENTS and CLIENTS.SESSION_ID='$session_id'
$result_consulta_factura_total =   mysql_db_query($database,$consulta_factura_total,$shopping_db_link);

$fila_factura_total =   mysql_fetch_array ($result_consulta_factura_total);

$factura_total =   $fila_factura_total["GRAND_TOTAL"];

# SI LA FACTURA ES MAYOR A US$500 SACAR AVISO PARA CONFIRMAR VIA FAX EL PEDIDO.

$fax_confirmation = <<< EOC
We truly appreciate your business.You will receive an e-mail with your FedEx tracking information on the evening before your product will be delivered.contact you briefly.<BR>
You may track your order on our website with your 4 digit order number or on www.fedex.com with your tracking number,
<br /><br />
<strong>Please read disclaimer below</strong>
<BR>
EOC;

$message = $fax_confirmation;

//if ($factura_total >= 500) 

$mail_header = <<< EOD
<table>
<tr><td>
            <div align=  "center"><font face=  "Arial, Helvetica, sans-serif" color=  "#000000" size= "3"><b>Order # $id_factura_facturas</b></font></div>
<p>
<center>
EOD;
$document_message = <<< EOM
    <b>FlowerFarmsToGo.com.<br><br>
    YOUR ORDER IS ACCEPTED.<br>
    PLEASE EXPECT EMAIL CONFIRMATION.<br>
    PRINT THIS PAGE FOR REFERENCE.<br>
	<br>
	<a href="#" onClick="document.print();">PRINT</a>
	<br>
    $message
    <a href=  "http://www.flowerfarmstogo.com">Click here to Finish.</a><br>
</b>
    <br>
EOM;

# EJECUTAMOS CONSULTA PARA SACAR EL ESTADO Y PAIS DEL SHIPPING

$consulta_shipping_state_country_desc = <<< EOCS
    select STATES.STATE,COUNTRIES.COUNTRY from STATES,COUNTRIES,FACTURAS
      where FACTURAS.SHIPPING_ID_STATE=STATES.ID_STATE
      and FACTURAS.SHIPPING_ID_COUNTRY=COUNTRIES.ID_COUNTRY
      and FACTURAS.ID_FACTURA=$id_factura_facturas
EOCS;

$result_consulta_shipping_state_country_desc =   mysql_db_query($database,$consulta_shipping_state_country_desc,$shopping_db_link);
$fila_shipping_state_country_desc =   mysql_fetch_array($result_consulta_shipping_state_country_desc);
$shipping_state_desc =   $fila_shipping_state_country_desc["STATE"];
$shipping_country_desc =   $fila_shipping_state_country_desc["COUNTRY"];
mysql_free_result ($result_consulta_shipping_state_country_desc);

# CONSULTA PARA MOSTRAR EL CLIENTE, LA FACTURA Y LO ADQUIRIDO

$consulta_factura = <<< EOM
    select CLIENTS.CLIENT_NAME,CLIENTS.CLIENT_LAST_NAME,
           CLIENTS.CLIENT_ADDRESS,CLIENTS.CLIENT_ZIPCODE,
           CLIENTS.CLIENT_PHONE,CLIENTS.CLIENT_FAX,CLIENTS.CLIENT_EMAIL,
           CLIENTS.CLIENT_CITY,STATES.STATE,COUNTRIES.COUNTRY,COUNTRIES.COUNTRY_CODE,
           FACTURAS.SHIPPING_CITY,FACTURAS.SHIPPING_NAME,
           FACTURAS.SHIPPING_LAST_NAME,FACTURAS.CREDIT_CARD_OWNER,
           FACTURAS.CREDIT_CARD_NUMBER,
           month(FACTURAS.CREDIT_CARD_EXPIRY) as CREDIT_CARD_EXPIRY_MONTH,
           year(FACTURAS.CREDIT_CARD_EXPIRY) as CREDIT_CARD_EXPIRY_YEAR,
           FACTURAS.CREDIT_CARD_EXPIRY,FACTURAS.CREDIT_CARD_ADDRESS,
           FACTURAS.SHIPPING_PHONE,FACTURAS.SHIPPING_FAX,FACTURAS.SHIPPING_ZIPCODE,
           FACTURAS.SHIPPING_ADDRESS,SHIPPING_METHODS.SHIPPING_METHOD,
           FACTURAS.SUBTOTAL_PRODUCTS,CREDIT_CARD_TYPES.CREDIT_CARD_TYPE,
           FACTURAS.RETAILER_NUMBER,
           FACTURAS.SHIPPING_COST,FACTURAS.SUBTOTAL_TAXES,
           FACTURAS.GRAND_TOTAL,FACTURAS.COMMENTS,FACTURAS.message
       from CLIENTS,FACTURAS,CREDIT_CARD_TYPES,SHIPPING_METHODS,STATES,COUNTRIES
         where CLIENTS.ID_CLIENT=FACTURAS.ID_CLIENT
         and CLIENTS.ID_STATE=STATES.ID_STATE
         and CLIENTS.ID_COUNTRY=COUNTRIES.ID_COUNTRY
         and FACTURAS.ID_CREDIT_CARD_TYPE=CREDIT_CARD_TYPES.ID_CREDIT_CARD_TYPE
         and FACTURAS.ID_SHIPPING_METHOD=SHIPPING_METHODS.ID_SHIPPING_METHOD
         and FACTURAS.ID_FACTURA=$id_factura_facturas         
EOM;
//and CLIENTS.SESSION_ID='$session_id'
# GENERAMOS LOS DATOS DE LA FACTURA

$result_consulta_factura =   mysql_db_query($database,$consulta_factura,$shopping_db_link);

$fila_factura =   mysql_fetch_array ($result_consulta_factura);

$client_name =   $fila_factura["CLIENT_NAME"];
$client_last_name =   $fila_factura["CLIENT_LAST_NAME"];
$client_address =   $fila_factura["CLIENT_ADDRESS"];
$client_zipcode =   $fila_factura["CLIENT_ZIPCODE"];
$client_phone =   $fila_factura["CLIENT_PHONE"];
$client_fax =   $fila_factura["CLIENT_FAX"];
$client_email =   $fila_factura["CLIENT_EMAIL"];
$client_city =   $fila_factura["CLIENT_CITY"];
$client_state_desc =   $fila_factura["STATE"];
$client_country_desc =   $fila_factura["COUNTRY"];
$client_country_code =   $fila_factura["COUNTRY_CODE"];
$retailer_number =     $fila_factura["RETAILER_NUMBER"];
$credit_card_owner =   $fila_factura["CREDIT_CARD_OWNER"];
$credit_card_number =   $fila_factura["CREDIT_CARD_NUMBER"];
$credit_card_expiry =   $fila_factura["CREDIT_CARD_EXPIRY"];
$credit_card_expiry_month =   $fila_factura["CREDIT_CARD_EXPIRY_MONTH"];
$credit_card_expiry_year =   $fila_factura["CREDIT_CARD_EXPIRY_YEAR"];
$credit_card_billing_address =   $fila_factura["CREDIT_CARD_ADDRESS"];
$credit_card_type =   $fila_factura["CREDIT_CARD_TYPE"];
$shipping_name =   $fila_factura["SHIPPING_NAME"];
$shipping_last_name =   $fila_factura["SHIPPING_LAST_NAME"];
$shipping_address =   $fila_factura["SHIPPING_ADDRESS"];
$shipping_zipcode =   $fila_factura["SHIPPING_ZIPCODE"];
$shipping_phone =   $fila_factura["SHIPPING_PHONE"];
$shipping_fax =   $fila_factura["SHIPPING_FAX"];
$shipping_city =   $fila_factura["SHIPPING_CITY"];
$subtotal_products_factura =   $fila_factura["SUBTOTAL_PRODUCTS"];
$shipping_method_desc =   $fila_factura["SHIPPING_METHOD"];
$shipping_cost_factura =   $fila_factura["SHIPPING_COST"];
$subtotal_taxes_factura =   $fila_factura["SUBTOTAL_TAXES"];
$grand_total_factura =   $fila_factura["GRAND_TOTAL"];
$comments =   $fila_factura["COMMENTS"];
$gmsg =   $fila_factura["message"];

### INFORMACION BANK OF AMERICA

$IOC_merchant_id =   "rosef2g";
$IOC_order_total_amount =   $grand_total_factura;
$IOC_merchant_order_id =   $id_factura_facturas;
$Ecom_billto_postal_name_first =   $client_name;
$Ecom_billto_postal_name_last =   $client_last_name;
$Ecom_billto_postal_street_line1 =   $credit_card_billing_address;
$Ecom_billto_postal_city =   $client_city;
$Ecom_billto_postal_stateprov =   $client_state_desc;
$Ecom_billto_postal_postalcode =   $client_zipcode;
$Ecom_billto_postal_countrycode =   $client_country_code;
$Ecom_billto_telecom_phone_number =   $client_phone;
$Ecom_billto_telecom_online_email =   $client_email;
$Ecom_payment_card_name =   $credit_card_owner;
$Ecom_payment_card_number =   $credit_card_number;
$Ecom_payment_card_expdate_month =   $credit_card_expiry_month;
$Ecom_payment_card_expdate_year =   $credit_card_expiry_year;

#### MOSTRAMOS INFORMACION CLIENTE

$mail_body .=   <<< EOI
    <table border=0 cellpadding=3 cellspacing=1 bgcolor=cecece width=100%>
      <tr>
      <td align=  "center" colspan=  2 bgcolor=  "#8080FF"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Customer Information</b></td>
      <td> </td>
      <td align=  "center" colspan=  5 bgcolor=  "#8080FF"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Shipping Information</b></td></tr>
      <tr align=  "right" bgcolor=ffffff><td>First Name</td><td>$client_name</td><td> </td><td>First Name</td><td>$shipping_name</td></tr>
      <tr align=  "right" bgcolor=ffffff><td>Last Name</td><td>$client_last_name</td><td> </td><td>Last Name</td><td>$shipping_last_name</td></tr>
      <tr align=  "right" bgcolor=ffffff><td>Address</td><td>$client_address</td><td> </td><td>Address</td><td>$shipping_address</td></tr>
      <tr align=  "right" bgcolor=ffffff><td>Zip Code</td><td>$client_zipcode</td><td> </td><td>Zip Code</td><td>$shipping_zipcode</td></tr>
      <tr align=  "right" bgcolor=ffffff><td>Phone</td><td>$client_phone</td><td> </td><td>Phone</td><td>$shipping_phone</td></tr>
      <tr align=  "right" bgcolor=ffffff><td>Fax</td><td>$client_fax</td><td> </td><td>Fax</td><td>$shipping_fax</td></tr>
      <tr align=  "right" bgcolor=ffffff><td>E-mail</td><td>$client_email</td><td> </td><td>Shipping Method</td><td>$shipping_method_desc</td></tr>
      <tr align=  "right" bgcolor=ffffff><td>City</td><td>$client_city</td><td> </td><td>City</td><td>$shipping_city</td></tr>
      <tr align=  "right" bgcolor=ffffff><td>State</td><td>$client_state_desc</td><td> </td><td>State</td><td>$shipping_state_desc</td></tr>
      <tr align=  "right" bgcolor=ffffff><td>Country</td><td>$client_country_desc</td><td> </td><td>Country</td><td>$shipping_country_desc</td></tr>
    </table>
    <br>
EOI;

$retailer_text = <<< EOM
   <table border=0 cellpadding=3 cellspacing=1 bgcolor=cecece width=100%>
      <tr><td align="center" bgcolor="#8080FF" colspan=2><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><b>Retailer Information</b></font></td></tr>
      <tr bgcolor=ffffff><td align="center">Retailer Number</td><td>$retailer_number</td></tr>
    </table>
<br>
EOM;

if ($retailer_number) $mail_body .= $retailer_text;

$cardn = str_repeat("X",strlen($credit_card_number)-4).substr($credit_card_number,-4);
$mail_body .= <<< EOI
    <table border=0 cellpadding=3 cellspacing=1 bgcolor=cecece width=100%>
      <tr>
      <td align=  "center" colspan=  2 bgcolor=  "#8080FF"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Credit Card Data</b></td>
      <td> </td>
      <td align=  "center" colspan=  5 bgcolor=  "#8080FF"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Your Comments</b></td></tr>
      <tr  bgcolor=ffffff align=  "right"><td>Card Holder's Name</td><td>$credit_card_owner </td><td> </td><td colspan=  "5" rowspan=  "5"><textarea name=  "comments" rows=  "10" disabled>$comments </textarea></td></tr>
      <tr bgcolor=ffffff align=  "right"><td>Card Number</td><td>$cardn </td><td> </td></tr>
      <tr bgcolor=ffffff align=  "right"><td>Expiry Date</td><td>$credit_card_expiry </td><td> </td></tr>
      <tr bgcolor=ffffff align=  "right"><td>Credit Card Type</td><td>$credit_card_type </td><td> </td></tr>
    </table>
    <br>
EOI;


#CONSULTA PARA SABER SI HAY PRODUCTOS CON ELECCION DE COLORES Y VARIEDADES EN LA FACTURA

$consulta_colors_varieties = <<< EOM
    select PRODUCTOS.COLOR_VARIETY_CHOICE
      from ITEMS_FACTURA,PRODUCTOS
     where ITEMS_FACTURA.ID_PRODUCTO=PRODUCTOS.ID_PRODUCTO
       and PRODUCTOS.COLOR_VARIETY_CHOICE=1
       and ITEMS_FACTURA.ID_FACTURA=$id_factura_facturas
EOM;

$result_consulta_colors_varieties = mysql_db_query($database,$consulta_colors_varieties,$shopping_db_link);

$fila_color_variety = mysql_fetch_array ($result_consulta_colors_varieties);

  $choice_color_variety=$fila_color_variety["COLOR_VARIETY_CHOICE"];

if ($choice_color_variety==1) {

$mail_body .=   <<< EOD
    $color_variety_choice
EOD;

}



# CONSULTA PARA SACAR LOS PRODUCTOS ADQUIRIDOS

$consulta_items_factura =   <<< EOC
    select ITEMS_FACTURA.ARRIVAL_DATE,ITEMS_FACTURA.ITEM_PRICE,
           ITEMS_FACTURA.PRODUCT_DESC,
           QUANTITIES.QUANTITY
       from ITEMS_FACTURA,QUANTITIES
       where QUANTITIES.ID_QUANTITY=ITEMS_FACTURA.ID_QUANTITY
       and ITEMS_FACTURA.ID_FACTURA=$id_factura_facturas
EOC;

# GENERAMOS LA LISTA DE PRODUCTOS ADQUIRIDOS

$result_consulta_items_factura =   mysql_db_query($database,$consulta_items_factura,$shopping_db_link);

$mail_body .=   <<< EOI
    <table border=0 cellpadding=3 cellspacing=1 bgcolor=cecece width=100%>
    <tr bgcolor=  "#8080FF">
    <td align=  "center"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Product Ordered</b></td><td align=  "center"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Quantity</b></td><td align=  "center"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Total</b></td><td align=  "center"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Desired Delivery Date</b></td></tr>
EOI;

 while ($fila_shopping_cart =   mysql_fetch_array ($result_consulta_items_factura)) {

    $delivery_date_shopping_cart=  $fila_shopping_cart["ARRIVAL_DATE"];
    $price_producto_shopping_cart=  $fila_shopping_cart["ITEM_PRICE"];
    $product_desc=  $fila_shopping_cart["PRODUCT_DESC"];
    $quantity_shopping_cart=  $fila_shopping_cart["QUANTITY"];

    $total_producto_shopping_cart=  $price_producto_shopping_cart*$quantity_shopping_cart;
    $day_desc =   date("M. j (l)",$delivery_date_shopping_cart);

    $format_total_producto_shopping_cart = sprintf ("%01.2f", $total_producto_shopping_cart);

$mail_body .=   <<< EOI
    <tr  bgcolor=ffffff align=  "right"><td align=  "center"><b>$product_desc</b></td><td>$quantity_shopping_cart</td><td>\$$format_total_producto_shopping_cart</td><td>$day_desc</td></tr>
EOI;

 }

 @mysql_free_result ($result_consulta_items_factura);

 $format_subtotal_products_factura = sprintf ("%01.2f", $subtotal_products_factura); $format_subtotal_taxes_factura = sprintf ("%01.2f", $subtotal_taxes_factura);
 $format_shipping_cost_factura = sprintf ("%01.2f", $shipping_cost_factura);
 $format_grand_total_factura = sprintf ("%01.2f", $grand_total_factura);

$mail_body .=   <<< EOI
 <tr bgcolor=ffffff align=  "right"><td colspan=  2 align=  "right"><b>Order Sub Total : </b></td><td>\$$format_subtotal_products_factura</td></tr>
 <tr bgcolor=ffffff align=  "right"><td colspan=  2 align=  "right"><b>Subtotal Taxes : </b></td><td>\$$format_subtotal_taxes_factura</td></tr>
 <tr bgcolor=ffffff align=  "right"><td colspan=  2 align=  "right"><b>Shipping Charges : </b></td><td>\$$format_shipping_cost_factura</td></tr>
 <tr bgcolor=ffffff align=  "right"><td colspan=  2 align=  "right"><b>Order Grand Total : </b></td><td><b>\$$format_grand_total_factura</b></td></tr>
 </table></td></tr>

</table>
EOI;

$mail_tail = <<< EOI
</body>
</html>
EOI;

$mail_text = "<html><body>".$mail_header.$message.$mail_body;
//ACTULIZANDO LA FACTURA CON EL MAIL.
mysql_db_query($database,"UPDATE FACTURAS set invoice_mail='".str_replace("'","",$mail_text)."' WHERE ID_FACTURA=$id_factura_facturas");

$subject =   "FlowerFarmsToGo.com - Order Accepted # $id_factura_facturas";

$mime_header =   <<< EOD
Content-Type: text/html; charset=  "iso-8859-1"
Content-Transfer-Encoding: 7bit
EOD;

# ENVIAMOS CORREO

/*
smtpmail($client_email,$subject,$mail_text,"From: Orders FlowerFarmsToGo.com <orders@flowerfarmstogo.com>\r\n".$mime_header);
*/
mail($client_email,$subject,$mail_text,"From: Orders FlowerFarmsToGo.com <orders@flowerfarmstogo.com>\n".$mime_header);
?>