<? 
require ('../../functions.php'); 
include('../head_admin.php');

$database = "flowerde_ff2gv";
$db = "flowerde_ff2gv";
$h2 = "mysqlv.temp1000.dreamhosters.com";
$u2 = "miguels";
$pw2 = "patricia";
$database2 = "ff2gv";
$db2 = "ff2gv";
$rosefarms=mysql_connect($h,$u,$pw) or die(mysql_error());
mysql_select_db($database,$rosefarms) or die(mysql_error());     

Titulo_Maestro("CLONE MARKETS FROM TEMPLATE");
foreach($_POST as $nombre_campo => $val){ 
   $asignacion = "\$" . $nombre_campo . "='" . $val . "';"; 
   eval($asignacion);
      }
///Funcion para clonar mercados
if(isset($id)){
foreach($_POST['id']  as $i => $valor ){
    $bun=$_POST['bunch'][$i];
	$qb=$_POST['qbcode'][$i];
	if (isset($nm[$i])) {
	$qry="insert into PRODUCTOS SELECT '',ID_SUB_TIPO_PRODUCTO , ID_TIPO_PRODUCTO , ID_SIZE , ".$nmk." , PRODUCTO , PRICE_STEM , STEMS_PER_BUNCH ,'".$bun."', BUNCHES_BOX , DELIVERY_DELAY , ID_PRODUCT_STATUS , COLOR_VARIETY_CHOICE ,'".$qb."', minimal_box,GENERIC_NAME 
FROM PRODUCTOS
WHERE ID_PRODUCTO='".$valor."'";
echo $qry."\n\n";	
			mysql_query($qry,$rosefarms) or die(mysql_error());
		}
}
RetornarOL ('reports.php');
}

$market=mysql_query("select DISTINCT ID_MARKET,MARKET FROM MARKETS",$rosefarms) or die(mysql_error());
$row_market=mysql_fetch_row($market);     
$prod=mysql_query("select distinct ID_TIPO_PRODUCTO,TIPO_PRODUCTO from TIPO_PRODUCTOS",$rosefarms) or die(mysql_error());
$row_prod=$row_market=mysql_fetch_row($prod);  
if($pt!=""){
$stype=mysql_query("select distinct ID_SUB_TIPO_PRODUCTO,SUB_TIPO_PRODUCTO from SUB_TIPO_PRODUCTOS WHERE ID_TIPO_PRODUCTO=$pt",
$rosefarms) or die(mysql_error());}else{
$stype=mysql_query("select distinct ID_SUB_TIPO_PRODUCTO,SUB_TIPO_PRODUCTO from SUB_TIPO_PRODUCTOS WHERE 0",
$rosefarms) or die(mysql_error());
}
$row_stype=mysql_fetch_row($stype);
$size=mysql_query("Select DISTINCT ID_SIZE,SIZE_LONG_DESC FROM SIZES",$rosefarms) or die(mysql_error());
$row_size=mysql_fetch_row($size);
 
     
if (!$pt) { $pt ="0"; }     
?>
<script language="javascript">
function valida(){
if(document.form2.nmk.value<1){
	alert("You must select a market to make this changes");
	document.form2.nmk.focus();
	} else { document.form2.submit(); }
}
</script>
<center>
<!-- CONTENIDO -->

<center>

<form action="" method=POST name="form1" id="form1">
	<select name="mk" onchange="this.form.submit()">
	  <option value="" <?php if (!(strcmp("", $mk))) {echo "selected=\"selected\"";} ?>>ALL MARKETS</option>
         <? SelecTable($db,'MARKETS','ID_MARKET','MARKET','',$mk) ?>
	</select>

&nbsp;&nbsp;
	<select name="pt" onchange="this.form.submit()">
	  <option value="" <?php if (!(strcmp("", $pt))) {echo "selected=\"selected\"";} ?>>ALL PRODUCT </option>
<?php do{ ?>
	 <option value="<?php echo $row_prod[0]; ?>" <?php if (!(strcmp("$row_prod[0]", $pt))) {echo "selected=\"selected\"";} ?>><?php echo $row_prod[1]; ?></option>
	 <?php }while($row_prod=mysql_fetch_row($prod));?>
  </select>


&nbsp;&nbsp;
	<select name="st" onchange="this.form.submit()">
	  <option value="" <?php if (!(strcmp("", $st))) {echo "selected=\"selected\"";} ?>>ALL SUB TYPES </option>
	  <?php do{ ?><option value="<?php echo $row_stype[0]; ?>" <?php if (!(strcmp($row_stype[0], $st))) {echo "selected=\"selected\"";} ?>><?php echo $row_stype[1]; ?></option>
	  <?php }while($row_stype=mysql_fetch_row($stype));?>
  </select>
&nbsp;&nbsp;	
	<select name="sz" onchange="this.form.submit()">
	  <option value="" <?php if (!(strcmp("", $sz))) {echo "selected=\"selected\"";} ?>>ALL SIZES </option>
	  <?php do{ ?><option value="<?php echo $row_size[0]; ?>" <?php if (!(strcmp($row_size[0], $sz))) {echo "selected=\"selected\"";} ?>><?php echo $row_size[1]; ?></option>
	  <?php }while($row_size=mysql_fetch_row($size));?>
	</select>

