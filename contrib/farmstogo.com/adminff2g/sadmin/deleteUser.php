<html>
<head>
<title>Flower Farms To Go .com</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script language="JavaScript" src="/javascript/validaciones/addUser.js"></script>
<?
/************************************************************************/
/*       	FLOWERDEALERS															*/
/*______________________________________________________________________*/
/*			Investigaci�n y Desarrollo									*/
/*                                                                      */
/*                    		FLOWERDEALERS  	                        	*/
/*        	Sistema de alimentaci�n de FLOWERDEALERS					*/
/************************************************************************/

/************************************************************************/
/* Definici�n de variables globales y librer�as requeridas.				*/
/*______________________________________________________________________*/

include('../head_admin.php');
require_once("../../forms/config/bd.conf"); 			// Get the connection variables
require_once("../../forms/lib/database.php"); 							// Get the DataBase extended functions
require_once("../../consultas/funcionesSQL.php"); 	
require_once("../../utilidades/sesiones.php"); 	

// +-----------------------------------------------------------------------+
// |  Incializa las plantillas y establece la conexi�n con la BD		   |
// +-----------------------------------------------------------------------+
$db = DB::connect($dsn, DB_CONNECT_MODE); // Creates a database connection object in $db 
										  // or, a database error object if it went wrong.
										  // Lista de categorias principales
$db->setFetchMode(DB_FETCHMODE_ASSOC);	

deleteUsers ($id, $db);


?>
<script>
	alert ("User has been delete");
</script>
<script>
	window.location.href='users.php';
</script>
