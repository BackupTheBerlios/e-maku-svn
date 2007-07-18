<?php 
require ('../../functions.php'); 
mysql_select_db($db,$shopping_db_link);
$query_prices="SELECT P.*,S.SIZE,TP.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET FROM PRODUCTOS P, SIZES S,TIPO_PRODUCTOS TP, SUB_TIPO_PRODUCTOS ST, MARKETS M WHERE TP.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND 
ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND P.ID_MARKET=M.ID_MARKET AND P.ID_SIZE=S.ID_SIZE AND P.ID_PRODUCT_STATUS=1 ORDER BY P.ID_TIPO_PRODUCTO,P.ID_SUB_TIPO_PRODUCTO,P.ID_MARKET";
$prices=mysql_query($query_prices,$shopping_db_link) or die(mysql_error());
$row_prices=mysql_fetch_assoc($prices);
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Price List</title>
</head>

<body>
<table width="200" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td>MARKET</td>
    <td>TIPO_PRODUCTO</td>
    <td>SUB_TIPO_PRODUCTO</td>
    <td>SIZE</td>
    <td>BUNCHES_BOX</td>
    <td>STEMS_PER_BUNCH</td>
    <td>PRICE_PER_STEM</td>
    <td>PRICE_BUNCH</td>
    <td>PRICE_BOX</td>
  </tr>
<?php do{ 
$box_price=$row_prices["PRICE_BUNCH"]*$row_prices["BUNCHES_BOX"];
$stem_price=$row_prices["PRICE_BUNCH"]/$row_prices["STEMS_PER_BUNCH"];

?>
  <tr>
    <td><?php echo $row_prices["MARKET"]; ?></td>
    <td><?php echo $row_prices["TIPO_PRODUCTO"]; ?></td>
    <td><?php echo $row_prices["SUB_TIPO_PRODUCTO"]." ".$row_prices["PRODUCTO"]; ?></td>
    <td><?php echo $row_prices["SIZE"]; ?></td>
    <td><?php echo $row_prices["BUNCHES_BOX"]; ?></td>
    <td><?php echo $row_prices["STEMS_PER_BUNCH"]; ?></td>
    <td><?php echo number_format($stem_price,2); ?></td>
    <td><?php echo number_format($row_prices["PRICE_BUNCH"],2); ?></td>
    <td><?php echo number_format($box_price,2); ?></td>
  </tr>
  <?php } while ($row_prices=mysql_fetch_assoc($prices)); ?>
</table>
</body>
</html>
