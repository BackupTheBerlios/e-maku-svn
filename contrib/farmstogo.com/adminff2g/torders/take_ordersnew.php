<?php
//Funciones requeridas
require_once("../../utilidades/sesiones.php"); 	// esta es la libreria en donde se crean las sesiones
validarSesion();  // funcion que abre la sesion para este archivo
require ('../../functions.php'); // en este archivo esta el esquema de base de datos.
include('../head_admin.php'); // en este archivo se encuentra el cabezote.
?>
<?php 
$SID=date("YmdHis");
if(isset($_GET['SID'])){
$SID=$_GET['SID'];
}
session_start();
if(!isset($_SESSION['itemsEnCesta'])){
session_register('itemsEnCesta');
session_register('ValorEnCesta');
}
$item=$_POST['product'];
$cantidad=$_POST['qty'];
$precio=$_POST['price'];
$valor=round($cantidad*$precio,2);
$itemsEnCesta=$_SESSION['itemsEnCesta'];
$ValorEnCesta=$_SESSION['ValorEnCesta'];
if ($item){
   if (!isset($itemsEnCesta)){
      $itemsEnCesta[$item]=$cantidad;
	  $ValorEnCesta[$item]=$precio;
   	}else{
	
      foreach($itemsEnCesta as $k => $v){
         if ($item==$k){
         $itemsEnCesta[$k]+=$cantidad;
	     $ValorEnCesta[$item]=precio;
         $encontrado=1;
         }
      }
      if (!$encontrado){
	   $itemsEnCesta[$item]=$cantidad;
	  $ValorEnCesta[$item]=$precio;
    	}
	}  
  }
$_SESSION['itemsEnCesta']=$itemsEnCesta;
$_SESSION['ValorEnCesta']=$ValorEnCesta;
?>
<?php
$today  = mktime (0,0,0,date("m"),date("d"),date("Y"));
$id_spr="NULL";
if(isset($_GET['id_spr'])){
$id_spr=$_GET['id_spr'];
}
mysql_select_db($database,$shopping_db_link);
$query_products="SELECT DISTINCT T.TIPO_PRODUCTO,S.ID_SUB_TIPO_PRODUCTO,S.SUB_TIPO_PRODUCTO
FROM 
TIPO_PRODUCTOS T,SUB_TIPO_PRODUCTOS S, PRODUCTOS P, MARKETS M 
WHERE S.ID_TIPO_PRODUCTO=T.ID_TIPO_PRODUCTO AND UNIX_TIMESTAMP(M.ACTIVATE_DATE) <= $today   AND 
UNIX_TIMESTAMP(M.DESACTIVATE_DATE) >= $today  AND M.ID_MARKET <> 2 
AND  M.ID_MARKET=P.ID_MARKET AND S.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO 
ORDER BY TIPO_PRODUCTO, SUB_TIPO_PRODUCTO";
$products=mysql_query($query_products,$shopping_db_link) or die(mysql_error());
$row_products=mysql_fetch_assoc($products);

mysql_select_db($database,$shopping_db_link);
$query_market="select DISTINCT M.MARKET,S.SIZE,P.ID_PRODUCTO,P.PRODUCTO,P.BUNCHES_BOX,round(P.PRICE_BUNCH*P.BUNCHES_BOX,2) AS PRICE 
from MARKETS M, SIZES S, PRODUCTOS P WHERE P.ID_SUB_TIPO_PRODUCTO=$id_spr 
AND S.ID_SIZE=P.ID_SIZE AND UNIX_TIMESTAMP(M.ACTIVATE_DATE) <= $today   AND
UNIX_TIMESTAMP(M.DESACTIVATE_DATE) >= $today  AND M.ID_MARKET <> 2 AND 
M.ID_MARKET=P.ID_MARKET AND M.public=1 order by MARKET, SIZE";
$market=mysql_query($query_market, $shopping_db_link) or die(mysql_error());
$row_market=mysql_fetch_assoc($market);

