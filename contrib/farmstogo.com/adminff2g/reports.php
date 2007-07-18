<?php
error_reporting(E_ALL ^ E_NOTICE);
require_once("../utilidades/sesiones.php");
require_once("morders/standing_process.php"); 	
require_once("../functions.php");
validarSesion();

?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<head>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<title>FF2GO Administrator</title>

	<link type="text/css" rel="stylesheet" media="all" href="css/report.css" />

	<script type="text/javascript" src="css/report.js"></script>

<?php

if ($_SESSION['typeUser']==""){

?>

		<script language="Javascript">

 			 window.location="http://www.farmstogo.com/adminff2g/login.php";

		</script>

<?

}

?>

<style>

 BODY,input, select

{

    COLOR: #000000;

    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;

    FONT-SIZE: 12px

}



a {
	cursor: hand;
}
</style>

</head>



<body>

<table border="0" align="center" cellpadding="0" cellspacing="0">

  <tr>

    <td><img src="http://www.farmstogo.com/s/imgs/11.gif" border="0"></td>

  </tr>

  <tr>

    <td>&nbsp;</td>

  </tr>

  <tr>

    <td><span class="textopeque">Hello, <? echo $_SESSION['name']; ?></span></td>

  </tr>

  <tr>

    <td>&nbsp;</td>

  </tr>

  <tr>

    <td>

	<ul class="folding">

          <li>Order Manager</li>

          <ul>

		  	<li><a href="torders/take_orders.php">Take Phone Orders</a></li>

       		<li><a href="torders/take_orders_nopublic.php">Take Florist Phone Orders</a> </li>

			<li><a href="torders/take_ordersso.php">Take Standing Orders</a> </li>

	        <li><a href="morders/report_st_orders.php">Standing Orders Reports</a> </li>

	        <li> <a href="morders/sales_report.php">Sales Reports</a></li>

	        <li><a href="morders/main_report.php">General Search</a></li>
	        <li><a href="morders/main_company_report.php">Company Search Reports</a></li>

          </ul>

        <li>Payment Manager</li>

          <ul>

       		 <li><a href="morders/control_report.php?status=2">Invoice Pending</a> </li>

       		 <li><a href="morders/control_report.php?status=11">Rejected</a></li>

       		 <li><a href="morders/control_report.php?status=4">Cancelled</a></li>

   	      </ul>

        <li>Shipping Manager</li>

      	  <ul>

    	    <li><a href="morders/control_report.php?status=10">Ready to Ship</a> </li>

    	    <li><a href="morders/control_report.php?status=8">Awaiting Delivery</a> </li>

    	    <li><a href="morders/control_report.php?status=14">Delayed Orders</a></li>

       		<li><a href="morders/control_report.php?status=3">Delivered</a> </li>

   	      </ul>

        <li> Credit Manager </li>

	   <ul>

       	 <li>    <a href="morders/credit_report.php?status=6">Quality Claims</a></li>

         <li>    <a href="morders/credit_report.php?status=5">FedEx Claims</a> </li>

       </ul>

        <li>Events Manager </li>

        <ul>

		<li><a href="events/panel_events.php">Received Events</a></li>

		<li><a href="events/panel_events.php?replayed=1">Answered Events</a></li>

		

		</ul>

		<li>Reports </li>

        <ul>

		<li><a href="morders/sales_record2.php">Invoicing Report</a></li>
		</ul>

		
		<?php
		if($_SESSION["email"]=="miguels@flowerdealers.com" || $_SESSION["email"]=="compraflor@flowerdealers.com") {
		?>
		<li><a href="sadmin/reports.php">System Administration (Products, Markets, Images, etc)</a></li>
		<?php
		}
		?>
        <li><a href="sadmin/users.php">User Administration</a></li>
		<li><a href="sadmin/sterms.php">Search Terms Administration</a></li>
        <li><a href="sadmin/customers.php">Customers Administration (Retailers And Florist)</a></li>

        <li><a href="http://www.farmstogo.com/adminff2g/login.php">Logout</a></li>

 </td>

  </tr>

</table>     

</body>

</html>
<?php Standing_orders_process($database) ?>
