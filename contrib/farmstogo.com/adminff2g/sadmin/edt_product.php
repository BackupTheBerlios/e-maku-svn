<? 
	session_start();
	include('../../functions.php'); 
   include('../head_admin.php'); 
   Titulo_Maestro("Update Product");
   $source=(isset($_GET['source']))?$_GET['source']:'update_prices.php';
   mysql_select_db($db,$shopping_db_link) or die (mysql_error());
   $query_fedex="select DISTINCT ID_SIZE,FEDEX_COST FROM SIZES";
   $fedex=mysql_query($query_fedex,$shopping_db_link) or die(mysql_error());
   $row_fedex=mysql_fetch_assoc($fedex);
    echo "<script> \n";
	echo "var fedex= new Array; \n";
	do{
	echo "fedex[".$row_fedex[ID_SIZE]."]=".$row_fedex[FEDEX_COST]."; \n ";
	} while ($row_fedex=mysql_fetch_assoc($fedex));
	echo "</script> \n";
?>
<body bgcolor="#FFFFFF">

<?
if ($ptx) {

	$CQuery = <<< EOP
UPDATE PRODUCTOS SET ID_SUB_TIPO_PRODUCTO='$st' , ID_SIZE='$sz' , 
ID_MARKET='$mk' , PRODUCTO='$product' , STEMS_PER_BUNCH=$stems_bunch , PRICE_BUNCH='$price',
BUNCHES_BOX='$bch_box',  ID_PRODUCT_STATUS='$status', COLOR_VARIETY_CHOICE='$dc' , QBCODE='$qbcode', minimal_box='$min_box',UNIT='$unit', UNIT_COST=$unit_cost
where ID_PRODUCTO=$id
EOP;
		#echo $CQuery;
		mysql_db_query($db,$CQuery);


		
		if(mysql_error()) {	
			Alerta(mysql_error());
			History("1");
		} else {
			Alerta("Product Updated successfully");
			RetornarOL($source."?st=$st&mk=$mk");
		}
	
	}
