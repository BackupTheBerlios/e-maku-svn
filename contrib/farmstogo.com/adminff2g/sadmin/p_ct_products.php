<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("Customer Type Vs. Products");
      
?>


<body bgcolor="#FFFFFF">

<center>

<? 
#######################################
#Filtro para búsqueda de productos
?>
<table cellpadding=2 cellspacing=1>
  <tr><form method=POST action="./p_ct_products.php">
    <td>
	<select name="farm" onchange="this.form.submit();">	
      <option value=""> SELECT A CUSTOMER TYPE </option>
             <? SelecTable($db,'m_ctypes','id_ctype','ctype_name',"",$farm) ?>
	</select>
    </td>
	<td>
		<select name="pt" onchange="this.form.submit()">
	            <option value=""> SELECT A PRODUCT</option>
	            <? SelecTable($db,'TIPO_PRODUCTOS','ID_TIPO_PRODUCTO','TIPO_PRODUCTO',"",$pt) ?> 
	</select>
	</td>
    </tr>
</table>
</form>
<p>

<? 

if ($farm) {
if ($pt) {  $spt = " AND P.ID_TIPO_PRODUCTO=$pt"; }
#######################################
#Despliegue de resultados

?>
<form method=POST action="./p_ct_products2.php">
<table cellpadding=3 cellspacing=1 bgcolor=cecece>
  	  <tr>
		<th colspan=6 bgcolor=ffffbb>Associated Products</th>
	  </tr>
	  <tr>
	  <td>&nbsp;</td>
    <td align=center><b>Market</strong></td>
    <td align=center><b>Product</strong></td>
    <td align=center><strong>Grade, Box</strong></td>
    <td align=center><strong>Stems</strong></td>
    <td align=center><strong>Bunches</strong></th>
	</tr>  
	  

			<?
			
$qryy = "SELECT P.*,S.SIZE,S.SIZE_LONG_DESC,TP.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET FROM PRODUCTOS P, SIZES S, ".
"TIPO_PRODUCTOS TP, SUB_TIPO_PRODUCTOS ST, j_ctypes_products JP, MARKETS M WHERE TP.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND P.ID_PRODUCTO=JP.id_product AND ".
"ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND ST.STATUS=1 AND P.ID_PRODUCT_STATUS=1 AND P.ID_SIZE=S.ID_SIZE AND JP.id_ctype='$farm' $spt ".
" AND M.ID_MARKET=P.ID_MARKET AND P.ID_PRODUCTO NOT IN (''$prds) ORDER BY M.MARKET,P.ID_TIPO_PRODUCTO,P.ID_SUB_TIPO_PRODUCTO,P.ID_SIZE";
// AND P.ID_MARKET=1			

				$sp = mysql_db_query($db,$qryy);
				while($spx = mysql_fetch_array($sp)) { 
					$prds .= ",'".$spx["ID_PRODUCTO"]."'"; 
				?>
					<tr bgcolor=e8e8e8><td><b><input type="checkbox" checked=on name=pr[] value="<?echo $spx["ID_PRODUCTO"]?>">
					</td>
					<td><?echo "<b>".$spx["MARKET"]."</b></td>" ?> </td>
				    <td><?echo "<b>".$spx["TIPO_PRODUCTO"]."</b><br>".str_replace("<br>"," ",$spx["SUB_TIPO_PRODUCTO"])?><?if (trim($spx["PRODUCTO"])) { echo $spx["PRODUCTO"];} ?> </td>				
    <td><?echo $spx["SIZE_LONG_DESC"]?></td>
    <td align=center>&nbsp;<?echo ($spx["STEMS_PER_BUNCH"]*$spx["BUNCHES_BOX"])?></td>
    <td align=center>&nbsp;<?echo $spx["BUNCHES_BOX"]?></td></tr>				
<?				
				}

// LOS QUE AUN NO ESTAN SELECCIONADOS				
$qry = "SELECT P.*,S.SIZE,S.SIZE_LONG_DESC,TP.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET FROM PRODUCTOS P, SIZES S, ".
"TIPO_PRODUCTOS TP, SUB_TIPO_PRODUCTOS ST, MARKETS M WHERE TP.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND ".
"ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND ST.STATUS=1 AND P.ID_PRODUCT_STATUS=1 AND P.ID_SIZE=S.ID_SIZE  $spt ".
" AND M.ID_MARKET=P.ID_MARKET AND P.ID_PRODUCTO NOT IN (''$prds) ORDER BY M.MARKET,P.ID_TIPO_PRODUCTO,P.ID_SUB_TIPO_PRODUCTO,P.ID_SIZE";
				
				
				$sp = mysql_db_query($db,$qry);
				echo mysql_error();
				while($spx = mysql_fetch_array($sp)) { ?>
					<tr bgcolor=ffffff><td><input type="checkbox" name=pr[] value="<?echo $spx["ID_PRODUCTO"]?>"></td>
										<td><?echo "<b>".$spx["MARKET"]."</b></td>" ?> </td>
				    <td><?echo "<b>".$spx["TIPO_PRODUCTO"]."</b><br>".str_replace("<br>"," ",$spx["SUB_TIPO_PRODUCTO"])?><?if (trim($spx["PRODUCTO"])) { echo $spx["PRODUCTO"];} ?> </td>				
    <td><?echo $spx["SIZE_LONG_DESC"]?></td>
    <td align=center>&nbsp;<?echo ($spx["STEMS_PER_BUNCH"]*$spx["BUNCHES_BOX"])?></td>
    <td align=center>&nbsp;<?echo $spx["BUNCHES_BOX"]?></td></tr>
			<?	
				}
			?></b>
</table>
	<input type=hidden name=farm value="<?echo $farm?>">
	<input type=hidden name=pt value="<?echo $pt?>"><p>
<p>
	<input type=submit value="Send >">
</form>
<? } ?>

</center>

<? include('../end_admin.php') ?>
</body>
</html>
