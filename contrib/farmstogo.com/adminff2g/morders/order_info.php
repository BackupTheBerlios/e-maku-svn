<?

include('./HOP.php');

session_start();

# HEADERS PARA NO CACHE



header ("Expires: Mon, 26 Jul 1997 05:00:00 GMT");    // Date in the past

header ("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");

                                                      // always modified

header ("Cache-Control: no-cache, must-revalidate");  // HTTP/1.1

header ("Pragma: no-cache");                          // HTTP/1.0



#CONECTARSE A LA BASE DE DATOS

require('../../functions.php');

include('../head_admin.php');



$id_factura = $ido;

session_register("id_factura_facturas");

$id_factura_facturas = $id_factura;

 foreach($_POST as $nombre_campo => $val){ 

   $asignacion = "\$" . $nombre_campo . "='" . $val . "';"; 



   eval($asignacion);

      } 

 foreach($_POST as $nombre_campo => $val){ 

   $asignacion = "\$" . $nombre_campo . "='" . $val . "';"; 



   eval($asignacion);

      } 



if (isset($_POST['flag'])) {

	$sph = $sph1.$sph2.$sph3;

	$sfax = $sfax1.$sfax2.$sfax3;

	#UPDATE ORDER INFO

	$updq = "UPDATE FACTURAS SET CREDIT_CARD_OWNER='$cco' , CREDIT_CARD_NUMBER='$ccn' , ".

	" CREDIT_CARD_EXPIRY='$cce' , ID_CREDIT_CARD_TYPE=$cct , SHIPPING_NAME='$sn', ".

	" SHIPPING_LAST_NAME='$sl' , SHIPPING_ADDRESS='$sad' , SHIPPING_ZIPCODE = '$szc' , ".

	" SHIPPING_PHONE='$sph' , SHIPPING_FAX='$sfax' , SHIPPING_CITY='$sct' , ID_ORDER_STATUS=$status,".

	" SHIPPING_ID_STATE='$sst', SHIPPING_ID_COUNTRY='$scy' , CVN='$cvn', COMMENTS='$ccc' WHERE ID_FACTURA=$flag";

	mysql_db_query($database,$updq);

	//echo $updq;

	echo mysql_error();

	$updcliente="UPDATE CLIENTS SET CLIENT_ADDRESS='$cad',CLIENT_CITY='$ccity',CLIENT_ZIPCODE='$czc',ID_STATE='$cst',ID_COUNTRY='$ccy' WHERE ID_CLIENT='$id_cliente'";

	mysql_db_query($database,$updcliente);

    echo mysql_error();

	//////////////////////////////////////////////////////////////////////////////////// 

	/// Paula Rojas 02-10-05

	#UPDATE SALES PERSON INFORMATION

	$sales_person_info = "UPDATE factura_vendedor

				 SET id_vendedor=$salesperson

			       WHERE id_factura=$flag

			     ";

	$update_sales_person_info = mysql_db_query($database, $sales_person_info);

	////////////////////////////////////////////////////////////////////////////////////

	

	mysql_db_query($db,"UPDATE CLIENTS SET CLIENT_EMAIL='$cli_email',CLIENT_NAME='$cnm',CLIENT_LAST_NAME='$clm' WHERE ID_CLIENT=".$id_cliente);

	echo mysql_error();

	

	#MODIFICACION DE DELIVERIES

//	for ($i=0; $i<count($idi); $i++) 

 foreach($_POST['f1'] as $key => $value)	

	{	

//		if (${"f1".$idi[$i]}) 

	if($value!=""){

			

		$charge = 0;	

		$dd = mysql_db_query($db,"SELECT UNIX_TIMESTAMP('".$fl[$value]."') FROM PRODUCTOS LIMIT 1");

		$dd = mysql_fetch_row($dd);

		if (date("w",$dd[0]) == "6") { $charge= 15; }

				//echo "UPDATE ITEMS_FACTURA SET DAY_CHARGE=$charge,ARRIVAL_DATE=UNIX_TIMESTAMP('".${"f1".$idi[$i]}."') WHERE ID_ITEM_FACTURA=$idi[$i]";

				mysql_db_query($db,"UPDATE ITEMS_FACTURA SET DAY_CHARGE=$charge,ARRIVAL_DATE=UNIX_TIMESTAMP('".$value."') WHERE ID_ITEM_FACTURA=$key");

				echo mysql_error();

 			}	

		}

	?>

		<SCRIPT>

			//alert('Order <?echo $flag?> Updated !!!');

	self.location='order_info.php?ido=<?php echo $flag; ?>';	

		</script>

	<?

}



