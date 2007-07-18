<?php
require_once('../../functions.php');
include('../head_admin.php');
mysql_select_db($database,$shopping_db_link) or die (mysql_error());
$query_users="SELECT * FROM USERLOGIN";
$users=mysql_query($query_users,$shopping_db_link) or die (mysql_error());
$row_users=mysql_fetch_assoc($users);
?>
<table border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <th>USERS ADMINISTRATION </th>
  </tr>
  <tr>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td><div align="center"><a href="edituser.php">Create New User</a> | <a href="../reports.php">Go Back to Main Menu </a></div></td>
  </tr>
  <tr>
    <td><table class="border">
      <tr bgcolor="#669933">
        <th bgcolor="#669933">Name</th>
        <th>E-mail</th>
        <th>Company</th>
        <th>Type</th>
        <th>Edit Info </th>
        <th>Change Pass </th>
        </tr>
	  <?php $color='';do {
	  $type="";
	  if($row_users['TYPE_USER']==1){
	  $type="Administrator";
	  }else if($row_users['TYPE_USER']==2){
    	  $type="Sales Man";
	  }else if($row_users['TYPE_USER']==3){$type="Shiping";
	  }else if($row_users['TYPE_USER']==4){$type="Procurement";
	  }else if($row_users['TYPE_USER']==5){$type="Packing"; 
	  }else if($row_users['TYPE_USER']==6){$type="Invoicing"; }
	  $color=($color=='')?'mixed':'';
	  ?>
      <tr class="<?php echo $color; ?>" onmouseover="this.className='over';" onmouseout="this.className='<?php echo $color; ?>';">
        <td><?php echo $row_users['FULL_NAME']; ?></td>
        <td><?php echo $row_users['EMAIL']; ?></td>
        <td><?php echo $row_users['COMPANY']; ?></td>
        <td align="center"><?php echo $type; ?></td>
        <td align="center"><a href="edituser.php?id_user=<?php echo $row_users['ID_USER']; ?>"><img src="../../images/edits.gif" alt="Edit" width="16px" height="16px" border="0" ></a></td>
        <td align="center"><a href="changepass.php?id_user=<?php echo $row_users['ID_USER']; ?>"><IMG src="../../images/keys.gif" alt="Change Password" border="0"></a></td>
        </tr><?php } while ($row_users=mysql_fetch_assoc($users));?>
    </table></td>
  </tr>
</table>
</body>
</html>