<?
if ($flag_page == "rosefarms") {
	session_start();
	$ch = curl_init();
	$timeout = 0; // set to zero for no timeout
	curl_setopt ($ch, CURLOPT_URL, 'http://www.rosefarmstogo.com/s/include/head.php');
	curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt ($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
	$file_contents = curl_exec($ch);
	echo $file_contents;
	include ('../include/checkout.php'); 
	?>
	<script>
		<!--
		function requestFedexAccount(country) {
				alert(ca.fedexaccount.style.visibility);
			if (country==2) {
				//document.m.fedexaccount.style.visibility = "hidden";
				alert(ca.fedexaccount.style.visibility);
			}
			CargarSTATES(country,document.ca.bstate);
			CargarSTATES(country,document.ca.sstate);
		}	
		-->
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
		if(!isset($_SESSION['session_id'])) {
			session_register("session_id");
		}
		$_SESSION['session_id'] = $session_id_real;
	}
	$ssl = 1;
	// Id del vendedor
	if(isset($_POST['vendedor'])) {
		$idVendedor = $_POST['vendedor'];
	}
	
	if ($xfactura && !$id_factura) { 
		$id_factura = $xfactura; 
	}
	
	#################################
	if ($id_factura) { 
		$nid_factura = " Order # $id_factura"; 
	} 
	?>
	<p><b><big>Check Out Process <?=$nid_factura?></b><br>
	</big>
	<b><font color="brown">RoseFarmsToGo.com will save your billing information, but not your credit card number.</font>
	<P>
	<? 
	if (!$mail)  { 
		//echo "este es el email".$mail;
		?>
		<script>
			<!--
			function CkEmail(em) {
				var x='';
				x = document.m.mail.value.search("[^*].*[@][^*].*[.][^*].");
				if (x == -1) {
					alert('The email entered is not a valid email address !!');
					return false;
				}	
			}
			-->
		</script>

	<form name="m" action="bill_ship_info_1.php" onsubmit="return CkEmail()">
			<table border="0"  bgcolor="#cecece" cellpadding="3" cellspacing="1" align="center">
				<tr bgcolor="#ffffff"> 
					<td valign=top>Please write your <b>e-mail</b></b></td>
					<td><input type="text" name="mail" size=30><br></td>
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
						<?  
						if ($flag==1) {
							?>			
							<input type="hidden" name="flag" value="2">    
							<?  
						} 
						if ($flag==3) {
							?>			
							<input type="hidden" name="flag" value="4">    
							<?  
						} 
						?> 
						<input type="submit" value="Retrive">		
					</td>
				</tr>
			</table>
	</form>
		<p>
	<? 
	} else  { 
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
			<!--
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
			-->	
		</script>
		<form name="ca"  method="POST" onsubmit="return Validar()" action="./bill_ship_info2.php">	
    	    <input type="hidden" name="id_client" value="<?=$id_client?>">
			<input type="hidden" name="id_factura" value="<?=$id_factura?>">	
			<input type="hidden" name="vendedor" value="<?=$vendedor?>">     			
			<table border="0" cellpadding="0" cellspacing="0" align="center">		
				<tr>
				    <td align="center">  
						<!-- BILLING -->
						<table border="0" width="100%" cellpadding="3" cellspacing="1">
							<tr> 
								<td colspan="4">
									<b><font size="2" color=brown>Billing Information</b>                              
									<br><font size="1" color=black>(Credit Card, Check Account or Paypal Account Information)         
									<hr noshade size=1>
								</td>
							</tr>
							<tr> 
								<td>
									<b>First Name<br>
									<input type="text" name="bfn" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[CLIENT_NAME]?>"></b>
								</td>
								<td>
									<b>Last Name<br>
									<input type="text" name="bln" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[CLIENT_LAST_NAME]?>"></b>
								</td>
								<td>
									<b>Country <br>
									<select name="bcountry" onchange="requestFedexAccount(this.options[this.selectedIndex].value);">               		
										<option value=""></option>
										<? SelecTable($db,"COUNTRIES","ID_COUNTRY","COUNTRY","",$sh[ID_COUNTRY]) ?> 
									</select>
									</b>
								</td>
								<td>
									<b>State <br>
									<select name="bstate">
										<option value="0"></option>
									</select>
									</b>
								</td>
							</tr>
							<tr> 
								<td>
									<b>Address <br>
									<input type="text" name="bad" onkeyup="this.value=this.value.toUpperCase();" size="25" value="<?=$sh[CLIENT_ADDRESS]?>"></b>
								</td>
								<td>
									<b>City <br>
									<input type="text" name="bct" onkeyup="this.value=this.value.toUpperCase();" size="11" value="<?=$sh[CLIENT_CITY]?>"></b>
								</td>
								<td>
									<b>Zip Code<br>
									<input type="text" name="bzc" size="6" maxlength="11"  value="<?=$sh[CLIENT_ZIPCODE]?>"></b>
								</td>
								<td colspan="2">
									<b>Phone Number<br>   
									<nobr><?php Phone("bph", $sh[CLIENT_PHONE]); ?>       
									</b>
								</td>            
							</tr>            
							<tr> 
								<td>
									<b>Email </b><font size=1>(Please check)<br>
									<input type="text" name="mail" size="25" value="<?=$mail?>" style='font-weight:bold'>
								</td>
								<td>
									Company <br>
									<input type="text" name="comp" size="25" onkeyup="this.value=this.value.toUpperCase();" value="<?=$sh[COMPANY]?>">
								</td>          
								<td colspan=4>
									<font color=brown>Retailer Number<br>
									<input type="text" name="ret" size="15"  value="<?=$sh[RETAILER_NUMBER]?>">
								</td>                    
							</tr>  
							<tr>
								<td>
									<table border="0" cellspacing="0" cellpadding="0" style="border:1px solid #f80542; border-collapse:collapse;">
										<tr>
											<td style="background-color:#F80542; font-size:x-small;">Warning!</td>
										</tr>
										<tr>
											<td>
												<p align="justify">
												Giving false information and tax excemptions is penalized by law, please fax a copy of your certificate to 18778180610
												</p>
											</td>
										</tr>
									</table>
								</td>
							</tr>         
						</table>
						<!-- SHIPPING -->
						<P>		
						<table border="0" width="100%" cellpadding=3 cellspacing=1>
							<tr> 
								<td colspan="4">
									<b><font size="2" color="brown">Shipping Information</b>            
									&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="checkbox" name="checkbox" onclick="ShipInfo(this.checked)">
									</b>
									<font size="1" color="black"><b>Same to Billing </b></font><br>
									<font size="1" color="black">(Where we are going to deliver the package)
									<hr noshade size=1>
								</td>
							</tr>
							<tr> 
								<td>
									<b>First Name<br>
									<input type="text" name="sfn" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[SHIPPING_NAME]?>"></b>
								</td>
								<td>
									<b>Last Name<br>
									<input type="text" name="sln" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[SHIPPING_LAST_NAME]?>"></b>
								</td>
								<td>
									<b>Country<br>
									<select name="scountry"  onchange="requestFedexAccount(this.options[this.selectedIndex].value);">
										<option value=""></option>
										<? SelecTable($db,"COUNTRIES","ID_COUNTRY","COUNTRY","",$sh[SHIPPING_ID_COUNTRY]) ?> 
									</select>
									</b>
								</td>
								<td>
									<b>State <br>
									<select name="sstate">
										<option value="0"></option>
									</select>
									</b>
								</td>
							</tr>
							<tr> 
								<td>
									<b>Address <br>
									<input type="text" name="sad" size="25" onkeyup="this.value=this.value.toUpperCase();" value="<?=$sh[SHIPPING_ADDRESS]?>"></b>
								</td>
								<td>
									<b>City <br>
									<input type="text" name="sct" onkeyup="this.value=this.value.toUpperCase();" size="11" value="<?=$sh[SHIPPING_CITY]?>"></b>
								</td>
								<td>
									<b>Zip Code<br>
									<input type="text" name="szc" size="6" maxlength="11"  value="<?=$sh[SHIPPING_ZIPCODE]?>"></b>
								</td>
								<td colspan="2">
									<b>Phone Number<br>          
									<nobr><?php Phone("sph", $sh[SHIPPING_PHONE]); ?></b>
								</td>            
							</tr>
							<tr> 
								<td colspan="4" bgcolor="ffff77">
									Add a personalized message for $5.00 (please write the message below)<br>
									<textarea name="msg" rows="2" cols="60">
										<?=Getdata("flowerde_ff2gv.FACTURAS","message","WHERE ID_FACTURA='$id_factura'",$db)?>
									</textarea>
								</td>
							</tr>
						</table>
						<!-- PAYMENT -->
						<?php
						#Sacando la fecha de entrega si es mas de 4 dias da todas las opcines de pago 
						#Hoy en timestam
						$today = mktime (0,0,0,date("m"),date("d"),date("Y"));
						$delivery = Getdata("SHOPPING_CART","DELIVERY_DATE","WHERE SESSION_ID='$session_id'",$db);
						$days = (int)($delivery - $today)/86400;
						if ($flag==2) { ?>				
							<p>
							<table border="0" cellpadding=3 cellspacing=1 width=250>
								<tr>
									<td><input type="checkbox" name="servicePhone" value="1" class="combos" checked></td>
									<td class="textoboldnegro">Phone orders service charge</td>
								</tr>
							</table>
						<?php
						}
				
						if ($flag==4) { ?>
							<table border="0" cellpadding=3 cellspacing=1 width=250>
								<tr>
									<td><input type="checkbox" name="servicePhone" value="1" class="combos" ></td>
									<td class="textoboldnegro">Phone orders service charge</td>
								</tr>
							</table>
							<?php 
						} 
						?>		

						<table border="0" cellpadding=3 cellspacing=1 width=100%>
							<tr> 
								<td colspan="4"><b><font size="2" color="brown">Payment Method</b> <hr>
								</td>
							</tr>
							<tr> 
								<td>
									<b><font size="2"> 
									<input type="radio" name="payment" value="CC" <? if (!$payment || $payment=='CC') { echo " checked=1";}?>>Credit Card
								</td>
								<!--Aca cambie 4 dias por 20 para que nunca salga e-check.-->		            
				          		<?php
								if ($days > 999) {  ?>		            
									<td>
										<b><font size="2">&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="radio" name="payment" value="EC" <? if ($payment=='EC') { echo " checked=1";}?>>E-Check
									</td>
									<?php 
								} 
								?>
								<td>
									<input type="hidden" name="flag" value="<? echo $flag; ?>">
									<input type="submit" name="Submit" style='font-weight:bold;font-size:12px' value="Next &gt;">
				            	</td>
							</tr>
						</table>
						<?php
						if ($days > 999) { 
						/*
						echo "Payment with Paypal and E-checks are subject to a 5 day approximate clearance period. 
						Shipping will never be done prior to payment clearance. If you can’t delay delivery 
						Of your order please make sure to use a credit card payment option.";
					*/
					} 
					?>			     			     				     				    				     				      		      
				</td>
			</tr>
		</table>
		</form>

		<script>
			<!--
			CargarSTATES(document.ca.scountry.options[document.ca.scountry.selectedIndex].value,document.ca.sstate);
			CargarSTATES(document.ca.bcountry.options[document.ca.bcountry.selectedIndex].value,document.ca.bstate);
			<?php
			if ($cargar_st) { 
				?>
				document.ca.bstate.value = <?=$sh[ID_STATE]?>;
				document.ca.sstate.value = <?=$sh[SHIPPING_ID_STATE]?>;
				<?php 
			} 
			?>
			-->
		</script>
		<?php
	} // end else of email 

	$timeout = 0; // set to zero for no timeout
	curl_setopt ($ch, CURLOPT_URL, 'http://www.rosefarmstogo.com/s/include/end.php');
	curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt ($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
	$file_contents = curl_exec($ch);
	echo $file_contents;

	curl_close ($ch);
} else { // FLOWER FARMS TO GO
	session_start();
	include ('../include/head.php');
	include ('../include/checkout.php');
	?>
	<script>
		<!--
		function requestFedexAccount(country,state) {
			if (country==2) {
	            document.getElementById('fedexaccount').style.display = "";
			} else {
	            document.getElementById('fedexaccount').style.display = "none";
			}
			CargarSTATES(country,document.ca.bstate);
			CargarSTATES(country,document.ca.sstate);
		}	
		-->
	</script>
	<?php
	if ($flag==1){
		$mail="";
	}

	if ($flag==3){
		$mail="";
	}


	if ($S_USER_EMAIL) { 
		$mail = $S_USER_EMAIL; 
	}

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
	
	if ($xfactura && !$id_factura) {
		$id_factura = $xfactura;
	}

	#################################
	if ($id_factura) { 
		$nid_factura = " Order # $id_factura"; 
	} 
	?>
	<p><b><big>Check Out Process <?=$nid_factura?></b></big><br>
	<b><font color=brown>FlowerFarmsToGo.com will save your billing information, but not your credit card number.</font>
	<P>
	<?php 
	if (!$mail)  { 
		?>
		<script>
			<!--
			function CkEmail(em) {
				var x='';
				x = document.m.mail.value.search("[^*].*[@][^*].*[.][^*].");
				if (x == -1) {
					alert('The email entered is not a valid email address !!');
					return false;
				}	
			}
		</script>

	<form name="m" action="bill_ship_info.php" onsubmit="return CkEmail()">
        <div align="center">
            <input type="text" name="mail" size=30>
            <br>
            <input type=hidden name=vendedor value="<?=$idVendedor?>">   
      </div>
            <table border="0"  bgcolor="#FFFFFF" cellpadding="3" cellspacing="1" align="center">
		        <tr bgcolor="#ffffff"> 
        			<td valign=top>Please write your <b>e-mail</b></b></td>
					<td> 
						<?php
						if ($phone) {
							if (!session_is_registered("xphone")) {
								session_register("xphone");						
							}
							$xphone=1;
						}

						if ($flag==1) { ?>			
							<input type="hidden" name="flag" value="2">    
						<?php  
						}

						if ($flag==3) { ?>			
							<input type="hidden" name="flag" value="4">    
							<?php  
						} 
						?> 

						<input type="submit" value=Retrieve>
					</td>
				</tr>
	  </table>
	</form>
		<p>
		<?php
	} else  { #Info de cliente recibida
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
			<!--
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

						if (scountry.value==2) {
	            			document.getElementById('fedexaccount').style.display = "";
						} else {
				            document.getElementById('fedexaccount').style.display = "none";
						}
					}		
				}
			}
			-->		
		</script>

		<form name="ca"  method="POST" onsubmit="return Validar()" action="./bill_ship_info2.php">
			<input type="hidden" name="id_client" value="<?=$id_client?>">
			<input type="hidden" name="id_factura" value="<?=$id_factura?>">	
			<input type="hidden" name="vendedor" value="<?=$vendedor?>">     			
			<table border="0" cellpadding="0" cellspacing="0" align="center">
				<tr>
					<td align="center">
 						<!-- BILLING -->
						<table border="0" width="100%" cellpadding="3" cellspacing="1">
							<tr> 
								<td colspan="4">
									<b><font size=2 color=brown>Billing Information</b>                              
									<br><font size=1 color=black>(Credit Card, Check Account or Paypal Account Information)<hr noshade size=1>
								</td>
							</tr>
							<tr> 
								<td>
									<b>First Name<br>
									<input type="text" name="bfn" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[CLIENT_NAME]?>"></b>
								</td>
								<td>
									<b>Last Name<br>
									<input type="text" name="bln" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[CLIENT_LAST_NAME]?>"></b>
								</td>
								<td>
									<b>Country <br>
									<select name="bcountry"  onchange="CargarSTATES(this.options[this.selectedIndex].value,document.ca.bstate);">
										<option value=""></option>
										<?php SelecTable($db,"COUNTRIES","ID_COUNTRY","COUNTRY","",$sh[ID_COUNTRY]) ?> 
									</select>
									</b>
								</td>
								<td>
									<b>State <br>
									<select name="bstate">
										<option value="0"></option>
									</select>
									</b>
								</td>
							</tr>
							<tr> 
								<td>
									<b>Address <br>
									<input type="text" name="bad" onkeyup="this.value=this.value.toUpperCase();" size="25" value="<?=$sh[CLIENT_ADDRESS]?>">
									</b>
								</td>
								<td>
									<b>City <br>
									<input type="text" name="bct" onkeyup="this.value=this.value.toUpperCase();" size="11" value="<?=$sh[CLIENT_CITY]?>"></b>
								</td>
								<td>
									<b>Zip Code<br>
									<input type="text" name="bzc" size="6" maxlength="11"  value="<?=$sh[CLIENT_ZIPCODE]?>"></b>
								</td>
								<td colspan="2">
									<b>Phone Number<br>   
									<nobr><? Phone("bph", $sh[CLIENT_PHONE]); ?></b>
								</td>            
							</tr>
							<tr> 
								<td>
									<b>Email </b><font size=1>(Please check)<br>
									<input type="text" name="mail" size="25" value="<?=$mail?>" style='font-weight:bold'>
								</td>
								<td>
									Company <br>
									<input type="text" name="comp" size="25" onkeyup="this.value=this.value.toUpperCase();" value="<?=$sh[COMPANY]?>">
								</td>
								<td colspan=4>
									<font color=brown>Retailer Number<br>
									<input type="text" name="ret" size="15"  value="<?=$sh[RETAILER_NUMBER]?>">
								</td>                    
							</tr>  
						</table>
						<!-- SHIPPING -->
						<P>		
						<table border="0" width="100%" cellpadding=3 cellspacing=1>
							<tr> 
								<td colspan="4">
									<b><font size="2" color="brown">Shipping Information</b>&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="checkbox" name="checkbox" onclick="ShipInfo(this.checked)">
									<font size="1" color="black"><b>Same to Billing </b></font><br>
									<font size="1" color="black">(Where we are going to deliver the package)
									<hr noshade size="1">
								</td>
							</tr>
							<tr> 
								<td>
									<b>First Name<br>
									<input type="text" name="sfn" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[SHIPPING_NAME]?>"></b>
								</td>
								<td>
									<b>Last Name<br>
									<input type="text" name="sln" onkeyup="this.value=this.value.toUpperCase();" size="15" value="<?=$sh[SHIPPING_LAST_NAME]?>"></b>
								</td>
								<td>
									<b>Country <br>
									<select name="scountry"  onchange="requestFedexAccount(this.options[this.selectedIndex].value);">
										<option value=""></option>
										<?php SelecTable($db,"COUNTRIES","ID_COUNTRY","COUNTRY","",$sh[SHIPPING_ID_COUNTRY]) ?> 
									</select></b>
								</td>
								<td>
									<b>State <br>
									<select name="sstate">
										<option value="0"></option>
									</select>
									</b>
								</td>
							</tr>
							<tr> 
								<td>
									<b>Address <br>
									<input type="text" name="sad" size="25" onkeyup="this.value=this.value.toUpperCase();" value="<?=$sh[SHIPPING_ADDRESS]?>"></b>
								</td>
								<td>
									<b>City <br>
									<input type="text" name="sct" onkeyup="this.value=this.value.toUpperCase();" size="11" value="<?=$sh[SHIPPING_CITY]?>"></b>
								</td>
								<td>
									<b>Zip Code<br>
									<input type="text" name="szc" size="6" maxlength="11"  value="<?=$sh[SHIPPING_ZIPCODE]?>"></b>
								</td>
								<td colspan="2">
									<b>Phone Number<br><nobr>
									<?php Phone("sph", $sh[SHIPPING_PHONE]); ?></b>
								</td>            
							</tr>
							<tr id="fedexaccount" style="display:none">
								<td colspan="4">
									<br />
									<div align="justify">
									Canadian orders will have an approximate 10% import duties that will be collected by FedEx. To avoid delays in Customs 
									Clearance it is important that you establish a FedEx account number that we will be used  for Duty collection. 
									If you already have a FedEx account number please enter it  in the space provided  below or contact FedEx at 1800GOFEDEX or <a href="www.fedex.com">www.fedex.com</a> to 
									create your account. It is not necessary to establish a FedEx account  to order for  Canadian delivery but 
									expect you could experience delivery delays if you don't.</div>
									<br />
									<div align="center">
									<b>Fedex Account </b>
									<input type="text" name="sfaccount" id ="sfaccount" size="25" onkeyup="this.value=this.value.toUpperCase();" value="<?=$sh[FEDEX_ACCOUNT]?>" />
									</b>
									</div>
									<br />
								</td>
							</tr>        
							<tr> 
								<td colspan="4" bgcolor="#ffff77">
									Add a personalized message for $5.00 (please write the message below)<br>
									<textarea name="msg" rows="2" cols="60">
										<?=Getdata("flowerde_ff2gv.FACTURAS","message","WHERE ID_FACTURA='$id_factura'",$db)?>
									</textarea>
								</td>
							</tr>
						</table>
						<!-- PAYMENT -->
						<p>
						<?php
						# Sacando la fecha de entrega si es mas de 4 dias da todas las opcines de pago 
						# Hoy en timestam
						$today = mktime (0,0,0,date("m"),date("d"),date("Y"));
						$delivery = Getdata("SHOPPING_CART","DELIVERY_DATE","WHERE SESSION_ID='$session_id'",$db);
						$days = (int)($delivery - $today)/86400;
						if ($flag==2) { ?>
					  <table border="0" cellpadding=3 cellspacing=1 width=250>
								<tr>
									<td><input type="checkbox" name="servicePhone" value="1" class="combos" checked></td>
									<td class="textoboldnegro">Phone orders service charge</td>
								</tr>
					  </table>
						<?php
						} 

						if ($flag==4) { ?>
							<table border="0" cellpadding=3 cellspacing=1 width=250>
								<tr>
									<td><input type="checkbox" name="servicePhone" value="1" class="combos" ></td>	
									<td class="textoboldnegro">Phone orders service charge</td>
								</tr>
							</table>
						<?php
						} 
						?>
		
						<table border="0" cellpadding="3" cellspacing="1" width="100%">
							<tr> 
								<td colspan="4"><b><font size="2" color="brown">Payment Method</b><hr></td>
							</tr>
							<tr> 
								<td>
									<b><font size="2"> 
									<input type="radio" name="payment" value="CC" <? if (!$payment || $payment=='CC') { echo " checked=1";}?>> Credit Card
								</td>
								<?php 
								if ($days > 999) {  ?>		            
									<td>
										<b><font size="2">&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="radio" name="payment" value="EC" <? if ($payment=='EC') { echo " checked=1";}?>> E-Check
									</td>
									<? 
								} 
								?>
								<td>
									<input type="hidden" name="flag" value="<? echo $flag; ?>">
									<input type="submit" name="Submit" style='font-weight:bold;font-size:12px' value="Next &gt;">
								</td>
							</tr>
						</table>

						<?php 
						if ($days > 999) {  ?>
							<b>
							Payment with Paypal and E-checks are subject to a 5 day approximate clearance period. Shipping will never be done prior to payment 	
							clearance. If you can’t delay delivery Of your order please make sure to use a credit card payment option.
							<?php 
						}
						?>			     			     				     				    				     				      
					</td>
				</tr>			
			</table>
		</form>

		<script>
			<!--
			CargarSTATES(document.ca.scountry.options[document.ca.scountry.selectedIndex].value,document.ca.sstate);
			CargarSTATES(document.ca.bcountry.options[document.ca.bcountry.selectedIndex].value,document.ca.bstate);
			<?php
			if ($cargar_st) { 
				?>
				document.ca.bstate.value = <?=$sh[ID_STATE]?>;
				document.ca.sstate.value = <?=$sh[SHIPPING_ID_STATE]?>;
				<?php
			} 
			?>
			-->
		</script>
		<?php
	} // end else of email
	include ('../include/end.php'); 
}
?>