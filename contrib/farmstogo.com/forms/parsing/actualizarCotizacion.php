<?php
/************************************************************************/
/*       	ISP															*/
/*______________________________________________________________________*/
/*			Investigaci�n y Desarrollo									*/
/*                                                                      */
/*                    	TERRA LYCOS COLOMBIA                          	*/
/*        	Sistema de alimentaci�n de TERRA COLOMBIA					*/
/************************************************************************/

/************************************************************************/
/* Definici�n de variables globales y librer�as requeridas.				*/
/*______________________________________________________________________*/

require_once("../../formas/config/general.conf");		// Get the global variables
require_once("../../formas/config/bd.conf"); 			// Get the connection variables
require_once("../../formas/lib/database.php"); 							// Get the DataBase extended functions
require_once("../../consultas/funcionesSQL.php"); 	


// +-----------------------------------------------------------------------+
// |  Incializa las plantillas y establece la conexi�n con la BD		   |
// +-----------------------------------------------------------------------+
$db = DB::connect($dsn, DB_CONNECT_MODE); // Creates a database connection object in $db 
										  // or, a database error object if it went wrong.
										  // Lista de categorias principales
$db->setFetchMode(DB_FETCHMODE_ASSOC);	

$porcentajeUtilidad=$utilidad/100;
$porcetajeComision=$comision/100;


if ($mil!=NULL){
$sumaMil=$COTPAPEL1000+$COTPMX11000+$COTUVP1000+$COTANILLADODOBLEO1000+$COTEND1000+$COTPLANCHAS1000+$COTPMX21000+$COTSCRATCH1000+$COTANILLADOPLASTICO1000+$COTCOLAMINADO1000+$COTPELICULAS1000+$COTPBX11000+$COTNUMERADO1000+$COTREPUJE1000+$COTBOLSAPLASTICA1000+$COTCTP1000+$COTPBX21000+$COTTROQUEL1000+$COTREPUJADO1000+$COTOTROS1000+$COTPRUEBADIGITAL1000+$COTUVTX11000+$COTTROQUELADO1000+$COTPLEGADO1000+$COTDISENO1000+$COTTIRAJE1000+$COTUVTX21000+$COTHOTMELT1000+$COTGRAFA1000+$COTCAJAS1000;
//echo "sumaMil: ". $sumaMil;
$utilidadMil=($sumaMil)*($porcentajeUtilidad);
//echo "utilidadMil". $utilidadMil; 
$comisionMil=($sumaMil)*($porcetajeComision);
//echo "comision". $porcetajeComision;
$totalFinalMil=$sumaMil+$utilidadMil+$comisionMil;
//echo "Total ". $totalFinalMil;
$valorUnitarioMil=$totalFinalMil/$mil;
$valorUnitarioMil=round($valorUnitarioMil);
}

if ($dosmil!=NULL){
$sumaDosmil=$COTPAPEL2000+$COTPMX12000+$COTUVP2000+$COTANILLADODOBLEO2000+$COTEND2000+$COTPLANCHAS2000+$COTPMX22000+$COTSCRATCH2000+$COTANILLADOPLASTICO2000+$COTCOLAMINADO2000+$COTPELICULAS2000+$COTPBX12000+$COTNUMERADO2000+$COTREPUJE2000+$COTBOLSAPLASTICA2000+$COTCTP2000+$COTPBX22000+$COTTROQUEL2000+$COTREPUJADO2000+$COTOTROS2000+$COTPRUEBADIGITAL2000+$COTUVTX12000+$COTTROQUELADO2000+$COTPLEGADO2000+$COTDISENO2000+$COTTIRAJE2000+$COTUVTX22000+$COTHOTMELT2000+$COTGRAFA2000+$COTCAJAS2000;
//echo "sumaDosmil: ". $sumaDosmil;
$utilidad2000=($sumaDosmil)*($porcentajeUtilidad);
//echo "utilidad2000". $utilidad2000; 
$comision2000=($sumaDosmil)*($porcetajeComision);
//echo "comision". $porcetajeComision;
$totalFinal2000=$sumaDosmil+$utilidad2000+$comision2000;
//echo "Total ". $totalFinal2000;
$valorUnitarioDosmil=$totalFinal2000/$dosmil;
$valorUnitarioDosmil=round($valorUnitarioDosmil);
}

if ($tresmil!=NULL){
$sumaTresmil=$COTPAPEL3000+$COTPMX13000+$COTUVP3000+$COTANILLADODOBLEO3000+$COTEND3000+$COTPLANCHAS3000+$COTPMX23000+$COTSCRATCH3000+$COTANILLADOPLASTICO3000+$COTCOLAMINADO3000+$COTPELICULAS3000+$COTPBX13000+$COTNUMERADO3000+$COTREPUJE3000+$COTBOLSAPLASTICA3000+$COTCTP3000+$COTPBX23000+$COTTROQUEL3000+$COTREPUJADO3000+$COTOTROS3000+$COTPRUEBADIGITAL3000+$COTUVTX13000+$COTTROQUELADO3000+$COTPLEGADO3000+$COTDISENO3000+$COTTIRAJE3000+$COTUVTX23000+$COTHOTMELT3000+$COTGRAFA3000+$COTCAJAS3000;
//echo "sumaTresmil: ". $sumaTresmil;
$utilidad3000=($sumaTresmil)*($porcentajeUtilidad);
//echo "utilidad3000". $utilidad3000; 
$comision3000=($sumaTresmil)*($porcetajeComision);
//echo "comision". $porcetajeComision;
$totalFinal3000=$sumaTresmil+$utilidad3000+$comision3000;
//echo "Total ". $totalFinal3000;
$valorUnitarioTresmil=$totalFinal3000/$tresmil;
$valorUnitarioTresmil=round($valorUnitarioTresmil);
}

if ($cuatromil!=NULL){
$sumaCuatromil=$COTPAPEL4000+$COTPMX14000+$COTUVP4000+$COTANILLADODOBLEO4000+$COTEND4000+$COTPLANCHAS4000+$COTPMX24000+$COTSCRATCH4000+$COTANILLADOPLASTICO4000+$COTCOLAMINADO4000+$COTPELICULAS4000+$COTPBX14000+$COTNUMERADO4000+$COTREPUJE4000+$COTBOLSAPLASTICA4000+$COTCTP4000+$COTPBX24000+$COTTROQUEL4000+$COTREPUJADO4000+$COTOTROS4000+$COTPRUEBADIGITAL4000+$COTUVTX14000+$COTTROQUELADO4000+$COTPLEGADO4000+$COTDISENO4000+$COTTIRAJE4000+$COTUVTX24000+$COTHOTMELT4000+$COTGRAFA4000+$COTCAJAS4000;
//echo "sumaCuatromil: ". $sumaCuatromil;
$utilidad4000=($sumaCuatromil)*($porcentajeUtilidad);
//echo "utilidad4000". $utilidad4000; 
$comision4000=($sumaCuatromil)*($porcetajeComision);
//echo "comision". $porcetajeComision;
$totalFinal4000=$sumaCuatromil+$utilidad4000+$comision4000;
//echo "Total ". $totalFinal4000;
$valorUnitarioCuatromil=$totalFinal4000/$cuatromil;
$valorUnitarioCuatromil=round($valorUnitarioCuatromil);
}

if ($cincomil!=NULL){
$sumaCincomil=$COTPAPEL5000+$COTPMX15000+$COTUVP5000+$COTANILLADODOBLEO5000+$COTEND5000+$COTPLANCHAS5000+$COTPMX25000+$COTSCRATCH5000+$COTANILLADOPLASTICO5000+$COTCOLAMINADO5000+$COTPELICULAS5000+$COTPBX15000+$COTNUMERADO5000+$COTREPUJE5000+$COTBOLSAPLASTICA5000+$COTCTP5000+$COTPBX25000+$COTTROQUEL5000+$COTREPUJADO5000+$COTOTROS5000+$COTPRUEBADIGITAL5000+$COTUVTX15000+$COTTROQUELADO5000+$COTPLEGADO5000+$COTDISENO5000+$COTTIRAJE5000+$COTUVTX25000+$COTHOTMELT5000+$COTGRAFA5000+$COTCAJAS5000;
//echo "sumaCincomil: ". $sumaCincomil;
$utilidad5000=($sumaCincomil)*($porcentajeUtilidad);
//echo "utilidad5000". $utilidad5000; 
$comision5000=($sumaCincomil)*($porcetajeComision);
//echo "comision". $porcetajeComision;
$totalFinal5000=$sumaCincomil+$utilidad5000+$comision5000;
//echo "Total ". $totalFinal5000;
$valorUnitarioCincomil=$totalFinal5000/$cincomil;
$valorUnitarioCincomil=round($valorUnitarioCincomil);
}