# EJECUTAMOS CONSULTA PARA SACAR EL ESTADO Y PAIS DEL SHIPPING



$consulta_shipping_state_country_desc = <<< EOCS

    select STATES.STATE,COUNTRIES.COUNTRY from STATES,COUNTRIES,FACTURAS

      where FACTURAS.SHIPPING_ID_STATE=STATES.ID_STATE

      and FACTURAS.SHIPPING_ID_COUNTRY=COUNTRIES.ID_COUNTRY

      and FACTURAS.ID_FACTURA=$id_factura

EOCS;



$result_consulta_shipping_state_country_desc =   mysql_db_query($database,$consulta_shipping_state_country_desc,$shopping_db_link);

$fila_shipping_state_country_desc =   mysql_fetch_array($result_consulta_shipping_state_country_desc);

$shipping_state_desc =   $fila_shipping_state_country_desc["STATE"];

$shipping_country_desc =   $fila_shipping_state_country_desc["COUNTRY"];

mysql_free_result ($result_consulta_shipping_state_country_desc);



# CONSULTA PARA MOSTRAR EL CLIENTE, LA FACTURA Y LO ADQUIRIDO



$consulta_factura = <<< EOM

    select CLIENTS.COMPANY,CLIENTS.CLIENT_NAME,CLIENTS.CLIENT_LAST_NAME,CLIENTS.ID_CLIENT,

           CLIENTS.CLIENT_ADDRESS,CLIENTS.CLIENT_ZIPCODE,CLIENTS.SESSION_ID,

           CLIENTS.CLIENT_PHONE,CLIENTS.CLIENT_FAX,CLIENTS.CLIENT_EMAIL,

           CLIENTS.CLIENT_CITY,CLIENTS.ID_STATE,CLIENTS.ID_COUNTRY,STATES.STATE,STATES.code,COUNTRIES.COUNTRY,COUNTRIES.cod_BA,COUNTRIES.COUNTRY_CODE,

           FACTURAS.DATE_TIME,FACTURAS.SHIPPING_CITY,FACTURAS.SHIPPING_NAME,

           FACTURAS.SHIPPING_LAST_NAME,FACTURAS.CREDIT_CARD_OWNER,

           FACTURAS.CREDIT_CARD_NUMBER,FACTURAS.ID_CREDIT_CARD_TYPE,

           month(FACTURAS.CREDIT_CARD_EXPIRY) as CREDIT_CARD_EXPIRY_MONTH,

           year(FACTURAS.CREDIT_CARD_EXPIRY) as CREDIT_CARD_EXPIRY_YEAR,

           FACTURAS.CREDIT_CARD_EXPIRY,FACTURAS.CREDIT_CARD_ADDRESS,

           FACTURAS.SHIPPING_PHONE,FACTURAS.SHIPPING_FAX,FACTURAS.SHIPPING_ZIPCODE,

           FACTURAS.SHIPPING_ADDRESS,SHIPPING_METHODS.SHIPPING_METHOD,

           FACTURAS.SUBTOTAL_PRODUCTS,CREDIT_CARD_TYPES.CREDIT_CARD_TYPE,

           FACTURAS.SHIPPING_COST,FACTURAS.SUBTOTAL_TAXES,FACTURAS.ID_ORDER_STATUS,

           FACTURAS.RETAILER_NUMBER, FACTURAS.SHIPPING_ID_STATE, FACTURAS.SHIPPING_ID_COUNTRY,

           FACTURAS.GRAND_TOTAL,FACTURAS.COMMENTS, FACTURAS.message, FACTURAS.CVN, FACTURAS.mess_charge,FACTURAS.phone_charge       

	   FROM CLIENTS INNER JOIN FACTURAS ON CLIENTS.ID_CLIENT=FACTURAS.ID_CLIENT

       INNER JOIN SHIPPING_METHODS ON FACTURAS.ID_SHIPPING_METHOD= SHIPPING_METHODS.ID_SHIPPING_METHOD

       INNER JOIN STATES ON FACTURAS.SHIPPING_ID_STATE=STATES.ID_STATE

       INNER JOIN COUNTRIES ON FACTURAS.SHIPPING_ID_COUNTRY=COUNTRIES.ID_COUNTRY

       LEFT JOIN CREDIT_CARD_TYPES ON FACTURAS.ID_CREDIT_CARD_TYPE= CREDIT_CARD_TYPES.ID_CREDIT_CARD_TYPE

       WHERE FACTURAS.ID_FACTURA=$id_factura     

