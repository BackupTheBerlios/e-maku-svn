<?php
include('../../functions.php');
include('../head_admin.php');
include('./standing_process.php');
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
                	<td align="center" class="titulogrisprincipal">Orders Manager</td>
			  	</tr>  
             	<tr>
					<td><a href="wconfirmation.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton2','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton2" width="31" height="16" border="0"></a>&nbsp;<a href="wconfirmation.php" class="titulogris">Waiting Bank Confirmation</a> </td>
			  	</tr>  
             	 <tr>
					<td><a href="invoices.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton3','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton3" width="31" height="16" border="0"></a>&nbsp;<a href="invoices.php" class="titulogris">Invoice Pending</a></td>
		  		</tr>  
           	   <tr>
					<td><a href="delivery.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton4','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton4" width="31" height="16" border="0"></a>&nbsp;<a href="delivery.php" class="titulogris">Awaiting Delivery</a></td>
		       </tr>  
            	<tr>
					<td><a href="ship.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton5','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton5" width="31" height="16" border="0"></a>&nbsp;<a href="ship.php" class="titulogris">Delivered</a></td>
			  	</tr>  
      			<tr>
					<td><a href="./report_st_orders.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton6','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton6" width="31" height="16" border="0"></a>&nbsp;<a href="./report_st_orders.php" class="titulogris">&copy; Standing Orders</a></td>
			  	</tr>  
            	<tr>
					<td><a href="./main_report.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton7','','../../s/imgs/bola3.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton7" width="31" height="16" border="0"></a>&nbsp;<a href="./main_report.php" class="titulogris">Sales Report</a></td>
			  	</tr> 
				<tr>
					<td><a href="../reports.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton8','','../../s/imgs/bola2.gif',1)"><img src="../../s/imgs/bola1.gif" name="boton8" width="31" height="16" border="0"></a>&nbsp;<a href="../reports.php" class="titulogris">Go Back to Menu</a></td>
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
     
<? Standing_orders_process($database) ?>
</body>
</html>
