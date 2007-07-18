<?
session_start();
include ('../include/head.php');
include ('../include/checkout.php');
//echo "----------------". $xphone;
//echo "flag". $flag;
$mess=$phone_charge=0.00;
$surcharge=0.00;
//if(isset($msg) && $msg!=""){$mess=5.00;}
$ssl=1;
$b_desc = array("F" => "FULL/BIG", "H" => "HALF/REGULAR", "Q" => "QUARTER/SMALL", "E" => "EIGHTH/CONSUMER", "C" => "CUSTOMER");
$b_cost = array("F" => "83.00", "H" => "48.50", "Q" => "33.00", "E" => "22.50", "C" => "60.00");
$sub_total_pro = 0;
$bph = $bph1.$bph2.$bph3;
$sph = $sph1.$sph2.$sph3;


#Info del anteror script
##################################
# Grabar Info del Shipping, vendedor, email , customer y sacar script de Pago
$query = "
	UPDATE	CLIENTS 
	SET		COMPANY = '$comp'
			, CLIENT_NAME = '$bfn'
			, CLIENT_LAST_NAME = '$bln'
			, CLIENT_ADDRESS = '$bad'
			, CLIENT_EMAIL='$mail'
			, CLIENT_ZIPCODE = '$bzc'
			, CLIENT_PHONE = '$bph'
			, CLIENT_CITY = '$bct'
			, ID_STATE = '$bstate'
			, ID_COUNTRY = '$bcountry'
			, SHIPPING_NAME = '$sfn'
			, SHIPPING_LAST_NAME = '$sln'
			, SHIPPING_ADDRESS = '$sad'
			, SHIPPING_PHONE = '$sph'
			, SHIPPING_CITY = '$sct'
			, SHIPPING_ZIPCODE  = '$szc'
			, SHIPPING_ID_COUNTRY = '$scountry'
			, SHIPPING_ID_STATE = '$sstate'
			, RETAILER_NUMBER='$ret' 
			, FEDEX_ACCOUNT='$sfaccount' 
		";

$query .= "
	WHERE	ID_CLIENT = $id_client
	";

?>
<!--
<?php echo $query;?>
-->
<?php
mysql_db_query($db,$query);


#CONSULTA PARA MOSTRAR LOS PRODUCTOS EN EL SHOPPING CART ACTUAL
$consulta_shopping_cart = <<< EOM
    select SHOPPING_CART.ID_SHOPPING_CART,SHOPPING_CART.DAY_CHARGE,
           SHOPPING_CART.DELIVERY_DATE,SHOPPING_CART.PRICE,
           SHOPPING_CART.ID_QUANTITY as QUANTITY,SHOPPING_CART.PRODUCT_DESC,SHOPPING_CART.surcharge,
           MARKETS.ID_MARKET,SHOPPING_CART.ID_PROMOTION, SHOPPING_CART.design_choice, PRODUCTOS.ID_TIPO_PRODUCTO 
      from SHOPPING_CART,MARKETS,PRODUCTOS
     where SHOPPING_CART.ID_PRODUCTO=PRODUCTOS.ID_PRODUCTO
       and MARKETS.ID_MARKET=PRODUCTOS.ID_MARKET
       and SHOPPING_CART.SESSION_ID='$session_id'
EOM;


$country_charge_shopping_cart = Getdata ("COUNTRIES","SHIPPING_CHARGE_COUNTRY","WHERE ID_COUNTRY=".$scountry,$db);
$charges_shipping_method = Getdata ("SHIPPING_METHODS","SHIPPING_COST","WHERE  ID_SHIPPING_METHOD=1",$db);
$state_charge_shopping_cart = Getdata ("STATES","SHIPPING_CHARGE_STATE","WHERE ID_STATE=".$sstate,$db);


# CALCULAMOS LOS VALORES DEL SHOPPING CART PARA INSERTAR EN LA FACTURA
$result_consulta_shopping_cart = mysql_db_query($database,$consulta_shopping_cart) or die(mysql_error());
//echo mysql_error();
$discount=$eb=0;
function applicance_discount($box){
	if ($box>=2 && $box<=3) {
		$discount=12;
	} elseif ($box>3 && $box<=5) {
		$discount=38.5;
	} elseif ($box>5 && $box<=7) {
	 	$discount=50.5;
	} elseif ($box>7 && $box<=9) {
		$discount=77;
	} elseif ($box>9) {
		$discount=89;
	} else {
		$box="NA";
		$discount="NA";
	}
	$string = "($discount $box)";
	return $discount;
}

