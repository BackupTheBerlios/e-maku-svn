<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("Company Search Report");
?>
<SCRIPT language=JavaScript src="./date.js"></SCRIPT>
<!-- CONTENIDO -->
<center>

<script>
<!--
	function DelO(oid) {
		if (confirm("Do you really want to delete the order [No. "+oid+"]")) {
			OpenW('./del_order.php?oid='+oid,'350','400','yes');
		}
	}	
	
	function Mark_all() {
		var i;
		for(i=0; i<document.list.elements.length; i++) {
			document.list.elements[i].checked=1;
		}
	}
-->		
</script>


<table bgcolor=cecece border =0 cellpadding=3 cellspacing=1>
	<form name=fecha>
		<tr bgcolor=ffffff>
			<td>
				Order Day Since<br>
				<input type="text" name="f3" size=10 value="<?echo $f3?>">
				<A href="javascript:show_calendar('fecha.f3');"><IMG src="../images/calendario.gif" align=center border=0></A>    
    		</td>
    		<td>
				Order Day To<br>
      			<input type="text" size=10 name="f4"  value="<?echo $f4?>">
				<A href="javascript:show_calendar('fecha.f4');"><IMG src="../images/calendario.gif" align=center border=0></A>      
			</td>
			<td>
				Delivery Since<br>
				<input type="text" name="f1" size=10 value="<?echo $f1?>">
				<A href="javascript:show_calendar('fecha.f1');"><IMG src="../images/calendario.gif" align=center border=0></A>    
			</td>
			<td>
				Delivery To<br>
				<input type="text" size=10 name="f2"  value="<?echo $f2?>">
				<A href="javascript:show_calendar('fecha.f2');"><IMG src="../images/calendario.gif" align=center border=0></A>      
			</td>
			<?php
			$sql_companyname = "
					SELECT		DISTINCT C.COMPANY as COMPANY_NAME
					FROM		CLIENTS C
					WHERE		C.COMPANY <> ''
					ORDER BY	COMPANY_NAME ASC
						";
			mysql_select_db($db,$shopping_db_link);
			$result_companyname = mysql_query($sql_companyname,$shopping_db_link) or die(mysql_error());				
			?> 
			<td valign=top>Company<br>
				<select name=status>
					<option value="ALL">ALL</option>
					<? 
					while ($company = mysql_fetch_array($result_companyname)) {
					?>
						<option value="<?=$company["COMPANY_NAME"]?>"
						<?php
						if ($status == $company["COMPANY_NAME"]) {
							echo " SELECTED";
						}	
						?>
						><?=$company["COMPANY_NAME"]?></option>
						<?php
						}
			 //SelecTable($database,'ORDERS_STATUS','ID_ORDER_STATUS',"ORDER_STATUS","",$status) 
			?> 						
		</select>
    </td>
   <!--td valign=top>Salesperson<br>
	<?php // Mostrar los vendedores de la BD poms
	$pomsComm = mysql_connect($POMS_HOST, $POMS_USER, $POMS_PASS) or die("Error al conectarse a la DB");
	$pomsDB = mysql_select_db($POMS_DB) or die("Error al seleccionar la BD");
	$query = "SELECT id_user, full_name from s_users ";
	$query .= "WHERE cod_type='ADMX'";
$result = mysql_query($query);
echo '<select name="vendedor">';
echo "<option value='NN'></option>";
while($fila = mysql_fetch_array($result)) {
  $id = $fila['id_user'];
  $nombre = $fila['full_name'];
  echo "<option value='$id'>$nombre</option>";
}
echo "</select>";
?>
</tr-->
	
    </td>
    
    <td valign=top>Keyword<br>
      <input type="text" name="kw" size=20 value="<?=$kw?>">
    </td>
    
    <td rowspan=2><input type=submit value="Go!"></td>
  </tr>
  <form>
</table>
<p>

<?

###################################################
	$reg=100; # Numero de registros a desplegar
	if (!$p) {$p=1;}
	$l1 = ($p-1)*$reg;
	$limit = " limit $l1,$reg ";
###################################################

$idVendedor = $_GET['vendedor'];
$f = "";
if($f1) { $f = " AND i.ARRIVAL_DATE >= UNIX_TIMESTAMP('$f1 00:00:00') "; }
if($f2) { $f .= " AND i.ARRIVAL_DATE <= UNIX_TIMESTAMP('$f2 23:59:59') "; }
//if(!$f) { $f .= " AND i.ARRIVAL_DATE >= UNIX_TIMESTAMP(NOW()-86400)"; }
// Si existe fecha pedido mayor que
$solicite = "";
if($f3) {
  $f .= " AND o.DATE_TIME >= '$f3' ";
}
// Si existe fecha de pedido menor que
if($f4) {
  $f .= " AND o.DATE_TIME <= '$f4' ";
}
if (!$status) { $st = " ";}
else if ($status=='ALL') { $st =""; }
else if($status) { $st = " AND C.COMPANY LIKE '%$status%'";}
if($kw) {$skw = " AND (o.ID_FACTURA LIKE '%$kw%' OR o.SHIPPING_NAME LIKE '%$kw%' OR o.SHIPPING_LAST_NAME LIKE '%$kw%' OR o.SHIPPING_ADDRESS like '%$kw%' OR o.SHIPPING_ZIPCODE like '%$kw%' OR C.COMPANY LIKE '%$kw%') ";}
// busqueda por vendedor
if($idVendedor != "NN" && $idVendedor != "") {
  $campoFacVen = ", factura_vendedor.id_vendedor";
  $tablaFacVen = ", factura_vendedor";
  $qvend = "factura_vendedor.id_vendedor = '$idVendedor' AND factura_vendedor.id_factura = o.ID_FACTURA AND ";
}

