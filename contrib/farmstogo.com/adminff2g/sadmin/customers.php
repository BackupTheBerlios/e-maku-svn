<?php require ('../../functions.php'); 

include('../head_admin.php');

Titulo_Maestro("CUSTOMERS MAIN");

 ?>

<?php



if(isset($_POST['disable'])){

 mysql_select_db($db, $shopping_db_link);

  foreach($_POST['disable'] as $key=>$value){

    if($value==1){

    mysql_query("UPDATE CLIENTS SET STATUS=1 WHERE ID_CLIENT=$key ", $shopping_db_link) or die(mysql_error());
    mysql_query("UPDATE CLIENTS SET confirmed=0 WHERE ID_CLIENT=$key ", $shopping_db_link) or die(mysql_error());

	}

  }



}
if(isset($_POST['enable'])){

 mysql_select_db($db, $shopping_db_link);

$subject = "Your FarmsTogo on line account has been activated ";

$headers = "From: FarmsToGo.com <orders@farmstogo.com> \r\n" .
       "MIME-Version: 1.0\r\n" .
       "Content-Type: text/html; charset=utf-8\r\n" .
       "Content-Transfer-Encoding: 8bit\r\n\r\n";

// Send
  foreach($_POST['enable'] as $key=>$value){

    if($value==1){
    $query_mail="SELECT CLIENT_NAME,CLIENT_EMAIL FROM CLIENTS WHERE ID_CLIENT=$key AND emailed=0 LIMIT 1 ";
	$mail=mysql_query($query_mail,$shopping_db_link) or die (mysql_error());
	$row_mail=mysql_fetch_assoc($mail);
	if(mysql_num_rows($mail)){
    mysql_query("UPDATE CLIENTS SET confirmed=1,emailed=1 WHERE ID_CLIENT=$key ", $shopping_db_link) or die(mysql_error());
	$message ="Dear ".$row_mail['CLIENT_NAME'].",<br />
Thank you for registering with Farms To Go .com. You can now start ordering on line and taking advantage of our resale prices and specials.
Please go to www.farmstogo.com <http://www.farmstogo.com/>   login now and start enjoying a world of freshness great prices and quality service!<br />
Kind Regards <br />
<br />
Miguel Saavedra<br /> 
CEO Farmstogo.com";

    mail('it@flowerdealers.com, miguels@flowerdealers.com,'.$row_mail['CLIENT_EMAIL'], $subject, $message, $headers);
    }
	}

  }



}


if (!function_exists("GetSQLValueString")) {

function GetSQLValueString($theValue, $theType, $theDefinedValue = "", $theNotDefinedValue = "") 

{

  $theValue = get_magic_quotes_gpc() ? stripslashes($theValue) : $theValue;



  $theValue = function_exists("mysql_real_escape_string") ? mysql_real_escape_string($theValue) : mysql_escape_string($theValue);



  switch ($theType) {

    case "text":

      $theValue = ($theValue != "") ? "'" . $theValue . "'" : "NULL";

      break;    

    case "long":

    case "int":

      $theValue = ($theValue != "") ? intval($theValue) : "NULL";

      break;

    case "double":

      $theValue = ($theValue != "") ? "'" . doubleval($theValue) . "'" : "NULL";

      break;

    case "date":

      $theValue = ($theValue != "") ? "'" . $theValue . "'" : "NULL";

      break;

    case "defined":

      $theValue = ($theValue != "") ? $theDefinedValue : $theNotDefinedValue;

      break;

  }

  return $theValue;

}

}



$currentPage = $_SERVER["PHP_SELF"];



$maxRows_customers = 20;

$pageNum_customers = 0;

if (isset($_GET['pageNum_customers'])) {

  $pageNum_customers = $_GET['pageNum_customers'];

}

$startRow_customers = $pageNum_customers * $maxRows_customers;

$where="";

if(isset($_POST['search']) && $_POST['search']!=""){
	$search=$_POST['search'];
	$where=" and (`COMPANY` like '%$search%' or `CLIENT_NAME` like '%$search%' or `CLIENT_LAST_NAME`like '%$search%' or `CLIENT_ADDRESS` like '%$search%' or `CLIENT_ZIPCODE` like '%$search%' or `CLIENT_EMAIL` like '%$search%' or `CLIENT_CITY` like '%$search%') ";
}

if(isset($_POST['status']) && $_POST['status']!='' ) { 
	$status = " and confirmed=".$_POST['status'];
} else {
	$status = "";
}


mysql_select_db($db, $shopping_db_link);

$query_customers = "SELECT * FROM CLIENTS WHERE STATUS IS NULL AND PASSWORD <> '' $where $status order by REG_DATE desc, CLIENT_NAME ASC , CLIENT_LAST_NAME ASC ";

$query_limit_customers = sprintf("%s LIMIT %d, %d", $query_customers, $startRow_customers, $maxRows_customers);

$customers = mysql_query($query_limit_customers,$shopping_db_link) or die(mysql_error().$query_limit_customers);

$row_customers = mysql_fetch_assoc($customers);



if (isset($_GET['totalRows_customers'])) {

  $totalRows_customers = $_GET['totalRows_customers'];

} else {

  $all_customers = mysql_query($query_customers);

  $totalRows_customers = mysql_num_rows($all_customers);

}

$totalPages_customers = ceil($totalRows_customers/$maxRows_customers)-1;



$queryString_customers = "";