$fila_shopping_cart = mysql_fetch_assoc($result_consulta_shopping_cart);
 do{
    $eb+=( $fila_shopping_cart["ID_TIPO_PRODUCTO"]==12) ?  $fila_shopping_cart["QUANTITY"] : 0;
	if($eb>=2 && $eb<=3){
	 $discount=12;
	}elseif($eb>3 && $eb<=5){
	  $discount=38.5;
	}elseif($eb>5 && $eb<=7){
	  $discount=50.5;
	}elseif($eb>7 && $eb<=9){
	  $discount=77;
	}elseif($eb>9){
	  $discount=89;
	}
	
	if ($fila_shopping_cart["ID_TIPO_PRODUCTO"]==12) {	
		$counter_date=0;
		$found_date=0;
		while ($date_item_shopping[$counter_date][0]!=NULL) {
			$applicance_discount_string .= $counter_date." ".$fila_shopping_cart["DELIVERY_DATE"];
			if ($fila_shopping_cart["DELIVERY_DATE"]==$date_item_shopping[$counter_date][0]) {
				$date_item_shopping[$counter_date][1] += $fila_shopping_cart["QUANTITY"];
				$found_date=1;
			}
			$counter_date++;
		}
		if($found_date==0) {
			$date_item_shopping[$counter_date][0]=$fila_shopping_cart["DELIVERY_DATE"];
			$date_item_shopping[$counter_date][1] += $fila_shopping_cart["QUANTITY"];
		}
	}
    $sub_total_pro += $fila_shopping_cart["PRICE"]*$fila_shopping_cart["QUANTITY"];
    $sub_total_day_charges += $fila_shopping_cart["QUANTITY"]*$fila_shopping_cart["DAY_CHARGE"];
    $sub_total_state_charges += $fila_shopping_cart["QUANTITY"]*$state_charge_shopping_cart;
    $sub_total_country_charges += $fila_shopping_cart["QUANTITY"]*$country_charge_shopping_cart;
    $sub_total_charges_shipping_method += $fila_shopping_cart["QUANTITY"]*$charges_shipping_method;
	$surcharge +=$fila_shopping_cart["surcharge"];
 }while ($fila_shopping_cart = mysql_fetch_assoc($result_consulta_shopping_cart));
$state_tax_shopping_cart = (float)Getdata("STATES","TAX","WHERE  ID_STATE=".$sstate,$db)/100;
if ($ret)  { $state_tax_shopping_cart = 0; }
$sub_total_charges = $sub_total_day_charges + $sub_total_state_charges + $sub_total_country_charges +$sub_total_charges_shipping_method;
if ($servicePhone==1) { 
$stvalue=($sub_total_pro+ $sub_total_charges +  $mess)*0.015;
  $phone_charge =( $stvalue<5.00)?5.00:round($stvalue,2);
} 
$subtotal_taxes = $sub_total_pro+$sub_total_charges+$mess+$phone_charge;
$subtotal_taxes +=$surcharge;
$subtotal_taxes = $subtotal_taxes*$state_tax_shopping_cart;
$grand_total_shopping_cart = $sub_total_pro+ $sub_total_charges + $subtotal_taxes + $mess + $phone_charge;
$grand_total_shopping_cart +=$surcharge;

$discount_eighthprogram=0;
for ($counter=0; $counter<=$counter_date; $counter++) {
	$discount_eighthprogram += applicance_discount($date_item_shopping[$counter][1]);
}
	
if($discount_eighthprogram){
$grand_total_shopping_cart=$grand_total_shopping_cart-$discount_eighthprogram;
}

