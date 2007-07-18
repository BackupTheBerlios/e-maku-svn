<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<?php
include('../../functions.php');
include('../head_admin.php');

$qx = "select * from DELIVERYINFORMATION";
$rs = mysql_db_query($database,$qx.$limit);
echo mysql_error();
$rsc = mysql_db_query($database,$qx);
while ($d=mysql_fetch_array($rs))
	{
	 $information=$d["INFORMATION"];
}
if ($flag==1){
	
	$s_update = "";
	$s_update = "UPDATE DELIVERYINFORMATION SET INFORMATION='$delivery' WHERE ID_INFORMATION=1";
	mysql_db_query($db,$s_update);	
	?>
		<script>
			alert ("your information has been saved");
		</script>
		<script>
			window.location.href='delivery_information.php';
		</script>
	<?
}
?>
<title>Flower Farms To Go .com Delivery Information</title>
</head>
<body>

<form action="delivery_information.php" method="post">
<table width="780" border="0" cellspacing="0" cellpadding="0" name="contenidos">
        <tr>
          <td>
            <table width="400" border="0" cellspacing="0" cellpadding="0" align="center">
            	<tr>
                	<td align="center" class="titulogrisprincipal">Edit Delivery Information</td>
			  	</tr>
				 <tr>
                	<td align="center"><textarea name="delivery" cols="80" rows="10" class="combos"><? echo $information; ?></textarea></td>
			  	</tr>
				<tr>
					<td><input type="submit" value="Update">
					    <input type="hidden" name="flag" value="1">
					</tr>
					<tr>
                		<td align="center" class="titulogris"><a href="/adminff2g/reports.php" class="titulogris">Go Back to Menu</a></td>
			  		</tr>
			</table>
			
		   </td>
         <tr>
</table>
</form>         
</body>
</html>
