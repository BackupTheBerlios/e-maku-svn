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

require_once("../../formas/config/general.conf");		// Get the global variables
require_once("../../formas/config/bd.conf"); 			// Get the connection variables
require_once("../../formas/lib/database.php"); 							// Get the DataBase extended functions
require_once("../../consultas/funcionesSQL.php"); 
require_once("../../utilidades/sesiones.php"); 		


// +-----------------------------------------------------------------------+
// |  Incializa las plantillas y establece la conexión con la BD		   |
// +-----------------------------------------------------------------------+
$db = DB::connect($dsn, DB_CONNECT_MODE); // Creates a database connection object in $db 
										  // or, a database error object if it went wrong.
										  // Lista de categorias principales
$db->setFetchMode(DB_FETCHMODE_ASSOC);	
validarSesion();

$usuarios=listaUsuarios ($login, $db);

$contador=count ($usuarios);
echo $contador;
if ($contador!=0){
?>
	<script>
		alert ("El usuario ya existe, por favor ingrese otro login!.");
	</script>
	<script>
		history.go(-1);
	</script>
<?
}
insertarUsuario ($nombre, $login, $contrasena, $tipoUsuario, $db);
	
?>
<script>
		alert ("El usuario fue agregado exitosamente!.");
	</script>
<script language="Javascript">
 		window.location="/admin/index.php";
</script>

