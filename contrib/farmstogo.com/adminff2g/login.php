<?

require_once("../utilidades/sesiones.php"); 	

terminarSesion();

@session_destroy();

?>

<html>

<head>

<title>flowerfarmstogo.com - admin</title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<link href="http://www.farmstogo.com/s/library/estilos.css" rel="stylesheet" type="text/css">

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



<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="MM_preloadImages('/img/enviar2.gif')">

<form name="form1" action="/forms/parsing/validarSesion.php" method="post">

<table width="780" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="#CCCCCC">

  <tr align="center"> 

    <td>&nbsp;</td>

  </tr>

  <tr> 

		<td><img src="../s/imgs/imatrsnp.gif" width="10" height="100"></td>

  </tr>

  <tr> 

    <td align="center"><img src="http://www.farmstogo.com/s/imgs/11.gif" border="0"></td>

  </tr>

    <tr> 

    <td>&nbsp;</td>

  </tr>

  <tr class="textoazulbold">

  	<td align="center">E-mail user</td>

  </tr>

  <tr> 

  <td align="center"><input type="text" name="email" class="combos"></td>

   </td>

  </tr>

  <tr class="textoazulbold">

  	<td align="center">Password</td>

  </tr>

  <tr>

	<td align="center"><input type="password" name="password" class="combos"></td>

  </tr>

   <tr> 

	  

	<td colspan="2">

	<br>

	<div align="center"><a href="#" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image18','','../s/imgs/enviar2.gif',1)" onClick="document.form1.submit();"><br>

	  <input type="image" src="../s/imgs/enviar.gif" name="Image18" width="94" height="21" border="0" onClick="document.form1.submit();" ></a></div></td>

</tr>

 </table>

</form>

</body>

</html>

