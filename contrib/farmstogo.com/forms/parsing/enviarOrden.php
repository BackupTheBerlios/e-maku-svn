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

echo "aqui va!";
$fechaEntrega = $anoEntrega."/".$mesEntrega."/".$diaEntrega;
$insertarNuevaOrden =   insertarOrden ($cliente, $afiche, $calendario, $diplomas, $folleto, $volantes,
										$hablador, $libretas, $libro, $escarapelas, $papeleria,
										$membrete, $plegable, $revista, $sticker, $hojas, $sobres, $tarjetas, 
										$tarjeton, $referencia, $cantidad, $planchas, 
										$maquinasolna1, $maquinagto, $maquinaroland, $maquinasormz, $papel60x90, $papel70x100, 
										$pliegosPapel, $tamanoPapel, $tintasPapel, $pantonePapel, $paginasPapel, 
										$caratula60x90, $caratula70x100, $pliegosCaratula, $tamanoCaratula, $tintasCaratula,
										$pantoneCaratula, $paginasCaratula, 
										$interiores60x90, $interiores70x100, $pliegosInteriores, $tamanoInteriores, $tintasInteriores, 
										$pantoneInteriores, $paginasInteriores, 
										$peliculasnegativas, $peliculaspositivas, $peliculascolorkey, $peliculasduplicado,
										$peliculasarchivo, $peliculassuministran, $peliculaspd, $peliculascpt, 
										$acabadosmate1caras, $acabadosmate2caras,  $acabadosbrillante1caras,
										$acabadosbrillante2caras, $acabadosuvtotal1caras, $acabadosuvtotal2caras, 
										$acabadosuvparcial1caras, $acabadosuvparcial2caras,
										$brillolitografico, $repujado, $grafado, $estampado, $perforado, $troquelado, $numerado,
										$numeradodel, $numeradohasta, $plegadoalcentro, $cuerpos2, $cuerpos3, $cuerpos4,
										$otro, $encolado, $caballete, $pegado, $hotmelt, $cintadoblefaz, $ganchoplano,
										$colaminado, $anilladocolor, $anilladodobleo, $anilladoplastico,
										$empaquepapelkraft, $empaquecajasdecarton, $empaquebolsaplastica, $empaqueexportacion,
										$observaciones, $fechaEntrega, $elaboro, $db);
			
								

$maxId=maxId ($db);

foreach ($maxId as $row){
	echo $row['max(ID)'];
		$id=$row['max(ID)'];
}