if (!empty($_SERVER['QUERY_STRING'])) {

  $params = explode("&", $_SERVER['QUERY_STRING']);

  $newParams = array();

  foreach ($params as $param) {

    if (stristr($param, "pageNum_customers") == false && 

        stristr($param, "totalRows_customers") == false) {

      array_push($newParams, $param);

    }

  }

  if (count($newParams) != 0) {

    $queryString_customers = "&" . htmlentities(implode("&", $newParams));

  }

}

$queryString_customers = sprintf("&totalRows_customers=%d%s", $totalRows_customers, $queryString_customers);

?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<head>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<title>Customers</title>

<style type="text/css">

<!--

.style1 {color: #ffffff}

-->

</style>

</head>



<body>

<table border="0" align="center" cellpadding="0" cellspacing="0">

  <tr>

    <td width="1318" align="center"><a href="../reports.php">

<p>Go Back to Main Menu </a></td>

  </tr>

  <tr>

    <td>&nbsp;

      <table border="0" align="center">

        <tr>

          <td align="center"><?php if ($pageNum_customers > 0) { // Show if not first page ?>

                <a href="<?php printf("%s?pageNum_customers=%d%s", $currentPage, 0, $queryString_customers); ?>">&lt;&lt;First</a>

          <?php } // Show if not first page ?>          </td>

          <td align="center"><?php if ($pageNum_customers > 0) { // Show if not first page ?>

              <a href="<?php printf("%s?pageNum_customers=%d%s", $currentPage, max(0, $pageNum_customers - 1), $queryString_customers); ?>">&lt;Previous</a>

          <?php } // Show if not first page ?>          </td>

          <td align="center"><?php if ($pageNum_customers < $totalPages_customers) { // Show if not last page ?>

                <a href="<?php printf("%s?pageNum_customers=%d%s", $currentPage, min($totalPages_customers, $pageNum_customers + 1), $queryString_customers); ?>">Next&gt;</a>

          <?php } // Show if not last page ?>          </td>

          <td align="center"><?php if ($pageNum_customers < $totalPages_customers) { // Show if not last page ?>

                <a href="<?php printf("%s?pageNum_customers=%d%s", $currentPage, $totalPages_customers, $queryString_customers); ?>">Last&gt;&gt;</a>

          <?php } // Show if not last page ?>          </td>
         <form id="form1" name="form1" method="post" action="customers.php"> <td align="center">Search</td>

           <td align="center">By Keyword<br />
            <input name="search" type="text" id="search" value=""/></td>

           <td align="center">Status<br />
             <select name="status" id="status">
             <option value="">Select One</option>
             <option value="1">Active</option>
             <option value="0">Inactive</option>
           </select>
           </td>
           <td align="center"><input type="submit" name="Submit" value="Go" /></td>
	      </form>
        </tr>
      </table>

      <form id="form2" name="form2" method="post" action="customers.php">

        <table border="0" align="center" cellpadding="2" cellspacing="1">

          <tr bgcolor="#669933">

            <th><span class="style1">Name</span></th>

            <th><span class="style1">Company</span></th>

            <th><span class="style1">Phone</span></th>

            <th><span class="style1">Email</span></th>

            <th><span class="style1">Retailer Number </span></th>

            <th ><span class="style1">Last Visit</span></th>
            <th><span class="style1">Florist</span></th>

            <th><span class="style1">Registered</span></th>

            <th><span class="style1">Enable</span></th>
            <th><span class="style1">Disable</span></th>
          </tr>

          <?php 

		$class="mixed";

		do { 

		$class=($class=="mixed")?"":"mixed";

		?>

          <tr class="<?php echo $class; ?>">

            <td><?php echo $row_customers['CLIENT_NAME']." ".$row_customers['CLIENT_LAST_NAME']; ?></td>

            <td><?php echo $row_customers['COMPANY']; ?></td>

            <td><?php echo $row_customers['CLIENT_PHONE']; ?></td>

            <td><?php echo $row_customers['CLIENT_EMAIL']; ?></td>

            <td><?php echo $row_customers['RETAILER_NUMBER']; ?></td>

            <td><?php echo $row_customers['LAST_VISIT']; ?></td>
            <td align="center"><?php if($row_customers['RETAILER_NUMBER']){ ?>

                <img src="../../images/selected.gif" alt="Florist" />

              <?php } ?></td>

            <td align="center" bgcolor="#eeeeee"><?php if($row_customers['PASSWORD']){ ?>

                <img src="../../images/selected.gif" alt="Registered" />

              <?php } ?></td>

            <td align="center"><input <?php if (!(strcmp($row_customers['confirmed'],1))) {echo "checked=\"checked\"";} ?> name="enable[<?php echo $row_customers['ID_CLIENT']; ?>]" type="checkbox" id="enable[<?php echo $row_customers['ID_CLIENT']; ?>]" value="1" /></td>
            <td align="center"><input name="disable[<?php echo $row_customers['ID_CLIENT']; ?>]" type="checkbox" id="disable[<?php echo $row_customers['ID_CLIENT']; ?>]" value="1" /></td>
          </tr>

          <?php } while ($row_customers = mysql_fetch_assoc($customers)); ?>
        </table>

      </form>

    </td>

  </tr>

  <tr>

    <td align="right"><input type="submit" name="Submit2" value="Process" onclick="document.form2.submit();" /></td>

  </tr>

  <tr>

    <td>&nbsp;</td>

  </tr>

</table>

</body>

<?include('../end_admin.php'); ?>

</html>



<?php

mysql_free_result($customers);

?>