updateCotizacion ($vendedor, $cliente, $att, $fax, $referencia, $mil, $dosmil, $tresmil, $cuatromil,
							$cincomil, $afiche, $calendario, $cajas, $folleto, $volantes, $hablador, $libretas,
							$libro, $bolsas, $papeleria, $revista, $plegable, $sticker, $carpeta, $sobres, $tarjetas,
							$tarjeton, $otro, $papel, $caratula, $interiores, $tamanoAbiertoPapel, $tamanoCerradoPapel,
							$tintasPapel, $pantonePapel, $tamanoAbiertoCaratula, $tamanoCerradoCaratula, $tintasCaratula,
							$pantoneCaratula, $tamanoAbiertoInteriores, $tamanoCerradoInteriores, $tintasInteriores, $pantoneInteriores, $paginasInteriores,
							$peliculasnegativas, $peliculaspositivas, $peliculasarchivo, $peliculasOriginales, $acabadosmate1caras,
							$acabadosmate2caras, $plastmateInteriores1cara, $plastmateInteriores2cara, $brillolitografico,
							$repujado, $plastbrillante1cara, $plastbrillante2cara, $plastbrillanteInteriores1cara,
							$plastbrillanteInteriores2cara, $grafado, $estampado, $acabadosuvtotal1caras, $acabadosuvtotal2caras,
							$acabadosuvtotalInteriores1caras, $acabadosuvtotalInteriores2caras, $perforado, $troquelado,
							$acabadosuvparcial1caras, $acabadosuvparcial2caras, $acabadosuvparcialInteriores1caras, $acabadosuvparcialInteriores2caras,
							$numerado, $numeradodel, $numeradohasta, $plegado, $centro, $cuerpos2, $cuerpos3, $cuerpos4, $otroEncuadernacion,
							$encolado, $caballete, $pegado, $hotmelt, $cintadoblefaz, $ensanduchado, $anilladocolor, $anilladodobleo, $anilladoplastico,
							$anilladoOtro, $empaquepapelkraft, $empaquecajasdecarton, $empaquebolsaplastica, $empaqueexportacion, $fechaEntrega, 
							$observaciones, $COTPAPEL1000, $COTPAPEL2000, $COTPAPEL3000, $COTPAPEL4000, $COTPAPEL5000, $COTPMX11000,
							$COTPMX12000, $COTPMX13000, $COTPMX14000, $COTPMX15000, $COTUVP1000, $COTUVP2000, $COTUVP3000,
							$COTUVP4000, $COTUVP5000, $COTANILLADODOBLEO1000, $COTANILLADODOBLEO2000, $COTANILLADODOBLEO3000, $COTANILLADODOBLEO4000,
							$COTANILLADODOBLEO5000, $COTEND1000, $COTEND2000, $COTEND3000, $COTEND4000, $COTEND5000, $COTPLANCHAS1000,
							$COTPLANCHAS2000, $COTPLANCHAS3000, $COTPLANCHAS4000, $COTPLANCHAS5000, $COTPMX21000, $COTPMX22000,
							$COTPMX23000, $COTPMX24000, $COTPMX25000, $COTSCRATCH1000, $COTSCRATCH2000, $COTSCRATCH3000, $COTSCRATCH4000,
							$COTSCRATCH5000, $COTANILLADOPLASTICO1000, $COTANILLADOPLASTICO2000, $COTANILLADOPLASTICO3000, $COTANILLADOPLASTICO4000,
							$COTANILLADOPLASTICO5000, $COTCOLAMINADO1000, $COTCOLAMINADO2000, $COTCOLAMINADO3000, $COTCOLAMINADO4000,
							$COTCOLAMINADO5000, $COTPELICULAS1000, $COTPELICULAS2000, $COTPELICULAS3000, $COTPELICULAS4000, $COTPELICULAS5000, $COTPBX11000, $COTPBX12000,
							$COTPBX13000, $COTPBX14000, $COTPBX15000, $COTNUMERADO1000, $COTNUMERADO2000, $COTNUMERADO3000, $COTNUMERADO4000,
							$COTNUMERADO5000, $COTREPUJE1000, $COTREPUJE2000, $COTREPUJE3000, $COTREPUJE4000, $COTREPUJE5000,
							$COTBOLSAPLASTICA1000, $COTBOLSAPLASTICA2000, $COTBOLSAPLASTICA3000, $COTBOLSAPLASTICA4000, $COTBOLSAPLASTICA5000, $COTCTP1000,
							$COTCTP2000, $COTCTP3000, $COTCTP4000, $COTCTP5000,  $COTPBX21000, $COTPBX22000, $COTPBX23000,
							$COTPBX24000, $COTPBX25000, $COTTROQUEL1000, $COTTROQUEL2000, $COTTROQUEL3000, $COTTROQUEL4000, $COTTROQUEL5000,
							$COTREPUJADO1000, $COTREPUJADO2000, $COTREPUJADO3000, $COTREPUJADO4000, $COTREPUJADO5000, $COTOTROS1000, $COTOTROS2000,
							$COTOTROS3000, $COTOTROS4000, $COTOTROS5000, $COTPRUEBADIGITAL1000, $COTPRUEBADIGITAL2000, $COTPRUEBADIGITAL3000,
							$COTPRUEBADIGITAL4000, $COTPRUEBADIGITAL5000, $COTUVTX11000, $COTUVTX12000, $COTUVTX13000, $COTUVTX14000,
							$COTUVTX15000, $COTTROQUELADO1000, $COTTROQUELADO2000, $COTTROQUELADO3000, $COTTROQUELADO4000, $COTTROQUELADO5000,
							$COTPLEGADO1000, $COTPLEGADO2000, $COTPLEGADO3000, $COTPLEGADO4000, $COTPLEGADO5000, $COTDISENO1000,
							$COTDISENO2000, $COTDISENO3000, $COTDISENO4000, $COTDISENO5000, $COTTIRAJE1000, $COTTIRAJE2000,
							$COTTIRAJE3000, $COTTIRAJE4000, $COTTIRAJE5000, $COTUVTX21000, $COTUVTX22000, $COTUVTX23000, $COTUVTX24000,
							$COTUVTX25000, $COTHOTMELT1000, $COTHOTMELT2000, $COTHOTMELT3000, $COTHOTMELT4000, $COTHOTMELT5000,
							$COTGRAFA1000, $COTGRAFA2000, $COTGRAFA3000, $COTGRAFA4000, $COTGRAFA5000, $COTCAJAS1000, $COTCAJAS2000,
							$COTCAJAS3000, $COTCAJAS4000, $COTCAJAS5000, 
							$comisionMil, $utilidadMil, $sumaMil, $totalFinalMil,
							$comision2000, $utilidad2000, $sumaDosmil, $totalFinal2000,
							$comision3000, $utilidad3000, $sumaTresmil, $totalFinal3000,
							$comision4000, $utilidad4000, $sumaCuatromil, $totalFinal4000,
							$comision5000, $utilidad5000, $sumaCincomil, $totalFinal5000, $id, 
							$valorUnitarioMil, $valorUnitarioDosmil, $valorUnitarioTresmil, $valorUnitarioCuatromil, $valorUnitarioCincomil, 
							$db);
$valoresCotizacion=seleccionarCotizacion ($id, $db);	



