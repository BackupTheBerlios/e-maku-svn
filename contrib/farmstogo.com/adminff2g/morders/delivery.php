<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("Orders List / Waiting Delivery");
?>
<SCRIPT language=JavaScript src="./date.js"></SCRIPT>
<!-- CONTENIDO -->
<center>

<script>
	function DelO(oid) {
		if (confirm("Do you really wnt to delete the order [No. "+oid+"]")) {
			OpenW('./del_order.php?oid='+oid,'350','400','yes');
		}
	}	
	
	function Mark_all() {
		var i;
		for(i=0; i<document.list.elements.length; i++) {
			document.list.elements[i].checked=1;
		}
	}	
	</script>


<table bgcolor=cecece border =0 cellpadding=3 cellspacing=1>
  <form name=fecha>
  <tr bgcolor=ffffff>
    <td>Delivery Since<br>
      <input type="text" name="f1" size=10 value="<?echo $f1?>">
<A href="javascript:show_calendar('fecha.f1');"><IMG
    src="../images/calendario.gif"
    align=center border=0></A>    
    </td>
    <td>Delivery To<br>
      <input type="text" size=10 name="f2"  value="<?echo $f2?>">
<A href="javascript:show_calendar('fecha.f2');"><IMG
    src="../images/calendario.gif"
    align=center border=0></A>      
    </td>
    <td valign=top>Keyword<br>
      <input type="text" name="kw" size=20>
    </td>
    
    <td rowspan=2><input type=submit value="Go!"></td>
  </tr>
  </form>
</table>
<p>

<?

###################################################
	$reg=100; # Numero de registros a desplegar
	if (!$p) {$p=1;}
	$l1 = ($p-1)*$reg;
	$limit = " limit $l1,$reg ";
###################################################


$f = "";
if($f1) { $f = " AND i.ARRIVAL_DATE >= UNIX_TIMESTAMP('$f1 00:00:00') "; }
if($f2) { $f .= " AND i.ARRIVAL_DATE <= UNIX_TIMESTAMP('$f2 23:59:59') "; }
//if(!$f) { $f .= " AND i.ARRIVAL_DATE >= UNIX_TIMESTAMP(NOW()-86400)"; }

if (!$status) { $st = " AND o.ID_ORDER_STATUS=8";}
else if ($status=='ALL') { $st =""; }
else if($status) { $st = " AND o.ID_ORDER_STATUS='$status'";}
if($kw) {$skw = " AND (o.ID_FACTURA LIKE '%$kw%' OR o.SHIPPING_NAME LIKE '%$kw%' OR o.SHIPPING_LAST_NAME LIKE '%$kw%' OR o.SHIPPING_ADDRESS like '%$kw%' OR o.SHIPPING_ZIPCODE like '%$kw%') ";}

$query = <<< EOM
	SELECT DISTINCT o.ID_FACTURA, UNIX_TIMESTAMP(o.DATE_TIME) as dt, o.SHIPPING_NAME , o.SHIPPING_LAST_NAME , 
	o.SHIPPING_ADDRESS , o.SHIPPING_ZIPCODE , o.SHIPPING_PHONE , o.SHIPPING_CITY , 
	s.code , c.COUNTRY_CODE,  o.GRAND_TOTAL , o.COMMENTS, x.ORDER_STATUS , x.ID_ORDER_STATUS,
	MIN(i.ARRIVAL_DATE) as rd FROM FACTURAS o, ITEMS_FACTURA i, COUNTRIES c, STATES s,
	ORDERS_STATUS x WHERE c.ID_COUNTRY=o.SHIPPING_ID_COUNTRY AND s.ID_STATE=o.SHIPPING_ID_STATE
	AND o.ID_ORDER_STATUS=x.ID_ORDER_STATUS	AND o.ID_FACTURA=i.ID_FACTURA $f $st $skw 
	GROUP BY o.ID_FACTURA, o.DATE_TIME, o.SHIPPING_NAME , o.SHIPPING_LAST_NAME , 
	o.SHIPPING_ADDRESS , o.SHIPPING_ZIPCODE , o.SHIPPING_PHONE , o.SHIPPING_CITY , 
	s.code , c.COUNTRY_CODE,  o.GRAND_TOTAL , o.COMMENTS, x.ORDER_STATUS , x.ID_ORDER_STATUS
	ORDER BY ID_FACTURA DESC
EOM;

//echo $query;
$rs = mysql_db_query($database,$query.$limit);
echo mysql_error();
$c = mysql_db_query($database,$query);
$dx = mysql_numrows($c);