if(!$id_factura) {
		################## INSERT FACTURA
		$insert_facturas = <<< EOM
		    INSERT INTO FACTURAS (DATE_TIME,ID_CLIENT,SHIPPING_NAME,SHIPPING_LAST_NAME,SHIPPING_ADDRESS,
		    SHIPPING_ZIPCODE,SHIPPING_PHONE,SHIPPING_CITY,SHIPPING_ID_STATE,SHIPPING_ID_COUNTRY,
		    SUBTOTAL_PRODUCTS,ID_SHIPPING_METHOD,SHIPPING_COST,SUBTOTAL_TAXES,GRAND_TOTAL,ID_ORDER_STATUS,
		    ID_STANDING_ORDER_INFO,RETAILER_NUMBER,cost_card,message,SESSION_ID,page,mess_charge,phone_charge,discount)
		    values (NOW(),$id_client,"$sfn","$sln","$sad","$szc","$sph","$sct",$sstate,$scountry,
		    $sub_total_pro,1,$sub_total_charges,$subtotal_taxes, $grand_total_shopping_cart,4,0,"$ret",5,"$msg","$session_id","ff2g",$mess,$phone_charge,$discount_eighthprogram)
EOM;

				mysql_db_query($db,"lock tables FACTURAS");
				mysql_db_query($db,$insert_facturas) or die (mysql_error().$insert_facturas);
				//echo $insert_facturas;
				//echo mysql_error();
				if (!mysql_error()) {
					$ic = mysql_fetch_row(mysql_db_query($db,"SELECT MAX(ID_FACTURA) FROM FACTURAS"));
					$id_factura = $ic[0];
					
					if (!session_is_registered("xfactura")) { 
							session_register("xfactura");
							$xfactura = $id_factura;
					}		
					
					
					mysql_db_query($db,"insert into factura_vendedor (id_factura, id_vendedor) VALUES ($id_factura,$vendedor)");
				}	else {
					smtpmail("paula@paularojas.com",$subject,mysql_error()." ".$insert_facturas,"From: $client_email Orders FlowerFarmsToGo.com <orders@flowerfarmstogo.com>\r\n".$mime_header);
				}
				mysql_db_query($db,"unlock tables");	
				//echo mysql_error();
				
				
				

} else {

	# INSERTAMOS LOS DATOS EN LA FACTURA ENLAZADOS CON EL CLIENTE
	$actualizar_facturas = <<< EOM
	    update FACTURAS 
	        SET DATE_TIME=NOW()
		   ,SHIPPING_NAME="$sfn"
		   ,SHIPPING_LAST_NAME="$sln"
		   ,SHIPPING_ADDRESS="$sad"
		   ,SHIPPING_ZIPCODE="$szc"
		   ,SHIPPING_PHONE="$sph"
		   ,SHIPPING_CITY="$sct"
		   ,SHIPPING_ID_STATE=$sstate
		   ,SHIPPING_ID_COUNTRY=$scountry
		   ,SUBTOTAL_PRODUCTS=$sub_total_pro
		   ,SHIPPING_COST=$sub_total_charges
		   ,SUBTOTAL_TAXES=$subtotal_taxes
		   ,GRAND_TOTAL=$grand_total_shopping_cart
		   ,ID_ORDER_STATUS=1
		   ,RETAILER_NUMBER="$ret"
		   ,cost_card=5
		   ,message="$msg"
		   ,othercharges=$sub_total_charges+$surcharge
		   ,discount=$discount_eighthprogram
	       WHERE ID_FACTURA=$id_factura
EOM;
	//echo $actualizar_facturas;
	mysql_db_query($db,$actualizar_facturas) or die(mysql_error());
}

		mysql_db_query($db,"delete from ITEMS_FACTURA where ID_FACTURA=".$id_factura);
		$insert_items_factura = <<< EOM
		   INSERT INTO ITEMS_FACTURA (ID_FACTURA,ID_CLIENT,ID_PRODUCTO,PRODUCT_DESC,ID_QUANTITY,ITEM_PRICE,ID_PROMOTION,ARRIVAL_DATE,DAY_CHARGE)
		   select $id_factura,$id_client,ID_PRODUCTO,CONCAT(PRODUCT_DESC,'<br><b><font color=brown>',design_choice,'</font></b>'),ID_QUANTITY,PRICE,ID_PROMOTION,DELIVERY_DATE,DAY_CHARGE
		   from SHOPPING_CART where SESSION_ID="$session_id"
EOM;
		mysql_db_query($db,$insert_items_factura);
		//echo mysql_error();
		//echo $insert_items_factura;


$queryP = "SELECT distinct Z.box_type,SUM(I.ID_QUANTITY) as ID_QUANTITY FROM ITEMS_FACTURA I, PRODUCTOS P LEFT OUTER JOIN SIZES Z ON Z.ID_SIZE=P.ID_SIZE WHERE  P.ID_PRODUCTO=I.ID_PRODUCTO AND I.ID_FACTURA=$id_factura GROUP BY Z.box_type";

