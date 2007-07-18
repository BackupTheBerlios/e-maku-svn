<?
if ($flag_page == "rosefarms") {

	session_start();
	
$ch = curl_init();
$timeout = 0; // set to zero for no timeout
curl_setopt ($ch, CURLOPT_URL, 'http://www.rosefarmstogo.com/s/include/head.php');
curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt ($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
$file_contents = curl_exec($ch);
//curl_close($ch);
echo $file_contents;

//$ch = curl_init();
//$timeout = 0; // set to zero for no timeout
//curl_setopt ($ch, CURLOPT_URL, 'http://www.rosefarmstogo.com/s/include/checkout.php');
//curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
//curl_setopt ($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
//$file_contents = curl_exec($ch);
//curl_close($ch);
//echo $file_contents;

include ('../include/checkout.php'); 

	//$ch = curl_init();
	//curl_setopt ($ch, CURLOPT_URL, "http://www.rosefarmstogo.com/s/include/head.php");
	//curl_setopt ($ch, CURLOPT_HEADER, 0);
	//curl_exec($ch);
	//curl_setopt ($ch, CURLOPT_URL, "http://www.rosefarmstogo.com/s/include/checkout.php");
	//curl_setopt ($ch, CURLOPT_HEADER, 0);
	//curl_exec($ch);
	
	//include ('');



?>
<script>

</script>

<?php


 

if ($flag==1){
		$mail="";

}

if ($flag==3){
		$mail="";
}


if ($S_USER_EMAIL) { $mail = $S_USER_EMAIL; }

#Info del anterior script
##################################
	if ($session_id_real) {
	  # REGISTRAMOS VARIABLE PARA EL RESTO DEL PROCEDIMIENTO
	  if(!isset($_SESSION['session_id'])){
	  session_register("session_id");
	  }
	  $_SESSION['session_id'] = $session_id_real;

	}
	$ssl = 1;
	// Id del vendedor
	if(isset($_POST['vendedor'])) {
	  $idVendedor = $_POST['vendedor'];
	}
	
	if ($xfactura && !$id_factura) { $id_factura = $xfactura; }
#################################

if ($id_factura) { $nid_factura = " Order # $id_factura"; } 
?>
<p><b><big>Check Out Process <?=$nid_factura?></b><br>
</big>
<b><font color=brown>RoseFarmsToGo.com will save your 
billing information, but not your credit card number.</font>
<P>

<? if (!$mail)  { 
//echo "este es el email".$mail;
?>
	<script>
		function CkEmail(em) {
			var x='';
			x = document.m.mail.value.search("[^*].*[@][^*].*[.][^*].");
			if (x == -1) {
				alert('The email entered is not a valid email address !!');
				return false;
			}	
		}
	</script>

      <table border="0"  bgcolor=cecece cellpadding=3 cellspacing=1 align=center>
        <tr bgcolor=ffffff> 
		<form name=m action="bill_ship_info_1.php" onsubmit="return CkEmail()">
          <td valign=top>Please write your <b>e-mail</b></b></td>
          <td> 
            <input type="text" name="mail" size=30><br>
            
          </td>
          <td> 
			<?
			echo "<input type=\"hidden\" name=\"flag_page\" value=\"$flag_page\">\n";
				if ($phone) {
					if (!session_is_registered("xphone")) {
						session_register("xphone");						
					}
					$xphone=1;
				}
			?>
			<input type=hidden name=vendedor value="<?=$idVendedor?>">   
			 <?  if ($flag==1){?>
			
				<input type="hidden" name="flag" value="2">    
			<?  } ?> 
			 <?  if ($flag==3){?>
			
				<input type="hidden" name="flag" value="4">    
			<?  } ?> 
            <input type="submit" value=Retrive>
			
          </td>
        </tr></form>
      </table>


      <p>


<? } else  { 
	#Info de cliente recibida
	
	$rs = mysql_db_query($db,"SELECT * FROM CLIENTS WHERE rtrim(ltrim(CLIENT_EMAIL))='".trim($mail)."' ORDER BY confirmed desc, ID_CLIENT  LIMIT 1");
	
	if (mysql_numrows($rs)) {
		$sh = mysql_fetch_array($rs);
		$id_client=$sh[ID_CLIENT];
		mysql_db_query($db,"UPDATE CLIENTS SET confirmed=1 WHERE ID_CLIENT=$id_client");
		$cargar_st = 1;
		
	} else {
		# Crea el cliente
		mysql_db_query($db,"lock tables CLIENTS");
		mysql_db_query($db,"INSERT INTO CLIENTS (CLIENT_EMAIL,confirmed) VALUES ('".trim($mail)."',1)");
		echo mysql_error();
		$id_client = Getdata("CLIENTS","ID_CLIENT","WHERE CLIENT_EMAIL='$mail'",$db);
		mysql_db_query($db,"unlock tables");		
			
	}


##################### SHOPPING CART
?>
<script language="JavaScript">
<?
	js_padre_hija(COUNTRIES, ID_COUNTRY, COUNTRY, STATES, ID_COUNTRY, ID_STATE,  code, $db);
?>
	function Validar() {
		with (document.ca) {
			if (sfn.value=='' || sln.value=='' || scountry.selectedIndex==0 || sstate.selectedIndex==0 || sad.value=='' ||  sct.value=='' ||  szc.value=='' ||  sph1.value=='') {
				alert('[Shipping Information] All fields in bold are required !!!');
				return false;
			}	
			
			if (bfn.value=='' || bln.value=='' || bcountry.selectedIndex==0 || bstate.selectedIndex==0 || bad.value=='' ||  bct.value=='' ||  bzc.value=='' ||  bph1.value=='') {
				alert('[Billing Information] All fields in bold are required !!!');
				return false;
			}				
			
		}
	}
	
	
	function ShipInfo(x) {
		if (x==1) {
			with (document.ca) {
				sfn.value=bfn.value;
				sln.value=bln.value;
				sad.value=bad.value;
				sct.value=bct.value;
				szc.value=bzc.value;
				//sph.value=bph.value;		
				
				sph1.value = bph1.value;
				sph2.value = bph2.value;				
				sph3.value = bph3.value;
				
				scountry.value = bcountry.value;	
				CargarSTATES(document.ca.scountry.options[document.ca.scountry.selectedIndex].value,document.ca.sstate);						
				sstate.value = bstate.value;
			}		
		}
	}
		
</script>
<table border="0" cellpadding=0 cellspacing=0 align=center>
  <tr>
	<form name=ca  method=POST onsubmit="return Validar()" action="./bill_ship_info2.php">

        <input type=hidden name=id_client value="<?=$id_client?>">
		<input type=hidden name=id_factura value="<?=$id_factura?>">	
			<input type=hidden name=vendedor value="<?=$vendedor?>">     			

    <td align=center>
    

<!-- BILLING -->
      <table border="0" width="100%" cellpadding=3 cellspacing=1>
        <tr> 
          <td colspan="4"><b><font size=2 color=brown>Billing Information</b>                              
          <br><font size=1 color=black>(Credit Card, Check Account or Paypal Account Information)
          
            <hr noshade size=1>
          </td>
        </tr>
        <tr> 
          <td><b>First Name<br>
            <input type="text" name="bfn" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[CLIENT_NAME]?>">
            </b></td>
          <td><b>Last Name<br>
            <input type="text" name="bln" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[CLIENT_LAST_NAME]?>">
            </b></td>
          <td><b>Country <br>
<script>
<!--
	function requestFedexAccount(country) {
		alert("Please select one state");
		if (country==2) {
			window.open('alertdeliverydate.html','AlertDeliveryDate','width=500,height=200');
		}
		CargarSTATES(this.options[this.selectedIndex].value,document.ca.bstate);
	}
	
	function showalertdelivery() {
		window.open('alertdeliverydate.html','Alert Delivery Date','width=500,height=200');
	}	
-->
</script>		  
            <select name="bcountry" onchange="CargarSTATES(this.options[this.selectedIndex].value,document.ca.bstate);">               		
				<option value=""></option>
              	<? SelecTable($db,"COUNTRIES","ID_COUNTRY","COUNTRY","",$sh[ID_COUNTRY]) ?> 
            </select>
            </b></td>
          <td><b>State <br>
            <select name="bstate">
              <option value="0"></option>
            </select>
            </b></td>
        </tr>
        <tr> 
          <td><b>Address <br>
            <input type="text" name="bad" onkeyup="this.value=this.value.toUpperCase();" size="25" value="<?=$sh[CLIENT_ADDRESS]?>">
            </b></td>
          <td><b>City <br>
            <input type="text" name="bct" onkeyup="this.value=this.value.toUpperCase();" size="11" value="<?=$sh[CLIENT_CITY]?>">
            </b></td>
          <td><b>Zip Code<br>
            <input type="text" name="bzc" size="6" maxlength="11"  value="<?=$sh[CLIENT_ZIPCODE]?>">
            </b></td>
          <td colspan="2"><b>Phone Number<br>   
            <nobr><? Phone("bph", $sh[CLIENT_PHONE]); ?>       
            </b> </td>            
        </tr>
              
        
        <!--
        <tr> 

          <td colspan="2">Fax <br>
            <input type="text" name="bpe" size="10"  value="<?=$sh[CLIENT_FAX]?>">
          </td>
        </tr>
        --> 
        
        <tr> 

          <td><b>Email </b><font size=1>(Please check)<br>
            <input type="text" name="mail" size="25" value="<?=$mail?>" style='font-weight:bold'>
          </td>

          <td>Company <br>
            <input type="text" name="comp" size="25" onkeyup="this.value=this.value.toUpperCase();" value="<?=$sh[COMPANY]?>">
          </td>
          
          <td colspan=4><font color=brown>Retailer Number<br>
            <input type="text" name="ret" size="15"  value="<?=$sh[RETAILER_NUMBER]?>">
          </td>
                    
        </tr>  
       <tr>
	   	<td><table border="0" cellspacing="0" cellpadding="0" style="border:1px solid #f80542; border-collapse:collapse;">
  <tr>
    <td style="background-color:#F80542; font-size:x-small;">Warning!</td>
  </tr>
  <tr>
    <td><p align="justify">Giving false information and tax excemptions is penalized by law, please fax a copy of your certificate to 18778180610</p></td>
  </tr>
</table>

		</td>
	   </tr>         

      </table>


<!-- SHIPPING -->
<P>
		
      <table border="0" width="100%" cellpadding=3 cellspacing=1>
        <tr> 
          <td colspan="4"><b><font size=2 color=brown>Shipping Information</b>  
          
          &nbsp;&nbsp;&nbsp;&nbsp;
			<input type="checkbox" name="checkbox" onclick="ShipInfo(this.checked)">
			</b><font size=1 color=black><b>Same to Billing </b></font>
          
          
          <font size=1 color=black>
          <br>(Where we are going to deliver the package)
            <hr noshade size=1>
          </td>
        </tr>
        <tr> 
          <td><b>First Name<br>
            <input type="text" name="sfn" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[SHIPPING_NAME]?>">
            </b></td>
          <td><b>Last Name<br>
            <input type="text" name="sln" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[SHIPPING_LAST_NAME]?>">
            </b></td>
          <td><b>Country <br>
            <select name="scountry"  onchange="CargarSTATES(this.options[this.selectedIndex].value,document.ca.sstate);">
              <option value=""></option>
              <? SelecTable($db,"COUNTRIES","ID_COUNTRY","COUNTRY","",$sh[SHIPPING_ID_COUNTRY]) ?> 
            </select>
            </b></td>
          <td><b>State <br>
            <select name="sstate">
              <option value="0"></option>
            </select>
            </b></td>
        </tr>
        <tr> 
          <td><b>Address <br>
            <input type="text" name="sad" size="25" onkeyup="this.value=this.value.toUpperCase();" value="<?=$sh[SHIPPING_ADDRESS]?>">
            </b></td>
          <td><b>City <br>
            <input type="text" name="sct" onkeyup="this.value=this.value.toUpperCase();" size="11" value="<?=$sh[SHIPPING_CITY]?>">
            </b></td>
          <td><b>Zip Code<br>
            <input type="text" name="szc" size="6" maxlength="11"  value="<?=$sh[SHIPPING_ZIPCODE]?>">
            </b></td>
          <td colspan="2"><b>Phone Number<br>          
          <nobr><? Phone("sph", $sh[SHIPPING_PHONE]); ?>     
            </b> </td>            
        </tr>
        
        <!--
        <tr> 

          <td colspan="2">Fax <br>
            <input type="text" name="spe" size="10"  value="<?=$sh[SHIPPING_FAX]?>">
          </td>
        </tr>
        --> 
        <tr> 
          <td colspan="4" bgcolor=ffff77>Add a personalized message for $5.00 
            (please write the message below)<br>
            <textarea name="msg" rows="2" cols="60"><?=Getdata("flowerde_ff2gv.FACTURAS","message","WHERE ID_FACTURA='$id_factura'",$db)?></textarea>
          </td>
        </tr>
      </table>


<!-- PAYMENT -->
		<? #Sacando la fecha de entrega si es mas de 4 dias da todas las opcines de pago 
		#Hoy en timestam
		$today = mktime (0,0,0,date("m"),date("d"),date("Y"));
		$delivery = Getdata("SHOPPING_CART","DELIVERY_DATE","WHERE SESSION_ID='$session_id'",$db);
		$days = (int)($delivery - $today)/86400;
		?>
		<p>
		<? if ($flag==2){ ?>
			<table border="0" cellpadding=3 cellspacing=1 width=250>
				<tr>
					<td><input type="checkbox" name="servicePhone" value="1" class="combos" checked></td>
					<td class="textoboldnegro">Phone orders service charge</td>
				</tr>
			</table>
		<? } ?>
		<? if ($flag==4){ ?>
			<table border="0" cellpadding=3 cellspacing=1 width=250>
				<tr>
					<td><input type="checkbox" name="servicePhone" value="1" class="combos" ></td>
					<td class="textoboldnegro">Phone orders service charge</td>
				</tr>
			</table>
		<? } ?>
		
		      <table border="0" cellpadding=3 cellspacing=1 width=100%>
		        <tr> 
		          <td colspan="4"><b><font size=2 color=brown>Payment Method</b> 
		            <hr>
		          </td>
		        </tr>
		        <tr> 
		          <td><b><font size=2> 
		            <input type="radio" name="payment" value="CC" <? if (!$payment || $payment=='CC') { echo " checked=1";}?>>
		            Credit Card</td>
<!--Aca cambie 4 dias por 20 para que nunca salga e-check.-->		            
		          <? if ($days > 999) {  ?>
		            
					<td><b><font size=2>&nbsp;&nbsp;&nbsp;&nbsp;
					  <input type="radio" name="payment" value="EC" <? if ($payment=='EC') { echo " checked=1";}?>>
					  E-Check</td>
					  <!--
					<td><b><font size=2><nobr>&nbsp;&nbsp;&nbsp;&nbsp;
					  <input type="radio" name="payment" value="PP" <? if ($payment=='PP') { echo " checked=1";}?>>
					  Paypal</td> -->
				  <? } ?>
	          <td>
		            <input type="hidden" name="flag" value="<? echo $flag; ?>">
					<input type="submit" name="Submit" style='font-weight:bold;font-size:12px' value="Next &gt;">
		            </td>

		        </tr>
		      </table>

<? if ($days > 999) { /*
echo "Payment with Paypal and E-checks are subject to a 5 day approximate clearance period. 
Shipping will never be done prior to payment clearance. If you can’t delay delivery 
Of your order please make sure to use a credit card payment option.";
*/} ?>			     			     				     				    				     				      
		      
		    </td>
		  </tr></form>
		</table>

<script>
		CargarSTATES(document.ca.scountry.options[document.ca.scountry.selectedIndex].value,document.ca.sstate);
		CargarSTATES(document.ca.bcountry.options[document.ca.bcountry.selectedIndex].value,document.ca.bstate);
	<? if ($cargar_st) { ?>
		document.ca.bstate.value = <?=$sh[ID_STATE]?>;
		document.ca.sstate.value = <?=$sh[SHIPPING_ID_STATE]?>;
	<? } ?>
</script>


<?  } // if of email 

//	$ch = curl_init();
	//curl_setopt ($ch, CURLOPT_URL, "http://www.rosefarmstogo.com/s/include/end.php");
	//curl_setopt ($ch, CURLOPT_HEADER, 0);
	//curl_exec($ch);

$timeout = 0; // set to zero for no timeout
curl_setopt ($ch, CURLOPT_URL, 'http://www.rosefarmstogo.com/s/include/end.php');
curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt ($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
$file_contents = curl_exec($ch);
//curl_close($ch);
echo $file_contents;

//include ('../include/end.php'); 
curl_close ($ch);

}

// FLOWER FARMS TO GO
else {

	session_start();
	include ('../include/head.php');
	include ('../include/checkout.php');
	
	




 

if ($flag==1){
		$mail="";

}

if ($flag==3){
		$mail="";
}


if ($S_USER_EMAIL) { $mail = $S_USER_EMAIL; }

#Info del anterior script
##################################
	if ($session_id_real) {
	  if(!isset($_SESSION['session_id'])){
	  session_register("session_id");
	  }
	  $_SESSION['session_id'] = $session_id_real;
	  # REGISTRAMOS VARIABLE PARA EL RESTO DEL PROCEDIMIENTO
	  session_register("session_id");
	}
	$ssl = 1;
	// Id del vendedor
	if(isset($_POST['vendedor'])) {
	  $idVendedor = $_POST['vendedor'];
	}
	
	if ($xfactura && !$id_factura) { $id_factura = $xfactura; }
#################################

if ($id_factura) { $nid_factura = " Order # $id_factura"; } 
?>
<p><b><big>Check Out Process <?=$nid_factura?></b><br>
</big>
<b><font color=brown>FlowerFarmsToGo.com will save your 
billing information, but not your credit card number.</font>
<P>

<? if (!$mail)  { 
?>
	<script>
		function CkEmail(em) {
			var x='';
			x = document.m.mail.value.search("[^*].*[@][^*].*[.][^*].");
			if (x == -1) {
				alert('The email entered is not a valid email address !!');
				return false;
			}	
		}
	</script>

      <table border="0"  bgcolor=cecece cellpadding=3 cellspacing=1 align=center>
        <tr bgcolor=ffffff> 
		<form name=m action="bill_ship_info_Paula.php" onsubmit="return CkEmail()">
          <td valign=top>Please write your <b>e-mail</b></b></td>
          <td> 
            <input type="text" name="mail" size=30><br>
            
          </td>
          <td> 
			<?
				if ($phone) {
					if (!session_is_registered("xphone")) {
						session_register("xphone");						
					}
					$xphone=1;
				}
			?>
			<input type=hidden name=vendedor value="<?=$idVendedor?>">   
			 <?  if ($flag==1){?>
			
				<input type="hidden" name="flag" value="2">    
			<?  } ?> 
			 <?  if ($flag==3){?>
			
				<input type="hidden" name="flag" value="4">    
			<?  } ?> 
            <input type="submit" value=Retrieve>
          </td>
        </tr></form>
      </table>


      <p>


<? } else  { 
	#Info de cliente recibida
	
	$rs = mysql_db_query($db,"SELECT * FROM CLIENTS WHERE rtrim(ltrim(CLIENT_EMAIL))='".trim($mail)."' ORDER BY confirmed desc, ID_CLIENT  LIMIT 1");
	
	if (mysql_numrows($rs)) {
		$sh = mysql_fetch_array($rs);
		$id_client=$sh[ID_CLIENT];
		mysql_db_query($db,"UPDATE CLIENTS SET confirmed=1 WHERE ID_CLIENT=$id_client");
		$cargar_st = 1;
		
	} else {
		# Crea el cliente
		mysql_db_query($db,"lock tables CLIENTS");
		mysql_db_query($db,"INSERT INTO CLIENTS (CLIENT_EMAIL,confirmed) VALUES ('".trim($mail)."',1)");
		echo mysql_error();
		$id_client = Getdata("CLIENTS","ID_CLIENT","WHERE CLIENT_EMAIL='$mail'",$db);
		mysql_db_query($db,"unlock tables");		
			
	}


##################### SHOPPING CART
?>
<script language="JavaScript">
<?
	js_padre_hija(COUNTRIES, ID_COUNTRY, COUNTRY, STATES, ID_COUNTRY, ID_STATE,  code, $db);
?>
	function Validar() {
		with (document.ca) {
			if (sfn.value=='' || sln.value=='' || scountry.selectedIndex==0 || sstate.selectedIndex==0 || sad.value=='' ||  sct.value=='' ||  szc.value=='' ||  sph1.value=='') {
				alert('[Shipping Information] All fields in bold are required !!!');
				return false;
			}	
			
			if (bfn.value=='' || bln.value=='' || bcountry.selectedIndex==0 || bstate.selectedIndex==0 || bad.value=='' ||  bct.value=='' ||  bzc.value=='' ||  bph1.value=='') {
				alert('[Billing Information] All fields in bold are required !!!');
				return false;
			}				
			
		}
	}
	
	
	function ShipInfo(x) {
		if (x==1) {
			with (document.ca) {
				sfn.value=bfn.value;
				sln.value=bln.value;
				sad.value=bad.value;
				sct.value=bct.value;
				szc.value=bzc.value;
				//sph.value=bph.value;		
				
				sph1.value = bph1.value;
				sph2.value = bph2.value;				
				sph3.value = bph3.value;
				
				scountry.value = bcountry.value;	
				CargarSTATES(document.ca.scountry.options[document.ca.scountry.selectedIndex].value,document.ca.sstate);						
				sstate.value = bstate.value;
			}		
		}
	}
		
</script>
<table border="0" cellpadding=0 cellspacing=0 align=center>
  <tr>
	<form name=ca  method=POST onsubmit="return Validar()" action="./bill_ship_info2.php">

        <input type=hidden name=id_client value="<?=$id_client?>">
		<input type=hidden name=id_factura value="<?=$id_factura?>">	
			<input type=hidden name=vendedor value="<?=$vendedor?>">     			

    <td align=center>
    

<!-- BILLING -->
      <table border="0" width="100%" cellpadding=3 cellspacing=1>
        <tr> 
          <td colspan="4"><b><font size=2 color=brown>Billing Information</b>                              
          <br><font size=1 color=black>(Credit Card, Check Account or Paypal Account Information)
          
            <hr noshade size=1>
          </td>
        </tr>
        <tr> 
          <td><b>First Name<br>
            <input type="text" name="bfn" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[CLIENT_NAME]?>">
            </b></td>
          <td><b>Last Name<br>
            <input type="text" name="bln" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[CLIENT_LAST_NAME]?>">
            </b></td>
          <td><b>Country <br>
            <select name="bcountry"  onchange="CargarSTATES(this.options[this.selectedIndex].value,document.ca.bstate);">
              <option value=""></option>
              <? SelecTable($db,"COUNTRIES","ID_COUNTRY","COUNTRY","",$sh[ID_COUNTRY]) ?> 
            </select>
            </b></td>
          <td><b>State <br>
            <select name="bstate">
              <option value="0"></option>
            </select>
            </b></td>
        </tr>
        <tr> 
          <td><b>Address <br>
            <input type="text" name="bad" onkeyup="this.value=this.value.toUpperCase();" size="25" value="<?=$sh[CLIENT_ADDRESS]?>">
            </b></td>
          <td><b>City <br>
            <input type="text" name="bct" onkeyup="this.value=this.value.toUpperCase();" size="11" value="<?=$sh[CLIENT_CITY]?>">
            </b></td>
          <td><b>Zip Code<br>
            <input type="text" name="bzc" size="6" maxlength="11"  value="<?=$sh[CLIENT_ZIPCODE]?>">
            </b></td>
          <td colspan="2"><b>Phone Number<br>   
            <nobr><? Phone("bph", $sh[CLIENT_PHONE]); ?>       
            </b> </td>            
        </tr>
              
        
        <!--
        <tr> 

          <td colspan="2">Fax <br>
            <input type="text" name="bpe" size="10"  value="<?=$sh[CLIENT_FAX]?>">
          </td>
        </tr>
        --> 
        
        <tr> 

          <td><b>Email </b><font size=1>(Please check)<br>
            <input type="text" name="mail" size="25" value="<?=$mail?>" style='font-weight:bold'>
          </td>

          <td>Company <br>
            <input type="text" name="comp" size="25" onkeyup="this.value=this.value.toUpperCase();" value="<?=$sh[COMPANY]?>">
          </td>
          
          <td colspan=4><font color=brown>Retailer Number<br>
            <input type="text" name="ret" size="15"  value="<?=$sh[RETAILER_NUMBER]?>">
          </td>
                    
        </tr>  
                

      </table>


<!-- SHIPPING -->
<P>
		
      <table border="0" width="100%" cellpadding=3 cellspacing=1>
        <tr> 
          <td colspan="4"><b><font size=2 color=brown>Shipping Information</b>  
          
          &nbsp;&nbsp;&nbsp;&nbsp;
			<input type="checkbox" name="checkbox" onclick="ShipInfo(this.checked)">
			</b><font size=1 color=black><b>Same to Billing </b></font>
          
          
          <font size=1 color=black>
          <br>(Where we are going to deliver the package)
            <hr noshade size=1>
          </td>
        </tr>
        <tr> 
          <td><b>First Name<br>
            <input type="text" name="sfn" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[SHIPPING_NAME]?>">
            </b></td>
          <td><b>Last Name<br>
            <input type="text" name="sln" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[SHIPPING_LAST_NAME]?>">
            </b></td>
          <td><b>Country <br>
            <select name="scountry"  onchange="CargarSTATES(this.options[this.selectedIndex].value,document.ca.sstate);">
              <option value=""></option>
              <? SelecTable($db,"COUNTRIES","ID_COUNTRY","COUNTRY","",$sh[SHIPPING_ID_COUNTRY]) ?> 
            </select>
            </b></td>
          <td><b>State <br>
            <select name="sstate">
              <option value="0"></option>
            </select>
            </b></td>
        </tr>
        <tr> 
          <td><b>Address <br>
            <input type="text" name="sad" size="25" onkeyup="this.value=this.value.toUpperCase();" value="<?=$sh[SHIPPING_ADDRESS]?>">
            </b></td>
          <td><b>City <br>
            <input type="text" name="sct" onkeyup="this.value=this.value.toUpperCase();" size="11" value="<?=$sh[SHIPPING_CITY]?>">
            </b></td>
          <td><b>Zip Code<br>
            <input type="text" name="szc" size="6" maxlength="11"  value="<?=$sh[SHIPPING_ZIPCODE]?>">
            </b></td>
          <td colspan="2"><b>Phone Number<br>          
          <nobr><? Phone("sph", $sh[SHIPPING_PHONE]); ?>     
            </b> </td>            
        </tr>
        
        <!--
        <tr> 

          <td colspan="2">Fax <br>
            <input type="text" name="spe" size="10"  value="<?=$sh[SHIPPING_FAX]?>">
          </td>
        </tr>
        --> 
        <tr> 
          <td colspan="4" bgcolor=ffff77>Add a personalized message for $5.00
            (please write the message below)<br>
            <textarea name="msg" rows="2" cols="60"><?=Getdata("flowerde_ff2gv.FACTURAS","message","WHERE ID_FACTURA='$id_factura'",$db)?></textarea>
          </td>
        </tr>
      </table>


<!-- PAYMENT -->
		<? #Sacando la fecha de entrega si es mas de 4 dias da todas las opcines de pago 
		#Hoy en timestam
		$today = mktime (0,0,0,date("m"),date("d"),date("Y"));
		$delivery = Getdata("SHOPPING_CART","DELIVERY_DATE","WHERE SESSION_ID='$session_id'",$db);
		$days = (int)($delivery - $today)/86400;
		?>
		<p>
		<? if ($flag==2){ ?>
			<table border="0" cellpadding=3 cellspacing=1 width=250>
				<tr>
					<td><input type="checkbox" name="servicePhone" value="1" class="combos" checked></td>
					<td class="textoboldnegro">Phone orders service charge</td>
				</tr>
			</table>
		<? } ?>
		<? if ($flag==4){ ?>
			<table border="0" cellpadding=3 cellspacing=1 width=250>
				<tr>
					<td><input type="checkbox" name="servicePhone" value="1" class="combos" ></td>
					<td class="textoboldnegro">Phone orders service charge</td>
				</tr>
			</table>
		<? } ?>
		
		      <table border="0" cellpadding=3 cellspacing=1 width=100%>
		        <tr> 
		          <td colspan="4"><b><font size=2 color=brown>Payment Method</b> 
		            <hr>
		          </td>
		        </tr>
		        <tr> 
		          <td><b><font size=2> 
		            <input type="radio" name="payment" value="CC" <? if (!$payment || $payment=='CC') { echo " checked=1";}?>>
		            Credit Card</td>
		            
		          <? if ($days > 999) {  ?>
		            
					<td><b><font size=2>&nbsp;&nbsp;&nbsp;&nbsp;
					  <input type="radio" name="payment" value="EC" <? if ($payment=='EC') { echo " checked=1";}?>>
					  E-Check</td>
					  <!--
					<td><b><font size=2><nobr>&nbsp;&nbsp;&nbsp;&nbsp;
					  <input type="radio" name="payment" value="PP" <? if ($payment=='PP') { echo " checked=1";}?>>
					  Paypal</td> -->
				  <? } ?>
	          <td>
		            <input type="hidden" name="flag" value="<? echo $flag; ?>">
					<input type="submit" name="Submit" style='font-weight:bold;font-size:12px' value="Next &gt;">
		            </td>

		        </tr>
		      </table>

<? if ($days > 999) {  ?>
<b>
Payment with Paypal and E-checks are subject to a 5 day approximate clearance period. 
Shipping will never be done prior to payment clearance. If you can’t delay delivery 
Of your order please make sure to use a credit card payment option.
<? } ?>			     			     				     				    				     				      
		      
		    </td>
		  </tr></form>
		</table>

<script>

		CargarSTATES(document.ca.scountry.options[document.ca.scountry.selectedIndex].value,document.ca.sstate);
		CargarSTATES(document.ca.bcountry.options[document.ca.bcountry.selectedIndex].value,document.ca.bstate);
	<? if ($cargar_st) { ?>
		document.ca.bstate.value = <?=$sh[ID_STATE]?>;
		document.ca.sstate.value = <?=$sh[SHIPPING_ID_STATE]?>;
	<? } ?>
</script>


<?  } // if of email ?>

<? include ('../include/end.php'); 
}

?>