if (mysql_numrows($rs)) {
echo "<b>$dx</b> records found  <p>"

?>


	
<table cellpadding=3 cellspacing=1 border=1 bgcolor=cecece>

<tr bgcolor=ccccaa><td align=center colspan=9>
	<b><form method=POST>
	For listed orders 
	<select name="op">
	  <option value="">Select One Operation</option>
	  <option value="venmaster_file.php">Generate VenMaster</option>
	  <option value="fedex_web_file.php">Generate Fedex File</option>
	</select> 
	<input type=button value="Submit" onclick="if (this.form.op.options[this.form.op.selectedIndex].value!='') { OpenW(this.form.op.options[this.form.op.selectedIndex].value+'?f1=<?echo $f1?>&f2=<?echo $f2?>&status=8&kw=<?echo $kw?>','600','400',1)}">
</td>
	</form>
</tr>

  <tr bgcolor=cecece>
  <form name=list action="main_report2.php">
	<th width=10>&nbsp;</th> 	
    <th>Order ID</td>
    <th>Client/Shipping to</td>
    <th>Delivery</td>
    <th>Amount</td>
    <th>Status</td>
    <th width=10>Delete</th>
	<th width=10><a href="javascript:Mark_all()">ALL</a></th> 
  <? while($dt = mysql_fetch_array($rs)) { 

	 $bg = "bgcolor=#ffffff";
	 if ((++$i % 2)==0) { $bg = "bgcolor=#ffffaa"; }
	 
	 $amount = number_format($dt["GRAND_TOTAL"],2,".",",");
  ?>
  <tr <?echo $bg?>> 
    <td align=right><?echo ++$d?></td>
    <td align=right valign=top><b>
    <a href="javascript:OpenW('http://flowerfarmstogo.com/adminff2g/morders/order_info.php?ido=<?echo $dt["ID_FACTURA"]?>','680','600','yes')">No. <?echo $dt["ID_FACTURA"]?></a>
    </b><br><font size=1><?echo date("d/m/Y",$dt["dt"])?></td>
    <td valign=top><b><font color=brown><?echo $dt["SHIPPING_NAME"]." ".$dt["SHIPPING_LAST_NAME"]?></font></b>
    <br><?echo $dt["SHIPPING_ADDRESS"]." ".$dt["SHIPPING_CITY"].", ".$dt["SHIPPING_ZIPCODE"].", ".$dt["COUNTRY_CODE"]."<br> <b>Phone:</b> ".$dt["SHIPPING_PHONE"]?></td>
	<td align=center valign=top><?echo date("d/m/Y",$dt["rd"])?></td>
	<td align=right  valign=top><b>$<?echo $amount?></td>  
    <td width=10 valign=top><b>
    <?if($dt["ID_ORDER_STATUS"]==4) { echo "<font color=red>"; } 
      else if($dt["ID_ORDER_STATUS"]==2) { echo "<font color=green>"; } 
      else if($dt["ID_ORDER_STATUS"]==3) { echo "<font color=brown>"; } 
   echo $dt["ORDER_STATUS"]?></b></td>
    
    <td align=center>
   <!a href="javascript:DelO('<?echo $dt["ID_FACTURA"]?>')"><img border=0 src="images/del.gif"></a>
    </td>
    
	<td align=center>
	<? if($dt["ID_ORDER_STATUS"]==8) { ?>
		<input type=checkbox name=idl[] value="<?echo $dt["ID_FACTURA"]?>">
	<? } ?>
	</td>

</tr>
 <? $total += $dt["GRAND_TOTAL"]; } ?> 
 <tr><td colspan=4 align=center><b><big>TOTAL</td>
 <td><b><big><?echo number_format($total,2,",",".")?></td>
 <td colspan=4 align=right>
 <input type=submit value="Delivered" style="font-weight:bold"></td></tr>
</table>
<input type=hidden name=sts value="3">
<input type=hidden name=url value="delivery.php">

</form>
<p>

<?
$pages = $dx/$reg;
if ($pages > 10) { $pages = 10; }

if (($pages) > 1) {
 echo "<br>Pages : ";

 for ($i=0; $i<$pages; $i++)
 { 
  if ($p<>($i+1)) { ?>
	<b><a href="main_report.php?f1=<?echo $f1?>&f2=<?echo $f2?>&status=<?echo $status?>&kw=<?echo $kw?>&p=<?echo ($i+1)?>"><?echo ($i+1)?></a>&nbsp;</b>
<? } 
 else { echo "<b>".($i+1)."</b>&nbsp;"; }
  }
 }
 ?>


<? } else { echo "<p><br> No record found <p><br>"; } ?>

<?include('../end_admin.php');?>

