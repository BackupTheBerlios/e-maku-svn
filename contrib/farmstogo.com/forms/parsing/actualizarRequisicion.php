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

updateRequisicion ($id, $solicitante,
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
					  

?>
<BODY bgcolor="#FFD33B" topmargin="0" leftmargin="0" onLoad="javascript:redireccion.submit();"> 
<form name="redireccion" action="/formas/parsing/desplegarRequisicion.php" method="post">
		<input type="hidden" name="id" value="<? echo $id; ?>">
</form>




