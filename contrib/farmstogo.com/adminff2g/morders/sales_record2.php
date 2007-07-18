<?php 

require ('../../functions.php');

$additwh="";

$ddate=$ddate2=$IdVendedor="";

$status=0;

$where="AND 0 ";

$datei=$datef="";

if(isset($_POST['datei']) && $_POST['datei']!="" ){

$datei=$_POST['datei'];

$where =" AND i.ARRIVAL_DATE >= UNIX_TIMESTAMP( '$datei 00:00:00' )"; 

}

if(isset($_POST['datef']) && $_POST['datef']!="" ){

$datef=$_POST['datef'];

if($where!="AND 0"){ 

$where .=" AND i.ARRIVAL_DATE <= UNIX_TIMESTAMP( '$datef 12:59:59' )"; 

} else { $where =" AND i.ARRIVAL_DATE <= UNIX_TIMESTAMP( '$datef 12:59:59' )"; }

}

$detail=$resume="";

if(isset($_POST['case'][0]) && $_POST['case'][0]==1){

$detail=1;

}else{

$resume=1;

}



mysql_select_db($db,$shopping_db_link);

$query_sperson="SELECT * FROM flowerde_pomsv.s_users where cod_type='ADMX' and status=1 ORDER BY full_name asc";

$sperson=mysql_query($query_sperson,$shopping_db_link) or die(mysql_error());

$row_sperson=mysql_fetch_assoc($sperson) or die(mysql_error()." $query_sperson ");

$gtotal=0;

?>



<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<head>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<title>Delivery </title>

<style>

body , input { 

	font-size:x-small;

	font-family:Arial, Helvetica, sans-serif;

	}

	.table, .table td, table th{ border-collapse:collapse;}

</style>

<SCRIPT language=JavaScript src="./date.js"></SCRIPT>

<SCRIPT language=JavaScript >

function validate(){

 if(document.form1.datei.value=="" && document.form1.datef.value==""){

   alert("Please select a range"); 

   document.form1.datei.focus();

 } else {

   document.form1.submit();

 }

}

</SCRIPT>

</head>

<body>

<table border="0" align="center" cellpadding="0" cellspacing="0">

  <tr>

      <td align="center" colspan="9"><img src="http://www.flowerfarmstogo.com/s/imgs/11.gif" border="0" /></td>

  </tr>

  <tr>

    <td colspan="9" align="center"><a href="../reports.php">Go Back To The Main Menu</a> </td>

  </tr>

  <tr>

    <td colspan="9"><h3>Sales Report  <a href="javascript:print();">Print</a> </h3></td>

  </tr>

  <tr>

    <td colspan="9">Generate Date : <?php echo date('r'); ?></td>

  </tr>

  <tr>

    <td colspan="9">&nbsp;</td>

  </tr>

 <form id="form1" name="form1" method="post" action="<?php echo $PHP_SELF; ?>">

  <tr>

    <td width="36"><div align="right">

    From : </div></td>

    <td width="66"><input name="datei" type="text" id="datei" onclick="javascript:show_calendar('form1.datei');" value="<?php echo $datei; ?>" size="10" readonly="true"  /></td>

    <td width="21" align="center"><A href="javascript:show_calendar('form1.datei');"><IMG

    src="../images/cal.gif" width="16" height="16" border=0

    align=center></A> </td>

    <td width="21"><div align="right">To : </div></td>

    <td width="66"><input name="datef" type="text" id="datei2" onclick="javascript:show_calendar('form1.datef');" value="<?php echo $datef; ?>" size="10" readonly="true" /></td>

    <td width="21" align="center"><A href="javascript:show_calendar('form1.datef');"><IMG

    src="../images/cal.gif" width="16" height="16" border=0

    align=center></A></td>

    <td width="22">&nbsp;</td>

    <td width="117"><p>

      <label>

        <input <?php if ($detail){echo "checked=\"checked\"";} ?> name="case" type="radio" value="1" />

        Detail</label>

      <label>

        <input <?php if ($resume){echo "checked=\"checked\"";} ?> type="radio" name="case" value="2" />

        Resume</label>

      <br />

    </p></td>

    <td width="301"><input type="button" name="Button" value="Go" onclick="validate();" /></td>

  </tr>

  </form>

  <tr>

    <td colspan="9">&nbsp;</td>

  </tr>

  <tr>

    <td colspan="9"><table border="0" cellpadding="2" cellspacing="1">

      <?php do{ 

	$salesperson=$row_sperson['id_user'];

	mysql_select_db($db,$shopping_db_link);

	$query_sales="SELECT DISTINCT o.ID_FACTURA, o.DATE_TIME AS DATE_TIME, CONCAT( o.SHIPPING_NAME, '', o.SHIPPING_LAST_NAME ) AS CLIENT_NAME, o.SHIPPING_ADDRESS, o.SHIPPING_ZIPCODE, o.SHIPPING_PHONE, o.SHIPPING_CITY, s.code, c.COUNTRY_CODE, o.GRAND_TOTAL, (

o.QC_CREDIT * -1

)QC_CREDIT, (

o.FEDEX_CREDIT * -1

)FEDEX_CREDIT, o.COMMENTS, x.ORDER_STATUS, x.ID_ORDER_STATUS, MIN( i.ARRIVAL_DATE ) AS ARRIVAL_DATE, o.bankmessage, s_users.full_name AS SALES_PERSON,x.ORDER_STATUS

