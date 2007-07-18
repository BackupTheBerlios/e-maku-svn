<?
?>
<script>
	function Empty() {
		window.location.href='empty_car_nopublic.php';
	}	
	
	function AddCharge(){
		var charge
		charge = prompt("Write the amount for additional charge :","");		
		window.location.href='add_charge_nopublic.php?charge='+charge;		
	}
	
	var dc = 0;
	var dcu  = 0;
	
	function Add_dc(texto) {
		if (texto != '') { dcu += 1;}
		else { dcu -= 1; }
	}
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <tr>
    <td valign="top" height="2" colspan="2" align="center">
    <table cellpadding=3 border=1 cellspacing=1 bordercolor=acacac>
    <tr bgcolor="#CC3333">
    <td align="center"><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">
    <b>Product Ordered</b></td><td align="center">
    <font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">
    <b>Price<br>per Box</b></td><td align="center">
    <font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><b>Qty</b></td>
    <td align="center"><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><b>Total</b></td>
    <td align="center"><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><b>Desired Delivery Date</b></td>
    <td></td><td></td></tr>
<?php

$session_id = $OID;
# CONSULTA PARA VER SI EXISTE UN MERCADO ACTIVO DIFERENTE AL REGULAR
# SACAMOS FECHA DE HOY PARA SABER SI EL MERCADO ESTA ACTIVO


# CONSULTA PARA MOSTRAR LOS PRODUCTOS EN EL SHOPPING CART ACTUAL

/*
PAULA ROJAS Dic 7/2004
No veo para que se debe consultar la tabla SUB_TIPO_PRODUCTO. La consulta a esta tabla hace que no se muestre en el shopping cart los productos nuevos que se crean para la orden.
OJO: Si llega a presentarse algn problema con esto, cambie la l�ea:
$result_consulta_shopping_cart = mysql_db_query($database,$consulta_shopping_cart,$shopping_db_link);
por
$result_consulta_shopping_cart = mysql_db_query($database,$consulta_shopping_cart1,$shopping_db_link);
*/

$consulta_shopping_cart1 = <<< EOM
    select SHOPPING_CART.ID_SHOPPING_CART,SHOPPING_CART.DAY_CHARGE,
           SHOPPING_CART.DELIVERY_DATE,SHOPPING_CART.PRICE,
           SHOPPING_CART.ID_QUANTITY AS QUANTITY,SHOPPING_CART.PRODUCT_DESC,
           SHOPPING_CART.PRICE*SHOPPING_CART.ID_QUANTITY as TOTAL_PRODUCTO, SHOPPING_CART.design_choice,
           MARKETS.ID_MARKET,SHOPPING_CART.ID_PROMOTION,
           PRODUCTOS.ID_SUB_TIPO_PRODUCTO,
           PRODUCTOS.ID_PRODUCTO, PRODUCTOS.DELIVERY_DELAY,
           PRODUCTOS.PRODUCTO,PRODUCTOS.COLOR_VARIETY_CHOICE,
           PRODUCTOS.ID_SIZE,
           SUB_TIPO_PRODUCTOS.ADDON,
           UNIX_TIMESTAMP(MARKETS.START_DATE) as startd,UNIX_TIMESTAMP(MARKETS.END_DATE) as endd,
           UNIX_TIMESTAMP(MARKETS.ACTIVATE_DATE) as activated,UNIX_TIMESTAMP(MARKETS.DESACTIVATE_DATE) as desactivated
      from SHOPPING_CART,MARKETS,PRODUCTOS,SUB_TIPO_PRODUCTOS
     where SHOPPING_CART.ID_PRODUCTO=PRODUCTOS.ID_PRODUCTO
       and SUB_TIPO_PRODUCTOS.ID_SUB_TIPO_PRODUCTO=PRODUCTOS.ID_SUB_TIPO_PRODUCTO
       and MARKETS.ID_MARKET=PRODUCTOS.ID_MARKET
       and PRODUCTOS.ID_MARKET!=2
       and SHOPPING_CART.SESSION_ID='$session_id'
EOM;

$consulta_shopping_cart = <<< EOM
    select SHOPPING_CART.ID_SHOPPING_CART,SHOPPING_CART.DAY_CHARGE,
           SHOPPING_CART.DELIVERY_DATE,SHOPPING_CART.PRICE,
           SHOPPING_CART.ID_QUANTITY AS QUANTITY,SHOPPING_CART.PRODUCT_DESC,
           SHOPPING_CART.PRICE*SHOPPING_CART.ID_QUANTITY as TOTAL_PRODUCTO, SHOPPING_CART.design_choice,
           MARKETS.ID_MARKET,SHOPPING_CART.ID_PROMOTION,
           PRODUCTOS.ID_SUB_TIPO_PRODUCTO,
           PRODUCTOS.ID_PRODUCTO, PRODUCTOS.DELIVERY_DELAY,
           PRODUCTOS.PRODUCTO,PRODUCTOS.COLOR_VARIETY_CHOICE,
           PRODUCTOS.ID_SIZE,
           UNIX_TIMESTAMP(MARKETS.START_DATE) as startd,UNIX_TIMESTAMP(MARKETS.END_DATE) as endd,
           UNIX_TIMESTAMP(MARKETS.ACTIVATE_DATE) as activated,UNIX_TIMESTAMP(MARKETS.DESACTIVATE_DATE) as desactivated
      from SHOPPING_CART,MARKETS,PRODUCTOS
     where SHOPPING_CART.ID_PRODUCTO=PRODUCTOS.ID_PRODUCTO
       and MARKETS.ID_MARKET=PRODUCTOS.ID_MARKET
       and PRODUCTOS.ID_MARKET!=2
       and SHOPPING_CART.SESSION_ID='$session_id'
