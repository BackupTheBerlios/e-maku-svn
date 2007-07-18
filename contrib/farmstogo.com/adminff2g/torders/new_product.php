<?session_start();
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("New Product");


if ($desc) {

	#################################### DELIVERY DAY
	
	if ($f1) { 
			$dtmp = mysql_db_query($database,"select UNIX_TIMESTAMP('$f1') FROM PRODUCTOS limit 1");
			echo mysql_error();			$dtmp = mysql_fetch_row($dtmp);
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
	    insert into SHOPPING_CART (SESSION_ID,PRODUCT_DESC,ID_PRODUCTO,DAY_CHARGE,ID_QUANTITY,DELIVERY_DATE,PRICE,TIME)
	             values ('$OID','$desc ($size)',999,$day_charge,$qty,$dd,$price,NOW())
EOI;
	//echo $insert_shopping_cart;
	# INSERTAMOS EL PRODUCTO EN EL SHOPPING CART

	$result_insert_shopping_cart = mysql_db_query($database,$insert_shopping_cart,$shopping_db_link);	echo mysql_error();
	
?>
	<script>
		window.location.href='take_orders.php';
	</script>
<?

}
?>
<SCRIPT language=JavaScript src="./date.js"></SCRIPT>

<center>

<script>
	function Validar() {
		var today = '<?echo date("Y-m-d")?>';
		with (document.px) {
			if (desc.value=='' || price.value=='' || qty.value=='' || size.selectedIndex==0 || (dd.selectedIndex==0 && f1.value=='')) {
				alert('All data are required !!!');
				return false;
			} else if (today > f1.value && f1.value != '') { 
				alert ('Delivery date is wrong date'); return false;
			}
		}
	}
</script>

<form method=POST name=px  onsubmit="return Validar()">
<table border="0" cellpadding=3 cellspacing=1>
  <tr> 
    <td colspan="3"> Product Description<br> 
      <input type="text" name="desc" size="80">
    </td>
  </tr>
  <tr> 
    <td> Size<br>
        <select name="size">
			<option value=""></option>
			 <? SelecTable($database,'SIZES','SIZE',"SIZE","",$size) ?> 						        
        </select>
    </td>
    <td> Price<br>
        <input type="text" name="price" size="4">
    </td>
    <td> Qty Ordered<br>
        <input type="text" name="qty" size="4" maxlength="3">
    </td>
  </tr>
  <tr> 
    <td colspan="2"> Delivery<br>
    <select name="dd">
		<option value="">Select an Arrival Date</option>
		
<?
              $date_option_counter = 0;
    $date_counter = 0;
    $daytime = 86400;    $p_delay = 2;
    $date_delay=0;
    $market_date_end_year = date("Y",$market_date_end);    $market_date_start_year = date("Y",$market_date_start);

    $market_start_day_timestamp = $market_date_start;
    $market_end_day_timestamp = $market_date_end;

    $now  = time();
    $today  = mktime (0,0,0,date("m"),date("d"),date("Y"));    if ($p_delay) { $today += $p_delay*86400; } 
    $today_day = date("w",$today);
    $producto_delay = 0;          

              while (($date_counter < 30)&&($date_option_counter < 50)) {
                  $date_option_day = $today + $daytime*($date_option_counter);
                  $date_option_day_now = $now + $daytime*($date_option_counter);
                     $current_day = date("w",$date_option_day);
                     if ($current_day == "6") $day_charge= " add $15 per Box";
                     if ($current_day > 1) {                       if (($date_option_day_now >= $delivery_date) && strcmp(date("m-d",$date_option_day_now),"01-01") && strcmp(date("m-d",$date_option_day_now),"01-02")) {                 
                         $day_desc = date("M. j (l",$date_option_day).$day_charge.")";
                         if ($date_option_day == $delivery_date_shopping_cart) $date_selected = "selected ";
                         echo "<option ".$date_selected."value=".$date_option_day.">".$day_desc."</option>\n";
                         $date_counter++;
                          }
                     }
                  $date_option_counter++;
                  $date_selected="";
                  $day_charge="";
               }

?>				
      </select>
<br>
Or       
<A href="javascript:show_calendar('px.f1');">
<IMG    src="../images/calendario.gif"
    align=center border=0></A>   
    <input type="text" name="f1" size=10 value="<?echo $f1?>">
      
      
    </td>
    <td>
      <input type="submit" name="Submit" value="Send &gt;&gt;">
    </td>
  </tr>
</table>
</form>