<?php 
require ('../../functions.php'); 

$additwh="";
$kw=$ddate=$ddate2=$fechas="";
$status=0;
$arr_stat=" WHERE 0";
//Cambiamos el status
if(isset($_POST['MM_Update'])){
mysql_select_db($db,$shopping_db_link);
$new_status=$_POST['new_status'];
foreach($_POST['change'] as $id_factura => $valor){
 mysql_query("UPDATE FACTURAS SET ID_ORDER_STATUS=$new_status WHERE ID_FACTURA=$id_factura ",$shopping_db_link) or die(mysql_error());
}
header("Location:control_report.php?status=".$_POST['pass_status']);
}

// Revisamos el status que deseamos mostrar
if(isset($_GET['status'])){
$status=$_GET['status'];
//$additwh .=" AND o.ID_ORDER_STATUS=$status ";
}
if(isset($_POST['status'])){
$status=$_POST['status'];
//$additwh .=" AND o.ID_ORDER_STATUS=$status ";
}

//Mostramos las opciones para cambiar status
	if($status==11){$arr_stat=" WHERE ID_ORDER_STATUS IN(4)";}
	if($status==2){$arr_stat=" WHERE ID_ORDER_STATUS IN(10,11)";}
	if($status==11){$arr_stat=" WHERE ID_ORDER_STATUS IN(4)";}
	if($status==10){$arr_stat=" WHERE ID_ORDER_STATUS IN(8,14)";}
	if($status==8){$arr_stat=" WHERE ID_ORDER_STATUS IN(3,14)";}


// Filtramos por Fechas
if(isset($_POST['ddate'])&& $_POST['ddate']!="") {
$ddate=$_POST['ddate'];
$additwh .= " AND i.ARRIVAL_DATE >= UNIX_TIMESTAMP('$ddate 00:00:00') ";
 }
if(isset($_POST['ddate2']) && $_POST['ddate2']!="") {
$ddate2=$_POST['ddate2'];
$additwh .= " AND i.ARRIVAL_DATE <= UNIX_TIMESTAMP('$ddate2 23:59:59') "; }

if(isset($_POST['kw']) && $_POST['kw']!=""){
//Filtramos por Keyword
$kw=$_POST['kw'];
$additwh .=" AND (o.ID_FACTURA LIKE '%$kw%' OR o.SHIPPING_NAME LIKE '%$kw%' OR o.SHIPPING_LAST_NAME LIKE '%$kw%' OR o.SHIPPING_ADDRESS like '%$kw%' OR o.SHIPPING_ZIPCODE like '%$kw%') ";
}


mysql_select_db($db,$shopping_db_link);
$query_report="SELECT DISTINCT o.ID_FACTURA, UNIX_TIMESTAMP(o.DATE_TIME) as dt, o.SHIPPING_NAME , o.SHIPPING_LAST_NAME , 
	o.SHIPPING_ADDRESS , o.SHIPPING_ZIPCODE , o.SHIPPING_PHONE , o.SHIPPING_CITY , 
	s.code , c.COUNTRY_CODE,  o.GRAND_TOTAL , o.COMMENTS, x.ORDER_STATUS , x.ID_ORDER_STATUS,
	MIN(i.ARRIVAL_DATE) as rd, o.DATE_TIME, o.bankmessage  FROM FACTURAS o, ITEMS_FACTURA i, COUNTRIES c, STATES s,
	ORDERS_STATUS x WHERE c.ID_COUNTRY=o.SHIPPING_ID_COUNTRY AND s.ID_STATE=o.SHIPPING_ID_STATE
	AND o.ID_ORDER_STATUS=x.ID_ORDER_STATUS	AND o.ID_FACTURA=i.ID_FACTURA AND o.ID_ORDER_STATUS=2
	GROUP BY o.ID_FACTURA, o.DATE_TIME, o.SHIPPING_NAME , o.SHIPPING_LAST_NAME , 
	o.SHIPPING_ADDRESS , o.SHIPPING_ZIPCODE , o.SHIPPING_PHONE , o.SHIPPING_CITY , 
	s.code , c.COUNTRY_CODE,  o.GRAND_TOTAL , o.COMMENTS, x.ORDER_STATUS , x.ID_ORDER_STATUS
	ORDER BY ID_FACTURA DESC ";
