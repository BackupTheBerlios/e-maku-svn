<?php
/************************************************************************/
/*       	ISP															*/
/*______________________________________________________________________*/
/*			Investigación y Desarrollo									*/
/*                                                                      */
/*                    	TERRA LYCOS COLOMBIA                          	*/
/*        	Sistema de alimentación de TERRA COLOMBIA					*/
/************************************************************************/

/************************************************************************/
/* Definición de variables globales y librerías requeridas.				*/
/*______________________________________________________________________*/

require_once("../config/general.conf");		// Get the global variables
require_once("../config/bd.conf"); 			// Get the connection variables
require_once("../lib/database.php"); 							// Get the DataBase extended functions
require_once("../../consultas/funcionesSQL.php"); 	


// +-----------------------------------------------------------------------+
// |  Incializa las plantillas y establece la conexión con la BD		   |
// +-----------------------------------------------------------------------+
$db = DB::connect($dsn, DB_CONNECT_MODE); // Creates a database connection object in $db 
										  // or, a database error object if it went wrong.
										  // Lista de categorias principales
$db->setFetchMode(DB_FETCHMODE_ASSOC);	





requisicion ($solicitante,
					  $cantidad1, $unidadmedida1, $descipcion1, $trabajo1, $ot1, $proveedor1, $precio1,
					  $cantidad2, $unidadmedida2, $descipcion2, $trabajo2, $ot2, $proveedor2, $precio2,
					  $cantidad3, $unidadmedida3, $descipcion3, $trabajo3, $ot3, $proveedor3, $precio3,
					  $cantidad4, $unidadmedida4, $descipcion4, $trabajo4, $ot4, $proveedor4, $precio4,
					  $cantidad5, $unidadmedida5, $descipcion5, $trabajo5, $ot5, $proveedor5, $precio5,
					  $cantidad6, $unidadmedida6, $descipcion6, $trabajo6, $ot6, $proveedor6, $precio6,
					  $cantidad7, $unidadmedida7, $descipcion7, $trabajo7, $ot7, $proveedor7, $precio7,
					  $cantidad8, $unidadmedida8, $descipcion8, $trabajo8, $ot8, $proveedor8, $precio8,
					  $cantidad9, $unidadmedida9, $descipcion9, $trabajo9, $ot9, $proveedor9, $precio9,
					  $autoriza, $db);
					  

					  
					  
$maxId = maxIdRequisicion ($db);
?>
<script>
	alert ("La información ha sido almacenada, por favor verifique los datos ingresados");
</script>

