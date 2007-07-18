<?php
$id_user='NULL';
if(isset($_GET['id_user'])){
$id_user=$_GET['id_user'];
}
require_once('../../functions.php');
include('../head_admin.php');
mysql_select_db($database,$shopping_db_link) or die (mysql_error());
$query_users="SELECT * FROM USERLOGIN WHERE ID_USER=$id_user";
$users=mysql_query($query_users,$shopping_db_link) or die (mysql_error());
$row_users=mysql_fetch_assoc($users);
if(isset($_POST['update'])){
 $update="REPLACE INTO USERLOGIN SET REG_DATE='".date('Y-m-d')."',EMAIL='".$_POST['EMAIL']."', PASSWD=MD5('".$_POST['PASSWD']."'),  FULL_NAME='".$_POST['FULL_NAME']."', COMPANY='".$_POST['COMPANY']."', TYPE_USER=".$_POST['TYPE_USER'].",ID_USER=$id_user";
  if($id_user!='NULL'){
   $update="REPLACE INTO USERLOGIN SET REG_DATE='".date('Y-m-d')."',EMAIL='".$_POST['EMAIL']."', FULL_NAME='".$_POST['FULL_NAME']."', COMPANY='".$_POST['COMPANY']."', TYPE_USER=".$_POST['TYPE_USER'].",ID_USER=$id_user";
  }	
 mysql_query($update,$shopping_db_link) or die (mysql_error()." query : $update");
	echo "<script>";
	echo "self.location='users.php';";
	echo "</script>";
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Untitled Document</title>
</head>
<body>
<form method="post" name="form1" id="form1">
<table align="center" class="border">
  <tr>
    <th colspan="2" bgcolor="#669933">Users Mintenance </th>
  </tr>
  <tr>
    <td>Email</td>
    <td><input name="EMAIL" type="text" id="EMAIL" value="<?php echo $row_users['EMAIL']; ?>"/></td>
  </tr>
<?php if($id_user=='NULL'){ ?>
  <tr>
    <td>Password</td>
    <td><input name="PASSWD" type="password" id="PASSWD" value="<?php echo $row_users['PASSWD']; ?>"/></td>
  </tr>
<?php } ?>
  <tr>
    <td>Full Name </td>
    <td><input name="FULL_NAME" type="text" id="FULL_NAME" value="<?php echo $row_users['FULL_NAME']; ?>" size="35"/></td>
  </tr>
  <tr>
    <td>Company</td>
    <td><input name="COMPANY" type="text" id="COMPANY" value="<?php echo $row_users['COMPANY']; ?>" size="35"/></td>
  </tr>
  <tr>
    <td>Type</td>
    <td><select name="TYPE_USER" id="TYPE_USER">
      <option value="NULL" <?php if (!(strcmp("NULL", $row_users['TYPE_USER']))) {echo "selected=\"selected\"";} ?>>Select</option>
      <option value="1" <?php if (!(strcmp(1, $row_users['TYPE_USER']))) {echo "selected=\"selected\"";} ?>>Administrator</option>
      <option value="2" <?php if (!(strcmp(2, $row_users['TYPE_USER']))) {echo "selected=\"selected\"";} ?>>Sales</option>
      <option value="3" <?php if (!(strcmp(3, $row_users['TYPE_USER']))) {echo "selected=\"selected\"";} ?>>Shiping</option>
      <option value="4" <?php if (!(strcmp(4, $row_users['TYPE_USER']))) {echo "selected=\"selected\"";} ?>>Procurement</option>
      <option value="5" <?php if (!(strcmp(5, $row_users['TYPE_USER']))) {echo "selected=\"selected\"";} ?>>Packing</option>
      <option value="6" <?php if (!(strcmp(6, $row_users['TYPE_USER']))) {echo "selected=\"selected\"";} ?>>Invoicing</option>
    </select></td>
  </tr>
  <tr>
    <td colspan="2"><input type="submit" value="Ok" />
      <input name="Button" type="button" value="Cancel" onclick="self.location='users.php';"/></td>
    </tr>
</table><input name="update" type="hidden" id="update" value="form1" />
</form>
</body>
</html>
