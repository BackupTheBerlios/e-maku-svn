<?php 
require ('../../functions.php'); 
$additwh="";
$kw=$ddate=$ddate2="";
$status=0;
if(isset($_POST['kw'])){
//Filtramos por Keyword
$kw=$_POST['kw'];
$additwh=" AND (o.ID_FACTURA LIKE '%$kw%' OR o.SHIPPING_NAME LIKE '%$kw%' OR o.SHIPPING_LAST_NAME LIKE '%$kw%' OR o.SHIPPING_ADDRESS like '%$kw%' OR o.SHIPPING_ZIPCODE like '%$kw%') ";
}

// Filtramos por Fechas
if(isset($_POST['ddate'])) {
$ddate=$_POST['ddate'];
 $additwh .= " AND i.ARRIVAL_DATE >= UNIX_TIMESTAMP('$ddate 00:00:00') ";
 }
if(isset($_POST['ddate2'])) {
$ddate2=$_POST['ddate2'];
$additwh .= " AND i.ARRIVAL_DATE <= UNIX_TIMESTAMP('$ddate2 23:59:59') "; }

// Revisamos el status que deseamos mostrar
if(isset($_GET['status'])){
$status=$_GET['status'];
$additwh .=" AND o.ID_ORDER_STATUS=$status ";
}
mysql_select_db($db,$shopping_db_link);
$query_report="SELECT DISTINCT o.ID_FACTURA, UNIX_TIMESTAMP(o.DATE_TIME) as dt, o.SHIPPING_NAME , o.SHIPPING_LAST_NAME , 
	o.SHIPPING_ADDRESS , o.SHIPPING_ZIPCODE , o.SHIPPING_PHONE , o.SHIPPING_CITY , 
	s.code , c.COUNTRY_CODE,  o.GRAND_TOTAL , o.COMMENTS, x.ORDER_STATUS , x.ID_ORDER_STATUS,
	MIN(i.ARRIVAL_DATE) as rd, o.DATE_TIME, o.bankmessage  FROM FACTURAS o, ITEMS_FACTURA i, COUNTRIES c, STATES s,
	ORDERS_STATUS x WHERE c.ID_COUNTRY=o.SHIPPING_ID_COUNTRY AND s.ID_STATE=o.SHIPPING_ID_STATE
	AND o.ID_ORDER_STATUS=x.ID_ORDER_STATUS	AND o.ID_FACTURA=i.ID_FACTURA $additwh
	GROUP BY o.ID_FACTURA, o.DATE_TIME, o.SHIPPING_NAME , o.SHIPPING_LAST_NAME , 
	o.SHIPPING_ADDRESS , o.SHIPPING_ZIPCODE , o.SHIPPING_PHONE , o.SHIPPING_CITY , 
	s.code , c.COUNTRY_CODE,  o.GRAND_TOTAL , o.COMMENTS, x.ORDER_STATUS , x.ID_ORDER_STATUS
	ORDER BY ID_FACTURA DESC ";
$report=mysql_query($query_report,$shopping_db_link) or die(mysql_error());
$row_report=mysql_fetch_assoc($report);
$totalRows=mysql_num_rows($report);
$total=0;
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title></title>
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
</head>
<?php 
Titulo_Maestro("Orders List");
?>
<body>
  <table border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td><form id="form2" name="form2" method="post" action="<?php echo $PHP_SELF; ?>">
        <table border="0" cellpadding="0" cellspacing="0" class="table">
          <tr>
            <th colspan="8">Search Bar </th>
          </tr>
          <tr>
            <td rowspan="2" align="right">Delivery Date </td>
            <td align="right">From:</td>
            <td><input name="ddate" type="text" id="ddate" value="<?php echo $ddate; ?>" size="10" readonly="true" /></td>
            <td><A href="javascript:show_calendar('form2.ddate');"><IMG
    src="../images/calendario.gif"
    align=center border=0></A> </td>
            <td>Keyword</td>
            <td><input name="kw" type="text" id="kw" value="<?php echo $kw; ?>" /></td>
            <td colspan="2" rowspan="2"><input type="submit" name="Submit2" value="Filter" /></td>
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
            </form>
	  </td>
    </tr>
    <tr>
      <td>
	  <form id="form1" name="form1" method="post" action="">
	  <table border="0" align="center" cellpadding="1" cellspacing="0" class="table">
		<?php if($totalRows){ ?>
        <tr>
          <td><div align="left"><a href="../reports.php">Go Back to Main Menu</a> : </div></td>
          <td>&nbsp;</td>
          <td>For Listed Orders </td>
          <td colspan="3"><select name="status" id="status">
              <option>Select Status</option>
  			<?php SelecTable($database,'ORDERS_STATUS','ID_ORDER_STATUS',"ORDER_STATUS","","") ?> 						
            </select>          </td>
        </tr>
        <tr>
          <th>Order Id </th>
          <th>Client/Shipping to </th>
          <th>Delivery</th>
          <th>Ammount</th>
          <th>Status</th>
          <th>All</th>
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
          <td><input type="checkbox" name="checkbox" value="checkbox" /></td>
        </tr>
        <?php } while($row_report=mysql_fetch_assoc($report));?>
        <tr valign="middle" class="<?php echo $class; ?>">
          <th colspan="3" align="center"><div align="right">Total</div></th>
          <th align="right"><?php echo "$".number_format($total,2,".",","); ?></th>
          <th colspan="2" align="center"><input type="submit" name="Submit" value="Process &gt;&gt;" /></th>
        </tr><?php } else { ?>
        <tr valign="middle" class="<?php echo $class; ?>">
          <th colspan="6" align="center">Sorry, no information available  </th>
          </tr>
		<?php } ?>
		</table>  
	  </form></td>
    </tr>
  </table>
</body>
</html>
<?php mysql_free_result($report);?>