<html>
<head>
<title>Horizonte Impresores Orden de Producción</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="/library/stiles.css" rel="stylesheet" type="text/css">
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
<table width="700" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr> 
    <td height="20">&nbsp;</td>
  </tr>
  <tr> 
    <td><img src="/img/logo.gif" width="212" height="84"></td>
  </tr>
    <tr>
	<td class="arial10prsrojaboldTitulo" align="right">No.
	<? 
	foreach ($maxId as $row){
		echo $row['max(ID)'];
		$id=$row['max(ID)'];
	}
	?>
	
	 Requisición de materiales</td>
  </tr>
  <tr> 
    <td width="700" height="17" valign="top"> 
	<table width="700" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td><img src="/img/img1.gif" width="700" height="17"></td>
        </tr>
        <tr> 
          <td width="700" valign="top" background="/img/img5.gif"> 
		    <table width="680" border="0" align="center" cellpadding="0" cellspacing="0">
              <tr> 
                <td><table width="680" border="0" align="left" cellpadding="0" cellspacing="1">
                    <tr> 
                    
                      <td width="277" valign="middle">
					   <form action="/formas/parsing/actualizarRequisicion.php" method="post">
					  <table width="678" border="0" cellpadding="0" cellspacing="3" class="arial10prsrojabold">
                          <tr> 
                            <td width="100" class="arial10ptsgrisnormal">Fecha:
							<b> 
							<?
							$appdate = date("Y m d");
							echo $appdate;

							?></b>
							</td>
							
							<td class="arial10ptsgrisnormal" align="right" width="250">
							
							Solictado por: <b>
                            <input type="text" name="solicitante" size="30" class="arial10ptsgrisnormal" value ="<? echo $solicitante; ?>">
							</td>
                          </tr>
                        </table></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td>&nbsp;</td>
              </tr>
              <tr> 
                <td> <table width="680" border="0" align="center" cellpadding="0" cellspacing="0" class="arial10prsrojabold">
                    <tr> 
                      <td width="590" height="19" valign="top">Clase de trabajo:</td>
                    </tr>
                    <tr> 
                      <td height="19" valign="top">
					  <table width="680" border="1" bordercolor="F5F4F4" align="center" cellpadding="0" cellspacing="2" class="arial10ptsgrisnormal">
                          <tr bgcolor="F5F4F4"> 
                            <td width="110"> <div align="center">Cantidad 
                                
                              </div></td>
                            <td width="92"> <div align="center">Unidad Medida 
                                
                              </div></td>
                            <td width="109"> <div align="center">Descripción 
                              
                              </div></td>
                            <td width="89"> <div align="center">Trabajo 
                               
                              </div></td>
                            <td width="93"> <div align="center">O.T. 
                                
                              </div></td>
                            <td width="83"> <div align="center">Proveedor 
                             
                              </div></td>
							  <td width="83"> <div align="center">Precio 
                             
                              </div></td>
                          </tr>
                          <tr> 
                            <td><div align="center">
							<input type="text" name="cantidad1" value ="<? echo $cantidad1; ?>" size="5" class="arial10ptsgrisnormal">                                
                              </div></td>
                            <td><div align="center">
							<input type="text" name="unidadmedida1" value ="<? echo $unidadmedida1; ?>" size="5" class="arial10ptsgrisnormal">   
                             </div></td>
                            <td><div align="center">
                             <textarea name="descipcion1"  cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $descipcion1; ?></textarea>   
							</div></td>
                            <td><div align="center">
							<textarea name="trabajo1" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $trabajo1; ?></textarea>  
                              </div></td>
                            <td><div align="center">
							<input type="text" name="ot1" value ="<? echo $ot1; ?>" size="5" class="arial10ptsgrisnormal">   
                              </div></td>
                            <td><div align="center">
							<input type="text" name="proveedor1" value ="<? echo $proveedor1; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
							   <td><div align="center">
							<input type="text" name="precio1" value ="<? echo $precio1; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                           <td><div align="center">
							<input type="text" name="cantidad2" value ="<? echo $cantidad2; ?>"  size="5" class="arial10ptsgrisnormal">                                
                              </div></td>
                            <td><div align="center">
							<input type="text" name="unidadmedida2" value ="<? echo $unidadmedida2; ?>"  size="5" class="arial10ptsgrisnormal">   
                             </div></td>
                            <td><div align="center">
                             <textarea name="descipcion2" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $descipcion2; ?></textarea>   
							</div></td>
                            <td><div align="center">
							<textarea name="trabajo2" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $trabajo2; ?></textarea>  
                              </div></td>
                            <td><div align="center">
							<input type="text" name="ot2" value ="<? echo $ot2; ?>"  size="5" class="arial10ptsgrisnormal">   
                              </div></td>
                            <td><div align="center">
							<input type="text" name="proveedor2" value ="<? echo $proveedor2; ?>"  size="10" class="arial10ptsgrisnormal">   
                              </div></td>
							    <td><div align="center">
							<input type="text" name="precio2" value ="<? echo $precio2; ?>"  size="10" class="arial10ptsgrisnormal">   
                              </div></td>
                          </tr>
						  <tr> 
                            <td><div align="center">
							<input type="text" name="cantidad3" value ="<? echo $cantidad3; ?>"  size="5" class="arial10ptsgrisnormal">                                
                              </div></td>
                            <td><div align="center">
							<input type="text" name="unidadmedida3" value ="<? echo $unidadmedida3; ?>"  size="5" class="arial10ptsgrisnormal">   
                             </div></td>
                            <td><div align="center">
                             <textarea name="descipcion3" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $descipcion3; ?></textarea>   
							</div></td>
                            <td><div align="center">
							<textarea name="trabajo3" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $trabajo3; ?></textarea>  
                              </div></td>
                            <td><div align="center">
							<input type="text" name="ot3" value ="<? echo $ot3; ?>"  size="5" class="arial10ptsgrisnormal">   
                              </div></td>
                            <td><div align="center">
							<input type="text" name="proveedor3" value ="<? echo $proveedor3; ?>"  size="10" class="arial10ptsgrisnormal">   
                              </div></td>
							   <td><div align="center">
							<input type="text" name="precio3" value ="<? echo $precio3; ?>"  size="10" class="arial10ptsgrisnormal">   
                              </div></td>
							  
                          </tr>
						  
						    <tr bgcolor="F5F4F4"> 
                           <td><div align="center">
							<input type="text" name="cantidad4" value ="<? echo $cantidad4; ?>" size="5" class="arial10ptsgrisnormal">                                
                              </div></td>
                            <td><div align="center">
							<input type="text" name="unidadmedida4" value ="<? echo $unidadmedida4; ?>" size="5" class="arial10ptsgrisnormal">   
                             </div></td>
                            <td><div align="center">
                             <textarea name="descipcion4" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $descipcion4; ?></textarea>   
							</div></td>
                            <td><div align="center">
							<textarea name="trabajo4" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $trabajo4; ?></textarea>  
                              </div></td>
                            <td><div align="center">
							<input type="text" name="ot4" value ="<? echo $ot4; ?>" size="5" class="arial10ptsgrisnormal">   
                              </div></td>
                            <td><div align="center">
							<input type="text" name="proveedor4" value ="<? echo $proveedor4; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
							   <td><div align="center">
							<input type="text" name="precio4" value ="<? echo $precio4; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
                          </tr>
						  <tr> 
                            <td><div align="center">
							<input type="text" name="cantidad5" value ="<? echo $cantidad5; ?>" size="5" class="arial10ptsgrisnormal">                                
                              </div></td>
                            <td><div align="center">
							<input type="text" name="unidadmedida5" value ="<? echo $unidadmedida5; ?>" size="5" class="arial10ptsgrisnormal">   
                             </div></td>
                            <td><div align="center">
                             <textarea name="descipcion5" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $descipcion5; ?></textarea>   
							</div></td>
                            <td><div align="center">
							<textarea name="trabajo5" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $trabajo5; ?></textarea>  
                              </div></td>
                            <td><div align="center">
							<input type="text" name="ot5" value ="<? echo $ot5; ?>" size="5" class="arial10ptsgrisnormal">   
                              </div></td>
                            <td><div align="center">
							<input type="text" name="proveedor5" value ="<? echo $proveedor5; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
							   <td><div align="center">
							<input type="text" name="precio5" value ="<? echo $precio5; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
                          </tr>
						     <tr bgcolor="F5F4F4"> 
                           <td><div align="center">
							<input type="text" name="cantidad6" value ="<? echo $cantidad6; ?>" size="5" class="arial10ptsgrisnormal">                                
                              </div></td>
                            <td><div align="center">
							<input type="text" name="unidadmedida6" value ="<? echo $unidadmedida6; ?>" size="5" class="arial10ptsgrisnormal">   
                             </div></td>
                            <td><div align="center">
                             <textarea name="descipcion6" cols="25"  rows="3" class="arial10ptsgrisnormal"><? echo $descipcion6; ?></textarea>   
							</div></td>
                            <td><div align="center">
							<textarea name="trabajo6" cols="25"  rows="3" class="arial10ptsgrisnormal"><? echo $trabajo6; ?></textarea>  
                              </div></td>
                            <td><div align="center">
							<input type="text" name="ot6" size="5" value ="<? echo $ot6; ?>"  class="arial10ptsgrisnormal">   
                              </div></td>
                            <td><div align="center">
							<input type="text" name="proveedor6" value ="<? echo $proveedor6; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
							   <td><div align="center">
							<input type="text" name="precio6" value ="<? echo $precio6; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
                          </tr>
						  <tr> 
                            <td><div align="center">
							<input type="text" name="cantidad7" value ="<? echo $cantidad7; ?>" size="5" class="arial10ptsgrisnormal">                                
                              </div></td>
                            <td><div align="center">
							<input type="text" name="unidadmedida7" value ="<? echo $unidadmedida7; ?>" size="5" class="arial10ptsgrisnormal">   
                             </div></td>
                            <td><div align="center">
                             <textarea name="descipcion7" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $descipcion7; ?></textarea>   
							</div></td>
                            <td><div align="center">
							<textarea name="trabajo7" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $trabajo7; ?></textarea>  
                              </div></td>
                            <td><div align="center">
							<input type="text" name="ot7" value ="<? echo $ot7; ?>" size="5" class="arial10ptsgrisnormal">   
                              </div></td>
                            <td><div align="center">
							<input type="text" name="proveedor7" value ="<? echo $proveedor7; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
							   <td><div align="center">
							<input type="text" name="precio7" value ="<? echo $precio7; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
                          </tr>
						   <tr bgcolor="F5F4F4"> 
                           <td><div align="center">
							<input type="text" name="cantidad8" value ="<? echo $cantidad8; ?>" size="5" class="arial10ptsgrisnormal">                                
                              </div></td>
                            <td><div align="center">
							<input type="text" name="unidadmedida8" value ="<? echo $unidadmedida8; ?>" size="5" class="arial10ptsgrisnormal">   
                             </div></td>
                            <td><div align="center">
                             <textarea name="descipcion8" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $descipcion8; ?></textarea>   
							</div></td>
                            <td><div align="center">
							<textarea name="trabajo8" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $trabajo8; ?></textarea>  
                              </div></td>
                            <td><div align="center">
							<input type="text" name="ot8" size="5" value ="<? echo $ot8; ?>" class="arial10ptsgrisnormal">   
                              </div></td>
                            <td><div align="center">
							<input type="text" name="proveedor8" value ="<? echo $proveedor8; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
							   <td><div align="center">
							<input type="text" name="precio8" value ="<? echo $precio8; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
                          </tr>
						  <tr> 
                            <td><div align="center">
							<input type="text" name="cantidad9" value ="<? echo $cantidad9; ?>" size="5" class="arial10ptsgrisnormal">                                
                              </div></td>
                            <td><div align="center">
							<input type="text" name="unidadmedida9" value ="<? echo $unidadmedida9; ?>" size="5" class="arial10ptsgrisnormal">   
                             </div></td>
                            <td><div align="center">
                             <textarea name="descipcion9" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $descipcion9; ?></textarea>   
							</div></td>
                            <td><div align="center">
							<textarea name="trabajo9" cols="25" rows="3" class="arial10ptsgrisnormal"><? echo $trabajo9; ?></textarea>  
                              </div></td>
                            <td><div align="center">
							<input type="text" name="ot9" size="5" value ="<? echo $ot9; ?>" class="arial10ptsgrisnormal">   
                              </div></td>
                            <td><div align="center">
							<input type="text" name="proveedor9" value ="<? echo $proveedor9; ?>" size="10" value ="<? echo $proveedor9; ?>" class="arial10ptsgrisnormal">   
                              </div></td>
							   <td><div align="center">
							<input type="text" name="precio9" value ="<? echo $precio9; ?>" size="10" class="arial10ptsgrisnormal">   
                              </div></td>
                          </tr>
						   
                        </table></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td><img src="/img/transp.gif" width="10" height="10"></td>
              </tr>
             
            </table>
			<table class="arial10ptsgrisnormal" align="right">
				<tr>
					<td>Autoriza: <input type="text" name="autoriza" value ="<? echo $autoriza; ?>" size="30" class="arial10ptsgrisnormal"></td>
				</tr>
				<tr align="center"> 
								<td colspan="2"><div align="center"><a href="#" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image19','','/img/actualizar2.gif',1)"><br>
								<input type="image" src="/img/actualizar.gif" name="Image19" width="94" height="21" border="0"></a></div>
								<input type="hidden" name="id" value="<? echo $id; ?>">
								</td>
						  </tr>
			</table>
		</td>
        </tr>
        <tr> 
          <td><img src="/img/img3.gif" width="700" height="19"></td>
        </tr>
      </table>
	  </form>