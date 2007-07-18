<?
session_start();
require ('../../functions.php'); 
include('../head_admin.php');


if (!session_is_registered("OID")) {
	session_register("OID");
	$OID = date("YmdHis");
	$S_SHIP = "";
}

//echo $OID;

Titulo_Maestro("Take Phone Orders");
$today  = mktime (0,0,0,date("m"),date("d"),date("Y"));

?>
<SCRIPT language=JavaScript src="./date.js"></SCRIPT>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<script>
	function Validar() {
		var today = '<?echo date("Y-m-d")?>';
		with (document.as) {
			if (qty.value=='' || it.selectedIndex==0 || (dd.selectedIndex==0 && f1.value=='')) {
				alert('All fields are required !!!');
			} else if (today > f1.value && f1.value != '') { 
				alert ('Delivery date is wrong date');
			}
			else { 
				action='add_to_sc_nopublic.php';
				submit(); }
		}
	}
</script>

<body bgcolor="#FFFFFF"><center>

<table cellpadding=3 cellspacing=1 border="0" bgcolor=cecece>
 <!-- <tr> 
    <th colspan="3">PRODUCTO/ITEM</td>
    <td>&nbsp;</td>
  </tr> -->
  <form method=POST name=as>
  <tr bgcolor=ffff77> 
    <td colspan="3"> 
      <select name="pr" onchange="this.form.submit()">
		   <option value="">CHOSE A PRODUCT</option>	
           <?            
	$qx = "select DISTINCT T.TIPO_PRODUCTO,S.ID_SUB_TIPO_PRODUCTO,S.SUB_TIPO_PRODUCTO from ".
	"TIPO_PRODUCTOS T,SUB_TIPO_PRODUCTOS S, PRODUCTOS P, MARKETS M WHERE S.ID_TIPO_PRODUCTO=T.ID_TIPO_PRODUCTO ".
	"AND UNIX_TIMESTAMP(M.ACTIVATE_DATE) <= $today   AND ". 
	"UNIX_TIMESTAMP(M.DESACTIVATE_DATE) >= $today  AND M.ID_MARKET <> 2 AND M.PUBLIC =0 AND ".
	" M.ID_MARKET=P.ID_MARKET AND S.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO ".
	"order by TIPO_PRODUCTO, SUB_TIPO_PRODUCTO";
	
	$rs = mysql_db_query($database,$qx);
	echo mysql_error();
	while($dt = mysql_fetch_array($rs)) { 
	$sel = "";
	if (!strcmp($dt["ID_SUB_TIPO_PRODUCTO"],$pr)) { $sel = " selected"; } 
		?>
		<option value="<?echo $dt["ID_SUB_TIPO_PRODUCTO"]?>" <?echo $sel?>> <?echo strtoupper($dt["TIPO_PRODUCTO"])." / ".$dt["SUB_TIPO_PRODUCTO"]?> </option> 
		<? 
	}                     
           ?>   
               
      </select>
      
      <?if ($pr) {?>
      </td></tr><tr><td colspan=3>
      <select name="it">
		   <option value="">CHOSE MARKET - SIZE</option>	
           <? 
           
	$qx = "select DISTINCT M.MARKET,S.SIZE,P.ID_PRODUCTO,P.PRODUCTO,P.BUNCHES_BOX,P.PRICE_BUNCH*P.BUNCHES_BOX AS PRICE from MARKETS M, SIZES S, ".
	"PRODUCTOS P WHERE P.ID_SUB_TIPO_PRODUCTO=$pr AND S.ID_SIZE=P.ID_SIZE AND ".
	"UNIX_TIMESTAMP(M.ACTIVATE_DATE) <= $today   AND ". 
	"UNIX_TIMESTAMP(M.DESACTIVATE_DATE) >= $today  AND M.ID_MARKET <> 2  AND M.PUBLIC =0 AND ".
	"M.ID_MARKET=P.ID_MARKET order by MARKET, SIZE";
	
	$rs = mysql_db_query($database,$qx);
	echo mysql_error();
	//echo $qx;
	while($dt = mysql_fetch_array($rs)) { 
	$sel = "";
	if (!strcmp($dt["ID_PRODUCTO"],$it)) { $sel = " selected"; } 
		//strtoupper($dt["MARKET"])." -
		?>
		<option value="<?echo $dt["ID_PRODUCTO"]?>" <?echo $sel?>> <?echo strtoupper($dt["MARKET"])." - ".strtoupper($dt["SIZE"]).", ".$dt["PRODUCTO"]." [".$dt["BUNCHES_BOX"]." Bchs, $".$dt["PRICE"]."]";?> </option> 
		<?	}                     
           ?>       
      
      </select>
      
    
    </td>
  </tr>

  <tr bgcolor=ffff77> 
    <td> 
    <b>QTY:</b> 	<input type=text name=qty size=2 maxlength=2>
	&nbsp;&nbsp;
	</td><td>
      <select name="dd">
		<option value="">Select an Arrival Date</option>
		
<?
          
    $date_option_counter = 0;
    $date_counter = 0;
    $daytime = 86400;
    $p_delay = 2;
    $date_delay=0;
    $market_date_end_year = date("Y",$market_date_end);
    $market_date_start_year = date("Y",$market_date_start);

    $market_start_day_timestamp = $market_date_start;
    $market_end_day_timestamp = $market_date_end;

    $now  = time();
    $today  = mktime (0,0,0,date("m"),date("d"),date("Y"));
    if ($p_delay) { $today += $p_delay*86400; } 
    $today_day = date("w",$today);
    $producto_delay = 0;          

              while (($date_counter < 30)&&($date_option_counter < 50)) {
                  $date_option_day = $today + $daytime*($date_option_counter);
                  $date_option_day_now = $now + $daytime*($date_option_counter);
                     $current_day = date("w",$date_option_day);
                     if ($current_day == "6") $day_charge= " add $15 per Box";
                     if ($current_day > 1) {
                       if (($date_option_day_now >= $delivery_date) && strcmp(date("m-d",$date_option_day_now),"01-01") && strcmp(date("m-d",$date_option_day_now),"01-02")) {                 
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
    		
      </select>&nbsp;&nbsp;
      Or 
<A href="javascript:show_calendar('as.f1');">
<IMG    src="../images/calendario.gif"
    align=center border=0></A>   
    <input type="text" name="f1" size=10 value="<?echo $f1?>">


      
      </td><td>
      <input type="button" value="Add to List" onclick="Validar()">
    
            <? }?>

    </td>
    

  </tr>
  </form>
</table>

<p>
    <!-- <a href="new_product.php"><b>New Product</b></a>
    &nbsp;&nbsp;<a target=oth href="../../price_list.php?pass=1"><b>View Price List</b></a>-->

<p>

<?include('./view_sc_nopublic.php');?>
<?//include('./end_admin.php');?>

