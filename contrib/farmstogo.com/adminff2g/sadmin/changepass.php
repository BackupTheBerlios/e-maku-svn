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
 $update="UPDATE USERLOGIN SET REG_DATE='".date('Y-m-d')."',PASSWD=MD5('".$_POST['PASSWD']."') WHERE ID_USER=$id_user";
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
    <th colspan="2" bgcolor="#669933">Change Password </th>
  </tr>
  <tr>
    <td>Full Name</td>
    <td><?php echo $row_users['FULL_NAME']; ?></td>
  </tr>
  <tr>
    <td>Email</td>
    <td><?php echo $row_users['EMAIL']; ?></td>
  </tr>
  <tr>
    <td>Password</td>
    <td><input name="PASSWD" type="password" id="PASSWD" value="<?php echo $row_users['PASSWD']; ?>"/></td>
  </tr>
  <tr>
    <td colspan="2"><input name="Submit" type="submit" value="Change" />
      <input name="Button" type="button" value="Cancel" onclick="self.location='users.php';"/></td>
    </tr>
</table>
<input name="update" type="hidden" id="update" value="form1" />
</form>
</body>
</html>