$valoresOrden=seleccionarOrden ($id, $db);
foreach ($valoresOrden as $myrows){
	$cliente=$myrows['CLIENTE'];
	$afiche= $myrows['AFICHE'];
	$calendario= $myrows['CALENDARIO'];
	$diplomas= $myrows['DIPLOMAS'];
	$folleto= $myrows['FOLLETO'];
	$volantes=$myrows['VOLANTES'];
	$hablador= $myrows['HABLADOR'];
	$libretas= $myrows['LIBRETAS'];
	$libro= $myrows['LIBRO'];
	$escarapelas= $myrows['ESCARAPELAS'];
	$papeleria=$myrows['PAPELERIA'];
	$membrete= $myrows['MEMBRETE'];
	$plegable= $myrows['PLEGABLE'];
	$revista= $myrows['REVISTA'];
	$sticker= $myrows['STICKER'];
	$hojas= $myrows['HOJAS'];
	$sobres= $myrows['SOBRES'];
	$tarjetas= $myrows['TARJETAS'];
	$tarjeton=$myrows['TARJETON'];
	$referencia=$myrows['REFERENCIA'];
	$cantidad= $myrows['CANTIDAD'];
	$planchas=$myrows['PLANCHAS'];
	$maquinasolna1= $myrows['MAQUINASOLNA1'];
	$maquinagto= $myrows['MAQUINAGTO'];
	$maquinaroland= $myrows['MAQUINAROLAND'];
	$maquinasormz= $myrows['MAQUINASORMZ'];
	$papel60x90= $myrows['PAPEL60X90'];
	$papel70x100= $myrows['PAPEL70X100'];
	$pliegosPapel= $myrows['PLIEGOSPAPEL'];
	$tamanoPapel= $myrows['TAMANOPAPEL'];
	$tintasPapel= $myrows['TINTASPAPEL'];
	$pantonePapel= $myrows['PANTONEPAPEL'];
	$paginasPapel= $myrows['PAGINASPAPEL'];
	$caratula60x90= $myrows['CARATULA60X90'];
	$caratula70x100= $myrows['CARATULA70X100'];
	$pliegosCaratula= $myrows['PLIEGOSCARATULA'];
	$tamanoCaratula= $myrows['TAMANOCARATULA'];
	$tintasCaratula=$myrows['TINTASCARATULA'];
	$pantoneCaratula= $myrows['PANTONECARATULA'];
	$paginasCaratula= $myrows['PAGINASCARATULA'];
	$interiores60x90= $myrows['INTERIORES60X90'];
	$interiores70x100= $myrows['INTERIORES70X100'];
	$pliegosInteriores= $myrows['PLIEGOSINTERIORES'];
	$tamanoInteriores= $myrows['TAMANOINTERIORES'];
	$tintasInteriores= $myrows['TINTASINTERIORES'];
	$pantoneInteriores= $myrows['PANTONEINTERIORES'];
	$paginasInteriores= $myrows['PAGINASINTERIORES'];
	$peliculasnegativas= $myrows['PELICULASNEGATIVAS'];
	$peliculaspositivas= $myrows['PELICULASPOSITIVAS'];
	$peliculascolorkey= $myrows['PELICULASCOLORKEY'];
	$peliculasduplicado=$myrows['PELICULASDUPLICADO'];
	$peliculasarchivo= $myrows['PELICULASARCHIVO'];
	$peliculassuministran= $myrows['PELICULASSUMINISTRAN'];
	$peliculaspd= $myrows['PELICULASPD'];
	$peliculascpt= $myrows['PELICULASCPT'];
	$acabadosmate1caras= $myrows['ACABADOSMATE1CARAS'];
	$acabadosmate2caras=  $myrows['ACABADOSMATE2CARAS'];
	$acabadosbrillante1caras=$myrows['ACABADOSBRILLANTE1CARAS'];
	$acabadosbrillante2caras= $myrows['ACABADOSBRILLANTE2CARAS'];
	$acabadosuvtotal1caras= $myrows['ACABADOSUVTOTAL1CARAS'];
	$acabadosuvtotal2caras= $myrows['ACABADOSUVTOTAL2CARAS'];
	$acabadosuvparcial1caras= $myrows['ACABADOSUVPARCIAL1CARAS'];
	$acabadosuvparcial2caras=$myrows['ACABADOSUVPARCIAL2CARAS'];
	$brillolitografico= $myrows['BRILLOLITOGRAFICO'];
	$repujado= $myrows['REPUJADO'];
	$grafado= $myrows['GRAFADO'];
	$estampado= $myrows['ESTAMPADO'];
	$perforado= $myrows['PERFORADO'];
	$troquelado= $myrows['TROQUELADO'];
	$numerado=$myrows['NUMERADO'];
	$numeradodel= $myrows['NUMERADODEL'];
	$numeradohasta= $myrows['NUMERADOHASTA'];
	$plegadoalcentro= $myrows['PLEGADOALCENTRO'];
	$cuerpos2= $myrows['2CUERPOS'];
	$cuerpos3= $myrows['3CUERPOS'];
	$cuerpos4=$myrows['4CUERPOS'];
	$otro= $myrows['OTRO'];
	$encolado= $myrows['ENCOLADO'];
	$caballete= $myrows['CABALLETE'];
	$pegado= $myrows['PEGADO'];
	$hotmelt= $myrows['HOTMELT'];
	$cintadoblefaz= $myrows['CINTADOBLEFAZ'];
	$ganchoplano=$myrows['GANCHOPLANO'];
	$colaminado= $myrows['COLAMINADO'];
	$anilladocolor= $myrows['ANILLADOCOLOR'];
	$anilladodobleo= $myrows['ANILLADODOBLEO'];
	$anilladoplastico=$myrows['ANILLADOPLASTICO'];
	$empaquepapelkraft= $myrows['EMPAQUEPAPELKRAFT'];
	$empaquecajasdecarton= $myrows['EMPAQUECAJASDECARTON'];
	$empaquebolsaplastica= $myrows['EMPAQUEBOLSAPLASTICA'];
	$empaqueexportacion=$myrows['EMPAQUEEXPORTACION'];
	$observaciones= $myrows['OBSERVACIONES'];
	$fechaEntrega= $myrows['FECHAENTREGA'];
	$elaboro=$myrows['ELABORACION'];
	
}
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
<form action="/formas/parsing/procesarOrden.php" method="post">
<table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr> 
    <td height="20">&nbsp;</td>
  </tr>
  <tr> 
    <td><img src="/img/logo.gif" width="212" height="84"></td>
  </tr>
    <tr> 
    <td>&nbsp;</td>
  </tr>
  <tr> 
    <td width="600" height="17" valign="top"> <table width="600" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td><img src="/img/img1.gif" width="600" height="17"></td>
        </tr>
        <tr> 
          <td width="600" valign="top" background="/img/img2.gif"> <table width="580" border="0" align="center" cellpadding="0" cellspacing="0">
              <tr> 
                <td><table width="580" border="0" align="left" cellpadding="0" cellspacing="1">
                    <tr> 
                    
                      <td width="277" valign="middle"><table width="260" border="0" cellpadding="0" cellspacing="3" class="arial10prsrojabold">
                          <tr> 
                            <td width="53">Cliente: </td>
                            <td width="201"><div align="left"> 
							<SELECT name="cliente" class="combos" >
									<option value="<? echo $cliente; ?>"><? echo $cliente; ?></option>
									<?
										
										 $desplegar=listadoClientes ($db);
											foreach ($desplegar as $row){
												echo "<option value='$row[EMPRESA]'>".$row[EMPRESA]."</option>";
											}
										?>	
							</SELECT>
                              
                              </div></td>
                          </tr>
                        </table></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td>&nbsp;</td>
              </tr>
              <tr> 
                <td> <table width="580" border="0" align="center" cellpadding="0" cellspacing="0" class="arial10prsrojabold">
                    <tr> 
                      <td width="590" height="19" valign="top">Clase de trabajo:</td>
                    </tr>
                    <tr> 
                      <td height="19" valign="top"><table width="580" border="0" align="center" cellpadding="0" cellspacing="2" class="arial10ptsgrisnormal">
                          <tr bgcolor="F5F4F4"> 
                            <td width="110"> <div align="right">Afiche 
							<?
								if ($afiche==1){
									echo "<input type='checkbox' name='afiche' value='1' checked>";
								}else{
                            ?>
							    	<input type="checkbox" name="afiche" value="1">
							<?
								}
							?>
                              </div></td>
                            <td width="92"> <div align="right">Calendario 
							<?
								if ($calendario==1){
									echo "<input type='checkbox' name='calendario' value='1' checked>";
								}else{
                            ?>
                                <input type="checkbox" name="calendario" value="1">
							<?
								}
							?>
                              </div></td>
                            <td width="109"> <div align="right">Diplomas 
							<?
								if ($diplomas==1){
									echo "<input type='checkbox' name='diplomas' value='1' checked>";
								}else{
                            ?>
                                <input type="checkbox" name="diplomas" value="1">
							<?
								}
							?>
                              </div></td>
                            <td width="89"> <div align="right">Folleto 
							<?
								if ($folleto==1){
									echo "<input type='checkbox' name='folleto' value='1' checked>";
								}else{
                            ?>
                                <input type="checkbox" name="folleto" value="1">
							<?
								}
							?>
                              </div></td>
                            <td width="93"> <div align="right">Volantes 
							<?
								if ($volantes==1){
									echo "<input type='checkbox' name='volantes' value='1' checked>";
								}else{
                            ?>
                                <input type="checkbox" name="volantes" value="1">
							<?
								}
							?>
                              </div></td>
                            <td width="83"> <div align="right">Hablador
							<?
								if ($hablador==1){
									echo "<input type='checkbox' name='hablador' value='1' checked>";
								}else{
                            ?> 
                                <input type="checkbox" name="hablador" value="1">
							<?
								}
							?>
                              </div></td>
                          </tr>
                          <tr> 
                            <td><div align="right">Libretas 
							<?
								if ($libretas==1){
									echo "<input type='checkbox' name='libretas' value='1' checked>";
								}else{
                            ?> 
                                <input type="checkbox" name="libretas" value="1">
							<?
								}
							?>
                              </div></td>
                            <td><div align="right">Libro 
							<?
								if ($libro==1){
									echo "<input type='checkbox' name='libro' value='1' checked>";
								}else{
                            ?> 
                                <input type="checkbox" name="libro" value="1">
							<?
								}
							?>
                              </div></td>
                            <td><div align="right">Escarapelas 
							<?
								if ($escarapelas==1){
									echo "<input type='checkbox' name='escarapelas' value='1' checked>";
								}else{
                            ?> 
                                <input type="checkbox" name="escarapelas" value="1">
							<?
								}
							?>
                              </div></td>
                            <td><div align="right">Papeleria 
							<?
								if ($papeleria==1){
									echo "<input type='checkbox' name='papeleria' value='1' checked>";
								}else{
                            ?> 
                                <input type="checkbox" name="papeleria" value="1">
							<?
								}
							?>
                              </div></td>
                            <td><div align="right">membrete 
							<?
								if ($membrete==1){
									echo "<input type='checkbox' name='membrete' value='1' checked>";
								}else{
                            ?> 
                                <input type="checkbox" name="membrete" value="1">
							<?
								}
							?>
                              </div></td>
                            <td><div align="right">Plagable 
							<?
								if ($plegable==1){
									echo "<input type='checkbox' name='plegable' value='1' checked>";
								}else{
                            ?> 
                                <input type="checkbox" name="plegable" value="1">
							<?
								}
							?>
                              </div></td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td> <div align="right">Revista 
							<?
								if ($revista==1){
									echo "<input type='checkbox' name='revista' value='1' checked>";
								}else{
                            ?> 
                                <input type="checkbox" name="revista" value="1">
							<?
								}
							?>
                              </div></td>
                            <td> <div align="right">Sticker 
							<?
								if ($sticker==1){
									echo "<input type='checkbox' name='sticker' value='1' checked>";
								}else{
                            ?>
                                <input type="checkbox" name="sticker" value="1">
							<?
								}
							?>
                              </div></td>
                            <td bgcolor="F5F4F4"> 
                              <div align="right">Hojas 
							  <?
								if ($hojas==1){
									echo "<input type='checkbox' name='hojas' value='1' checked>";
								}else{
                            ?>
                                <input type="checkbox" name="hojas" value="1">
							<?
								}
							?>
                              </div></td>
                            <td> <div align="right">Sobres 
							<?
								if ($sobres==1){
									echo "<input type='checkbox' name='sobres' value='1' checked>";
								}else{
                            ?>
                                <input type="checkbox" name="sobres" value="1">
							<?
								}
							?>
                              </div></td>
                            <td> <div align="right">Tarjetas 
							<?
								if ($tarjetas==1){
									echo "<input type='checkbox' name='tarjetas' value='1' checked>";
								}else{
                            ?>
                                <input type="checkbox" name="tarjetas" value="1">
							<?
								}
							?>
                              </div></td>
                            <td bgcolor="F5F4F4"> <div align="right">Tarjeton 
							<?
								if ($tarjetas==1){
									echo "<input type='checkbox' name='tarjeton' value='1' checked>";
								}else{
                            ?>
                                <input type="checkbox" name="tarjeton" value="1">
							<?
								}
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
              <tr> 
                <td><table width="305" border="0" align="center" cellpadding="0" cellspacing="3" class="arial10prsrojabold">
                    <tr> 
                      <td width="135"><div align="center">Referencia 
                          <input name="referencia" type="text" class="combos" size="20" value="<? echo $referencia; ?>">
                        </div></td>
                      <td width="86"><div align="center">Cantidad 
                          <input name="cantidad" type="text" class="combos" size="10" value="<? echo $cantidad; ?>">
                        </div></td>
                      <td width="72"><div align="center">planchas 
                          <input name="planchas" type="text" class="combos" size="10" value="<? echo $planchas; ?>">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td><img src="/img/img3.gif" width="600" height="19"></td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td><img src="/img/transp.gif" width="10" height="10"></td>
  </tr>
  <tr> 
    <td><table width="600" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td><img src="/img/img1.gif" width="600" height="17"></td>
        </tr>
        <tr> 
          <td width="600" valign="top" background="/img/img2.gif"> <table width="580" border="0" align="center" cellpadding="0" cellspacing="0">
              <tr> 
                <td><table width="580" border="0" cellpadding="0" cellspacing="5" class="arial10prsrojabold">
                    <tr> 
                      <td width="71" height="15"><div align="center">MAQUINA</div></td>
                      <td width="123" bgcolor="F5F4F4" class="arial10ptsgrisnormal"> 
                        <div align="center"><span class="arial10ptsgrisBOLD">SOLNA 
                          1</span> 
						 	 <?
								if ($maquinasolna1==1){
									echo "<input type='checkbox' name='maquinasolna1' value='1' checked>";
								}else{
                            ?>
                          		<input type="checkbox" name="maquinasolna1" value="1">
						   <?
								}
						   ?>
                        </div></td>
                      <td width="114" bgcolor="F5F4F4" class="arial10ptsgrisnormal"> 
                        <div align="center"><span class="arial10ptsgrisBOLD">GTO</span> 
						    <?
								if ($maquinagto==1){
									echo "<input type='checkbox' name='maquinagto' value='1' checked>";
								}else{
                            ?>
                          <input type="checkbox" name="maquinagto" value="1">
						  <?
								}
						   ?>
                        </div></td>
                      <td width="118" bgcolor="F5F4F4" class="arial10ptsgrisnormal"> 
                        <div align="center"><span class="arial10ptsgrisBOLD">ROLAND</span>
						   <?
								if ($maquinaroland==1){
									echo "<input type='checkbox' name='maquinaroland' value='1' checked>";
								}else{
                            ?> 
                          <input type="checkbox" name="maquinaroland" value="1">
						  <?
								}
						   ?>
                        </div></td>
                      <td width="124" bgcolor="F5F4F4" class="arial10ptsgrisnormal"> 
                        <div align="center"><span class="arial10ptsgrisBOLD">SORMZ</span> 
						 <?
								if ($maquinasormz==1){
									echo "<input type='checkbox' name='maquinasormz' value='1' checked>";
								}else{
                            ?> 
                          <input type="checkbox" name="maquinasormz" value="1">
						  <?
								}
						   ?>
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td><table width="580" border="0" cellpadding="0" cellspacing="7" class="arial10prsrojabold">
                    <tr> 
                      <td width="186" height="15"><div align="center"> 
                          <table width="186" border="0" cellspacing="2" cellpadding="0">
                            <tr> 
                              <td colspan="2" class="arial10prsrojabold"><div align="center">PAPEL</div></td>
                            </tr>
                            <tr bgcolor="F5F4F4"> 
                              <td width="86" class="arial10prsrojabold"> <div align="center"><span class="arial10ptsgrisnormal">60x90 
                                <?
								if ($papel60x90==1){
									echo "<input type='checkbox' name='papel60x90' value='1' checked>";
								}else{
								?> 
								  <input type="checkbox" name="papel60x90" value="1">
								<?
								}
						   		?>
                                  </span></div></td>
                              <td width="94"> <div align="center"><span class="arial10ptsgrisnormal">70x100</span> 
                                <?
								if ($papel70x100==1){
									echo "<input type='checkbox' name='papel70x100' value='1' checked>";
								}else{
								?> 
								  <input type="checkbox" name="papel70x100" value="1">
								<?
								}
						   		?>
                                </div></td>
                            </tr>
                          </table>
                        </div></td>
                      <td width="186"><div align="center"> 
                          <table width="186" border="0" cellspacing="2" cellpadding="0">
                            <tr> 
                              <td colspan="2" class="arial10prsrojabold"><div align="center">CARATULA</div></td>
                            </tr>
                            <tr bgcolor="F5F4F4"> 
                              <td width="86" class="arial10prsrojabold"> <div align="center"><span class="arial10ptsgrisnormal">60x90 
                                <?
								if ($caratula60x90==1){
									echo "<input type='checkbox' name='caratula60x90' value='1' checked>";
								}else{
								?>
								  <input type="checkbox" name="caratula60x90" value="1">
								<?
								}
						   		?>
                                  </span></div></td>
                              <td width="94"> <div align="center"><span class="arial10ptsgrisnormal">70x100</span> 
                                <?
								if ($caratula70x100==1){
									echo "<input type='checkbox' name='caratula70x100' value='1' checked>";
								}else{
								?>  
								  <input type="checkbox" name="caratula70x100" value="1">
								<?
								}
						   		?>
                                </div></td>
                            </tr>
                          </table>
                        </div></td>
                      <td width="187"><div align="center"> 
                          <table width="186" border="0" cellspacing="2" cellpadding="0">
                            <tr> 
                              <td colspan="2" class="arial10prsrojabold"><div align="center">INTERIORES</div></td>
                            </tr>
                            <tr bgcolor="F5F4F4"> 
                              <td width="86" class="arial10prsrojabold"> <div align="center"><span class="arial10ptsgrisnormal">60x90 
                                 <?
								if ($interiores60x90==1){
									echo "<input type='checkbox' name='interiores60x90' value='1' checked>";
								}else{
								?> 
								  <input type="checkbox" name="interiores60x90" value="1">
								<?
								}
						   		?>
                                  </span></div></td>
                              <td width="94"> <div align="center"><span class="arial10ptsgrisnormal">70x100</span> 
                                 <?
								if ($interiores70x100==1){
									echo "<input type='checkbox' name='interiores70x100' value='1' checked>";
								}else{
								?>  
								 
								  <input type="checkbox" name="interiores70x100" value="1">
								<?
								}
						   		?>
                                </div></td>
                            </tr>
                          </table>
                        </div></td>
                    </tr>
                    <tr> 
                      <td height="94" valign="top"> <table width="186" border="0" align="center" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="83" bgcolor="F5F4F4"> 
                              <div align="right">Pliegos</div></td>
                            <td width="94" bgcolor="F5F4F4"> 
                              <input name="pliegosPapel" type="text" class="combos" size="17" value="<? echo $pliegosPapel; ?>"></td>
                          </tr>
                          <tr> 
                            <td height="22" bgcolor="F5F4F4"> 
                              <div align="right">Tama&ntilde;o</div></td>
                            <td bgcolor="F5F4F4">
							<input name="tamanoPapel" type="text" class="combos" size="17" value="<? echo $tamanoPapel; ?>"> 
                            </td>
                          </tr>
                          <tr> 
                            <td bgcolor="F5F4F4"> 
                              <div align="right">Tintas</div></td>
                            <td bgcolor="F5F4F4">
							<input name="tintasPapel" type="text" class="combos" size="17" value="<? echo $tintasPapel; ?>"> 
                            </td>
                          </tr>
                          <tr> 
                            <td bgcolor="F5F4F4"> 
                              <div align="right">Pantone</div></td>
                            <td bgcolor="F5F4F4">
							<input name="pantonePapel" type="text" class="combos" size="17" value="<? echo $pantonePapel; ?>"> 
                            </td>
                          </tr>
						  <tr> 
                            <td bgcolor="F5F4F4"> 
                              <div align="right">Páginas</div></td>
                            <td bgcolor="F5F4F4">
							<input name="paginasPapel" type="text" class="combos" size="17" value="<? echo $paginasPapel; ?>"> 
                            </td>
                          </tr>
                        </table></td>
                      <td><table width="186" border="0" align="center" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr bgcolor="F5F4F4"> 
                            <td width="84"> <div align="right">Pliegos</div></td>
                            <td width="93"><input name="pliegosCaratula" type="text" class="combos" size="17" value="<? echo $pliegosCaratula; ?>"> 
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td height="22"> <div align="right">Tama&ntilde;o</div></td>
                            <td><input name="tamanoCaratula" type="text" class="combos" size="17" value="<? echo $tamanoCaratula; ?>"> 
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td> <div align="right">Tintas</div></td>
                            <td><input name="tintasCaratula" type="text" class="combos" size="17" value="<? echo $tintasCaratula; ?>"> 
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td bgcolor="F5F4F4"> <div align="right">Pantone</div></td>
                            <td><input name="pantoneCaratula" type="text" class="combos" size="17" value="<? echo $pantoneCaratula; ?>"> 
                            </td>
                          </tr>
						   <tr> 
                            <td bgcolor="F5F4F4"> 
                              <div align="right">Páginas</div></td>
                            <td bgcolor="F5F4F4">
							<input name="paginasCaratula" type="text" class="combos" size="17" value="<? echo $paginasCaratula; ?>"> 
                            </td>
                          </tr>
                        </table></td>
                      <td><table width="186" border="0" align="center" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr bgcolor="F5F4F4"> 
                            <td width="85"> <div align="right">Pliegos</div></td>
                            <td width="92"><input name="pliegosInteriores" type="text" class="combos" size="17" value="<? echo $pliegosInteriores; ?>"> 
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td height="22"> <div align="right">Tama&ntilde;o</div></td>
                            <td><input name="tamanoInteriores" type="text" class="combos" size="17" value="<? echo $tamanoInteriores; ?>"> 
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td> <div align="right">Tintas</div></td>
                            <td><input name="tintasInteriores" type="text" class="combos" size="17" value="<? echo $tintasInteriores; ?>"> 
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td bgcolor="F5F4F4"> <div align="right">Pantone</div></td>
                            <td><input name="pantoneInteriores" type="text" class="combos" size="17" value="<? echo $pantoneInteriores; ?>"> 
                            </td>
                          </tr>
						   <tr> 
                            <td bgcolor="F5F4F4"> 
                              <div align="right">Páginas</div></td>
                            <td bgcolor="F5F4F4">
							<input name="paginasInteriores" type="text" class="combos" size="17" value="<? echo $paginasInteriores; ?>"> 
                            </td>
                          </tr>
                        </table></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td><table width="400" border="0" align="center" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                    <tr valign="middle"> 
                      <td colspan="4" class="arial10prsrojabold"><div align="center">PELICULAS</div></td>
                    </tr>
                    <tr> 
                      <td bgcolor="F5F4F4"> <div align="right">negativas 
                           <?
								if ($peliculasnegativas==1){
									echo "<input type='checkbox' name='peliculasnegativas' value='1' checked>";
								}else{
							?>  
						  <input type="checkbox" name="peliculasnegativas" value="1">
						  <?
						  }
						  ?>
                        </div></td>
                      <td bgcolor="F5F4F4"> <div align="right">positivas 
					  		<?
								if ($peliculaspositivas==1){
									echo "<input type='checkbox' name='peliculaspositivas' value='1' checked>";
								}else{
							?>  
                          <input type="checkbox" name="peliculaspositivas" value="1">
						   <?
						  }
						  ?>
                        </div></td>
                      <td width="88" bgcolor="F5F4F4"> <div align="right">Color 
                          key 
						  <?
								if ($peliculascolorkey==1){
									echo "<input type='checkbox' name='peliculascolorkey' value='1' checked>";
								}else{
							?>  
                          <input type="checkbox" name="peliculascolorkey" value="1">
						   <?
						  }
						  ?>
                        </div></td>
                      <td bgcolor="F5F4F4"> <div align="right">Duplicado 
					   <?
								if ($peliculasduplicado==1){
									echo "<input type='checkbox' name='peliculasduplicado' value='1' checked>";
								}else{
							?>  
                          <input type="checkbox" name="peliculasduplicado" value="1">
						    <?
						  }
						  ?>
                        </div></td>
                    </tr>
                    <tr bgcolor="F5F4F4"> 
                      <td> <div align="right">Archivo 
					  		<?
								if ($peliculasarchivo==1){
									echo "<input type='checkbox' name='peliculasarchivo' value='1' checked>";
								}else{
							?>  
                          <input type="checkbox" name="peliculasarchivo" value="1">
						    <?
						  }
						  ?>
                        </div></td>
                      <td> <div align="right">Suminsitran 
					  		<?
								if ($peliculassuministran==1){
									echo "<input type='checkbox' name='peliculassuministran' value='1' checked>";
								}else{
							?>  
                          <input type="checkbox" name="peliculassuministran" value="1">
						  <?
						  }
						  ?>
                        </div></td>
                      <td> <div align="right">PD 
							  <?
								if ($peliculaspd==1){
									echo "<input type='checkbox' name='peliculaspd' value='1' checked>";
								}else{
							?>  
                          <input type="checkbox" name="peliculaspd" value="1">
						    <?
							  }
							?>
                        </div></td>
                      <td> <div align="right">CPT 
					  		<?
								if ($peliculascpt==1){
									echo "<input type='checkbox' name='peliculascpt' value='1' checked>";
								}else{
							?>  
                          <input type="checkbox" name="peliculascpt" value="1">
						  <?
							  }
							?>
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td><img src="/img/img3.gif" width="600" height="19"></td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td><img src="/img/transp.gif" width="10" height="10"></td>
  </tr>
  <tr> 
    <td><table width="600" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td><img src="/img/img1.gif" width="600" height="17"></td>
        </tr>
        <tr> 
          <td width="600" valign="top" background="/img/img2.gif"><table width="580" border="0" align="center" cellpadding="0" cellspacing="0">
              <tr> 
                <td class="arial10prsrojabold"><div align="center">ACABADOS<br>
                  </div></td>
              </tr>
              <tr> 
                <td><table width="530" border="0" align="center" cellpadding="0" cellspacing="2">
                    <tr> 
                      <td width="268">
					
					
					  <table width="260" border="0" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="111"><div align="right"> 
                                <p class="arial10ptsgrisBOLD">Plast. MATE</p>
                              </div></td>
                            <td width="65" bgcolor="F5F4F4"> <div align="center">1 cara 
							<?
								if ($acabadosmate1caras==1){
									echo "<input type='checkbox' name='acabadosmate1caras' value='1' checked>";
								}else{
							?>  
                                <input type="checkbox" name="acabadosmate1caras" value="1">
							<?
								}
							?>
                              </div></td>
                            <td width="80" bgcolor="F5F4F4"> <div align="center">2 caras 
							<?
								if ($acabadosmate2caras==1){
									echo "<input type='checkbox' name='acabadosmate2caras' value='1' checked>";
								}else{
							?>
                                <input type="checkbox" name="acabadosmate2caras" value="1">
							<?
								}
							?>
                              </div></td>
                          </tr>
                        </table>
						
						</td>
                      <td width="124"> 
					  
					  <table width="120" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Brillo Litogr&aacute;fico 
								<?
								if ($brillolitografico==1){
									echo "<input type='checkbox' name='brillolitografico' value='1' checked>";
								}else{
								?>
                                  <input type="checkbox" name="brillolitografico" value="1">
								<?
								}
								?>
                                </p>
                              </div></td>
                          </tr>
                        </table>
						
						
                        <div align="center"></div></td>
                      <td width="130">
					  
					  <table width="130" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="129"> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Repujado
								 <?
								if ($repujado==1){
									echo "<input type='checkbox' name='repujado' value='1' checked>";
								}else{
								?>
                                  <input type="checkbox" name="repujado" value="1">
								<?
								}
								?>
                                </p>
                              </div></td>
                          </tr>
                        </table>
						
						</td>
                    </tr>
                    <tr> 
                      <td><table width="260" border="0" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="110" height="15"> <div align="right"> 
                                <p class="arial10ptsgrisBOLD">Plast. BRILLANTE</p>
                              </div></td>
                            <td width="64" bgcolor="F5F4F4"> <div align="center">1 cara 
							 	<?
								if ($acabadosbrillante1caras==1){
									echo "<input type='checkbox' name='acabadosbrillante1caras' value='1' checked>";
								}else{
								?>
                                <input type="checkbox" name="acabadosbrillante1caras" value="1">
								<?
								}
								?>
                              </div></td>
                            <td width="82" bgcolor="F5F4F4"> <div align="center">2 caras 
								<?
								if ($acabadosbrillante2caras==1){
									echo "<input type='checkbox' name='acabadosbrillante2caras' value='1' checked>";
								}else{
								?>
                                <input type="checkbox" name="acabadosbrillante2caras" value="1">
								<?
								}
								?>
                              </div></td>
                          </tr>
                        </table></td>
                      <td><table width="120" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Grafado 
								<?
								if ($grafado==1){
									echo "<input type='checkbox' name='grafado' value='1' checked>";
								}else{
								?>
                                  <input type="checkbox" name="grafado" value="1">
								<?
								}
								?>
                                </p>
                              </div></td>
                          </tr>
                        </table></td>
                      <td><table width="130" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="128"> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Estampado 
								<?
								if ($estampado==1){
									echo "<input type='checkbox' name='estampado' value='1' checked>";
								}else{
								?>
                                  <input type="checkbox" name="estampado" value="1">
								<?
								}
								?>
                                </p>
                              </div></td>
                          </tr>
                        </table></td>
                    </tr>
                    <tr> 
                      <td>
					  
					  
					  <table width="260" border="0" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="108"><div align="right"> 
                                <p class="arial10ptsgrisBOLD">UV. TOTAL</p>
                              </div></td>
                            <td width="66" bgcolor="F5F4F4"> <div align="center">1 cara
								<?
								if ($acabadosuvtotal1caras==1){
									echo "<input type='checkbox' name='acabadosuvtotal1caras' value='1' checked>";
								}else{
								?> 
                                <input type="checkbox" name="acabadosuvtotal1caras" value="1">
								<?
								}
								?>
                              </div></td>
                            <td width="78" bgcolor="F5F4F4"> <div align="center">2 caras 
								<?
								if ($acabadosuvtotal2caras==1){
									echo "<input type='checkbox' name='acabadosuvtotal2caras' value='1' checked>";
								}else{
								?> 
                                <input type="checkbox" name="acabadosuvtotal2caras" value="1">
								<?
								}
								?>
                              </div></td>
                          </tr>
                        </table></td>
                      <td><table width="120" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Perforado 
								<?
								if ($perforado==1){
									echo "<input type='checkbox' name='perforado' value='1' checked>";
								}else{
								?> 
                                  <input type="checkbox" name="perforado" value="1">
								 <?
								}
								?>
                                </p>
                              </div></td>
                          </tr>
                        </table></td>
                      <td><table width="130" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="130"> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Numerado 
								<?
								if ($numerado==1){
									echo "<input type='checkbox' name='numerado' value='1' checked>";
								}else{
								?> 
                                  <input type="checkbox" name="numerado" value="1">
							   <?
								}
								?>
                                </p>
                              </div></td>
                          </tr>
                        </table></td>
                    </tr>
                    <tr> 
                      <td><table width="260" border="0" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="109"><div align="right"> 
                                <p class="arial10ptsgrisBOLD">UV PARCIAL</p>
                              </div></td>
                            <td width="65" bgcolor="F5F4F4"> <div align="center">1 cara
							<?
								if ($acabadosuvparcial1caras==1){
									echo "<input type='checkbox' name='acabadosuvparcial1caras' value='1' checked>";
								}else{
								?>  
                                <input type="checkbox" name="acabadosuvparcial1caras" value="1">
								 <?
								}
								?>
                              </div></td>
                            <td width="78" bgcolor="F5F4F4"> <div align="center">2 caras
							<?
								if ($acabadosuvparcial2caras==1){
									echo "<input type='checkbox' name='acabadosuvparcial2caras' value='1' checked>";
								}else{
								?>   
                                <input type="checkbox" name="acabadosuvparcial2caras" value="1">
								 <?
								}
								?>
                              </div></td>
                          </tr>
                        </table></td>
                      <td><table width="120" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Troquelado 
								<?
								if ($troquelado==1){
									echo "<input type='checkbox' name='troquelado' value='1' checked>";
								}else{
								?>   
                                  <input type="checkbox" name="troquelado" value="1">
								  <?
								}
								?>
                                </p>
                              </div></td>
                          </tr>
                        </table></td>
                      <td><div align="right"><span class="arial10ptsgrisnormal">Del 
                          </span> 
                          <input name="numeradodel" type="text" class="combos" size="2" value="<? echo $numeradodel; ?>">
                          <span class="arial10ptsgrisnormal">al</span> 
                          <input name="numeradohasta" type="text" class="combos" size="2" value="<? echo $numeradohasta; ?>">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td><img src="/img/img3.gif" width="600" height="19"></td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td><img src="/img/transp.gif" width="10" height="10"></td>
  </tr>
  <tr> 
    <td><table width="600" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td><img src="/img/img1.gif" width="600" height="17"></td>
        </tr>
        <tr> 
          <td width="600" valign="top" background="/img/img2.gif"><table width="580" border="0" align="center" cellpadding="0" cellspacing="0">
              <tr> 
                <td class="arial10prsrojabold"><div align="center">ENCUADERNACI&Oacute;N<br>
                  </div></td>
              </tr>
              <tr> 
                <td><table width="580" border="0" align="center" cellpadding="0" cellspacing="4" class="arial10ptsgrisBOLD">
                    <tr> 
                      <td width="128" bgcolor="F5F4F4"> <div align="center">Plegado al centro 
					 			 <?
								if ($plegadoalcentro==1){
									echo "<input type='checkbox' name='plegadoalcentro' value='1' checked>";
								}else{
								?>  
							  <input type="checkbox" name="plegadoalcentro" value="1">
							   <?
								}
								?>
                        </div></td>
                      <td width="108" bgcolor="F5F4F4"> <div align="center">2 cuerpos 
					  			 <?
								if ($cuerpos2==1){
									echo "<input type='checkbox' name='cuerpos2' value='1' checked>";
								}else{
								?>  
                         			 <input type="checkbox" name="cuerpos2" value="1">
						   		<?
								}
								?>
                        </div></td>
                      <td width="103" bgcolor="F5F4F4"> <div align="center">3 cuerpos
					  			<?
								if ($cuerpos3==1){
									echo "<input type='checkbox' name='cuerpos3' value='1' checked>";
								}else{
								?>   
                         			 <input type="checkbox" name="cuerpos3" value="1">
								 <?
								}
								?>
                        </div></td>
                      <td width="90" bgcolor="F5F4F4"> <div align="center">4 cuerpos 
					 			 <?
								if ($cuerpos4==1){
									echo "<input type='checkbox' name='cuerpos4' value='1' checked>";
								}else{
								?>   
                         		 <input type="checkbox" name="cuerpos4" value="1">
						 		 <?
								}
								?>
                        </div></td>
                      <td width="47" bgcolor="F5F4F4"><div align="center">Otro 
					  		<?
								if ($otro==1){
									echo "<input type='checkbox' name='otro' value='1' checked>";
								}else{
								?>   
                          <input type="checkbox" name="otro" value="1">
						   <?
								}
								?>
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td><table width="400" border="0" align="center" cellpadding="0" cellspacing="4" class="arial10ptsgrisnormal">
                    <tr> 
                      <td width="121" bgcolor="F5F4F4"> <div align="right">Encolado 
					 			 <?
								if ($encolado==1){
									echo "<input type='checkbox' name='encolado' value='1' checked>";
								}else{
								?>   
                         		 <input type="checkbox" name="encolado" value="1">
						  		<?
								}
								?>
                        </div></td>
                      <td width="102" bgcolor="F5F4F4"> <div align="right">Caballete
					   			<?
								if ($caballete==1){
									echo "<input type='checkbox' name='caballete' value='1' checked>";
								}else{
								?>   
                         		 <input type="checkbox" name="caballete" value="1">
						 		 <?
								}
								?>
                        </div></td>
                      <td width="87" bgcolor="F5F4F4"> <div align="right">Pegado 
					 		 <?
								if ($pegado==1){
									echo "<input type='checkbox' name='pegado' value='1' checked>";
								}else{
								?>   
                       		   <input type="checkbox" name="pegado" value="1">
						  		<?
								}
								?>
                        </div></td>
                      <td width="70" bgcolor="F5F4F4"> <div align="right">Hotmelt 
					  			 <?
								if ($hotmelt==1){
									echo "<input type='checkbox' name='hotmelt' value='1' checked>";
								}else{
								?>  
                         		 <input type="checkbox" name="hotmelt" value="1">
								 <?
								}
								?>
                        </div></td>
                    </tr>
                    <tr> 
                      <td bgcolor="F5F4F4"><div align="right">Cinta de doble faz 
							   <?
								if ($cintadoblefaz==1){
									echo "<input type='checkbox' name='cintadoblefaz' value='1' checked>";
								}else{
								?>  
                         			 <input type="checkbox" name="cintadoblefaz" value="1">
								<?
								}
								?>
                        </div></td>
                      <td bgcolor="F5F4F4"><div align="right">Gancho plano 
							  <?
								if ($ganchoplano==1){
									echo "<input type='checkbox' name='ganchoplano' value='1' checked>";
								}else{
								?>  
                          			<input type="checkbox" name="ganchoplano" value="1">
								  <?
								}
								?>
                        </div></td>
                      <td bgcolor="F5F4F4"><div align="right">Colaminado 
					  		<?
								if ($colaminado==1){
									echo "<input type='checkbox' name='colaminado' value='1' checked>";
								}else{
								?>  
                          <input type="checkbox" name="colaminado" value="1">
						  <?
								}
								?>
                        </div></td>
                      <td> <div align="right"></div></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td>&nbsp;</td>
              </tr>
              <tr> 
                <td class="arial10prsrojabold"> <table width="580" border="0" align="center" cellpadding="0" cellspacing="4" class="arial10ptsgrisBOLD">
                    <tr> 
                      <td width="87"> <div align="center" class="arial10prsrojabold">ANILLADO 
                        </div></td>
                      <td width="179" bgcolor="F5F4F4"> <div align="center">Color 
					  			<?
								if ($anilladocolor==1){
									echo "<input type='checkbox' name='anilladocolor' value='1' checked>";
								}else{
								?>  
								  <input type="checkbox" name="anilladocolor" value="1">
								<?
								}
								?>
                        </div></td>
                      <td width="157" bgcolor="F5F4F4"> <div align="center">Doble O 
					  <?
								if ($anilladodobleo==1){
									echo "<input type='checkbox' name='anilladodobleo' value='1' checked>";
								}else{
								?>  
                          <input type="checkbox" name="anilladodobleo" value="1">
						  <?
								}
								?>
                        </div></td>
                      <td width="137" bgcolor="F5F4F4"> <div align="center">Plastico 
					  <?
								if ($anilladoplastico==1){
									echo "<input type='checkbox' name='anilladoplastico' value='1' checked>";
								}else{
								?>  
                          <input type="checkbox" name="anilladoplastico" value="1">
						   <?
								}
								?>
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td><img src="/img/transp.gif" width="10" height="5"></td>
              </tr>
              <tr> 
                <td><table width="580" border="0" align="center" cellpadding="0" cellspacing="4" class="arial10ptsgrisBOLD">
                    <tr> 
                      <td width="88"> <div align="center" class="arial10prsrojabold">EMPAQUE 
                        </div></td>
                      <td width="115" bgcolor="F5F4F4"> <div align="center">Papel Kraft 
					  <?
								if ($empaquepapelkraft==1){
									echo "<input type='checkbox' name='empaquepapelkraft' value='1' checked>";
								}else{
								?>
                          <input type="checkbox" name="empaquepapelkraft" value="1">
						  <?
								}
								?>
                        </div></td>
                      <td width="126" bgcolor="F5F4F4"> <div align="center">Cajas de cart&oacute;n 
					   <?
								if ($empaquecajasdecarton==1){
									echo "<input type='checkbox' name='empaquecajasdecarton' value='1' checked>";
								}else{
								?>
                          <input type="checkbox" name="empaquecajasdecarton" value="1">
						  <?
								}
								?>
                        </div></td>
                      <td width="105" bgcolor="F5F4F4"> <div align="center">Bolsa plastica 
					   <?
								if ($empaquebolsaplastica==1){
									echo "<input type='checkbox' name='empaquebolsaplastica' value='1' checked>";
								}else{
								?>
                          <input type="checkbox" name="empaquebolsaplastica" value="1">
						  <?
								}
								?>
                        </div></td>
                      <td width="122" bgcolor="F5F4F4"><div align="center">Exportaci&oacute;n 
					  <?
								if ($empaqueexportacion==1){
									echo "<input type='checkbox' name='empaqueexportacion' value='1' checked>";
								}else{
								?>
                          <input type="checkbox" name="empaqueexportacion" value="1">
						   <?
								}
								?>
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td><img src="/img/img3.gif" width="600" height="19"></td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td><img src="/img/transp.gif" width="10" height="10"></td>
  </tr>
  <tr> 
    <td><table width="600" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td><img src="/img/img1.gif" width="600" height="17"></td>
        </tr>
        <tr> 
          <td width="600" valign="top" background="/img/img2.gif"><table width="550" border="0" align="center" cellpadding="0" cellspacing="4">
              <tr> 
                <td width="106" valign="top" class="arial10prsrojabold"> <div align="right">Observaciones 
                    :</div></td>
                <td width="407"> <div align="center"> 
                    <textarea name="observaciones" cols="65" rows="4" class="combos2"><? echo $observaciones; ?></textarea>
                  </div></td>
              </tr>
            </table>
            <table width="550" border="0" align="center" cellpadding="0" cellspacing="5">
              <tr> 
                <td width="312" height="26">