EOM;



# GENERAMOS LOS DATOS DE LA FACTURA

//echo $consulta_factura;

$result_consulta_factura =   mysql_db_query($database,$consulta_factura,$shopping_db_link);



$fila_factura =   mysql_fetch_array ($result_consulta_factura);





session_register("xclient");

$xclient = $fila_factura["ID_CLIENT"];

$client_name =   $fila_factura["CLIENT_NAME"];

$client_last_name =   $fila_factura["CLIENT_LAST_NAME"];

$client_address =   $fila_factura["CLIENT_ADDRESS"];

$client_zipcode =   $fila_factura["CLIENT_ZIPCODE"];

$client_phone =   $fila_factura["CLIENT_PHONE"];

$client_fax =   $fila_factura["CLIENT_FAX"];

$client_email =   $fila_factura["CLIENT_EMAIL"];

$client_city =   $fila_factura["CLIENT_CITY"];

$client_state_desc =   $fila_factura["STATE"];

$client_state =   $fila_factura["code"];

$client_country_desc =   $fila_factura["COUNTRY"];

$client_country_code =   strtolower($fila_factura["COUNTRY_CODE"]);

$country_BA = $fila_factura["cod_BA"];

$retailer_number =     $fila_factura["RETAILER_NUMBER"];

$credit_card_owner =   $fila_factura["CREDIT_CARD_OWNER"];

$credit_card_number =   $fila_factura["CREDIT_CARD_NUMBER"];

$credit_card_expiry =   $fila_factura["CREDIT_CARD_EXPIRY"];

$credit_card_expiry_month =   $fila_factura["CREDIT_CARD_EXPIRY_MONTH"];

$credit_card_expiry_year =   $fila_factura["CREDIT_CARD_EXPIRY_YEAR"];

$credit_card_billing_address =   $fila_factura["CREDIT_CARD_ADDRESS"];

$credit_card_type =   $fila_factura["CREDIT_CARD_TYPE"];

$cc_type = "00".$fila_factura["ID_CREDIT_CARD_TYPE"];

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

$company =   $fila_factura["COMPANY"];

$msgx = $fila_factura["message"];

$cvn = $fila_factura["CVN"];
$mess_charge=number_format($fila_factura["mess_charge"],2);
$phone_charge=number_format($fila_factura["phone_charge"],2);


session_register("session_id");

$session_id = $fila_factura["SESSION_ID"];

////////////////////////////////////////////////////////////////////////////////////////////////////

// Paula Andrea Rojas 25-01-2005



$consulta_id_vendedor = " SELECT id_vendedor

                         FROM factura_vendedor

                        WHERE id_factura=$id_factura

                     ";

$result_consulta_id_vendedor =   @mysql_db_query($database,$consulta_id_vendedor,$shopping_db_link);

$id_vendedor =   @mysql_result($result_consulta_id_vendedor, 0, "id_vendedor");



/// If the query is empty, then added to factura_vendedor.

/// MACHETAZO

if ($id_vendedor =='') {

  $id_vendedor=35;

  $consulta_insertar_factura_vendedor = "INSERT INTO factura_vendedor (id_factura

		 						    , id_vendedor

                        	                                    )

 							     VALUES ( $id_factura

		 						    , $id_vendedor

								    )

	                            ";

  $insertar_factura_vendedor =   @mysql_db_query($database,$consulta_insertar_factura_vendedor,$shopping_db_link);

}





////////////////////////////////////////////////////////////////////////////////////////////////////





Titulo_Maestro("Information, Order  [No. #$ido / ".$fila_factura["DATE_TIME"]."]");

#### MOSTRAMOS INFORMACION CLIENTE

