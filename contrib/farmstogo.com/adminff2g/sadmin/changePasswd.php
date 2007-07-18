<html>
<head>
<title>Flower Farms To Go .com</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="http://www.flowerfarmstogo.com/s/library/estilos.css" type="text/css">

<script language="JavaScript" src="/javascript/validaciones/addUser.js"></script>
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

require_once("../../forms/config/bd.conf"); 			// Get the connection variables
require_once("../../forms/lib/database.php"); 							// Get the DataBase extended functions
require_once("../../consultas/funcionesSQL.php"); 	
require_once("../../utilidades/sesiones.php"); 	

// +-----------------------------------------------------------------------+
// |  Incializa las plantillas y establece la conexión con la BD		   |
// +-----------------------------------------------------------------------+
$db = DB::connect($dsn, DB_CONNECT_MODE); // Creates a database connection object in $db 
										  // or, a database error object if it went wrong.
										  // Lista de categorias principales
$db->setFetchMode(DB_FETCHMODE_ASSOC);	

?>
<body background="../images/fondo.gif">
<table>
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>
<form action="/forms/process.php" method="post">
<table>
	<tr>
		<td>&nbsp;</td><td class="titulogrisprincipal">Change password</td>
	</tr>
	<tr>
		<td class="texto">New Password</td><td><input type="password" name="password" class="combos"></td>
	</tr>
	<tr>
		<td>&nbsp;</td><td>
			<input type="submit" value="Change"> 
			<input type="hidden" name='change' value="1">
			<input type="hidden" name='id' value=<? echo $id; ?>>
			
		</td>
	</tr>
</table>
</form>
</body>