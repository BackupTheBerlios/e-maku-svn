<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("ADMIN PROMOTIONS");


if ($prom)  {

	if ($idx) {
		$query = "UPDATE PROMOTIONS SET PROMOTION='$prom' , DESC_PROMOTION='$desc' , ".
		" ID_PROMOTION_STATUS='$st' , ID_PRODUCTO_PROMOTION='$pr' , ID_QUANTITY_PROMOTION='$qty' , ".
		" PROMOTION_PRICE='$price' , ID_PRODUCTO_BONUS='$bonus' , ID_QUANTITY_BONUS='$qtyb'".
		" , BONUS_PRICE='$bprice' , BONUS_WITH_IT='$with' , BONUS_ARRIVAL_DATE='$adate' WHERE ID_PROMOTION=$idx";
	} else {
		$query = "INSERT INTO PROMOTIONS ( PROMOTION , DESC_PROMOTION , ".
		" ID_PROMOTION_STATUS , ID_PRODUCTO_PROMOTION , ID_QUANTITY_PROMOTION , PROMOTION_PRICE ,".
		" ID_PRODUCTO_BONUS , ID_QUANTITY_BONUS , BONUS_PRICE , BONUS_WITH_IT , BONUS_ARRIVAL_DATE ) ".
		" VALUES ( '$prom', '$desc', '$st', '$pr', '$qty', '$price', '$bonus', '$qtyb',".
		"  '$bprice', '$with','$adate')";		
	}	
		
	//echo $query;
	mysql_db_query($database,$query);

	echo mysql_error();
	Alerta("Promotion Ready !!!");
}

$today  = mktime (0,0,0,date("m"),date("d"),date("Y"));

/*
$qx = "select DISTINCT T.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET,S.SIZE,P.ID_PRODUCTO,".
"P.PRODUCTO,P.BUNCHES_BOX,P.PRICE_BUNCH*P.BUNCHES_BOX AS PRICE from MARKETS M, SIZES S, ".
"PRODUCTOS P, TIPO_PRODUCTOS T , SUB_TIPO_PRODUCTOS ST WHERE P.COLOR_VARIETY_CHOICE=0 AND S.ID_SIZE=P.ID_SIZE AND ".
"T.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND ".
" UNIX_TIMESTAMP(M.ACTIVATE_DATE) <= $today   AND UNIX_TIMESTAMP(M.DESACTIVATE_DATE) >= $today ".
"  AND M.ID_MARKET <> 2 AND M.ID_MARKET=P.ID_MARKET order by TIPO_PRODUCTO,SUB_TIPO_PRODUCTO,PRODUCTO,MARKET, SIZE";
*/
$qx = "select DISTINCT T.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET,S.SIZE,P.ID_PRODUCTO,".
"P.PRODUCTO,P.BUNCHES_BOX,P.PRICE_BUNCH*P.BUNCHES_BOX AS PRICE from MARKETS M, SIZES S, ".
"PRODUCTOS P, TIPO_PRODUCTOS T , SUB_TIPO_PRODUCTOS ST WHERE S.ID_SIZE=P.ID_SIZE AND ".
"T.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND ".
" UNIX_TIMESTAMP(M.ACTIVATE_DATE) <= $today   AND UNIX_TIMESTAMP(M.DESACTIVATE_DATE) >= $today ".
"  AND M.ID_MARKET <> 2 AND M.ID_MARKET=P.ID_MARKET order by TIPO_PRODUCTO,SUB_TIPO_PRODUCTO,PRODUCTO,MARKET, SIZE";

if ($id) {
	$dx = mysql_db_query($database,"SELECT * FROM PROMOTIONS WHERE ID_PROMOTION=$id");
	$dx = mysql_fetch_array($dx);
}
mysql_select_db($db,$shopping_db_link);
/*
$query_bonus="select DISTINCT T.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET,S.SIZE,P.ID_PRODUCTO,".
"P.PRODUCTO,P.BUNCHES_BOX,P.PRICE_BUNCH*P.BUNCHES_BOX AS PRICE from MARKETS M, SIZES S, ".
"PRODUCTOS P, TIPO_PRODUCTOS T , SUB_TIPO_PRODUCTOS ST WHERE P.COLOR_VARIETY_CHOICE=0 AND S.ID_SIZE=P.ID_SIZE AND ".
"T.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND ".
" UNIX_TIMESTAMP(M.ACTIVATE_DATE) <= $today   AND UNIX_TIMESTAMP(M.DESACTIVATE_DATE) >= $today ".
"  AND M.ID_MARKET <> 2 AND M.ID_MARKET=P.ID_MARKET order by TIPO_PRODUCTO,SUB_TIPO_PRODUCTO,PRODUCTO,MARKET, SIZE";
*/
$query_bonus="select DISTINCT T.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET,S.SIZE,P.ID_PRODUCTO,".
"P.PRODUCTO,P.BUNCHES_BOX,P.PRICE_BUNCH*P.BUNCHES_BOX AS PRICE from MARKETS M, SIZES S, ".
"PRODUCTOS P, TIPO_PRODUCTOS T , SUB_TIPO_PRODUCTOS ST WHERE S.ID_SIZE=P.ID_SIZE AND ".
"T.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND ".
" UNIX_TIMESTAMP(M.ACTIVATE_DATE) <= $today   AND UNIX_TIMESTAMP(M.DESACTIVATE_DATE) >= $today ".
"  AND M.ID_MARKET <> 2 AND M.ID_MARKET=P.ID_MARKET order by TIPO_PRODUCTO,SUB_TIPO_PRODUCTO,PRODUCTO,MARKET, SIZE";
$bonusx=mysql_query($query_bonus,$shopping_db_link) or die(mysql_error());
$row_bonus=mysql_fetch_assoc($bonusx);

