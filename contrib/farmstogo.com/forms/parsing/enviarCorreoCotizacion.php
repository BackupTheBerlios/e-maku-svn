
<?
require_once("../../formas/config/general.conf");		// Get the global variables
require_once("../../formas/config/bd.conf"); 			// Get the connection variables
require_once("../../formas/lib/database.php"); 							// Get the DataBase extended functions
require_once("../../consultas/funcionesSQL.php"); 	


$db = DB::connect($dsn, DB_CONNECT_MODE); // Creates a database connection object in $db 
										  // or, a database error object if it went wrong.
										  // Lista de categorias principales
$db->setFetchMode(DB_FETCHMODE_ASSOC);	




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

			
	if ($plegado!="" || $centro!="" || $cuerpos2!="" || $cuerpos3!="" || $cuerpos4!="" || $otroEncuadernacion!="" || $encolado!="" || $caballete!="" || $pegado!="" || $hotmelt!="" || $cintadoblefaz!="" || $ensanduchado!="" ){
		$encuadernacion="<B>ENCUADERNACION:</B> ";
	}
	else{
		$encuadernacion="";
	}
	
	
	if ($empaquepapelkraft!="" || $empaquecajasdecarton!="" || $empaquebolsaplastica!= "" || empaqueexportacion!=""){
		$empaque="<B>EMPAQUE: </B>";
	}else{
		$empaque="";
	}
	
	if ($anilladocolor!="" || $anilladodobleo!="" || $anilladoplastico!="" || $anilladoOtro!=""){
		$anillado="<B>ANILLADO: </B>";
	}else{
		$anillado="";
	}
	
	
	if ($peliculasnegativas!="" || $peliculaspositivas!="" || $peliculasarchivo!="" || $peliculasOriginales!=""){
		$peliculas="<B>PELICULAS: </B>";
	}else{
		$peliculas="";
	}
	
	
	if ($totalFinalMil!=""){
		$totalFinalMil="$". $totalFinalMil;
	}
	if ($totalFinal2000!=""){
		$totalFinal2000="$". $totalFinal2000;
	}
	if ($totalFinal3000!=""){
		$totalFinal3000="$". $totalFinal3000;
	}
	if ($totalFinal4000!=""){
		$totalFinal4000="$". $totalFinal4000;
	}
	if ($totalFinal5000!=""){
		$totalFinal5000="$". $totalFinal5000;
	}
	
	if ($valorUnitarioMil!=""){
		$valorUnitarioMil="$". $valorUnitarioMil;
	}
	
	if ($valorUnitarioDosmil!=""){
		$valorUnitarioDosmil="$". $valorUnitarioDosmil;
	}
	
	if ($valorUnitarioTresmil!=""){
		$valorUnitarioTresmil="$". $valorUnitarioTresmil;
	}
	
	if ($valorUnitarioCuatromil!=""){
		$valorUnitarioCuatromil="$". $valorUnitarioCuatromil;
	}
	
	if ($valorUnitarioCincomil!=""){
		$valorUnitarioCincomil="$". $valorUnitarioCincomil;
	}
	
	if ($caratula!=""){
		$caratula2="CARATULA: ";
	}else{
		$caratula2="";
	}
	
	if ($interiores!=""){
		$interiores2="INTERIORES: ";
	}else{
		$interiores2="";
	}
	
			$name=urlencode($name);
			$prev_path=urlencode($prev_path);
			
			$SENDER = "horizonte@horizonteimpresores.com";
            $unSubject = "Cotizaci�n.";
            $unHeaderAdicional="From: $SENDER\nContent-type: text/html";
            
            
            $archivo ="preCorreo.php";
            $fp=fopen($archivo,"r");
            $unMensaje = fread($fp,filesize($archivo));
            $unMensaje = str_replace("\\\"","\"",$unMensaje);
			
			$unMensaje = str_replace("{CANTIDAD1}",$mil,$unMensaje);
			$unMensaje = str_replace("{CANTIDAD2}",$dosmil,$unMensaje);
			$unMensaje = str_replace("{CANTIDAD3}",$tresmil,$unMensaje);
			$unMensaje = str_replace("{CANTIDAD4}",$cuatromil,$unMensaje);
			$unMensaje = str_replace("{CANTIDAD5}",$cincomil,$unMensaje);
			$unMensaje = str_replace("{CLIENTE}",$cliente,$unMensaje);
			$unMensaje = str_replace("{ATT}",$att,$unMensaje);
			$unMensaje = str_replace("{FECHA}",$fecha_alta,$unMensaje);
			$unMensaje = str_replace("{FAX}",$fax,$unMensaje);
			$unMensaje = str_replace("{REFERENCIA}",$referencia,$unMensaje);
			$unMensaje = str_replace("{AFICHE}",$afiche,$unMensaje);
			$unMensaje = str_replace("{CALENDARIO}",$calendario,$unMensaje);
			$unMensaje = str_replace("{CAJAS}",$cajas,$unMensaje);
			$unMensaje = str_replace("{FOLLETO}",$folleto,$unMensaje);
			$unMensaje = str_replace("{VOLANTES}",$volantes,$unMensaje);
			$unMensaje = str_replace("{HABLADOR}",$hablador,$unMensaje);
			$unMensaje = str_replace("{LIBRETAS}",$libretas,$unMensaje);
			$unMensaje = str_replace("{LIBRO}",$libro,$unMensaje);
			$unMensaje = str_replace("{BOLSAS}",$bolsas,$unMensaje);
			$unMensaje = str_replace("{PAPELERIA}",$papeleria,$unMensaje);
			$unMensaje = str_replace("{REVISTA}",$revista,$unMensaje);
			$unMensaje = str_replace("{PLEGABLE}",$plegable,$unMensaje);
			$unMensaje = str_replace("{STICKER}",$sticker,$unMensaje);
			$unMensaje = str_replace("{CARPETA}",$carpeta,$unMensaje);
			$unMensaje = str_replace("{SOBRES}",$sobres,$unMensaje);
			$unMensaje = str_replace("{TARJETAS}",$tarjetas,$unMensaje);
			$unMensaje = str_replace("{TARJETON}",$tarjeton,$unMensaje);
			$unMensaje = str_replace("{OTRO}",$otro,$unMensaje);
			$unMensaje = str_replace("{PAPEL}",$papel,$unMensaje);
			$unMensaje = str_replace("{CARATULA}",$caratula,$unMensaje);
			$unMensaje = str_replace("{INTERIORES}",$interiores,$unMensaje);
			$unMensaje = str_replace("{CARATULA2}",$caratula2,$unMensaje);
			$unMensaje = str_replace("{INTERIORES2}",$interiores2,$unMensaje);
			$unMensaje = str_replace("{TAMANOABIERTOPAPEL}",$tamanoAbiertoPapel,$unMensaje);
			$unMensaje = str_replace("{TAMANOCERRADOPAPEL}",$tamanoCerradoPapel,$unMensaje);
			$unMensaje = str_replace("{TINTASPAPEL}",$tintasPapel,$unMensaje);
			$unMensaje = str_replace("{PANTONEPAPEL}",$pantonePapel,$unMensaje);
			$unMensaje = str_replace("{TAMANOABIERTOINTERIORES}",$tamanoAbiertoInteriores,$unMensaje);
			$unMensaje = str_replace("{TAMANOCERRADOINTERIORES}",$tamanoCerradoInteriores,$unMensaje);
			$unMensaje = str_replace("{TINTASINTERIORES}",$tintasInteriores,$unMensaje);
			$unMensaje = str_replace("{PANTONEINTERIORES}",$pantoneInteriores,$unMensaje);
			$unMensaje = str_replace("{PAGINASINTERIORES}",$paginasInteriores,$unMensaje);
			$unMensaje = str_replace("{TAMANOABIERTOCARATULA}",$tamanoAbiertoCaratula,$unMensaje);
			$unMensaje = str_replace("{TAMANOCERRADOCARATULA}",$tamanoCerradoCaratula,$unMensaje);
			$unMensaje = str_replace("{TINTASCARATULA}",$tintasCaratula,$unMensaje);
			$unMensaje = str_replace("{PANTONECARATULA}",$pantoneCaratula,$unMensaje);
			$unMensaje = str_replace("{PELICULASNEGATIVAS}",$peliculasnegativas,$unMensaje);
			$unMensaje = str_replace("{PELICULASPOSITIVAS}",$peliculaspositivas,$unMensaje);
			$unMensaje = str_replace("{PELICULASARCHIVO}",$peliculasarchivo,$unMensaje);
			$unMensaje = str_replace("{PELICULASORIGINALES}",$peliculasOriginales,$unMensaje);
			$unMensaje = str_replace("{PELICULAS}",$peliculas,$unMensaje);
			$unMensaje = str_replace("{ACABADOSMATE1CARAS}",$acabadosmate1caras,$unMensaje);
			$unMensaje = str_replace("{ACABADOSMATE2CARAS}",$acabadosmate2caras,$unMensaje);
			$unMensaje = str_replace("{PLASTMATEINTERIORES1CARA}",$plastmateInteriores1cara,$unMensaje);
			$unMensaje = str_replace("{PLASTMATEINTERIORES2CARA}",$plastmateInteriores2cara,$unMensaje);
			$unMensaje = str_replace("{BRILLOLITOGRAFICO}",$brillolitografico,$unMensaje);
			$unMensaje = str_replace("{REPUJADO}",$repujado,$unMensaje);
			$unMensaje = str_replace("{PLASTBRILLANTE1CARA}",$plastbrillante1cara,$unMensaje);
			$unMensaje = str_replace("{PLASTBRILLANTE2CARA}",$plastbrillante2cara,$unMensaje);
			$unMensaje = str_replace("{PLASTBRILLANTEINTERIORES1CARA}",$plastbrillanteInteriores1cara,$unMensaje);
			$unMensaje = str_replace("{PLASTBRILLANTEINTERIORES2CARA}",$plastbrillanteInteriores2cara,$unMensaje);
			$unMensaje = str_replace("{GRAFADO}",$grafado,$unMensaje);
			$unMensaje = str_replace("{ESTAMPADO}",$estampado,$unMensaje);
			$unMensaje = str_replace("{ACABADOSUVTOTAL1CARAS}",$acabadosuvtotal1caras,$unMensaje);
			$unMensaje = str_replace("{ACABADOSUVTOTAL2CARAS}",$acabadosuvtotal2caras,$unMensaje);
			$unMensaje = str_replace("{ACABADOSUVTOTALINTERIORES1CARAS}",$acabadosuvtotalInteriores1caras,$unMensaje);
			$unMensaje = str_replace("{ACABADOSUVTOTALINTERIORES2CARAS}",$acabadosuvtotalInteriores2caras,$unMensaje);
			$unMensaje = str_replace("{PERFORADO}",$perforado,$unMensaje);
			$unMensaje = str_replace("{TROQUELADO}",$troquelado,$unMensaje);
			$unMensaje = str_replace("{ACABADOSUVPARCIAL1CARAS}",$acabadosuvparcial1caras,$unMensaje);
			$unMensaje = str_replace("{ACABADOSUVPARCIAL2CARAS}",$acabadosuvparcial2caras,$unMensaje);
			$unMensaje = str_replace("{ACABADOSUVPARCIALINTERIORES1CARAS}",$acabadosuvparcialInteriores1caras,$unMensaje);
			$unMensaje = str_replace("{ACABADOSUVPARCIALINTERIORES2CARAS}",$acabadosuvparcialInteriores2caras,$unMensaje);
			$unMensaje = str_replace("{NUMERADO}",$numerado,$unMensaje);
			$unMensaje = str_replace("{NUMERADODEL}",$numeradodel,$unMensaje);
			$unMensaje = str_replace("{NUMERADOHASTA}",$numeradohasta,$unMensaje);
			$unMensaje = str_replace("{ENCUADERNACION}",$encuadernacion,$unMensaje);
			$unMensaje = str_replace("{PLEGADO}",$plegado,$unMensaje);
			$unMensaje = str_replace("{CENTRO}",$centro,$unMensaje);
			$unMensaje = str_replace("{CUERPOS2}",$cuerpos2,$unMensaje);
			$unMensaje = str_replace("{CUERPOS3}",$cuerpos3,$unMensaje);
			$unMensaje = str_replace("{CUERPOS4}",$cuerpos4,$unMensaje);
			$unMensaje = str_replace("{OTROENCUADERNACION}",$otroEncuadernacion,$unMensaje);
			$unMensaje = str_replace("{ENCOLADO}",$encolado,$unMensaje);
			$unMensaje = str_replace("{CABALLETE}",$caballete,$unMensaje);
			$unMensaje = str_replace("{PEGADO}",$pegado,$unMensaje);
			$unMensaje = str_replace("{HOTMELT}",$hotmelt,$unMensaje);
			$unMensaje = str_replace("{CINTADOBLEFAZ}",$cintadoblefaz,$unMensaje);
			$unMensaje = str_replace("{ENSANDUCHADO}",$ensanduchado,$unMensaje);
			$unMensaje = str_replace("{ANILLADO}",$anillado,$unMensaje);
			$unMensaje = str_replace("{ANILLADOCOLOR}",$anilladocolor,$unMensaje);
			$unMensaje = str_replace("{ANILLADODOBLEO}",$anilladodobleo,$unMensaje);
			$unMensaje = str_replace("{ANILLADOPLASTICO}",$anilladoplastico,$unMensaje);
			$unMensaje = str_replace("{ANILLADOOTRO}",$anilladoOtro,$unMensaje);
			$unMensaje = str_replace("{EMPAQUE}",$empaque,$unMensaje);
			$unMensaje = str_replace("{EMPAQUEPAPELKRAFT}",$empaquepapelkraft,$unMensaje);
			$unMensaje = str_replace("{EMPAQUECAJASDECARTON}",$empaquecajasdecarton,$unMensaje);
			$unMensaje = str_replace("{EMPAQUEBOLSAPLASTICA}",$empaquebolsaplastica,$unMensaje);
			$unMensaje = str_replace("{EMPAQUEEXPORTACION}",$empaqueexportacion,$unMensaje);
			$unMensaje = str_replace("{FECHAENTREGA}",$fechaEntrega,$unMensaje);
			$unMensaje = str_replace("{OBSERVACIONES}",$observaciones,$unMensaje);
			$unMensaje = str_replace("{TOTAL1000}",$totalFinalMil,$unMensaje);
			$unMensaje = str_replace("{TOTAL2000}",$totalFinal2000,$unMensaje);
			$unMensaje = str_replace("{TOTAL3000}",$totalFinal3000,$unMensaje);
			$unMensaje = str_replace("{TOTAL4000}",$totalFinal4000,$unMensaje);
			$unMensaje = str_replace("{TOTAL5000}",$totalFinal5000,$unMensaje);
			$unMensaje = str_replace("{VALORUNITARIO1000}",$valorUnitarioMil,$unMensaje);
			$unMensaje = str_replace("{VALORUNITARIO2000}",$valorUnitarioDosmil,$unMensaje);
			$unMensaje = str_replace("{VALORUNITARIO3000}",$valorUnitarioTresmil,$unMensaje);
			$unMensaje = str_replace("{VALORUNITARIO4000}",$valorUnitarioCuatromil,$unMensaje);
			$unMensaje = str_replace("{VALORUNITARIO5000}",$valorUnitarioCincomil,$unMensaje);
			$unCorreo_electronico = $email;
			 mail($unCorreo_electronico, $unSubject, $unMensaje, $unHeaderAdicional);	
			 
?>
<script>
	alert ("Correo enviado con �xito!");
</script>

<script language="Javascript">
  window.location="/admin/login.php";
</script>