foreach ($valoresCotizacion as $myrows){
	$fecha_alta=$myrows['FECHA_DE_ALTA'];
	$cliente= $myrows['CLIENTE'];
	$att= $myrows['ATT'];
	$fax= $myrows['FAX'];
	$referencia= $myrows['REFERENCIA'];
	$mil= $myrows['CANTIDAD1000'];
	$dosmil= $myrows['CANTIDAD2000'];
	$tresmil= $myrows['CANTIDAD3000'];
	$cuatromil=$myrows['CANTIDAD4000'];
	$cincomil= $myrows['CANTIDAD5000'];
	$afiche= $myrows['AFICHE'];
	$calendario= $myrows['CALENDARIO'];
	$cajas= $myrows['CAJAS'];
	$folleto= $myrows['FOLLETO'];
	$volantes=$myrows['VOLANTES'];
	$hablador= $myrows['HABLADOR'];
	$libretas= $myrows['LIBRETAS'];
	$libro= $myrows['LIBRO'];
	$bolsas= $myrows['BOLSAS'];
	$papeleria=$myrows['PAPELERIA'];
	$revista= $myrows['REVISTA'];
	$plegable= $myrows['PLEGABLE'];
	$sticker= $myrows['STICKER'];
	$carpeta= $myrows['CARPETA'];
	$sobres= $myrows['SOBRES'];
	$tarjetas= $myrows['TARJETAS'];
	$tarjeton= $myrows['TARJETON'];
	$otro= $myrows['OTRO'];
	$papel=$myrows['PAPEL'];
	$caratula=$myrows['CARATULA'];
	$interiores=$myrows['INTERIORES'];
	$tamanoAbiertoPapel=$myrows['TAMANOABIERTOPAPEL'];
	$tamanoCerradoPapel=$myrows['TAMANOCERRADOPAPEL'];
	$tintasPapel=$myrows['TINTASPAPEL'];
	$pantonePapel=$myrows['PANTONEPAPEL'];
	$tamanoAbiertoCaratula=$myrows['TAMANOABIERTOCARATULA'];
	$tamanoCerradoCaratula=$myrows['TAMANOCERRADOCARATULA'];
	$tintasCaratula=$myrows['TINTASCARATULA'];
	$pantoneCaratula=$myrows['PANTONECARATULA'];
	$tamanoAbiertoInteriores=$myrows['TAMANOABIERTOINTERIORES'];
	$tamanoCerradoInteriores=$myrows['TAMANOCERRADOINTERIORES'];
	$tintasInteriores=$myrows['TINTASINTERIORES'];
	$pantoneInteriores=$myrows['PANTONEINTERIORES'];
	$paginasInteriores=$myrows['PAGINASINTERIORES'];
	$peliculasnegativas=$myrows['PELICULASNEGATIVAS'];
	$peliculaspositivas=$myrows['PELICULASPOSITIVAS'];
	$peliculasarchivo=$myrows['PELICULASARCHIVO'];
	$peliculasOriginales=$myrows['PELICULASORIGINALES'];
	$acabadosmate1caras=$myrows['ACABADOSMATE1CARAS'];
	$acabadosmate2caras=$myrows['ACABADOSMATE2CARAS'];
	$plastmateInteriores1cara=$myrows['PLASTMATEINTERIORES1CARA'];
	$plastmateInteriores2cara=$myrows['PLASTMATEINTERIORES2CARA'];
	$brillolitografico=$myrows['BRILLOLITOGRAFICO'];
	$repujado=$myrows['REPUJADO'];
	$plastbrillante1cara=$myrows['PLASTBRILLANTE1CARA'];
	$plastbrillante2cara=$myrows['PLASTBRILLANTE2CARA'];
	$plastbrillanteInteriores1cara=$myrows['PLASTBRILLANTEINTERIORES1CARA'];
	$plastbrillanteInteriores2cara=$myrows['PLASTBRILLANTEINTERIORES2CARA'];
	$grafado=$myrows['GRAFADO'];
	$estampado=$myrows['ESTAMPADO'];
	$acabadosuvtotal1caras=$myrows['ACABADOSUVTOTAL1CARAS'];
	$acabadosuvtotal2caras=$myrows['ACABADOSUVTOTAL2CARAS'];
	$acabadosuvtotalInteriores1caras=$myrows['ACABADOSUVTOTALINTERIORES1CARAS'];
	$acabadosuvtotalInteriores2caras=$myrows['ACABADOSUVTOTALINTERIORES2CARAS'];
	$perforado=$myrows['PERFORADO'];
	$troquelado=$myrows['TROQUELADO'];
	$acabadosuvparcial1caras=$myrows['ACABADOSUVPARCIAL1CARAS'];
	$acabadosuvparcial2caras=$myrows['ACABADOSUVPARCIAL2CARAS'];
	$acabadosuvparcialInteriores1caras=$myrows['ACABADOSUVPARCIALINTERIORES1CARAS'];
	$acabadosuvparcialInteriores2caras=$myrows['ACABADOSUVPARCIALINTERIORES2CARAS'];
	$numerado=$myrows['NUMERADO'];
	$numeradodel=$myrows['NUMERADODEL'];
	$numeradohasta=$myrows['NUMERADOHASTA'];
	$plegado=$myrows['PLEGADO'];
	$centro=$myrows['CENTRO'];
	$cuerpos2=$myrows['CUERPOS2'];
	$cuerpos3=$myrows['CUERPOS3'];
	$cuerpos4=$myrows['CUERPOS4'];
	$otroEncuadernacion=$myrows['OTROENCUADERNACION'];
	$encolado=$myrows['ENCOLADO'];
	$caballete=$myrows['CABALLETE'];
	$pegado=$myrows['PEGADO'];
	$hotmelt=$myrows['HOTMELT'];
	$cintadoblefaz=$myrows['CINTADOBLEFAZ'];
	$ensanduchado=$myrows['ENSANDUCHADO'];
	$anilladocolor=$myrows['ANILLADOCOLOR'];
	$anilladodobleo=$myrows['ANILLADODOBLEO'];
	$anilladoplastico=$myrows['ANILLADOPLASTICO'];
	$anilladoOtro=$myrows['ANILLADOOTRO'];
	$empaquepapelkraft=$myrows['EMPAQUEPAPELKRAFT'];
	$empaquecajasdecarton=$myrows['EMPAQUECAJASDECARTON'];
	$empaquebolsaplastica=$myrows['EMPAQUEBOLSAPLASTICA'];
	$empaqueexportacion=$myrows['EMPAQUEEXPORTACION'];
	$fechaEntrega=$myrows['FECHAENTREGA'];
	$observaciones=$myrows['OBSERVACIONES'];
	$COTPAPEL1000=$myrows['COTPAPEL1000'];
	$COTPAPEL2000=$myrows['COTPAPEL2000'];
	$COTPAPEL3000=$myrows['COTPAPEL3000'];
	$COTPAPEL4000=$myrows['COTPAPEL4000'];
	$COTPAPEL5000=$myrows['COTPAPEL5000'];
	$COTPMX11000=$myrows['COTPMX11000'];
	$COTPMX12000=$myrows['COTPMX12000'];
	$COTPMX13000=$myrows['COTPMX13000'];
	$COTPMX14000=$myrows['COTPMX14000'];
	$COTPMX15000=$myrows['COTPMX15000'];
	$COTUVP1000=$myrows['COTUVP1000'];
	$COTUVP2000=$myrows['COTUVP2000'];
	$COTUVP3000=$myrows['COTUVP3000'];
	$COTUVP4000=$myrows['COTUVP4000'];
	$COTUVP5000=$myrows['COTUVP5000'];
	$COTANILLADODOBLEO1000=$myrows['COTANILLADODOBLEO1000'];
	$COTANILLADODOBLEO2000=$myrows['COTANILLADODOBLEO2000'];
	$COTANILLADODOBLEO3000=$myrows['COTANILLADODOBLEO3000'];
	$COTANILLADODOBLEO4000=$myrows['COTANILLADODOBLEO4000'];
	$COTANILLADODOBLEO5000=$myrows['COTANILLADODOBLEO5000'];
	$COTEND1000=$myrows['COTEND1000'];
	$COTEND2000=$myrows['COTEND2000'];
	$COTEND3000=$myrows['COTEND3000'];
	$COTEND4000=$myrows['COTEND4000'];
	$COTEND5000=$myrows['COTEND5000'];
	$COTPLANCHAS1000=$myrows['COTPLANCHAS1000'];
	$COTPLANCHAS2000=$myrows['COTPLANCHAS2000'];
	$COTPLANCHAS3000=$myrows['COTPLANCHAS3000'];
	$COTPLANCHAS4000=$myrows['COTPLANCHAS4000'];
	$COTPLANCHAS5000=$myrows['COTPLANCHAS5000'];
	$COTPMX21000=$myrows['COTPMX21000'];
	$COTPMX22000=$myrows['COTPMX22000'];
	$COTPMX23000=$myrows['COTPMX23000'];
	$COTPMX24000=$myrows['COTPMX24000'];
	$COTPMX25000=$myrows['COTPMX25000'];
	$COTSCRATCH1000=$myrows['COTSCRATCH1000'];
	$COTSCRATCH2000=$myrows['COTSCRATCH2000'];
	$COTSCRATCH3000=$myrows['COTSCRATCH3000'];
	$COTSCRATCH4000=$myrows['COTSCRATCH4000'];
	$COTSCRATCH5000=$myrows['COTSCRATCH5000'];
	$COTANILLADOPLASTICO1000=$myrows['COTANILLADOPLASTICO1000'];
	$COTANILLADOPLASTICO2000=$myrows['COTANILLADOPLASTICO2000'];
	$COTANILLADOPLASTICO3000=$myrows['COTANILLADOPLASTICO3000'];
	$COTANILLADOPLASTICO4000=$myrows['COTANILLADOPLASTICO4000'];
	$COTANILLADOPLASTICO5000=$myrows['COTANILLADOPLASTICO5000'];
	$COTCOLAMINADO1000=$myrows['COTCOLAMINADO1000'];
	$COTCOLAMINADO2000=$myrows['COTCOLAMINADO2000'];
	$COTCOLAMINADO3000=$myrows['COTCOLAMINADO3000'];
	$COTCOLAMINADO4000=$myrows['COTCOLAMINADO4000'];
	$COTCOLAMINADO5000=$myrows['COTCOLAMINADO5000'];
	$COTPELICULAS1000=$myrows['COTPELICULAS1000'];
	$COTPELICULAS2000=$myrows['COTPELICULAS2000'];
	$COTPELICULAS3000=$myrows['COTPELICULAS3000'];
	$COTPELICULAS4000=$myrows['COTPELICULAS4000'];
	$COTPELICULAS5000=$myrows['COTPELICULAS5000'];
	$COTPBX11000=$myrows['COTPBX11000'];
	$COTPBX12000=$myrows['COTPBX12000'];
	$COTPBX13000=$myrows['COTPBX13000'];
	$COTPBX14000=$myrows['COTPBX14000'];
	$COTPBX15000=$myrows['COTPBX15000'];
	$COTNUMERADO1000=$myrows['COTNUMERADO1000'];
	$COTNUMERADO2000=$myrows['COTNUMERADO2000'];
	$COTNUMERADO3000=$myrows['COTNUMERADO3000'];
	$COTNUMERADO4000=$myrows['COTNUMERADO4000'];
	$COTNUMERADO5000=$myrows['COTNUMERADO5000'];
	$COTREPUJE1000=$myrows['COTREPUJE1000'];
	$COTREPUJE2000=$myrows['COTREPUJE2000'];
	$COTREPUJE3000=$myrows['COTREPUJE3000'];
	$COTREPUJE4000=$myrows['COTREPUJE4000'];
	$COTREPUJE5000=$myrows['COTREPUJE5000'];
	$COTBOLSAPLASTICA1000=$myrows['COTBOLSAPLASTICA1000'];
	$COTBOLSAPLASTICA2000=$myrows['COTBOLSAPLASTICA2000'];
	$COTBOLSAPLASTICA3000=$myrows['COTBOLSAPLASTICA3000'];
	$COTBOLSAPLASTICA4000=$myrows['COTBOLSAPLASTICA4000'];
	$COTBOLSAPLASTICA5000=$myrows['COTBOLSAPLASTICA5000'];
	$COTCTP1000=$myrows['COTCTP1000'];
	$COTCTP2000=$myrows['COTCTP2000'];
	$COTCTP3000=$myrows['COTCTP3000'];
	$COTCTP4000=$myrows['COTCTP4000'];
	$COTCTP5000=$myrows['COTCTP5000'];
	$COTPBX21000=$myrows['COTPBX21000'];
	$COTPBX22000=$myrows['COTPBX22000'];
	$COTPBX23000=$myrows['COTPBX23000'];
	$COTPBX24000=$myrows['COTPBX24000'];
	$COTPBX25000=$myrows['COTPBX25000'];
	$COTTROQUEL1000=$myrows['COTTROQUEL1000'];
	$COTTROQUEL2000=$myrows['COTTROQUEL2000'];
	$COTTROQUEL3000=$myrows['COTTROQUEL3000'];
	$COTTROQUEL4000=$myrows['COTTROQUEL4000'];
	$COTTROQUEL5000=$myrows['COTTROQUEL5000'];
	$COTREPUJADO1000=$myrows['COTREPUJADO1000'];
	$COTREPUJADO2000=$myrows['COTREPUJADO2000'];
	$COTREPUJADO3000=$myrows['COTREPUJADO3000'];
	$COTREPUJADO4000=$myrows['COTREPUJADO4000'];
	$COTREPUJADO5000=$myrows['COTREPUJADO5000'];
	$COTOTROS1000=$myrows['COTOTROS1000'];
	$COTOTROS2000=$myrows['COTOTROS2000'];
	$COTOTROS3000=$myrows['COTOTROS3000'];
	$COTOTROS4000=$myrows['COTOTROS4000'];
	$COTOTROS5000=$myrows['COTOTROS5000'];
	$COTPRUEBADIGITAL1000=$myrows['COTPRUEBADIGITAL1000'];
	$COTPRUEBADIGITAL2000=$myrows['COTPRUEBADIGITAL2000'];
	$COTPRUEBADIGITAL3000=$myrows['COTPRUEBADIGITAL3000'];
	$COTPRUEBADIGITAL4000=$myrows['COTPRUEBADIGITAL4000'];
	$COTPRUEBADIGITAL5000=$myrows['COTPRUEBADIGITAL5000'];
	$COTUVTX11000=$myrows['COTUVTX11000'];
	$COTUVTX12000=$myrows['COTUVTX12000'];
	$COTUVTX13000=$myrows['COTUVTX13000'];
	$COTUVTX14000=$myrows['COTUVTX14000'];
	$COTUVTX15000=$myrows['COTUVTX15000'];
	$COTTROQUELADO1000=$myrows['COTTROQUELADO1000'];
	$COTTROQUELADO2000=$myrows['COTTROQUELADO2000'];
	$COTTROQUELADO3000=$myrows['COTTROQUELADO3000'];
	$COTTROQUELADO4000=$myrows['COTTROQUELADO4000'];
	$COTTROQUELADO5000=$myrows['COTTROQUELADO5000'];
	$COTPLEGADO1000=$myrows['COTPLEGADO1000'];
	$COTPLEGADO2000=$myrows['COTPLEGADO2000'];
	$COTPLEGADO3000=$myrows['COTPLEGADO3000'];
	$COTPLEGADO4000=$myrows['COTPLEGADO4000'];
	$COTPLEGADO5000=$myrows['COTPLEGADO5000'];
	$COTDISENO1000=$myrows['COTDISENO1000'];
	$COTDISENO2000=$myrows['COTDISENO2000'];
	$COTDISENO3000=$myrows['COTDISENO3000'];
	$COTDISENO4000=$myrows['COTDISENO4000'];
	$COTDISENO5000=$myrows['COTDISENO5000'];
	$COTTIRAJE1000=$myrows['COTTIRAJE1000'];
	$COTTIRAJE2000=$myrows['COTTIRAJE2000'];
	$COTTIRAJE3000=$myrows['COTTIRAJE3000'];
	$COTTIRAJE4000=$myrows['COTTIRAJE4000'];
	$COTTIRAJE5000=$myrows['COTTIRAJE5000'];
	$COTUVTX21000=$myrows['COTUVTX21000'];
	$COTUVTX22000=$myrows['COTUVTX22000'];
	$COTUVTX23000=$myrows['COTUVTX23000'];
	$COTUVTX24000=$myrows['COTUVTX24000'];
	$COTUVTX25000=$myrows['COTUVTX25000'];
	$COTHOTMELT1000=$myrows['COTHOTMELT1000'];
	$COTHOTMELT2000=$myrows['COTHOTMELT2000'];
	$COTHOTMELT3000=$myrows['COTHOTMELT3000'];
	$COTHOTMELT4000=$myrows['COTHOTMELT4000'];
	$COTHOTMELT5000=$myrows['COTHOTMELT5000'];
	$COTGRAFA1000=$myrows['COTGRAFA1000'];
	$COTGRAFA2000=$myrows['COTGRAFA2000'];
	$COTGRAFA3000=$myrows['COTGRAFA3000'];
	$COTGRAFA4000=$myrows['COTGRAFA4000'];
	$COTGRAFA5000=$myrows['COTGRAFA5000'];
	$COTCAJAS1000=$myrows['COTCAJAS1000'];
	$COTCAJAS2000=$myrows['COTCAJAS2000'];
	$COTCAJAS3000=$myrows['COTCAJAS3000'];
	$COTCAJAS4000=$myrows['COTCAJAS4000'];
	$COTCAJAS5000=$myrows['COTCAJAS5000'];
	$comisionMil=$myrows['COMISION1000'];
	$comision2000=$myrows['COMISION2000'];
	$comision3000=$myrows['COMISION3000'];
	$comision4000=$myrows['COMISION4000'];
	$comision5000=$myrows['COMISION5000'];
	$utilidadMil= $myrows['UTILIDAD1000']; 
	$utilidad2000=$myrows['UTILIDAD2000'];
	$utilidad3000=$myrows['UTILIDAD3000'];
	$utilidad4000=$myrows['UTILIDAD4000'];
	$utilidad5000=$myrows['UTILIDAD5000'];
	$sumaMil=$myrows['SUBTOTAL1000'];
	$sumaDosmil=$myrows['SUBTOTAL2000'];
	$sumaTresmil=$myrows['SUBTOTAL3000'];
	$sumaCuatromil=$myrows['SUBTOTAL4000'];
	$sumaCincomil=$myrows['SUBTOTAL5000'];
    $totalFinalMil=$myrows['TOTAL1000'];
	$totalFinal2000=$myrows['TOTAL2000'];
	$totalFinal3000=$myrows['TOTAL3000'];
	$totalFinal4000=$myrows['TOTAL4000'];
	$totalFinal5000=$myrows['TOTAL5000'];
	$valorUnitarioMil=$myrows['VALORUNITARIO1000'];
	$valorUnitarioDosmil=$myrows['VALORUNITARIO2000'];
	$valorUnitarioTresmil=$myrows['VALORUNITARIO3000'];
	$valorUnitarioCuatromil=$myrows['VALORUNITARIO4000'];
	$valorUnitarioCincomil=$myrows['VALORUNITARIO5000'];
    
}				
?>

