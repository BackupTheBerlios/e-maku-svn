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

$consultarRequisicion=verRequisicion($id, $db);


	foreach ($consultarRequisicion as $row){
	    $id=$row['ID'];
		$fecha=$row['FECHA'];
		$solicitante=$row['SOLICITANTE'];
		$cantidad1=$row['CANTIDAD1'];
		$unidadmedida1=$row[UNIDADMEDIDA1];
		$descipcion1=$row['DESCRIPCION1'];
		$trabajo1=$row['TRABAJO1'];
		$ot1=$row['OT1'];
		$proveedor1=$row['PROVEEDOR1'];
		$precio1=$row['PRECIO1'];
		$cantidad2=$row['CANTIDAD2'];
		$unidadmedida2=$row[UNIDADMEDIDA2];
		$descipcion2=$row['DESCRIPCION2'];
		$trabajo2=$row['TRABAJO2'];
		$ot2=$row['OT2'];
		$proveedor2=$row['PROVEEDOR2'];
		$precio2=$row['PRECIO2'];
		$cantidad3=$row['CANTIDAD3'];
		$unidadmedida3=$row[UNIDADMEDIDA3];
		$descipcion3=$row['DESCRIPCION3'];
		$trabajo3=$row['TRABAJO3'];
		$ot3=$row['OT3'];
		$proveedor3=$row['PROVEEDOR3'];
		$precio3=$row['PRECIO3'];
		$cantidad4=$row['CANTIDAD4'];
		$unidadmedida4=$row[UNIDADMEDIDA4];
		$descipcion4=$row['DESCRIPCION4'];
		$trabajo4=$row['TRABAJO4'];
		$ot4=$row['OT4'];
		$proveedor4=$row['PROVEEDOR4'];
		$precio4=$row['PRECIO4'];
		$cantidad5=$row['CANTIDAD5'];
		$unidadmedida5=$row[UNIDADMEDIDA5];
		$descipcion5=$row['DESCRIPCION5'];
		$trabajo5=$row['TRABAJO5'];
		$ot5=$row['OT5'];
		$proveedor5=$row['PROVEEDOR5'];
		$precio5=$row['PRECIO5'];
		$cantidad6=$row['CANTIDAD6'];
		$unidadmedida6=$row[UNIDADMEDIDA6];
		$descipcion6=$row['DESCRIPCION6'];
		$trabajo6=$row['TRABAJO6'];
		$ot6=$row['OT6'];
		$proveedor6=$row['PROVEEDOR6'];
		$precio6=$row['PRECIO6'];
		$cantidad7=$row['CANTIDAD7'];
		$unidadmedida7=$row[UNIDADMEDIDA7];
		$descipcion7=$row['DESCRIPCION7'];
		$trabajo7=$row['TRABAJO7'];
		$ot7=$row['OT7'];
		$proveedor7=$row['PROVEEDOR7'];
		$precio7=$row['PRECIO7'];
		$cantidad8=$row['CANTIDAD8'];
		$unidadmedida8=$row[UNIDADMEDIDA8];
		$descipcion8=$row['DESCRIPCION8'];
		$trabajo8=$row['TRABAJO8'];
		$ot8=$row['OT8'];
		$proveedor8=$row['PROVEEDOR8'];
		$precio8=$row['PRECIO8'];
		$cantidad9=$row['CANTIDAD9'];
		$unidadmedida9=$row[UNIDADMEDIDA9];
		$descipcion9=$row['DESCRIPCION9'];
		$trabajo9=$row['TRABAJO9'];
		$ot9=$row['OT9'];
		$proveedor9=$row['PROVEEDOR9'];
		$precio9=$row['PRECIO9'];
		$autoriza=$row['AUTORIZA'];
				
	}
	

	?>

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
	echo $id;
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
					   
					  <table width="678" border="0" cellpadding="0" cellspacing="3" class="arial10prsrojabold">
                          <tr> 
                            <td width="100" class="arial10ptsgrisnormal">Fecha:
							<b> 
							<?
							echo $fecha;

							?></b>
							</td>
							
							<td class="arial10ptsgrisnormal" align="right" width="250">
							
							Solictado por: <b>
                           <?
						  
						   echo $solicitante; 
						   ?>
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
							<?
							echo $cantidad1;
							?>                               
                              </div></td>
                            <td><div align="center">
							<?
							echo $unidadmedida1;
							?>      
                             </div></td>
                            <td><div align="center">
                             <?
							 echo $descipcion1;
							 ?>
							</div></td>
                            <td><div align="center">
							<?
							echo $trabajo1;
							?>  
                              </div></td>
                            <td><div align="center">
							<?
							echo $ot1;
							?>
							   
                              </div></td>
                            <td><div align="center">
							<?
							echo $proveedor1;
							?>
							  
                              </div></td>
							   <td><div align="center">
							<?
							echo $precio1;
							?>   
                              </div></td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td><div align="center">
							<?
							echo $cantidad2;
							?>                               
                              </div></td>
                            <td><div align="center">
							<?
							echo $unidadmedida2;
							?>      
                             </div></td>
                            <td><div align="center">
                             <?
							 echo $descipcion2;
							 ?>
							</div></td>
                            <td><div align="center">
							<?
							echo $trabajo2;
							?>  
                              </div></td>
                            <td><div align="center">
							<?
							echo $ot2;
							?>
							   
                              </div></td>
                            <td><div align="center">
							<?
							echo $proveedor2;
							?>
							  
                              </div></td>
							   <td><div align="center">
							<?
							echo $precio2;
							?>   
                              </div></td>
                          </tr>
						  <tr> 
                             <td><div align="center">
							<?
							echo $cantidad3;
							?>                               
                              </div></td>
                            <td><div align="center">
							<?
							echo $unidadmedida3;
							?>      
                             </div></td>
                            <td><div align="center">
                             <?
							 echo $descipcion3;
							 ?>
							</div></td>
                            <td><div align="center">
							<?
							echo $trabajo3;
							?>  
                              </div></td>
                            <td><div align="center">
							<?
							echo $ot3;
							?>
							   
                              </div></td>
                            <td><div align="center">
							<?
							echo $proveedor3;
							?>
							  
                              </div></td>
							   <td><div align="center">
							<?
							echo $precio3;
							?>   
                              </div></td>
							  
                          </tr>
						  
						    <tr bgcolor="F5F4F4"> 
                            <td><div align="center">
							<?
							echo $cantidad4;
							?>                               
                              </div></td>
                            <td><div align="center">
							<?
							echo $unidadmedida4;
							?>      
                             </div></td>
                            <td><div align="center">
                             <?
							 echo $descipcion4;
							 ?>
							</div></td>
                            <td><div align="center">
							<?
							echo $trabajo4;
							?>  
                              </div></td>
                            <td><div align="center">
							<?
							echo $ot4;
							?>
							   
                              </div></td>
                            <td><div align="center">
							<?
							echo $proveedor4;
							?>
							  
                              </div></td>
							   <td><div align="center">
							<?
							echo $precio4;
							?>   
                              </div></td>
                          </tr>
						  <tr> 
                             <td><div align="center">
							<?
							echo $cantidad5;
							?>                               
                              </div></td>
                            <td><div align="center">
							<?
							echo $unidadmedida5;
							?>      
                             </div></td>
                            <td><div align="center">
                             <?
							 echo $descipcion5;
							 ?>
							</div></td>
                            <td><div align="center">
							<?
							echo $trabajo5;
							?>  
                              </div></td>
                            <td><div align="center">
							<?
							echo $ot5;
							?>
							   
                              </div></td>
                            <td><div align="center">
							<?
							echo $proveedor5;
							?>
							  
                              </div></td>
							   <td><div align="center">
							<?
							echo $precio5;
							?>   
                              </div></td>
                          </tr>
						     <tr bgcolor="F5F4F4"> 
                            <td><div align="center">
							<?
							echo $cantidad6;
							?>                               
                              </div></td>
                            <td><div align="center">
							<?
							echo $unidadmedida6;
							?>      
                             </div></td>
                            <td><div align="center">
                             <?
							 echo $descipcion6;
							 ?>
							</div></td>
                            <td><div align="center">
							<?
							echo $trabajo6;
							?>  
                              </div></td>
                            <td><div align="center">
							<?
							echo $ot6;
							?>
							   
                              </div></td>
                            <td><div align="center">
							<?
							echo $proveedor6;
							?>
							  
                              </div></td>
							   <td><div align="center">
							<?
							echo $precio6;
							?>   
                              </div></td>
                          </tr>
						  <tr> 
                             <td><div align="center">
							<?
							echo $cantidad7;
							?>                               
                              </div></td>
                            <td><div align="center">
							<?
							echo $unidadmedida7;
							?>      
                             </div></td>
                            <td><div align="center">
                             <?
							 echo $descipcion7;
							 ?>
							</div></td>
                            <td><div align="center">
							<?
							echo $trabajo7;
							?>  
                              </div></td>
                            <td><div align="center">
							<?
							echo $ot7;
							?>
							   
                              </div></td>
                            <td><div align="center">
							<?
							echo $proveedor7;
							?>
							  
                              </div></td>
							   <td><div align="center">
							<?
							echo $precio7;
							?>   
                              </div></td>
                          </tr>
						   <tr bgcolor="F5F4F4"> 
                           <td><div align="center">
							<?
							echo $cantidad8;
							?>                               
                              </div></td>
                            <td><div align="center">
							<?
							echo $unidadmedida8;
							?>      
                             </div></td>
                            <td><div align="center">
                             <?
							 echo $descipcion8;
							 ?>
							</div></td>
                            <td><div align="center">
							<?
							echo $trabajo8;
							?>  
                              </div></td>
                            <td><div align="center">
							<?
							echo $ot8;
							?>
							   
                              </div></td>
                            <td><div align="center">
							<?
							echo $proveedor8;
							?>
							  
                              </div></td>
							   <td><div align="center">
							<?
							echo $precio8;
							?>   
                              </div></td>
                          </tr>
						  <tr> 
                            <td><div align="center">
							<?
							echo $cantidad9;
							?>                               
                              </div></td>
                            <td><div align="center">
							<?
							echo $unidadmedida9;
							?>      
                             </div></td>
                            <td><div align="center">
                             <?
							 echo $descipcion9;
							 ?>
							</div></td>
                            <td><div align="center">
							<?
							echo $trabajo9;
							?>  
                              </div></td>
                            <td><div align="center">
							<?
							echo $ot9;
							?>
							   
                              </div></td>
                            <td><div align="center">
							<?
							echo $proveedor9;
							?>
							  
                              </div></td>
							   <td><div align="center">
							<?
							echo $precio9;
							?>   
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
			<table class="arial10ptsgrisnormal" >
				<tr align="right">
					<td>Autoriza: <? echo $autoriza; ?></td>
				</tr>
				
			</table>
		</td>
        </tr>
        <tr> 
          <td><img src="/img/img3.gif" width="700" height="19"></td>
        </tr>
      </table>
	  <table>
	  	<tr> 
					<td>
					<a href="javascript:window.print();"><img src="/img/imprimir.gif" border="0"></a>
					</td>
					<td><a href="/formas/parsing/generarRequisicion.php?id=<? echo $id; ?> "><img src="/img/excel.gif" border="0"></a></td>
		 		 </tr>
	  </table>
	  <br>