?>

<body bgcolor="#FFFFFF">
<center>
<table cellpadding=3 cellspacing=1 border="0">
  
  
  <form method=POST name=x>
  <tr> 
    <td>Promotion<br>
      <input type="text" name="prom" size=45 value="<?echo $dx["PROMOTION"]?>">
    </td>
    <td>Description<br>
      <input type="text" name="desc" size=65 value="<?echo $dx["DESC_PROMOTION"]?>">
    </td>
  </tr>
  <tr> 
    <td colspan=3>Producto<br>
      <select name="pr"  style="font-size:11px;">
		   <option value="">CHOSE A PRODUCT</option>	
           <?            
	$rs = mysql_db_query($database,$qx);
	echo mysql_error();
	while($dt = mysql_fetch_array($rs)) { 
	$sel = "";
	if (!strcmp($dt["ID_PRODUCTO"],$dx["ID_PRODUCTO_PROMOTION"])) { $sel = " selected"; } 
		?>
		<option value="<?echo $dt["ID_PRODUCTO"]?>" <?echo $sel?>> <?echo strtoupper($dt["TIPO_PRODUCTO"])."-".$dt["SUB_TIPO_PRODUCTO"]?>-<? echo strtoupper($dt["SIZE"]).", ".$dt["PRODUCTO"]." [$".number_format($dt["PRICE"],2)."]";?></option> 
		<? 
	}                     
      ?>   
               
      </select>
    </td>
  </tr>
  <tr>    
    <td>Quantity<br>
      <input type="text" name="qty" size="5" maxlength="2"  value="<?echo $dx["ID_QUANTITY_PROMOTION"]?>">
    </td>

    <td>Price<br>
      <input type="text" name="price" size="8"  value="<?echo $dx["PROMOTION_PRICE"]?>">
    </td>
  
  </tr>
  <tr>       
    <td colspan=3>Producto Bonus<br>
      <select name="bonus" style="font-size:11px;">
        <option value="" <?php if (!(strcmp("",$dx["ID_PRODUCTO_BONUS"]))) {echo "selected=\"selected\"";} ?>>CHOSE A PRODUCT</option>
        <?            
	do { 
		?>
        <option value="<? echo $row_bonus["ID_PRODUCTO"]?>" <?php if (!(strcmp($row_bonus["ID_PRODUCTO"],$dx["ID_PRODUCTO_BONUS"]))) {echo "selected=\"selected\"";} ?>><?echo strtoupper($row_bonus["TIPO_PRODUCTO"])."-".$row_bonus["SUB_TIPO_PRODUCTO"]?>-<? echo strtoupper($row_bonus["SIZE"]).", ".$row_bonus["PRODUCTO"]." [$".number_format($row_bonus["PRICE"],2)."]";?></option>
        <? 
	} while($row_bonus = mysql_fetch_assoc($bonusx));                
      ?>        
      </select>
    </td>
   </tr>
  <tr>
    <td>Quantity Bonus<br>
      <input type="text" name="qtyb" size="5" maxlength="2"  value="<?echo $dx["ID_QUANTITY_BONUS"]?>">
    </td>
    <td>Bonus Price<br>
      <input type="text" name="bprice" size="8"  value="<?echo $dx["BONUS_PRICE"]?>">
    </td>
  </tr>
  <tr> 
    <td>Bonus With it?<br>
      <input type="checkbox" name="with" value="1"  <?if ($dx["BONUS_WITH_IT"]) { echo " checked=on";} ?>">
    </td>
    <td>Bonus Arrival Date<br>
      <input type="text" name="adate" value="<?echo $dx["BONUS_ARRIVAL_DATE"]?>">
    </td>

    <td>Status<br>
      <select name="st">
		<option value="1" <?if ($dx["ID_PROMOTION_STATUS"]==1) { echo " selected"; }?>>ACTIVE</option>
		<option value="2" <?if ($dx["ID_PROMOTION_STATUS"]==2) { echo " selected"; }?>>INACTIVE</option>
      </select>
          </td>
  </tr>
  <tr>     
    <td colspan=3 align=center> <br>
		<input type=hidden name=idx value="<?echo $id?>">
      <input type="submit" name="Submit" value="Save &gt;&gt;">
    </td>
  </tr>
  </form>
</table>
<P>
<a href="admin_promotions.php">Admin Promotions</a><P>
<p>
<?include('../end_admin.php'); ?>
</body>
</html>
