<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("ADMIN SEARCH TERMS");
 mysql_select_db($db,$shopping_db_link) or die('Can not connect the database');
$query_sterms="SELECT `value` from s_parameters where `param_cod`='STERMS'";
$sterms=mysql_query($query_sterms,$shopping_db_link) or die (mysql_error());
$row_sterms=mysql_fetch_assoc($sterms);

if(isset($_POST['value'])){
 mysql_select_db($db,$shopping_db_link) or die('Can not connect the database');
mysql_query("UPDATE s_parameters SET `value`='".$_POST['value']."' where `param_cod`='STERMS'",$shopping_db_link) or die (mysql_error());
echo "<script>";
echo "self.location='sterms.php';";
echo "</script>";


}
     
?>

<form action="<?php echo $PHP_SELF; ?>" method="post" name="form1">
<table width="200" border="0" align="center" cellpadding="4" cellspacing="0">
  <tr>
    <td>This is the text of the terms in this momment : </td>
  </tr>
  <tr>
    <td>
      <textarea name="value" cols="60" id="value" ><?php echo $row_sterms["value"];?></textarea></td>
  </tr>
  <tr>
    <td><input type="submit" name="Submit" value="Update"></td>
  </tr>
</table>
</form>
<div align="center">

<?php include('../end_admin.php'); ?>
</div>