?>

<style>
. tabla, tabla td, tabla th { border:1px solid #999999; border-collapse:collapse;}
</style>

<script>

	function OpenX(url,w,h,sb) {

		window.open(url,'XS','menubar,toolbar=yes,scrollbars=yes,width='+w+',height='+h); //,toolbars=yes,resizable=yes, ,'OpenW','toolbars=yes,width='+w+',height='+h+',top=0,left=0');

		//x.opener=self;

	}

</script>



<body leftmargin=  "0" topmargin=  "0" marginwidth=  "0" marginheight=  "0" bottommargin=  "0" rightmargin=  "0" bgcolor=  "#FFFFFF" text=  "#000000" link=  "#000099" vlink=  "#000099" alink=  "#000099">

<table><tr><td align=center>

<SCRIPT language=JavaScript src="./date.js"></SCRIPT>

<form name=x method="post" action="<?=$PHP_SELF ?>">

<input type=hidden name=id_cliente value="<?echo $fila_factura["ID_CLIENT"];?>">

		STATUS: <select name=status style="font-weight:bold">

			 <? SelecTable($database,'ORDERS_STATUS','ID_ORDER_STATUS',"ORDER_STATUS","",$fila_factura["ID_ORDER_STATUS"]); ?> 						

		</select>

<P>

<table width="100%" border=  "0" cellspacing=  "0" cellpadding=  "0" height=  "100%"  style="border:1px solid #999999; border-collapse:collapse;">

   <tr>

    <td valign=  "top" height=  "2" colspan=  "2" align=  "center">



<table width=100%  cellpadding=3 cellspacing=0  style="border:1px solid #999999; border-collapse:collapse;">

  <tr> 

    <td align=  "center" colspan= 2 bgcolor=  "#8080FF"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif">

	<b>Customer Information</b></font></td>

    <td> </td>

    <td align=  "center" colspan=  5 bgcolor=  "#8080FF"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif">

	<b>Shipping 

      Information</b></font></td>
  </tr>

  <tr> 

    <td>Full Name</td>

    <td><b><?echo $client_name." ".$client_last_name; if($company) { echo " ($company)"; } ?>

      <input type="hidden" name="id_client" value="<?php echo $fila_factura["ID_CLIENT"]; ?>">

    </b></td>

    <td> </td>

    <td>Full Name</td>

    <td><b> 

      <input type="text" name="sn" size=15 value="<?echo $shipping_name?>">

      <input type="text" name="sl"  size=15  value="<?echo $shipping_last_name?>">

      </b></td>
  </tr>

  <tr> 

    <td height="84">Address</td>

    <td><b>

      <textarea name="cad" cols="25" rows=2  ><? echo $client_address ?></textarea>

      <br>

    </b> 

      <input type="text" name="ccity" size="15" value="<? echo $client_city ?>">

      ,

      <input type="text" name="czc" size="6" maxlength="6" value="<? echo $client_zipcode; ?>">

      <br><select name="cst">

           <? SelecTable($database,'STATES','ID_STATE','STATE',"WHERE ID_COUNTRY=".$fila_factura["ID_COUNTRY"],$fila_factura["ID_STATE"]); ?> 

      </select> <select name="ccy">

		   <? SelecTable($database,'COUNTRIES','ID_COUNTRY','COUNTRY',"",$fila_factura["ID_COUNTRY"]); ?> 

      </select>

      <br></td>

    <td> </td>

    <td>Address</td> 

    <td>  



      <textarea name="sad" cols="25" rows=2 ><?echo $shipping_address?></textarea>

      <input type="text" name="sct" size="15" value="<?echo $shipping_city?>">,

      <input type="text" name="szc" size="6" maxlength="6" value="<?echo $shipping_zipcode?>">

<br>

      <select name="sst">

           <? SelecTable($database,'STATES','ID_STATE','STATE',"WHERE ID_COUNTRY=".$fila_factura["SHIPPING_ID_COUNTRY"],$fila_factura["SHIPPING_ID_STATE"]); ?> 
      </select>





      <select name="scy">

		   <? SelecTable($database,'COUNTRIES','ID_COUNTRY','COUNTRY',"",$fila_factura["SHIPPING_ID_COUNTRY"]); ?> 
      </select>    </td>
  </tr>

  <tr> 

    <td>Day Time Phone</td>

    <td><b><?echo Dphone($client_phone)?></b></td>

    <td> </td>

    <td>Day Time Phone</td>

    <td><b> 

      <!--<input type="text" name="sph" value="<?echo $shipping_phone?>"> -->

	  <? Phone("sph",$shipping_phone);?>

      </b></td>
  </tr>

  <tr> 

    <td>Evening Time Phone</td>

    <td><b><?echo Dphone($client_fax)?>&nbsp;</b></td>

    <td> </td>

    <td>Evening Time Phone</td>

    <td><b> 

      <!-- <input type="text" name="sf" value="<?echo $shipping_fax?>"> -->

	  	  <? Phone("sfax",$shipping_fax);?>



      </b></td>
  </tr>

  <tr> 

    <td>E-mail</td>

    <td>

    <input type="text" name="cli_email" value="<?echo $client_email?>" size=35><br><b><a href="mailto:<?echo $client_email?>">Send Email to Customer</a></b></td>

    <td> </td>

    <td>Shipping Method</td>

    <td><b><?echo $shipping_method_desc?></b></td>
  </tr>
</table> 

<br>

<?

$retailer_text = <<< EOM

   <table width=100%   style="border:1px solid #999999; border-collapse:collapse;">

      <tr><td align="center" bgcolor="#8080FF" colspan=2><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><b>Retailer Information</b></font></td></tr>

      <tr><td align="center">Retailer Number</td><td>$retailer_number</td></tr>

    </table>

<br>

EOM;



?>

<table width=100%  style="border:1px solid #999999; border-collapse:collapse;" >

  <tr> 

    <td align=  "center" colspan=4 bgcolor=  "#8080FF"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Credit 

      Card Data</b></font></td>

  </tr>

  <tr> 

    <td>Card Holder's Name<br>

    <font color=red><?=$credit_card_owner?>    

    </td>

    <td> 

	  <nobr>

      <input type="text" name="cnm" value="<?echo $client_name?>" size=15>

      <input type="text" name="clm" value="<?echo $client_last_name?>" size=15>

    </td>



    <td>Card Number</td>

    <td> <nobr>

      <input type="text" name="ccn" value="<?echo $credit_card_number?>"> 

      &nbsp;

      CVN:  <input type="text" name="cvn" value="<?echo $cvn?>" size=4>

    </td>

  </tr>

  

  <tr> 

    <td>Credit Card Type</td>

    <td> 

      <select name="cct">

		   <? 

		   SelecTable($database,'CREDIT_CARD_TYPES','ID_CREDIT_CARD_TYPE','CREDIT_CARD_TYPE',"", $fila_factura["ID_CREDIT_CARD_TYPE"]); 

		   ?> 

      </select>

    </td>



    <td>Expiry Date</td>

    <td>

	<A href="javascript:show_calendar('x.cce');">

    <IMG src="../images/calendario.gif" align=center border=0></A>

      <input type="text" name="cce" value="<?echo $credit_card_expiry?>">

    </td>

    

  </tr>

<tr>

    <td colspan=4 align=center> Comments: <br>

	<textarea cols=85 rows=3 name=ccc><?echo $comments?></textarea>

	     </td>

</tr>

<tr>

    <td colspan=4 align=center> 

	<b>Sales Person:</b>

	<?

	$consulta_vendedor = " SELECT full_name

				      , id_user

                	         FROM s_users

				WHERE cod_type='ADMX'

	                     ";

	$result_consulta_vendedor =   @mysql_db_query($db_poms,$consulta_vendedor,$shopping_db_link);

	?>



	<select name="salesperson">

	<?

	while($salesperson = mysql_fetch_array($result_consulta_vendedor)) {

	  ?>

	  <option value="<?=$salesperson["id_user"]?>"

	  <?

	  if ($salesperson["id_user"]==$id_vendedor) {

	     echo " SELECTED";

	  }

	  ?>

	  >

	  <?=$salesperson["full_name"]?>

	  </option>

	  <?

	}

	?>

	</select>

    </td>

</tr></table>

    <br>



<?

#CONSULTA PARA SABER SI HAY PRODUCTOS CON ELECCION DE COLORES Y VARIEDADES EN LA FACTURA



$consulta_colors_varieties = <<< EOM

    select COLOR_VARIETY_CHOICE 

      from FACTURAS

     where ID_FACTURA=$id_factura

EOM;





/*

$consulta_colors_varieties = <<< EOM

    select PRODUCTOS.COLOR_VARIETY_CHOICE 

      from ITEMS_FACTURA,PRODUCTOS

     where ITEMS_FACTURA.ID_PRODUCTO=PRODUCTOS.ID_PRODUCTO

       and PRODUCTOS.COLOR_VARIETY_CHOICE=1

       and ITEMS_FACTURA.ID_FACTURA=$id_factura

EOM;



*/

$result_consulta_colors_varieties = mysql_db_query($database,$consulta_colors_varieties,$shopping_db_link);

echo mysql_error();

$fila_color_variety = mysql_fetch_array ($result_consulta_colors_varieties);



  $choice_color_variety=$fila_color_variety["COLOR_VARIETY_CHOICE"];



if ($choice_color_variety) {



$mail_body .=   <<< EOD

    <table width=100%   style="border:1px solid #999999; border-collapse:collapse;">

      <tr style="border:1px solid #999999; border-collapse:collapse;"><td align="center" bgcolor="#8080FF"><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><b>Color & Varieties Choice</b></font></td></tr>

      <tr><td align="center">$choice_color_variety</td></tr>

    </table>

    <br>

EOD;



}



if ($msgx) {



$mail_body .=   <<< EOD

    <table width=100%   style="border:1px solid #999999; border-collapse:collapse;">

      <tr><td align="center" bgcolor="#8080FF"><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><b>Personalized Message</b></font></td></tr>

      <tr><td align="center">$msgx</td></tr>

    </table>

    <br>

EOD;



}







# CONSULTA PARA SACAR LOS PRODUCTOS ADQUIRIDOS



$consulta_items_factura =   <<< EOC

    select ITEMS_FACTURA.ARRIVAL_DATE,ITEMS_FACTURA.ITEM_PRICE,

           ITEMS_FACTURA.PRODUCT_DESC,ITEMS_FACTURA.ID_ITEM_FACTURA,

           ITEMS_FACTURA.ID_QUANTITY AS QUANTITY

       from ITEMS_FACTURA

       where ITEMS_FACTURA.ID_FACTURA=$id_factura

EOC;



# GENERAMOS LA LISTA DE PRODUCTOS ADQUIRIDOS



$result_consulta_items_factura =   mysql_db_query($database,$consulta_items_factura,$shopping_db_link);



$mail_body .=   <<< EOI

<table width=100%  cellpadding=3  style="border:1px solid #999999; border-collapse:collapse;">

  <tr bgcolor=  "#8080FF"> 

    <td align=  "center"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Product 

      Ordered</b></font></td>

    <td align=  "center"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Qty</b></font></td>

    <td align=  "center"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Total</b></font></td>

    <td align=  "center"><font color=  "#FFFFFF" size=  "2" face=  "Arial, Helvetica, sans-serif"><b>Desired 

      Delivery Date</b></font></td>

  </tr>

EOI;



 while ($fila_shopping_cart =   mysql_fetch_array ($result_consulta_items_factura)) {



    $delivery_date_shopping_cart=  $fila_shopping_cart["ARRIVAL_DATE"];

    $price_producto_shopping_cart=  $fila_shopping_cart["ITEM_PRICE"];

    $product_desc=  $fila_shopping_cart["PRODUCT_DESC"];

    $quantity_shopping_cart=  $fila_shopping_cart["QUANTITY"];



    $total_producto_shopping_cart=  $price_producto_shopping_cart*$quantity_shopping_cart;

    $format_total_producto_shopping_cart = sprintf ("%01.2f", $total_producto_shopping_cart);



$mail_body .= "<tr style='border:1px solid #999999; border-collapse:collapse;'><td>$product_desc</b></td><td align=right>$quantity_shopping_cart</td><td align=right>\$".$format_total_producto_shopping_cart."</td><td>";



	$day_charge = "";

	if (date("w",$delivery_date_shopping_cart) == "6") { $day_charge= " add $15 per Box";}

    $mail_body .= date("M. j (l",$delivery_date_shopping_cart).$day_charge.")<br>";

    $mail_body .="<A href=\"javascript:show_calendar('x.f1[".$fila_shopping_cart["ID_ITEM_FACTURA"]."]');\">

    <IMG src=\"../images/calendario.gif\" align=center border=0></A>

    <input type=hidden name=idi[] value=".$fila_shopping_cart["ID_ITEM_FACTURA"].">

    <input type=text name=\"f1[".$fila_shopping_cart["ID_ITEM_FACTURA"]."]\" id=\"f1[".$fila_shopping_cart['ID_ITEM_FACTURA']."]\" size=10></td></tr>";

    

 }



 mysql_free_result ($result_consulta_items_factura);



 $format_subtotal_products_factura = sprintf ("%01.2f", $subtotal_products_factura);

 $format_subtotal_taxes_factura = sprintf ("%01.2f", $subtotal_taxes_factura);

 $format_shipping_cost_factura = sprintf ("%01.2f", $shipping_cost_factura);

 $format_grand_total_factura = sprintf ("%01.2f", $grand_total_factura);



$mail_body .=   <<< EOI

 <tr><td colspan=  2><b>Order Sub Total : </b></td><td align=right><b>\$$format_subtotal_products_factura</td></tr>

 <tr><td colspan=  2><b>Subtotal Taxes : </b></td><td align=right>\$$format_subtotal_taxes_factura</td></tr>
 <tr><td colspan=  2 ><b>Personalized Message : </b></td><td align=right>\$$mess_charge</td></tr>
 <tr><td colspan=  2 ><b>Phone Order Service Charge : </b></td><td align=right>\$$phone_charge</td></tr>

 <tr><td colspan=  2><b>Shipping Charges : </b></td><td align=right>\$$format_shipping_cost_factura</td></tr>

 <tr><td colspan=  2><b>Order Grand Total : </b></td><td align=right><b><big>\$$format_grand_total_factura</b></td></tr>

 </table></td></tr>



<tr><td></td></tr></table><p>

<center>

<input type=button onclick="javascript:print()" value="Print" style="font-weight:bold">&nbsp;&nbsp;&nbsp;&nbsp;

<input type=submit value="Update" style="font-weight:bold">

<input type=button value="Re-Send Mail" style="font-weight:bold" onclick="OpenX('./resend.php?c=$client_email&id_factura_facturas=$id_factura','350','350',1)">

<input type=hidden name=flag value="$id_factura">

</form>



EOI;



echo $mail_body;

?>

<form method="post" action="https://orderpage.ic3.com/hop/ProcessOrder.do">

<?

InsertSignature($grand_total_factura);

$body1 = <<< EOM

	<input type="hidden" name="orderNumber" value="$ido">

	<input type="hidden" name="comments" value="$comments $color_variety_choice">



	<input type="hidden" name="billTo_city" value="$client_city">

	<input type="hidden" name="billTo_country" value="$client_country_code">

	<input type="hidden" name="billTo_company" value="$company">

	<input type="hidden" name="billTo_email" value="$client_email">

	<input type="hidden" name="billTo_firstName" value="$client_name">

	<input type="hidden" name="billTo_lastName" value="$client_last_name">

	

	<input type="hidden" name="billTo_postalCode" value="$client_zipcode">

	<input type="hidden" name="billTo_state" value="$client_state">

	<input type="hidden" name="billTo_phoneNumber" value="$client_phone">

	<input type="hidden" name="billTo_street1" value="$client_address">

	<input type="hidden" name="card_cardType" value="$cc_type">

	<input type="hidden" name="card_expirationMonth" value="$credit_card_expiry_month">

	<input type="hidden" name="card_expirationYear" value="$credit_card_expiry_year">

	<input type="hidden" name="card_accountNumber" value="$credit_card_number">

	<input type="hidden" name="card_cvNumber" value="$cvn">

EOM;

echo $body1;

?>

<input type=submit value="Resubmit Payment" style="font-weight:bold;color=red">

</form>



</td></tr></table><P>

</body>

</html>

<?

if (!session_is_registered("id_factura_facturas")) {

	session_register("id_factura_facturas");

}

	$id_factura_facturas = $ido;



echo $mail_header;

//echo $mail_body;



?>