EOM;

//echo $consulta_shopping_cart;
$result_consulta_shopping_cart = mysql_db_query($database,$consulta_shopping_cart,$shopping_db_link);
echo mysql_error();
# GENERAMOS EL SHOPPING CART

 while ($fila_shopping_cart = mysql_fetch_array ($result_consulta_shopping_cart)) {

    $id_shopping_cart=$fila_shopping_cart["ID_SHOPPING_CART"];
    $day_charge_shopping_cart=$fila_shopping_cart["DAY_CHARGE"];
    $delivery_date=$fila_shopping_cart["DELIVERY_DATE"];
    $price_producto_shopping_cart=$fila_shopping_cart["PRICE"];
    $quantity_shopping_cart=$fila_shopping_cart["QUANTITY"];
    $id_sub_tipo_producto_shopping_cart=$fila_shopping_cart["ID_SUB_TIPO_PRODUCTO"];
    $addon_shopping_cart=$fila_shopping_cart["ADDON"];
    $id_producto_shopping_cart=$fila_shopping_cart["ID_PRODUCTO"];
    //$p_delay = $fila_shopping_cart["DELIVERY_DELAY"];
    $producto_shopping_cart=$fila_shopping_cart["PRODUCTO"];
    $id_size_shopping_cart=$fila_shopping_cart["ID_SIZE"];
    $id_market=$fila_shopping_cart["ID_MARKET"];
    $id_promotion=$fila_shopping_cart["ID_PROMOTION"];
    $market_date_start=$fila_shopping_cart["startd"];
    $market_date_end=$fila_shopping_cart["endd"];
    $market_date_activated=$fila_shopping_cart["activated"];
    $market_date_desactivated=$fila_shopping_cart["desactivated"];
    $product_desc=$fila_shopping_cart["PRODUCT_DESC"];
    $total_producto_shopping_cart=$fila_shopping_cart["TOTAL_PRODUCTO"];
    $design=$fila_shopping_cart["COLOR_VARIETY_CHOICE"]; 
	$dc_texto=$fila_shopping_cart["design_choice"]; 

#    $total_producto_shopping_cart=$price_producto_shopping_cart*$quantity_shopping_cart;
    $sub_total_productos_shopping_cart += $total_producto_shopping_cart;
    settype($sub_total_productos_shopping_cart,"double");
    $sub_total_day_charges += $quantity_shopping_cart*$day_charge_shopping_cart;

    echo "<form name=\"select_date_".$id_shopping_cart."\" method=\"post\" action=\"producto_delivery_sc_nopublic.php\">\n";
    echo "<tr><td>".$product_desc."</b><br>";
   if($design) { 
	?>
	<script>
		dc += 1; 
		<? if ($dc_texto) { ?>
			dcu += 1;
		<?} ?>
	</script>
	<b>Please write color & variety choice</b><br>
	<textarea name="dch" rows=2 cols=55 onChange="Add_dc(this.value); this.form.submit()"><?echo $dc_texto?></textarea>
	<br><!input type=submit value="Update">
	<?
	}

    
    echo "</td>";
	if ($_SESSION['typeUser']==1){
    	echo "<td align=right>$<input type=text name=price size=4 maxlength=7 value=\"".$price_producto_shopping_cart."\">";
    	
	}else{
		echo "<td align=right>$". $price_producto_shopping_cart."</td>";
		echo "<input type='hidden' name='price' value=".$price_producto_shopping_cart.">";
	}
    echo "<td align=center>".$quantity_shopping_cart."</td><td>\$".$total_producto_shopping_cart."</td>\n<td>\n";
          
          echo "<input type=\"hidden\" name=\"id_shopping_cart\" value=".$id_shopping_cart.">\n";

	$day_charge = "";
	if (date("w",$delivery_date) == "6") $day_charge= " add $15 per Box";
    echo date("M. j (l",$delivery_date).$day_charge.")<br>";    
    ?>
    <A href="javascript:show_calendar('select_date_<?echo $id_shopping_cart?>.f1');">
	<IMG    src="../images/calendario.gif" align=center border=0></A>   
    <input type="text" name="f1" size=10 value="<?echo $f1?>">
	<?
    
    echo "</td>";
	echo "<td><b>Surcharge : </b>$<input type='text' name='surcharge' size='5'></td>\n";
    echo "<td><input style=\"font-weight:bold;\" type=\"submit\" value=\"Update\"></td>";
    echo "<td><a href=\"delete_producto_shopping_cart_nopublic.php?ss=$session_id&id_shopping_cart=$id_shopping_cart\"><img src=../images/del.gif border=0></a></td>";
	echo "</form></tr>\n";
}

 $state_tax_shopping_cart = 0;

 $subtotal_taxes = $sub_total_productos_shopping_cart*$state_tax_shopping_cart;

 $grand_total_shopping_cart = $sub_total_productos_shopping_cart + $sub_total_day_charges + $subtotal_taxes;

 $format_sub_total_productos_shopping_cart = sprintf ("%01.2f", $sub_total_productos_shopping_cart);
 $format_subtotal_taxes = sprintf ("%01.2f", $subtotal_taxes);
 $format_sub_total_day_charges = sprintf ("%01.2f", $sub_total_day_charges);
 $format_grand_total_shopping_cart = sprintf ("%01.2f", $grand_total_shopping_cart);
 echo "<tr align=\"right\"><td colspan=3 align=\"left\"><b>Order Sub Total : </b></td><td>\$".$format_sub_total_productos_shopping_cart."</td><td></td></tr>\n";
 echo "<tr align=\"right\"><td colspan=3 align=\"left\"><b>Subtotal Taxes : </b></td><td>\$".$format_subtotal_taxes."</td><td></td></tr>\n";
 echo "<tr align=\"right\"><td colspan=3 align=\"left\"><b>Shipping Charges : </b></td><td>\$".$format_sub_total_day_charges."</td><td></td></tr>\n";
 echo "<tr align=\"right\"><td colspan=3 align=\"left\"><b>Order Grand Total : </b></td><td>\$<b>".$format_grand_total_shopping_cart."</b></td><td></td></tr>\n";
 mysql_free_result ($result_consulta_shopping_cart);
 echo "</table></td></tr>\n";
 //echo "<form name=\"check_out\" target=_top method=\"post\" action=\"../../s/home/bill_ship_info.php\" OnSubmit=\"return validar(this)\">\n";
 echo "<form name=\"check_out\" target=_top method=\"post\" action=\"".SSL_HOST."bill_ship_info.php\" OnSubmit=\"return validar(this)\">\n";