FROM FACTURAS o, ITEMS_FACTURA i, COUNTRIES c, STATES s, ORDERS_STATUS x, factura_vendedor, flowerde_pomsv.s_users

WHERE factura_vendedor.id_factura = o.ID_FACTURA

AND c.ID_COUNTRY = o.SHIPPING_ID_COUNTRY

AND factura_vendedor.id_vendedor = flowerde_pomsv.s_users.id_user

AND s.ID_STATE = o.SHIPPING_ID_STATE

AND o.ID_ORDER_STATUS = x.ID_ORDER_STATUS

AND o.ID_FACTURA = i.ID_FACTURA

AND x.ID_ORDER_STATUS IN (1,2,10,8,3)

AND factura_vendedor.id_vendedor =$salesperson

$where

GROUP BY o.ID_FACTURA, o.DATE_TIME, o.SHIPPING_NAME, o.SHIPPING_LAST_NAME, o.SHIPPING_ADDRESS, o.SHIPPING_ZIPCODE, o.SHIPPING_PHONE, o.SHIPPING_CITY, s.code, c.COUNTRY_CODE, o.GRAND_TOTAL, o.COMMENTS, x.ORDER_STATUS, x.ID_ORDER_STATUS

ORDER BY ID_FACTURA DESC ";

$sales=mysql_query($query_sales,$shopping_db_link) or die (mysql_error()." Qruery : ".$query_sales);

$row_sales=mysql_fetch_assoc($sales);
//echo $query_sales;