?>
<html>
<style type="text/css">
<!--
.style1 {color: #EEEEEE}
-->
</style>
<body>
<form action="<?=$PHP_SELF."?SID=".$SID?>" method="post">
  <table border="0" cellspacing="0" cellpadding="1" style="border:1px solid #dddddd;">
    <tr bgcolor="#FFFF00">
      <td bgcolor="ffff77">Product</td>
      <td colspan="3" bgcolor="#FFFF77"><select name="product" onChange="self.location='<?php echo $PHP_SELF."?SID=".$SID; ?>&id_spr='+this.value">
        <option value="" <?php if (!(strcmp("", $id_spr))) {echo "selected=\"selected\"";} ?>>Select Product</option>
		<?php do{ ?>
		 <option value="<?php echo $row_products['ID_SUB_TIPO_PRODUCTO']; ?>" <?php if (!(strcmp($row_products['ID_SUB_TIPO_PRODUCTO'], $id_spr))) {echo "selected=\"selected\"";} ?>><?php echo $row_products['TIPO_PRODUCTO']."/".$row_products['SUB_TIPO_PRODUCTO']?></option>
		<?php } while($row_products=mysql_fetch_array($products));?>
      </select>      </td>
    </tr>
    <?php if(isset($_GET['id_spr'])){?>
	<tr bgcolor="#BBBBBB">
      <td>Market</td>
      <td colspan="3"><select name="price">
        <option>Select Market</option>
		<?php do{  ?>
	        <option value="<?php echo $row_market["PRICE"] ?>"><?php echo strtoupper($row_market["MARKET"])." - ".strtoupper($row_market["SIZE"]).", ".$row_market["PRODUCTO"]." [".$row_market["BUNCHES_BOX"]." Bchs, $".$row_market["PRICE"]."]";?></option>
		<?php } while($row_market=mysql_fetch_assoc($market)); ?>
      </select>      </td>
    </tr>
    <tr bgcolor="#FFFF77">
      <td>Qty</td>
      <td><input name="qty" type="text" size="10"></td>
      <td><select name="ddate" id="ddate">
        <option>Select Date</option>
      </select>      </td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td colspan="4"><input name="submit" type="submit" value="Add"></td>
    </tr>
	<?php } ?>
  </table>
  <br>
  <br>
  <br>
</form>
<?php if (isset($itemsEnCesta)){ ?>
 <form name="form1" method="post" action="">
   <table cellpadding="1">
     <tr bgcolor="#CC3333">
       <th><span class="style1">Product Ordered </span></th>
       <th><span class="style1">Price per Box </span></th>
       <th><span class="style1">QTY</span></th>
       <th><span class="style1">Total</span></th>
       <th><span class="style1">Desired Delivery Date</span></th>
       <th><span class="style1">Surcharge</span></th>
       <th><span class="style1">&nbsp;</span></th>
       <th><span class="style1">&nbsp;</span></th>
     </tr>
     <?
   foreach($itemsEnCesta as $k => $v){
   $query_body="SELECT DISTINCT S.ID_SUB_TIPO_PRODUCTO, T.TIPO_PRODUCTO, S.SUB_TIPO_PRODUCTO
FROM TIPO_PRODUCTOS T, SUB_TIPO_PRODUCTOS S, PRODUCTOS P
WHERE S.ID_TIPO_PRODUCTO = T.ID_TIPO_PRODUCTO
AND S.ID_SUB_TIPO_PRODUCTO = P.ID_SUB_TIPO_PRODUCTO and  P.ID_SUB_TIPO_PRODUCTO=$k
ORDER BY ID_SUB_TIPO_PRODUCTO
";
$body=mysql_query($query_body,$shopping_db_link) or die(mysql_error());
$row_body=mysql_fetch_array($body);
   ?>
     <tr>
       <td><?php echo strtoupper($row_body['TIPO_PRODUCTO'])."/ ".$row_body['SUB_TIPO_PRODUCTO']; ?></td>
       <td align="right"><?php echo $ValorEnCesta[$k]; ?></td>
       <td align="right"><?php echo $v; ?></td>
       <td align="right"><?php echo number_format($ValorEnCesta[$k]*v,2); ?></td>
       <td><div align="right">
         <input name="ddate" type="text" id="ddate" size="10">
       </div></td>
       <td><input name="textfield" type="text" size="10"></td>
       <td>&nbsp;</td>
       <td>&nbsp;</td>
     </tr>
     <?php } ?>
   </table>
  </form>
 <? } ?>
</body>
</html> 