$query = "
	SELECT		DISTINCT o.ID_FACTURA
				, UNIX_TIMESTAMP(o.DATE_TIME) as dt
				, o.SHIPPING_NAME 
				, o.SHIPPING_LAST_NAME 
				, o.SHIPPING_ADDRESS 
				, o.SHIPPING_ZIPCODE 
				, o.SHIPPING_PHONE 
				, o.SHIPPING_CITY 
				, s.code 
				, c.COUNTRY_CODE
				, o.GRAND_TOTAL 
				, o.COMMENTS
				, x.ORDER_STATUS 
				, x.ID_ORDER_STATUS
				, MIN(i.ARRIVAL_DATE) as rd
				, o.DATE_TIME $campoFacVen 
	FROM		FACTURAS o
				, ITEMS_FACTURA i
				, COUNTRIES c
				, STATES s
				, ORDERS_STATUS x $tablaFacVen 
				, CLIENTS C
	WHERE		$qvend c.ID_COUNTRY=o.SHIPPING_ID_COUNTRY 
				AND s.ID_STATE=o.SHIPPING_ID_STATE
				AND o.ID_ORDER_STATUS=x.ID_ORDER_STATUS	
				AND o.ID_FACTURA=i.ID_FACTURA $f $st $skw 
				AND C.ID_CLIENT=o.ID_CLIENT
	GROUP BY 	o.ID_FACTURA
				, o.DATE_TIME
				, o.SHIPPING_NAME 
				, o.SHIPPING_LAST_NAME 
				, o.SHIPPING_ADDRESS 
				, o.SHIPPING_ZIPCODE 
				, o.SHIPPING_PHONE 
				, o.SHIPPING_CITY 
				, s.code 
				, c.COUNTRY_CODE
				, o.GRAND_TOTAL 
				, o.COMMENTS
				, x.ORDER_STATUS 
				, x.ID_ORDER_STATUS
	ORDER BY 	ID_FACTURA DESC
		";

$rs = mysql_db_query($database,$query.$limit);
echo mysql_error();
//echo $query;
$c = mysql_db_query($database,$query);
$dx = mysql_numrows($c);


if (mysql_numrows($rs)) {
echo "<b>$dx</b> records found<p>"

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
	<input type=button value="Submit" onclick="if (this.form.op.options[this.form.op.selectedIndex].value!='') { OpenW(this.form.op.options[this.form.op.selectedIndex].value+'?f1=<?echo $f1?>&f2=<?echo $f2?>&status=<?echo $status?>&kw=<?echo $kw?>','600','400',1)}">
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
  <? while($dt = mysql_fetch_array($rs)) { 

	 $bg = "bgcolor=#ffffff";
	 if ((++$i % 2)==0) { $bg = "bgcolor=#ffffaa"; }
	 
	 $amount = number_format($dt["GRAND_TOTAL"],2,".",",");
  ?>
  <tr <?echo $bg?>> 
    <td align=right><?echo ++$d?></td>
    <td align=right valign=top><b>
    <a href="javascript:OpenW('./order_info.php?ido=<?echo $dt["ID_FACTURA"]?>','680','600','yes')">No. <?echo $dt["ID_FACTURA"]?></a>
    </b><br><font size=1><?echo date("d/m/Y",$dt["dt"])?></td>
    <td valign=top><b><font color=brown><?echo $dt["SHIPPING_NAME"]." ".$dt["SHIPPING_LAST_NAME"]?></font></b>
    <br><?echo $dt["SHIPPING_ADDRESS"]." ".$dt["SHIPPING_CITY"].", ".$dt["SHIPPING_ZIPCODE"].", ".$dt["COUNTRY_CODE"]."<br> <b>Phone:</b> ".Dphone($dt["SHIPPING_PHONE"])?></td>
	<td align=center valign=top><?echo date("d/m/Y",$dt["rd"])?></td>
	<td align=right  valign=top><b>$<?echo $amount?></td>  
    <td width=10 valign=top><b>
    <?if($dt["ID_ORDER_STATUS"]==4) { echo "<font color=red>"; } 
      else if($dt["ID_ORDER_STATUS"]==2) { echo "<font color=green>"; } 
      else if($dt["ID_ORDER_STATUS"]==3) { echo "<font color=brown>"; } 
   echo $dt["ORDER_STATUS"]?></b></td>
    
    

</tr>
 <? $total += $dt["GRAND_TOTAL"]; } ?> 
 <tr><td colspan=4 align=center><b><big>TOTAL</td>
 <td><b><big><?echo number_format($total,2,",",".")?></td>
 <td colspan=4 align=right><input type=submit value="Delivered" style="font-weight:bold"></td></tr>
</table>

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
<table>
	<tr>
		<td><a href="#" onClick="javascript:window.open('venmaster_file.php','ee','width=400,height= 300')">>Export Excel</a></td>
	</tr>
</table>
<p align="center"><a href="../reports.php">Go Back to Main Menu</a></p>
</body>
</html>