if($status!=0){
$query_report="SELECT DISTINCT o.ID_FACTURA, UNIX_TIMESTAMP(o.DATE_TIME) as dt, o.SHIPPING_NAME , o.SHIPPING_LAST_NAME , 
	o.SHIPPING_ADDRESS , o.SHIPPING_ZIPCODE , o.SHIPPING_PHONE , o.SHIPPING_CITY , 
	s.code , c.COUNTRY_CODE,  o.GRAND_TOTAL , o.COMMENTS, x.ORDER_STATUS , x.ID_ORDER_STATUS,
	MIN(i.ARRIVAL_DATE) as rd, o.DATE_TIME, o.bankmessage  FROM FACTURAS o, ITEMS_FACTURA i, COUNTRIES c, STATES s,
	ORDERS_STATUS x WHERE c.ID_COUNTRY=o.SHIPPING_ID_COUNTRY AND s.ID_STATE=o.SHIPPING_ID_STATE
	AND o.ID_ORDER_STATUS=x.ID_ORDER_STATUS	AND o.ID_FACTURA=i.ID_FACTURA AND o.ID_ORDER_STATUS=$status $additwh
	GROUP BY o.ID_FACTURA, o.DATE_TIME, o.SHIPPING_NAME , o.SHIPPING_LAST_NAME , 
	o.SHIPPING_ADDRESS , o.SHIPPING_ZIPCODE , o.SHIPPING_PHONE , o.SHIPPING_CITY , 
	s.code , c.COUNTRY_CODE,  o.GRAND_TOTAL , o.COMMENTS, x.ORDER_STATUS , x.ID_ORDER_STATUS
	ORDER BY ID_FACTURA DESC ";
	} 
$report=mysql_query($query_report,$shopping_db_link) or die(mysql_error());
$row_report=mysql_fetch_assoc($report);
$totalRows=mysql_num_rows($report);
$total=0;

