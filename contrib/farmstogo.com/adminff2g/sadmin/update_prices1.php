
<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("ADMIN PRODUCT PRICES");


    
     
if ($flag) {
	for ($i=0; $i<count($bunch); $i++) {
	  if ($status==0) {
	  /*
		mysql_db_query($db,"UPDATE PRODUCTOS SET UNIT_COST=$bunch[$i],QBCODE='$qbcode[$i]' WHERE ID_PRODUCTO=$id[$i]");
		mysql_db_query($db,"UPDATE `PRODUCTOS` P,
SIZES S SET PRICE_STEM = CASE WHEN P.UNIT = 'S' THEN ROUND( ROUND( (
(
ROUND( (
(
P.`UNIT_COST` * ( P.`STEMS_PER_BUNCH` * P.`BUNCHES_BOX` ) ) / $markup ) , 2 ) + S.FEDEX_COST
) * 1.026
),
2
) / ( P.`STEMS_PER_BUNCH` * P.`BUNCHES_BOX` ) ,
2
) WHEN P.UNIT = 'B' THEN ROUND( ROUND( (
(
ROUND( (
(
P.`UNIT_COST` * (  P.`BUNCHES_BOX` ) ) / $markup ) , 2 ) + S.FEDEX_COST
) * 1.026
),
2
) / ( P.`BUNCHES_BOX` ) ,
2
) END WHERE P.`ID_SIZE` = S.`ID_SIZE` AND P.`ID_PRODUCTO`=$id[$i]") or die(mysql_error());
		mysql_db_query($db,"UPDATE PRODUCTOS  SET `PRICE_BUNCH`= CASE WHEN UNIT='S' THEN ROUND(`PRICE_STEM`*`STEMS_PER_BUNCH`,2) WHEN UNIT='B' THEN ROUND(`PRICE_STEM`,3) END , `LAST_MARKUP`=$markup, `LAST_UPDATE` = NULL WHERE `ID_PRODUCTO`=$id[$i]") or die(mysql_error());
		if (trim(${"d".$id[$i]})) {
				mysql_db_query($db,"delete  from PRODUCTOS where ID_PRODUCTO='".$id[$i]."'");
		}
				
	*/
	echo "STATUS 0 ";			
	}			
		if (trim(${"s".$id[$i]})) {
			//echo "update PRODUCTOS SET ID_PRODUCT_STATUS=1 where ID_PRODUCTO='".$id[$i]."'<br>";
			mysql_db_query($db,"update PRODUCTOS SET ID_PRODUCT_STATUS=1 where ID_PRODUCTO='".$id[$i]."'");
		} else {
			//echo "update PRODUCTOS SET ID_PRODUCT_STATUS=0 where ID_PRODUCTO='".$id[$i]."'<br>";
			mysql_db_query($db,"update PRODUCTOS SET ID_PRODUCT_STATUS=0 where ID_PRODUCTO='".$id[$i]."'");		
		}		
		
		echo mysql_error();
	}
}
    
     
if (!$pt) { $pt ="0"; }     
?>
<script>
  function valida(){
  if(document.form2.markup.value==""){
  alert("You must indicate the markup");
  document.form2.markup.focus();
  return false;
  }else{ return true; }
  
  }
</script>
<center>
<!-- CONTENIDO -->

<center>

<form method=POST>
	<select name="mk" onchange="this.form.submit()">
	            <option value="">ALL MARKETS</option>
	            <? SelecTable($db,'MARKETS','ID_MARKET','MARKET',"",$mk) ?> 
	</select>

&nbsp;&nbsp;
	<select name="pt" onchange="this.form.submit()">
	            <option value="">ALL PRODUCT </option>
	            <? SelecTable($db,'TIPO_PRODUCTOS','ID_TIPO_PRODUCTO','TIPO_PRODUCTO',"",$pt) ?> 
	</select>


&nbsp;&nbsp;
	<select name="st" onchange="this.form.submit()">
	            <option value="">ALL SUB TYPES </option>
	            <? SelecTable($db,'SUB_TIPO_PRODUCTOS','ID_SUB_TIPO_PRODUCTO','SUB_TIPO_PRODUCTO'," WHERE ID_TIPO_PRODUCTO=$pt ",$st) ?> 
	</select>
