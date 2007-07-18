<?php
require_once("../utilidades/sesiones.php"); 	
validarSesion();
include('./head_admin.php');
if ($_SESSION['typeUser']==""){
	?>
		<script language="Javascript">
 			 window.location="http://www.flowerfarmstogo.com/adminff2g/login.php";
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

<BODY onLoad="MM_preloadImages('../s/imgs/bola3.gif')"> 


  <table border="0" width="780">
		<tr>
			<td>
		<table border="0" width="400" align="center">
			 <tr> 
				<td><img src="../s/imgs/imatrsnp.gif" width="10" height="50"></td>
 			 </tr>
			<tr> 
				<td class="textopeque" align="right">Hello, <? echo $_SESSION['name']; ?> </td>
 			 </tr>
			<tr>
  			     <td class="titulogrisprincipal" align="center">Administrative Suite</td>
			</tr> 
			
			<tr>
				<td><a href="morders/reports.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton2','','../s/imgs/bola3.gif',1)"><img src="../s/imgs/bola1.gif" name="boton2" width="31" height="16" border="0"></a>&nbsp;<a href="morders/reports.php" class="titulogris">Orders Manager (Admin, Invoicing, Delivery)</a></td>
			</tr>
			<? 
				//if ($_SESSION['typeUser']==1 or $_SESSION['typeUser']==3){ 
			?>
             <tr>
			 <td><a href="torders/take_orders.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton4','','../s/imgs/bola3.gif',1)"><img src="../s/imgs/bola1.gif" name="boton4" width="31" height="16" border="0"></a>&nbsp;<a href="torders/take_orders.php" class="titulogris">Take Phone Orders</a></td>
			</tr>
             <tr>
               <td><a href="torders/take_ordersso.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('999','','../s/imgs/bola3.gif',1)"><img src="../s/imgs/bola1.gif" alt="Take orders So" name="boton4" width="31" height="16" border="0"> Take Standing Orders (Temporary) </a></td>
             </tr>
             <tr>
			 <td><a href="torders/take_orders_nopublic.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton6','','../s/imgs/bola3.gif',1)"><img src="../s/imgs/bola1.gif" name="boton6" width="31" height="16" border="0"></a>&nbsp;<a href="torders/take_orders_nopublic.php" class="titulogris">Take Florist Phone Orders *</a></td>
			</tr>
			<tr>
				<td><a href="events/panel_events.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton10','','../s/imgs/bola3.gif',1)"><img src="../s/imgs/bola1.gif" name="boton10" width="31" height="16" border="0"></a>&nbsp;<a href="events/panel_events.php" class="titulogris">Events Report</a></td>
			</tr>
			<? 
				if ($_SESSION['typeUser']==1){ 
			?>
			<tr>
				<td><a href="sadmin/reports.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton8','','../s/imgs/bola3.gif',1)"><img src="../s/imgs/bola1.gif" name="boton8" width="31" height="16" border="0"></a>&nbsp;<a href="sadmin/reports.php" class="titulogris">System Administration (Products, Markets, Images, etc)</a></td>
			</tr>
            <tr>
				<td><a href="sadmin/users.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton11','','../s/imgs/bola3.gif',1)"><img src="../s/imgs/bola1.gif" name="boton11" width="31" height="16" border="0"></a>&nbsp;<a href="sadmin/users.php" class="titulogris">Administration</a></td>
			</tr>
			<?
				//}
			}
						
			?>
			<tr>
				<td><a href="login.php" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('boton11','','../s/imgs/bola3.gif',1)"><img src="../s/imgs/bola1.gif" name="boton11" width="31" height="16" border="0"></a>&nbsp;<a href="login.php" class="titulogris">Logout</a></td>
			</tr>
            <tr>
				<td>&nbsp;</td>
            </tr>
			</table>
			<table align="center">
			<tr >
				<td class="textopeque">Copyright 2000 Flower Farms To Go.com LLC All Rights Reserved </td>
            </tr>
			</table>
          </td>
        </tr>

</table>

</body>
</html>