else {

$rs = mysql_db_query($db,"SELECT * FROM PRODUCTOS WHERE ID_PRODUCTO=$id");
echo mysql_error();
$rs = mysql_fetch_array($rs);

?>
	<center>
	<script>
		function Validar() {
			with (document.pr) {
				if(price.value=='' || stems_bunch.value=='' || bch_box.value=='' || mk.selectedIndex==0 || pt.selectedIndex==0 || st.selectedIndex==0 || sz.selectedIndex==0  || !st.unit[0].checked || !st.unit[1].checked || st.unit_cost.value=='' || st.markup.value=='')
					{ alert ('All fields signed with (*) are required'); return false; }						
			}
		}	
		
	function cambia(){
		valor=0;
		stemsbunch=parseFloat(document.pr.stems_bunch.value);
		bunchesbox=parseFloat(document.pr.bch_box.value);
		unitcost=parseFloat(document.pr.unit_cost.value);
		markup=parseFloat(document.pr.markup.value);
				if(markup==0 || !markup){markup=1; }

		
		  if(document.pr.unit[0].checked){ 
		  valor=((((unitcost/markup)*bunchesbox*stemsbunch)+fedex[parseInt(document.pr.sz.value)])*1.026)/bunchesbox ;
		  }else if(document.pr.unit[1].checked){
		  valor=((((unitcost/markup)*bunchesbox)+fedex[parseInt(document.pr.sz.value)])*1.026)/bunchesbox  ;
		  }
		  valround=Math.round(valor*100)/100;
		  document.pr.price.value=valround;
		}			
	</script>
	
	
	
	<form name=pr method=post onSubmit="return Validar()" >	
	<table border="0" cellpadding=3 cellspacing=2>
	  <tr> 
	  
   <td width="168" >Market * <br>
	<select name="mk">	
      <option value=""> </option>
       <? SelecTable($db,'MARKETS','ID_MARKET','MARKET','',$rs["ID_MARKET"]) ?>
	</select>	 </td>

	  
	    <td width="178">Product Type * <br>
	<select name="pt" onChange="this.form.submit()" disabled=1>
	            <option value=""> </option>
	            <? SelecTable($db,'TIPO_PRODUCTOS','ID_TIPO_PRODUCTO','TIPO_PRODUCTO',"",$rs["ID_TIPO_PRODUCTO"]) ?> 
	</select>	    </td>

	    <td colspan="4">Sub Type * <br>
	<select name="st">
	            <option value=""></option>
	            <? SelecTable($db,'SUB_TIPO_PRODUCTOS','ID_SUB_TIPO_PRODUCTO','SUB_TIPO_PRODUCTO'," WHERE ID_TIPO_PRODUCTO=".$rs["ID_TIPO_PRODUCTO"],$rs["ID_SUB_TIPO_PRODUCTO"]) ?> 
	</select>	    </td>
	  </tr>
	 
	  <tr> 
   <td colspan=2>Size * <br>
	<select name="sz" style="font-size:11px;" onChange="cambia();">	
      <option value=""> </option>
       <? SelecTable($db,'SIZES','ID_SIZE','SIZE','',$rs["ID_SIZE"]) ?>
	</select>	 </td>

	    <td colspan="5">Product Name <br>
		<textarea cols=50 rows=3 name=product><?echo $rs["PRODUCTO"]?></textarea>	    </td>
	  </tr>
	  <tr> 
	  
	  	 <td>Stems per Bunch *<br>
	      <input type="text" name="stems_bunch" size="10"  value="<?echo $rs["STEMS_PER_BUNCH"]?>" onChange="cambia();">	    </td>
      

	    
	    
	    <td width="181">Bunches per box *<br>
	      <input type="text" name="bch_box" size="10" maxlength=3  value="<?echo $rs["BUNCHES_BOX"]?>" onChange="cambia();">	    </td>
		  <td width="30">Mark up * <br>
	      <label>
	      <input name="markup" type="text" id="markup" size="10" value="<?php echo $rs['LAST_MARKUP']; ?>" onChange="cambia();">
        </label></td>
		  <td width="30">Unit Cost * <br>
        <input name="unit_cost" type="text" id="unit_cost" size="10"  value="<? echo $rs["UNIT_COST"]; ?>" onChange="cambia();" ></td>
	    	    
	    <td width="214">Unit*<br>
	      <p>
	        <label>
	          <input name="unit" type="radio" value="S" <? if($rs["UNIT"]=='S'){ echo " checked ";} ?> onChange="cambia();" >
	          Stem</label>
	      
	        <label>
	          <input type="radio" name="unit" value="B" <? if($rs["UNIT"]=='B'){ echo " checked ";} ?> onChange="cambia();" >
	          Bunch</label>
	        <br>
        </p></td>
	    
	    
	  <td>Price Bunch *<br>
	      <input type="text" name="price" size="10" maxlenght=6  value="<?echo $rs["PRICE_BUNCH"]?>" readonly="TRUE">	    </td>
	  </tr>
 
	  <tr>

	    <td bgcolor=ffff00><b>QuickBooks CODE *<br>
	      <input type="text" name="qbcode" size="10"  value="<?echo $rs["QBCODE"]?>">	    </td>


		<td>
		<? if ($rs["COLOR_VARIETY_CHOICE"]==1) { $chk = " checked=on"; } ?>
		<input type="checkbox" name="dc" value="1" <?echo $chk?>> Designer Choice
		&nbsp;</td>
		
	<td colspan="4" >
		<? if ($rs["ID_PRODUCT_STATUS"]==1) { $chkx = " checked=on"; } ?>
		<input type="checkbox" name="status" value="1" <?echo $chkx?>><b> Active</b>
		&nbsp;
		&nbsp;		
		&nbsp;		&nbsp;		&nbsp;
		<select name="min_box" style='color:brown; font-weight:bold'>
  <option value="1">Minimal Box Order</option>
  <option value="1" <?if ($rs["minimal_box"]==1) echo "selected " ?>>1 Box Order</option>
  <option value="2" <?if ($rs["minimal_box"]==2) echo "selected " ?>>2 Boxes Order</option>
  <option value="3" <?if ($rs["minimal_box"]==3) echo "selected " ?>>3 Boxes Order</option>
  <option value="4" <?if ($rs["minimal_box"]==4) echo "selected " ?>>4 Boxes Order</option>
  <option value="5" <?if ($rs["minimal_box"]==5) echo "selected " ?>>5 Boxes Order</option>
  <option value="6" <?if ($rs["minimal_box"]==6) echo "selected " ?>>6 Boxes Order</option>
  <option value="7" <?if ($rs["minimal_box"]==7) echo "selected " ?>>7 Boxes Order</option>
  <option value="8" <?if ($rs["minimal_box"]==8) echo "selected " ?>>8 Boxes Order</option>
  <option value="9" <?if ($rs["minimal_box"]==9) echo "selected " ?>>9 Boxes Order</option>
  <option value="10" <?if ($rs["minimal_box"]==10) echo "selected " ?>>10 Boxes Order</option>
</select>		</td>
	 </tr>	
	</table>
	<p>
	<input type="button" style="font-weight:bold;color:white;background:red" value="Go Back" onClick="history.go(-1)">	
	  <input type="submit" name="Submit" value="Update &gt;&gt;">
	</p>	
	<input type=hidden name=ptx value="<?echo $id?>">
	<input type=hidden name=id value="<?echo $id?>">
	
	</form>
	<p><br>

<?
##################### LISTA DE PRODUCTOS COMUNES

 $spt = " AND P.ID_TIPO_PRODUCTO=".$rs["ID_TIPO_PRODUCTO"];
 $sst = " AND P.ID_SUB_TIPO_PRODUCTO=".$rs["ID_SUB_TIPO_PRODUCTO"];
 $ssz = " AND P.ID_SIZE=".$rs["ID_SIZE"];


$qry = "SELECT P.*,S.SIZE,TP.TIPO_PRODUCTO,ST.SUB_TIPO_PRODUCTO,M.MARKET FROM PRODUCTOS P, SIZES S, ".
"TIPO_PRODUCTOS TP, SUB_TIPO_PRODUCTOS ST, MARKETS M WHERE TP.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO AND ".
"ST.ID_SUB_TIPO_PRODUCTO=P.ID_SUB_TIPO_PRODUCTO AND P.ID_MARKET=M.ID_MARKET AND P.ID_SIZE=S.ID_SIZE $smk $spt $sst $ssz $ssx".
" ORDER BY P.ID_TIPO_PRODUCTO,P.ID_SUB_TIPO_PRODUCTO,P.ID_MARKET";
$rs = mysql_db_query($db,$qry);
//echo $qry;
echo mysql_error();

?>

<table cellpadding=3 cellspacing=1 border="0" bgcolor=cecece>
  <tr> 
	<th>ID</th>
	<th>Market</th>
    <th>Product</th>
    <th>Size</th>
    <th>Status</th>
    <th>Stems/Buch</th>
    <th>Bunches/Box</th>
    <th>Bunch Price</th>
    <th>Box Price</th>
  </tr>
<? while($dt = mysql_fetch_array($rs)) { 

	$sch ="";
	if ($dt["ID_PRODUCT_STATUS"]) { $sch = " checked=on"; }

?>
  
  <tr bgcolor=ffffff> 
	<td align=right><b><a href="edt_product.php?id=<?echo $dt["ID_PRODUCTO"]?>"><?echo $dt["ID_PRODUCTO"]?>&nbsp;</a></td>
	<td><?echo $dt["MARKET"]?></td>
    <td width=250><?echo $dt["TIPO_PRODUCTO"]?> / <?echo $dt["SUB_TIPO_PRODUCTO"]?><?if (trim($dt["PRODUCTO"])) { echo "<br>".$dt["PRODUCTO"];} ?> </td>
    <td><?echo $dt["SIZE"]?></td>
    <td align=center><input <?echo $sch?> type="checkbox" name="s<? echo $dt["ID_PRODUCTO"]?>" value="1" disabled=1></td>
    <td align=center><?echo $dt["STEMS_PER_BUNCH"]?></td>
    <td align=center><?echo $dt["BUNCHES_BOX"]?></td>
    <td align=right>
      <b><?echo $dt["PRICE_BUNCH"]?>
      <input type=hidden name=id[] value="<?echo $dt["ID_PRODUCTO"]?>">
    </td>    
    <td align=right><font color=brown><b>$<?echo number_format($dt["BUNCHES_BOX"]*$dt["PRICE_BUNCH"],2,".",",")?></td>
  </tr>
<? } ?>  
</table>
<p>
           <form  method="post" action="./activate_market.php?source=<?php echo $PHP_SELF; ?>">

				<select name="market">
				           
				            <? SelecTable($db,'MARKETS','ID_MARKET','MARKET',"","") ?> 
				</select>
				<input name="producto" type="hidden" value="<?echo $id?>">
              <input type="submit" value="Add Market">
              
           </form>



	</center>
<br>
<? }
  

include('../end_admin.php');
  
  ?>

</body>
</html>