&nbsp;&nbsp;	
	<select name="sx" onchange="this.form.submit()">
	            <option value="">ALL STATUS</option>
				<option value="2" <? if ($sx==2) { echo " selected";} ?>>Enable</option>
				<option value="1" <? if ($sx==1) { echo " selected";} ?>>Disable</option>
	             
	</select>	
<p>
&nbsp;&nbsp; <b><a href="./new_product.php">New Product</a></b> | <a href="reports.php"><strong>Go to main menu</strong></a>
</form>
<? 
$qry = "SELECT P.*,S.SIZE,TP.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET FROM PRODUCTOS P, SIZES S,
TIPO_PRODUCTOS TP, SUB_TIPO_PRODUCTOS ST, MARKETS M WHERE TP.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND P.ID_MARKET=M.ID_MARKET AND P.ID_SIZE=S.ID_SIZE ORDER BY P.ID_TIPO_PRODUCTO,P.ID_SUB_TIPO_PRODUCTO,P.ID_MARKET";
if ($mk || $pt || $sz || $sx ){ 
if ($pt) {  $spt = " AND P.ID_TIPO_PRODUCTO=$pt"; }
if ($mk) {  $smk = " AND P.ID_MARKET=$mk"; }
if ($st) {  $sst = " AND P.ID_SUB_TIPO_PRODUCTO=$st"; }
if ($sz) {  $ssz = " AND P.ID_SIZE=$sz"; }
if ($sx) {  $ssx = " AND P.ID_PRODUCT_STATUS=($sx-1)"; } 
$qry = "SELECT P.*,S.SIZE,TP.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET FROM PRODUCTOS P, SIZES S,
TIPO_PRODUCTOS TP, SUB_TIPO_PRODUCTOS ST, MARKETS M WHERE TP.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND P.ID_MARKET=M.ID_MARKET AND P.ID_SIZE=S.ID_SIZE $smk $spt $sst $ssz $ssx ORDER BY P.ID_TIPO_PRODUCTO,P.ID_SUB_TIPO_PRODUCTO,P.ID_MARKET";
}
$rs = mysql_query($qry,$rosefarms) or die("Unable to load products, error: ".mysql_error());
$dt = mysql_fetch_array($rs);

?>
<form action="change_market.php" method="post" name="form2" id="form2" >
<table cellpadding=3 cellspacing=1 border="0" bgcolor=cecece>
  <tr bgcolor="#FFFFFF">
    <th colspan="10">                  <div align="left">
      <select name="nmk" id="nmk">
	  <option value="">Select Market</option>
        <? SelecTable($db,'MARKETS','ID_MARKET','MARKET','','') ?>
      </select>
      <input name="Button2" type="button" value="Process Changes" onclick="valida();" />
    </div></th>
  </tr>
  <tr> 
	<th width="25">ID</th>
	<th width="52">Market</th>
    <th>Product</td>
    <th width="27">Size</td>
    <th width="42">Status</td>
    <th width="79">Stems/Buch</td>
    <th width="88">Bunches/Box</td>
    <th width="54">Bunch Price</td>
    <th>
    QBCODE
    </td>
  
    <th>Create new Market  </tr>
<? $xx=0; do { 

	$sch ="";
	if ($dt["ID_PRODUCT_STATUS"]) { $sch = " checked=on"; }

?>
  
  <tr bgcolor=ffffff> 
	<td align=right><b><a href="edt_product.php?id=<?echo $dt["ID_PRODUCTO"]?>"><?echo $dt["ID_PRODUCTO"]?>&nbsp;</a></td>
	<td><?echo $dt["MARKET"]?></td>
    <td width=82><?echo $dt["TIPO_PRODUCTO"]?> / <?echo $dt["SUB_TIPO_PRODUCTO"]?><?if (trim($dt["PRODUCTO"])) { echo "<br>".$dt["PRODUCTO"];} ?> </td>
    <td><?echo $dt["SIZE"]?></td>
    <td align=center><input <?echo $sch?> type="checkbox" name="s<? echo $dt["ID_PRODUCTO"]?>" value="1"></td>
    <td align=center><?echo $dt["STEMS_PER_BUNCH"]?></td>
    <td align=center><?echo $dt["BUNCHES_BOX"]?></td>
    <td>
      <input type="text" id="bunch[<?php echo $xx; ?>]" name="bunch[<?php echo $xx; ?>]" value="<? echo $dt['PRICE_BUNCH']; ?>" maxlength="6" size="6">
      <input type=hidden name="id[<?php echo $xx; ?>]" value="<? echo $dt['ID_PRODUCTO']; ?>" id="id[]" >    <input name="stp[]" type="hidden" id="stp[]" value="<? echo $dt["ID_PRODUCTO"]?>" /></td>    
    <td align=right>
      <input type="text" id="qbcode[<?php echo $xx; ?>]" name="qbcode[<?php echo $xx; ?>]" value="<? echo $dt['QBCODE']; ?>" size="13">    </td>
    <td align=center bgcolor=ffffbb><input type="checkbox" name="nm[<?php echo $xx; ?>]" value="1" /></td>
  </tr>
  <? $xx++;}while($dt = mysql_fetch_array($rs)); ?>  
</table>
<P>
<input type=hidden name="flag" value="1">
<input name="Button" type=button value="Process Changes" onclick="valida();">
</form>
<? include('../end_admin.php'); ?>
