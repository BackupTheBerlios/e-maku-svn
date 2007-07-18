<?php
include('../../functions.php');
include('../head_admin.php');
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Untitled Document</title>

</head>

<body>
<table border="0" align="center" cellpadding="2" cellspacing="0">
  <tr>
    <td><img src="../../s/imgs/folder.gif" width="19" height="16"></td>
    <td colspan="2">PAYMENT</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><img src="../../s/imgs/folder_open.gif" width="19" height="17"></td>
    <td><a href="control_report.php?status=2">Invoice Pending</a> </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><img src="../../s/imgs/folder_open.gif" alt="Avs" width="19" height="17"></td>
    <td><a href="control_report.php?status=12">Declined AVS (Address Verification Service)</a> </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><img src="../../s/imgs/folder_open.gif" alt="Avs" width="19" height="17" /></td>
    <td><a href="control_report.php?status=13">Declined CVN (Card Verification Number)</a> </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><img src="../../s/imgs/folder_open.gif" alt="Avs" width="19" height="17" /></td>
    <td><a href="control_report.php?status=11">Rejected</a></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><img src="../../s/imgs/folder_open.gif" alt="Avs" width="19" height="17" /></td>
    <td><a href="#">Ready To Ship</a> </td>
  </tr>
  <tr>
    <td><img src="../../s/imgs/folder.gif" alt="Option" width="19" height="16" /></td>
    <td colspan="2"><a href="./report_st_orders.php">&copy; Standing Orders </a></td>
  </tr>
  <tr>
    <td><img src="../../s/imgs/folder.gif" alt="Option" width="19" height="16" /></td>
    <td colspan="2"><a href="#">Advanced Search </a></td>
  </tr>
  <tr>
    <td><img src="../../s/imgs/home.gif" alt="Home" width="16" height="16" /></td>
    <td colspan="2"><a href="../reports.php">Go Back To Men&uacute;</a> </td>
  </tr>
</table>
</body>
</html>