<table width="312" border="0" align="left" cellpadding="0" cellspacing="1">
                    <tr> 
                      <td width="104" class="arial10prsrojabold"> Fecha de entrega</td>
                      <td width="68" valign="middle"><span class="arial10ptsgrisnormal">Dia</span> 
                        <select name="diaEntrega" class="combos">
						  <option selected><? echo $diaEntrega; ?></option>	
                          <option>01</option>
                          <option>02</option>
                          <option>03</option>
                          <option>04</option>
                          <option>05</option>
                          <option>06</option>
                          <option>07</option>
                          <option>08</option>
                          <option>09</option>
                          <option>10</option>
                          <option>11</option>
                          <option>12</option>
                          <option>13</option>
                          <option>14</option>
                          <option>15</option>
                          <option>16</option>
                          <option>17</option>
                          <option>18</option>
                          <option>19</option>
                          <option>20</option>
                          <option>21</option>
                          <option>22</option>
                          <option>23</option>
                          <option>24</option>
                          <option>25</option>
                          <option>26</option>
                          <option>27</option>
                          <option>28</option>
                          <option>29</option>
                          <option>30</option>
                          <option>31</option>
                        </select></td>
                      <td width="72" valign="middle" class="arial10ptsgrisnormal">mes 
                        <select name="mesEntrega" class="combos">
						  <option selected><? echo $mesEntrega; ?></option>	
                          <option>01</option>
                          <option>02</option>
                          <option>03</option>
                          <option>04</option>
                          <option>05</option>
                          <option>06</option>
                          <option>07</option>
                          <option>08</option>
                          <option>09</option>
                          <option>10</option>
                          <option>11</option>
                          <option>12</option>
                        </select></td>
                      <td width="63" valign="middle"><span class="arial10ptsgrisnormal">A&ntilde;o</span> 
                        <input name="anoEntrega" type="text" class="combos" size="2" maxlength="4" value="<? echo $anoEntrega; ?>"> 
                      </td>
                    </tr>
                  </table></td>
                <td width="223" class="arial10ptsgrisBOLD"><div align="center">Elaboro 
                    <input name="elaboro" type="text" class="combos" size="30" value="<? echo $elaboro; ?>">
                  </div></td>
              </tr>
              <tr> 
                <td colspan="2"><div align="center"><a href="#" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image18','','/img/actualizar.gif',1)"><br>
                    <input type="image" src="/img/actualizar.gif" name="Image18" width="94" height="21" border="0"></a></div>
					<input type="hidden" name="id" value="<? echo $id; ?>">
					</td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td><img src="/img/img3.gif" width="600" height="19"></td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
  </tr>
</table>
</form>
</body>
</html>