//echo $queryP;
$st = mysql_db_query($db,$queryP) or die(mysql_error());
//echo mysql_error();

if (mysql_num_rows($st)) { 

$boxesMI = "<table width=100% cellpadding=0 cellspacing=1><tr><td><br><b>This price includes Non Refundable FedEx Overnight Priority delivery charges.<br>".
"The Non Refundable FedEx delivery charge included in the price of this order is: ".
"<br><table cellpadding=3 cellspacing=1 width=100% bgcolor=cecece>".
  "<tr>     <td> <div align=center><b>Box Type</b></div></td><td>Unit Cost</tD><td> <div align=center><b>Cost</b></div>".
    "</td></tr>";

 while($dx = mysql_fetch_array($st)) {

  $boxesMx .= "<tr bgcolor=ffffff> <td> ".$b_desc[$dx["box_type"]]."</td><td align=right> ".number_format($b_cost[$dx["box_type"]],2)."</td><td align=right>".number_format($dx["ID_QUANTITY"]*$b_cost[$dx["box_type"]],2).
     "</td></tr>";
  
	} 
$boxesMF .= "</table></td></tr></table><p>";
} 

if ($boxesMx) { $boxesM =  $boxesMI. $boxesMx .$boxesMF; }
#################################################
### Cargando la Orden en la tabla de facturas ###
#################################################
if ($id_factura) { $nid_factura = " Order # $id_factura"; } 
?>
<p><b><big>Check Out Process <?=$nid_factura?></b><br>
</big>
<b><font color=brown>FlowerFarmsToGo.com will save your 
billing information, but not your credit card number.</font>
<P>


<table border="0" cellpadding=0 cellspacing=0 align=center>
  <tr>	
    <td align=center>
    
    		
<P>
<table bgcolor=dedede cellpadding=3 cellspacing=1 width=100%>
        <tr bgcolor=efefef> 
        
          <td colspan="4"><b><font size=2 color=brown>Order Details 
          </td>
        </tr>
<?

$result_consulta_shopping_cart = mysql_db_query($database,$consulta_shopping_cart);

while ($fila_shopping_cart = mysql_fetch_array ($result_consulta_shopping_cart)) {

    $id_shopping_cart=$fila_shopping_cart["ID_SHOPPING_CART"];
    $day_charge_shopping_cart=$fila_shopping_cart["DAY_CHARGE"];
    $delivery_date_shopping_cart=$fila_shopping_cart["DELIVERY_DATE"];
    $price_producto_shopping_cart=$fila_shopping_cart["PRICE"];
    $quantity_shopping_cart=$fila_shopping_cart["QUANTITY"];
    $id_promotion=$fila_shopping_cart["ID_PROMOTION"];
    $product_desc=$fila_shopping_cart["PRODUCT_DESC"];
    $dc = "";
	if ($fila_shopping_cart["design_choice"]) { $dc = "\n<br><font color=brown><b>".$fila_shopping_cart["design_choice"]."</b></font>"; }

    $total_producto_shopping_cart=$price_producto_shopping_cart*$quantity_shopping_cart;
    $sub_total_productos_shopping_cart += $total_producto_shopping_cart;
    $sub_total_day_charges += $quantity_shopping_cart*$day_charge_shopping_cart;
    $sub_total_state_charges += $quantity_shopping_cart*$state_charge_shopping_cart;
    $sub_total_country_charges += $quantity_shopping_cart*$country_charge_shopping_cart;
    $sub_total_charges_shipping_method += $quantity_shopping_cart*$charges_shipping_method;

    $format_total_producto_shopping_cart = sprintf ("%01.2f", $total_producto_shopping_cart);

    echo "<tr align=\"right\" bgcolor=ffffff><td align=left>".$product_desc.$dc."</td><td>".$quantity_shopping_cart."</td><td align=right>\$".$format_total_producto_shopping_cart."</td>\n<td>\n";
      $day_desc = date("M. j (l)",$delivery_date_shopping_cart);
    echo "<nobr>$day_desc</nobr></td>\n";
    echo "</tr>\n";
 }

# 6.5% TAX de la Florida

 //$state_tax_shopping_cart = 0;
 //$subtotal_taxes = $sub_total_productos_shopping_cart*$state_tax_shopping_cart;