// echo "<form name=\"check_out\" target=_top method=\"post\" action=\"../../s/home/client_data_invoice_nopublic.php\" OnSubmit=\"return validar(this)\">\n";

$script = <<< EOS
	<script language="JavaScript">

	function validar(forma) {
	   if ((forma.date_null.value == "1")||(forma.grand_total_shopping_cart.value == "0")) 
	   		{ window.open('missingfields.htm','TryAgain','width=300,height=240'); return false; }
	   if (dc > dcu)
	   		{ alert ('Please fill the designer choise space!!!'); return false; }
	}
	</script>
EOS;
echo "<tr align='center'>";
    
	   
	
	 // Mostrar los vendedores de la BD poms
	$pomsComm = mysql_connect($POMS_HOST, $POMS_USER, $POMS_PASS) or die("Error al conectarse a la DB");
	$pomsDB = mysql_select_db($POMS_DB) or die("Error al seleccionar la BD");
	$admqry="select cod_type from s_users where email='".$_SESSION['email']."'";
	$admin=mysql_query($admqry,$pomsComm) or die(mysql_error());
	$row_admin=mysql_fetch_row($admin);
	$query = "SELECT id_user, full_name from s_users ";
	$query .= "WHERE cod_type='ADMX'";
	$query .=" AND email='".$_SESSION['email']."'";
	if($row_admin[0]=='ADC'){
	$query = "SELECT id_user, full_name from s_users ";
	$query .= "WHERE cod_type='ADMX'";
	}
	$result = mysql_query($query);
	echo '<td colspan="3"><br><br>Salesperson: <select name="vendedor">';
	while($fila = mysql_fetch_array($result)) {
	  $id = $fila['id_user'];
	  $nombre = $fila['full_name'];
	  echo "<option value='$id'>$nombre</option>";
	}
 	 
echo " </select>  ";

 echo $script;
 echo "<input type=\"hidden\" name=\"session_id_real\" value=\"$session_id\">\n";
 echo "<input type=\"hidden\" name=\"grand_total_shopping_cart\" value=\"$grand_total_shopping_cart\">\n";
 echo "<input type=\"hidden\" name=\"date_null\" value=\"$date_null\">\n";
 echo "<input type=\"hidden\" name=\"phone\" value=\"1\">\n";
 echo "<input type=\"hidden\" name=\"flag\" value=3>\n";
 echo "<tr><td align=\"center\"><br><input type=\"button\" onclick=\"Empty()\" value=\"Empty Cart\">";
 echo "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input style=\"font-weight:bold;color:white;background:red\" type=\"submit\" name=\"check_out\" value=\"Check Out\">\n</form></td></tr>\n";
echo "<tr align='center'>
		<td ><img src='../../s/imgs/home.gif' border=0 alt='Go Back to Menu'><a href='/adminff2g/reports.php' class='combos'>Go Back to Menu</a></td>
	</tr>";
echo "<tr align='center'><td><a href='/adminff2g/index.php' class='combos'> X Logout</a></td></tr>";
 echo "</table>\n";
?>
</body>
</html>
  