$query_title="SELECT ORDER_STATUS FROM ORDERS_STATUS WHERE ID_ORDER_STATUS=$status";
$title=mysql_query($query_title,$shopping_db_link) or die(mysql_error());
$row_title=mysql_fetch_assoc($title);
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>FF2G Administrator</title>
<style>
BODY,input, select
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 11px
}
.table, .table th, .table td {border:1px solid #cecece; border-collapse:collapse; padding-left:2px; padding-right:2px;}
.table th { background:#ccccaa; }
.mixedyl { background:#FFFFCC}
</style>
<SCRIPT language=JavaScript src="./date.js"></SCRIPT>
<script>
function Mark_all(){
		var i;
		for (i=0;i<document.form1.elements.length;i++){
      	if(document.form1.elements[i].type == "checkbox"){
        	 if(document.form1.elements[i].checked){
		 	document.form1.elements[i].checked=0;
		 	} else {
		 	document.form1.elements[i].checked=1
			}
		}
	  } 
	}
function valida(){
	if(document.form1.new_status.value==""){
	 alert("You must select an status to proceed"); 
	 document.form1.new_status.focus();
	 }else{
	 document.form1.submit();
	 }
}	

</script>
</head>
<body>
  <table border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td align="center"><img src="http://www.flowerfarmstogo.com/s/imgs/11.gif" border="0" /></td>
    </tr>
    <tr>
      <td align="center"><h4>
        <?php  echo $row_title["ORDER_STATUS"]; ?>
      </h4></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><form id="form2" name="form2" method="post" action="<?php echo $PHP_SELF; ?>">
        <table border="0" align="center" cellpadding="0" cellspacing="0" class="table">
          <tr>
            <th colspan="8">Search Bar 
            <input name="status" type="hidden" id="status" value="<?php echo $status; ?>" /></th>
          </tr>
          <tr>
            <td rowspan="2" align="right">Delivery Date            </td>
            <td align="right">From:</td>
            <td><input name="ddate" type="text" id="ddate" value="<?php echo $ddate; ?>" size="10" readonly="true" /></td>
            <td><A href="javascript:show_calendar('form2.ddate');"><IMG
    src="../images/calendario.gif"
    align=center border=0></A> </td>
            <td>Keyword</td>
            <td><input name="kw" type="text" id="kw" value="<?php echo $kw; ?>" /></td>
            <td colspan="2" rowspan="2" align="center" valign="middle"><input type="submit" name="Submit2" value="Search" />
            <br/><input name="reset" type="reset" value="Reset.." /></td>
          </tr>
          <tr>
            <td align="right">To:</td>
            <td><input name="ddate2" type="text" id="ddate2" value="<?php echo $ddate2; ?>" size="10" readonly="true" /></td>
            <td><A href="javascript:show_calendar('form2.ddate2');"><IMG
    src="../images/calendario.gif"
    align=center border=0></A> </td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
        </table>
            </form>	  </td>
    </tr>
    <tr>
      <td>
	  <form id="form1" name="form1" method="post" action="<? echo $PHP_SELF."?status=$status"; ?>">
	  <table border="0" align="center" cellpadding="1" cellspacing="0" class="table">
		<?php if($totalRows){ ?>
        <tr>
          <td colspan="2"><div align="left"><a href="../reports.php">Go Back to Main Menu</a></div></td>
          <td>For Listed Orders </td>
          <td colspan="3"><select name="new_status" id="new_status">
              <option>Select Status</option>
  			<?php SelecTable($database,'ORDERS_STATUS','ID_ORDER_STATUS',"ORDER_STATUS"," $arr_stat ","") ?> 						
            </select>          </td>
        </tr>
        <tr>
          <th>Order Id </th>
          <th>Client/Shipping to </th>
          <th>Delivery</th>
          <th>Ammount</th>
          <th>Status</th>
          <th><a href="javascript:Mark_all()" >All</a></th>
        </tr>
        <?php	$class="";
	 do{
	 $total=$total+$row_report["GRAND_TOTAL"];
	$class=($class=="")?"mixedyl":"";
	 ?>
        <tr valign="middle" class="<?php echo $class; ?>">
          <td align="center"><b><a onclick="window.open('./order_info.php?ido=<? echo $row_report["ID_FACTURA"]; ?>','orderinfo','scrollbars=yes width=600,height=680');" href="#"> No. <?php echo $row_report['ID_FACTURA']; ?></a></b><br />
              <?php echo date("d/m/Y",$row_report['dt']); ?></td>
          <td><font color="brown"><b><? echo $row_report["SHIPPING_NAME"]." ".$row_report["SHIPPING_LAST_NAME"]?></b></font><br/>
              <? echo $row_report["SHIPPING_ADDRESS"]." ".$row_report["SHIPPING_CITY"].", ".$row_report["SHIPPING_ZIPCODE"].", ".$row_report["COUNTRY_CODE"]; ?><br/>
              <?php if($row_report["SHIPPING_PHONE"]!=""){ echo "<b>Phone:</b> ".$row_report["SHIPPING_PHONE"];}?>          </td>
          <td align="center"><? echo date("d/m/Y",$row_report["rd"]); ?></td>
          <td align="right">$<?php echo number_format($row_report["GRAND_TOTAL"],2,".",","); ?></td>
          <td align="center"><b>
            <? if($row_report["ID_ORDER_STATUS"]==4) { echo "<font color=red>"; } 
      else if($row_report["ID_ORDER_STATUS"]==2) { echo "<font color=green>"; } 
      else if($row_report["ID_ORDER_STATUS"]==3) { echo "<font color=brown>"; } 
   echo $row_report["ORDER_STATUS"] ?><br />
   <?php if($row_report["bankmessage"]){
    echo "<font color=brown >".$row_report["bankmessage"]."</font>";
   }?>
          </b>		  </td>
          <td><input name="change[<?php echo $row_report['ID_FACTURA'] ?>]" type="checkbox" id="change[<?php echo $row_report['ID_FACTURA'] ?>]" value="1" /></td>
        </tr>
        <?php } while($row_report=mysql_fetch_assoc($report));?>
        <tr valign="middle" class="<?php echo $class; ?>">
          <th colspan="3" align="center"><div align="right">Total</div></th>
          <th align="right"><input name="pass_status" type="hidden" id="pass_status" value="<? echo $status; ?>" />
            <input name="MM_Update" type="hidden" id="MM_Update" value="form1" />
            <?php echo "$".number_format($total,2,".",","); ?></th>
          <th colspan="2" align="center"><input type="button" name="Button" onclick="valida();" value="Process &gt;&gt;" /></th>
        </tr><?php } else { ?>
        <tr valign="middle" class="<?php echo $class; ?>">
          <th colspan="6" align="center">Sorry, no information available for this feature. </th>
          </tr>
		<?php } ?>
		</table>  
	  </form></td>
    </tr>
  </table>
s
<p align="center"><a href="../reports.php">Go Back to Main Menu</a></p>
</body>
</html>
<?php mysql_free_result($report);?>
