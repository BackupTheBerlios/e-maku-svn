<html>
<head>
<title>Flower Farms To Go .com</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<?
/************************************************************************/
/*       	FLOWERDEALERS															*/
/*______________________________________________________________________*/
/*			Investigación y Desarrollo									*/
/*                                                                      */
/*                    		FLOWERDEALERS  	                        	*/
/*        	Sistema de alimentación de FLOWERDEALERS					*/
/************************************************************************/

/************************************************************************/
/* Definición de variables globales y librerías requeridas.				*/
/*______________________________________________________________________*/

require_once("config/bd.conf"); 			// Get the connection variables
require_once("lib/database.php"); 							// Get the DataBase extended functions
require_once("../consultas/funcionesSQL.php"); 	
require_once("../utilidades/sesiones.php"); 	

// +-----------------------------------------------------------------------+
// |  Incializa las plantillas y establece la conexión con la BD		   |
// +-----------------------------------------------------------------------+
$db = DB::connect($dsn, DB_CONNECT_MODE); // Creates a database connection object in $db 
										  // or, a database error object if it went wrong.
										  // Lista de categorias principales
$db->setFetchMode(DB_FETCHMODE_ASSOC);	

if ($updateUser==1){
	updateUsuario ($email, $name, $company, $type, $id, $db);
	?>
		<script>
			alert ("your information has been saved");
		</script>
		<script>
			window.location.href='../adminff2g/sadmin/users.php';
		</script>
	<?
}

if ($insertUser==2){
	$password=md5($password);
	insertUsuario ($email, $password, $name, $company, $type, $db);
	?>
		<script>
			alert ("your information has been saved");
		</script>
		<script>
			window.location.href='../adminff2g/sadmin/users.php';
		</script>
	<?
}

if ($change==1){
	$password=md5($password);
	updatepassd ($password, $id, $db);
	?>
		<script>
			alert ("your information has been saved");
		</script>
		<script>
			window.close();
		</script>
	<?
}
?>