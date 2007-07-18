<?php 
require ('../../functions.php'); 
if(isset($_POST['INSERT'])){
mysql_select_db($db,$shopping_db_link);
$query="INSERT INTO TIPO_PRODUCTOS SET TIPO_PRODUCTO='".$_POST['TIPO_PRODUCTO']."', `ORDER`='".$_POST['ORDER']."'";
mysql_query($query,$shopping_db_link)or die (mysql_error());
}
if(isset($_POST['ID_TIPO_PRODUCTO'])){
mysql_select_db($db,$shopping_db_link);
foreach ($_POST['ID_TIPO_PRODUCTO'] as $key=>$value){
 if(isset($_POST['TIPO_PRODUCTO'][$key])){
 $query="UPDATE TIPO_PRODUCTOS SET TIPO_PRODUCTO='".$_POST['TIPO_PRODUCTO'][$key]."' where ID_TIPO_PRODUCTO=$key";
mysql_query($query,$shopping_db_link)or die (mysql_error());
 }
  if(isset($_POST['ORDER'][$key])){
 $query="UPDATE TIPO_PRODUCTOS SET `ORDER`='".$_POST['ORDER'][$key]."' where ID_TIPO_PRODUCTO=$key";
mysql_query($query,$shopping_db_link)or die (mysql_error());
 }
  if(isset($_POST['DELETE'][$key]) && $_POST['DELETE'][$key]==1){
 $query="DELETE FROM TIPO_PRODUCTOS  where ID_TIPO_PRODUCTO=$key";
mysql_query($query,$shopping_db_link)or die (mysql_error());
 }
   if(isset($_POST['STATUS'][$key]) && $_POST['STATUS'][$key]==1){
 $query="UPDATE TIPO_PRODUCTOS SET STATUS= 1 where ID_TIPO_PRODUCTO=$key";
mysql_query($query,$shopping_db_link)or die (mysql_error());
 }else {
  $query="UPDATE TIPO_PRODUCTOS SET STATUS= 0 where ID_TIPO_PRODUCTO=$key";
mysql_query($query,$shopping_db_link)or die (mysql_error());
 }

}
}

mysql_select_db($db,$shopping_db_link);
$query_tproduct="select * from TIPO_PRODUCTOS order by ID_TIPO_PRODUCTO";
$tproduct=mysql_query($query_tproduct,$shopping_db_link) or die (mysql_error());
$row_tproduct=mysql_fetch_assoc($tproduct);

include('../head_admin.php');
Titulo_Maestro("ADMIN TIPO PRODUCTOS");
?>
<script>
function valida_form1(){
if(document.form1.TIPO_PRODUCTO.value=="" || document.form1.ORDER.value==""){
 alert("Please type an Product Type and an order");
}else { document.form1.submit();}
}
</script>
<table border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td><div align="center"><a href="../reports.php">Go Back to Main Menu</a> </div></td>
  </tr>
  <tr>
    <td><form id="form1" name="form1" method="post" action="<?php echo $PHP_SELF; ?>">
      <table border="0" cellspacing="1" cellpadding="2">
        <tr>
          <th>Product Type </th>
          <th>Order</th>
          <th><input name="INSERT" type="hidden" id="INSERT" value="1" /></th>
        </tr>
        <tr>
          <td><input name="TIPO_PRODUCTO" type="text" id="TIPO_PRODUCTO" /></td>
          <td><div align="center">
            <input name="ORDER" type="text" id="ORDER" size="4" />
          </div></td>
          <td><input type="button" name="Button" value="Create new" onClick="valida_form1();" /></td>
        </tr>
      </table>
          </form>    </td>
  </tr>
  <tr>
    <td><hr noshade size=1 color=9966cc width=350><br/>
</td>
  </tr>
  <tr>
    <td><form id="form2" name="form2" method="post" action="<?php echo $PHP_SELF; ?>">
      <table border="0" cellspacing="1" cellpadding="2">
        <tr>
          <th>Product type </th>
          <th>Order</th>
          <th>Status</th>
          <th>Delete</th>
        </tr>
       <?php $bgcolor="#FFFFFF"; do{
	   $bgcolor=($bgcolor=="#cecece")?"#ffffff":"#cecece";
	    ?>
	    <tr bgcolor="<?php echo $bgcolor; ?>">
          <td>
            <input name="TIPO_PRODUCTO[<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>]" type="text" id="TIPO_PRODUCTO[<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>]" value="<?php echo $row_tproduct['TIPO_PRODUCTO']; ?>" /></td>
          <td><input name="ORDER[<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>]" type="text" id="ORDER[<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>]" value="<?php echo $row_tproduct['ORDER']; ?>" size="4" /></td>
          <td align="center"><input name="STATUS[<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>]" type="checkbox" id="STATUS[<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>]" value="1" <?php if (!(strcmp($row_tproduct['STATUS'],1))) {echo "checked=\"checked\"";} ?> /></td>
          <td align="center"><input name="DELETE[<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>]" type="checkbox" id="DELETE[<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>]" value="1" />
            <input name="ID_TIPO_PRODUCTO[<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>]" type="hidden" id="ID_TIPO_PRODUCTO[<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>]" value="<?php echo $row_tproduct['ID_TIPO_PRODUCTO']; ?>" /></td>
        </tr>
		<?php } while($row_tproduct=mysql_fetch_assoc($tproduct));?>
	    <tr>
	      <td colspan="4"><div align="right">
	        <input type="submit" name="Submit" value="Process" />
	        </div></td>
	      </tr>
      </table>
          </form>    </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
  </tr>
</table>
</body>
</html>