if($row_sales){

?>

      <tr>

        <th align="left" colspan="7"><h3>Sales Person : <em><strong><?php echo $row_sperson['full_name']; ?></strong></em></h3></th>

      </tr>

      <tr bgcolor="#DDDDDD">

        <?php if($detail){  ?>

		<th>PO</th>

        <th>CUSTOMER</th>

        <th>STATE</th>

		<?php } else { ?>

		<th colspan="3">&nbsp;</th>

		<?php } ?>

        <th>VALUE</th>

        <th>QC CREDIT </th>

        <th>FEDEX CREDIT</th>

        <th>BALANCE</th>

		<th>STATUS</th>

      </tr>

      <?php $bgcolor="#FFFFFF";

		    $tvalue=$tqc_credit=$tfedex_credit=$tbalance=$tfl=0;

		    do{ 

			$bgcolor=($bgcolor=="#EEEEEE")?"#FFFFFF":"#EEEEEE";

			$tvalue+=$row_sales['GRAND_TOTAL'];

			$tqc_credit+=$row_sales['QC_CREDIT'];

			$fedex_credit+=$row_sales['FEDEX_CREDIT'];

			$tbalance+=($row_sales['GRAND_TOTAL']-($row_sales['QC_CREDIT']+$row_sales['FEDEX_CREDIT']));

			if($row_sales['code']=="FL"){

			$tfl+=$tbalance;

			}

		if($detail){?>

      <tr bgcolor="<?php echo $bgcolor; ?>">

        <td align="center"><?php echo $row_sales['ID_FACTURA']; ?></td>

        <td><?php echo $row_sales['CLIENT_NAME']; ?></td>

        <td align="center"><?php echo $row_sales['code']; ?></td>

        <td align="right"><?php echo number_format($row_sales['GRAND_TOTAL'],2); ?></td>

        <td align="right"><?php echo number_format($row_sales['QC_CREDIT'],2); ?></td>

        <td align="right"><?php echo number_format($row_sales['FEDEX_CREDIT'],2); ?></td>

        <td align="right"><?php echo number_format(($row_sales['GRAND_TOTAL']+$row_sales['QC_CREDIT']+$row_sales['FEDEX_CREDIT']),2); ?></td>

		<td align="left"><?php echo $row_sales['ORDER_STATUS']; ?></td>

      </tr>

      <?php

	  } 

	   } while($row_sales=mysql_fetch_assoc($sales)); 

		$gtotal+= $tbalance;

		  mysql_free_result($sales);

		    ?>

      <tr bgcolor="#DDDDDD">

        <th colspan="3" align="right"><strong>RUNNING TOTALS ---&gt;</strong></th>

        <th align="right"><strong><?php echo number_format($tvalue,2); ?></strong></th>

        <th align="right"><strong><?php echo number_format($tqc_credit,2); ?></strong></th>

        <th align="right"><strong><?php echo number_format($tfedex_credit,2); ?></strong></th>

        <th align="right" ><strong><?php echo number_format($tbalance,2); ?></strong></th>

		<th >&nbsp;</th>

      </tr>

      <tr bgcolor="#DDDDDD">

        <th colspan="8" align="right" bgcolor="#FFFFFF"><div align="center">-------------------------------------------------------------------------------------------------------------------------</div></th>

        </tr>

      <tr bgcolor="#DDDDDD">

        <th colspan="3" align="right">SUB TOTAL FLORIDA ---&gt; </th>

        <th align="right"><strong><?php echo number_format($tfl,2); ?></strong></th>

        <th align="right" bgcolor="#DDDDDD">SALES TAX  : </th>

        <th align="right" bgcolor="#DDDDDD"><?php echo number_format($tfl*0.07,2); ?></th>

        <th align="right" bgcolor="#FFFFFF" >&nbsp;</th>

        <th bgcolor="#FFFFFF" >&nbsp;</th>

      </tr>

      <tr bgcolor="#DDDDDD">

        <th colspan="3" align="right">SUB TOTAL OTHER STATES ---&gt; </th>

        <th align="right"><strong><?php echo number_format($tbalance-$tfl,2); ?></strong></th>

        <th align="right" bgcolor="#FFFFFF">&nbsp;</th>

        <th align="right" bgcolor="#FFFFFF">&nbsp;</th>

        <th align="right" bgcolor="#FFFFFF" >&nbsp;</th>

        <th bgcolor="#FFFFFF" >&nbsp;</th>

      </tr>

      <tr bgcolor="#DDDDDD">

        <th colspan="3" align="right">GRAND TOTAL ---&gt; </th>

        <th align="right"><strong><?php echo number_format($tbalance,2); ?></strong></th>

        <th align="right" bgcolor="#FFFFFF">&nbsp;</th>

        <th align="right" bgcolor="#FFFFFF">&nbsp;</th>

        <th align="right" bgcolor="#FFFFFF" >&nbsp;</th>

        <th bgcolor="#FFFFFF" >&nbsp;</th>

		</tr>

		

      

      <?php 

	}

	 } while($row_sperson=mysql_fetch_assoc($sperson)); ?>

    </table></td>

  </tr>

  

  <tr>

    <td colspan="9"><h3>Total Sales this Period : <?php echo number_format($gtotal,2); ?></h3></td>

  </tr>

</table>

</body>

</html>