&nbsp;&nbsp;	
	<select name="sz" onchange="this.form.submit()">
	            <option value="">ALL SIZES </option>
	            <? SelecTable($db,'SIZES','ID_SIZE','SIZE_LONG_DESC',"",$sz) ?> 
	</select>

&nbsp;&nbsp;	
	<select name="sx" onchange="this.form.submit()">
	            <option value="">ALL STATUS</option>
				<option value="2" <? if ($sx==2) { echo " selected";} ?>>Enable</option>
				<option value="1" <? if ($sx==1) { echo " selected";} ?>>Disable</option>
	             
	</select>	
<p>
&nbsp;&nbsp; <b><a href="./new_product.php">New Product</a></b>
</form>

<? if ($mk || $pt || $sz || $sx)  { 

if ($pt) {  $spt = " AND P.ID_TIPO_PRODUCTO=$pt"; }
if ($mk) {  $smk = " AND P.ID_MARKET=$mk"; }
if ($st) {  $sst = " AND P.ID_SUB_TIPO_PRODUCTO=$st"; }
if ($sz) {  $ssz = " AND P.ID_SIZE=$sz"; }
if ($sx) {  $ssx = " AND P.ID_PRODUCT_STATUS=($sx-1)"; }

$qry = "SELECT P.*,S.SIZE,TP.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET FROM PRODUCTOS P, SIZES S, ".
"TIPO_PRODUCTOS TP, SUB_TIPO_PRODUCTOS ST, MARKETS M WHERE TP.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND ".
"ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND P.ID_MARKET=M.ID_MARKET AND P.ID_SIZE=S.ID_SIZE $smk $spt $sst $ssz $ssx".
" ORDER BY MARKET, TIPO_PRODUCTO, SUB_TIPO_PRODUCTO, PRODUCTO, SIZE";
$rs = mysql_db_query($db,$qry);
//echo $qry;
echo mysql_error();
$qry_excell="SELECT P.*,S.SIZE,TP.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET FROM PRODUCTOS P, SIZES S, ".
"TIPO_PRODUCTOS TP, SUB_TIPO_PRODUCTOS ST, MARKETS M WHERE TP.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND ".
"ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND P.ID_MARKET=M.ID_MARKET AND P.ID_SIZE=S.ID_SIZE $smk $spt $sst $ssz $ssx".
" ORDER BY MARKET, TIPO_PRODUCTO, SUB_TIPO_PRODUCTO, PRODUCTO, SIZE ";
$excell=mysql_db_query($db,$qry_excell) or die (mysql_error());
$row_excell=mysql_fetch_assoc($excell);
//Generamos interface en excell
	if(mysql_num_rows($excell)){
	$file = "files/Excell_Prices_".date("YmdHis").".CSV";

	$fp = fopen($file,"w");
    $head=strtoupper("ID,Market,Product,Size,Stems_Buch,Bunches_Box,Unit,UNIT_COST,Bunch_Price,Box_Price,QBCODE");
	$head=$head."\n";
	fwrite($fp,$head) or die("Error de Escritura");
	do{
      $desc=trim(preg_replace('@<[\/\!]*?[^<>]*?>@si',"", str_replace(',','',$row_excell["TIPO_PRODUCTO"]."/".$row_excell["SUB_TIPO_PRODUCTO"]." ".$row_excell["PRODUCTO"]))); 	
	  $de=$row_excell["ID_PRODUCTO"].",".$row_excell["MARKET"].",".$desc.",".$row_excell["SIZE"].",".$row_excell["STEMS_PER_BUNCH"].",".$row_excell["BUNCHES_BOX"].",".$row_excell["UNIT"].",".$row_excell["UNIT_COST"].",".$row_excell["PRICE_BUNCH"].",".number_format($row_excell["BUNCHES_BOX"]*$row_excell["PRICE_BUNCH"],3,".","").",".$row_excell["QBCODE"];
$de = str_replace(chr(13),'',str_replace(chr(10),'',$de))."\n";
fwrite($fp,str_replace('"','',$de));
	}while($row_excell=mysql_fetch_assoc($excell));
	fclose($fp);
   }



//Fin Interface

?>
<form method=POST onsubmit="return valida();" name="form2">
<table cellpadding=3 cellspacing=1 border="0" bgcolor=cecece>
  <tr bgcolor="#ffffff">
    <th colspan="5">
		<?php if(mysql_num_rows($excell)){ ?>
	       <div align="left"><a href="<?php echo $file; ?>" target="_blank">Excell Version of this query </a></div>
	<?php } ?>
	</th>
    <th colspan="6"><div align="right">Select Mark up for this selection</div></th>
    <th><input name="markup" type="text" id="markup" size="10" dir="rtl" />  
    <th>    
    <th>    
    <th>  </tr>
  <tr> 
	<th>ID</th>
	<th>Market</th>
    <th>Product</td>
    <th>Size</td>
    <th>Status</td>
    <th>Stems/Buch</td>
    <th>Bunches/Box</td>
    <th>Unit    
    <th>Unit Cost     
    <th>Bunch Price</td>
    <th>Box Price</td>
	    <th>QBCODE</td>
        <th>Last<br />
Markup       
        <th>Last <br />Update       
    <th><br>Delete</td>  </tr>

<? while($dt = mysql_fetch_array($rs)) { 

	$sch ="";
	if ($dt["ID_PRODUCT_STATUS"]) { $sch = " checked=on"; }

?>
  
  <tr bgcolor=ffffff> 
	<td align=right><b><a href="edt_product.php?id=<?echo $dt["ID_PRODUCTO"]?>&source=<?php echo $PHP_SELF; ?>"><?echo $dt["ID_PRODUCTO"]?>&nbsp;</a></td>
	<td><?echo $dt["MARKET"]?></td>
    <td width=250><?echo $dt["TIPO_PRODUCTO"]?> / <?echo $dt["SUB_TIPO_PRODUCTO"]?><?if (trim($dt["PRODUCTO"])) { echo "<br>".$dt["PRODUCTO"];} ?> </td>
    <td><?echo $dt["SIZE"]?></td>
    <td align=center><input <?echo $sch?> type="checkbox" name="s<? echo $dt["ID_PRODUCTO"]?>" value="1"></td>
    <td align=center><?echo $dt["STEMS_PER_BUNCH"]?></td>
    <td align=center><?echo $dt["BUNCHES_BOX"]?></td>
    <td><?echo $dt["UNIT"]?></td>
    <td><input type="text" name="bunch[]" value="<?echo $dt["UNIT_COST"]?>" maxlength="6" size="6" />
    <input type=hidden name=id[] value="<?echo $dt["ID_PRODUCTO"]?>" /></td>
    <td><?echo $dt["PRICE_BUNCH"]?></td>    
    <td align=right><font color=brown><b>$<?echo number_format($dt["BUNCHES_BOX"]*$dt["PRICE_BUNCH"],2,".",",")?></td>
    <td bgcolor=ffff00>
      <input type="text" name="qbcode[]" value="<?echo $dt["QBCODE"]?>" size="13">    </td>
    <td align=center bgcolor=ffffbb><?php echo number_format($dt["LAST_MARKUP"],2,".",","); ?></td>
    <td align=center bgcolor=ffffbb><?php echo $dt["LAST_UPDATE"]; ?></td>
    <td align=center bgcolor=ffffbb><input type="checkbox" name="d<? echo $dt["ID_PRODUCTO"]?>" value="1"></td>
  </tr>
<? } ?>  
</table>
<P>
<input type=hidden name=flag value="1">
<input type=hidden name=pt value="<?echo $pt?>">
<input type=hidden name=st value="<?echo $st?>">
<input type=hidden name=mk value="<?echo $mk?>">
<input type=hidden name="status" value="0">
<input name="change_status" type=submit value="Status Changes" onclick="document.form2.status.value=1;document.form2.submit();">
<input name="Submit" type=submit value="Process Changes">
</form>

<? } ?>
<?include('../end_admin.php'); ?>

