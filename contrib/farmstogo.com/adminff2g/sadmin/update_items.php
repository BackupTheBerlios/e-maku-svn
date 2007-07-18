
<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("ADMIN ITEMS");


    
     
if ($flag) {
		
foreach($id as $k=>$i){		
		mysql_db_query($db,"UPDATE PRODUCTOS SET QBCODE='$qbcode[$i]',GENERIC_NAME='$generic_name[$i]',delay='$delay[$i]' WHERE ID_PRODUCTO=$id[$i]") or die(mysql_error());
		if (trim(${"d".$id[$i]})) {
				mysql_db_query($db,"delete  from PRODUCTOS where ID_PRODUCTO='".$id[$i]."'");
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
<center>
<!-- CONTENIDO -->

<center>

<form method=POST action="update_items.php">
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

?>
<form method=POST  name="form2" action="update_items.php">
<table cellpadding=3 cellspacing=1 border="0" bgcolor=cecece>
  
  <tr> 
	<th>ID
	  <input name="flag" type="hidden" id="flag" value="1" /></th>
	<th>Market</th>
    <th>Product</td>
    <th>Generic Name     
    <th>Size</td>
    <th>Status</td>
    <th>Stems/Buch</td>
    <th>Bunches/Box</td>
    <th>Unit    
    <th>QBCODE</td>
        <th>Last <br />Update       
        <th>Delay        
    <th><br>Delete</td>  </tr>

<? while($dt = mysql_fetch_array($rs)) { 

	$sch ="";
	if ($dt["ID_PRODUCT_STATUS"]) { $sch = " checked=on"; }

?>
  
  <tr bgcolor=ffffff> 
	<td align=right><input name="id[<?php echo $dt["ID_PRODUCTO"]; ?>]" type="hidden" id="id[<?php echo $dt["ID_PRODUCTO"]; ?>]" value="<?php echo $dt["ID_PRODUCTO"]; ?>" />
	  <b><a href="edt_product.php?id=<?echo $dt["ID_PRODUCTO"]?>&source=<?php echo $PHP_SELF; ?>"><?echo $dt["ID_PRODUCTO"]?>&nbsp;</a></td>
	<td><?echo $dt["MARKET"]?></td>
    <td width=250><?echo $dt["TIPO_PRODUCTO"]?> / <?echo $dt["SUB_TIPO_PRODUCTO"]?><?if (trim($dt["PRODUCTO"])) { echo "<br>".$dt["PRODUCTO"];} ?> </td>
    <td><input name="generic_name[<?php echo $dt["ID_PRODUCTO"]; ?>]" type="text" id="generic_name[<?php echo $dt["ID_PRODUCTO"]; ?>]" value="<?echo $dt["GENERIC_NAME"]?>" size="30" /></td>
    <td><?echo $dt["SIZE"]?></td>
    <td align=center><input <?echo $sch?> type="checkbox" name="s<? echo $dt["ID_PRODUCTO"]?>" value="1"></td>
    <td align=center><?echo $dt["STEMS_PER_BUNCH"]?></td>
    <td align=center><?echo $dt["BUNCHES_BOX"]?></td>
    <td><?echo $dt["UNIT"]?></td>
    <td bgcolor=ffff00>
      <input name="qbcode[<?php echo $dt["ID_PRODUCTO"]; ?>]" type="text" id="qbcode[<?php echo $dt["ID_PRODUCTO"]; ?>]" value="<?echo $dt["QBCODE"]?>" size="13">    </td>
    <td align=center bgcolor=ffffbb><?php echo $dt["LAST_UPDATE"]; ?></td>
    <td align=center bgcolor=ffffbb><input name="delay[<?php echo $dt["ID_PRODUCTO"]; ?>]" type="text" id="delay[<?php echo $dt["ID_PRODUCTO"]; ?>]" size="6" value="<?php echo $dt["delay"]; ?>"  alt="Time in advance for the request for this product"/></td>
    <td align=center bgcolor=ffffbb><input type="checkbox" name="d<? echo $dt["ID_PRODUCTO"]?>" value="1"></td>
  </tr>
<? } ?>  
</table>
<P>
<input type=hidden name=flag value="1">
<input type=hidden name=pt value="<?echo $pt?>">
<input type=hidden name=st value="<?echo $st?>">
<input type=hidden name=mk value="<?echo $mk?>">
<input name="Submit" type=submit value="Process Changes">
</form>

<? } ?>
<?include('../end_admin.php'); ?>

