<?php
require_once("../../utilidades/sesiones.php");
require_once("../morders/standing_process.php"); 	
require_once("../../functions.php");
validarSesion();

include('../head_admin.php');
if(!($_SESSION["email"]=="miguels@flowerdealers.com" || $_SESSION["email"]=="compraflor@flowerdealers.com")) {
?>
	<script language="Javascript">
		alert("You are trying to enter a restricted site.");
		window.location="/adminff2g/reports.php";
	</script> 
<?
}
?>
<html>
<head>
<title>Flower Farms To Go .com</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script language="JavaScript" type="text/JavaScript">
<!--
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->
</script>
</head>

<body>
<table width="780" border="0" cellspacing="0" cellpadding="0" name="contenidos">
        <tr>
          <td>
            <table width="400" border="0" cellspacing="0" cellpadding="0" align="center">
            	<tr>
                	<td align="center" class="titulogrisprincipal">Administrative System</td>
			  	</tr>  
    			<tr>
					<td><a href="images_manager.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton1','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton1" width="31" height="16" border="0"></a>&nbsp;<a href="images_manager.php" class="titulogris">Add Images to Products</a></td>
			    </tr>
            	<tr>
					<td><a href="nt.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton2','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton2" width="31" height="16" border="0"></a>&nbsp;<a href="nt.php" class="titulogris">Add Testimonials</a></td>
			    </tr>
				<tr>
					<td><a href="nfaq.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton3','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton3" width="31" height="16" border="0"></a>&nbsp;<a href="nfaq.php" class="titulogris">Add FAQ's</a></td>
				</tr>
            	<tr>
					<td><a href="new_product.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton4','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton4" width="31" height="16" border="0"></a>&nbsp;<a href="new_product.php" class="titulogris">New Item Product</a></td>
			    </tr>
            	<tr>
					<td><a href="admin_markets.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton5','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton5" width="31" height="16" border="0"></a>&nbsp;<a href="admin_markets.php" class="titulogris">Admin Markets</a></td>
			    </tr>
            	<tr>
					<td><a href="change_market.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton5','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton5" width="31" height="16" border="0"></a>&nbsp;<a href="change_market.php" class="titulogris">Clone Markets</a></td>
			    </tr>
            	<tr>
					<td><a href="admin_sizes.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton6','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton6" width="31" height="16" border="0"></a>&nbsp;<a href="admin_sizes.php" class="titulogris">Admin Sizes</a></td>
			    </tr>
           		<tr>
					<td><a href="admin_products.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton7','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton7" width="31" height="16" border="0"></a>&nbsp;<a href="admin_products.php" class="titulogris">Admin Product Types</a></td>
			    </tr>
                <tr>
					<td><a href="admin_sproducts.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton8','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton8" width="31" height="16" border="0"></a>&nbsp;<a href="admin_sproducts.php" class="titulogris">Admin Subproducts</a></td>
				</tr>
            	<tr>
					<td><a href="update_prices.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton9','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton9" width="31" height="16" border="0"></a>&nbsp;<a href="update_prices.php" class="titulogris">&copy; Update Prices</a></td>
				</tr>
            	<tr>
            	  <td><a href="update_prices.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('999','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" alt="Admin items" name="boton9" width="31" height="16" border="0"></a>&nbsp;<a href="update_items.php" class="titulogris">&copy; Admin Items </a></td>
          	  </tr>
            	<tr>
					<td><a href="admin_promotions.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton10','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton10" width="31" height="16" border="0"></a>&nbsp;<a href="admin_promotions.php" class="titulogris">* Admin Promotions</a></td>
				</tr>
            	 <tr>
					<td><a href="admin_ctypes.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton11','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton11" width="31" height="16" border="0"></a>&nbsp;<a href="admin_ctypes.php" class="titulogris">&copy Admin Customer Type</a></td>
				</tr>
				 <tr>
					<td><a href="p_ct_products.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton12','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton12" width="31" height="16" border="0"></a>&nbsp;<a href="p_ct_products.php" class="titulogris">&copy Customer Type Vs. Products</a></td>
				</tr>
				 <tr>
					<td><a href="customer_list.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton13','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton13" width="31" height="16" border="0"></a>&nbsp;<a href="customer_list.php" class="titulogris">Customer List</a></td>
				</tr>
				 <tr>
					<td><a href="delivery_information.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton14','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton14" width="31" height="16" border="0"></a>&nbsp;<a href="delivery_information.php" class="titulogris">Delivery Information</a></td>
				</tr>
				<tr>
					<td><a href="../reports.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton15','','../../s/imgs/bola2.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton15" width="31" height="16" border="0"></a>&nbsp;<a href="../reports.php" class="titulogris">Go Back to Menu</a></td>
				</tr>
			</table>
			<table align="center">
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td class="textopeque">Copyright 2000 Flower Farms To Go.com LLC All Rights Reserved </td>
       		  </tr>
			</table>
          </td>
        </tr>

</table>




</body>
</html>