// $sub_total_charges = $sub_total_day_charges + $sub_total_state_charges + $sub_total_country_charges + $sub_total_charges_shipping_method ;
// $grand_total_shopping_cart = $sub_total_productos_shopping_cart + $sub_total_charges + $subtotal_taxes;

 $format_sub_total_productos_shopping_cart = number_format($sub_total_productos_shopping_cart,2);
 $format_sub_total_charges = number_format($sub_total_charges,2);
 //echo $format_sub_total_charges;
 //echo $flag . "flag";
 $format_subtotal_taxes = number_format($subtotal_taxes,2);
 $format_grand_total_shopping_cart = number_format($grand_total_shopping_cart,2);

 echo "<tr align=\"right\" bgcolor=ffffff style=\"border-top:2px solid #000000;\"><td colspan=2 align=\"right\" style=\"border-top:2px solid #000000;\"><b>Order Sub Total : </b></td><td style=\"border-top:2px solid #000000;\">\$$format_sub_total_productos_shopping_cart</td></tr>";
  echo "<tr align=\"right\" bgcolor=ffffff><td colspan=2 align=\"right\"><b>Other Delivery Charges : </b></td><td>\$".number_format($sub_total_charges,2)."</td></tr>";
 echo "<tr align=\"right\" bgcolor=ffffff><td colspan=2 align=\"right\"><b>Personal message : </b></td><td>\$".number_format($mess,2)."</td></tr>";

 echo "<tr align=\"right\" bgcolor=ffffff><td colspan=2 align=\"right\"><b>Phone Orders Service Charge : </b></td><td>\$".number_format($phone_charge,2)."</td></tr>";
  echo "<tr align=\"right\" bgcolor=ffffff><td colspan=2 align=\"right\"><b>Other Charges : </b></td><td>\$".number_format($surcharge,2)."</td></tr>";
   echo "<tr align=\"right\" bgcolor=ffffff><td colspan=2 align=\"right\"><b>Eighth Box Program Discount : </b></td><td>\$".number_format($discount_eighthprogram,2)."</td></tr>";
  echo "<tr align=\"right\" bgcolor=ffffbb><td colspan=2 align=\"right\"><b>Order Total : </b></td><td>\$".number_format($grand_total_shopping_cart-$subtotal_taxes,2)."</td></tr>";
 echo "<tr align=\"right\" bgcolor=ffffff><td colspan=2 align=\"right\"><b>Florida Sales Tax : </b></td><td>\$$format_subtotal_taxes</td></tr>";
 echo "<tr align=\"right\" bgcolor=ffffbb><td colspan=2 align=\"right\"><b>Order Grand Total : </b></td><td><b>\$$format_grand_total_shopping_cart</b></td></tr>";
 mysql_free_result ($result_consulta_shopping_cart);
 echo "</table>";
 echo $boxesM;


################### FIN SHOPPING CART
?>

<p>
	    		
    		
<table width="100%" border="0" bgcolor=cecece cellpadding=3 cellspacing=1> 
  <tr bgcolor=efefef> 
    <td><b><font size=2 color=brown>Billing Information</td>
    <td><b><font size=2 color=brown>Shipping Information</td>
  </tr>
  <tr bgcolor=ffffff> 
    <td width=50%>
    <b><?= "$bfn $bln"?></b><br>
    <?= "$bad"?><br>
    <?= "$bct"?>, <?= Getdata("STATES","code","WHERE ID_STATE=".$bstate,$db)?>, <?= "$bzc"?> <br>
      <?= Getdata("COUNTRIES","COUNTRY","WHERE ID_COUNTRY=".$bcountry,$db)?>
    </td>
    
    
    
    <td>
    <b><?= "$sfn $sln"?></b><br>
    <?= "$sad"?><br>
    <?= "$sct"?>, <?= Getdata("STATES","code","WHERE ID_STATE=".$sstate,$db)?>,  <?= "$szc"?><br>
    <?= Getdata("COUNTRIES","COUNTRY","WHERE ID_COUNTRY=".$scountry,$db)?>
    
    </td>
  </tr>
  <tr><td bgcolor=ffff77 colspan=2>
	<b>Personalized message:</b> <?=$msg?>
  </td></tr>
</table>

<p>



<!---- METODOS DE PAGO - CC, EC, PP ###############   -->
<!---- ############################################   -->

<? 

include($payment."_payment.php");
echo $payment;
?>
<P>

<table><tr><td>
<?
include("./checkout_disclaimer.php");
?>
</td></tr></table>

</td>
</tr>
</table>

<? include ('../include/end.php'); ?>