<html>
<head>
<title>Horizonte Impresores Orden de Producci�n</title>
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
<script language=JavaScript src="/javascript/cotizacion.js"></script>
</head>

<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="MM_preloadImages('/img/enviar2.gif')">
<form action="/formas/parsing/enviarCorreoCotizacion.php" method="post" name="frmCotizacion">
<table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr> 
    <td height="20">&nbsp;</td>
  </tr>
  <tr> 
    <td><img src="/img/logo.jpg" width="212" height="84"></td>
  </tr>
  <tr class="arial10prsrojaboldTitulo">
  	<td align="right"><B>SOLICITUD DE COTIZACI�N <? echo $id ?></B></td>
  </tr>
    <tr> 
    <td>&nbsp;</td>
  </tr>
  <tr align="right">
  	<td class="arial10prsrojabold">Vendedor&nbsp;<? echo $vendedor; ?></td>
  </tr>
  <tr> 
    <td width="600" height="17" valign="top"> 
	
			
		
	
	<table width="600" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td><img src="/img/img1.gif" width="600" height="17"></td>
        </tr>
        <tr> 
          <td width="600" valign="top" background="/img/img2.gif">
		   <table width="580" border="0" align="center" cellpadding="0" cellspacing="0">
              <tr> 
                <td><table width="580" border="0" align="left" cellpadding="0" cellspacing="1">
                    <tr> 
                    
                      <td width="277" valign="middle">
					  <table width="460" border="0" cellpadding="0" cellspacing="3" class="arial10prsrojabold">
                          <tr> 
                            <td width="53">Cliente: </td>
                            <td width="201" class="arial10ptsgrisnormal"><div align="left"> 
							<? echo $cliente; ?>
                              
                              </div></td>
							  <td><img src="/img/transp.gif" width="40"></td>
							  <td width="53">Att: </td>
							  <td width="201" class="arial10ptsgrisnormal">
							  	<? echo $att; ?>
							  </td>
                          </tr>
						 
                        </table></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td>
				<table width="580" border="0" align="center" cellpadding="0" cellspacing="0" class="arial10prsrojabold">
				             <tr>
						   	<td width="53">Fax: </td>
							  <td width="201" class="arial10ptsgrisnormal">
							  	<? echo $fax; ?>
							  </td>
							  
							  <td width="53">Referencia: </td>
							  <td width="201" class="arial10ptsgrisnormal">
							  	<? echo $referencia; ?>
							  </td>
							 </tr>
							 <tr>
							 	<td>&nbsp;</td>
							 </tr>
							 <tr>
							 <td>&nbsp;</td>
							 <td>&nbsp;</td>
							 <td>&nbsp;</td>
							  <td width="53" bgcolor="F5F4F4">Cantidad: </td>
							 		  <td width="311"> 
							  <td width="201" bgcolor="F5F4F4" class="arial10ptsgrisnormal">Valor 1
							 <input type="text" name="mil" value="<? echo $mil; ?>" size="5">
							 	
							 				
							  </td>
							  <td width="201" bgcolor="F5F4F4" class="arial10ptsgrisnormal">Valor 2
							
							 <input type="text" name="dosmil" value="<? echo $dosmil; ?>" size="5">
							 
							  </td>
							  <td width="201" bgcolor="F5F4F4" class="arial10ptsgrisnormal">Valor 3
							  
								  <input type="text" name="tresmil" value="<? echo $tresmil; ?>" size="5">
							  
							  </td>
							  <td width="201" bgcolor="F5F4F4" class="arial10ptsgrisnormal">Valor 4
							 
							  	 <input type="text" name="cuatromil" value="<? echo $cuatromil; ?>" size="5">
								
								
							  </td>
							  <td width="201" bgcolor="F5F4F4" class="arial10ptsgrisnormal">Valor 5
							  
							  	 <input type="text" name="cincomil" value="<? echo $cincomil; ?>" size="5" >
							 
							  </td>
						  </tr>
						  <tr>
							 	<td>&nbsp;</td>
							 </tr>
					</table>
				</td>
              </tr>
              <tr> 
                <td> <table width="580" border="0" align="center" cellpadding="0" cellspacing="0" class="arial10prsrojabold">
                    <tr> 
                      <td width="590" height="19" valign="top">Clase de trabajo:</td>
                    </tr>
                    <tr> 
                      <td height="19" valign="top">
					  <table width="580" border="0" align="center" cellpadding="0" cellspacing="2" class="arial10ptsgrisnormal">
                          <tr bgcolor="F5F4F4"> 
                            <td width="110"> <div align="right">Afiche 
							<?
								if ($afiche!=NULL){
							?>
                                <input type="checkbox" name="afiche" value="afiche" checked>
							<?
							}else{
							?>
								 <input type="checkbox" name="afiche" value="afiche">
							<?
							}
							?>
                              </div></td>
                            <td width="92"> <div align="right">Calendario 
							<?
								if ($calendario!=NULL){
							?>
                                <input type="checkbox" name="calendario" value="calendario" checked>
							<?
							}else{
							?>
							 <input type="checkbox" name="calendario" value="calendario">
							<?
							}
							?>
                              </div></td>
                            <td width="109"> <div align="right">Cajas 
							<?
								if ($cajas!=NULL){
							?>
                                <input type="checkbox" name="cajas" value="cajas" checked>
							<?
							}else{
							?>
								<input type="checkbox" name="cajas" value="cajas">
							<?
							}
							?>
                              </div></td>
                            <td width="89"> <div align="right">Folleto 
							<?
								if ($folleto!=NULL){
							?>
                                <input type="checkbox" name="folleto" value="folleto" checked>
							<?
							}else{
							?>
								<input type="checkbox" name="folleto" value="folleto">
							<?
							}
							?>
							
                              </div></td>
                            <td width="93"> <div align="right">Volantes 
							<?
								if ($volantes!=NULL){
							?>
                                <input type="checkbox" name="volantes" value="volantes" checked>
							<?
							}else{
							?>
							<input type="checkbox" name="volantes" value="volantes">
							<?
							}
							?>
                              </div></td>
                            <td width="83"> <div align="right">Hablador 
							<?
								if ($hablador!=NULL){
							?>
                                <input type="checkbox" name="hablador" value="hablador" checked>
							<?
							}else{
							?>
							<input type="checkbox" name="hablador" value="hablador">
							<?
							}
							?>
                              </div></td>
                          </tr>
                          <tr> 
                            <td><div align="right">Libretas 
							<?
								if ($libretas!=NULL){
							?>
                            <input type="checkbox" name="libretas" value="libretas" checked>
							<?
							}else{
							?>
							 <input type="checkbox" name="libretas" value="libretas">
							<?
							}
							?>
                              </div></td>
                            <td><div align="right">Libro 
							<?
							if ($libro!=NULL){
							?>
                                <input type="checkbox" name="libro" value="libro" checked>
							<?
							}else{
							?>
							<input type="checkbox" name="libro" value="libro">
							<?
							}
							?>
                              </div></td>
                            <td><div align="right">Bolsas
							<?
							if ($bolsas!=NULL){
							?>
                            <input type="checkbox" name="bolsas" value="bolsas" checked>
							<?
								}else{
							?>
							<input type="checkbox" name="bolsas" value="bolsas">
							<?
							}
							?>
                              </div></td>
                            <td><div align="right">Papeleria 
							<?
								if ($papeleria!=NULL){
							?>
                                <input type="checkbox" name="papeleria" value="papeleria" checked>
							<?
							}else{
							?>
							<input type="checkbox" name="papeleria" value="papeleria">
							<?
							}
							?>
                              </div></td>
                           <td> <div align="right">Revista 
						   <?
						   	if ($revista!=NULL){
						   ?>
                                <input type="checkbox" name="revista" value="revista" checked>
							<?
							}else{
							?>
								<input type="checkbox" name="revista" value="revista">
							<?
							}
							?>
                              </div></td>
                            <td><div align="right">Plagable 
							<?
							if ($plegable!=NULL){
							?>
                                <input type="checkbox" name="plegable" value="plegable" checked>
							<?
							}else{
							?>
								<input type="checkbox" name="plegable" value="plegable">
							<?
							}
							?>
                              </div></td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td> <div align="right">Sticker 
							<?
							if ($sticker!=NULL){
							?>
                                <input type="checkbox" name="sticker" value="sticker" checked>
							<?
							}else{
							?>
							<input type="checkbox" name="sticker" value="sticker">
							<?
							}
							?>
                              </div></td>
                            <td bgcolor="F5F4F4"> 
                              <div align="right">Carpeta 
							  <?
							  if ($carpeta!=NULL){
							  ?>
                                <input type="checkbox" name="carpeta" value="carpeta" checked>
							<?
							}else{
							?>
							<input type="checkbox" name="carpeta" value="carpeta">
							<?
							}
							?>
                              </div></td>
                            <td> <div align="right">Sobres 
							<?
								if ($sobres!=NULL){
							?>
                                <input type="checkbox" name="sobres" value="sobres" checked>
							<?
							}else{
							?>	
								<input type="checkbox" name="sobres" value="sobres">
							<?
							}
							?>
                              </div></td>
                            <td> <div align="right">Tarjetas
							<?
								if ($tarjetas!=NULL){
							?> 
                                <input type="checkbox" name="tarjetas" value="tarjetas" checked>
							<?
							}else{
							?>
							<input type="checkbox" name="tarjetas" value="tarjetas">
							<?
							}
							?>
                              </div></td>
                            <td bgcolor="F5F4F4"> <div align="right">Tarjeton 
							<?
								if ($tarjeton!=NULL){
							?>
                                <input type="checkbox" name="tarjeton" value="tarjeton" checked>
							<?
								}else{
							?>
							<input type="checkbox" name="tarjeton" value="tarjeton">
							
							<?
							}
							?>
                              </div></td>
							  <td>&nbsp;</td>
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
                      <td width="135"><div align="center">Otro:  
                         <spam class="arial10ptsgrisnormal"><b><? echo $otro ?></b></spam>
                        </div></td>
                      <td width="86">&nbsp;</td>
                      <td width="72">&nbsp;</td>
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
                <td><table width="580" border="0" cellpadding="0" cellspacing="7" class="arial10prsrojabold">
                    <tr> 
                      <td width="186" height="15"><div align="center"> 
                          <table width="186" border="0" cellspacing="2" cellpadding="0" class="arial10ptsgrisnormal">
                            <tr> 
                              <td colspan="2" class="arial10prsrojabold"width="83"  bgcolor="F5F4F4"><div align="center">PAPEL</div></td>
							  <td><? echo $papel; ?></td>
                            </tr>
                           </table>
                        </div></td>
                      <td width="186"><div align="center"> 
                          <table width="186" border="0" cellspacing="2" cellpadding="0" class="arial10ptsgrisnormal">
                            <tr> 
                              <td colspan="2" class="arial10prsrojabold" width="83" bgcolor="F5F4F4"><div align="center">CARATULA</div></td>
							  <td>
							  <? echo $caratula; ?>
							  </td>
                            </tr>
                           
                          </table>
                        </div></td>
                      <td width="187"><div align="center"> 
                          <table width="186" border="0" cellspacing="2" cellpadding="0" class="arial10ptsgrisnormal">
                            <tr> 
                              <td colspan="2" class="arial10prsrojabold" width="83" bgcolor="F5F4F4"><div align="center">INTERIORES</div></td>
							  <td>
							  <? echo $interiores; ?>
							  </td>
                            </tr>
                            
                          </table>
                        </div></td>
                    </tr>
                    <tr> 
                      <td height="94" valign="top"> 
					  <table width="186" border="0" align="center" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="83" bgcolor="F5F4F4"> 
                              <div align="right">Tama�o Abierto</div></td>
                            <td width="94" bgcolor="F5F4F4"> 
                              <? echo $tamanoAbiertoPapel; ?></td>
                          </tr>
                          <tr> 
                            <td height="22" bgcolor="F5F4F4"> 
                              <div align="right">Tama�o Cerrado</div></td>
                            <td bgcolor="F5F4F4">
							<? echo $tamanoCerradoPapel; ?>
                            </td>
                          </tr>
                          <tr> 
                            <td bgcolor="F5F4F4"> 
                              <div align="right">Tintas</div></td>
                            <td bgcolor="F5F4F4">
							<? echo $tintasPapel; ?>
                            </td>
                          </tr>
                          <tr> 
                            <td bgcolor="F5F4F4"> 
                              <div align="right">Pantone</div></td>

                            <td bgcolor="F5F4F4">
							<? echo $pantonePapel; ?>
                            </td>
                          </tr>
						  
                        </table></td>
                      <td><table width="186" border="0" align="center" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr bgcolor="F5F4F4"> 
                            <td width="84"> <div align="right">Tama�o Abierto</div></td>
                            <td width="93"><? echo $tamanoAbiertoCaratula; ?> 
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td height="22"> <div align="right">Tama&ntilde;o Cerrado</div></td>
                            <td><? echo $tamanoCerradoCaratula; ?>
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td> <div align="right">Tintas</div></td>
                            <td><? echo $tintasCaratula; ?>
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td bgcolor="F5F4F4"> <div align="right">Pantone</div></td>
                            <td><? echo $pantoneCaratula; ?>
                            </td>
                          </tr>
						   
                        </table></td>
                      <td><table width="186" border="0" align="center" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr bgcolor="F5F4F4"> 
                            <td width="85"> <div align="right">Tama�o Abierto</div></td>
                            <td width="92"><? echo $tamanoAbiertoInteriores; ?>
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td height="22"> <div align="right">Tama&ntilde;o Cerrado</div></td>
                            <td><? echo $tamanoCerradoInteriores; ?>
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td> <div align="right">Tintas</div></td>
                            <td><? echo $tintasInteriores; ?>
                            </td>
                          </tr>
                          <tr bgcolor="F5F4F4"> 
                            <td bgcolor="F5F4F4"> <div align="right">Pantone</div></td>
                            <td><? echo $pantoneInteriores; ?> 
                            </td>
                          </tr>
						  <tr bgcolor="F5F4F4"> 
                            <td bgcolor="F5F4F4"> <div align="right">P�ginas</div></td>
                            <td><? echo $paginasInteriores; ?>
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
					  	if ($peliculasnegativas!=NULL){
					  ?> 
                          <input type="checkbox" name="peliculasnegativas" value="peliculasnegativas" checked>
					  <?
					  }else{
					   ?>
					   <input type="checkbox" name="peliculasnegativas" value="peliculasnegativas">
					   <?
					   }
					   ?>
                        </div></td>
                      <td bgcolor="F5F4F4"> <div align="right">positivas 
					   <?
					  	if ($peliculaspositivas!=NULL){
					  ?> 
                          <input type="checkbox" name="peliculaspositivas" value="peliculaspositivas" checked>
					<?
					}else{
					?>
					<input type="checkbox" name="peliculaspositivas" value="peliculaspositivas">
					<?
					}
					?>
                        </div></td>
                      <td width="88" bgcolor="F5F4F4"> <div align="right">Archivo 
					  <?
					  if ($peliculasarchivo!=NULL){
					  ?>
                          <input type="checkbox" name="peliculasarchivo" value="peliculasarchivo" checked>
					<?
					}else{
					?>
					 <input type="checkbox" name="peliculasarchivo" value="peliculasarchivo">
					<?
					}
					?>
                        </div></td>
                      <td bgcolor="F5F4F4"> <div align="right">Originales
					 <?
					 if ($peliculasOriginales!=NULL){ 
					 ?>					  
                          <input type="checkbox" name="peliculasOriginales" value="peliculasOriginales" checked>
				     <?
					 }else{
					 ?>
					 <input type="checkbox" name="peliculasOriginales" value="peliculasOriginales">
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
					
					 <table width="235" border="0" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
					  <tr>
						  	<td class="arial10prsrojabold" bgcolor="F5F4F4"><div align="center">CARATULA / OTRO</td>
						  </tr>
					 </table>
					  <table width="235" border="0" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                         
						  <tr> 
                            <td width="111"><div align="right"> 
                                <p class="arial10ptsgrisBOLD">Plast. MATE</p>
                              </div></td>
                            <td width="65" bgcolor="F5F4F4"> <div align="center">1 cara 
							<?
								 if ($acabadosmate1caras!=NULL){ 
							?>			
                                <input type="checkbox" name="acabadosmate1caras" value="Plast. MATE 1 cara" checked>
							<?
							}else{
							?>
							<input type="checkbox" name="acabadosmate1caras" value="Plast. MATE 1 cara">
							<?
							}
							?>
                              </div></td>
                            <td width="80" bgcolor="F5F4F4"> <div align="center">2 caras 
							<?
								 if ($acabadosmate2caras!=NULL){ 
							?>	
                                <input type="checkbox" name="acabadosmate2caras" value="Plast. MATE 2 caras" checked>
							<?
							}else{
							?>
							<input type="checkbox" name="acabadosmate2caras" value="Plast. MATE 2 caras">
							<?
							}
							?>
                              </div></td>
                          </tr>
                        </table>
						
						</td>
                      <td width="124"> 
					  <table width="140" border="0" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
					  <tr>
						  	<td class="arial10prsrojabold" bgcolor="F5F4F4"><div align="center">PAGINAS INTERIORES</td>
						  </tr>
					 </table>
					  <table width="120" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">1 cara 
								<?
									if ($plastmateInteriores1cara!=NULL){
								?>
                                  <input type="checkbox" name="plastmateInteriores1cara" value="Plast. MATE Paginas interiores 1 cara" checked>
								<?
								}else{
								?>
								<input type="checkbox" name="plastmateInteriores1cara" value="Plast. MATE Paginas interiores 1 cara">
								<?
								}
								?>
                                </p>
                              </div></td>
							   <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">2 caras 
								<?
								if ($plastmateInteriores2cara!=NULL){
								?>
                                  <input type="checkbox" name="plastmateInteriores2cara" value="Plast. MATE Paginas interiores 2 caras" checked>
								 <?
								 }else{
								 ?>
								  <input type="checkbox" name="plastmateInteriores2cara" value="Plast. MATE Paginas interiores 2 caras">
								  <?
								  }
								  ?>
                                </p>
                              </div></td>
                          </tr>
                        </table>
						</td>
						<td>
					  <table width="115" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td bgcolor="F5F4F4"> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Brillo Litogr&aacute;fico
								<?
								if ($brillolitografico!=NULL){
								?> 
                                  <input type="checkbox" name="brillolitografico" value="brillolitografico" checked>
								 <?
								 }else{
								 ?>
								 <input type="checkbox" name="brillolitografico" value="brillolitografico">
								 <?
								 }
								 ?>
                                </p>
                              </div></td>
                          </tr>
                        </table>
						
						
                        <div align="center"></div></td>
                      <td width="130">
					  
					  <table width="95" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="129" bgcolor="F5F4F4"> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Repujado 
								<?
								if ($repujado!=NULL){
								?>
                                  <input type="checkbox" name="repujado" value="Repujado" checked>
								  <?
								 }else{
								  ?>
								  <input type="checkbox" name="repujado" value="Repujado">
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
                      <td><table width="235" border="0" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="110" height="15"> <div align="right"> 
                                <p class="arial10ptsgrisBOLD">Plast. BRILLANTE</p>
                              </div></td>
                            <td width="64" bgcolor="F5F4F4"> <div align="center">1 cara 
							<?
							if ($plastbrillante1cara!=NULL){
							?>
                                <input type="checkbox" name="plastbrillante1cara" value="Plast. BRILLANTE 1 cara" checked>
                            <?
							}else{
							?>
							 <input type="checkbox" name="plastbrillante1cara" value="Plast. BRILLANTE 1 cara">
							 <?
							 }
							 ?>
							  </div></td>
                            <td width="82" bgcolor="F5F4F4"> <div align="center">2 caras 
							<?
							if ($plastbrillante2cara!=NULL){
							?>
                                <input type="checkbox" name="plastbrillante2cara" value="Plast. BRILLANTE 2 caras" checked>
                           <?
						   }else{
						   ?>
						   <input type="checkbox" name="plastbrillante2cara" value="Plast. BRILLANTE 2 caras">
						   <?
						   }
						   ?>
						      </div></td>
                          </tr>
                        </table></td>
						<td>
						<table width="120" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">1 cara 
								<?
									if ($plastbrillanteInteriores1cara!=NULL){
								?>
                                  <input type="checkbox" name="plastbrillanteInteriores1cara" value="Plast. BRILLANTE Interiores 1 Cara" checked>
                                <?
								}else{
								?>
								<input type="checkbox" name="plastbrillanteInteriores1cara" value="Plast. BRILLANTE Interiores 1 Cara">
								<?
								}
								?>
							</p>
                              </div></td>
							   <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">2 caras
								 <?
									if ($plastbrillanteInteriores2cara!=NULL){
								?>
                                  <input type="checkbox" name="plastbrillanteInteriores2cara" value="Plast. BRILLANTE Interiores 2 Caras" checked>
                                <?
								}else{
								?>
								 <input type="checkbox" name="plastbrillanteInteriores2cara" value="Plast. BRILLANTE Interiores 2 Caras">
								 <?
								 }
								 ?>
								</p>
                              </div></td>
                          </tr>
						  </table>
						</td>
                      <td><table width="110" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td bgcolor="F5F4F4"> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Grafado 
								<?
									if ($grafado!=NULL){
								?>
                                  <input type="checkbox" name="grafado" value="Grafado" checked>
								 <?
								 }else{
								 ?> 
								 <input type="checkbox" name="grafado" value="Grafado">
								 <?
								 }
								 ?>
                                </p>
                              </div></td>
                          </tr>
                        </table></td>
                      <td><table width="95" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="128" bgcolor="F5F4F4"> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Estampado 
								<?
									if ($grafado!=NULL){
								?>
                                  <input type="checkbox" name="estampado" value="Estampado" checked>
								 <?
								 }else{

								 ?>
								 <input type="checkbox" name="estampado" value="Estampado">
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
					  
					  
					  <table width="235" border="0" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="108"><div align="right"> 
                                <p class="arial10ptsgrisBOLD">UV. TOTAL</p>
                              </div></td>
                            <td width="66" bgcolor="F5F4F4"> <div align="center">1 cara 
								<?
									if ($acabadosuvtotal1caras!=NULL){
								?>
                                <input type="checkbox" name="acabadosuvtotal1caras" value="UV. TOTAL 1 cara" checked>
                              <?
							  }else{
							  ?>
							  <input type="checkbox" name="acabadosuvtotal1caras" value="UV. TOTAL 1 cara">
							  <?
							  }
							  ?>
							  </div></td>
                            <td width="78" bgcolor="F5F4F4"> <div align="center">2 caras 
							<?
									if ($acabadosuvtotal2caras!=NULL){
								?>
                                <input type="checkbox" name="acabadosuvtotal2caras" value="UV. TOTAL 2 caras" checked>
							<?
							}else{
							?>
							<input type="checkbox" name="acabadosuvtotal2caras" value="UV. TOTAL 2 caras">
							<?
							}
							?>
                              </div></td>
                          </tr>
                        </table></td>
                      <td>
					  <table width="120" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">1 cara
								 <?
									if ($acabadosuvtotalInteriores1caras!=NULL){
								?>
                                  <input type="checkbox" name="acabadosuvtotalInteriores1caras" value="UV. TOTAL 1 cara Interiores" checked>
                               <?
							   }else{
							   ?>
							    <input type="checkbox" name="acabadosuvtotalInteriores1caras" value="UV. TOTAL 1 cara Interiores">
							   <?
							   }
							   ?>
							    </p>
                              </div></td>
							   <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">2 caras 
								 <?
									if ($acabadosuvtotalInteriores2caras!=NULL){
								?>
                                  <input type="checkbox" name="acabadosuvtotalInteriores2caras" value="UV. TOTAL 2 cara Interiores" checked>
                                <?
								}else{
								?>
								<input type="checkbox" name="acabadosuvtotalInteriores2caras" value="UV. TOTAL 2 cara Interiores">
							   <?
							   } 
							   ?>	
					
								</p>
                              </div></td>
                          </tr>
						  </table>
					  </td>
					  <td><table width="110" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td bgcolor="F5F4F4"> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Perforado 
								<?
								if ($perforado!=NULL){
								?>
                                  <input type="checkbox" name="perforado" value="Perforado" checked>
								<?
								}else{
								?>
								<input type="checkbox" name="perforado" value="Perforado">
								<?
								}
								?>
                                </p>
                              </div></td>
                          </tr>
                        </table></td>
                      <td><table width="95" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="130" bgcolor="F5F4F4"> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Troquelado
								 <?
								if ($troquelado!=NULL){
								?>
                                  <input type="checkbox" name="troquelado" value="Troquelado" checked>
								 <?
								 }else{
								 ?>
								  <input type="checkbox" name="troquelado" value="Troquelado">
								  <?
								  }
								  ?> 
                                </p>
                              </div></td>
                          </tr>
                        </table></td>
                    </tr>
                    <tr> 
                      <td><table width="235" border="0" cellpadding="0" cellspacing="3" class="arial10ptsgrisnormal">
                          <tr> 
                            <td width="109"><div align="right"> 
                                <p class="arial10ptsgrisBOLD">UV PARCIAL</p>
                              </div></td>
                            <td width="65" bgcolor="F5F4F4"> <div align="center">1 cara 
							<?
							if ($acabadosuvparcial1caras!=NULL){
							?>
                                <input type="checkbox" name="acabadosuvparcial1caras" value="UV PARCIAL 1 cara" checked>
                            <?
							}else{
							?>
							<input type="checkbox" name="acabadosuvparcial1caras" value="UV PARCIAL 1 cara">
							<?
							}
							?>
							  </div></td>
                            <td width="78" bgcolor="F5F4F4"> <div align="center">2 caras 
							<?
							if ($acabadosuvparcial2caras!=NULL){
							?>
                                <input type="checkbox" name="acabadosuvparcial2caras" value="UV PARCIAL 2 caras">
							<?
							}else{
							?>
							<input type="checkbox" name="acabadosuvparcial2caras" value="UV PARCIAL 2 caras">
							<?
							}
							?>
                              </div></td>
                          </tr>
                        </table></td>
						<td>
						<table width="120" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">1 cara 
								<?
								if ($acabadosuvparcialInteriores1caras!=NULL){
								?>
                                  <input type="checkbox" name="acabadosuvparcialInteriores1caras" value="UV PARCIAL 1 cara Interiores" checked>
								 <?
								 }else{
								 ?>
								  <input type="checkbox" name="acabadosuvparcialInteriores1caras" value="UV PARCIAL 1 cara Interiores">
								 <?
								 }
								 ?>
                                </p>
                              </div></td>
							   <td> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">2 caras 
								<?
								if ($acabadosuvparcialInteriores2caras!=NULL){
								?>
                                  <input type="checkbox" name="acabadosuvparcialInteriores2caras" value="UV PARCIAL 2 cara Interiores" checked>
                               <?
							   }else{
							   ?>
							    <input type="checkbox" name="acabadosuvparcialInteriores2caras" value="UV PARCIAL 2 cara Interiores">
								<?
								}
								?>
							    </p>
                              </div></td>
                          </tr>
						  </table>
						</td>
						
                      <td><table width="110" border="0" align="left" cellpadding="0" cellspacing="1" class="arial10ptsgrisnormal">
                          <tr> 
                            <td bgcolor="F5F4F4"> <div align="right"> 
                                <p align="right" class="arial10ptsgrisBOLD">Numerado 
								<?
								if ($numerado!=NULL){
								?>
								<input type="checkbox" name="numerado" value="numerado" checked>
								<?
								}else{
								?>
								<input type="checkbox" name="numerado" value="numerado">
								<?
								}
								?>
                                </p>
                              </div></td>
                         </tr>
						 <tr>
						 <td bgcolor="F5F4F4"><div align="right"><span class="arial10ptsgrisnormal">Del 
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
                <td><table width="600" border="0" align="center" cellpadding="0" cellspacing="4" class="arial10ptsgrisBOLD">
                    <tr> 
                      <td width="128" bgcolor="F5F4F4"> <div align="center">Plegado 
					  <?
						if ($plegado!=NULL){
					   ?>
                          <input type="checkbox" name="plegado" value="Plegado" checked>
					  <?
					  	}else{
					  ?>
					  <input type="checkbox" name="plegado" value="Plegado">
					  <?
					  }
					  ?>
                        </div></td>
					<td width="108" bgcolor="F5F4F4"> <div align="center">centro 
					   <?
						if ($centro!=NULL){
					   ?>
                          <input type="checkbox" name="centro" value="centro" checked>
						<?
						}else{
						?>
						<input type="checkbox" name="centro" value="centro">
						<?
						}
						?>
                        </div></td>	
                      <td width="108" bgcolor="F5F4F4"> <div align="center">2 cuerpos 
					   <?
						if ($cuerpos2!=NULL){
					   ?>
                          <input type="checkbox" name="cuerpos2" value="2 cuerpos" checked>
					  <?
					  }else{
					  ?>
					  <input type="checkbox" name="cuerpos2" value="2 cuerpos">
					  <?
					  }
					  ?>
                        </div></td>
                      <td width="103" bgcolor="F5F4F4"> <div align="center">3 cuerpos 
					    <?
						if ($cuerpos3!=NULL){
					   ?>
                          <input type="checkbox" name="cuerpos3" value="3 cuerpos" checked>
					<?
					}else{
					?>
					 <input type="checkbox" name="cuerpos3" value="3 cuerpos">
					 <?
					 }
					 ?>
					
                        </div></td>
                      <td width="90" bgcolor="F5F4F4"> <div align="center">4 cuerpos 
					  <?
						if ($cuerpos4!=NULL){
					   ?>
                          <input type="checkbox" name="cuerpos4" value="4 cuerpos" checked>
						<?
						}else{
						?>
						 <input type="checkbox" name="cuerpos4" value="4 cuerpos">
						 <?
						 }
						 ?>
                        </div></td>
                      <td width="108" bgcolor="F5F4F4"><div align="center">Otro 
					   <?
						if ($otroEncuadernacion!=NULL){
					   ?>
                          <input type="checkbox" name="otroEncuadernacion" value="Otro" checked>
						<?
						}else{
						?>
						 <input type="checkbox" name="otroEncuadernacion" value="Otro">
						 <?
						 }
						 ?>
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td><table width="580" border="0" align="center" cellpadding="0" cellspacing="4" class="arial10ptsgrisnormal">
                    <tr> 
                      <td width="70" bgcolor="F5F4F4"> <div align="right">Encolado 
					  <?
						if ($encolado!=NULL){
					   ?>
                          <input type="checkbox" name="encolado" value="Encolado" checked>
						  <?
						  }else{
						  ?>
						  <input type="checkbox" name="encolado" value="Encolado">
						  <?
						  }
						  ?>
                        </div></td>
                      <td width="102" bgcolor="F5F4F4"> <div align="right">Caballete 
					  <?
						if ($caballete!=NULL){
					   ?>
                          <input type="checkbox" name="caballete" value="Caballete" checked>
						<?
						}else{
						?>
						<input type="checkbox" name="caballete" value="Caballete">
						<?
						}
						?>
                        </div></td>
                      <td width="87" bgcolor="F5F4F4"> <div align="right">Pegado 
					   <?
						if ($pegado!=NULL){
					   ?>
                          <input type="checkbox" name="pegado" value="Pegado" checked>
						<?
						}else{
						?>
						 <input type="checkbox" name="pegado" value="Pegado">
						 <?
						 }
						 ?>
						 
                        </div></td>
                      <td width="70" bgcolor="F5F4F4"> <div align="right">Hotmelt 
					  <?
						if ($hotmelt!=NULL){
					   ?>
                          <input type="checkbox" name="hotmelt" value="Hotmelt" checked>
						<?
						}else{
						?>
						 <input type="checkbox" name="hotmelt" value="Hotmelt">
						 <?
						 }
						 ?>
                        </div></td>
					  <td width="120" bgcolor="F5F4F4"><div align="right">Cinta de doble faz 
					  <?
						if ($cintadoblefaz!=NULL){
					   ?>
                          <input type="checkbox" name="cintadoblefaz" value="Cinta de doble faz" checked>
						<?
						}else{
						?>
						<input type="checkbox" name="cintadoblefaz" value="Cinta de doble faz">
						<?
						}
						?>
                        </div></td>
						<td width="100" bgcolor="F5F4F4"><div align="right">Ensanduchado 
						 <?
							if ($ensanduchado!=NULL){
						  ?>
                          <input type="checkbox" name="ensanduchado" value="Ensanduchado" checked>
						  <?
						  }else{
						  ?>
						  <input type="checkbox" name="ensanduchado" value="Ensanduchado">
						  <?
						  }
						  ?>
                        </div></td>
                    </tr>
                   </table></td>
              </tr>
              <tr> 
                <td>&nbsp;</td>
              </tr>
              <tr> 
                <td class="arial10prsrojabold"> 
				<table width="580" border="0" align="center" cellpadding="0" cellspacing="4" class="arial10ptsgrisBOLD">
                    <tr> 
                      <td width="87"> <div align="center" class="arial10prsrojabold">ANILLADO 
                        </div></td>
                      <td width="179" bgcolor="F5F4F4"> <div align="center">Color
					   <?
							if ($anilladocolor!=NULL){
						  ?>
                          <input type="checkbox" name="anilladocolor" value="Color" checked>
						  <?
						  }else{
						  ?>
						  <input type="checkbox" name="anilladocolor" value="Color">
						<?
						}
						?>
						
                        </div></td>
                      <td width="157" bgcolor="F5F4F4"> <div align="center">Doble O 
					  	<?
							if ($anilladodobleo!=NULL){
						  ?>
                          <input type="checkbox" name="anilladodobleo" value="Doble O" checked>
						  <?
						  }else{
						  ?>
						  <input type="checkbox" name="anilladodobleo" value="Doble O">
						  <?
						  }
						  ?>						  
                        </div></td>
                      <td width="137" bgcolor="F5F4F4"> <div align="center">Plastico
					  <?
							if ($anilladoplastico!=NULL){
						  ?>
                          <input type="checkbox" name="anilladoplastico" value="Plastico" checked>
						  <?
						  }else{
						  ?>
						  <input type="checkbox" name="anilladoplastico" value="Plastico">
						  <?
						  }
						  ?>
                        </div></td>
						<td width="137" bgcolor="F5F4F4"> <div align="center">Otro 
						 <?
							if ($anilladoOtro!=NULL){
						  ?>
                         <input type="checkbox" name="anilladoOtro" value="Otro" checked>
						 <?
						 }else{
						 ?>
						 <input type="checkbox" name="anilladoOtro" value="Otro">
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
							if ($empaquepapelkraft!=NULL){
						  ?>
                          <input type="checkbox" name="empaquepapelkraft" value="Papel Kraft" checked>
						  <?
						  }else{
						  ?>
						  <input type="checkbox" name="empaquepapelkraft" value="Papel Kraft">
						  <?
						  }
						  ?>
                        </div></td>
                      <td width="126" bgcolor="F5F4F4"> <div align="center">Cajas de cart&oacute;n 
					  <?
							if ($empaquecajasdecarton!=NULL){
						  ?>
                          <input type="checkbox" name="empaquecajasdecarton" value="Cajas de carton" checked>
						  <?
						  }else{
						  ?>
						  <input type="checkbox" name="empaquecajasdecarton" value="Cajas de carton">
						  <?
						  }
						  ?>
                        </div></td>
                      <td width="105" bgcolor="F5F4F4"> <div align="center">Bolsa plastica 
					   	   <?
							if ($empaquebolsaplastica!=NULL){
						  ?>
                          <input type="checkbox" name="empaquebolsaplastica" value="Bolsa plastica" checked>
						  <?
						  	}else{
						  ?>
						  <input type="checkbox" name="empaquebolsaplastica" value="Bolsa plastica">
						  <?
						  }
						  ?>
                        </div></td>
                      <td width="122" bgcolor="F5F4F4"><div align="center">Exportaci&oacute;n 
					   <?
							if ($empaqueexportacion!=NULL){
						  ?>
                          <input type="checkbox" name="empaqueexportacion" value="Exportacion" checked>
						  <?
						  }else{
						  ?>
						  <input type="checkbox" name="empaqueexportacion" value="Exportacion">
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
          <td width="600" valign="top" background="/img/img2.gif">
		  
		  <table width="550" border="0" align="center" cellpadding="0" cellspacing="4">
              <tr> 
                <td width="106" valign="top" class="arial10prsrojabold"> <div align="right">Observaciones 
                    :</div></td>
                <td width="407" class="arial10ptsgrisnormal"> <div align="center"> 
                    <? echo $observaciones; ?>
                  </div></td>
              </tr>
            </table>
            <table width="550" border="0" align="center" cellpadding="0" cellspacing="5">
              <tr> 
                <td width="312" height="26">
<table width="312" border="0" align="left" cellpadding="0" cellspacing="1">
                    <tr> 
                      <td width="104" class="arial10prsrojabold"> Fecha de entrega</td>
                       <td width="63" valign="middle"><span class="arial10ptsgrisnormal"><? echo $fechaEntrega; ?></span> 
                        
                      </td>
                    </tr>
									
                  </table>
				  
				 
				  </td>
				 </tr> 
				 <tr>
				 <td>
						 <table class="arial10ptsgrisBOLD">
						<tr bgcolor="F5F4F4" align="center">
								<td>&nbsp;</td>
								<td>Cantidad 1</td>
								<td>Cantidad 2</td>
								<td>Cantidad 3</td>
								<td>Cantidad 4</td>
								<td>Cantidad 5</td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Papel</td>
								<td><? echo $COTPAPEL1000; ?></td>
								<td><? echo $COTPAPEL2000; ?></td>
								<td><? echo $COTPAPEL3000; ?></td>
								<td><? echo $COTPAPEL4000; ?></td>
								<td><? echo $COTPAPEL5000; ?></td>
						</tr>
						
						<tr>
								<td>Pmx1</td>
								<td><? echo $COTPMX11000; ?></td>
								<td><? echo $COTPMX12000; ?></td>
								<td><? echo $COTPMX13000; ?></td>
								<td><? echo $COTPMX14000; ?></td>
								<td><? echo $COTPMX15000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Uvp</td>
								<td><? echo $COTUVP1000; ?></td>
								<td><? echo $COTUVP2000; ?></td>
								<td><? echo $COTUVP3000; ?></td>
								<td><? echo $COTUVP4000; ?></td>
								<td><? echo $COTUVP5000; ?></td>
						</tr>
						<tr>
								<td>Anillado doble o</td>
								<td><? echo $COTANILLADODOBLEO1000; ?></td>
								<td><? echo $COTANILLADODOBLEO2000; ?></td>
								<td><? echo $COTANILLADODOBLEO3000; ?></td>
								<td><? echo $COTANILLADODOBLEO4000; ?></td>
								<td><? echo $COTANILLADODOBLEO5000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>End</td>
								<td><? echo $COTEND1000; ?></td>
								<td><? echo $COTEND2000; ?></td>
								<td><? echo $COTEND3000; ?></td>
								<td><? echo $COTEND4000; ?></td>
								<td><? echo $COTEND5000; ?></td>
						</tr>
						<tr>
								<td>Planchas</td>
								<td><? echo $COTPLANCHAS1000; ?></td>
								<td><? echo $COTPLANCHAS2000; ?></td>
								<td><? echo $COTPLANCHAS3000; ?></td>
								<td><? echo $COTPLANCHAS4000; ?></td>
								<td><? echo $COTPLANCHAS5000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Pmx2</td>
								<td><? echo $COTPMX21000; ?></td>
								<td><? echo $COTPMX22000; ?></td>
								<td><? echo $COTPMX23000; ?></td>
								<td><? echo $COTPMX24000; ?></td>
								<td><? echo $COTPMX25000; ?></td>
						</tr>
						<tr>
								<td>Scratch</td>
								<td><? echo $COTSCRATCH1000; ?></td>
								<td><? echo $COTSCRATCH2000; ?></td>
								<td><? echo $COTSCRATCH3000; ?></td>
								<td><? echo $COTSCRATCH4000; ?></td>
								<td><? echo $COTSCRATCH5000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Anillado plastico</td>
								<td><? echo $COTANILLADOPLASTICO1000; ?></td>
								<td><? echo $COTANILLADOPLASTICO2000; ?></td>
								<td><? echo $COTANILLADOPLASTICO3000; ?></td>
								<td><? echo $COTANILLADOPLASTICO4000; ?></td>
								<td><? echo $COTANILLADOPLASTICO5000; ?></td>
						</tr>
						<tr>
								<td>Colaminado</td>
								<td><? echo $COTCOLAMINADO1000; ?></td>
								<td><? echo $COTCOLAMINADO2000; ?></td>
								<td><? echo $COTCOLAMINADO3000; ?></td>
								<td><? echo $COTCOLAMINADO4000; ?></td>
								<td><? echo $COTCOLAMINADO5000; ?></td>
					    </tr>
						<tr bgcolor="F5F4F4">
								<td>Peliculas</td>
								<td><? echo $COTPELICULAS1000; ?></td>
								<td><? echo $COTPELICULAS2000; ?></td>
								<td><? echo $COTPELICULAS3000; ?></td>
								<td><? echo $COTPELICULAS4000; ?></td>
								<td><? echo $COTPELICULAS5000; ?></td>
						</tr>
						<tr>
								<td>Pbx1</td>
								<td><? echo $COTPBX11000; ?></td>
								<td><? echo $COTPBX12000; ?></td>
								<td><? echo $COTPBX13000; ?></td>
								<td><? echo $COTPBX14000; ?></td>
								<td><? echo $COTPBX15000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>N�merado</td>
								<td><? echo $COTNUMERADO1000; ?></td>
								<td><? echo $COTNUMERADO2000; ?></td>
								<td><? echo $COTNUMERADO3000; ?></td>
								<td><? echo $COTNUMERADO4000; ?></td>
								<td><? echo $COTNUMERADO5000; ?></td>
						</tr>
						<tr>
								<td>Repuje</td>
								<td><? echo $COTREPUJE1000; ?></td>
								<td><? echo $COTREPUJE2000; ?></td>
								<td><? echo $COTREPUJE3000; ?></td>
								<td><? echo $COTREPUJE4000; ?></td>
								<td><? echo $COTREPUJE5000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Bolsa plastica</td>
								<td><? echo $COTBOLSAPLASTICA1000; ?></td>
								<td><? echo $COTBOLSAPLASTICA2000; ?></td>
								<td><? echo $COTBOLSAPLASTICA3000; ?></td>
								<td><? echo $COTBOLSAPLASTICA4000; ?></td>
								<td><? echo $COTBOLSAPLASTICA5000; ?></td>
						</tr>		
						
						
						
							<tr>
								<td>Ctp</td>
								<td><? echo $COTCTP1000; ?></td>
								<td><? echo $COTCTP2000; ?></td>
								<td><? echo $COTCTP3000; ?></td>
								<td><? echo $COTCTP4000; ?></td>
								<td><? echo $COTCTP5000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Pbx2</td>
								<td><? echo $COTPBX21000; ?></td>
								<td><? echo $COTPBX22000; ?></td>
								<td><? echo $COTPBX23000; ?></td>
								<td><? echo $COTPBX24000; ?></td>
								<td><? echo $COTPBX25000; ?></td>
						</tr>
						<tr>
								<td>Troquel</td>
								<td><? echo $COTTROQUEL1000; ?></td>
								<td><? echo $COTTROQUEL2000; ?></td>
								<td><? echo $COTTROQUEL3000; ?></td>
								<td><? echo $COTTROQUEL4000; ?></td>
								<td><? echo $COTTROQUEL5000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Repujado</td>
								<td><? echo $COTREPUJADO1000; ?></td>
								<td><? echo $COTREPUJADO2000; ?></td>
								<td><? echo $COTREPUJADO3000; ?></td>
								<td><? echo $COTREPUJADO4000; ?></td>
								<td><? echo $COTREPUJADO5000; ?></td>
						</tr>
						<tr>
								<td>Otros</td>
								<td><? echo $COTOTROS1000; ?></td>
								<td><? echo $COTOTROS2000; ?></td>
								<td><? echo $COTOTROS3000; ?></td>
								<td><? echo $COTOTROS4000; ?></td>
								<td><? echo $COTOTROS5000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Prueba digital</td>
								<td><? echo $COTPRUEBADIGITAL1000; ?></td>
								<td><? echo $COTPRUEBADIGITAL2000; ?></td>
								<td><? echo $COTPRUEBADIGITAL3000; ?></td>
								<td><? echo $COTPRUEBADIGITAL4000; ?></td>
								<td><? echo $COTPRUEBADIGITAL5000; ?></td>
						</tr>
						<tr>
								<td>Uvtx1</td>
								<td><? echo $COTUVTX11000; ?></td>
								<td><? echo $COTUVTX12000; ?></td>
								<td><? echo $COTUVTX13000; ?></td>
								<td><? echo $COTUVTX14000; ?></td>
								<td><? echo $COTUVTX15000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Troquelado</td>
								<td><? echo $COTTROQUELADO1000; ?></td>
								<td><? echo $COTTROQUELADO2000; ?></td>
								<td><? echo $COTTROQUELADO3000; ?></td>
								<td><? echo $COTTROQUELADO4000; ?></td>
								<td><? echo $COTTROQUELADO5000; ?></td>
						</tr>
						<tr>
								<td>Plegado</td>
								<td><? echo $COTPLEGADO1000; ?></td>
								<td><? echo $COTPLEGADO2000; ?></td>
								<td><? echo $COTPLEGADO3000; ?></td>
								<td><? echo $COTPLEGADO4000; ?></td>
								<td><? echo $COTPLEGADO5000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Dise�o</td>
								<td><? echo $COTDISENO1000; ?></td>
								<td><? echo $COTDISENO2000; ?></td>
								<td><? echo $COTDISENO3000; ?></td>
								<td><? echo $COTDISENO4000; ?></td>
								<td><? echo $COTDISENO5000; ?></td>
						</tr>
						<tr>
								<td>Tiraje</td>
								<td><? echo $COTTIRAJE1000; ?></td>
								<td><? echo $COTTIRAJE2000; ?></td>
								<td><? echo $COTTIRAJE3000; ?></td>
								<td><? echo $COTTIRAJE4000; ?></td>
								<td><? echo $COTTIRAJE5000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Uvtx2</td>
								<td><? echo $COTUVTX21000; ?></td>
								<td><? echo $COTUVTX22000; ?></td>
								<td><? echo $COTUVTX23000; ?></td>
								<td><? echo $COTUVTX24000; ?></td>
								<td><? echo $COTUVTX25000; ?></td>
						</tr>
						<tr>
								<td>Hotmelt</td>
								<td><? echo $COTHOTMELT1000; ?></td>
								<td><? echo $COTHOTMELT2000; ?></td>
								<td><? echo $COTHOTMELT3000; ?></td>
								<td><? echo $COTHOTMELT4000; ?></td>
								<td><? echo $COTHOTMELT5000; ?></td>
						</tr>
						<tr bgcolor="F5F4F4">
								<td>Grafa</td>
								<td><? echo $COTGRAFA1000; ?></td>
								<td><? echo $COTGRAFA2000; ?></td>
								<td><? echo $COTGRAFA3000; ?></td>
								<td><? echo $COTGRAFA4000; ?></td>
								<td><? echo $COTGRAFA5000; ?></td>
						</tr>
						<tr>
								<td>Cajas</td>
								<td><? echo $COTCAJAS1000; ?></td>
								<td><? echo $COTCAJAS2000; ?></td>
								<td><? echo $COTCAJAS3000; ?></td>
								<td><? echo $COTCAJAS4000; ?></td>
								<td><? echo $COTCAJAS5000; ?></td>
						</tr>
						<tr>
								<td>Subtotal</td>
								<td><u><b><? echo $sumaMil; ?></u></b></td>
								<td><u><b><? echo $sumaDosmil; ?></u></b></td>
								<td><u><b><? echo $sumaTresmil; ?></u></b></td>
								<td><u><b><? echo $sumaCuatromil; ?></u></b></td>
								<td><u><b><? echo $sumaCincomil; ?></u></b></td>
						</tr>
						<tr>
								<td>Utilidad</td>
								<td><u><b><? echo $utilidadMil; ?></u></b></td>
								<td><u><b><? echo $utilidad2000; ?></u></b></td>
								<td><u><b><? echo $utilidad3000; ?></u></b></td>
								<td><u><b><? echo $utilidad4000; ?></u></b></td>
								<td><u><b><? echo $utilidad5000; ?></u></b></td>
						</tr>
						<tr>
								<td>Comisi�n</td>
								<td><u><b><? echo $comisionMil; ?></u></b></td>
								<td><u><b><? echo $comision2000; ?></u></b></td>
								<td><u><b><? echo $comision3000; ?></u></b></td>
								<td><u><b><? echo $comision4000; ?></u></b></td>
								<td><u><b><? echo $comision5000; ?></u></b></td>
						</tr>
						<tr>
								<td>Total</td>
								<td><u><b><? echo $totalFinalMil; ?></u></b></td>
								<td><u><b><? echo $totalFinal2000; ?></u></b></td>
								<td><u><b><? echo $totalFinal3000; ?></u></b></td>
								<td><u><b><? echo $totalFinal4000; ?></u></b></td>
								<td><u><b><? echo $totalFinal5000; ?></u></b></td>
						</tr>
						<tr>
								<td>Valor Unitario</td>
								<td><u><b><? echo $valorUnitarioMil; ?></u></b></td>
								<td><u><b><? echo $valorUnitarioDosmil; ?></u></b></td>
								<td><u><b><? echo $valorUnitarioTresmil; ?></u></b></td>
								<td><u><b><? echo $valorUnitarioCuatromil; ?></u></b></td>
								<td><u><b><? echo $valorUnitarioCincomil; ?></u></b></td>
						</tr>
												
					</table>
				 
				 </td>
				 </tr>
				<tr class="arial10ptsgrisBOLD">
					<td>Correo electronico del cliente</td>
					<td><input type="text" name="email" size="30"> </td>
				</tr>
				 	
				
				 <tr> 
				  
                <td colspan="2">
				<br>
				<div align="center"><a href="#" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image18','','/img/enviar2.gif',1)"><br>
                    <input type="image" src="/img/enviar.gif" name="Image18" width="94" height="21" border="0"></a></div>
					<input type="hidden" name='id' value=<? echo $id; ?>>
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