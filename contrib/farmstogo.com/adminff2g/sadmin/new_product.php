<? 
	session_start();
	include('../../functions.php'); 
   include('../head_admin.php'); 
   Titulo_Maestro("New Product");
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
if ($sz) {

	$CQuery = <<< EOP
INSERT INTO PRODUCTOS ( ID_SUB_TIPO_PRODUCTO , ID_TIPO_PRODUCTO , ID_SIZE , 
ID_MARKET , PRODUCTO , STEMS_PER_BUNCH , PRICE_BUNCH , BUNCHES_BOX , 
ID_PRODUCT_STATUS , COLOR_VARIETY_CHOICE, QBCODE, minimal_box,UNIT,UNIT_COST)  VALUES ('$st', '$pt', '$sz',
'$mk', '$product', $stems_bunch, '$price', '$bch_box',1,'$dc','$qbcode','$min_box','$unit',$unit_cost)

EOP;
	#echo $CQuery;
	mysql_db_query($db,$CQuery);


		
	if(mysql_error()) {	
		Alerta(mysql_error());
		History("1");
	} else {
		Alerta("Product created successfully");
		RetornarOL("new_product.php");
	}
	
}
else {
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
	  
   <td >Market * <br>
	<select name="mk" tabindex="1" >	
      <option value=""> </option>
       <? SelecTable($db,'MARKETS','ID_MARKET','MARKET','',$mk) ?>
	</select>	 </td>

	  
	    <td>Product Type * <br>
	<select name="pt" onChange="this.form.submit()" tabindex="2">
	            <option value=""> </option>
	            <? SelecTable($db,'TIPO_PRODUCTOS','ID_TIPO_PRODUCTO','TIPO_PRODUCTO',"",$pt) ?> 
	</select>	    </td>

	    <td colspan="4">Sub Type * <br>
	<select name="st" tabindex="3">
	            <option value=""></option>
	            <? SelecTable($db,'SUB_TIPO_PRODUCTOS','ID_SUB_TIPO_PRODUCTO','SUB_TIPO_PRODUCTO'," WHERE ID_TIPO_PRODUCTO=$pt ",$st) ?> 
	</select>	    </td>
	  </tr>
	 
	  <tr> 
   <td colspan=2>Size * <br>
	<select name="sz" style="font-size:11px;" tabindex="4" onChange="cambia();">	
      <option value=""> </option>
       <? SelecTable($db,'SIZES','ID_SIZE','SIZE_LONG_DESC','',$sz) ?>
	</select>	 </td>

	    <td colspan="5">Product Name <br>
	      <input type="text" name="product" size="30" tabindex="6">	    </td>
	  </tr>
	  <tr> 
	  
	  	 <td>Stems per Bunch *<br>
	      <input type="text" name="stems_bunch" size="10" onChange="cambia();" tabindex="7">	    </td> 
	    <td>Bunches per box *<br> 
	      <input type="text" name="bch_box" size="10" maxlength=3 onChange="cambia();" tabindex="8">	    </td>
<td>Mark up*<br>
	      <label>
	      <input name="markup" type="text" id="markup" size="10" tabindex="9">
        </label></td>
	    <td>Unit Cost * <br>
          <input name="unit_cost" type="text" id="unit_cost" size="10"  value="" tabindex="10" ></td>	    	    
	    <td>Unit*<br>
          <p>
            <label>
            <input name="unit" type="radio" value="S" onChange="cambia();" tabindex="11"  >
              Stem</label>
            <label>
            <input type="radio" name="unit" value="B"  onChange="cambia();" tabindex="12" >
              Bunch</label>
          </p></td>
	    
	  
	   <td>Price Bunch *<br>
	      <input type="text" name="price" size="10" maxlenght=6 readonly="true" tabindex="12">	    </td>
	  </tr>
 
	  <tr>

	    <td bgcolor=ffff00><b>QuickBooks CODE *<br>
	      <input type="text" name="qbcode" size="10" tabindex="13">	    </td>

		<td>
		<input type="checkbox" name="dc" value="1" tabindex="14"> Designer Choice
		&nbsp;</td>


		<td colspan=5>
		<select name="min_box" style='color:brown; font-weight:bold' tabindex="15">
  <option value="1">Minimal Box Order</option>
  <option value="1">1 Box Order</option>
  <option value="2">2 Boxes Order</option>
  <option value="3">3 Boxes Order</option>
  <option value="4">4 Boxes Order</option>
  <option value="5">5 Boxes Order</option>
  <option value="6">6 Boxes Order</option>
  <option value="7">7 Boxes Order</option>
  <option value="8">8 Boxes Order</option>
  <option value="9">9 Boxes Order</option>
  <option value="10">10 Boxes Order</option>
</select></td>
	 </tr>	
	</table>
	<p>
<input type="button" style="font-weight:bold;color:white;background:red" value="Go Back" onClick="history.go(-1)">	
	  <input type="submit" name="Submit" value="Send &gt;&gt;">
	</p>	
	
	</form>
	</center>

<? }
  
include('../end_admin.php');
  
  ?>

</body>
</html>
